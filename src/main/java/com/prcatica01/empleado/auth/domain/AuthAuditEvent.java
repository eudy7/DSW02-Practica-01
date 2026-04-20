package com.prcatica01.empleado.auth.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;

@Entity
@Table(name = "auth_audit_event")
public class AuthAuditEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "username", nullable = false, length = 100)
    private String username;

    @Enumerated(EnumType.STRING)
    @Column(name = "event_type", nullable = false, length = 40)
    private AuthEventType eventType;

    @Column(name = "result_message", nullable = false, length = 255)
    private String resultMessage;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    protected AuthAuditEvent() {
    }

    public AuthAuditEvent(String username, AuthEventType eventType, String resultMessage, Instant createdAt) {
        this.username = username;
        this.eventType = eventType;
        this.resultMessage = resultMessage;
        this.createdAt = createdAt;
    }
}
