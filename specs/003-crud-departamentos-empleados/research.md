# Phase 0 Research: CRUD de Departamentos Relacionado con Empleados

## Decision 1: Identidad y ciclo de vida de Departamento
- Decision: Usar `departamento.id` numerico autogenerado como PK tecnica, con `nombre` de negocio y bandera `activo` para baja logica.
- Rationale: Simplifica referencias desde historial y permite inactivar sin borrar registros, cumpliendo FR-005.
- Alternatives considered:
  - PK por nombre: complica cambios de nombre y referencias historicas.
  - Baja fisica: contradice requisitos de historial y trazabilidad.

## Decision 2: Unicidad de nombre en catalogo activo
- Decision: Garantizar unicidad case-insensitive de nombre para departamentos activos mediante indice unico sobre `lower(nombre)` filtrado por `activo = true`.
- Rationale: Cumple FR-002 y evita duplicados funcionales en catalogo operativo.
- Alternatives considered:
  - Unicidad global (activos e inactivos): dificulta reactivar o recrear catalogo historico.
  - Validacion solo en aplicacion: deja riesgo de carrera concurrente.

## Decision 3: Modelo de relacion empleado-departamento con historial
- Decision: Crear tabla `empleado_departamento_historial` con `fecha_inicio`, `fecha_fin`, FK a empleado y departamento, y regla de una sola asignacion activa por empleado (`fecha_fin IS NULL`).
- Rationale: Cumple FR-009 y FR-014, y soporta reasignacion cerrando periodo previo.
- Alternatives considered:
  - Columna `departamento_id` directa en `empleado`: no conserva historial.
  - Tabla de relacion sin fechas: no permite trazabilidad de periodos.

## Decision 4: Reasignacion y desvinculacion transaccional
- Decision: Implementar reasignacion como una transaccion: cerrar asignacion activa previa (`fecha_fin=now`) y crear nueva fila activa; para desvincular, solo cerrar la activa.
- Rationale: Evita estados inconsistentes y asegura integridad temporal del historial.
- Alternatives considered:
  - Actualizar la misma fila activa: pierde trazabilidad.
  - Operaciones separadas no atomicas: riesgo de doble asignacion o huecos.

## Decision 5: Regla de baja de departamento con empleados asociados
- Decision: Bloquear inactivacion cuando existan asignaciones activas hacia el departamento y responder conflicto con mensaje correctivo.
- Rationale: Cumple FR-011 y evita departamentos inactivos con empleados aun vinculados.
- Alternatives considered:
  - Inactivar y desvincular automaticamente: oculta accion de negocio critica.
  - Permitir inactivar con empleados activos: rompe consistencia funcional.

## Decision 6: Contrato API para CRUD y asignaciones
- Decision: Exponer endpoints REST bajo `/api/departamentos` y `/api/empleados/{clave}/departamento`, con `bearerAuth` obligatorio y filtro de estado por query param.
- Rationale: Reutiliza convenciones de `EmpleadoController` y seguridad ya aplicada en `/api/**`.
- Alternatives considered:
  - Endpoints fuera de `/api`: rompe convencion actual.
  - Endpoint unico multiproposito para asignacion: reduce claridad de operaciones.

## Decision 7: Estrategia de persistencia y migraciones
- Decision: Agregar migraciones Flyway incrementales (`V4`, `V5`) con constraints, indices y FKs a `empleado(clave_prefijo, clave_numero)`.
- Rationale: Mantiene evolucion de esquema auditable y consistente con V1-V3.
- Alternatives considered:
  - DDL automatico por Hibernate: contradice `ddl-auto: validate` y practica actual.
  - Migracion unica grande: dificulta revision y rollback dirigido.

## Decision 8: Testing para reglas de negocio y contratos
- Decision: Cubrir unit tests de servicio (validaciones y transiciones), integration tests con PostgreSQL para constraints SQL, y contract tests de endpoints y codigos HTTP.
- Rationale: Las reglas clave (unicidad activa, una asignacion activa, bloqueo de baja) dependen de combinacion de logica de aplicacion y DB.
- Alternatives considered:
  - Solo unit tests: no valida constraints reales.
  - Solo integration tests: menor granularidad para diagnostico.
