package com.example.demo.dto.subreddit.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.OffsetDateTime;
import java.util.UUID;

@Data
public class SubredditResponseDto {
    private UUID id;
    private String name;
    private String displayName;
    private String description;
    private int memberCount;
    private int postCount;

    private String iconUrl;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", timezone = "UTC")
    OffsetDateTime createdAt;
}
