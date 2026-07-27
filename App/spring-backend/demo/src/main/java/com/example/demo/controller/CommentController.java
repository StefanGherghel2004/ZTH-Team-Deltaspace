package com.example.demo.controller;

import com.example.demo.dto.comment.CommentCreateDto;
import com.example.demo.dto.comment.CommentUpdateDto;
import com.example.demo.model.Comment;
import com.example.demo.service.CommentService;
import com.example.demo.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    public Comment addComment (@Valid @RequestBody CommentCreateDto commentDto){
        return commentService.addComment(commentDto);
    }

    @GetMapping("/{id}")
    public Comment getCommentById (@PathVariable UUID id) {
        return commentService.findById(id);
    }

    @GetMapping
    public List<Comment> getComments(@RequestParam(required = false) UUID postId) {
        if (postId != null) {
            return commentService.getCommentsByPostId(postId);
        }
        return commentService.getAllComments();
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCommentById (@PathVariable UUID id) {
        commentService.deleteCommentById(id);
    }

    @PutMapping("/{id}")
    public Comment updateComment (@PathVariable UUID id, @Valid @RequestBody CommentUpdateDto updateDto) {

        return commentService.editComment(id,updateDto);
    }
}
