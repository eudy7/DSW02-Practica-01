# Phase 0 Research: Login para CRUD de Empleados

## Decision 1: Modelo de identidad para acceso
- Decision: Mantener una sola credencial global configurable por entorno para operar el CRUD.
- Rationale: Esta decision ya fue aclarada en la especificacion y reduce complejidad operativa en la primera version.
- Alternatives considered:
  - Multiples usuarios con roles: mejora segregacion, pero excede el alcance del feature.
  - Integracion SSO/OAuth2: robusta para federacion, pero agrega complejidad y dependencia externa.

## Decision 2: Mecanismo de sesion para APIs protegidas
- Decision: Usar token bearer opaco (server-side) emitido por `/auth/login` y validado en cada request protegida.
- Rationale: Permite logout explicito, expiracion por inactividad y politica de sesion unica sin exponer datos sensibles en el token.
- Alternatives considered:
  - JWT stateless: complica invalidacion inmediata y sesion unica sin listas de revocacion.
  - Basic Auth permanente: no cumple logout ni invalidacion por reemplazo de sesion.

## Decision 3: Politica de vigencia de sesion
- Decision: Aplicar timeout por inactividad de 30 minutos con renovacion de ultima actividad en requests validos.
- Rationale: Cumple FR-004 y reduce sesiones abandonadas con acceso vigente.
- Alternatives considered:
  - Timeout absoluto fijo sin sliding: mas simple, pero menos alineado con uso operativo continuo.
  - Sesion sin expiracion: incrementa riesgo de acceso no autorizado por sesiones olvidadas.

## Decision 4: Control de intentos fallidos y bloqueo
- Decision: Bloquear por 15 minutos despues de 5 intentos fallidos dentro de 15 minutos; resetear contador en login exitoso.
- Rationale: Implementa directamente FR-007 y FR-009 con una regla clara y auditable.
- Alternatives considered:
  - Backoff exponencial: flexible, pero menos directo de explicar/operar en este alcance.
  - Umbral mayor o sin ventana: menor proteccion frente a fuerza bruta.

## Decision 5: Politica de sesion unica
- Decision: Al autenticar exitosamente, invalidar cualquier sesion activa previa de la credencial global con motivo `REPLACED_BY_NEW_LOGIN`.
- Rationale: Cumple FR-012 y evita sesiones concurrentes de la misma credencial.
- Alternatives considered:
  - Permitir sesiones concurrentes: contradice requisito funcional.
  - Rechazar nuevo login si existe sesion: afecta continuidad operativa.

## Decision 6: Auditoria de autenticacion
- Decision: Registrar eventos de login exitoso, login fallido, bloqueo de cuenta, logout, expiracion de sesion y reemplazo de sesion.
- Rationale: Cubre FR-008 con trazabilidad temporal y soporte a analisis operativo.
- Alternatives considered:
  - Solo logging en consola: dificulta consulta historica y correlacion.
  - Auditoria parcial (solo errores): no cubre visibilidad de flujo completo.

## Decision 7: Errores de autenticacion seguros
- Decision: Responder mensajes genericos para fallos de credenciales/sesion no valida, evitando revelar si fallo usuario o contrasena.
- Rationale: Cumple FR-006 y reduce filtracion de informacion sensible.
- Alternatives considered:
  - Mensajes detallados por causa: mejor UX, pero aumenta riesgo de enumeracion.
  - Respuesta vacia sin contexto: dificulta soporte y troubleshooting.

## Decision 8: Compatibilidad con constitucion de seguridad
- Decision: Mantener credencial externa y trazabilidad de seguridad, documentando excepcion controlada para migrar de Basic baseline a sesion bearer.
- Rationale: Requisitos del feature demandan capacidades (logout, timeout, sesion unica) que no se cubren con Basic puro.
- Alternatives considered:
  - Forzar Basic sin sesion: incumple requisitos funcionales del feature.
  - Posponer autenticacion: bloquea objetivo principal de acceso seguro al CRUD.
