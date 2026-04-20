package com.prcatica01.empleado.empleado.application;

import com.prcatica01.empleado.auth.infrastructure.AuthSessionRepository;
import com.prcatica01.empleado.auth.infrastructure.AuthUserRepository;
import com.prcatica01.empleado.departamento.infrastructure.HistorialAsignacionDepartamentoRepository;
import com.prcatica01.empleado.empleado.api.dto.EmpleadoDtos.EmpleadoCreateRequest;
import com.prcatica01.empleado.empleado.api.dto.EmpleadoDtos.EmpleadoResponse;
import com.prcatica01.empleado.empleado.api.dto.EmpleadoUpdateRequest;
import com.prcatica01.empleado.empleado.domain.Empleado;
import com.prcatica01.empleado.empleado.domain.EmpleadoId;
import com.prcatica01.empleado.empleado.infrastructure.EmpleadoRepository;
import com.prcatica01.empleado.shared.domain.DomainExceptions.ConflictException;
import com.prcatica01.empleado.shared.domain.DomainExceptions.InvalidClaveFormatException;
import com.prcatica01.empleado.shared.domain.DomainExceptions.NotFoundException;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class EmpleadoService {

    private static final String CLAVE_PREFIX = "EMP";
    private static final Pattern CLAVE_PATTERN = Pattern.compile("^EMP-(\\d+)$");

    private final EmpleadoRepository empleadoRepository;
    private final HistorialAsignacionDepartamentoRepository historialAsignacionDepartamentoRepository;
    private final AuthUserRepository authUserRepository;
    private final AuthSessionRepository authSessionRepository;

    public EmpleadoService(
        EmpleadoRepository empleadoRepository,
        HistorialAsignacionDepartamentoRepository historialAsignacionDepartamentoRepository,
        AuthUserRepository authUserRepository,
        AuthSessionRepository authSessionRepository
    ) {
        this.empleadoRepository = empleadoRepository;
        this.historialAsignacionDepartamentoRepository = historialAsignacionDepartamentoRepository;
        this.authUserRepository = authUserRepository;
        this.authSessionRepository = authSessionRepository;
    }

    @Transactional
    public EmpleadoResponse crear(EmpleadoCreateRequest request) {
        Long claveNumero = empleadoRepository.nextClaveNumero();
        if (claveNumero == null || claveNumero <= 0) {
            throw new ConflictException("No fue posible generar la clave del empleado");
        }

        EmpleadoId empleadoId = new EmpleadoId(CLAVE_PREFIX, claveNumero);
        if (empleadoRepository.existsById(empleadoId)) {
            throw new ConflictException("La clave generada ya existe");
        }

        Empleado empleado = new Empleado(empleadoId, request.nombre(), request.direccion(), request.telefono());
        Empleado saved = empleadoRepository.save(empleado);
        return mapToResponse(saved);
    }

    @Transactional(readOnly = true)
    public EmpleadoResponse obtenerPorClave(String clave) {
        Empleado empleado = empleadoRepository.findById(parseClave(clave))
            .orElseThrow(() -> new NotFoundException("Empleado no encontrado"));
        return mapToResponse(empleado);
    }

    @Transactional(readOnly = true)
    public List<EmpleadoResponse> listar() {
        return empleadoRepository.findAll(Sort.by(Sort.Direction.ASC, "id.claveNumero"))
            .stream()
            .map(this::mapToResponse)
            .toList();
    }

    @Transactional
    public EmpleadoResponse actualizar(String clave, EmpleadoUpdateRequest request) {
        Empleado empleado = empleadoRepository.findById(parseClave(clave))
            .orElseThrow(() -> new NotFoundException("Empleado no encontrado"));

        empleado.actualizarDatos(request.nombre(), request.direccion(), request.telefono());
        Empleado updated = empleadoRepository.save(empleado);
        return mapToResponse(updated);
    }

    @Transactional
    public void eliminar(String clave) {
        EmpleadoId empleadoId = parseClave(clave);
        if (!empleadoRepository.existsById(empleadoId)) {
            throw new NotFoundException("Empleado no encontrado");
        }

        historialAsignacionDepartamentoRepository.deleteByEmpleadoIdClavePrefijoAndEmpleadoIdClaveNumero(
            empleadoId.getClavePrefijo(),
            empleadoId.getClaveNumero()
        );

        authUserRepository
            .findByEmpleadoIdClavePrefijoAndEmpleadoIdClaveNumero(empleadoId.getClavePrefijo(), empleadoId.getClaveNumero())
            .ifPresent(authUser -> {
                authSessionRepository.deleteByUserId(authUser.getId());
                authUserRepository.delete(authUser);
            });

        empleadoRepository.deleteById(empleadoId);
    }

    private EmpleadoId parseClave(String clave) {
        Matcher matcher = CLAVE_PATTERN.matcher(clave);
        if (!matcher.matches()) {
            throw new InvalidClaveFormatException("La clave debe tener formato EMP-<autonumérico>");
        }

        long numero;
        try {
            numero = Long.parseLong(matcher.group(1));
        } catch (NumberFormatException exception) {
            throw new InvalidClaveFormatException("La clave debe tener formato EMP-<autonumérico>");
        }

        return new EmpleadoId(CLAVE_PREFIX, numero);
    }

    private EmpleadoResponse mapToResponse(Empleado empleado) {
        return new EmpleadoResponse(
            empleado.getClave(),
            empleado.getId().getClavePrefijo(),
            empleado.getId().getClaveNumero(),
            empleado.getNombre(),
            empleado.getDireccion(),
            empleado.getTelefono()
        );
    }
}
