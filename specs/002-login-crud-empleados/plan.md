# Implementation Plan: Login para CRUD de Empleados

**Branch**: `002-login-crud-empleados` | **Date**: 2026-03-26 | **Spec**: `/specs/002-login-crud-empleados/spec.md`
**Input**: Feature specification from `/specs/002-login-crud-empleados/spec.md`

**Note**: This template is filled in by the `/speckit.plan` command. See `.specify/templates/plan-template.md` for the execution workflow.

## Summary

Implementar autenticacion para el CRUD de empleados con una unica credencial
global configurable, sesion con expiracion por inactividad (30 minutos), cierre
de sesion explicito, bloqueo temporal por intentos fallidos (5 intentos en 15
minutos con bloqueo de 15 minutos), auditoria de eventos de acceso y politica
de sesion unica (nuevo login invalida sesion previa). El CRUD de empleados se
mantiene funcional, pero protegido por sesion valida.

## Technical Context

**Language/Version**: Java 17  
**Primary Dependencies**: Spring Boot 3 (Web, Validation, Data JPA, Security), springdoc-openapi (Swagger UI), PostgreSQL Driver  
**Storage**: PostgreSQL (tablas de autenticacion y auditoria controladas por Flyway)  
**Testing**: JUnit 5 + Spring Boot Test + MockMvc + Testcontainers PostgreSQL  
**Target Platform**: Servicio backend Linux container-friendly (desarrollo local con Docker)  
**Project Type**: web-service (backend API REST)  
**Performance Goals**: p95 < 2s para login valido y operaciones CRUD con sesion activa  
**Constraints**: credencial global configurable por entorno, timeout por inactividad 30m, lockout 5/15/15, sesion unica activa, errores de autenticacion genericos, auditoria obligatoria de eventos de acceso  
**Scale/Scope**: una sola identidad global para operacion interna; foco en seguridad de acceso al CRUD existente

## Constitution Check

*GATE: Must pass before Phase 0 research. Re-check after Phase 1 design.*

### Gate Review (Pre-Phase 0)

- **I. Backend-First Architecture**: PASS. El alcance se limita a API backend,
  seguridad, dominio y persistencia.
- **II. Spring Boot 3 + Java 17**: PASS. El plan mantiene versiones y stack
  constitucionales.
- **III. Security: HTTP Basic Auth**: PASS CON EXCEPCION JUSTIFICADA. La
  constitucion define Basic Auth como baseline, pero los requisitos FR-004,
  FR-005, FR-010 y FR-012 exigen sesion invalidable y expirable por
  inactividad. Se adopta sesion bearer con credencial global externa y se deja
  trazado en Complexity Tracking para seguimiento de gobernanza.
- **IV. Data: PostgreSQL via Docker (Manjaro)**: PASS. Se mantiene PostgreSQL y
  migraciones Flyway para nuevas tablas de auth.
- **V. API Documentation via Swagger (OpenAPI)**: PASS. El feature incluye
  contrato OpenAPI para login/logout/config y CRUD protegido.

**Resultado**: PASS (con excepcion justificada de seguridad documentada).

## Project Structure

### Documentation (this feature)

```text
specs/002-login-crud-empleados/
├── plan.md              # This file (/speckit.plan command output)
├── research.md          # Phase 0 output (/speckit.plan command)
├── data-model.md        # Phase 1 output (/speckit.plan command)
├── quickstart.md        # Phase 1 output (/speckit.plan command)
├── contracts/           # Phase 1 output (/speckit.plan command)
└── tasks.md             # Phase 2 output (/speckit.tasks command - NOT created by /speckit.plan)
```

### Source Code (repository root)

```text
src/
├── main/
│   ├── java/com/prcatica01/empleado/
│   │   ├── auth/
│   │   │   ├── api/
│   │   │   ├── application/
│   │   │   ├── domain/
│   │   │   └── infrastructure/
│   │   ├── config/
│   │   ├── empleado/
│   │   └── shared/
│   └── resources/
│       ├── application.yml
│       └── db/migration/
└── test/
    ├── java/com/prcatica01/empleado/
    │   ├── contract/
    │   ├── integration/
    │   └── unit/
    └── resources/
```

**Structure Decision**: Se mantiene un unico servicio backend Spring Boot con
modulo dedicado `auth` y reuso del modulo `empleado`, conservando separacion en
capas (`api/application/domain/infrastructure`) y convenciones existentes del
repositorio.

## Complexity Tracking

| Violation | Why Needed | Simpler Alternative Rejected Because |
|-----------|------------|-------------------------------------|
| III. Security: HTTP Basic Auth baseline | El feature requiere login/logout, expiracion por inactividad y sesion unica invalidable para cumplir FR-004, FR-005, FR-010 y FR-012 | Mantener Basic puro no permite invalidar sesiones ni cerrar sesion por token sin reintroducir credenciales en cada request |

## Constitution Check (Post-Phase 1 Design)

- **I. Backend-First Architecture**: PASS. Diseno y contratos permanecen en
  backend sin componentes UI.
- **II. Spring Boot 3 + Java 17**: PASS. Artefactos de diseno y quickstart
  preservan stack definido.
- **III. Security: HTTP Basic Auth**: PASS CON EXCEPCION JUSTIFICADA. Se
  documenta migracion controlada a sesion bearer por requerimientos de negocio y
  seguridad del feature.
- **IV. Data: PostgreSQL via Docker (Manjaro)**: PASS. Modelo y quickstart usan
  PostgreSQL dockerizado y migraciones versionadas.
- **V. API Documentation via Swagger (OpenAPI)**: PASS. El contrato OpenAPI
  incluye endpoints de autenticacion y CRUD protegido.

**Resultado**: PASS. Feature lista para Phase 2 (`/speckit.tasks`).
