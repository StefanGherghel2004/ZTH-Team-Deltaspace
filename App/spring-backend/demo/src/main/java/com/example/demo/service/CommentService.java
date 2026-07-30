package com.example.demo.service;

import com.example.demo.dto.comment.CommentCreateDto;
import com.example.demo.dto.comment.CommentUpdateDto;
import com.example.demo.dto.comment.response.CommentResponseDto;
import com.example.demo.dto.post.response.PostResponseDto;
import com.example.demo.dto.vote.VoteResponseDto;
import com.example.demo.exception.notfound.CommentNotFoundException;
import com.example.demo.exception.AccessDeniedException;
import com.example.demo.mapper.CommentMapper;
import com.example.demo.model.*;
import com.example.demo.model.enums.VoteType;
import com.example.demo.repository.CommentRepository;
import com.example.demo.repository.CommentVoteRepository;
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

    private final CommentRepository commentRepository;
    private final UserService userService;
    private final PostService postService;
    private final CommentVoteRepository commentVoteRepository;
    private final CommentMapper commentMapper;

    @Transactional
    public Comment addComment (CommentCreateDto commentDto) {

        User authorUser = userService.getAuthenticatedUser();
        Post targetPost = postService.findById(commentDto.getPostId());

        Comment commentToAdd = new Comment();
        commentToAdd.setText(commentDto.getText());
        commentToAdd.setUser(authorUser);
        commentToAdd.setPost(targetPost);

        if (commentDto.getParentCommentId() != null){
            Comment parentComment = commentRepository.findById(commentDto.getParentCommentId())
                    .orElseThrow(() -> new CommentNotFoundException("Parent comment with id: " +
                            commentDto.getParentCommentId() + " was not found."));
            commentToAdd.setParentComment(parentComment);
        }
        return commentRepository.save(commentToAdd);
    }

    public Comment findById (UUID id) {
        return commentRepository.findById(id).orElseThrow(() ->
                new CommentNotFoundException("Comment with id: " + id + " was not found."));
    }

    @Transactional
    public void deleteCommentById (UUID id) {
        Comment comment = findById(id);
        if (!comment.getUser().equals(userService.getAuthenticatedUser()))
            throw new AccessDeniedException("You are not the author of this comment");

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

        Comment masked = new Comment();
        masked.setId(comment.getId());
        masked.setUser(comment.getUser());
        masked.setPost(comment.getPost());
        masked.setCreatedAt(comment.getCreatedAt());
        masked.setParentComment(comment.getParentComment());
        masked.setDeleted(true);

        masked.setText("[DELETED]");

        return masked;
    }

    public List<Comment> getAllComments (){
        return commentRepository.findAll();
    }

    @Transactional
    public Comment editComment (UUID commentId, CommentUpdateDto updateDto) {
        Comment updatedComment = findById(commentId);
        updatedComment.setText(updateDto.getText());
        return commentRepository.save(updatedComment);
    }

    public VoteResponseDto voteComment(UUID commentId, String voteTypeStr) {
        Comment comment = findById(commentId);
        User user = userService.getAuthenticatedUser();

        Optional<CommentVote> existingVoteOpt = commentVoteRepository.findByCommentAndUser(comment, user);

        if (voteTypeStr.equals("none")) {
            if (existingVoteOpt.isPresent()) {
                CommentVote existingVote = existingVoteOpt.get();
                removeVoteFromComment(comment, existingVote.getVoteType());
                commentVoteRepository.delete(existingVote);
            }
        } else {
            VoteType newVoteType = voteTypeStr.equals("up") ? VoteType.UPVOTE : VoteType.DOWNVOTE;

            if (existingVoteOpt.isPresent()) {
                CommentVote existingVote = existingVoteOpt.get();

                if (existingVote.getVoteType() == newVoteType) {
                    removeVoteFromComment(comment, existingVote.getVoteType());
                    commentVoteRepository.delete(existingVote);
                    voteTypeStr = "none";
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
                .userVote(voteTypeStr.equals("none") ? null : voteTypeStr)
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

    public CommentResponseDto getEnrichedCommentDto(Comment comment) {
        CommentResponseDto dto = commentMapper.toDto(comment);

        // this is filled according to the docs from the frontend not used in CLI
        dto.setScore(comment.getUpvotes() - comment.getDownvotes());
        dto.setUpVotes(comment.getUpvotes());
        dto.setDownVotes(comment.getDownvotes());
        try {
            User currentUser = userService.getAuthenticatedUser();
            Optional<CommentVote> voteOpt = commentVoteRepository.findByCommentAndUser(comment, currentUser);

            if (voteOpt.isPresent()) {
                VoteType type = voteOpt.get().getVoteType();
                dto.setCommentVote(type == VoteType.UPVOTE ? "up" : "down");
            } else {
                dto.setCommentVote(null);
            }

        } catch (Exception e) {
            dto.setCommentVote(null);
        }

        return dto;
    }
}
