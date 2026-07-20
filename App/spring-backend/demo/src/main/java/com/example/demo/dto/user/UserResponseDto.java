package com.example.demo.dto.user;

import lombok.Data;
import java.time.LocalDate;

@Data
public class UserResponseDto {
    private Long id;
    private String username;
    private String email;
    private LocalDate dateOfBirth;
}