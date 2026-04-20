package com.prcatica01.empleado.auth.domain;

public enum AuthEventType {
    LOGIN_SUCCESS,
    LOGIN_FAILURE,
    ACCOUNT_LOCKED,
    LOGOUT,
    SESSION_REPLACED,
    SESSION_EXPIRED,
    ROLE_CHANGED
}
