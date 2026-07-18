package com.example.demo.controller;

import com.example.demo.dto.post.PostFeedDto;
import com.example.demo.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("api/feed")
@RequiredArgsConstructor
public class FeedController {
    private final PostService postService;

    @GetMapping
    public PostFeedDto getRandomizedFeed(@RequestParam(required = false) String seed, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size){
        String feedSeed = seed;
        if(seed.isEmpty() || seed==null){
            feedSeed= UUID.randomUUID().toString();
        }
        return postService.getRandomizedFeed(feedSeed,page,size);
    }
}
