package com.example.demo.service;

import com.example.demo.dto.comment.CommentCreateDto;
import com.example.demo.dto.comment.CommentUpdateDto;
import com.example.demo.dto.comment.response.CommentResponseDto;
import com.example.demo.dto.vote.VoteAction;
import com.example.demo.dto.vote.VoteResponseDto;
import com.example.demo.exception.notfound.CommentNotFoundException;
import com.example.demo.exception.AccessDeniedException;
import com.example.demo.logger.Logger;
import com.example.demo.mapper.CommentMapper;
import com.example.demo.model.*;
import com.example.demo.model.enums.VoteType;
import com.example.demo.repository.CommentRepository;
import com.example.demo.repository.CommentVoteRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class CommentService {

    private final UserService userService;
    private final PostService postService;

    private final ProfanityFilterService profanityFilterService;

    private final CommentRepository commentRepository;
    private final CommentVoteRepository commentVoteRepository;
    private final CommentMapper commentMapper;

    @Transactional
    public CommentResponseDto addComment(CommentCreateDto commentDto, UUID postId) {
        User authorUser = userService.getAuthenticatedUser();
        Post targetPost = postService.findById(postId);

        String clearContent = profanityFilterService.censor(commentDto.getContent());

        Comment parentComment = null;
        if (commentDto.getParentId() != null) {
            parentComment = commentRepository.findById(commentDto.getParentId())
                    .orElseThrow(() -> new CommentNotFoundException(
                            "Parent comment with id: " + commentDto.getParentId() + " was not found."));
        }


        Comment commentToAdd = Comment.builder()
                .content(clearContent)
                .user(authorUser)
                .post(targetPost)
                .parentComment(parentComment)
                .build();

        Comment savedComment = commentRepository.save(commentToAdd);

        voteComment(savedComment.getId(), VoteAction.UP);
        Logger.info("Comment created by %s", authorUser.getUsername());

        return getEnrichedCommentDto(savedComment);
    }

    public Comment findById (UUID id) {
        return commentRepository.findById(id)
                .map(this::maskIfDeleted)
                .orElseThrow(() ->
                new CommentNotFoundException("Comment with id: " + id + " was not found."));
    }

    @Transactional
    public void deleteCommentById(UUID id) {
        Comment comment = findById(id);

        if (comment.isDeleted()) {
            throw new IllegalStateException("Comment is already deleted");
        }

        if (!comment.getUser().equals(userService.getAuthenticatedUser())) {
            throw new AccessDeniedException("You are not the author of this comment");
        }
        comment.setDeleted(true);
        Logger.info("Comment deleted by %s", userService.getAuthenticatedUser().getUsername());
        commentRepository.save(comment);
    }

    private Comment maskIfDeleted(Comment comment) {
        if (!comment.isDeleted()) {
            return comment;
        }

        Comment masked = commentMapper.clone(comment);
        masked.setDeleted(true);
        masked.setContent("[DELETED]");

        return masked;
    }

    @Transactional
    public CommentResponseDto editComment(UUID commentId, CommentUpdateDto updateDto) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CommentNotFoundException("Comment with id: " + commentId + " was not found."));

        User currentUser = userService.getAuthenticatedUser();
        if (!comment.getUser().equals(currentUser)) {
            throw new AccessDeniedException("You are not the author of this comment");
        }
        if (comment.isDeleted()) {
            throw new IllegalStateException("Cannot edit a deleted comment");
        }
        String clearContent = profanityFilterService.censor(updateDto.getContent());
        comment.setContent(clearContent);
        Comment updatedComment = commentRepository.save(comment);
        Logger.info("Comment edited by %s", currentUser.getUsername());
        return getEnrichedCommentDto(updatedComment);
    }

    @Transactional
    public VoteResponseDto voteComment(UUID commentId, VoteAction action) {
        // todo see if u wanna log the fact that an upvote was done after a new comment

        // todo extract find by id and is deleted so that it is only for the voting. new comments dont need to be fetched again from the DB.
        Comment comment = findById(commentId);

        if (comment.isDeleted()) {
            throw new IllegalStateException("Cannot vote on a deleted comment");
        }

        User user = userService.getAuthenticatedUser();
        VoteAction finalAction = (action != null) ? action : VoteAction.NONE;

        Optional<CommentVote> existingVoteOpt = commentVoteRepository.findByCommentAndUser(comment, user);


        VoteType requestedVoteType = switch(finalAction){
            case UP -> VoteType.UPVOTE;
            case DOWN -> VoteType.DOWNVOTE;
            case NONE -> null;
        };
        VoteType currentVoteType = existingVoteOpt.map(CommentVote::getVoteType).orElse(null);
        VoteType newVoteType = (currentVoteType == requestedVoteType) ? null:requestedVoteType;

        if(currentVoteType==null && newVoteType ==null) {
            return null;
        }

        existingVoteOpt.ifPresent(existingVote->{removeVoteFromComment(comment,existingVote.getVoteType());
        if(newVoteType ==null) {
            Logger.info("User %s unvoted comment %s", user.getUsername(), commentId);
            commentVoteRepository.delete(existingVote);
        }
        });

        if(newVoteType !=null){
            CommentVote voteToSave = existingVoteOpt.orElseGet(()->{
                CommentVote vote = new CommentVote();
                vote.setComment(comment);
                vote.setUser(user);
                return vote;
            });
            voteToSave.setVoteType(newVoteType);
            Logger.info("User %s voted comment %s", user.getUsername(), commentId);
            commentVoteRepository.save(voteToSave);
            addVoteToComment(comment, newVoteType);
        }


        return VoteResponseDto.builder()
                .upvotes(comment.getUpvotes())
                .downvotes(comment.getDownvotes())
                .score(comment.getUpvotes() - comment.getDownvotes())
                .userVote(finalAction == VoteAction.NONE ? null : finalAction.getValue())
                .build();
    }

    private void addVoteToComment(Comment comment, VoteType voteType) {
        if (voteType == VoteType.UPVOTE) {
            comment.setUpvotes(comment.getUpvotes() + 1);
        } else {
            comment.setDownvotes(comment.getDownvotes() + 1);
        }
    }

    private void removeVoteFromComment(Comment comment, VoteType voteType) {
        if (voteType == VoteType.UPVOTE) {
            comment.setUpvotes(comment.getUpvotes() - 1);
        } else {
            comment.setDownvotes(comment.getDownvotes() - 1);
        }
    }

    @Transactional(readOnly = true)
    public List<Comment> getTopLevelCommentsByPostId(UUID postId) {
        postService.findById(postId);
        return commentRepository.findByPostIdAndParentCommentIdIsNull(postId)
                .stream()
                .map(this::maskIfDeleted)
                .toList();
    }
    @Transactional(readOnly = true)
    public CommentResponseDto getEnrichedCommentDto(Comment comment) {
        Comment displayComment = maskIfDeleted(comment);
        displayComment.setUser(userService.maskIfDeleted(displayComment.getUser()));

        CommentResponseDto dto = commentMapper.toDto(displayComment);


        UUID parentId = (displayComment.getParentComment() != null)
                ? displayComment.getParentComment().getId()
                : null;

        // this is filled according to the docs from the frontend not used in CLI
        dto.setScore(displayComment.getUpvotes() - displayComment.getDownvotes());
        dto.setUpvotes(displayComment.getUpvotes());
        dto.setDownvotes(displayComment.getDownvotes());
        dto.setParentId(parentId);
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if(auth!=null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getPrincipal())) {
            try {
                User currentUser = userService.getAuthenticatedUser();
                Optional<CommentVote> voteOpt = commentVoteRepository.findByCommentAndUser(displayComment, currentUser);

                if (voteOpt.isPresent()) {
                    VoteType type = voteOpt.get().getVoteType();
                    dto.setUserVote(type == VoteType.UPVOTE ? "up" : "down");
                } else {
                    dto.setUserVote(null);
                }
            } catch (Exception e) {
                dto.setUserVote(null);
            }
        }else{
            dto.setUserVote(null);
        }

        List<CommentResponseDto> replyDtos = commentRepository.findByParentCommentId(comment.getId())
                .stream()
                .map(this::getEnrichedCommentDto)
                .toList();
        dto.setReplies(replyDtos);

        return dto;
    }

}
