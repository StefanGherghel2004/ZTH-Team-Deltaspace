package com.example.demo.controller;

import com.example.demo.dto.comment.CommentCreateDto;
import com.example.demo.dto.comment.CommentUpdateDto;
import com.example.demo.dto.comment.response.CommentResponseDto;
import com.example.demo.dto.post.response.PostResponseDto;
import com.example.demo.dto.vote.VoteRequestDto;
import com.example.demo.dto.vote.VoteResponseDto;
import com.example.demo.model.Comment;
import com.example.demo.repository.CommentRepository;
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
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;
    private final CommentRepository commentRepository;

    @PostMapping("/posts/{postId}/comments")
    public ResponseEntity<ApiResponse<CommentResponseDto>> addComment(
            @Valid @RequestBody CommentCreateDto commentDto,
            @PathVariable UUID postId) {

        CommentResponseDto createdComment = commentService.addComment(commentDto, postId);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(createdComment));
    }

    @GetMapping("/comments/{id}")
    public ResponseEntity<ApiResponse<CommentResponseDto>> getCommentById (@PathVariable UUID id) {
        Comment comment = commentRepository.getReferenceById(id);
        CommentResponseDto response = commentService.getEnrichedCommentDto(comment);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/posts/{postId}/comments")
    public ResponseEntity<ApiResponse<List<CommentResponseDto>>>
    getComments(@PathVariable UUID postId) {
        List<Comment> comments;
        comments = commentService.getTopLevelCommentsByPostId(postId);

        List<CommentResponseDto> response = comments.stream()
                .map(commentService::getEnrichedCommentDto)
                .toList();

        int totalComments = commentRepository.countByPostId(postId);

        return ResponseEntity.ok(ApiResponse.success(response,totalComments));
    }

    @DeleteMapping("/comments/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteComment(@PathVariable UUID id) {
        commentService.deleteCommentById(id);

        return ResponseEntity.ok(ApiResponse.successMessage("Comentariul a fost șters cu succes"));
    }

    @PutMapping("/comments/{id}")
    public ResponseEntity<ApiResponse<CommentResponseDto>> updateComment(
            @PathVariable UUID id,
            @Valid @RequestBody CommentUpdateDto updateDto) {

        CommentResponseDto updatedComment = commentService.editComment(id, updateDto);

        return ResponseEntity.ok(ApiResponse.success(updatedComment));
    }

    @PutMapping("/comments/{id}/vote")
    public ResponseEntity<ApiResponse<VoteResponseDto>> voteComment(
            @PathVariable UUID id,
            @Valid @RequestBody VoteRequestDto voteDto) {

        VoteResponseDto response = commentService.voteComment(id, voteDto.getVoteType());

        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
