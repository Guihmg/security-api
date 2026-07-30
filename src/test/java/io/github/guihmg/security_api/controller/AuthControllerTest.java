package io.github.guihmg.security_api.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.Instant;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import io.github.guihmg.security_api.domain.User;
import io.github.guihmg.security_api.dto.CurrentUserResponse;
import io.github.guihmg.security_api.exception.GlobalExceptionHandler;
import io.github.guihmg.security_api.exception.InvalidCredentialsException;
import io.github.guihmg.security_api.security.JwtService;
import io.github.guihmg.security_api.service.AuthService;

class AuthControllerTest {

    private AuthService authService;
    private JwtService jwtService;
    private AuthController authController;
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        authService = mock(AuthService.class);
        jwtService = mock(JwtService.class);

        authController = new AuthController(
                authService,
                jwtService
        );

        mockMvc = MockMvcBuilders
                .standaloneSetup(authController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void shouldAuthenticateUserAndReturnToken() throws Exception {
        String email = "guilhermeservh@gmail.com";
        String password = "12345678";
        String token = "generated-jwt-token";

        User authenticatedUser = new User(
                "Guilherme Gomes",
                email,
                "encrypted-password"
        );

        when(authService.authenticate(email, password))
                .thenReturn(authenticatedUser);

        when(jwtService.generateToken(authenticatedUser))
                .thenReturn(token);

        String requestBody = """
                {
                    "email": "guilhermeservh@gmail.com",
                    "password": "12345678"
                }
                """;

        mockMvc.perform(
                        post("/api/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value(token))
                .andExpect(jsonPath("$.tokenType").value("Bearer"))
                .andExpect(jsonPath("$.name")
                        .value("Guilherme Gomes"))
                .andExpect(jsonPath("$.email").value(email))
                .andExpect(jsonPath("$.password").doesNotExist())
                .andExpect(jsonPath("$.passwordHash").doesNotExist());
    }

    @Test
    void shouldReturnUnauthorizedForInvalidCredentials()
            throws Exception {

        String email = "guilhermeservh@gmail.com";
        String password = "senha-errada";

        when(authService.authenticate(email, password))
                .thenThrow(new InvalidCredentialsException(
                        "E-mail ou senha inválidos."
                ));

        String requestBody = """
                {
                    "email": "guilhermeservh@gmail.com",
                    "password": "senha-errada"
                }
                """;

        mockMvc.perform(
                        post("/api/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody)
                )
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.message")
                        .value("E-mail ou senha inválidos."));

        verifyNoInteractions(jwtService);
    }

    @Test
    void shouldReturnCurrentAuthenticatedUser() {
        String name = "Guilherme Gomes";
        String email = "guilhermeservh@gmail.com";

        Instant now = Instant.now();

        Jwt jwt = new Jwt(
                "generated-jwt-token",
                now,
                now.plusSeconds(3600),
                Map.of("alg", "HS256"),
                Map.of(
                        "sub", email,
                        "name", name
                )
        );

        ResponseEntity<CurrentUserResponse> response =
                authController.me(jwt);

        assertEquals(HttpStatus.OK, response.getStatusCode());

        CurrentUserResponse body = response.getBody();

        assertNotNull(body);
        assertEquals(name, body.name());
        assertEquals(email, body.email());
    }
}