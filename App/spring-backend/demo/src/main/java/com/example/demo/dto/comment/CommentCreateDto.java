package com.example.demo.dto.comment;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.UUID;

@Data
public class CommentCreateDto {

    @NotBlank(message = "Comment contents cannot be empty.")
    @Size(max=1000,message="You are allowed maximum 1000 characters!")
    private String content;

    private UUID parentId;
}
