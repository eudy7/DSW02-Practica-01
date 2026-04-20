# Data Model: CRUD de Departamentos Relacionado con Empleados

## Entidad: Departamento (`departamento`)

### Campos
- `id` (bigserial, PK, requerido)
- `nombre` (varchar(120), requerido)
  - Reglas:
    - No nulo, no vacio, trim obligatorio
    - Unico entre departamentos activos (comparacion case-insensitive)
- `descripcion` (varchar(255), opcional)
  - Reglas:
    - Puede ser null
    - Si existe, se normaliza con trim
- `activo` (boolean, requerido)
  - Reglas:
    - `true` al crear
    - `false` al aplicar baja logica
- `created_at` (timestamptz, requerido)
- `updated_at` (timestamptz, requerido)

### Reglas de negocio
- No se permite crear/actualizar con nombre duplicado activo.
- La inactivacion se rechaza si existe al menos una asignacion activa en historial.
- Listado por defecto incluye solo `activo=true`; inactivos solo con filtro explicito.

## Entidad existente: Empleado (`empleado`)

### Clave y campos relevantes para relacion
- PK compuesta:
  - `clave_prefijo` (varchar(3), valor esperado `EMP`)
  - `clave_numero` (bigint)
- Otros campos existentes: `nombre`, `direccion`, `telefono`

### Regla de relacion
- Un empleado puede tener cero o una asignacion activa de departamento.

## Entidad: Historial de Asignacion Empleado-Departamento (`empleado_departamento_historial`)

### Campos
- `id` (bigserial, PK, requerido)
- `empleado_clave_prefijo` (varchar(3), requerido)
- `empleado_clave_numero` (bigint, requerido)
- `departamento_id` (bigint, requerido)
- `fecha_inicio` (timestamptz, requerido)
- `fecha_fin` (timestamptz, opcional)
- `created_at` (timestamptz, requerido)

### Reglas
- Una fila activa por empleado se define como `fecha_fin IS NULL`.
- Reasignar implica:
  1. Cerrar fila activa previa (`fecha_fin = now`) si existe.
  2. Insertar nueva fila activa para el nuevo departamento.
- Desvincular empleado de departamento implica cerrar fila activa sin crear nueva.
- `fecha_fin` debe ser mayor que `fecha_inicio` cuando no sea null.

## Relaciones
- `departamento (1) -> (N) empleado_departamento_historial`
- `empleado (1) -> (N) empleado_departamento_historial`
- Vista funcional:
  - `empleado (0..1) -> (1) departamento` para estado actual (derivado de la fila activa)

## Indices y restricciones sugeridas
- PK: `departamento(id)`
- PK: `empleado_departamento_historial(id)`
- FK:
  - `empleado_departamento_historial (empleado_clave_prefijo, empleado_clave_numero)` -> `empleado(clave_prefijo, clave_numero)`
  - `empleado_departamento_historial (departamento_id)` -> `departamento(id)`
- Unique parcial para nombre activo:
  - `unique index uq_departamento_nombre_activo on departamento (lower(nombre)) where activo = true`
- Unique parcial para una asignacion activa por empleado:
  - `unique index uq_historial_empleado_activo on empleado_departamento_historial (empleado_clave_prefijo, empleado_clave_numero) where fecha_fin is null`
- Indices de consulta:
  - `idx_historial_departamento_activo (departamento_id) where fecha_fin is null`
  - `idx_historial_empleado_fechas (empleado_clave_prefijo, empleado_clave_numero, fecha_inicio desc)`

## Transiciones de estado

### Departamento
- `ACTIVO` -> `INACTIVO`
  - Precondicion: sin empleados activos asociados.
- `INACTIVO` -> `ACTIVO` (opcional segun endpoint futuro)
  - Debe validar unicidad de nombre activo.

### Asignacion empleado-departamento
- `SIN_ASIGNACION` -> `ASIGNACION_ACTIVA`
- `ASIGNACION_ACTIVA` -> `ASIGNACION_ACTIVA (NUEVO_DEPARTAMENTO)`
  - Cierre de periodo previo + alta de nuevo periodo.
- `ASIGNACION_ACTIVA` -> `SIN_ASIGNACION`
  - Cierre de periodo (`fecha_fin`).

## Validaciones operativas (mapping a FR)
- FR-001/FR-004: nombre obligatorio, descripcion opcional, update de datos del departamento.
- FR-002: conflicto por nombre duplicado activo.
- FR-003/FR-013: listado filtrable por estado, default solo activos.
- FR-005/FR-011: baja logica bloqueada con empleados asociados activos.
- FR-007/FR-008/FR-009: asignar y reasignar garantizando una sola activa.
- FR-010: detalle de departamento incluye `totalEmpleadosActivos`.
- FR-014: historial persistente con `fecha_inicio` y `fecha_fin`.
