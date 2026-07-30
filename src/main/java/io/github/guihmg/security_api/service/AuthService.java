package io.github.guihmg.security_api.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import io.github.guihmg.security_api.domain.User;
import io.github.guihmg.security_api.repository.UserRepository;

@Service
public class AuthService {

    private static final String INVALID_CREDENTIALS =
            "E-mail ou senha inválidos.";

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User authenticate(String email, String rawPassword) {
        User user = userRepository
                .findByEmail(email)
                .orElseThrow(() ->
                        new IllegalArgumentException(INVALID_CREDENTIALS)
                );

        boolean passwordMatches = passwordEncoder.matches(
                rawPassword,
                user.getPasswordHash()
        );

        if (!passwordMatches) {
            throw new IllegalArgumentException(INVALID_CREDENTIALS);
        }

        return user;
    }
}