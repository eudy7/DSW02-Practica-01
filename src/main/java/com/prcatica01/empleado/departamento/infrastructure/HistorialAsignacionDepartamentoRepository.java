package com.prcatica01.empleado.departamento.infrastructure;

import com.prcatica01.empleado.departamento.domain.HistorialAsignacionDepartamento;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.Lock;

import java.util.List;
import java.util.Optional;

public interface HistorialAsignacionDepartamentoRepository extends JpaRepository<HistorialAsignacionDepartamento, Long> {

    boolean existsByDepartamentoIdAndFechaFinIsNull(Long departamentoId);

    @Query("select count(h) from HistorialAsignacionDepartamento h where h.departamento.id = :departamentoId and h.fechaFin is null")
    long countActivosByDepartamentoId(@Param("departamentoId") Long departamentoId);

    Optional<HistorialAsignacionDepartamento> findFirstByEmpleadoIdClavePrefijoAndEmpleadoIdClaveNumeroAndFechaFinIsNull(
        String clavePrefijo,
        Long claveNumero
    );

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
        select h
        from HistorialAsignacionDepartamento h
        where h.empleado.id.clavePrefijo = :clavePrefijo
          and h.empleado.id.claveNumero = :claveNumero
          and h.fechaFin is null
        """)
    Optional<HistorialAsignacionDepartamento> findActiveByEmpleadoWithLock(
        @Param("clavePrefijo") String clavePrefijo,
        @Param("claveNumero") Long claveNumero
    );

    @Query("""
        select h
        from HistorialAsignacionDepartamento h
        join fetch h.empleado e
        where h.departamento.id = :departamentoId
          and h.fechaFin is null
        order by h.fechaInicio asc
        """)
    List<HistorialAsignacionDepartamento> findActivosByDepartamentoId(@Param("departamentoId") Long departamentoId);

    void deleteByEmpleadoIdClavePrefijoAndEmpleadoIdClaveNumero(String clavePrefijo, Long claveNumero);
}
