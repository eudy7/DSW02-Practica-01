package com.prcatica01.empleado.shared.api;

import com.prcatica01.empleado.shared.domain.DomainExceptions.ConflictException;
import com.prcatica01.empleado.shared.domain.DomainExceptions.InvalidClaveFormatException;
import com.prcatica01.empleado.shared.domain.DomainExceptions.NotFoundException;
import com.prcatica01.empleado.shared.domain.DomainExceptions.AccountLockedException;
import com.prcatica01.empleado.shared.domain.DomainExceptions.AuthenticationFailedException;
import com.prcatica01.empleado.shared.domain.DomainExceptions.DepartamentoConEmpleadosActivosException;
import com.prcatica01.empleado.shared.domain.DomainExceptions.DepartamentoDuplicadoException;
import com.prcatica01.empleado.shared.domain.DomainExceptions.DepartamentoInactivoException;
import com.prcatica01.empleado.shared.domain.DomainExceptions.ForbiddenException;
import com.prcatica01.empleado.shared.domain.DomainExceptions.SessionExpiredException;
import com.prcatica01.empleado.shared.domain.DomainExceptions.UnauthorizedException;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValid(MethodArgumentNotValidException exception) {
        List<String> details = exception.getBindingResult().getAllErrors().stream()
            .map(error -> {
                if (error instanceof FieldError fieldError) {
                    return fieldError.getField() + ": " + fieldError.getDefaultMessage();
                }
                return error.getDefaultMessage();
            })
            .toList();

        return ResponseEntity.badRequest()
            .body(new ErrorResponse("VALIDATION_ERROR", "Solicitud inválida", details));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraintViolation(ConstraintViolationException exception) {
        List<String> details = exception.getConstraintViolations().stream()
            .map(violation -> violation.getPropertyPath() + ": " + violation.getMessage())
            .toList();

        return ResponseEntity.badRequest()
            .body(new ErrorResponse("VALIDATION_ERROR", "Solicitud inválida", details));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgument(IllegalArgumentException exception) {
        return ResponseEntity.badRequest()
            .body(new ErrorResponse("VALIDATION_ERROR", "Solicitud inválida", List.of(exception.getMessage())));
    }

    @ExceptionHandler(InvalidClaveFormatException.class)
    public ResponseEntity<ErrorResponse> handleInvalidClaveFormat(InvalidClaveFormatException exception) {
        return ResponseEntity.badRequest()
            .body(new ErrorResponse("INVALID_CLAVE_FORMAT", exception.getMessage(), List.of()));
    }

    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<ErrorResponse> handleConflict(ConflictException exception) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
            .body(new ErrorResponse("CONFLICT", exception.getMessage(), List.of()));
    }

    @ExceptionHandler(ObjectOptimisticLockingFailureException.class)
    public ResponseEntity<ErrorResponse> handleOptimisticLockConflict(ObjectOptimisticLockingFailureException exception) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
            .body(new ErrorResponse("CONFLICT", "Conflicto de concurrencia, intenta nuevamente", List.of()));
    }

    @ExceptionHandler(DepartamentoDuplicadoException.class)
    public ResponseEntity<ErrorResponse> handleDepartamentoDuplicado(DepartamentoDuplicadoException exception) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
            .body(new ErrorResponse("CONFLICT", exception.getMessage(), List.of()));
    }

    @ExceptionHandler(DepartamentoConEmpleadosActivosException.class)
    public ResponseEntity<ErrorResponse> handleDepartamentoConEmpleadosActivos(
        DepartamentoConEmpleadosActivosException exception
    ) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
            .body(new ErrorResponse("CONFLICT", exception.getMessage(), List.of()));
    }

    @ExceptionHandler(DepartamentoInactivoException.class)
    public ResponseEntity<ErrorResponse> handleDepartamentoInactivo(DepartamentoInactivoException exception) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
            .body(new ErrorResponse("CONFLICT", exception.getMessage(), List.of()));
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(NotFoundException exception) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(new ErrorResponse("NOT_FOUND", exception.getMessage(), List.of()));
    }

    @ExceptionHandler(AuthenticationFailedException.class)
    public ResponseEntity<ErrorResponse> handleAuthenticationFailed(AuthenticationFailedException exception) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
            .body(new ErrorResponse("AUTHENTICATION_FAILED", "Credenciales invalidas o sesion no valida", List.of()));
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ErrorResponse> handleUnauthorized(UnauthorizedException exception) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
            .body(new ErrorResponse("UNAUTHORIZED", exception.getMessage(), List.of()));
    }

    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<ErrorResponse> handleForbidden(ForbiddenException exception) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
            .body(new ErrorResponse("FORBIDDEN", exception.getMessage(), List.of()));
    }

    @ExceptionHandler(SessionExpiredException.class)
    public ResponseEntity<ErrorResponse> handleSessionExpired(SessionExpiredException exception) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
            .body(new ErrorResponse("SESSION_EXPIRED", exception.getMessage(), List.of()));
    }

    @ExceptionHandler(AccountLockedException.class)
    public ResponseEntity<ErrorResponse> handleAccountLocked(AccountLockedException exception) {
        return ResponseEntity.status(HttpStatus.LOCKED)
            .body(new ErrorResponse(
                "ACCOUNT_LOCKED",
                exception.getMessage(),
                List.of("retry_after_seconds: " + exception.getRetryAfterSeconds())
            ));
    }
}
