package com.example.demo.dto.user;

import lombok.Data;
import java.time.LocalDate;
import java.util.UUID;

@Data
public class UserResponseDto {
    private UUID id;
    private String username;
    private String email;
    private LocalDate dateOfBirth;
}