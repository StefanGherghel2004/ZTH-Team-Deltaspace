package com.example.demo.service;

import com.example.demo.dto.auth.PasswordChangeDto;
import com.example.demo.dto.user.UserUpdateDto;
import com.example.demo.exception.AccessDeniedException;
import com.example.demo.exception.notfound.UserNotFoundException;
import com.example.demo.exception.UserTooYoungException;
import com.example.demo.logger.Logger;
import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private static final int MIN_AGE = 13;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final S3ImageService s3ImageService;


    public User addUser(User user) {
        user.setId(null);
        validateAge(user.getDateOfBirth());

        Logger.info("Adding new user" + user);

        String hashedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(hashedPassword);
        return userRepository.save(user);
    }

    public List<User> listAllUsers() {
        return userRepository.findAll();
    }

    public User updateAuthenticatedUser(UserUpdateDto updateDto) {
        User user = getAuthenticatedUser();
        return applyUpdatesAndSave(user, updateDto);
    }

    public User updateUserByUsername(String username, UserUpdateDto updateDto) {
        User authenticatedUser = getAuthenticatedUser();

        if (!authenticatedUser.getUsername().equals(username)) {
            throw new AccessDeniedException("This account is not yours.");
        }

        return applyUpdatesAndSave(authenticatedUser, updateDto);
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

    public User findByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User not found with username: " + username));
    }

    public User findByUsernameOrEmail(String usernameOrEmail) {
        return userRepository.findByUsernameOrEmail(usernameOrEmail,usernameOrEmail)
                .orElseThrow(() -> new UserNotFoundException(
                        "User not found with username or email: " + usernameOrEmail));
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found with email: " + email + "."));
    }

    public void deleteUserByUsername(String username) {
        User user = getAuthenticatedUser();

        if (!user.getUsername().equals(username)) {
            throw new AccessDeniedException("This account is not yours.");
        }

        userRepository.delete(user);
    }

    public User getAuthenticatedUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
            return getOrCreateCurrentUser();
        }

        String username = auth.getName();

        return findByUsername(username);
    }

    private User getOrCreateCurrentUser() {
        String username = "current_user";

        return userRepository.findByUsername(username)
                .orElseGet(() -> {

                    Logger.warning("Creating hardcoded current_user");
                    User newUser = new User();
                    newUser.setUsername(username);
                    newUser.setEmail("current_user@gmail.com");
                    newUser.setPassword(passwordEncoder.encode("Parola1111+"));
                    newUser.setDateOfBirth(LocalDate.now().minusYears(20));

                    return userRepository.save(newUser);
                });
    }

    private void validateAge(LocalDate dateOfBirth) {

        if (dateOfBirth == null) {
            return;
        }

        if (Period.between(dateOfBirth, LocalDate.now()).getYears() < MIN_AGE) {
            throw new UserTooYoungException(MIN_AGE);
        }
    }

    public void changePassword(PasswordChangeDto passwordDto) {
        User authenticatedUser = getAuthenticatedUser();

        if (!passwordEncoder.matches(passwordDto.getCurrentPassword(), authenticatedUser.getPassword())) {
            throw new BadCredentialsException("Current password is incorrect");
        }

        authenticatedUser.setPassword(passwordEncoder.encode(passwordDto.getNewPassword()));
        userRepository.save(authenticatedUser);
    }
}

