package io.github.guihmg.security_api.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import io.github.guihmg.security_api.domain.AuthHistory;

public interface AuthHistoryRepository
        extends JpaRepository<AuthHistory, Long> {

    List<AuthHistory> findByEmailOrderByOccurredAtDesc(String email);
}