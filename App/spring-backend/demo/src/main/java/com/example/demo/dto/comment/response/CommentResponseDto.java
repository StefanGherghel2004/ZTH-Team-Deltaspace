package com.example.demo.dto.comment.response;

import com.example.demo.model.enums.VoteType;
import lombok.Data;

import java.time.OffsetDateTime;
import java.util.UUID;

@Data
public class CommentResponseDto {
    private UUID id;
    private UUID postId;
    private UUID parentId;

    private String text;
    private String author;
    private int upVotes;
    private int downVotes;
    private int score;
    private String commentVote;

    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
}
