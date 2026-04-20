# Tasks: CRUD de Empleados

**Input**: Design documents from `/specs/001-cloud-empleados/`  
**Prerequisites**: plan.md (required), spec.md (required for user stories), research.md, data-model.md, contracts/

**Tests**: No se incluyen tareas de pruebas en esta fase porque no fueron solicitadas explícitamente en la especificación.

**Organization**: Tasks are grouped by user story to enable independent implementation and testing of each story.

## Format: `[ID] [P?] [Story] Description`

- **[P]**: Can run in parallel (different files, no dependencies)
- **[Story]**: Which user story this task belongs to (e.g., US1, US2, US3)
- Include exact file paths in descriptions

## Phase 1: Setup (Shared Infrastructure)

**Purpose**: Project initialization and base structure for Spring Boot backend

- [X] T001 Create Maven project descriptor with Spring Boot, Security, Validation, JPA, Flyway, PostgreSQL, and springdoc dependencies in pom.xml
- [X] T002 Create application bootstrap class in src/main/java/com/prcatica01/empleado/EmpleadoApplication.java
- [X] T003 [P] Create base environment configuration template in src/main/resources/application.yml
- [X] T004 [P] Create package structure placeholders with package-info.java in src/main/java/com/prcatica01/empleado/config/package-info.java

---

## Phase 2: Foundational (Blocking Prerequisites)

**Purpose**: Core infrastructure that MUST be complete before ANY user story can be implemented

**⚠️ CRITICAL**: No user story work can begin until this phase is complete

- [X] T005 Configure datasource, JPA, and Flyway properties for PostgreSQL env vars in src/main/resources/application.yml
- [X] T006 Create initial empleado table migration with composite PK (`clave_prefijo`,`clave_numero`) and `nombre`/`direccion`/`telefono` VARCHAR(100) in src/main/resources/db/migration/V1__create_empleado_table.sql
- [X] T028 [P] Create sequence or identity strategy for `clave_numero` autonumérico in src/main/resources/db/migration/V2__empleado_numero_sequence.sql
- [X] T007 [P] Implement Basic Auth security configuration (protect `/api/**`, allow health/readiness and swagger docs) in src/main/java/com/prcatica01/empleado/config/SecurityConfig.java
- [X] T008 [P] Implement OpenAPI metadata and Basic Auth scheme configuration in src/main/java/com/prcatica01/empleado/config/OpenApiConfig.java
- [X] T009 [P] Implement global API exception handler for 400/404/409 responses in src/main/java/com/prcatica01/empleado/shared/api/GlobalExceptionHandler.java
- [X] T010 Create shared error response DTO used by API handlers in src/main/java/com/prcatica01/empleado/shared/api/ErrorResponse.java

**Checkpoint**: Foundation ready - user story implementation can now begin

---

## Phase 3: User Story 1 - Registrar y consultar empleados (Priority: P1) 🎯 MVP

**Goal**: Permitir alta y consulta/listado de empleados con `clave` generada `EMP-<autonumérico>` y validaciones de campos

**Independent Test**: Crear un empleado válido sin enviar `clave`, verificar clave generada con formato `EMP-<autonumérico>`, consultarlo/listarlo y validar rechazo de campos >100 caracteres

### Implementation for User Story 1

- [X] T011 [P] [US1] Create empleado domain entity with composite key (`clavePrefijo`,`claveNumero`) and derived `clave` formatter in src/main/java/com/prcatica01/empleado/empleado/domain/Empleado.java
- [X] T012 [P] [US1] Create Spring Data repository for Empleado by composite PK and lookup by formatted clave in src/main/java/com/prcatica01/empleado/empleado/infrastructure/EmpleadoRepository.java
- [X] T013 [P] [US1] Create create/list/get request-response DTOs (create sin `clave`) with Bean Validation in src/main/java/com/prcatica01/empleado/empleado/api/dto/EmpleadoDtos.java
- [X] T014 [US1] Implement create/list/get use cases with generated `EMP-<autonumérico>` key, conflict and not-found rules in src/main/java/com/prcatica01/empleado/empleado/application/EmpleadoService.java
- [X] T015 [US1] Implement POST/GET list/GET by clave endpoints aligned with OpenAPI contract in src/main/java/com/prcatica01/empleado/empleado/api/EmpleadoController.java
- [X] T016 [US1] Map domain exceptions for invalid clave format, conflict and not found in src/main/java/com/prcatica01/empleado/shared/domain/DomainExceptions.java

**Checkpoint**: User Story 1 is fully functional and independently testable (MVP)

---

## Phase 4: User Story 2 - Actualizar datos de empleado (Priority: P2)

**Goal**: Permitir actualización de nombre, direccion y telefono sin cambiar la clave compuesta

**Independent Test**: Actualizar un empleado existente y confirmar persistencia de nuevos datos conservando la misma clave

### Implementation for User Story 2

- [X] T017 [P] [US2] Create update request DTO with max-length validations in src/main/java/com/prcatica01/empleado/empleado/api/dto/EmpleadoUpdateRequest.java
- [X] T018 [US2] Extend service with update use case preserving immutable (`clave_prefijo`,`clave_numero`) in src/main/java/com/prcatica01/empleado/empleado/application/EmpleadoService.java
- [X] T019 [US2] Add PUT `/api/empleados/{clave}` endpoint and response mapping in src/main/java/com/prcatica01/empleado/empleado/api/EmpleadoController.java
- [X] T020 [US2] Add update domain behavior helper for mutable fields in src/main/java/com/prcatica01/empleado/empleado/domain/Empleado.java

**Checkpoint**: User Stories 1 and 2 work independently and together

---

## Phase 5: User Story 3 - Eliminar empleado (Priority: P3)

**Goal**: Permitir eliminación de empleados por clave

**Independent Test**: Eliminar un empleado existente y verificar respuesta 204; volver a consultar y obtener no encontrado

### Implementation for User Story 3

- [X] T021 [US3] Extend service with delete use case and not-found handling in src/main/java/com/prcatica01/empleado/empleado/application/EmpleadoService.java
- [X] T022 [US3] Add DELETE `/api/empleados/{clave}` endpoint returning 204 in src/main/java/com/prcatica01/empleado/empleado/api/EmpleadoController.java
- [X] T023 [US3] Align OpenAPI delete operation responses and examples in specs/001-cloud-empleados/contracts/openapi.yaml

**Checkpoint**: All user stories are independently functional

---

## Phase 6: Polish & Cross-Cutting Concerns

**Purpose**: Improvements that affect multiple user stories

- [X] T024 [P] Document environment variables and startup flow in README.md
- [X] T025 [P] Add request examples for create/update/list/get/delete in specs/001-cloud-empleados/quickstart.md
- [X] T026 Verify Swagger endpoint metadata and operation summaries are synchronized with controller paths in src/main/java/com/prcatica01/empleado/config/OpenApiConfig.java
- [X] T027 Ensure migration and entity constraints remain consistent at 100-char limits and `EMP-<autonumérico>` key generation in src/main/resources/db/migration/V1__create_empleado_table.sql

---

## Dependencies & Execution Order

### Phase Dependencies

- **Setup (Phase 1)**: No dependencies
- **Foundational (Phase 2)**: Depends on Setup completion; blocks all user stories
- **User Stories (Phases 3-5)**: Depend on Foundational completion
- **Polish (Phase 6)**: Depends on desired user stories completion

### User Story Dependencies

- **US1 (P1)**: Starts after Foundational; no dependency on other user stories
- **US2 (P2)**: Starts after Foundational; builds on same service/controller introduced in US1
- **US3 (P3)**: Starts after Foundational; builds on same service/controller introduced in US1

### Within Each User Story

- Domain and DTO definitions before service logic
- Service logic before endpoint wiring
- Endpoint wiring before cross-file contract sync

### Parallel Opportunities

- Setup: T003 and T004 in parallel after T001/T002
- Foundational: T007, T008, T009 can run in parallel once T005 starts
- US1: T011, T012, T013 can run in parallel before T014
- US2: T017 can run in parallel while preparing service changes
- Polish: T024 and T025 can run in parallel

---

## Parallel Example: User Story 1

```bash
Task: "T011 [US1] Create empleado domain entity in src/main/java/com/prcatica01/empleado/empleado/domain/Empleado.java"
Task: "T012 [US1] Create repository in src/main/java/com/prcatica01/empleado/empleado/infrastructure/EmpleadoRepository.java"
Task: "T013 [US1] Create DTOs in src/main/java/com/prcatica01/empleado/empleado/api/dto/EmpleadoDtos.java"
```

## Parallel Example: User Story 2

```bash
Task: "T017 [US2] Create update DTO in src/main/java/com/prcatica01/empleado/empleado/api/dto/EmpleadoUpdateRequest.java"
Task: "T020 [US2] Add domain update helper in src/main/java/com/prcatica01/empleado/empleado/domain/Empleado.java"
```

## Parallel Example: User Story 3

```bash
Task: "T021 [US3] Extend delete use case in src/main/java/com/prcatica01/empleado/empleado/application/EmpleadoService.java"
Task: "T023 [US3] Align OpenAPI delete contract in specs/001-cloud-empleados/contracts/openapi.yaml"
```

---

## Implementation Strategy

### MVP First (User Story 1 Only)

1. Complete Phase 1 (Setup)
2. Complete Phase 2 (Foundational)
3. Complete Phase 3 (US1)
4. Validate create/list/get + validations + duplicate-key behavior
5. Demo/deploy MVP

### Incremental Delivery

1. Foundation ready (Phases 1-2)
2. Deliver US1 (core CRUD start)
3. Deliver US2 (update)
4. Deliver US3 (delete)
5. Finish with Phase 6 cross-cutting polish

### Parallel Team Strategy

1. Team aligns on Setup + Foundational
2. Then split:
   - Dev A: US2 extension
   - Dev B: US3 extension
   - Dev C: Documentation/Polish tasks
