package io.github.guihmg.security_api.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;

import io.github.guihmg.security_api.domain.User;

class JwtServiceTest {

    private JwtEncoder jwtEncoder;
    private JwtService jwtService;

    @BeforeEach
    void setUp() {
        jwtEncoder = mock(JwtEncoder.class);
        jwtService = new JwtService(jwtEncoder);
    }

    @Test
    void shouldGenerateTokenForAuthenticatedUser() {
        String name = "Guilherme Gomes";
        String email = "guilhermeservh@gmail.com";

        User user = new User(
                name,
                email,
                "encrypted-password"
        );

        Instant issuedAt = Instant.now();

        Jwt encodedJwt = new Jwt(
                "generated-jwt-token",
                issuedAt,
                issuedAt.plusSeconds(3600),
                Map.of("alg", "HS256"),
                Map.of(
                        "sub", email,
                        "name", name
                )
        );

        when(jwtEncoder.encode(any(JwtEncoderParameters.class)))
                .thenReturn(encodedJwt);

        String token = jwtService.generateToken(user);

        assertEquals("generated-jwt-token", token);

        ArgumentCaptor<JwtEncoderParameters> parametersCaptor =
                ArgumentCaptor.forClass(JwtEncoderParameters.class);

        verify(jwtEncoder).encode(parametersCaptor.capture());

        JwtEncoderParameters parameters = parametersCaptor.getValue();

        assertEquals(
                email,
                parameters.getClaims().getSubject()
        );

        assertEquals(
                name,
                parameters.getClaims()
                        .getClaims()
                        .get("name")
        );
    }
}