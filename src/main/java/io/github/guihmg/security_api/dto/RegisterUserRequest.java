package io.github.guihmg.security_api.dto;

public record RegisterUserRequest(
        String name,
        String email,
        String password
) {
}