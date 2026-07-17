package com.example.demo.service;

import com.example.demo.dto.user.UserUpdateDto;
import com.example.demo.exception.AccessDeniedException;
import com.example.demo.exception.UserNotFoundException;
import com.example.demo.exception.UserTooYoungException;
import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import java.util.Optional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;


@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private static final int MIN_AGE = 13;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;


    public User addUser(User user) {
        user.setId(null);

        if (Period.between(user.getDateOfBirth(), LocalDate.now()).getYears() < MIN_AGE) {
            throw new UserTooYoungException(MIN_AGE);
        }

        String hashedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(hashedPassword);
        return userRepository.save(user);
    }

    public List<User> listAllUsers() {
        return userRepository.findAll();
    }

    public User updateUser(String username, UserUpdateDto updateDto) {

        User user = getAuthenticatedUser();

        if (!user.getUsername().equals(username)) {
            throw new AccessDeniedException("This account is not yours.");
        }

        if (updateDto.getPassword() != null && !updateDto.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(updateDto.getPassword()));
        }

        if (updateDto.getDateOfBirth() != null) {
            user.setDateOfBirth(updateDto.getDateOfBirth());
        }

        return userRepository.save(user);
    }

    public User findByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User not found with username: " + username));
    }

    public void deleteUserByUsername(String username) {
        User user = getAuthenticatedUser();

        if (!user.getUsername().equals(username)) {
            throw new AccessDeniedException("This account is not yours.");
        }

        userRepository.delete(user);
    }

    public User getAuthenticatedUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return findByUsername(username);
    }
}

