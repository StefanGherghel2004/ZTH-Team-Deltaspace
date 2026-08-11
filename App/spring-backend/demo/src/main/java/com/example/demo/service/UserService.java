package com.example.demo.service;

import com.example.demo.dto.user.PasswordChangeRequestDto;
import com.example.demo.dto.user.UserCreateDto;
import com.example.demo.exception.AccessDeniedException;
import com.example.demo.dto.user.UserUpdateDto;
import com.example.demo.exception.DuplicateUserInformationException;
import com.example.demo.exception.IdenticalPasswordException;
import com.example.demo.exception.notfound.UserNotFoundException;
import com.example.demo.exception.UserTooYoungException;
import com.example.demo.mapper.UserMapper;
import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class UserService {

    private static final int MIN_AGE = 13;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    public User addUser(UserCreateDto userCreateDto) {
        User user = userMapper.toEntity(userCreateDto);
        user.setId(null);
        validateAge(user.getDateOfBirth());

        if(userRepository.findByUsername(user.getUsername()).isPresent())
            throw new DuplicateUserInformationException("A user with this username already exists, please choose another username.");

        if(userRepository.findByEmail(user.getEmail()).isPresent())
            throw new DuplicateUserInformationException("A user with this email already exists.");

        log.info("Adding new user: {}", user.getUsername());

        String hashedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(hashedPassword);
        return userRepository.save(user);
    }

    public User updateAuthenticatedUser(UserUpdateDto updateDto) {
        User user = getAuthenticatedUser();
        return applyUpdatesAndSave(user, updateDto);
    }

    private User applyUpdatesAndSave(User user, UserUpdateDto updateDto) {
        if (updateDto.getDisplayName() != null) {
            user.setDisplayName(updateDto.getDisplayName());
        }
        if (updateDto.getAvatarUrl() != null) {
            user.setAvatarUrl(updateDto.getAvatarUrl());
        }
        return userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public User findByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User not found with username: " + username));
        if (user.isDeleted())
            throw new AccessDeniedException("The user account you are trying to access is deleted.");

        return user;
    }

    @Transactional(readOnly = true)
    public User findByUsernameOrEmail(String usernameOrEmail) {
        return userRepository.findByUsernameOrEmail(usernameOrEmail, usernameOrEmail)
                .orElseThrow(() -> new UserNotFoundException(
                        "User not found with username or email: " + usernameOrEmail));
    }

    @Transactional(readOnly = true)
    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found with email: " + email + "."));
    }

    public void deleteUserByUsername(String username) {
        User user = getAuthenticatedUser();
        if (!user.getUsername().equals(username)) {
            throw new AccessDeniedException("This account is not yours.");
        }

        user.setDeleted(true);
        userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public User getAuthenticatedUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
            throw new BadCredentialsException("Full authentication is required.");
        }

        return findByUsername(auth.getName());
    }

    private void validateAge(LocalDate dateOfBirth) {
        if (dateOfBirth == null) {
            return;
        }

        if (Period.between(dateOfBirth, LocalDate.now()).getYears() < MIN_AGE) {
            throw new UserTooYoungException(MIN_AGE);
        }
    }

    public void changePassword(PasswordChangeRequestDto passwordDto) {
        User authenticatedUser = getAuthenticatedUser();

        if (!passwordEncoder.matches(passwordDto.getCurrentPassword(), authenticatedUser.getPassword())) {
            throw new BadCredentialsException("Current password is incorrect");
        }

        if (passwordEncoder.matches(passwordDto.getNewPassword(), authenticatedUser.getPassword())) {
            throw new IdenticalPasswordException("New password cannot be the same as the current password.");
        }

        authenticatedUser.setPassword(passwordEncoder.encode(passwordDto.getNewPassword()));
        userRepository.save(authenticatedUser);
    }

    public User maskIfDeleted(User user){
        if(!user.isDeleted()){
            return user;
        }
        User masked = userMapper.clone(user);
        masked.setDeleted(true);
        masked.setUsername("[DELETED]");

        return masked;
    }
}