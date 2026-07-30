package io.github.guihmg.security_api.exception;

import java.time.Instant;

public record ApiError(
        Instant timestamp,
        int status,
        String message
) {
}