package io.github.guihmg.security_api.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import io.github.guihmg.security_api.domain.User;

public interface UserRepository extends JpaRepository<User, Long> {

    boolean existsByEmail(String email);

    Optional<User> findByEmail(String email);
}