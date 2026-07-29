package com.example.demo.dto.post.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.OffsetDateTime;
import java.util.UUID;

@Data
public class PostResponseDto {
    private UUID id;
    private String title;
    private String content;

    private boolean nsfw;
    private String author;

    private int upvotes;
    private int downvotes;

    private String userVote;
    private int score;

    private String subreddit;

    private String imageUrl;

    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
}
