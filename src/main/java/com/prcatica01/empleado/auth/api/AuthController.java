package com.prcatica01.empleado.auth.api;

import com.prcatica01.empleado.auth.api.dto.AuthDtos.AuthConfigResponse;
import com.prcatica01.empleado.auth.api.dto.AuthDtos.LoginRequest;
import com.prcatica01.empleado.auth.api.dto.AuthDtos.LoginResponse;
import com.prcatica01.empleado.auth.application.AuthService;
import com.prcatica01.empleado.shared.domain.DomainExceptions.UnauthorizedException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    @Operation(
        summary = "Iniciar sesion",
        description = "Devuelve token para usar en Authorize (bearerAuth). Ejemplo: usuario admin y contrasena admin123.",
        security = {}
    )
    public LoginResponse login(@Valid @RequestBody LoginRequest request) {
        return authService.login(request);
    }

    @PostMapping("/logout")
    @Operation(summary = "Cerrar sesion", security = { @SecurityRequirement(name = "bearerAuth") })
    public ResponseEntity<Void> logout(HttpServletRequest request) {
        authService.logout(extractBearerToken(request));
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/config")
    public AuthConfigResponse getConfig() {
        return authService.getAuthConfig();
    }

    private String extractBearerToken(HttpServletRequest request) {
        String header = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (header == null || !header.startsWith("Bearer ")) {
            throw new UnauthorizedException("Credenciales invalidas o sesion no valida");
        }

        String token = header.substring(7).trim();
        if (token.isEmpty()) {
            throw new UnauthorizedException("Credenciales invalidas o sesion no valida");
        }

        return token;
    }
}
