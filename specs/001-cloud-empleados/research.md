# Phase 0 Research: CRUD de Empleados

## Decisión 1: Stack base del servicio
- Decision: Usar Spring Boot 3 con Java 17 como base del servicio REST.
- Rationale: Cumple la constitución del proyecto y ofrece integración nativa para web, seguridad, validaciones y persistencia.
- Alternatives considered:
  - Jakarta EE puro: más configuración y menor velocidad de entrega para este alcance.
  - Micronaut/Quarkus: válidos, pero no alineados con la constitución establecida.

## Decisión 2: Persistencia y migraciones
- Decision: PostgreSQL como única base de datos y Flyway para control de esquema.
- Rationale: PostgreSQL está mandatado por constitución y Flyway permite versionar cambios con rollback planificado.
- Alternatives considered:
  - Liquibase: viable, pero Flyway es más simple para migraciones lineales del MVP.
  - H2 en desarrollo: descartado por desalineación con comportamiento real de PostgreSQL.

## Decisión 3: Estrategia de seguridad
- Decision: Proteger endpoints CRUD con HTTP Basic Auth y exponer health/readiness sin autenticación estricta para operación.
- Rationale: Cumple con principio de seguridad y facilita pruebas iniciales con credenciales externas.
- Alternatives considered:
  - JWT/OAuth2: más robusto para federación, pero fuera de alcance del CRUD MVP.
  - Sin autenticación: viola constitución.

## Decisión 4: Documentación de API
- Decision: Definir contrato OpenAPI 3 y publicarlo por Swagger UI (springdoc-openapi).
- Rationale: Permite trazabilidad, pruebas manuales y sincronización de contrato con implementación.
- Alternatives considered:
  - Documentación manual en Markdown: propensa a divergencias.
  - Postman-only collection: útil como apoyo, pero no sustituye OpenAPI canónico.

## Decisión 5: Reglas de validación de campos
- Decision: `nombre`, `direccion` y `telefono` tendrán `maxLength = 100` y serán obligatorios; `clave` se genera automáticamente.
- Rationale: Requisito funcional explícito y validable en capa API + base de datos sin exponer generación manual de clave.
- Alternatives considered:
  - Longitudes diferentes por campo: contradice requisito.
  - Validar solo en DB: reduce calidad de error para consumidores API.

## Decisión 6: Diseño de `clave` compuesta
- Decision: Definir `clave` como valor derivado `EMP-<autonumérico>` construido desde una PK compuesta (`clave_prefijo`, `clave_numero`).
- Rationale: Cumple el requerimiento de prefijo fijo y consecutivo automático, con trazabilidad entre representación externa y estructura de persistencia.
- Alternatives considered:
  - Clave manual ingresada por usuario: aumenta riesgo de colisiones y errores.
  - PK simple string autogenerada: reduce claridad sobre prefijo fijo + componente numérico.

## Decisión 7: Estrategia de pruebas
- Decision: Usar pirámide con pruebas unitarias (reglas), integración (repositorio/servicio) y contrato API (OpenAPI + MockMvc), con PostgreSQL en Testcontainers.
- Rationale: Asegura comportamiento real de persistencia y cumplimiento de contrato sin depender del entorno local.
- Alternatives considered:
  - Solo pruebas unitarias: cobertura insuficiente de integración real con PostgreSQL.
  - Solo pruebas end-to-end: diagnóstico lento y costoso para MVP.
