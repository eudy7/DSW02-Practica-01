package com.prcatica01.empleado.departamento.application;

import com.prcatica01.empleado.departamento.api.dto.EmpleadoDepartamentoDtos.EmpleadoDepartamentoActualResponse;
import com.prcatica01.empleado.departamento.api.dto.EmpleadoDepartamentoDtos.EmpleadoDepartamentoAssignRequest;
import com.prcatica01.empleado.departamento.api.dto.EmpleadoDepartamentoDtos.EmpleadoDepartamentoResponse;
import com.prcatica01.empleado.departamento.api.dto.EmpleadoDepartamentoDtos;
import com.prcatica01.empleado.departamento.domain.Departamento;
import com.prcatica01.empleado.departamento.domain.HistorialAsignacionDepartamento;
import com.prcatica01.empleado.departamento.infrastructure.DepartamentoRepository;
import com.prcatica01.empleado.departamento.infrastructure.HistorialAsignacionDepartamentoRepository;
import com.prcatica01.empleado.empleado.domain.Empleado;
import com.prcatica01.empleado.empleado.domain.EmpleadoId;
import com.prcatica01.empleado.empleado.infrastructure.EmpleadoRepository;
import com.prcatica01.empleado.shared.domain.DomainExceptions.ConflictException;
import com.prcatica01.empleado.shared.domain.DomainExceptions.DepartamentoInactivoException;
import com.prcatica01.empleado.shared.domain.DomainExceptions.InvalidClaveFormatException;
import com.prcatica01.empleado.shared.domain.DomainExceptions.NotFoundException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class EmpleadoDepartamentoService {

    private static final String CLAVE_PREFIX = "EMP";
    private static final Pattern CLAVE_PATTERN = Pattern.compile("^EMP-(\\d+)$");

    private final EmpleadoRepository empleadoRepository;
    private final DepartamentoRepository departamentoRepository;
    private final HistorialAsignacionDepartamentoRepository historialRepository;

    public EmpleadoDepartamentoService(
        EmpleadoRepository empleadoRepository,
        DepartamentoRepository departamentoRepository,
        HistorialAsignacionDepartamentoRepository historialRepository
    ) {
        this.empleadoRepository = empleadoRepository;
        this.departamentoRepository = departamentoRepository;
        this.historialRepository = historialRepository;
    }

    @Transactional(readOnly = true)
    public EmpleadoDepartamentoActualResponse obtenerAsignacionActual(String claveEmpleado) {
        EmpleadoId empleadoId = parseClave(claveEmpleado);
        Empleado empleado = empleadoRepository.findById(empleadoId)
            .orElseThrow(() -> new NotFoundException("Empleado no encontrado"));

        HistorialAsignacionDepartamento activa =
            historialRepository.findFirstByEmpleadoIdClavePrefijoAndEmpleadoIdClaveNumeroAndFechaFinIsNull(
                    empleado.getId().getClavePrefijo(),
                    empleado.getId().getClaveNumero()
                )
                .orElseThrow(() -> new NotFoundException("El empleado no tiene un departamento activo asignado"));

        return mapToActualResponse(empleado, activa.getDepartamento(), activa.getFechaInicio());
    }

    @Transactional
    public EmpleadoDepartamentoActualResponse asignar(String claveEmpleado, EmpleadoDepartamentoAssignRequest request) {
        Empleado empleado = empleadoRepository.findById(parseClave(claveEmpleado))
            .orElseThrow(() -> new NotFoundException("Empleado no encontrado"));

        Departamento departamento = departamentoRepository.findById(request.departamentoId())
            .orElseThrow(() -> new NotFoundException("Departamento no encontrado"));

        if (!departamento.isActivo()) {
            throw new DepartamentoInactivoException("No se puede asignar un empleado a un departamento inactivo");
        }

        Instant now = Instant.now();

        Optional<HistorialAsignacionDepartamento> activaOpt =
            historialRepository.findActiveByEmpleadoWithLock(
                empleado.getId().getClavePrefijo(),
                empleado.getId().getClaveNumero()
            );

        if (activaOpt.isPresent()) {
            HistorialAsignacionDepartamento activa = activaOpt.get();
            if (activa.getDepartamento().getId().equals(departamento.getId())) {
                return mapToActualResponse(empleado, activa.getDepartamento(), activa.getFechaInicio());
            }
            activa.cerrar(now);
            // Flush the close operation before inserting a new active assignment.
            historialRepository.saveAndFlush(activa);
        }

        HistorialAsignacionDepartamento nuevaAsignacion =
            new HistorialAsignacionDepartamento(empleado, departamento, now);
        HistorialAsignacionDepartamento saved;
        try {
            saved = historialRepository.save(nuevaAsignacion);
        } catch (DataIntegrityViolationException exception) {
            Optional<HistorialAsignacionDepartamento> activaActual =
                historialRepository.findFirstByEmpleadoIdClavePrefijoAndEmpleadoIdClaveNumeroAndFechaFinIsNull(
                    empleado.getId().getClavePrefijo(),
                    empleado.getId().getClaveNumero()
                );

            if (activaActual.isPresent() && activaActual.get().getDepartamento().getId().equals(departamento.getId())) {
                HistorialAsignacionDepartamento activa = activaActual.get();
                return mapToActualResponse(empleado, activa.getDepartamento(), activa.getFechaInicio());
            }
            throw new ConflictException("Conflicto de reasignacion concurrente, intenta nuevamente");
        }

        return mapToActualResponse(empleado, departamento, saved.getFechaInicio());
    }

    @Transactional
    public void removerAsignacion(String claveEmpleado) {
        EmpleadoId empleadoId = parseClave(claveEmpleado);
        Empleado empleado = empleadoRepository.findById(empleadoId)
            .orElseThrow(() -> new NotFoundException("Empleado no encontrado"));

        HistorialAsignacionDepartamento activa =
            historialRepository.findActiveByEmpleadoWithLock(
                    empleado.getId().getClavePrefijo(),
                    empleado.getId().getClaveNumero()
                )
                .orElseThrow(() -> new NotFoundException("El empleado no tiene un departamento activo asignado"));

        activa.cerrar(Instant.now());
        historialRepository.save(activa);
    }

    @Transactional(readOnly = true)
    public List<EmpleadoDepartamentoResponse> listarEmpleadosActivosPorDepartamento(Long departamentoId) {
        if (!departamentoRepository.existsById(departamentoId)) {
            throw new NotFoundException("Departamento no encontrado");
        }

        return historialRepository.findActivosByDepartamentoId(departamentoId)
            .stream()
            .map(historial -> new EmpleadoDepartamentoResponse(
                historial.getEmpleado().getClave(),
                historial.getEmpleado().getNombre(),
                historial.getFechaInicio()
            ))
            .toList();
    }

    private EmpleadoDepartamentoActualResponse mapToActualResponse(
        Empleado empleado,
        Departamento departamento,
        Instant fechaInicio
    ) {
        return EmpleadoDepartamentoDtos.toActualResponse(
            empleado.getClave(),
            departamento.getId(),
            departamento.getNombre(),
            fechaInicio
        );
    }

    private EmpleadoId parseClave(String clave) {
        Matcher matcher = CLAVE_PATTERN.matcher(clave);
        if (!matcher.matches()) {
            throw new InvalidClaveFormatException("La clave debe tener formato EMP-<autonumerico>");
        }

        long numero;
        try {
            numero = Long.parseLong(matcher.group(1));
        } catch (NumberFormatException exception) {
            throw new InvalidClaveFormatException("La clave debe tener formato EMP-<autonumerico>");
        }

        return new EmpleadoId(CLAVE_PREFIX, numero);
    }
}
