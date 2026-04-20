package com.prcatica01.empleado.auth.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public final class AuthDtos {

    private AuthDtos() {
    }

    public record LoginRequest(
        @NotBlank(message = "El usuario es obligatorio")
        @Schema(example = "admin")
        String usuario,
        @NotBlank(message = "La contrasena es obligatoria")
        @Schema(example = "admin123")
        String contrasena
    ) {
    }

    public record LoginResponse(
        @Schema(example = "9bbf7ad66f8b4a67b5e65ad78f1f5f8193f9f4f2679d4a96a7f4f2e1ab8899c1")
        String token,
        @Schema(example = "Bearer")
        String tokenType,
        @Schema(example = "1800")
        long expiresInSeconds,
        @Schema(example = "admin")
        String usuario,
        @Schema(example = "ADMIN")
        String role
    ) {
    }

    public record AuthConfigResponse(
        String usuario,
        int sessionTimeoutMinutes,
        int maxFailedAttempts,
        int failedAttemptWindowMinutes,
        int lockMinutes
    ) {
    }
}
