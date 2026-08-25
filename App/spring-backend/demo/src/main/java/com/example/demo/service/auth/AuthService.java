package com.example.demo.service.auth;

import com.example.demo.dto.auth.AuthResponseDto;
import com.example.demo.dto.user.UserCreateDto;
import com.example.demo.model.User;
import com.example.demo.model.VerificationToken;
import com.example.demo.repository.UserRepository;
import com.example.demo.repository.VerificationTokenRepository;
import com.example.demo.service.ApiResponseService;
import com.example.demo.service.EmailService;
import com.example.demo.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final ApiResponseService apiResponseService;
    private final UserService userService;
    private final JwtService jwtService;
    private final EmailService emailService;

    private final VerificationTokenRepository tokenRepository;
    private final UserRepository userRepository;

    @Value("${app.base-url}")
    private String baseUrl;

    @Transactional
    public AuthResponseDto register(UserCreateDto createDto) {
        User savedUser = userService.addUser(createDto);

        String token = UUID.randomUUID().toString();
        VerificationToken verificationToken = new VerificationToken(token, savedUser, 1440);
        tokenRepository.save(verificationToken);

        String verificationUrl = baseUrl + "/email-auth/verify?token=" + token;
        emailService.sendVerificationEmail(savedUser.getEmail(), verificationUrl);

        String jwtToken = jwtService.generateToken(savedUser.getUsername());
        return apiResponseService.getAuthenticationResponse(savedUser, jwtToken);
    }

    @Transactional
    public String verifyEmailToken(String token) {
        VerificationToken verificationToken = tokenRepository.findByToken(token)
                .orElse(null);

        if (verificationToken == null) {
            return "Invalid verification link.";
        }

        if (verificationToken.isExpired()) {
            return "This verification link has expired.";
        }

        User user = verificationToken.getUser();
        user.setVerified(true);
        userRepository.save(user);

        tokenRepository.delete(verificationToken);

        return "Email verified successfully! You can now log in.";
    }
}