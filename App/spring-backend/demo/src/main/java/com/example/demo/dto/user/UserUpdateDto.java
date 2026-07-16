package com.example.demo.dto.user;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.time.LocalDate;

@Data
public class UserUpdateDto {

    @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{8,}$",
            message = "Password must be at least 8 characters long, contain an uppercase letter, a digit, and a special character")
    private String password;

    @JsonFormat(pattern = "dd-MM-yyyy")
    private LocalDate dateOfBirth;
}
