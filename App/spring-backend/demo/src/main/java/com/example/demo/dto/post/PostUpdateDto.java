package com.example.demo.dto.post;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class PostUpdateDto {
    @NotBlank
    private String title;

    private String content;
    private boolean nsfw;
    private MultipartFile file;
}
