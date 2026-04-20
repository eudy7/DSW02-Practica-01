package com.prcatica01.empleado.empleado.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record EmpleadoUpdateRequest(
    @NotBlank(message = "nombre es obligatorio")
    @Size(max = 100, message = "nombre debe tener máximo 100 caracteres")
    String nombre,
    @NotBlank(message = "direccion es obligatoria")
    @Size(max = 100, message = "direccion debe tener máximo 100 caracteres")
    String direccion,
    @NotBlank(message = "telefono es obligatorio")
    @Size(max = 100, message = "telefono debe tener máximo 100 caracteres")
    String telefono
) {
}
