package com.example.demo.dto.post;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class PostCreateDto {
    @NotBlank(message = "Title is required")
    private String title;
    private String content;
    private boolean nsfw;

    @NotBlank(message = "Author username is required")
    private String authorUsername;

    private MultipartFile file;
}