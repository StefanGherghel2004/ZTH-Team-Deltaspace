package com.example.demo.dto.post;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class PostCreateDto {
    @NotBlank(message = "Title is required")
    @Size(min = 3, max = 300, message = "Title must be between 3 and 300 characters")
    private String title;

    @Size(max = 10000, message = "Content cannot exceed 10000 characters")
    private String content;

    //private boolean nsfw;

    private Integer filter;

    @NotBlank(message = "Subreddit is required")
    private String subreddit;

    private MultipartFile image;
}