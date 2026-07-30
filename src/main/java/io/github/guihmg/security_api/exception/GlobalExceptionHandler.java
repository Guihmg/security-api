package io.github.guihmg.security_api.exception;

import java.time.Instant;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiError> handleIllegalArgumentException(
            IllegalArgumentException exception
    ) {
        return createResponse(
                HttpStatus.CONFLICT,
                exception.getMessage()
        );
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<ApiError> handleInvalidCredentials(
            InvalidCredentialsException exception
    ) {
        return createResponse(
                HttpStatus.UNAUTHORIZED,
                exception.getMessage()
        );
    }

    private ResponseEntity<ApiError> createResponse(
            HttpStatus status,
            String message
    ) {
        ApiError error = new ApiError(
                Instant.now(),
                status.value(),
                message
        );

        return ResponseEntity
                .status(status)
                .body(error);
    }
}