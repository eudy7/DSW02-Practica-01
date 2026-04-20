# Tasks: CRUD de Departamentos Relacionado con Empleados

**Input**: Design documents from `/specs/003-crud-departamentos-empleados/`
**Prerequisites**: plan.md (required), spec.md (required for user stories), research.md, data-model.md, contracts/

**Tests**: No se incluyen tareas de pruebas automatizadas en esta lista porque la especificacion no exige enfoque TDD de forma explicita.

**Organization**: Tasks are grouped by user story to enable independent implementation and testing of each story.

## Format: `[ID] [P?] [Story] Description`

- **[P]**: Can run in parallel (different files, no dependencies)
- **[Story]**: Which user story this task belongs to (e.g., US1, US2, US3)
- Include exact file paths in descriptions

## Phase 1: Setup (Shared Infrastructure)

**Purpose**: Consolidar alcance funcional y artefactos base del feature

- [X] T001 Consolidar clarificaciones finales de alcance, permisos y concurrencia en specs/003-crud-departamentos-empleados/spec.md
- [X] T002 Alinear precondiciones y flujo de validacion manual del feature en specs/003-crud-departamentos-empleados/quickstart.md
- [X] T003 [P] Verificar contrato base de departamentos y asignaciones en specs/003-crud-departamentos-empleados/contracts/openapi.yaml
- [X] T004 [P] Registrar estado de checklist de requisitos previo a implementacion en specs/003-crud-departamentos-empleados/checklists/requirements.md

---

## Phase 2: Foundational (Blocking Prerequisites)

**Purpose**: Infraestructura y modelo base que bloquean todas las historias

**⚠️ CRITICAL**: Ninguna historia puede iniciar hasta completar esta fase

- [X] T005 Crear migracion de tabla de departamentos con baja logica e indice de unicidad activa en src/main/resources/db/migration/V4__create_departamento_table.sql
- [X] T006 Crear migracion de historial empleado-departamento con restriccion de una asignacion activa en src/main/resources/db/migration/V5__create_empleado_departamento_historial.sql
- [X] T007 [P] Implementar entidad Departamento con reglas de actualizacion/inactivacion en src/main/java/com/prcatica01/empleado/departamento/domain/Departamento.java
- [X] T008 [P] Implementar entidad HistorialAsignacionDepartamento con cierre de periodo en src/main/java/com/prcatica01/empleado/departamento/domain/HistorialAsignacionDepartamento.java
- [X] T009 [P] Implementar enum de filtro de estado de departamentos en src/main/java/com/prcatica01/empleado/departamento/domain/DepartamentoEstadoFiltro.java
- [X] T010 [P] Implementar repositorio de departamentos con validacion case-insensitive y trim en src/main/java/com/prcatica01/empleado/departamento/infrastructure/DepartamentoRepository.java
- [X] T011 [P] Implementar repositorio de historial con consultas activas y conteo por departamento en src/main/java/com/prcatica01/empleado/departamento/infrastructure/HistorialAsignacionDepartamentoRepository.java
- [X] T012 [P] Implementar DTOs base de departamentos y asignaciones en src/main/java/com/prcatica01/empleado/departamento/api/dto/DepartamentoDtos.java
- [X] T013 [P] Implementar DTOs de asignacion/remocion de empleado-departamento en src/main/java/com/prcatica01/empleado/departamento/api/dto/EmpleadoDepartamentoDtos.java
- [X] T014 Incorporar excepciones de dominio de departamentos/asignaciones en src/main/java/com/prcatica01/empleado/shared/domain/DomainExceptions.java
- [X] T015 Mapear errores 400/404/409 consistentes del modulo en src/main/java/com/prcatica01/empleado/shared/api/GlobalExceptionHandler.java

**Checkpoint**: Fundacion lista; historias de usuario pueden implementarse

---

## Phase 3: User Story 1 - Registrar y consultar departamentos (Priority: P1) 🎯 MVP

**Goal**: Permitir alta y consulta de departamentos con listado filtrable por estado

**Independent Test**: Crear departamentos, consultarlos por id y verificar listado por defecto solo activos con opcion de incluir inactivos por filtro

### Implementation for User Story 1

- [X] T016 [US1] Implementar caso de uso de alta y consulta de departamentos en src/main/java/com/prcatica01/empleado/departamento/application/DepartamentoService.java
- [X] T017 [US1] Implementar normalizacion de nombre con trim y validacion de duplicidad activa en src/main/java/com/prcatica01/empleado/departamento/application/DepartamentoService.java
- [X] T018 [US1] Implementar endpoints GET/POST y filtro de estado en src/main/java/com/prcatica01/empleado/departamento/api/DepartamentoController.java
- [X] T019 [US1] Implementar mapping de respuesta resumen/detalle con total de activos en src/main/java/com/prcatica01/empleado/departamento/application/DepartamentoService.java
- [X] T020 [US1] Alinear contrato OpenAPI de alta/consulta/listado de departamentos en specs/003-crud-departamentos-empleados/contracts/openapi.yaml

**Checkpoint**: User Story 1 funcional y validable de forma independiente

---

## Phase 4: User Story 2 - Actualizar y eliminar departamentos (Priority: P2)

**Goal**: Permitir actualizacion e inactivacion de departamentos con bloqueo por empleados activos asociados

**Independent Test**: Actualizar un departamento existente y validar inactivacion permitida sin asociados activos y rechazada con asociados activos

### Implementation for User Story 2

- [X] T021 [US2] Implementar caso de uso de actualizacion de departamentos en src/main/java/com/prcatica01/empleado/departamento/application/DepartamentoService.java
- [X] T022 [US2] Implementar caso de uso de inactivacion con bloqueo por asociaciones activas en src/main/java/com/prcatica01/empleado/departamento/application/DepartamentoService.java
- [X] T023 [US2] Implementar endpoints PUT/DELETE de departamentos en src/main/java/com/prcatica01/empleado/departamento/api/DepartamentoController.java
- [X] T024 [US2] Aplicar politica de acceso para operaciones (cualquier usuario autenticado) en src/main/java/com/prcatica01/empleado/config/SecurityConfig.java
- [X] T025 [US2] Alinear contrato OpenAPI de update/delete y errores de conflicto en specs/003-crud-departamentos-empleados/contracts/openapi.yaml

**Checkpoint**: User Story 2 funcional y validable de forma independiente

---

## Phase 5: User Story 3 - Relacionar empleados con departamentos (Priority: P3)

**Goal**: Permitir asignar y reasignar empleados manteniendo historial y respuesta de conflicto en concurrencia

**Independent Test**: Asignar y reasignar un empleado, verificar cierre de periodo previo, una sola asignacion activa y respuesta 409 en conflicto concurrente

### Implementation for User Story 3

- [X] T026 [US3] Implementar caso de uso de asignacion y reasignacion historica en src/main/java/com/prcatica01/empleado/departamento/application/EmpleadoDepartamentoService.java
- [X] T027 [US3] Implementar caso de uso de remocion de asignacion activa en src/main/java/com/prcatica01/empleado/departamento/application/EmpleadoDepartamentoService.java
- [X] T028 [US3] Implementar deteccion de conflicto concurrente con respuesta 409 en reasignacion en src/main/java/com/prcatica01/empleado/departamento/application/EmpleadoDepartamentoService.java
- [X] T029 [US3] Implementar endpoints PUT/DELETE de asignacion por empleado en src/main/java/com/prcatica01/empleado/departamento/api/EmpleadoDepartamentoController.java
- [X] T030 [US3] Implementar endpoint GET de empleados activos por departamento en src/main/java/com/prcatica01/empleado/departamento/api/DepartamentoController.java
- [X] T031 [US3] Alinear contrato OpenAPI de asignacion/reasignacion/remocion y conflictos 409 en specs/003-crud-departamentos-empleados/contracts/openapi.yaml

**Checkpoint**: User Story 3 funcional y validable de forma independiente

---

## Phase 6: Polish & Cross-Cutting Concerns

**Purpose**: Cierre de coherencia transversal y entrega operativa

- [X] T032 [P] Actualizar documentacion de endpoints y reglas de negocio del modulo en README.md
- [X] T033 [P] Refinar guia quickstart con flujo completo login + CRUD + asignaciones + conflictos en specs/003-crud-departamentos-empleados/quickstart.md
- [X] T034 Verificar consistencia final de codigos de error del modulo en src/main/java/com/prcatica01/empleado/shared/api/GlobalExceptionHandler.java
- [X] T035 Ejecutar checklist final de requisitos y registrar cierre de feature en specs/003-crud-departamentos-empleados/checklists/requirements.md

---

## Dependencies & Execution Order

### Phase Dependencies

- **Phase 1 (Setup)**: Sin dependencias
- **Phase 2 (Foundational)**: Depende de Phase 1 y bloquea todas las historias
- **Phase 3 (US1)**: Depende de Phase 2
- **Phase 4 (US2)**: Depende de Phase 2 y reutiliza componentes de departamentos
- **Phase 5 (US3)**: Depende de Phase 2 y reutiliza componentes de departamentos y empleados
- **Phase 6 (Polish)**: Depende de historias objetivo completadas

### User Story Dependencies

- **US1 (P1)**: Primer incremento funcional (MVP)
- **US2 (P2)**: Requiere base de departamentos de US1 para editar/inactivar
- **US3 (P3)**: Requiere base de departamentos y empleados para asignaciones historicas

### Within Each User Story

- Reglas de dominio antes de cerrar endpoints
- Endpoints despues de servicios/repositorios
- Contrato OpenAPI actualizado dentro de la misma historia
- Validar criterio independiente de cada historia antes de avanzar

### Parallel Opportunities

- **Setup**: T003 y T004 pueden avanzar en paralelo con T001-T002
- **Foundational**: T007-T013 en paralelo tras T005-T006
- **US1**: T017 y T019 pueden avanzar en paralelo mientras T016 define flujo base
- **US2**: T022 y T024 pueden avanzar en paralelo despues de T021
- **US3**: T028 y T030 pueden avanzar en paralelo despues de T026
- **Polish**: T032 y T033 pueden ejecutarse en paralelo

---

## Parallel Example: User Story 1

```bash
Task: "T017 [US1] Implementar normalizacion de nombre y validacion de duplicidad activa"
Task: "T019 [US1] Implementar mapping de respuesta resumen/detalle"
```

## Parallel Example: User Story 2

```bash
Task: "T022 [US2] Implementar inactivacion con bloqueo por asociaciones activas"
Task: "T024 [US2] Aplicar politica de acceso para operaciones autenticadas"
```

## Parallel Example: User Story 3

```bash
Task: "T028 [US3] Implementar deteccion de conflicto concurrente con 409"
Task: "T030 [US3] Implementar endpoint GET de empleados activos por departamento"
```

---

## Implementation Strategy

### MVP First (User Story 1 Only)

1. Completar Phase 1 (Setup)
2. Completar Phase 2 (Foundational)
3. Completar Phase 3 (US1)
4. Validar registro y consulta de departamentos
5. Entregar MVP

### Incremental Delivery

1. Setup + Foundational
2. Entregar US1
3. Entregar US2
4. Entregar US3
5. Completar polish final

### Parallel Team Strategy

1. Equipo completo en Setup y Foundational
2. Luego dividir por historias:
   - Dev A: US1
   - Dev B: US2
   - Dev C: US3
3. Integracion y validacion final en Phase 6
