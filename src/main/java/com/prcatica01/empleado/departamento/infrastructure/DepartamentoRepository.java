package com.prcatica01.empleado.departamento.infrastructure;

import com.prcatica01.empleado.departamento.domain.Departamento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface DepartamentoRepository extends JpaRepository<Departamento, Long> {

    boolean existsByNombreIgnoreCaseAndActivoTrue(String nombre);

    @Query("select count(d) > 0 from Departamento d where lower(d.nombre) = lower(:nombre) and d.activo = true and d.id <> :id")
    boolean existsOtherActiveByNombreIgnoreCase(@Param("nombre") String nombre, @Param("id") Long id);

    List<Departamento> findByActivoTrueOrderByNombreAsc();

    List<Departamento> findByActivoFalseOrderByNombreAsc();

    List<Departamento> findByActivoOrderByNombreAsc(boolean activo);

    List<Departamento> findAllByOrderByNombreAsc();
}
