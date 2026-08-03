package com.example.demo.dto.community;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CommunityCreateDto {
    @NotBlank(message= "Community Name is required")
    private String name;

    @NotBlank(message = "Display Name is required")
    private String displayName;

    @NotBlank(message = "Topic is required")
    private String topic;
    @NotBlank(message = "Description is required")
    private String description;

    private String iconUrl;

}
