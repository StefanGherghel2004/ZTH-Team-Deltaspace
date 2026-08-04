package com.example.demo.controller;

import com.example.demo.dto.auth.AuthResponseDto;
import com.example.demo.dto.user.PasswordChangeRequestDto;
import com.example.demo.dto.user.UserCreateDto;
import com.example.demo.dto.user.UserResponseDto;
import com.example.demo.dto.user.UserUpdateDto;
import com.example.demo.mapper.UserMapper;
import com.example.demo.model.User;
import com.example.demo.response.ApiResponse;
import com.example.demo.service.UserService;
import java.util.List;

import com.example.demo.service.auth.JwtService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final UserMapper userMapper;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<ApiResponse<AuthResponseDto>> addUser(@Valid @RequestBody UserCreateDto createDto) {
        User userToSave = userMapper.toEntity(createDto);
        User savedUser = userService.addUser(userToSave);

        String jwtToken = jwtService.generateToken(savedUser.getUsername());

        AuthResponseDto response = AuthResponseDto.builder()
                .accessToken(jwtToken)
                .user(AuthResponseDto.UserDto.builder()
                        .username(savedUser.getUsername())
                        .email(savedUser.getEmail())
                        .build())
                .build();

            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(ApiResponse.success(response));
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserResponseDto>> getAuthenticatedUser() {
        User authenticatedUser = userService.getAuthenticatedUser();
        UserResponseDto response = userMapper.toResponseDto(authenticatedUser);

        return ResponseEntity.ok(ApiResponse.success(response));
    }

    // a soft delete under hood (sets deleted = true)
    @DeleteMapping("/{username}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable String username) {
        userService.deleteUserByUsername(username);
    }

    @PutMapping("/me")
    public ResponseEntity<ApiResponse<UserResponseDto>> updateUserDisplayNameOrAvatar(@Valid @RequestBody UserUpdateDto updateDto) {
        User updatedUser = userService.updateAuthenticatedUser(updateDto);

        UserResponseDto response = UserResponseDto.builder()
                .username(updatedUser.getUsername())
                .email(updatedUser.getEmail())
                .displayName(updatedUser.getDisplayName())
                .avatarUrl(updatedUser.getAvatarUrl())
                .build();

        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PutMapping("/me/password")
    public ResponseEntity<ApiResponse<String>> changePassword(
            @Valid @RequestBody PasswordChangeRequestDto passwordDto
    ) {
        userService.changePassword(passwordDto);
        return ResponseEntity.ok(ApiResponse.success("Password changed successfully"));
    }
}
