package com.prcatica01.empleado.departamento.api.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.time.Instant;

public final class EmpleadoDepartamentoDtos {

    private EmpleadoDepartamentoDtos() {
    }

    public record EmpleadoDepartamentoAssignRequest(
        @NotNull(message = "departamentoId es obligatorio")
        @Min(value = 1, message = "departamentoId debe ser mayor a 0")
        Long departamentoId
    ) {
    }

    public record EmpleadoDepartamentoActualResponse(
        String empleadoClave,
        Long departamentoId,
        String departamentoNombre,
        Instant fechaInicio
    ) {
    }

    public record EmpleadoDepartamentoResponse(
        String empleadoClave,
        String nombre,
        Instant fechaInicio
    ) {
    }

    public static EmpleadoDepartamentoActualResponse toActualResponse(
        String empleadoClave,
        Long departamentoId,
        String departamentoNombre,
        Instant fechaInicio
    ) {
        return new EmpleadoDepartamentoActualResponse(
            empleadoClave,
            departamentoId,
            departamentoNombre,
            fechaInicio
        );
    }
}
