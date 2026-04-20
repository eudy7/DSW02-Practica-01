package com.prcatica01.empleado.auth.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Duration;
import java.time.Instant;

@Entity
@Table(name = "auth_credential")
public class AuthCredential {

    @Id
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "username", nullable = false, unique = true, length = 100)
    private String username;

    @Column(name = "password_hash", nullable = false, length = 255)
    private String passwordHash;

    @Column(name = "is_active", nullable = false)
    private boolean active;

    @Column(name = "failed_attempts", nullable = false)
    private int failedAttempts;

    @Column(name = "first_failed_at")
    private Instant firstFailedAt;

    @Column(name = "blocked_until")
    private Instant blockedUntil;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    protected AuthCredential() {
    }

    public AuthCredential(Long id, String username, String passwordHash, Instant now) {
        this.id = id;
        this.username = username;
        this.passwordHash = passwordHash;
        this.active = true;
        this.failedAttempts = 0;
        this.updatedAt = now;
    }

    public boolean isBlocked(Instant now) {
        return blockedUntil != null && blockedUntil.isAfter(now);
    }

    public long retryAfterSeconds(Instant now) {
        if (!isBlocked(now)) {
            return 0;
        }
        return Math.max(0, Duration.between(now, blockedUntil).getSeconds());
    }

    public boolean registerFailedAttempt(Instant now, Duration attemptWindow, int maxAttempts, Duration lockDuration) {
        if (firstFailedAt == null || firstFailedAt.plus(attemptWindow).isBefore(now)) {
            firstFailedAt = now;
            failedAttempts = 0;
        }

        failedAttempts += 1;
        updatedAt = now;

        if (failedAttempts >= maxAttempts) {
            blockedUntil = now.plus(lockDuration);
            failedAttempts = 0;
            firstFailedAt = null;
            return true;
        }

        return false;
    }

    public void resetFailures(Instant now) {
        failedAttempts = 0;
        firstFailedAt = null;
        blockedUntil = null;
        updatedAt = now;
    }

    public void syncCredential(String newUsername, String newPasswordHash, Instant now) {
        username = newUsername;
        passwordHash = newPasswordHash;
        active = true;
        resetFailures(now);
    }

    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public boolean isActive() {
        return active;
    }
}
