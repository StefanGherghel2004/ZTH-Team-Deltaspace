package com.example.demo.controller;

import com.example.demo.dto.post.PostCreateDto;
import com.example.demo.dto.post.PostFeedDto;
import com.example.demo.dto.post.PostUpdateDto;
import com.example.demo.model.Post;
import com.example.demo.response.ApiResponse;
import com.example.demo.service.PostService;
import com.example.demo.service.UserService;
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

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<Post>> createPost(@Valid @ModelAttribute PostCreateDto dto) {
        Post createdPost = postService.createPost(dto);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(createdPost));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Post>> getPostById(@PathVariable UUID id) {
        Post post = postService.findById(id);
        return ResponseEntity.ok(ApiResponse.success(post));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<Post>>> getPosts(@RequestParam(required = false) String subreddit) {
        List<Post> posts;
        if (subreddit != null && !subreddit.trim().isEmpty()) {
            posts = postService.getCommunityPosts(subreddit);
        } else {
            posts = postService.getAllPosts();
        }

        return ResponseEntity.ok(ApiResponse.success(posts));
    }

    @PutMapping("/{id}")
    public Post updatePost(@PathVariable UUID id, @Valid @ModelAttribute PostUpdateDto updateDto){
        return postService.updatePost(id,updateDto);
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