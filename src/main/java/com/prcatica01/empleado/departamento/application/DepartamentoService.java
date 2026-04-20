package com.prcatica01.empleado.departamento.application;

import com.prcatica01.empleado.departamento.api.dto.DepartamentoDtos.DepartamentoCreateRequest;
import com.prcatica01.empleado.departamento.api.dto.DepartamentoDtos.DepartamentoDetalleResponse;
import com.prcatica01.empleado.departamento.api.dto.DepartamentoDtos.DepartamentoResumenResponse;
import com.prcatica01.empleado.departamento.api.dto.DepartamentoDtos.DepartamentoUpdateRequest;
import com.prcatica01.empleado.departamento.api.dto.DepartamentoDtos;
import com.prcatica01.empleado.departamento.domain.Departamento;
import com.prcatica01.empleado.departamento.domain.DepartamentoEstadoFiltro;
import com.prcatica01.empleado.departamento.infrastructure.DepartamentoRepository;
import com.prcatica01.empleado.departamento.infrastructure.HistorialAsignacionDepartamentoRepository;
import com.prcatica01.empleado.shared.domain.DomainExceptions.DepartamentoConEmpleadosActivosException;
import com.prcatica01.empleado.shared.domain.DomainExceptions.DepartamentoDuplicadoException;
import com.prcatica01.empleado.shared.domain.DomainExceptions.NotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class DepartamentoService {

    private final DepartamentoRepository departamentoRepository;
    private final HistorialAsignacionDepartamentoRepository historialRepository;

    public DepartamentoService(
        DepartamentoRepository departamentoRepository,
        HistorialAsignacionDepartamentoRepository historialRepository
    ) {
        this.departamentoRepository = departamentoRepository;
        this.historialRepository = historialRepository;
    }

    @Transactional
    public DepartamentoDetalleResponse crear(DepartamentoCreateRequest request) {
        String nombre = normalizarTextoObligatorio(request.nombre());
        String descripcion = normalizarTextoOpcional(request.descripcion());

        if (departamentoRepository.existsByNombreIgnoreCaseAndActivoTrue(nombre)) {
            throw new DepartamentoDuplicadoException("Ya existe un departamento activo con ese nombre");
        }

        Departamento departamento = new Departamento(nombre, descripcion);
        Departamento saved = departamentoRepository.save(departamento);
        return DepartamentoDtos.toDetalleResponse(saved, 0L);
    }

    @Transactional(readOnly = true)
    public List<DepartamentoResumenResponse> listar(DepartamentoEstadoFiltro estadoFiltro) {
        List<Departamento> departamentos = switch (estadoFiltro) {
            case ACTIVO -> departamentoRepository.findByActivoOrderByNombreAsc(true);
            case INACTIVO -> departamentoRepository.findByActivoOrderByNombreAsc(false);
            case TODOS -> departamentoRepository.findAllByOrderByNombreAsc();
        };

        return departamentos.stream()
            .map(DepartamentoDtos::toResumenResponse)
            .toList();
    }

    @Transactional(readOnly = true)
    public DepartamentoDetalleResponse obtenerPorId(Long departamentoId) {
        Departamento departamento = departamentoRepository.findById(departamentoId)
            .orElseThrow(() -> new NotFoundException("Departamento no encontrado"));

        long totalActivos = historialRepository.countActivosByDepartamentoId(departamentoId);
        return DepartamentoDtos.toDetalleResponse(departamento, totalActivos);
    }

    @Transactional
    public DepartamentoDetalleResponse actualizar(Long departamentoId, DepartamentoUpdateRequest request) {
        Departamento departamento = departamentoRepository.findById(departamentoId)
            .orElseThrow(() -> new NotFoundException("Departamento no encontrado"));

        String nombre = normalizarTextoObligatorio(request.nombre());
        String descripcion = normalizarTextoOpcional(request.descripcion());

        if (departamentoRepository.existsOtherActiveByNombreIgnoreCase(nombre, departamentoId)) {
            throw new DepartamentoDuplicadoException("Ya existe un departamento activo con ese nombre");
        }

        departamento.actualizar(nombre, descripcion);
        Departamento updated = departamentoRepository.save(departamento);

        long totalActivos = historialRepository.countActivosByDepartamentoId(updated.getId());
        return DepartamentoDtos.toDetalleResponse(updated, totalActivos);
    }

    @Transactional
    public void inactivar(Long departamentoId) {
        Departamento departamento = departamentoRepository.findById(departamentoId)
            .orElseThrow(() -> new NotFoundException("Departamento no encontrado"));

        if (!departamento.isActivo()) {
            return;
        }

        if (historialRepository.existsByDepartamentoIdAndFechaFinIsNull(departamentoId)) {
            throw new DepartamentoConEmpleadosActivosException(
                "No se puede inactivar un departamento con empleados activos asociados"
            );
        }

        departamento.inactivar();
        departamentoRepository.save(departamento);
    }

    @Transactional
    public DepartamentoDetalleResponse activar(Long departamentoId) {
        Departamento departamento = departamentoRepository.findById(departamentoId)
            .orElseThrow(() -> new NotFoundException("Departamento no encontrado"));

        if (departamento.isActivo()) {
            long totalActivos = historialRepository.countActivosByDepartamentoId(departamentoId);
            return DepartamentoDtos.toDetalleResponse(departamento, totalActivos);
        }

        if (departamentoRepository.existsOtherActiveByNombreIgnoreCase(departamento.getNombre(), departamentoId)) {
            throw new DepartamentoDuplicadoException("Ya existe un departamento activo con ese nombre");
        }

        departamento.activar();
        Departamento updated = departamentoRepository.save(departamento);
        long totalActivos = historialRepository.countActivosByDepartamentoId(updated.getId());
        return DepartamentoDtos.toDetalleResponse(updated, totalActivos);
    }

    private String normalizarTextoObligatorio(String valor) {
        if (valor == null || valor.trim().isEmpty()) {
            throw new IllegalArgumentException("nombre es obligatorio");
        }
        return valor.trim();
    }

    private String normalizarTextoOpcional(String valor) {
        if (valor == null) {
            return null;
        }
        String normalizado = valor.trim();
        return normalizado.isEmpty() ? null : normalizado;
    }
}
