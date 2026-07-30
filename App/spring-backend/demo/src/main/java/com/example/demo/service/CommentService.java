package com.example.demo.service;

import com.example.demo.dto.comment.CommentCreateDto;
import com.example.demo.dto.comment.CommentUpdateDto;
import com.example.demo.exception.notfound.CommentNotFoundException;
import com.example.demo.exception.AccessDeniedException;
import com.example.demo.model.Comment;
import com.example.demo.model.Post;
import com.example.demo.model.User;
import com.example.demo.repository.CommentRepository;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class CommentService {

    private final CommentRepository commentRepository;
    private final UserService userService;
    private final PostService postService;

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
}
