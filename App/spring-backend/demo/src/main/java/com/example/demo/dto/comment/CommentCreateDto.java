package com.example.demo.dto.comment;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class CommentCreateDto {

    @NotBlank(message = "Comment contents cannot be empty.")
    private String content;

    @NotNull(message = "Comment must be referred to a user.")
    private String author;

    private UUID parentId;
}
