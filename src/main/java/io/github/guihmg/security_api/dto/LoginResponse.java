package io.github.guihmg.security_api.dto;

import io.github.guihmg.security_api.domain.User;

public record LoginResponse(
        String token,
        String tokenType,
        String name,
        String email
) {

    public static LoginResponse from(User user, String token) {
        return new LoginResponse(
                token,
                "Bearer",
                user.getName(),
                user.getEmail()
        );
    }
}