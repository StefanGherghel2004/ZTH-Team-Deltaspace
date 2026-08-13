package com.example.demo.service.auth;

import com.example.demo.dto.auth.AuthResponseDto;
import com.example.demo.dto.user.UserCreateDto;
import com.example.demo.model.User;
import com.example.demo.service.ApiResponseService;
import com.example.demo.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final ApiResponseService apiResponseService;
    private final UserService userService;
    private final JwtService jwtService;

    @Transactional
    public AuthResponseDto register(UserCreateDto createDto){
        User savedUser = userService.addUser(createDto);
        String jwtToken = jwtService.generateToken(savedUser.getUsername());

        return apiResponseService.getAuthenticationResponse(savedUser,jwtToken);
    }

}
