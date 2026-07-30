package com.example.demo.controller;

import com.example.demo.dto.post.PostCreateDto;
import com.example.demo.dto.post.PostUpdateDto;
import com.example.demo.dto.post.response.PostResponseDto;
import com.example.demo.dto.vote.VoteRequestDto;
import com.example.demo.dto.vote.VoteResponseDto;
import com.example.demo.mapper.PostMapper;
import com.example.demo.model.Post;
import com.example.demo.response.ApiResponse;
import com.example.demo.service.PostService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;
    private final PostMapper postMapper;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<PostResponseDto>> createPost(@Valid @ModelAttribute PostCreateDto dto) {
        Post createdPost = postService.createPost(dto);

        PostResponseDto response = postService.getEnrichedPostDto(createdPost);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(response));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<PostResponseDto>> getPostById(@PathVariable UUID id) {
        Post post = postService.findById(id);

        PostResponseDto response = postService.getEnrichedPostDto(post);

        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<PostResponseDto>>> getPosts(@RequestParam(required = false) String subreddit) {
        List<Post> posts;
        if (subreddit != null && !subreddit.trim().isEmpty()) {
            posts = postService.getCommunityPosts(subreddit);
        } else {
            posts = postService.getAllPosts();
        }

        List<PostResponseDto> response = posts.stream()
                .map(postService::getEnrichedPostDto)
                .toList();

        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PutMapping("/{id}/vote")
    public ResponseEntity<ApiResponse<VoteResponseDto>> votePost(
            @PathVariable UUID id,
            @Valid @RequestBody VoteRequestDto voteDto) {

        VoteResponseDto response = postService.votePost(id, voteDto.getVoteType());

        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<PostResponseDto>> updatePost(@PathVariable UUID id, @Valid @ModelAttribute PostUpdateDto updateDto){
        Post updatedPost = postService.updatePost(id, updateDto);
        
        PostResponseDto response = postService.getEnrichedPostDto(updatedPost);

        return ResponseEntity.ok(ApiResponse.success(response));
    }

    // this is 200 OK because 204 DELETED would not have a body and the docs specify this body
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deletePostById (@PathVariable UUID id) {

        postService.deletePostById(id);

        return ResponseEntity.ok(
                ApiResponse.successMessage("Post deleted successfully")
        );

    }


}