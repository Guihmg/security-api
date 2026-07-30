package io.github.guihmg.security_api.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import io.github.guihmg.security_api.domain.AuthEventType;
import io.github.guihmg.security_api.domain.AuthHistory;
import io.github.guihmg.security_api.domain.User;
import io.github.guihmg.security_api.repository.AuthHistoryRepository;

class AuthHistoryServiceTest {

    private AuthHistoryRepository authHistoryRepository;
    private AuthHistoryService authHistoryService;

    @BeforeEach
    void setUp() {
        authHistoryRepository = mock(AuthHistoryRepository.class);

        authHistoryService = new AuthHistoryService(
                authHistoryRepository
        );

        when(authHistoryRepository.save(any(AuthHistory.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
    }

    @Test
    void shouldRecordUserRegistration() {
        User user = createUser();

        AuthHistory history =
                authHistoryService.recordRegistration(user);

        assertEquals(user, history.getUser());
        assertEquals(user.getEmail(), history.getEmail());
        assertEquals(
                AuthEventType.USER_REGISTERED,
                history.getEventType()
        );
    }

    @Test
    void shouldRecordSuccessfulLogin() {
        User user = createUser();

        AuthHistory history =
                authHistoryService.recordLoginSuccess(user);

        assertEquals(user, history.getUser());
        assertEquals(user.getEmail(), history.getEmail());
        assertEquals(
                AuthEventType.LOGIN_SUCCESS,
                history.getEventType()
        );
    }

    @Test
    void shouldRecordFailedLogin() {
        String email = "tentativa@gmail.com";

        authHistoryService.recordLoginFailure(email);

        ArgumentCaptor<AuthHistory> captor =
                ArgumentCaptor.forClass(AuthHistory.class);

        verify(authHistoryRepository).save(captor.capture());

        AuthHistory history = captor.getValue();

        assertNull(history.getUser());
        assertEquals(email, history.getEmail());
        assertEquals(
                AuthEventType.LOGIN_FAILURE,
                history.getEventType()
        );
    }

    private User createUser() {
        return new User(
                "Guilherme Gomes",
                "guilhermeservh@gmail.com",
                "encrypted-password"
        );
    }
}