package io.github.guihmg.security_api.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.github.guihmg.security_api.domain.User;
import io.github.guihmg.security_api.dto.RegisterUserRequest;
import io.github.guihmg.security_api.dto.UserResponse;
import io.github.guihmg.security_api.service.UserService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<UserResponse> register(
            @Valid @RequestBody RegisterUserRequest request
    ) {
        User registeredUser = userService.register(
                request.name(),
                request.email(),
                request.password()
        );

        UserResponse response = UserResponse.from(registeredUser);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }
}