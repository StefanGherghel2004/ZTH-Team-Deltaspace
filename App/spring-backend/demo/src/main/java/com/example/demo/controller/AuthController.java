package com.example.demo.controller;

import com.example.demo.dto.auth.AuthRequestDto;
import com.example.demo.model.User;
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

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final JwtService jwtService;

    private final AuthenticationManager authenticationManager;
    private final UserService userService;

    @PostMapping
    public ResponseEntity<?> loginUser(@RequestBody AuthRequestDto request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsernameOrEmail(), request.getPassword())
            );

            UserDetails userDetails = (UserDetails) authentication.getPrincipal();

            String jwtToken = jwtService.generateToken(userDetails.getUsername());
            User user = userService.findByUsernameOrEmail(request.getUsernameOrEmail());

            return ResponseEntity.ok(Map.of(
                    "token", jwtToken,
                    "user", user
            ));
        } catch (AuthenticationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Wrong credentials.");
        }
    }


}
