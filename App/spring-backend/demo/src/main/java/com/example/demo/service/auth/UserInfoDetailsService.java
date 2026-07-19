package com.example.demo.service.auth;

import com.example.demo.exception.notfound.UserNotFoundException;
import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserInfoDetailsService implements UserDetailsService {

    private final UserRepository repository;

    @Override
    public UserDetails loadUserByUsername(String identifier) {

        User user = repository.findByUsernameOrEmail(identifier, identifier)
                .orElseThrow(() -> new UserNotFoundException("User not found with username or email: " + identifier));

        return new UserInfoDetails(user);
    }
}