package com.example.demo.dto.comment.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Data
public class CommentResponseDto {
    private UUID id;
    private UUID postId;
    private UUID parentId;

    private String content;
    private String author;
    private int upvotes;
    private int downvotes;
    private int score;
    private String userVote;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'")
    private OffsetDateTime createdAt;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'")
    private OffsetDateTime updatedAt;

    private List<CommentResponseDto> replies;
}
