package com.prcatica01.empleado.auth.application;

import com.prcatica01.empleado.auth.domain.AuthCredential;
import com.prcatica01.empleado.auth.domain.AuthUser;
import com.prcatica01.empleado.auth.domain.UserRole;
import com.prcatica01.empleado.auth.infrastructure.AuthCredentialRepository;
import com.prcatica01.empleado.auth.infrastructure.AuthUserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Component
public class AuthCredentialInitializer {

    private static final long GLOBAL_CREDENTIAL_ID = 1L;

    private final AuthCredentialRepository credentialRepository;
    private final AuthUserRepository authUserRepository;
    private final PasswordEncoder passwordEncoder;
    private final String configuredUsername;
    private final String configuredPassword;
    private final String configuredAdminUsername;
    private final String configuredAdminPassword;
    private final String configuredEmpleadoUsername;
    private final String configuredEmpleadoPassword;

    public AuthCredentialInitializer(
        AuthCredentialRepository credentialRepository,
        AuthUserRepository authUserRepository,
        PasswordEncoder passwordEncoder,
        @Value("${auth.credential.username:${BASIC_AUTH_USER:admin}}") String configuredUsername,
        @Value("${auth.credential.password:${BASIC_AUTH_PASSWORD:admin123}}") String configuredPassword,
        @Value("${AUTH_SEED_ADMIN_USER:admin}") String configuredAdminUsername,
        @Value("${AUTH_SEED_ADMIN_PASSWORD:admin123}") String configuredAdminPassword,
        @Value("${AUTH_SEED_EMPLEADO_USER:empleado}") String configuredEmpleadoUsername,
        @Value("${AUTH_SEED_EMPLEADO_PASSWORD:empleado123}") String configuredEmpleadoPassword
    ) {
        this.credentialRepository = credentialRepository;
        this.authUserRepository = authUserRepository;
        this.passwordEncoder = passwordEncoder;
        this.configuredUsername = configuredUsername;
        this.configuredPassword = configuredPassword;
        this.configuredAdminUsername = configuredAdminUsername;
        this.configuredAdminPassword = configuredAdminPassword;
        this.configuredEmpleadoUsername = configuredEmpleadoUsername;
        this.configuredEmpleadoPassword = configuredEmpleadoPassword;
    }

    @Transactional
    @EventListener(ApplicationReadyEvent.class)
    public void syncGlobalCredential() {
        if (configuredUsername == null || configuredUsername.isBlank()) {
            throw new IllegalStateException("auth.credential.username no puede estar vacio");
        }
        if (configuredPassword == null || configuredPassword.isBlank()) {
            throw new IllegalStateException("auth.credential.password no puede estar vacio");
        }

        Instant now = Instant.now();
        AuthCredential credential = credentialRepository.findTopByOrderByIdAsc().orElse(null);

        if (credential == null) {
            credential = credentialRepository.save(
                new AuthCredential(
                    GLOBAL_CREDENTIAL_ID,
                    configuredUsername,
                    passwordEncoder.encode(configuredPassword),
                    now
                )
            );
        } else {
            boolean sameUsername = configuredUsername.equals(credential.getUsername());
            boolean samePassword = passwordEncoder.matches(configuredPassword, credential.getPasswordHash());
            if (!sameUsername || !samePassword) {
                credential.syncCredential(configuredUsername, passwordEncoder.encode(configuredPassword), now);
                credentialRepository.save(credential);
            }
        }

        syncSeedUser(configuredAdminUsername, configuredAdminPassword, UserRole.ADMIN, now);
        syncSeedUser(configuredEmpleadoUsername, configuredEmpleadoPassword, UserRole.EMPLEADO, now);
    }

    private void syncSeedUser(String username, String password, UserRole role, Instant now) {
        if (username == null || username.isBlank()) {
            throw new IllegalStateException("Usuario semilla invalido para rol " + role);
        }
        if (password == null || password.isBlank()) {
            throw new IllegalStateException("Contrasena semilla invalida para rol " + role);
        }

        AuthUser user = authUserRepository.findByUsernameIgnoreCase(username).orElse(null);
        String encodedPassword = passwordEncoder.encode(password);

        if (user == null) {
            authUserRepository.save(new AuthUser(username, encodedPassword, role, true, null, now));
            return;
        }

        boolean changed = false;
        if (user.getRole() != role) {
            user.applyRole(role, now);
            changed = true;
        }
        if (!passwordEncoder.matches(password, user.getPasswordHash())) {
            user.applyPasswordHash(encodedPassword, now);
            changed = true;
        }
        if (!user.isActive()) {
            user.setActive(true, now);
            changed = true;
        }

        if (changed) {
            authUserRepository.save(user);
        }
    }
}
