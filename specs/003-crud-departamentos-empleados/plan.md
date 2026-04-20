# Implementation Plan: CRUD de Departamentos Relacionado con Empleados

**Branch**: `003-crud-departamentos-empleados` | **Date**: 2026-03-26 | **Spec**: `/specs/003-crud-departamentos-empleados/spec.md`
**Input**: Feature specification from `/specs/003-crud-departamentos-empleados/spec.md`

**Note**: This template is filled in by the `/speckit.plan` command. See `.specify/templates/plan-template.md` for the execution workflow.

## Summary

Implementar CRUD de departamentos con baja logica, listado por estado, y relacion historica empleado-departamento con una sola asignacion activa por empleado. El diseno mantiene la arquitectura por capas existente (`api/application/domain/infrastructure`), utiliza PostgreSQL con migraciones Flyway para nuevas tablas e indices, y conserva autenticacion bearer para todos los endpoints bajo `/api/**`.

## Technical Context

**Language/Version**: Java 17  
**Primary Dependencies**: Spring Boot 3 (Web, Validation, Data JPA, Security), springdoc-openapi (Swagger UI), PostgreSQL Driver, Flyway  
**Storage**: PostgreSQL (nuevas tablas `departamento` y `empleado_departamento_historial` con migraciones Flyway)  
**Testing**: JUnit 5 + Spring Boot Test + MockMvc + Testcontainers PostgreSQL  
**Target Platform**: Servicio backend Linux container-friendly (desarrollo local con Docker)  
**Project Type**: web-service (backend API REST)  
**Performance Goals**: p95 < 2s para CRUD de departamentos y operaciones de asignacion/reasignacion  
**Constraints**: baja logica de departamentos, unicidad de nombre activo, maximo una asignacion activa por empleado, historial obligatorio de asignaciones, bearer auth requerida en todos los endpoints del feature  
**Scale/Scope**: catalogo interno de departamentos con relacion a empleados existentes y trazabilidad historica

## Constitution Check

*GATE: Must pass before Phase 0 research. Re-check after Phase 1 design.*

### Gate Review (Pre-Phase 0)

- **I. Backend-First Architecture**: PASS. El alcance es solo API, dominio, persistencia y migraciones.
- **II. Spring Boot 3 + Java 17**: PASS. El feature mantiene stack vigente sin cambios de runtime.
- **III. Security: HTTP Basic Auth**: PASS CON CONTEXTO EXISTENTE. El repositorio ya opera con sesion bearer para `/api/**`; el feature reutiliza ese mecanismo sin introducir un nuevo modelo de seguridad.
- **IV. Data: PostgreSQL via Docker (Manjaro)**: PASS. Se usa PostgreSQL y Flyway para evolucion de esquema.
- **V. API Documentation via Swagger (OpenAPI)**: PASS. Se entrega contrato OpenAPI de departamentos y relacion con empleados.

**Resultado**: PASS.

## Project Structure

### Documentation (this feature)

```text
specs/003-crud-departamentos-empleados/
├── plan.md
├── research.md
├── data-model.md
├── quickstart.md
├── contracts/
│   └── openapi.yaml
└── tasks.md
```

### Source Code (repository root)

```text
src/
├── main/
│   ├── java/com/prcatica01/empleado/
│   │   ├── auth/
│   │   ├── config/
│   │   ├── departamento/              # nuevo modulo del feature
│   │   │   ├── api/
│   │   │   ├── application/
│   │   │   ├── domain/
│   │   │   └── infrastructure/
│   │   ├── empleado/
│   │   └── shared/
│   └── resources/
│       ├── application.yml
│       └── db/migration/
│           ├── V1__create_empleado_table.sql
│           ├── V2__empleado_numero_sequence.sql
│           ├── V3__create_auth_tables.sql
│           ├── V4__create_departamento_table.sql                 # nuevo
│           └── V5__create_empleado_departamento_historial.sql    # nuevo
└── test/
    ├── java/com/prcatica01/empleado/
    │   ├── contract/
    │   ├── integration/
    │   └── unit/
    └── resources/
```

**Structure Decision**: Se mantiene un unico servicio Spring Boot y se agrega el bounded context `departamento` siguiendo convenciones ya usadas por `empleado` y `auth`, con migraciones versionadas en `db/migration`.

## Complexity Tracking

No additional constitution violations identified for this feature.

## Constitution Check (Post-Phase 1 Design)

- **I. Backend-First Architecture**: PASS. `research.md`, `data-model.md`, `quickstart.md` y `contracts/openapi.yaml` solo describen backend.
- **II. Spring Boot 3 + Java 17**: PASS. El diseno no introduce frameworks o versiones fuera del baseline.
- **III. Security: HTTP Basic Auth**: PASS CON CONTEXTO EXISTENTE. El contrato declara `bearerAuth` consistente con seguridad ya implementada en el repositorio.
- **IV. Data: PostgreSQL via Docker (Manjaro)**: PASS. Modelo y quickstart usan PostgreSQL dockerizado y Flyway.
- **V. API Documentation via Swagger (OpenAPI)**: PASS. El contrato cubre CRUD de departamentos y endpoints de asignacion.

**Resultado**: PASS. Feature lista para Phase 2 (`/speckit.tasks`).
