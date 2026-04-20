CREATE TABLE empleado_departamento_historial (
    id BIGSERIAL PRIMARY KEY,
    empleado_clave_prefijo VARCHAR(3) NOT NULL,
    empleado_clave_numero BIGINT NOT NULL,
    departamento_id BIGINT NOT NULL,
    fecha_inicio TIMESTAMPTZ NOT NULL,
    fecha_fin TIMESTAMPTZ NULL,
    created_at TIMESTAMPTZ NOT NULL,
    CONSTRAINT fk_historial_empleado
        FOREIGN KEY (empleado_clave_prefijo, empleado_clave_numero)
        REFERENCES empleado (clave_prefijo, clave_numero),
    CONSTRAINT fk_historial_departamento
        FOREIGN KEY (departamento_id)
        REFERENCES departamento (id),
    CONSTRAINT chk_historial_fechas
        CHECK (fecha_fin IS NULL OR fecha_fin > fecha_inicio)
);

CREATE UNIQUE INDEX uq_historial_empleado_activo
    ON empleado_departamento_historial (empleado_clave_prefijo, empleado_clave_numero)
    WHERE fecha_fin IS NULL;

CREATE INDEX idx_historial_departamento_activo
    ON empleado_departamento_historial (departamento_id)
    WHERE fecha_fin IS NULL;

CREATE INDEX idx_historial_empleado_fechas
    ON empleado_departamento_historial (empleado_clave_prefijo, empleado_clave_numero, fecha_inicio DESC);
