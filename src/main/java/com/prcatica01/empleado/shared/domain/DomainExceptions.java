package com.prcatica01.empleado.shared.domain;

public final class DomainExceptions {

    private DomainExceptions() {
    }

    public static class NotFoundException extends RuntimeException {
        public NotFoundException(String message) {
            super(message);
        }
    }

    public static class ConflictException extends RuntimeException {
        public ConflictException(String message) {
            super(message);
        }
    }

    public static class InvalidClaveFormatException extends RuntimeException {
        public InvalidClaveFormatException(String message) {
            super(message);
        }
    }

    public static class AuthenticationFailedException extends RuntimeException {
        public AuthenticationFailedException(String message) {
            super(message);
        }
    }

    public static class AccountLockedException extends RuntimeException {
        private final long retryAfterSeconds;

        public AccountLockedException(String message, long retryAfterSeconds) {
            super(message);
            this.retryAfterSeconds = retryAfterSeconds;
        }

        public long getRetryAfterSeconds() {
            return retryAfterSeconds;
        }
    }

    public static class SessionExpiredException extends RuntimeException {
        public SessionExpiredException(String message) {
            super(message);
        }
    }

    public static class UnauthorizedException extends RuntimeException {
        public UnauthorizedException(String message) {
            super(message);
        }
    }

    public static class ForbiddenException extends RuntimeException {
        public ForbiddenException(String message) {
            super(message);
        }
    }

    public static class DepartamentoDuplicadoException extends RuntimeException {
        public DepartamentoDuplicadoException(String message) {
            super(message);
        }
    }

    public static class DepartamentoConEmpleadosActivosException extends RuntimeException {
        public DepartamentoConEmpleadosActivosException(String message) {
            super(message);
        }
    }

    public static class DepartamentoInactivoException extends RuntimeException {
        public DepartamentoInactivoException(String message) {
            super(message);
        }
    }
}
