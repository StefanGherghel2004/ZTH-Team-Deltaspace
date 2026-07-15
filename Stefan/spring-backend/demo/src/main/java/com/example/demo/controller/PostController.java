package com.example.demo.controller;

import com.example.demo.dto.post.PostCreateDto;
import com.example.demo.model.Post;
import com.example.demo.service.PostService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public Post createPost(@Valid @ModelAttribute PostCreateDto dto) {
        return postService.createPost(dto);
    }

    @GetMapping("/{id}")
    public Post getPostById(@PathVariable Long id) {
        return postService.findById(id);
    }

    @GetMapping
    public List<Post> listAllPosts() {
        return postService.getAllPosts();
    }
}