CREATE TABLE auth_user (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(100) NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    role VARCHAR(20) NOT NULL,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    empleado_clave_prefijo VARCHAR(3) NULL,
    empleado_clave_numero BIGINT NULL,
    updated_at TIMESTAMPTZ NOT NULL,
    CONSTRAINT ck_auth_user_role CHECK (role IN ('ADMIN', 'EMPLEADO')),
    CONSTRAINT fk_auth_user_empleado FOREIGN KEY (empleado_clave_prefijo, empleado_clave_numero)
        REFERENCES empleado (clave_prefijo, clave_numero)
);

CREATE UNIQUE INDEX uq_auth_user_username_ci
    ON auth_user (lower(username));

CREATE UNIQUE INDEX uq_auth_user_empleado
    ON auth_user (empleado_clave_prefijo, empleado_clave_numero)
    WHERE empleado_clave_prefijo IS NOT NULL AND empleado_clave_numero IS NOT NULL;

INSERT INTO auth_user (username, password_hash, role, is_active, updated_at)
SELECT ac.username, ac.password_hash, 'ADMIN', ac.is_active, ac.updated_at
FROM auth_credential ac
WHERE NOT EXISTS (
    SELECT 1
    FROM auth_user au
    WHERE lower(au.username) = lower(ac.username)
);

ALTER TABLE auth_session
    ADD COLUMN user_id BIGINT NULL;

UPDATE auth_session s
SET user_id = au.id
FROM auth_user au
WHERE lower(s.username) = lower(au.username)
  AND s.user_id IS NULL;

ALTER TABLE auth_session
    ADD CONSTRAINT fk_auth_session_user
    FOREIGN KEY (user_id)
    REFERENCES auth_user (id);

CREATE INDEX idx_auth_session_user_active
    ON auth_session (user_id, is_active);
