package com.example.demo.controller;

import com.example.demo.annotation.RateLimit;
import com.example.demo.dto.auth.AuthRequestLoginDto;
import com.example.demo.dto.auth.AuthResponseDto;
import com.example.demo.dto.user.*;
import com.example.demo.logger.Logger;
import com.example.demo.dto.user.*;
import com.example.demo.model.User;
import com.example.demo.response.ApiResponse;
import com.example.demo.service.ApiResponseService;
import com.example.demo.service.UserService;

import com.example.demo.service.auth.AuthService;
import com.example.demo.service.auth.JwtService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final ApiResponseService apiResponseService;
    private final AuthService authService;

    @RateLimit
    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<ApiResponse<AuthResponseDto>> addUser(@Valid @RequestBody UserCreateDto createDto) {
        AuthResponseDto response = authService.register(createDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(response));
    }

    @RateLimit
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponseDto>> loginUser(@RequestBody AuthRequestLoginDto request) {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
            );

            UserDetails userDetails = (UserDetails) authentication.getPrincipal();

            String jwtToken;
            if (userDetails != null) {
                jwtToken = jwtService.generateToken(userDetails.getUsername());
            }
            else{
                jwtToken = null;
            }
            User user = userService.findByUsername(request.getUsername());

            AuthResponseDto response = apiResponseService.getAuthenticationResponse(user,jwtToken);

            Logger.info("User %s logged in", user.getUsername());
            return ResponseEntity.ok(ApiResponse.success(response));
    }

    @RateLimit(requests = 100)
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserResponseDto>> getAuthenticatedUser() {
        User authenticatedUser = userService.getAuthenticatedUser();
        UserResponseDto response = UserResponseDto.fromEntity(authenticatedUser);

        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @DeleteMapping("/me")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<ApiResponse<Void>> deleteUser(@RequestBody UserDeleteDto userDeleteDto) {

        userService.deleteAuthenticatedUser(userDeleteDto);
        return ResponseEntity.ok(ApiResponse.successMessage("Account deleted successfully"));
    }

    @RateLimit
    @PutMapping("/me")
    public ResponseEntity<ApiResponse<UserResponseDto>> updateUserDisplayNameOrAvatar(@Valid @RequestBody UserUpdateDto updateDto) {
        User updatedUser = userService.updateAuthenticatedUser(updateDto);
        UserResponseDto response = UserResponseDto.fromEntity(updatedUser);

        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @RateLimit
    @PutMapping("/me/password")
    public ResponseEntity<ApiResponse<String>> changePassword(
            @Valid @RequestBody PasswordChangeRequestDto passwordDto) {
        userService.changePassword(passwordDto);
        return ResponseEntity.ok(ApiResponse.success("Password changed successfully"));
    }
}
