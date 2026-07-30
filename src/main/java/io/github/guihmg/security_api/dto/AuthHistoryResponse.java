package io.github.guihmg.security_api.dto;

import java.time.Instant;

import io.github.guihmg.security_api.domain.AuthHistory;

public record AuthHistoryResponse(
        String eventType,
        Instant occurredAt
) {

    public static AuthHistoryResponse from(AuthHistory history) {
        return new AuthHistoryResponse(
                history.getEventType().name(),
                history.getOccurredAt()
        );
    }
}