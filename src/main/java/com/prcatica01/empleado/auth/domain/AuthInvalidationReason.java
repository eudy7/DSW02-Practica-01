package com.prcatica01.empleado.auth.domain;

public enum AuthInvalidationReason {
    LOGOUT,
    EXPIRED,
    REPLACED_BY_NEW_LOGIN,
    ROLE_CHANGED
}
