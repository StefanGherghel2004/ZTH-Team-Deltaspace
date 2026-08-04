package com.example.demo.controller;

import com.example.demo.dto.auth.AuthRequestLoginDto;
import com.example.demo.dto.auth.AuthResponseDto;
import com.example.demo.model.User;
import com.example.demo.response.ApiError;
import com.example.demo.response.ApiResponse;
import com.example.demo.service.UserService;
import com.example.demo.service.auth.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final JwtService jwtService;

    private final AuthenticationManager authenticationManager;
    private final UserService userService;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponseDto>> loginUser(@RequestBody AuthRequestLoginDto request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
            );

            UserDetails userDetails = (UserDetails) authentication.getPrincipal();

            String jwtToken = jwtService.generateToken(userDetails.getUsername());
            User user = userService.findByUsername(request.getUsername());

            AuthResponseDto response = AuthResponseDto.builder()
                    .accessToken(jwtToken)
                    .user(AuthResponseDto.UserDto.builder()
                            .username(user.getUsername())
                            .email(user.getEmail())
                            .build())
                    .build();

            return ResponseEntity.ok(ApiResponse.success(response));
        } catch (AuthenticationException e) {
            ApiError apiError = new ApiError("UNAUTHORIZED", "Wrong credentials.", List.of());

            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error(apiError, "/auth/login"));
        }
    }
}
