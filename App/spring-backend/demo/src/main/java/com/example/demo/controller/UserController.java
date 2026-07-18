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
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final UserMapper userMapper;

    @PostMapping("/addUser")
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

    @PutMapping(value = "{username}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public User updateUser(@PathVariable String username, @Valid @ModelAttribute UserUpdateDto updateDto) {
        return userService.updateUser(username, updateDto);
    }
}
