package io.github.guihmg.security_api.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import io.github.guihmg.security_api.domain.AuthEventType;
import io.github.guihmg.security_api.domain.AuthHistory;
import io.github.guihmg.security_api.domain.User;
import io.github.guihmg.security_api.repository.AuthHistoryRepository;

@Service
public class AuthHistoryService {

    private final AuthHistoryRepository authHistoryRepository;

    public AuthHistoryService(
            AuthHistoryRepository authHistoryRepository
    ) {
        this.authHistoryRepository = authHistoryRepository;
    }

    @Transactional
    public AuthHistory recordRegistration(User user) {
        return save(
                user,
                user.getEmail(),
                AuthEventType.USER_REGISTERED
        );
    }

    @Transactional
    public AuthHistory recordLoginSuccess(User user) {
        return save(
                user,
                user.getEmail(),
                AuthEventType.LOGIN_SUCCESS
        );
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public AuthHistory recordLoginFailure(String email) {
        return save(
                null,
                email,
                AuthEventType.LOGIN_FAILURE
        );
    }

    @Transactional(readOnly = true)
    public List<AuthHistory> findByEmail(String email) {
        return authHistoryRepository
                .findByEmailOrderByOccurredAtDesc(email);
    }

    private AuthHistory save(
            User user,
            String email,
            AuthEventType eventType
    ) {
        AuthHistory history = new AuthHistory(
                user,
                email,
                eventType
        );

        return authHistoryRepository.save(history);
    }
}