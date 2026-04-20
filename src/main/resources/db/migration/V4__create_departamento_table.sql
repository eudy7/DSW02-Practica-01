CREATE TABLE departamento (
    id BIGSERIAL PRIMARY KEY,
    nombre VARCHAR(120) NOT NULL,
    descripcion VARCHAR(255) NULL,
    activo BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMPTZ NOT NULL,
    updated_at TIMESTAMPTZ NOT NULL,
    CONSTRAINT chk_departamento_nombre_len CHECK (char_length(nombre) <= 120),
    CONSTRAINT chk_departamento_descripcion_len CHECK (descripcion IS NULL OR char_length(descripcion) <= 255)
);

CREATE UNIQUE INDEX uq_departamento_nombre_activo
    ON departamento (lower(nombre))
    WHERE activo = TRUE;
