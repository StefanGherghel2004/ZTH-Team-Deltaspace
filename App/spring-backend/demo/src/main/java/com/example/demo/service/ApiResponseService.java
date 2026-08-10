package com.example.demo.service;

import com.example.demo.dto.auth.AuthResponseDto;
import com.example.demo.dto.post.response.PostResponseDto;
import com.example.demo.model.Post;
import com.example.demo.model.User;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ApiResponseService {
    PostService postService;

    public AuthResponseDto getAuthenticationResponse(User user, String jwtToken) {

        return AuthResponseDto.builder()
                .accessToken(jwtToken)
                .user(AuthResponseDto.UserDto.builder()
                        .username(user.getUsername())
                        .email(user.getEmail())
                        .build())
                .build();
    }

    public List<PostResponseDto> getPostListResponse(List<Post> posts) {
        return posts.stream().map(postService::getEnrichedPostDto).toList();
    }
}
