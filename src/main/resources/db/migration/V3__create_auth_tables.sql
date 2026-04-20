CREATE TABLE auth_credential (
    id BIGINT PRIMARY KEY,
    username VARCHAR(100) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    failed_attempts INTEGER NOT NULL DEFAULT 0,
    first_failed_at TIMESTAMPTZ NULL,
    blocked_until TIMESTAMPTZ NULL,
    updated_at TIMESTAMPTZ NOT NULL
);

CREATE TABLE auth_session (
    token VARCHAR(128) PRIMARY KEY,
    username VARCHAR(100) NOT NULL,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMPTZ NOT NULL,
    last_activity_at TIMESTAMPTZ NOT NULL,
    invalidated_at TIMESTAMPTZ NULL,
    invalidation_reason VARCHAR(40) NULL
);

CREATE INDEX idx_auth_session_active_username
    ON auth_session (username, is_active);

CREATE INDEX idx_auth_session_active_last_activity
    ON auth_session (is_active, last_activity_at);

CREATE TABLE auth_audit_event (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(100) NOT NULL,
    event_type VARCHAR(40) NOT NULL,
    result_message VARCHAR(255) NOT NULL,
    created_at TIMESTAMPTZ NOT NULL
);

CREATE INDEX idx_auth_audit_event_created_at
    ON auth_audit_event (created_at);