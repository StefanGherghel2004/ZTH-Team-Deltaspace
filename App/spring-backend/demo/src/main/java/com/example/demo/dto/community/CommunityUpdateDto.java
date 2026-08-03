package com.example.demo.dto.community;


import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CommunityUpdateDto {
    private String topic;

    @Size(max=500,message = "Description must be max 500 characters")
    private String description;

    @Size(min=3,max=100,message = "Display Name must be between 3 and 100 characters")
    private String displayName;

    private String iconUrl;


}
