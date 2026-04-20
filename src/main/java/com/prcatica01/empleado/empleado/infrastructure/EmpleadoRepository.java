package com.prcatica01.empleado.empleado.infrastructure;

import com.prcatica01.empleado.empleado.domain.Empleado;
import com.prcatica01.empleado.empleado.domain.EmpleadoId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface EmpleadoRepository extends JpaRepository<Empleado, EmpleadoId> {

    @Query(value = "SELECT nextval('empleado_numero_seq')", nativeQuery = true)
    Long nextClaveNumero();
}
