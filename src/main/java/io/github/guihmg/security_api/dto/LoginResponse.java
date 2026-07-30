package io.github.guihmg.security_api.dto;

import io.github.guihmg.security_api.domain.User;

public record LoginResponse(
        String name,
        String email
) {

    public static LoginResponse from(User user) {
        return new LoginResponse(
                user.getName(),
                user.getEmail()
        );
    }
}