# Feature Specification: CRUD de Departamentos Relacionado con Empleados

**Feature Branch**: `003-crud-departamentos-empleados`  
**Created**: 2026-03-26  
**Status**: Draft  
**Input**: User description: "haz un crud de departamenetos relacionado a la tbla de empleados"

## Clarifications

### Session 2026-03-26

- Q: Al crear o gestionar empleados, ¿la asignacion a departamento debe ser obligatoria o opcional? → A: Opcional; un empleado puede quedar temporalmente sin departamento.
- Q: ¿La baja de departamentos debe ser fisica o logica? → A: Baja logica; el departamento se marca inactivo y se conserva el historial.
- Q: ¿Como debe comportarse el listado de departamentos respecto a inactivos? → A: Por defecto solo activos; inactivos solo con filtro explicito.
- Q: En reasignaciones de empleados, ¿se debe conservar historial o solo estado actual? → A: Conservar historial completo de asignaciones con una sola asignacion activa por empleado.
- Q: ¿Qué alcance de permisos aplica para operaciones de departamentos y asignaciones? → A: Todos los usuarios autenticados pueden ejecutar todas las operaciones.
- Q: ¿Como se determina duplicidad de nombre de departamento? → A: Duplicado por nombre ignorando mayusculas/minusculas y espacios extremos (trim).
- Q: ¿Como se maneja una reasignacion concurrente del mismo empleado? → A: Detectar conflicto concurrente y responder 409 para que el cliente reintente.

## User Scenarios & Testing *(mandatory)*

### User Story 1 - Registrar y consultar departamentos (Priority: P1)

Como usuario del sistema, quiero crear y consultar departamentos para tener una estructura organizativa base y poder clasificar empleados.

**Why this priority**: Sin el catalogo de departamentos no existe la base necesaria para relacionarlos con empleados.

**Independent Test**: Se valida creando departamentos, listandolos y consultando uno especifico, verificando que la informacion creada se conserva y se recupera correctamente.

**Acceptance Scenarios**:

1. **Given** que no existe un departamento con el mismo nombre, **When** el usuario registra un nuevo departamento con datos validos, **Then** el sistema guarda el departamento y lo muestra en la consulta.
2. **Given** que ya existe un departamento con ese nombre, **When** el usuario intenta registrar otro con el mismo nombre, **Then** el sistema rechaza la operacion y comunica el conflicto.
3. **Given** que existen departamentos registrados, **When** el usuario solicita el listado sin filtros, **Then** el sistema devuelve solo departamentos activos con su identificador y nombre.
4. **Given** que existen departamentos inactivos, **When** el usuario solicita el listado con filtro de estado, **Then** el sistema incluye inactivos segun el filtro aplicado.

---

### User Story 2 - Actualizar y eliminar departamentos (Priority: P2)

Como usuario del sistema, quiero actualizar y eliminar departamentos para mantener la estructura organizativa vigente y limpia.

**Why this priority**: Mantener datos actualizados evita errores operativos y reportes inconsistentes.

**Independent Test**: Se valida actualizando un departamento existente y eliminando uno sin empleados asociados, verificando que los cambios se reflejan en consultas posteriores.

**Acceptance Scenarios**:

1. **Given** un departamento existente, **When** el usuario actualiza su nombre o descripcion con datos validos, **Then** el sistema persiste el cambio y devuelve la version actualizada.
2. **Given** un departamento sin empleados asociados, **When** el usuario solicita eliminarlo, **Then** el sistema marca el departamento como inactivo y deja de mostrarlo en listados por defecto.
3. **Given** un departamento con empleados asociados, **When** el usuario intenta eliminarlo, **Then** el sistema rechaza la baja e informa que primero debe reasignar o desvincular a los empleados.

---

### User Story 3 - Relacionar empleados con departamentos (Priority: P3)

Como usuario del sistema, quiero vincular empleados a departamentos para consultar la distribucion del personal por area.

**Why this priority**: Esta relacion entrega valor operativo y analitico adicional una vez que el CRUD base de departamentos ya existe.

**Independent Test**: Se valida asignando empleados a un departamento y consultando el detalle del departamento para confirmar cantidad y listado de empleados asociados.

**Acceptance Scenarios**:

1. **Given** un empleado y un departamento existentes, **When** el usuario asigna el empleado al departamento, **Then** el sistema guarda la relacion y la muestra en consultas de ambos lados.
2. **Given** un empleado ya asignado a un departamento, **When** el usuario lo reasigna a otro departamento, **Then** el sistema actualiza la relacion y evita duplicidad de asignaciones activas.
3. **Given** un departamento con empleados asociados, **When** el usuario consulta su detalle, **Then** el sistema devuelve el departamento junto con la cantidad de empleados vinculados.

### Edge Cases

- Intento de crear un departamento con nombre vacio o solo espacios.
- Intento de crear o actualizar un departamento con un nombre que ya existe.
- Intento de consultar, actualizar o eliminar un departamento inexistente.
- Intento de eliminar un departamento que todavia tiene empleados vinculados.
- Intento de asignar un empleado inexistente a un departamento existente.
- Intento de asignar un empleado existente a un departamento inexistente.
- Intento de consultar departamentos inactivos sin aplicar filtro de estado.
- Reasignacion de empleado sin cerrar correctamente la asignacion activa anterior.
- Reasignaciones concurrentes del mismo empleado que compiten por la misma asignacion activa.

## Requirements *(mandatory)*

### Functional Requirements

- **FR-001**: El sistema MUST permitir crear un departamento con nombre obligatorio y descripcion opcional.
- **FR-002**: El sistema MUST impedir la creacion de departamentos con nombre duplicado, evaluando duplicidad de forma case-insensitive y aplicando trim de espacios extremos.
- **FR-003**: El sistema MUST permitir listar departamentos activos por defecto y consultar un departamento especifico por su identificador.
- **FR-013**: El sistema MUST permitir incluir departamentos inactivos mediante un filtro explicito de estado en el listado.
- **FR-004**: El sistema MUST permitir actualizar los datos de un departamento existente.
- **FR-005**: El sistema MUST aplicar baja logica (inactivar) de un departamento solo cuando no tenga empleados asociados.
- **FR-006**: El sistema MUST devolver un error claro cuando se intente operar sobre un departamento inexistente.
- **FR-007**: El sistema MUST permitir asociar un empleado existente a un departamento existente.
- **FR-008**: El sistema MUST permitir cambiar la asociacion de un empleado de un departamento a otro.
- **FR-009**: El sistema MUST garantizar que un empleado tenga como maximo un departamento activo asociado.
- **FR-010**: El sistema MUST exponer en la consulta de departamento la cantidad total de empleados asociados.
- **FR-011**: El sistema MUST rechazar la baja de un departamento con empleados asociados e indicar la accion correctiva.
- **FR-012**: El sistema MUST requerir sesion autenticada valida para todas las operaciones del CRUD de departamentos y de relacion con empleados.
- **FR-014**: El sistema MUST conservar historial de asignaciones empleado-departamento, registrando fecha de inicio y fin por cada periodo de vinculacion.
- **FR-015**: El sistema MUST permitir que cualquier usuario autenticado ejecute todas las operaciones de departamentos y asignaciones, sin diferenciacion de roles en este alcance.
- **FR-016**: El sistema MUST detectar conflictos de reasignacion concurrente para el mismo empleado y responder conflicto (409), evitando sobreescritura silenciosa.

### Key Entities *(include if feature involves data)*

- **Departamento**: Representa un area organizativa con identificador unico, nombre unico, descripcion y estado de vigencia.
- **Empleado**: Representa un miembro del personal que puede estar asociado a un departamento.
- **Relacion Empleado-Departamento**: Representa la vinculacion historica entre un empleado y un departamento, incluyendo fecha de inicio, fecha de fin y estado de asignacion activa.

## Assumptions

- El sistema ya cuenta con CRUD de empleados y autenticacion operativa.
- Cada empleado puede tener cero o un departamento activo a la vez.
- La baja de departamento es logica; el registro se conserva para historial y reportes, y se rechaza mientras haya asociaciones activas.
- Las reasignaciones cierran la vinculacion activa previa y crean una nueva vinculacion para mantener trazabilidad historica.

## Success Criteria *(mandatory)*

### Measurable Outcomes

- **SC-001**: Al menos 95% de registros de departamentos validos se completa en menos de 1 minuto por operacion.
- **SC-002**: El 100% de intentos de eliminar departamentos con empleados asociados es bloqueado correctamente.
- **SC-003**: Al menos 95% de consultas de departamentos devuelve resultados en menos de 2 segundos bajo carga operativa normal.
- **SC-004**: Al menos 90% de usuarios de prueba completa el flujo crear departamento, asociar empleado y consultar detalle sin asistencia.
