CREATE TABLE empleado (
    clave_prefijo VARCHAR(3) NOT NULL,
    clave_numero BIGINT NOT NULL,
    nombre VARCHAR(100) NOT NULL,
    direccion VARCHAR(100) NOT NULL,
    telefono VARCHAR(100) NOT NULL,
    CONSTRAINT pk_empleado PRIMARY KEY (clave_prefijo, clave_numero),
    CONSTRAINT chk_empleado_prefijo CHECK (clave_prefijo = 'EMP'),
    CONSTRAINT chk_empleado_nombre_len CHECK (char_length(nombre) <= 100),
    CONSTRAINT chk_empleado_direccion_len CHECK (char_length(direccion) <= 100),
    CONSTRAINT chk_empleado_telefono_len CHECK (char_length(telefono) <= 100)
);
