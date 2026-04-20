package com.prcatica01.empleado.auth.infrastructure;

import com.prcatica01.empleado.auth.domain.AuthCredential;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AuthCredentialRepository extends JpaRepository<AuthCredential, Long> {

    Optional<AuthCredential> findTopByOrderByIdAsc();
}
