package com.example.demo.dto.user;

import com.example.demo.model.User;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserResponseDto {

    private String username;
    private String email;
    private String displayName;
    private String avatarUrl;

    public static UserResponseDto fromEntity(User user) {
        if (user == null) {
            return null;
        }

        return UserResponseDto.builder()
                .username(user.getUsername())
                .email(user.getEmail())
                .displayName(user.getDisplayName())
                .avatarUrl(user.getAvatarUrl())
                .build();
    }
}