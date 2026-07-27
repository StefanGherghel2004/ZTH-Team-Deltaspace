package com.example.demo.dto.post.response;

import lombok.Data;

import java.time.OffsetDateTime;
import java.util.UUID;

@Data
public class PostResponseDto {
    private UUID id;
    private String title;
    private String content;

    private String author;

    private String subreddit;

    private String imageUrl;

    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
}
