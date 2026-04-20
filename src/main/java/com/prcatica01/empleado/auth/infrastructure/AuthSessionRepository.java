package com.prcatica01.empleado.auth.infrastructure;

import com.prcatica01.empleado.auth.domain.AuthSession;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AuthSessionRepository extends JpaRepository<AuthSession, String> {

    Optional<AuthSession> findByTokenAndActiveTrue(String token);

    Optional<AuthSession> findByUsernameAndActiveTrue(String username);

    Optional<AuthSession> findByUserIdAndActiveTrue(Long userId);

    List<AuthSession> findAllByUserIdAndActiveTrue(Long userId);

    void deleteByUserId(Long userId);
}
