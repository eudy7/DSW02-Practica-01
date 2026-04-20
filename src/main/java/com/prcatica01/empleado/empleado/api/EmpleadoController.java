package com.prcatica01.empleado.empleado.api;

import com.prcatica01.empleado.empleado.api.dto.EmpleadoDtos.EmpleadoCreateRequest;
import com.prcatica01.empleado.empleado.api.dto.EmpleadoDtos.EmpleadoResponse;
import com.prcatica01.empleado.empleado.api.dto.EmpleadoUpdateRequest;
import com.prcatica01.empleado.empleado.application.EmpleadoService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Validated
@RestController
@RequestMapping("/api/empleados")
public class EmpleadoController {

    private final EmpleadoService empleadoService;

    public EmpleadoController(EmpleadoService empleadoService) {
        this.empleadoService = empleadoService;
    }

    @GetMapping
    public List<EmpleadoResponse> listarEmpleados() {
        return empleadoService.listar();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EmpleadoResponse crearEmpleado(@Valid @RequestBody EmpleadoCreateRequest request) {
        return empleadoService.crear(request);
    }

    @GetMapping("/{clave}")
    public EmpleadoResponse obtenerEmpleado(
        @PathVariable
        @Pattern(regexp = "^EMP-[0-9]+$", message = "La clave debe tener formato EMP-<autonumérico>")
        String clave
    ) {
        return empleadoService.obtenerPorClave(clave);
    }

    @PutMapping("/{clave}")
    public EmpleadoResponse actualizarEmpleado(
        @PathVariable
        @Pattern(regexp = "^EMP-[0-9]+$", message = "La clave debe tener formato EMP-<autonumérico>")
        String clave,
        @Valid @RequestBody EmpleadoUpdateRequest request
    ) {
        return empleadoService.actualizar(clave, request);
    }

    @DeleteMapping("/{clave}")
    public ResponseEntity<Void> eliminarEmpleado(
        @PathVariable
        @Pattern(regexp = "^EMP-[0-9]+$", message = "La clave debe tener formato EMP-<autonumérico>")
        String clave
    ) {
        empleadoService.eliminar(clave);
        return ResponseEntity.noContent().build();
    }
}
