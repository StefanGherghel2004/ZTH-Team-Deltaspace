package com.example.demo.dto.community;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CommunityCreateDto {
    @NotBlank(message= "Community Name is required")
    private String title;
    private String topic2;
    private String topic;
    private String description;
}
