package com.prcatica01.empleado.auth.domain;

import com.prcatica01.empleado.empleado.domain.EmpleadoId;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;

@Entity
@Table(name = "auth_user")
public class AuthUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "username", nullable = false, length = 100)
    private String username;

    @Column(name = "password_hash", nullable = false, length = 255)
    private String passwordHash;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false, length = 20)
    private UserRole role;

    @Column(name = "is_active", nullable = false)
    private boolean active;

    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "clavePrefijo", column = @Column(name = "empleado_clave_prefijo", length = 3)),
        @AttributeOverride(name = "claveNumero", column = @Column(name = "empleado_clave_numero"))
    })
    private EmpleadoId empleadoId;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    protected AuthUser() {
    }

    public AuthUser(
        String username,
        String passwordHash,
        UserRole role,
        boolean active,
        EmpleadoId empleadoId,
        Instant updatedAt
    ) {
        this.username = username;
        this.passwordHash = passwordHash;
        this.role = role;
        this.active = active;
        this.empleadoId = empleadoId;
        this.updatedAt = updatedAt;
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

    public UserRole getRole() {
        return role;
    }

    public boolean isActive() {
        return active;
    }

    public EmpleadoId getEmpleadoId() {
        return empleadoId;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void applyRole(UserRole newRole, Instant now) {
        this.role = newRole;
        this.updatedAt = now;
    }

    public void applyPasswordHash(String newPasswordHash, Instant now) {
        this.passwordHash = newPasswordHash;
        this.updatedAt = now;
    }

    public void setActive(boolean active, Instant now) {
        this.active = active;
        this.updatedAt = now;
    }
}
