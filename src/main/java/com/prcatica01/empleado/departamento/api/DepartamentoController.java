package com.prcatica01.empleado.departamento.api;

import com.prcatica01.empleado.departamento.api.dto.DepartamentoDtos.DepartamentoCreateRequest;
import com.prcatica01.empleado.departamento.api.dto.DepartamentoDtos.DepartamentoDetalleResponse;
import com.prcatica01.empleado.departamento.api.dto.DepartamentoDtos.DepartamentoResumenResponse;
import com.prcatica01.empleado.departamento.api.dto.DepartamentoDtos.DepartamentoUpdateRequest;
import com.prcatica01.empleado.departamento.api.dto.EmpleadoDepartamentoDtos.EmpleadoDepartamentoResponse;
import com.prcatica01.empleado.departamento.application.DepartamentoService;
import com.prcatica01.empleado.departamento.application.EmpleadoDepartamentoService;
import com.prcatica01.empleado.departamento.domain.DepartamentoEstadoFiltro;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.springframework.security.access.prepost.PreAuthorize;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Validated
@RestController
@RequestMapping("/api/departamentos")
public class DepartamentoController {

    private final DepartamentoService departamentoService;
    private final EmpleadoDepartamentoService empleadoDepartamentoService;

    public DepartamentoController(
        DepartamentoService departamentoService,
        EmpleadoDepartamentoService empleadoDepartamentoService
    ) {
        this.departamentoService = departamentoService;
        this.empleadoDepartamentoService = empleadoDepartamentoService;
    }

    @GetMapping
    public List<DepartamentoResumenResponse> listarDepartamentos(
        @RequestParam(name = "includeInactive", defaultValue = "false")
        boolean includeInactive
    ) {
        DepartamentoEstadoFiltro estadoFiltro = includeInactive
            ? DepartamentoEstadoFiltro.TODOS
            : DepartamentoEstadoFiltro.ACTIVO;
        return departamentoService.listar(estadoFiltro);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.CREATED)
    public DepartamentoDetalleResponse crearDepartamento(@Valid @RequestBody DepartamentoCreateRequest request) {
        return departamentoService.crear(request);
    }

    @GetMapping("/{departamentoId}")
    public DepartamentoDetalleResponse obtenerDepartamento(
        @PathVariable
        @Min(value = 1, message = "departamentoId debe ser mayor a 0")
        Long departamentoId
    ) {
        return departamentoService.obtenerPorId(departamentoId);
    }

    @PutMapping("/{departamentoId}")
    @PreAuthorize("hasRole('ADMIN')")
    public DepartamentoDetalleResponse actualizarDepartamento(
        @PathVariable
        @Min(value = 1, message = "departamentoId debe ser mayor a 0")
        Long departamentoId,
        @Valid @RequestBody DepartamentoUpdateRequest request
    ) {
        return departamentoService.actualizar(departamentoId, request);
    }

    @DeleteMapping("/{departamentoId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> inactivarDepartamento(
        @PathVariable
        @Min(value = 1, message = "departamentoId debe ser mayor a 0")
        Long departamentoId
    ) {
        departamentoService.inactivar(departamentoId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{departamentoId}/activar")
    @PreAuthorize("hasRole('ADMIN')")
    public DepartamentoDetalleResponse activarDepartamento(
        @PathVariable
        @Min(value = 1, message = "departamentoId debe ser mayor a 0")
        Long departamentoId
    ) {
        return departamentoService.activar(departamentoId);
    }

    @GetMapping("/{departamentoId}/empleados")
    @PreAuthorize("hasRole('ADMIN')")
    public List<EmpleadoDepartamentoResponse> listarEmpleadosActivosPorDepartamento(
        @PathVariable
        @Min(value = 1, message = "departamentoId debe ser mayor a 0")
        Long departamentoId
    ) {
        return empleadoDepartamentoService.listarEmpleadosActivosPorDepartamento(departamentoId);
    }
}
