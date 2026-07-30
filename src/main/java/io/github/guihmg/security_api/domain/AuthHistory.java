package io.github.guihmg.security_api.domain;

import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

@Entity
@Table(name = "auth_history")
public class AuthHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(nullable = false, length = 150)
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(name = "event_type", nullable = false, length = 30)
    private AuthEventType eventType;

    @Column(name = "occurred_at", nullable = false, updatable = false)
    private Instant occurredAt;

    protected AuthHistory() {
        // Construtor exigido pelo JPA.
    }

    public AuthHistory(
            User user,
            String email,
            AuthEventType eventType
    ) {
        this.user = user;
        this.email = email;
        this.eventType = eventType;
    }

    @PrePersist
    private void beforeInsert() {
        this.occurredAt = Instant.now();
    }

    public Long getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public String getEmail() {
        return email;
    }

    public AuthEventType getEventType() {
        return eventType;
    }

    public Instant getOccurredAt() {
        return occurredAt;
    }
}