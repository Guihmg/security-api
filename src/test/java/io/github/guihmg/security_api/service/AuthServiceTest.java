package io.github.guihmg.security_api.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;

import io.github.guihmg.security_api.domain.User;
import io.github.guihmg.security_api.repository.UserRepository;

class AuthServiceTest {

    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;
    private AuthService authService;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        passwordEncoder = mock(PasswordEncoder.class);

        authService = new AuthService(
                userRepository,
                passwordEncoder
        );
    }

    @Test
    void shouldAuthenticateUserWithValidCredentials() {
        String email = "guilhermeservh@gmail.com";
        String rawPassword = "12345678";
        String passwordHash = "encrypted-password";

        User user = new User(
                "Guilherme Gomes",
                email,
                passwordHash
        );

        when(userRepository.findByEmail(email))
                .thenReturn(Optional.of(user));

        when(passwordEncoder.matches(rawPassword, passwordHash))
                .thenReturn(true);

        User authenticatedUser = authService.authenticate(
                email,
                rawPassword
        );

        assertEquals(user, authenticatedUser);
    }

    @Test
    void shouldRejectInvalidPassword() {
        String email = "guilhermeservh@gmail.com";
        String rawPassword = "senha-errada";
        String passwordHash = "encrypted-password";

        User user = new User(
                "Guilherme Gomes",
                email,
                passwordHash
        );

        when(userRepository.findByEmail(email))
                .thenReturn(Optional.of(user));

        when(passwordEncoder.matches(rawPassword, passwordHash))
                .thenReturn(false);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> authService.authenticate(email, rawPassword)
        );

        assertEquals(
                "E-mail ou senha inválidos.",
                exception.getMessage()
        );
    }
}