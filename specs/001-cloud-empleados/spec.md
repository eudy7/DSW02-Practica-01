# Feature Specification: CRUD de Empleados

**Feature Branch**: `001-cloud-empleados`  
**Created**: 2026-02-25  
**Status**: Draft  
**Input**: User description: "crea un crud con empleado con los campos clave, nombre, direccion, y telefono. done clave sea el PK y nombre, direccion y telefono sea de 100 caracteres"

## User Scenarios & Testing *(mandatory)*

### User Story 1 - Registrar y consultar empleados (Priority: P1)

Como usuario de operaciones, quiero registrar un empleado y consultar su información para tener un padrón confiable y accesible.

**Why this priority**: Sin alta y consulta no existe valor funcional mínimo del módulo.

**Independent Test**: Puede probarse creando un empleado válido y recuperándolo por su clave, confirmando que los datos guardados coinciden exactamente.

**Acceptance Scenarios**:

1. **Given** que no existe el siguiente consecutivo para empleados, **When** el usuario registra un empleado con nombre, dirección y teléfono válidos, **Then** el sistema genera una clave con formato `EMP-<autonumérico>` y guarda el empleado.
2. **Given** que existe un empleado con clave `EMP-1`, **When** el usuario lo consulta por clave, **Then** el sistema devuelve su clave, nombre, dirección y teléfono.

---

### User Story 2 - Actualizar datos de empleado (Priority: P2)

Como usuario de operaciones, quiero actualizar nombre, dirección o teléfono de un empleado para mantener datos vigentes.

**Why this priority**: Mantener la calidad del dato reduce errores operativos y retrabajo.

**Independent Test**: Puede probarse actualizando un empleado existente y validando que la consulta posterior refleje solo los campos modificados.

**Acceptance Scenarios**:

1. **Given** que existe un empleado con clave `EMP-1`, **When** el usuario actualiza nombre, dirección o teléfono con valores válidos, **Then** el sistema conserva la misma clave y aplica los nuevos datos.

---

### User Story 3 - Eliminar empleado (Priority: P3)

Como usuario de operaciones, quiero eliminar empleados que ya no deben permanecer activos para mantener un catálogo limpio.

**Why this priority**: Es importante para mantenimiento, pero depende de que ya exista registro y consulta.

**Independent Test**: Puede probarse eliminando un empleado existente y verificando que no pueda consultarse después.

**Acceptance Scenarios**:

1. **Given** que existe un empleado con clave `EMP-1`, **When** el usuario elimina ese empleado, **Then** la consulta por `EMP-1` indica que no existe.

---

### Edge Cases

- Intentar crear un empleado cuando hay desalineación entre el consecutivo y la PK compuesta (colisión de clave generada).
- Intentar consultar, actualizar o eliminar una clave con formato inválido (por ejemplo `EM-1` o `EMP-ABC`).
- Intentar registrar o actualizar `nombre`, `direccion` o `telefono` con más de 100 caracteres.
- Intentar registrar con campos obligatorios vacíos.
- Consultar, actualizar o eliminar una clave inexistente.

## Requirements *(mandatory)*

### Functional Requirements

- **FR-001**: El sistema MUST permitir crear un empleado con los campos `nombre`, `direccion` y `telefono`.
- **FR-002**: El sistema MUST generar automáticamente la `clave` del empleado con el formato `EMP-<autonumérico>` al momento de creación.
- **FR-003**: El sistema MUST usar una PK compuesta por `clave_prefijo` y `clave_numero`, donde `clave_prefijo` es `EMP` y `clave_numero` es autonumérico.
- **FR-004**: El sistema MUST permitir consultar un empleado por `clave` en formato `EMP-<autonumérico>`.
- **FR-005**: El sistema MUST permitir listar los empleados registrados.
- **FR-006**: El sistema MUST permitir actualizar `nombre`, `direccion` y `telefono` de un empleado existente sin cambiar su `clave`.
- **FR-007**: El sistema MUST permitir eliminar un empleado por `clave`.
- **FR-008**: El sistema MUST validar que `nombre`, `direccion` y `telefono` tengan longitud máxima de 100 caracteres cada uno.
- **FR-009**: El sistema MUST rechazar operaciones de creación o actualización cuando `nombre`, `direccion` o `telefono` excedan 100 caracteres, informando el motivo de validación.
- **FR-010**: El sistema MUST rechazar operaciones con `clave` que no cumplan el formato `EMP-<autonumérico>`.
- **FR-011**: El sistema MUST responder con resultado de “no encontrado” al consultar, actualizar o eliminar una `clave` inexistente.

### Key Entities *(include if feature involves data)*

- **Empleado**: Representa un registro de personal con PK compuesta (`clave_prefijo`, `clave_numero`), clave expuesta `EMP-<autonumérico>`, y atributos `nombre` (máx. 100), `direccion` (máx. 100) y `telefono` (máx. 100).

## Assumptions

- `clave` no es proporcionada por el usuario; se genera automáticamente como `EMP-<autonumérico>`.
- Solo `nombre`, `direccion` y `telefono` son obligatorios para crear un empleado.
- La longitud máxima de 100 caracteres aplica al valor completo de cada campo de texto indicado.

## Success Criteria *(mandatory)*

### Measurable Outcomes

- **SC-001**: El 100% de altas con datos válidos crea el empleado correctamente en un solo intento.
- **SC-002**: El 100% de operaciones con campos de más de 100 caracteres es rechazado con un mensaje claro de validación.
- **SC-003**: Al menos 95% de operaciones CRUD válidas finaliza en menos de 2 segundos en condiciones normales de operación.
- **SC-004**: El 100% de consultas por `clave` con formato `EMP-<autonumérico>` refleja el estado real del empleado (existente o no encontrado) inmediatamente después de crear, actualizar o eliminar.
