package com.example.demo.dto.subreddit;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class SubredditCreateDto {
    @NotBlank(message= "Subreddit Name is required")
    @Size(min=3,max=50,message = "Name must be  between 3 and 50 characters, alphanumerical + underscore")

    @Pattern(regexp="^[a-zA-Z_]+$")
    private String name;

    @NotBlank(message = "Display Name is required")
    @Size(min=3,max=100,message = "Display Name must be between 3 and 100 characters")
    private String displayName;


    private String topic;

    @NotBlank(message = "Description is required")
    @Size(max=500,message = "Description must be max 500 characters")
    private String description;

    private String iconUrl;

}
