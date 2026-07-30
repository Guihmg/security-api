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
import io.github.guihmg.security_api.service.UserService;

class UserControllerTest {

    private UserService userService;
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        userService = mock(UserService.class);

        mockMvc = MockMvcBuilders
                .standaloneSetup(new UserController(userService))
                .build();
    }

    @Test
    void shouldRegisterUser() throws Exception {
        String name = "Guilherme Gomes";
        String email = "guilhermeservh@gmail.com";
        String password = "12345678";

        User registeredUser = new User(
                name,
                email,
                "encrypted-password"
        );

        when(userService.register(name, email, password))
                .thenReturn(registeredUser);

        String requestBody = """
                {
                    "name": "Guilherme Gomes",
                    "email": "guilhermeservh@gmail.com",
                    "password": "12345678"
                }
                """;

        mockMvc.perform(
                        post("/api/users")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody)
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value(name))
                .andExpect(jsonPath("$.email").value(email))
                .andExpect(jsonPath("$.password").doesNotExist())
                .andExpect(jsonPath("$.passwordHash").doesNotExist());
    }
}