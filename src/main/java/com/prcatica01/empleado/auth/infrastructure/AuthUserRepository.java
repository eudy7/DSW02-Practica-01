package com.prcatica01.empleado.auth.infrastructure;

import com.prcatica01.empleado.auth.domain.AuthUser;
import com.prcatica01.empleado.auth.domain.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AuthUserRepository extends JpaRepository<AuthUser, Long> {

    Optional<AuthUser> findByUsernameIgnoreCase(String username);

    Optional<AuthUser> findByUsernameIgnoreCaseAndActiveTrue(String username);

    Optional<AuthUser> findByEmpleadoIdClavePrefijoAndEmpleadoIdClaveNumero(String clavePrefijo, Long claveNumero);

    Optional<AuthUser> findTopByRoleAndActiveTrueOrderByIdAsc(UserRole role);

    Optional<AuthUser> findTopByActiveTrueOrderByIdAsc();
}
