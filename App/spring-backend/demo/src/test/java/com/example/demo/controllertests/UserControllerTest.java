package com.example.demo.controllertests;

import com.example.demo.controller.UserController;
import com.example.demo.dto.auth.AuthRequestLoginDto;
import com.example.demo.dto.auth.AuthResponseDto;
import com.example.demo.dto.user.*;
import com.example.demo.model.User;
import com.example.demo.service.ApiResponseService;
import com.example.demo.service.UserService;
import com.example.demo.service.auth.AuthService;
import com.example.demo.service.auth.JwtService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private UserService userService;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtService jwtService;

    @Mock
    private ApiResponseService apiResponseService;

    @Mock
    private AuthService authService;

    @InjectMocks
    private UserController userController;

    private User sampleUser;
    private AuthResponseDto sampleAuthResponse;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();

        sampleUser = new User();
        sampleUser.setId(UUID.randomUUID());
        sampleUser.setUsername("testuser");
        sampleUser.setEmail("testuser@example.com");

        sampleAuthResponse = AuthResponseDto.builder()
                .accessToken("jwt-token-123")
                .user(AuthResponseDto.UserDto.builder()
                        .username("testuser")
                        .email("testuser@example.com")
                        .build())
                .build();
    }

    @Test
    @DisplayName("POST /auth/register - Should register user and return 201 Created")
    void addUserSuccess() throws Exception {
        UserCreateDto createDto = new UserCreateDto("testuser","testuser@example.com","password123!", null);

        when(authService.register(any(UserCreateDto.class))).thenReturn(sampleAuthResponse);

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.accessToken").value("jwt-token-123"))
                .andExpect(jsonPath("$.data.user.username").value("testuser"))
                .andExpect(jsonPath("$.data.user.email").value("testuser@example.com"));

        verify(authService, times(1)).register(any(UserCreateDto.class));
    }

    @Test
    @DisplayName("POST /auth/login - Should authenticate user and return 200 OK with token")
    void loginUserSuccess() throws Exception {
        AuthRequestLoginDto loginDto = new AuthRequestLoginDto();
        loginDto.setUsername("testuser");
        loginDto.setPassword("password123!");

        Authentication mockAuth = mock(Authentication.class);
        UserDetails mockUserDetails = mock(UserDetails.class);

        when(mockUserDetails.getUsername()).thenReturn("testuser");
        when(mockAuth.getPrincipal()).thenReturn(mockUserDetails);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(mockAuth);
        when(jwtService.generateToken("testuser")).thenReturn("jwt-token-123");
        when(userService.findByUsername("testuser")).thenReturn(sampleUser);
        when(apiResponseService.getAuthenticationResponse(sampleUser, "jwt-token-123")).thenReturn(sampleAuthResponse);

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.accessToken").value("jwt-token-123"))
                .andExpect(jsonPath("$.data.user.username").value("testuser"))
                .andExpect(jsonPath("$.data.user.email").value("testuser@example.com"));

        verify(authenticationManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(jwtService, times(1)).generateToken("testuser");
        verify(userService, times(1)).findByUsername("testuser");
    }

    @Test
    @DisplayName("GET /auth/me - Should return authenticated user details")
    void getAuthenticatedUserSuccess() throws Exception {
        when(userService.getAuthenticatedUser()).thenReturn(sampleUser);

        mockMvc.perform(get("/auth/me"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.username").value("testuser"))
                .andExpect(jsonPath("$.data.email").value("testuser@example.com"));

        verify(userService, times(1)).getAuthenticatedUser();
    }

    @Test
    @DisplayName("DELETE /auth/me - Should delete authenticated user and return success message")
    void deleteUserSuccess() throws Exception {
        UserDeleteDto deleteDto = new UserDeleteDto();
        deleteDto.setPassword("password123!");

        doNothing().when(userService).deleteAuthenticatedUser(any(UserDeleteDto.class));

        mockMvc.perform(delete("/auth/me")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(deleteDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Account deleted successfully"));

        verify(userService, times(1)).deleteAuthenticatedUser(any(UserDeleteDto.class));
    }

    @Test
    @DisplayName("PUT /auth/me - Should update display name or avatar and return updated user")
    void updateUserDisplayNameOrAvatarSuccess() throws Exception {
        UserUpdateDto updateDto = new UserUpdateDto();
        updateDto.setDisplayName("Updated Name");
        updateDto.setAvatarUrl("https://avatar.url/img.png");

        when(userService.updateAuthenticatedUser(any(UserUpdateDto.class))).thenReturn(sampleUser);

        mockMvc.perform(put("/auth/me")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.username").value("testuser"));

        verify(userService, times(1)).updateAuthenticatedUser(any(UserUpdateDto.class));
    }

    @Test
    @DisplayName("PUT /auth/me/password - Should update password and return success message")
    void changePasswordSuccess() throws Exception {
        PasswordChangeRequestDto passwordDto = new PasswordChangeRequestDto();
        passwordDto.setCurrentPassword("oldPassword123!");
        passwordDto.setNewPassword("newPassword123!");

        doNothing().when(userService).changePassword(any(PasswordChangeRequestDto.class));

        mockMvc.perform(put("/auth/me/password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(passwordDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").value("Password changed successfully"));

        verify(userService, times(1)).changePassword(any(PasswordChangeRequestDto.class));
    }
}