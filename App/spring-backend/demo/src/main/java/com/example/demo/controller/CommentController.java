package com.example.demo.controller;

import com.example.demo.dto.comment.CommentCreateDto;
import com.example.demo.dto.comment.CommentUpdateDto;
import com.example.demo.dto.comment.response.CommentResponseDto;
import com.example.demo.dto.post.response.PostResponseDto;
import com.example.demo.dto.vote.VoteRequestDto;
import com.example.demo.dto.vote.VoteResponseDto;
import com.example.demo.model.Comment;
import com.example.demo.response.ApiResponse;
import com.example.demo.service.CommentService;
import com.example.demo.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<ApiResponse<CommentResponseDto>> updateComment (@PathVariable UUID id, @Valid @RequestBody CommentUpdateDto updateDto) {

        Comment updatedComment = commentService.editComment(id,updateDto);
        CommentResponseDto response = commentService.getEnrichedCommentDto(updatedComment);

        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PutMapping("/{id}/vote")
    public ResponseEntity<ApiResponse<VoteResponseDto>> voteComment(
            @PathVariable UUID id,
            @Valid @RequestBody VoteRequestDto voteDto) {

        VoteResponseDto response = commentService.voteComment(id, voteDto.getVoteType());

        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
