package com.prcatica01.empleado.departamento.api.dto;

import com.prcatica01.empleado.departamento.domain.Departamento;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public final class DepartamentoDtos {

    private DepartamentoDtos() {
    }

    public record DepartamentoCreateRequest(
        @NotBlank(message = "nombre es obligatorio")
        @Size(max = 120, message = "nombre debe tener maximo 120 caracteres")
        String nombre,
        @Size(max = 255, message = "descripcion debe tener maximo 255 caracteres")
        String descripcion
    ) {
    }

    public record DepartamentoUpdateRequest(
        @NotBlank(message = "nombre es obligatorio")
        @Size(max = 120, message = "nombre debe tener maximo 120 caracteres")
        String nombre,
        @Size(max = 255, message = "descripcion debe tener maximo 255 caracteres")
        String descripcion
    ) {
    }

    public record DepartamentoResumenResponse(
        Long id,
        String nombre,
        String descripcion,
        boolean activo
    ) {
    }

    public record DepartamentoDetalleResponse(
        Long id,
        String nombre,
        String descripcion,
        boolean activo,
        long totalEmpleadosActivos
    ) {
    }

    public static DepartamentoResumenResponse toResumenResponse(Departamento departamento) {
        return new DepartamentoResumenResponse(
            departamento.getId(),
            departamento.getNombre(),
            departamento.getDescripcion(),
            departamento.isActivo()
        );
    }

    public static DepartamentoDetalleResponse toDetalleResponse(Departamento departamento, long totalEmpleadosActivos) {
        return new DepartamentoDetalleResponse(
            departamento.getId(),
            departamento.getNombre(),
            departamento.getDescripcion(),
            departamento.isActivo(),
            totalEmpleadosActivos
        );
    }
}
