package com.example.demo.service;

import com.example.demo.dto.comment.CommentCreateDto;
import com.example.demo.dto.comment.CommentUpdateDto;
import com.example.demo.dto.comment.response.CommentResponseDto;
import com.example.demo.dto.vote.VoteResponseDto;
import com.example.demo.exception.notfound.CommentNotFoundException;
import com.example.demo.exception.AccessDeniedException;
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

    private final CommentRepository commentRepository;
    private final CommentVoteRepository commentVoteRepository;
    private final CommentMapper commentMapper;

    @Transactional
    public CommentResponseDto addComment(CommentCreateDto commentDto, UUID postId) {
        User authorUser = userService.getAuthenticatedUser();
        Post targetPost = postService.findById(postId);
        // use @Builder
        Comment commentToAdd = new Comment();
        commentToAdd.setContent(commentDto.getContent());
        commentToAdd.setUser(authorUser);
        commentToAdd.setPost(targetPost);
        commentToAdd.setUpvotes(0);
        commentToAdd.setDownvotes(0);

        if (commentDto.getParentId() != null) {
            Comment parentComment = commentRepository.findById(commentDto.getParentId())
                    .orElseThrow(() -> new CommentNotFoundException("Parent comment with id: " +
                            commentDto.getParentId() + " was not found."));
            commentToAdd.setParentComment(parentComment);
        }

        Comment savedComment = commentRepository.save(commentToAdd);
        voteComment(savedComment.getId(),"up");
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
        commentRepository.save(comment);
    }

    @Transactional(readOnly = true)
    public List<Comment> getCommentsByPostId(UUID postId) {
        Post post = postService.findById(postId);

        return post.getComments().stream()
                .map(this::maskIfDeleted)
                .toList();
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

    public List<Comment> getAllComments (){
        return commentRepository.findAll();
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
        comment.setContent(updateDto.getContent());
        Comment updatedComment = commentRepository.save(comment);
        return getEnrichedCommentDto(updatedComment);
    }

    @Transactional
    public VoteResponseDto voteComment(UUID commentId, String voteTypeStr) {
// todo see if u wanna log the fact that an upvote was done after a new comment

       // todo extract find by id and is deleted so that it is only for the voting. new comments dont need to be fetched again from the DB.
        Comment comment = findById(commentId);

        if (comment.isDeleted()) {
            throw new IllegalStateException("Cannot vote on a deleted comment");
        }

        User user = userService.getAuthenticatedUser();
        String voteType = (voteTypeStr != null) ? voteTypeStr.toLowerCase() : "none";

        Optional<CommentVote> existingVoteOpt = commentVoteRepository.findByCommentAndUser(comment, user);
// todo ifelesifelseifelseilfesfs ?? be smarter
        if ("none".equals(voteType)) {
            if (existingVoteOpt.isPresent()) {
                CommentVote existingVote = existingVoteOpt.get();
                removeVoteFromComment(comment, existingVote.getVoteType());
                commentVoteRepository.delete(existingVote);
            }
        } else {
            VoteType newVoteType = "up".equals(voteType) ? VoteType.UPVOTE : VoteType.DOWNVOTE;

            if (existingVoteOpt.isPresent()) {
                CommentVote existingVote = existingVoteOpt.get();

                if (existingVote.getVoteType() == newVoteType) {

                    removeVoteFromComment(comment, existingVote.getVoteType());
                    commentVoteRepository.delete(existingVote);
                    voteType = "none";
                } else {

                    removeVoteFromComment(comment, existingVote.getVoteType());
                    addVoteToComment(comment, newVoteType);
                    existingVote.setVoteType(newVoteType);
                    commentVoteRepository.save(existingVote);
                }
            } else {

                CommentVote newVote = new CommentVote();
                newVote.setComment(comment);
                newVote.setUser(user);
                newVote.setVoteType(newVoteType);
                commentVoteRepository.save(newVote);

                addVoteToComment(comment, newVoteType);
            }
        }

        commentRepository.save(comment);

        return VoteResponseDto.builder()
                .upvotes(comment.getUpvotes())
                .downvotes(comment.getDownvotes())
                .score(comment.getUpvotes() - comment.getDownvotes())
                .userVote("none".equals(voteType) ? null : voteType)
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
