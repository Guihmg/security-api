package io.github.guihmg.security_api.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import io.github.guihmg.security_api.domain.User;

public interface UserRepository extends JpaRepository<User, Long> {

    boolean existsByEmail(String email);
}