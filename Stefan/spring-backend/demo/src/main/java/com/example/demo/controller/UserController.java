package com.example.demo.controller;

import com.example.demo.dto.user.UserCreateDto;
import com.example.demo.dto.user.UserResponseDto;
import com.example.demo.dto.user.UserUpdateDto;
import com.example.demo.mapper.UserMapper;
import com.example.demo.model.User;
import com.example.demo.service.UserService;
import java.util.List;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final UserMapper userMapper;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponseDto addUser(@Valid @RequestBody UserCreateDto createDto) {
        User userToSave = userMapper.toEntity(createDto);

        User savedUser = userService.addUser(userToSave);

        return userMapper.toResponseDto(savedUser);
    }

    @GetMapping("{username}")
    public User getUserByUsername(@PathVariable String username) {
        return userService.findByUsername(username);
    }

    // a soft delete under hood (sets deleted = true)
    @DeleteMapping("/{username}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable String username) {
        userService.deleteUserByUsername(username);
    }

    @GetMapping
    public List<User> listAllUsers() {
        return userService.listAllUsers();
    }

    @PutMapping("{username}")
    public User updateUser(@PathVariable String username, @Valid @RequestBody UserUpdateDto updateDto) {
        return userService.updateUser(username, updateDto);
    }
}
