package com.example.demo.servicetests;

import com.example.demo.dto.user.PasswordChangeRequestDto;
import com.example.demo.dto.user.UserCreateDto;
import com.example.demo.dto.user.UserDeleteDto;
import com.example.demo.dto.user.UserUpdateDto;
import com.example.demo.exception.AccessDeniedException;
import com.example.demo.exception.DuplicateUserInformationException;
import com.example.demo.exception.IdenticalPasswordException;
import com.example.demo.exception.UserTooYoungException;
import com.example.demo.exception.notfound.UserNotFoundException;
import com.example.demo.mapper.UserMapper;
import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.UserService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserService userService;

    private User sampleUser;

    @BeforeEach
    void setUp() {
        sampleUser = new User();
        sampleUser.setId(UUID.randomUUID());
        sampleUser.setUsername("testuser");
        sampleUser.setEmail("testuser@example.com");
        sampleUser.setPassword("encodedPassword");
        sampleUser.setDisplayName("Test User");
        sampleUser.setAvatarUrl("https://example.com/avatar.png");
        sampleUser.setDateOfBirth(LocalDate.now().minusYears(20));
        sampleUser.setDeleted(false);
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    private void mockAuthentication(String username) {
        Authentication auth = new UsernamePasswordAuthenticationToken(
                username,
                "password",
                List.of(new SimpleGrantedAuthority("ROLE_USER"))
        );
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(auth);
        SecurityContextHolder.setContext(context);
    }

    @Test
    @DisplayName("Should successfully add a user when all inputs are valid")
    void addUserSuccess() {
        UserCreateDto dto = new UserCreateDto("testuser", "testuser@example.com", "plainPassword123", LocalDate.now().minusYears(20));

        when(userMapper.toEntity(dto)).thenReturn(sampleUser);
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.empty());
        when(userRepository.findByEmail("testuser@example.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode(anyString())).thenReturn("hashedPassword123");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User result = userService.addUser(dto);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isNull();
        assertThat(result.getUsername()).isEqualTo("testuser");
        assertThat(result.getPassword()).isEqualTo("hashedPassword123");

        verify(userRepository, times(1)).save(sampleUser);
    }

    @Test
    @DisplayName("Should throw UserTooYoungException when date of birth indicates age < 13")
    void addUserUnderageThrowsUserTooYoungException() {
        UserCreateDto dto = new UserCreateDto("testuser", "testuser@example.com", "plainPassword123", LocalDate.now().minusYears(10));

        sampleUser.setDateOfBirth(LocalDate.now().minusYears(10));
        when(userMapper.toEntity(dto)).thenReturn(sampleUser);

        assertThatThrownBy(() -> userService.addUser(dto))
                .isInstanceOf(UserTooYoungException.class);

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Shouldn't add a user because username is already taken")
    void addUserFailUsernameAlreadyTaken() {
        UserCreateDto dto = new UserCreateDto("testuser", "testuser@example.com", "plainPassword123", LocalDate.now().minusYears(20));

        when(userMapper.toEntity(dto)).thenReturn(sampleUser);
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(sampleUser));

        assertThatThrownBy(() -> userService.addUser(dto))
                .isInstanceOf(DuplicateUserInformationException.class)
                .hasMessageContaining("username already exists");

        verify(userRepository, never()).findByEmail(anyString());
        verify(passwordEncoder, never()).encode(anyString());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Shouldn't add a user because email is already taken")
    void addUserFailEmailAlreadyTaken() {
        UserCreateDto dto = new UserCreateDto("testuser", "testuser@example.com", "plainPassword123", LocalDate.now().minusYears(20));

        when(userMapper.toEntity(dto)).thenReturn(sampleUser);
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.empty());
        when(userRepository.findByEmail("testuser@example.com")).thenReturn(Optional.of(sampleUser));

        assertThatThrownBy(() -> userService.addUser(dto))
                .isInstanceOf(DuplicateUserInformationException.class)
                .hasMessageContaining("email already exists");

        verify(passwordEncoder, never()).encode(anyString());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Should return user when found and account is active")
    void findByUsernameSuccess() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(sampleUser));

        User result = userService.findByUsername("testuser");

        assertThat(result).isNotNull();
        assertThat(result.getUsername()).isEqualTo("testuser");
        assertThat(result.isDeleted()).isFalse();

        verify(userRepository, times(1)).findByUsername("testuser");
    }

    @Test
    @DisplayName("Should throw UserNotFoundException when username is not found in database")
    void findByUsernameNotFoundThrowsUserNotFoundException() {
        when(userRepository.findByUsername("unknownUser")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.findByUsername("unknownUser"))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessageContaining("User not found with username: unknownUser");

        verify(userRepository, times(1)).findByUsername("unknownUser");
    }

    @Test
    @DisplayName("Should throw AccessDeniedException when the user account is marked as deleted")
    void findByUsernameAccountDeletedThrowsAccessDeniedException() {
        sampleUser.setDeleted(true);
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(sampleUser));

        assertThatThrownBy(() -> userService.findByUsername("testuser"))
                .isInstanceOf(AccessDeniedException.class)
                .hasMessageContaining("The user account you are trying to access is deleted.");

        verify(userRepository, times(1)).findByUsername("testuser");
    }

    @Test
    @DisplayName("Should return the authenticated user when valid authentication exists")
    void getAuthenticatedUserSuccess() {
        mockAuthentication("testuser");
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(sampleUser));

        User result = userService.getAuthenticatedUser();

        assertThat(result).isNotNull();
        assertThat(result.getUsername()).isEqualTo("testuser");
        verify(userRepository, times(1)).findByUsername("testuser");
    }

    @Test
    @DisplayName("Should throw BadCredentialsException when authentication is null")
    void getAuthenticatedUserNullAuthThrowsBadCredentialsException() {
        SecurityContextHolder.clearContext();

        assertThatThrownBy(() -> userService.getAuthenticatedUser())
                .isInstanceOf(BadCredentialsException.class)
                .hasMessageContaining("Full authentication is required");

        verify(userRepository, never()).findByUsername(anyString());
    }

    @Test
    @DisplayName("Should throw BadCredentialsException when isAuthenticated returns false")
    void getAuthenticatedUserNotAuthenticatedThrowsBadCredentialsException() {
        Authentication auth = mock(Authentication.class);
        when(auth.isAuthenticated()).thenReturn(false);

        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(auth);
        SecurityContextHolder.setContext(context);

        assertThatThrownBy(() -> userService.getAuthenticatedUser())
                .isInstanceOf(BadCredentialsException.class)
                .hasMessageContaining("Full authentication is required");

        verify(userRepository, never()).findByUsername(anyString());
    }

    @Test
    @DisplayName("Should throw BadCredentialsException when principal equals 'anonymousUser'")
    void getAuthenticatedUserAnonymousUserThrowsBadCredentialsException() {
        Authentication auth = mock(Authentication.class);
        when(auth.isAuthenticated()).thenReturn(true);
        when(auth.getPrincipal()).thenReturn("anonymousUser");

        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(auth);
        SecurityContextHolder.setContext(context);

        assertThatThrownBy(() -> userService.getAuthenticatedUser())
                .isInstanceOf(BadCredentialsException.class)
                .hasMessageContaining("Full authentication is required");

        verify(userRepository, never()).findByUsername(anyString());
    }

    @Test
    @DisplayName("Should update displayName and avatarUrl when both are provided")
    void updateAuthenticatedUserFullUpdateSuccess() {
        mockAuthentication("testuser");
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(sampleUser));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UserUpdateDto updateDto = new UserUpdateDto();
        updateDto.setDisplayName("Updated Test User");
        updateDto.setAvatarUrl("https://example.com/new-avatar.png");

        User updatedUser = userService.updateAuthenticatedUser(updateDto);

        assertThat(updatedUser).isNotNull();
        assertThat(updatedUser.getDisplayName()).isEqualTo("Updated Test User");
        assertThat(updatedUser.getAvatarUrl()).isEqualTo("https://example.com/new-avatar.png");

        verify(userRepository, times(1)).save(sampleUser);
    }

    @Test
    @DisplayName("Should fallback to username and empty avatarUrl when fields in DTO are null")
    void updateAuthenticatedUserNullFieldsAppliesDefaults() {
        mockAuthentication("testuser");
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(sampleUser));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UserUpdateDto updateDto = new UserUpdateDto();
        updateDto.setDisplayName(null);
        updateDto.setAvatarUrl(null);

        User updatedUser = userService.updateAuthenticatedUser(updateDto);

        assertThat(updatedUser).isNotNull();
        assertThat(updatedUser.getDisplayName()).isEqualTo("testuser");
        assertThat(updatedUser.getAvatarUrl()).isEqualTo("");

        verify(userRepository, times(1)).save(sampleUser);
    }

    @Test
    @DisplayName("Should successfully soft-delete user when password is correct")
    void deleteAuthenticatedUserSuccess() {
        mockAuthentication("testuser");
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(sampleUser));
        when(passwordEncoder.matches("plainPassword123", sampleUser.getPassword())).thenReturn(true);

        UserDeleteDto deleteDto = new UserDeleteDto();
        deleteDto.setPassword("plainPassword123");

        userService.deleteAuthenticatedUser(deleteDto);

        assertThat(sampleUser.isDeleted()).isTrue();
        verify(userRepository, times(1)).save(sampleUser);
    }

    @Test
    @DisplayName("Should throw AccessDeniedException when password does not match")
    void deleteAuthenticatedUserPasswordMismatchThrowsAccessDeniedException() {
        mockAuthentication("testuser");
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(sampleUser));
        when(passwordEncoder.matches("wrongPassword", sampleUser.getPassword())).thenReturn(false);

        UserDeleteDto deleteDto = new UserDeleteDto();
        deleteDto.setPassword("wrongPassword");

        assertThatThrownBy(() -> userService.deleteAuthenticatedUser(deleteDto))
                .isInstanceOf(AccessDeniedException.class)
                .hasMessageContaining("Password is incorrect");

        assertThat(sampleUser.isDeleted()).isFalse();
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Should successfully change password when current password matches and new password is distinct")
    void changePasswordSuccess() {
        mockAuthentication("testuser");
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(sampleUser));
        when(passwordEncoder.matches("oldPassword", sampleUser.getPassword())).thenReturn(true);
        when(passwordEncoder.matches("newPassword123", sampleUser.getPassword())).thenReturn(false);
        when(passwordEncoder.encode("newPassword123")).thenReturn("newHashedPassword");

        PasswordChangeRequestDto dto = new PasswordChangeRequestDto();
        dto.setCurrentPassword("oldPassword");
        dto.setNewPassword("newPassword123");

        userService.changePassword(dto);

        assertThat(sampleUser.getPassword()).isEqualTo("newHashedPassword");
        verify(userRepository, times(1)).save(sampleUser);
    }

    @Test
    @DisplayName("Should throw AccessDeniedException when current password is incorrect")
    void changePasswordIncorrectCurrentPasswordThrowsAccessDeniedException() {
        mockAuthentication("testuser");
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(sampleUser));
        when(passwordEncoder.matches("wrongCurrentPassword", sampleUser.getPassword())).thenReturn(false);

        PasswordChangeRequestDto dto = new PasswordChangeRequestDto();
        dto.setCurrentPassword("wrongCurrentPassword");
        dto.setNewPassword("newPassword123");

        assertThatThrownBy(() -> userService.changePassword(dto))
                .isInstanceOf(AccessDeniedException.class)
                .hasMessageContaining("Current password is incorrect");

        verify(passwordEncoder, never()).encode(anyString());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Should throw IdenticalPasswordException when new password matches current password")
    void changePasswordIdenticalNewPasswordThrowsIdenticalPasswordException() {
        mockAuthentication("testuser");
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(sampleUser));
        when(passwordEncoder.matches("oldPassword", sampleUser.getPassword())).thenReturn(true);
        when(passwordEncoder.matches("oldPassword", sampleUser.getPassword())).thenReturn(true);

        PasswordChangeRequestDto dto = new PasswordChangeRequestDto();
        dto.setCurrentPassword("oldPassword");
        dto.setNewPassword("oldPassword");

        assertThatThrownBy(() -> userService.changePassword(dto))
                .isInstanceOf(IdenticalPasswordException.class)
                .hasMessageContaining("New password cannot be the same as the current password.");

        verify(passwordEncoder, never()).encode(anyString());
        verify(userRepository, never()).save(any(User.class));
    }
}
