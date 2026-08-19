package com.example.demo.service;

import com.example.demo.dto.user.PasswordChangeRequestDto;
import com.example.demo.dto.user.UserCreateDto;
import com.example.demo.dto.user.UserDeleteDto;
import com.example.demo.exception.AccessDeniedException;
import com.example.demo.dto.user.UserUpdateDto;
import com.example.demo.exception.DuplicateUserInformationException;
import com.example.demo.exception.IdenticalPasswordException;
import com.example.demo.exception.notfound.UserNotFoundException;
import com.example.demo.exception.UserTooYoungException;
import com.example.demo.logger.Logger;
import com.example.demo.mapper.UserMapper;
import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.Period;

/**
 * Service class for managing user-related operations.
 * Handles user creation, updates, soft deletion, password changes, and authentication context retrieval.
 */
@Service
@RequiredArgsConstructor
@Transactional
public class UserService {

    private static final int MIN_AGE = 13;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    /**
     * Registers a new user in the system.
     *
     * @param userCreateDto the data transfer object containing new user details
     * @return the saved {@link User} entity
     * @throws UserTooYoungException if the user is below the minimum required age
     * @throws DuplicateUserInformationException if the username or email is already taken
     */
    public User addUser(UserCreateDto userCreateDto) {
        User user = userMapper.toEntity(userCreateDto);
        user.setId(null);
        validateAge(user.getDateOfBirth());

        if(userRepository.findByUsername(user.getUsername()).isPresent()) {
            Logger.warning("A user with this username already exists, please choose another username.");
            throw new DuplicateUserInformationException("A user with this username already exists, please choose another username.");
        }
        if(userRepository.findByEmail(user.getEmail()).isPresent()) {
            Logger.warning("A user with this email already exists.");
            throw new DuplicateUserInformationException("A user with this email already exists.");
        }

        Logger.info("Added %s as a new user", user.getUsername());

        String hashedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(hashedPassword);
        return userRepository.save(user);
    }

    /**
     * Updates the currently authenticated user's profile information.
     *
     * @param updateDto the data transfer object containing the updated fields
     * @return the updated {@link User} entity
     */
    public User updateAuthenticatedUser(UserUpdateDto updateDto) {
        User user = getAuthenticatedUser();
        Logger.info("Updated info of user %s", user.getUsername());
        return applyUpdatesAndSave(user, updateDto);
    }

    /**
     * Helper method to apply updates to a user entity and save it.
     *
     * @param user the user entity to update
     * @param updateDto the data transfer object containing the new values
     * @return the saved {@link User} entity
     */
    private User applyUpdatesAndSave(User user, UserUpdateDto updateDto) {
        if (updateDto.getDisplayName() == null)
            user.setDisplayName(user.getUsername());
        else
            user.setDisplayName(updateDto.getDisplayName());

        if(updateDto.getAvatarUrl() == null)
            user.setAvatarUrl("");
        else
            user.setAvatarUrl(updateDto.getAvatarUrl());

        return userRepository.save(user);
    }

    /**
     * Finds a user by their username.
     *
     * @param username the username to search for
     * @return the found {@link User} entity
     * @throws UserNotFoundException if no user is found with the given username
     * @throws AccessDeniedException if the found user account is marked as deleted
     */
    @Transactional(readOnly = true)
    public User findByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User not found with username: " + username));
        if (user.isDeleted()) {
            Logger.warning("The user account you are trying to access is deleted.");
            throw new AccessDeniedException("The user account you are trying to access is deleted.");
        }

        return user;
    }

    /**
     * Soft deletes the currently authenticated user's account.
     *
     * @param userDeleteDto the data transfer object containing the user's password for verification
     * @throws AccessDeniedException if the provided password does not match the current password
     */
    public void deleteAuthenticatedUser(UserDeleteDto userDeleteDto) {
        User user = getAuthenticatedUser();
        if (!passwordEncoder.matches(userDeleteDto.getPassword(),user.getPassword())) {
            Logger.warning("Current user is trying to delete another user's account.");
            throw new AccessDeniedException("Password is incorrect");
        }

        user.setDeleted(true);
        userRepository.save(user);
    }

    /**
     * Retrieves the currently authenticated user from the security context.
     *
     * @return the authenticated {@link User} entity
     * @throws BadCredentialsException if the user is not authenticated
     */
    @Transactional(readOnly = true)
    public User getAuthenticatedUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
            Logger.warning("User is not authenticated.");
            throw new BadCredentialsException("Full authentication is required.");
        }

        return findByUsername(auth.getName());
    }

    /**
     * Retrieves the username of the currently authenticated user.
     *
     * @return the username if authenticated, or {@code null} if not authenticated
     */
    public String currentUsernameOrNull() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
            return null;
        }

        return auth.getName();
    }

    /**
     * Validates that the provided date of birth meets the minimum age requirement.
     *
     * @param dateOfBirth the user's date of birth
     * @throws UserTooYoungException if the user is younger than {@link #MIN_AGE}
     */
    private void validateAge(LocalDate dateOfBirth) {
        if (dateOfBirth == null) {
            return;
        }

        if (Period.between(dateOfBirth, LocalDate.now()).getYears() < MIN_AGE) {
            throw new UserTooYoungException(MIN_AGE);
        }
    }

    /**
     * Changes the password of the currently authenticated user.
     *
     * @param passwordDto the data transfer object containing current and new passwords
     * @throws AccessDeniedException if the provided current password is incorrect
     * @throws IdenticalPasswordException if the new password is the same as the current password
     */
    public void changePassword(PasswordChangeRequestDto passwordDto) {
        User authenticatedUser = getAuthenticatedUser();

        if (!passwordEncoder.matches(passwordDto.getCurrentPassword(), authenticatedUser.getPassword())) {
            Logger.warning("Current password is incorrect");
            throw new AccessDeniedException("Current password is incorrect");
        }

        if (passwordEncoder.matches(passwordDto.getNewPassword(), authenticatedUser.getPassword())) {
            Logger.warning("New password cannot be the same as the current password.");
            throw new IdenticalPasswordException("New password cannot be the same as the current password.");
        }

        authenticatedUser.setPassword(passwordEncoder.encode(passwordDto.getNewPassword()));
        Logger.info("Password changed for current user:", authenticatedUser.getUsername());
        userRepository.save(authenticatedUser);
    }

    /**
     * Masks the user's username as [DELETED] if their account is marked as deleted.
     *
     * @param user the user entity to check
     * @return a masked clone of the user if deleted, otherwise the original user entity
     */
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