package io.github.guihmg.security_api.controller;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import io.github.guihmg.security_api.domain.User;
import io.github.guihmg.security_api.exception.GlobalExceptionHandler;
import io.github.guihmg.security_api.exception.InvalidCredentialsException;
import io.github.guihmg.security_api.service.AuthService;

class AuthControllerTest {

    private AuthService authService;
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        authService = mock(AuthService.class);

        mockMvc = MockMvcBuilders
                .standaloneSetup(new AuthController(authService))
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void shouldAuthenticateUser() throws Exception {
        String email = "guilhermeservh@gmail.com";
        String password = "12345678";

        User authenticatedUser = new User(
                "Guilherme Gomes",
                email,
                "encrypted-password"
        );

        when(authService.authenticate(email, password))
                .thenReturn(authenticatedUser);

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
                .andExpect(jsonPath("$.name")
                        .value("Guilherme Gomes"))
                .andExpect(jsonPath("$.email")
                        .value(email))
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
    }
}