package com.prcatica01.empleado.departamento.api;

import com.prcatica01.empleado.departamento.api.dto.EmpleadoDepartamentoDtos.EmpleadoDepartamentoActualResponse;
import com.prcatica01.empleado.departamento.api.dto.EmpleadoDepartamentoDtos.EmpleadoDepartamentoAssignRequest;
import com.prcatica01.empleado.departamento.application.EmpleadoDepartamentoService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequestMapping("/api/empleados/{clave}/departamento")
public class EmpleadoDepartamentoController {

    private final EmpleadoDepartamentoService empleadoDepartamentoService;

    public EmpleadoDepartamentoController(EmpleadoDepartamentoService empleadoDepartamentoService) {
        this.empleadoDepartamentoService = empleadoDepartamentoService;
    }

    @GetMapping
    public EmpleadoDepartamentoActualResponse obtenerAsignacionActual(
        @PathVariable
        @Pattern(regexp = "^EMP-[0-9]+$", message = "La clave debe tener formato EMP-<autonumerico>")
        String clave
    ) {
        return empleadoDepartamentoService.obtenerAsignacionActual(clave);
    }

    @PutMapping
    @PreAuthorize("hasRole('ADMIN')")
    public EmpleadoDepartamentoActualResponse asignarDepartamento(
        @PathVariable
        @Pattern(regexp = "^EMP-[0-9]+$", message = "La clave debe tener formato EMP-<autonumerico>")
        String clave,
        @Valid @RequestBody EmpleadoDepartamentoAssignRequest request
    ) {
        return empleadoDepartamentoService.asignar(clave, request);
    }

    @DeleteMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> removerAsignacion(
        @PathVariable
        @Pattern(regexp = "^EMP-[0-9]+$", message = "La clave debe tener formato EMP-<autonumerico>")
        String clave
    ) {
        empleadoDepartamentoService.removerAsignacion(clave);
        return ResponseEntity.noContent().build();
    }
}
