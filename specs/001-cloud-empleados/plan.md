# Implementation Plan: CRUD de Empleados

**Branch**: `001-cloud-empleados` | **Date**: 2026-02-26 | **Spec**: `/specs/001-cloud-empleados/spec.md`
**Input**: Feature specification from `/specs/001-cloud-empleados/spec.md`

**Note**: This template is filled in by the `/speckit.plan` command. See `.specify/templates/plan-template.md` for the execution workflow.

## Summary

Implementar un CRUD completo para la entidad Empleado con `clave` generada en
formato `EMP-<autonumérico>` y PK compuesta (`clave_prefijo`, `clave_numero`),
y validación de longitud máxima de 100 caracteres para `nombre`,
`direccion` y `telefono`. El enfoque técnico es un servicio backend con Spring
Boot 3 y Java 17, persistencia en PostgreSQL (Docker local en Manjaro),
autenticación HTTP Basic, y contrato OpenAPI publicado en Swagger UI.

## Technical Context

**Language/Version**: Java 17  
**Primary Dependencies**: Spring Boot 3 (Web, Validation, Data JPA, Security), springdoc-openapi (Swagger UI), PostgreSQL Driver  
**Storage**: PostgreSQL (contenedor Docker en desarrollo local)  
**Testing**: JUnit 5 + Spring Boot Test + MockMvc + Testcontainers PostgreSQL  
**Target Platform**: Servicio backend Linux container-friendly (desarrollo en Manjaro)  
**Project Type**: web-service (backend API REST)  
**Performance Goals**: p95 < 2s para operaciones CRUD válidas bajo carga operativa normal  
**Constraints**: HTTP Basic obligatoria (excepto health/readiness), `nombre`/`direccion`/`telefono` <= 100 chars, `clave` en formato `EMP-<autonumérico>` con PK compuesta, documentación Swagger actualizada  
**Scale/Scope**: CRUD de una entidad (`Empleado`) para uso operativo interno (bajo a medio volumen)

## Constitution Check

*GATE: Must pass before Phase 0 research. Re-check after Phase 1 design.*

### Gate Review (Pre-Phase 0)

- **I. Backend-First Architecture**: PASS. El alcance define exclusivamente API
  backend y persistencia.
- **II. Spring Boot 3 + Java 17**: PASS. Contexto técnico fijado a estas
  versiones.
- **III. Security: HTTP Basic Auth**: PASS. Seguridad requerida para endpoints
  funcionales.
- **IV. Data: PostgreSQL via Docker (Manjaro)**: PASS. Persistencia y entorno
  local alineados.
- **V. API Documentation via Swagger (OpenAPI)**: PASS. Contratos OpenAPI y
  publicación Swagger incluidos en entregables.

**Resultado**: PASS. Se puede iniciar Fase 0.

## Project Structure

### Documentation (this feature)

```text
specs/001-cloud-empleados/
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
│   │   ├── config/
│   │   ├── empleado/
│   │   │   ├── api/
│   │   │   ├── application/
│   │   │   ├── domain/
│   │   │   └── infrastructure/
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

**Structure Decision**: Se adopta un único servicio backend Spring Boot con
estructura Maven estándar en `src/main` y `src/test`, organizado por módulo de
negocio (`empleado`) y capas (`api/application/domain/infrastructure`).

## Complexity Tracking

No se identifican violaciones de constitución; sección mantenida vacía por
trazabilidad.

| Violation | Why Needed | Simpler Alternative Rejected Because |
|-----------|------------|-------------------------------------|


## Constitution Check (Post-Phase 1 Design)

- **I. Backend-First Architecture**: PASS. Diseño y contratos sólo cubren API
  backend y persistencia.
- **II. Spring Boot 3 + Java 17**: PASS. Quickstart y contexto técnico
  mantienen esta base.
- **III. Security: HTTP Basic Auth**: PASS. Contrato define seguridad Basic
  para endpoints CRUD.
- **IV. Data: PostgreSQL via Docker (Manjaro)**: PASS. Modelo y quickstart usan
  PostgreSQL dockerizado con variables externas.
- **V. API Documentation via Swagger (OpenAPI)**: PASS. Se incluye contrato
  OpenAPI en `contracts/` y publicación esperada en Swagger UI.

**Resultado**: PASS. Feature lista para Phase 2 (`/speckit.tasks`).
