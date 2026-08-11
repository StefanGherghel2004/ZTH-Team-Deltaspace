package com.example.demo.dto.user;

import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

@Data
public class UserUpdateDto {

    @Size(min = 3, max = 100, message = "Display Name must be between 3 and 100 characters")
    private String displayName;

    private String avatarUrl;
}
