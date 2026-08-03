package com.example.demo.dto.post;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class PostUpdateDto {
    @Size(min = 3, max = 300, message = "Title must be between 3 and 300 characters")
    @Pattern(regexp = ".*\\S.*", message = "Title cannot consist only of spaces")
    private String title;

    @Size(max = 10000, message = "Content cannot exceed 10000 characters")
    private String content;
    private boolean nsfw;

    private Integer filter;

    private MultipartFile image;
}
