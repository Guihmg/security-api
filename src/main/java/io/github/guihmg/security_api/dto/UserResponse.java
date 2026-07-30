package io.github.guihmg.security_api.dto;

import io.github.guihmg.security_api.domain.User;

public record UserResponse(
        String name,
        String email
) {

    public static UserResponse from(User user) {
        return new UserResponse(
                user.getName(),
                user.getEmail()
        );
    }
}