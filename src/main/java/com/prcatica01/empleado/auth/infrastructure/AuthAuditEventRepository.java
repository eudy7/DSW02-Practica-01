package com.prcatica01.empleado.auth.infrastructure;

import com.prcatica01.empleado.auth.domain.AuthAuditEvent;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuthAuditEventRepository extends JpaRepository<AuthAuditEvent, Long> {
}
