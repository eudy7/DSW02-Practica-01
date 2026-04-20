# Tasks: Login para CRUD de Empleados

**Input**: Design documents from `/specs/002-login-crud-empleados/`
**Prerequisites**: plan.md (required), spec.md (required for user stories), research.md, data-model.md, contracts/

**Tests**: No se agregan tareas de pruebas automatizadas en este plan porque la especificacion no exige enfoque TDD ni suites nuevas obligatorias.

**Organization**: Tasks are grouped by user story to enable independent implementation and testing of each story.

## Format: `[ID] [P?] [Story] Description`

- **[P]**: Can run in parallel (different files, no dependencies)
- **[Story]**: Which user story this task belongs to (e.g., US1, US2, US3)
- Include exact file paths in descriptions

## Phase 1: Setup (Shared Infrastructure)

**Purpose**: Alinear contrato y configuracion base del feature

- [ ] T001 Alinear endpoints y esquemas de autenticacion/CRUD en specs/002-login-crud-empleados/contracts/openapi.yaml
- [ ] T002 Definir y validar propiedades de autenticacion por entorno en src/main/resources/application.yml
- [ ] T003 [P] Documentar ejecucion local y variables de autenticacion en README.md

---

## Phase 2: Foundational (Blocking Prerequisites)

**Purpose**: Infraestructura base que debe estar lista antes de cualquier user story

**CRITICAL**: Ninguna historia puede iniciar hasta completar esta fase

- [ ] T004 Crear esquema de persistencia de credencial, sesion y auditoria en src/main/resources/db/migration/V3__create_auth_tables.sql
- [ ] T005 [P] Implementar entidad base de credencial global en src/main/java/com/prcatica01/empleado/auth/domain/AuthCredential.java
- [ ] T006 [P] Implementar repositorio base de credencial global en src/main/java/com/prcatica01/empleado/auth/infrastructure/AuthCredentialRepository.java
- [ ] T007 [P] Crear filtro bearer para validar sesion por token en src/main/java/com/prcatica01/empleado/config/AuthSessionFilter.java
- [ ] T008 Configurar cadena de seguridad stateless y reglas de acceso en src/main/java/com/prcatica01/empleado/config/SecurityConfig.java
- [ ] T009 [P] Agregar excepciones de dominio de autenticacion en src/main/java/com/prcatica01/empleado/shared/domain/DomainExceptions.java
- [ ] T010 Implementar mapeo de errores de autenticacion y sesion en src/main/java/com/prcatica01/empleado/shared/api/GlobalExceptionHandler.java
- [ ] T011 [P] Configurar esquema bearer en Swagger/OpenAPI en src/main/java/com/prcatica01/empleado/config/OpenApiConfig.java

**Checkpoint**: Fundacion lista, se puede avanzar por historias de usuario

---

## Phase 3: User Story 1 - Iniciar sesion para acceder al modulo (Priority: P1) MVP

**Goal**: Habilitar login/logout con una sola credencial global y una unica sesion activa

**Independent Test**: Con credenciales validas se obtiene token y acceso; con credenciales invalidas se rechaza; un segundo login invalida la sesion previa.

### Implementation for User Story 1

- [ ] T012 [P] [US1] Definir DTOs de login/logout/config en src/main/java/com/prcatica01/empleado/auth/api/dto/AuthDtos.java
- [ ] T013 [P] [US1] Inicializar la credencial global configurable al arranque en src/main/java/com/prcatica01/empleado/auth/application/AuthCredentialInitializer.java
- [ ] T014 [US1] Implementar login, logout e invalidacion de sesion previa por nuevo login en src/main/java/com/prcatica01/empleado/auth/application/AuthService.java
- [ ] T015 [US1] Exponer endpoints /auth/login, /auth/logout y /auth/config en src/main/java/com/prcatica01/empleado/auth/api/AuthController.java
- [ ] T016 [US1] Unificar mensajes genericos de error de autenticacion en src/main/java/com/prcatica01/empleado/shared/api/GlobalExceptionHandler.java

**Checkpoint**: US1 completa y validable de forma independiente

---

## Phase 4: User Story 2 - Ejecutar CRUD con sesion valida (Priority: P2)

**Goal**: Proteger el CRUD de empleados para que solo opere con sesion vigente

**Independent Test**: Con token valido se ejecuta CRUD completo; sin token o con token invalido todas las operaciones CRUD responden 401.

### Implementation for User Story 2

- [ ] T017 [P] [US2] Restringir rutas /api/empleados/** a sesion autenticada en src/main/java/com/prcatica01/empleado/config/SecurityConfig.java
- [ ] T018 [US2] Mantener comportamiento funcional del CRUD bajo seguridad de sesion en src/main/java/com/prcatica01/empleado/empleado/api/EmpleadoController.java
- [ ] T019 [US2] Actualizar contrato de seguridad y respuestas 401 del CRUD en specs/002-login-crud-empleados/contracts/openapi.yaml
- [ ] T020 [US2] Refrescar last_activity en cada request CRUD autenticado en src/main/java/com/prcatica01/empleado/config/AuthSessionFilter.java

**Checkpoint**: US2 completa y validable de forma independiente

---

## Phase 5: User Story 3 - Proteger contra intentos fallidos repetidos (Priority: P3)

**Goal**: Aplicar lockout temporal y auditoria completa de eventos de acceso

**Independent Test**: Tras 5 intentos fallidos en 15 minutos, el login queda bloqueado 15 minutos; se registran eventos de auditoria para exito, fallo, bloqueo y cierre.

### Implementation for User Story 3

- [ ] T021 [P] [US3] Implementar ventana de intentos fallidos y bloqueo temporal en src/main/java/com/prcatica01/empleado/auth/application/AuthService.java
- [ ] T022 [P] [US3] Persistir repositorio de eventos de auditoria en src/main/java/com/prcatica01/empleado/auth/infrastructure/AuthAuditEventRepository.java
- [ ] T023 [US3] Registrar eventos LOGIN_SUCCESS, LOGIN_FAILURE, ACCOUNT_LOCKED, LOGOUT, SESSION_REPLACED y SESSION_EXPIRED en src/main/java/com/prcatica01/empleado/auth/application/AuthService.java
- [ ] T024 [US3] Responder 423 cuando la cuenta este bloqueada en src/main/java/com/prcatica01/empleado/shared/api/GlobalExceptionHandler.java

**Checkpoint**: US3 completa y validable de forma independiente

---

## Phase 6: Polish & Cross-Cutting Concerns

**Purpose**: Cierre, hardening y validacion transversal

- [ ] T025 [P] Actualizar pasos de validacion funcional en specs/002-login-crud-empleados/quickstart.md
- [ ] T026 [P] Actualizar documentacion de configuracion y uso del login en README.md
- [ ] T027 Endurecer logging para evitar fuga de datos sensibles en src/main/java/com/prcatica01/empleado/auth/application/AuthService.java
- [ ] T028 Ejecutar checklist final de la feature y registrar estado en specs/002-login-crud-empleados/checklists/requirements.md

---

## Dependencies & Execution Order

### Phase Dependencies

- **Phase 1 (Setup)**: Sin dependencias
- **Phase 2 (Foundational)**: Depende de Phase 1 y bloquea todas las historias
- **Phase 3 (US1)**: Depende de Phase 2
- **Phase 4 (US2)**: Depende de Phase 2 y usa flujo de autenticacion de US1 para obtener token
- **Phase 5 (US3)**: Depende de Phase 2 y extiende el flujo de login de US1
- **Phase 6 (Polish)**: Depende de historias objetivo completadas

### User Story Dependencies

- **US1 (P1)**: Primera entrega funcional (MVP)
- **US2 (P2)**: Requiere base de seguridad lista y uso del token emitido por login
- **US3 (P3)**: Requiere login base y persistencia de auditoria

### Parallel Opportunities

- **Setup**: T003 en paralelo con T001-T002
- **Foundational**: T005, T006, T007, T009 y T011 en paralelo tras T004
- **US1**: T012 y T013 en paralelo; luego T014-T016
- **US2**: T017 y T019 en paralelo; luego T018-T020
- **US3**: T021 y T022 en paralelo; luego T023-T024
- **Polish**: T025 y T026 en paralelo; luego T027-T028

---

## Parallel Example: User Story 1

```bash
Task T012: Definir DTOs de login/logout/config
Task T013: Inicializar credencial global configurable
```

## Parallel Example: User Story 2

```bash
Task T017: Restringir rutas /api/empleados/** a sesion autenticada
Task T019: Actualizar contrato de seguridad y respuestas 401 del CRUD
```

## Parallel Example: User Story 3

```bash
Task T021: Implementar ventana de intentos fallidos y bloqueo temporal
Task T022: Persistir repositorio de eventos de auditoria
```

---

## Implementation Strategy

### MVP First (US1)

1. Completar Phase 1 (Setup)
2. Completar Phase 2 (Foundational)
3. Completar Phase 3 (US1)
4. Validar flujo login/logout/sesion unica
5. Entregar MVP

### Incremental Delivery

1. Setup + Foundational
2. Entregar US1
3. Entregar US2
4. Entregar US3
5. Ejecutar polish y validacion final

### Team Parallelization Strategy

1. Equipo completo en Phase 1 y Phase 2
2. Luego dividir por historia:
   - Dev A: US1
   - Dev B: US2
   - Dev C: US3
3. Integrar y validar en Phase 6

---

## Notes

- Todos los tasks siguen formato checklist con ID secuencial y path explicito.
- Los tasks con [P] no comparten archivo critico pendiente y pueden ejecutarse en paralelo.
- Cada historia incluye criterio de prueba independiente para validar incrementos.
