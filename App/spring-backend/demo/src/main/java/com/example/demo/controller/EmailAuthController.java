package com.example.demo.controller;

import com.example.demo.annotation.RateLimit;
import com.example.demo.logger.Logger;
import com.example.demo.response.ApiResponse;
import com.example.demo.service.auth.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/email-auth")
@RequiredArgsConstructor
public class EmailAuthController {

    private final AuthService authService;

    @RateLimit(requests = 20)
    @GetMapping("/verify")
    public ResponseEntity<ApiResponse<String>> verifyEmail(@RequestParam("token") String token) {
        String result = authService.verifyEmailToken(token);
        Logger.info("Email verification processed for token: %s", token);
        return ResponseEntity.ok(ApiResponse.success(result));
    }
}