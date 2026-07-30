package io.github.guihmg.security_api.dto;

import org.springframework.security.oauth2.jwt.Jwt;

public record CurrentUserResponse(
        String name,
        String email
) {

    public static CurrentUserResponse from(Jwt jwt) {
        return new CurrentUserResponse(
                jwt.getClaimAsString("name"),
                jwt.getSubject()
        );
    }
}