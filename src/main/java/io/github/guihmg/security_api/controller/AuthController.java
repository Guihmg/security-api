package io.github.guihmg.security_api.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.github.guihmg.security_api.domain.User;
import io.github.guihmg.security_api.dto.LoginRequest;
import io.github.guihmg.security_api.dto.LoginResponse;
import io.github.guihmg.security_api.service.AuthService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(
            @Valid @RequestBody LoginRequest request
    ) {
        User authenticatedUser = authService.authenticate(
                request.email(),
                request.password()
        );

        return ResponseEntity.ok(
                LoginResponse.from(authenticatedUser)
        );
    }
}