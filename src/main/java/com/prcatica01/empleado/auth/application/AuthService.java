package com.prcatica01.empleado.auth.application;

import com.prcatica01.empleado.auth.api.dto.AuthDtos.AuthConfigResponse;
import com.prcatica01.empleado.auth.api.dto.AuthDtos.LoginRequest;
import com.prcatica01.empleado.auth.api.dto.AuthDtos.LoginResponse;
import com.prcatica01.empleado.auth.domain.AuthAuditEvent;
import com.prcatica01.empleado.auth.domain.AuthEventType;
import com.prcatica01.empleado.auth.domain.AuthInvalidationReason;
import com.prcatica01.empleado.auth.domain.AuthSession;
import com.prcatica01.empleado.auth.domain.AuthUser;
import com.prcatica01.empleado.auth.domain.UserRole;
import com.prcatica01.empleado.auth.infrastructure.AuthAuditEventRepository;
import com.prcatica01.empleado.auth.infrastructure.AuthSessionRepository;
import com.prcatica01.empleado.auth.infrastructure.AuthUserRepository;
import com.prcatica01.empleado.shared.domain.DomainExceptions.AuthenticationFailedException;
import com.prcatica01.empleado.shared.domain.DomainExceptions.SessionExpiredException;
import com.prcatica01.empleado.shared.domain.DomainExceptions.UnauthorizedException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

@Service
public class AuthService {

    private static final String GENERIC_AUTH_ERROR = "Credenciales invalidas o sesion no valida";

    private final AuthUserRepository authUserRepository;
    private final AuthSessionRepository sessionRepository;
    private final AuthAuditEventRepository auditEventRepository;
    private final PasswordEncoder passwordEncoder;
    private final int sessionTimeoutMinutes;
    private final int maxFailedAttempts;
    private final int failedAttemptWindowMinutes;
    private final int lockMinutes;

    public AuthService(
        AuthUserRepository authUserRepository,
        AuthSessionRepository sessionRepository,
        AuthAuditEventRepository auditEventRepository,
        PasswordEncoder passwordEncoder,
        @Value("${auth.session-timeout-minutes:30}") int sessionTimeoutMinutes,
        @Value("${auth.max-failed-attempts:5}") int maxFailedAttempts,
        @Value("${auth.failed-attempt-window-minutes:15}") int failedAttemptWindowMinutes,
        @Value("${auth.lock-minutes:15}") int lockMinutes
    ) {
        this.authUserRepository = authUserRepository;
        this.sessionRepository = sessionRepository;
        this.auditEventRepository = auditEventRepository;
        this.passwordEncoder = passwordEncoder;
        this.sessionTimeoutMinutes = sessionTimeoutMinutes;
        this.maxFailedAttempts = maxFailedAttempts;
        this.failedAttemptWindowMinutes = failedAttemptWindowMinutes;
        this.lockMinutes = lockMinutes;
    }

    @Transactional
    public LoginResponse login(LoginRequest request) {
        Instant now = Instant.now();
        String usernameInput = request.usuario() == null ? "" : request.usuario().trim();
        String passwordInput = request.contrasena() == null ? "" : request.contrasena();
        AuthUser user = authUserRepository.findByUsernameIgnoreCaseAndActiveTrue(usernameInput)
            .orElse(null);

        if (user == null || !passwordEncoder.matches(passwordInput, user.getPasswordHash())) {
            auditEvent(usernameInput, AuthEventType.LOGIN_FAILURE, "Credenciales invalidas");
            throw new AuthenticationFailedException(GENERIC_AUTH_ERROR);
        }

        String token = buildToken();
        AuthSession session = new AuthSession(token, user.getUsername(), user.getId(), now);
        sessionRepository.save(session);
        auditEvent(user.getUsername(), AuthEventType.LOGIN_SUCCESS, "Login exitoso");

        return new LoginResponse(
            token,
            "Bearer",
            sessionTimeout().toSeconds(),
            user.getUsername(),
            user.getRole().name()
        );
    }

    @Transactional
    public void logout(String token) {
        AuthSession session = sessionRepository.findByTokenAndActiveTrue(token)
            .orElseThrow(() -> new UnauthorizedException(GENERIC_AUTH_ERROR));

        Instant now = Instant.now();
        session.invalidate(AuthInvalidationReason.LOGOUT, now);
        sessionRepository.save(session);
        auditEvent(session.getUsername(), AuthEventType.LOGOUT, "Logout exitoso");
    }

    @Transactional
    public AuthSession validateAndTouchSession(String token) {
        AuthSession session = sessionRepository.findByTokenAndActiveTrue(token)
            .orElseThrow(() -> new UnauthorizedException(GENERIC_AUTH_ERROR));

        AuthUser user = resolveSessionUser(session);
        Instant now = Instant.now();

        if (user.getUpdatedAt() != null && session.getCreatedAt() != null && user.getUpdatedAt().isAfter(session.getCreatedAt())) {
            session.invalidate(AuthInvalidationReason.ROLE_CHANGED, now);
            sessionRepository.save(session);
            auditEvent(session.getUsername(), AuthEventType.ROLE_CHANGED, "Sesion invalidada por cambio de rol");
            throw new UnauthorizedException(GENERIC_AUTH_ERROR);
        }

        if (session.isExpired(now, sessionTimeout())) {
            session.invalidate(AuthInvalidationReason.EXPIRED, now);
            sessionRepository.save(session);
            auditEvent(session.getUsername(), AuthEventType.SESSION_EXPIRED, "Sesion expirada por inactividad");
            throw new SessionExpiredException("Sesion expirada por inactividad");
        }

        session.touch(now);
        sessionRepository.save(session);
        return session;
    }

    @Transactional(readOnly = true)
    public String resolveRoleForSession(AuthSession session) {
        return resolveSessionUser(session).getRole().name();
    }

    @Transactional(readOnly = true)
    public AuthConfigResponse getAuthConfig() {
        String username = authUserRepository.findTopByRoleAndActiveTrueOrderByIdAsc(UserRole.ADMIN)
            .or(() -> authUserRepository.findTopByActiveTrueOrderByIdAsc())
            .map(AuthUser::getUsername)
            .orElse("N/A");

        return new AuthConfigResponse(
            username,
            sessionTimeoutMinutes,
            maxFailedAttempts,
            failedAttemptWindowMinutes,
            lockMinutes
        );
    }

    private AuthUser resolveSessionUser(AuthSession session) {
        AuthUser user;

        if (session.getUserId() != null) {
            user = authUserRepository.findById(session.getUserId()).orElse(null);
        } else {
            user = authUserRepository.findByUsernameIgnoreCase(session.getUsername()).orElse(null);
        }

        if (user == null || !user.isActive()) {
            throw new UnauthorizedException(GENERIC_AUTH_ERROR);
        }

        return user;
    }

    private Duration sessionTimeout() {
        return Duration.ofMinutes(sessionTimeoutMinutes);
    }

    private String buildToken() {
        return UUID.randomUUID().toString().replace("-", "") + UUID.randomUUID().toString().replace("-", "");
    }

    private void auditEvent(String username, AuthEventType eventType, String resultMessage) {
        String safeUsername = username == null || username.isBlank() ? "unknown" : username;
        auditEventRepository.save(new AuthAuditEvent(safeUsername, eventType, resultMessage, Instant.now()));
    }
}
