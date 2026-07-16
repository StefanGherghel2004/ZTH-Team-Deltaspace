package com.example.demo.dto.comment;

import jakarta.validation.constraints.NotBlank;

public class CommentCreateDto {

    @NotBlank(message = "Comment contents cannot be empty.")
    private String text;

    @NotBlank(message = "Author username is required.")
    private String authorUsername;

    @NotBlank(message = "Comment must be referred to a post.")
    private Long postId;

    private Long parentCommentId;
}
