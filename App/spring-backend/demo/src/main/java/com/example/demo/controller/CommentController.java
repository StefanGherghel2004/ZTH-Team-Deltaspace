package com.example.demo.controller;

import com.example.demo.dto.comment.CommentCreateDto;
import com.example.demo.model.Comment;
import com.example.demo.service.CommentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Comment addComment (@Valid @RequestBody CommentCreateDto commentDto){
        return commentService.addComment(commentDto);
    }

    @GetMapping("/{id}")
    public Comment getCommentById (@PathVariable Long id) {
        return commentService.findById(id);
    }

    @GetMapping
    public List<Comment> listAllCommentsOnPost (@RequestParam Long postId){
        return commentService.getCommentsByPostId(postId);
    }

}
