package com.prcatica01.empleado.auth.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Duration;
import java.time.Instant;

@Entity
@Table(name = "auth_session")
public class AuthSession {

    @Id
    @Column(name = "token", nullable = false, length = 128)
    private String token;

    @Column(name = "username", nullable = false, length = 100)
    private String username;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "is_active", nullable = false)
    private boolean active;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "last_activity_at", nullable = false)
    private Instant lastActivityAt;

    @Column(name = "invalidated_at")
    private Instant invalidatedAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "invalidation_reason", length = 40)
    private AuthInvalidationReason invalidationReason;

    protected AuthSession() {
    }

    public AuthSession(String token, String username, Instant now) {
        this(token, username, null, now);
    }

    public AuthSession(String token, String username, Long userId, Instant now) {
        this.token = token;
        this.username = username;
        this.userId = userId;
        this.active = true;
        this.createdAt = now;
        this.lastActivityAt = now;
    }

    public boolean isExpired(Instant now, Duration inactivityTimeout) {
        return lastActivityAt.plus(inactivityTimeout).isBefore(now);
    }

    public void touch(Instant now) {
        this.lastActivityAt = now;
    }

    public void invalidate(AuthInvalidationReason reason, Instant now) {
        this.active = false;
        this.invalidationReason = reason;
        this.invalidatedAt = now;
        this.lastActivityAt = now;
    }

    public String getToken() {
        return token;
    }

    public String getUsername() {
        return username;
    }

    public Long getUserId() {
        return userId;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public boolean isActive() {
        return active;
    }
}
