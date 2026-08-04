package com.example.demo.dto.user;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDate;
import java.util.UUID;

@Data
@Builder
public class UserResponseDto {

    private String username;
    private String email;
    private String displayName;
    private String avatarUrl;
}