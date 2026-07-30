package io.github.guihmg.security_api.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.github.guihmg.security_api.domain.User;
import io.github.guihmg.security_api.repository.UserRepository;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthHistoryService authHistoryService;

    public UserService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            AuthHistoryService authHistoryService
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authHistoryService = authHistoryService;
    }

    @Transactional
    public User register(
            String name,
            String email,
            String rawPassword
    ) {
        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException(
                    "Já existe um usuário cadastrado com esse e-mail."
            );
        }

        String passwordHash = passwordEncoder.encode(rawPassword);

        User user = new User(
                name,
                email,
                passwordHash
        );

        User savedUser = userRepository.save(user);

        authHistoryService.recordRegistration(savedUser);

        return savedUser;
    }
}