package com.example.demo.service;

import com.example.demo.dto.comment.CommentCreateDto;
import com.example.demo.dto.comment.CommentUpdateDto;
import com.example.demo.exception.notfound.CommentNotFoundException;
import com.example.demo.exception.notfound.PostNotFoundException;
import com.example.demo.exception.AccessDeniedException;
import com.example.demo.model.Comment;
import com.example.demo.model.Post;
import com.example.demo.model.User;
import com.example.demo.repository.CommentRepository;
import com.example.demo.repository.PostRepository;
import com.example.demo.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CommentService {

    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final UserService userService;

    @Transactional
    public Comment addComment (CommentCreateDto commentDto) {

        User authorUser = userService.getAuthenticatedUser();

        Post targetPost = postRepository.findById(commentDto.getPostId()).orElseThrow(() ->
                new PostNotFoundException("Post with id: " + commentDto.getPostId() +
                        " was not found."));

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

    public Comment findById (Long id) {
        return commentRepository.findById(id).orElseThrow(() ->
                new CommentNotFoundException("Comment with id: " + id + " was not found."));
    }

    @Transactional
    public void deleteCommentById (Long id) {
        Comment comment = commentRepository.findById(id).orElseThrow(
                () -> new CommentNotFoundException("Comment with id: " + " was not found."));

        if (!comment.getUser().equals(userService.getAuthenticatedUser()))
            throw new AccessDeniedException("You are not the author of this comment");

        commentRepository.delete(comment);
    }

    public List<Comment> getCommentsByPostId(Long postId) {
        return commentRepository.findByPostId(postId);
    }

    public List<Comment> getAllComments (){
        return commentRepository.findAll();
    }

    @Transactional
    public Comment editComment (Long commentId, CommentUpdateDto updateDto) {
        Comment updatedComment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CommentNotFoundException("Comment with id: " + commentId +
                        " was not found."));
        updatedComment.setText(updateDto.getText());
        return commentRepository.save(updatedComment);
    }
}
