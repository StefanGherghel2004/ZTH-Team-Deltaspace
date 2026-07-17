package com.example.demo.dto.comment;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CommentUpdateDto {
    @NotBlank(message = "Comment contents cannot be empty.")
    private String text;
}
