package io.github.guihmg.security_api.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.github.guihmg.security_api.domain.User;
import io.github.guihmg.security_api.dto.CurrentUserResponse;
import io.github.guihmg.security_api.dto.LoginRequest;
import io.github.guihmg.security_api.dto.LoginResponse;
import io.github.guihmg.security_api.security.JwtService;
import io.github.guihmg.security_api.service.AuthService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final JwtService jwtService;

    public AuthController(
            AuthService authService,
            JwtService jwtService
    ) {
        this.authService = authService;
        this.jwtService = jwtService;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(
            @Valid @RequestBody LoginRequest request
    ) {
        User authenticatedUser = authService.authenticate(
                request.email(),
                request.password()
        );

        String token = jwtService.generateToken(authenticatedUser);

        LoginResponse response = LoginResponse.from(
                authenticatedUser,
                token
        );

        return ResponseEntity.ok(response);
    }

    @GetMapping("/me")
    public ResponseEntity<CurrentUserResponse> me(
            @AuthenticationPrincipal Jwt jwt
    ) {
        return ResponseEntity.ok(
                CurrentUserResponse.from(jwt)
        );
    }
}