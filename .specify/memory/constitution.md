<!--
Sync Impact Report
- Version change: 1.0.0 -> 2.0.0
- Modified principles: I. Backend-First Architecture -> I. Fullstack Modular Architecture; III. Security: HTTP Basic Auth -> III. Frontend Security: TLS + Email/Password Login
- Added sections: None
- Removed sections: None
- Templates requiring updates: ✅ reviewed .specify/templates/plan-template.md (no change needed); ✅ reviewed .specify/templates/spec-template.md (no change needed); ✅ reviewed .specify/templates/tasks-template.md (no change needed); ⚠ pending .specify/templates/commands/*.md (missing)
- Follow-up TODOs: TODO(RATIFICATION_DATE): confirm original ratification date
-->
# Prcatica01 Platform Constitution

## Core Principles

### I. Fullstack Modular Architecture
The project MUST keep clear separation between backend and frontend concerns.
Backend APIs, domain logic, and data access MUST remain layered and independently
testable. A frontend application is explicitly allowed and MUST be kept in a
separate module/folder from backend code. Rationale: enables fullstack delivery
without coupling UI and domain internals.

### II. Spring Boot 3 + Java 17
All application code MUST target Spring Boot 3 and Java 17. Dependencies and
configuration MUST be compatible with these versions. Rationale: aligns with the
agreed runtime and avoids mixed-version behavior.

### III. Frontend Security: TLS + Email/Password Login
If a frontend is implemented, it MUST be built with Angular 21 and served over
TLS in every non-local environment. The authentication flow exposed to end users
MUST support login with email and password. User credentials MUST be handled
through secure transport and MUST never be hardcoded in source code. Rationale:
establishes a minimum secure baseline for user-facing access.

### IV. Data: PostgreSQL via Docker (Manjaro)
PostgreSQL MUST be the only supported database. Local development MUST use a
Dockerized PostgreSQL instance suitable for Manjaro, with connection parameters
provided via environment configuration. Schema changes MUST be tracked via
migrations. Rationale: consistent local and CI data behavior.

### V. API Documentation via Swagger (OpenAPI)
All REST endpoints MUST be documented with OpenAPI and exposed through Swagger
UI. Documentation MUST be updated in the same change that modifies an endpoint.
Rationale: keeps API behavior discoverable and testable.

## Technology & Environment Constraints

- Runtime MUST use Java 17 with Spring Boot 3.
- Frontend, when present, MUST use Angular 21.
- Frontend traffic MUST use TLS outside local development.
- User login UX MUST support email and password credentials.
- Database MUST be PostgreSQL, started via Docker for local development on
	Manjaro.
- Configuration MUST be externalized through environment variables or secret
	stores, never committed as plain credentials.
- API documentation MUST be published via Swagger UI in the running service.

## Development Workflow & Quality Gates

- Every change MUST preserve the five core principles and update Swagger docs
	when endpoints change.
- Frontend changes MUST include evidence of TLS configuration in deployment
	configuration or environment docs.
- Authentication changes MUST include explicit login flow validation for
	email/password.
- Database schema changes MUST include migrations and a rollback plan.
- Configuration changes MUST include updated environment examples.
- Releases MUST document any security or auth changes explicitly.

## Governance

- This constitution supersedes all other practices in this repository.
- Amendments require a documented proposal, review approval, and a migration
	note if behavior changes.
- Versioning follows semantic versioning: MAJOR for breaking governance changes,
	MINOR for new or expanded principles/sections, PATCH for clarifications.
- Each plan/spec/task MUST include a constitution compliance check.
- Compliance review MUST confirm platform split (backend/frontend), TLS
	enforcement for frontend deployments, and login requirements.

**Version**: 2.0.0 | **Ratified**: TODO(RATIFICATION_DATE): confirm original ratification date | **Last Amended**: 2026-03-26
