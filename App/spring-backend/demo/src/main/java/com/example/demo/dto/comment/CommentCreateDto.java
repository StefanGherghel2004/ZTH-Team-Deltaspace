package com.example.demo.dto.comment;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CommentCreateDto {

    @NotBlank(message = "Comment contents cannot be empty.")
    private String text;

    @NotNull(message = "Comment must be referred to a post.")
    private Long postId;

    private Long parentCommentId;
}
