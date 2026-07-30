package io.github.guihmg.security_api.exception;

import java.time.Instant;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import jakarta.servlet.http.HttpServletRequest;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger LOGGER =
            LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiError> handleIllegalArgumentException(
            IllegalArgumentException exception,
            HttpServletRequest request
    ) {
        HttpStatus status = HttpStatus.CONFLICT;

        LOGGER.warn(
                "Conflito na requisicao: method={} path={} message={}",
                request.getMethod(),
                request.getRequestURI(),
                exception.getMessage()
        );

        ApiError error = new ApiError(
                Instant.now(),
                status.value(),
                exception.getMessage()
        );

        return ResponseEntity.status(status).body(error);
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<ApiError> handleInvalidCredentialsException(
            InvalidCredentialsException exception,
            HttpServletRequest request
    ) {
        HttpStatus status = HttpStatus.UNAUTHORIZED;

        LOGGER.warn(
                "Tentativa de autenticacao recusada: method={} path={}",
                request.getMethod(),
                request.getRequestURI()
        );

        ApiError error = new ApiError(
                Instant.now(),
                status.value(),
                exception.getMessage()
        );

        return ResponseEntity.status(status).body(error);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidationException(
            MethodArgumentNotValidException exception,
            HttpServletRequest request
    ) {
        HttpStatus status = HttpStatus.BAD_REQUEST;

        String message = exception
                .getBindingResult()
                .getFieldErrors()
                .stream()
                .findFirst()
                .map(fieldError -> fieldError.getDefaultMessage())
                .orElse("Dados invalidos.");

        LOGGER.warn(
                "Requisicao invalida: method={} path={} errors={}",
                request.getMethod(),
                request.getRequestURI(),
                exception.getBindingResult().getErrorCount()
        );

        ApiError error = new ApiError(
                Instant.now(),
                status.value(),
                message
        );

        return ResponseEntity.status(status).body(error);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleUnexpectedException(
            Exception exception,
            HttpServletRequest request
    ) {
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;

        LOGGER.error(
                "Erro interno: method={} path={}",
                request.getMethod(),
                request.getRequestURI(),
                exception
        );

        ApiError error = new ApiError(
                Instant.now(),
                status.value(),
                "Erro interno do servidor."
        );

        return ResponseEntity.status(status).body(error);
    }
}