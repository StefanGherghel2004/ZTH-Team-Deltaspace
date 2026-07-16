package com.example.demo.controllers;

import com.example.demo.model.User;
import org.springframework.web.bind.annotation.*;
import com.example.demo.repositories.UserRepository;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserRepository userRepository;

    public UserController(UserRepository userRepository){

        this.userRepository = userRepository;
    }

    @PostMapping
    public User addUser(@RequestBody User user) {

        return userRepository.save(user);
    }

    @GetMapping
    public List<User> getAllUsers(){

        return userRepository.findAll();
    }

    @PutMapping("/{id}")
    public User updateUser(@PathVariable Integer id, @RequestBody User updatedUser) {

        return userRepository.findById(id).map(user -> {
            user.setUsername(updatedUser.getUsername());
            user.setEmail(updatedUser.getEmail());
            user.setDateOfBirth(updatedUser.getDateOfBirth());
            return userRepository.save(user);
        }).orElseThrow(() -> new RuntimeException("User not found with id: " + id));
    }
}
