package io.github.guihmg.security_api.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import io.github.guihmg.security_api.domain.User;
import io.github.guihmg.security_api.exception.InvalidCredentialsException;
import io.github.guihmg.security_api.repository.UserRepository;

@Service
public class AuthService {

    private static final String INVALID_CREDENTIALS =
            "E-mail ou senha inválidos.";

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthHistoryService authHistoryService;

    public AuthService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            AuthHistoryService authHistoryService
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authHistoryService = authHistoryService;
    }

    public User authenticate(String email, String rawPassword) {
        User user = userRepository
                .findByEmail(email)
                .orElse(null);

        if (user == null) {
            authHistoryService.recordLoginFailure(email);

            throw new InvalidCredentialsException(
                    INVALID_CREDENTIALS
            );
        }

        boolean passwordMatches = passwordEncoder.matches(
                rawPassword,
                user.getPasswordHash()
        );

        if (!passwordMatches) {
            authHistoryService.recordLoginFailure(email);

            throw new InvalidCredentialsException(
                    INVALID_CREDENTIALS
            );
        }

        authHistoryService.recordLoginSuccess(user);

        return user;
    }
}