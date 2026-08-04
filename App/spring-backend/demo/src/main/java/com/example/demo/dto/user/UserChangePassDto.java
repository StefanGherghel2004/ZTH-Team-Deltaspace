package com.example.demo.dto.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class UserChangePassDto {

    @NotBlank(message = "Current Password Required")
    @Size(min=8,message = "Password must be at least 8 characters long")
    private String currentPassword;

    @NotBlank(message = "New Password Required")
    @Size(min=8,message = "Password must be at least 8 characters long")
    private String newPassword;
}
