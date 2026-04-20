# Data Model: Login para CRUD de Empleados

## Entidad: Credencial de Acceso Global (`auth_credential`)

### Campos
- `id` (bigint, PK, requerido)
  - Reglas:
    - Valor fijo para la unica credencial global (por ejemplo `1`)
- `username` (string, requerido, unico)
  - Reglas:
    - No nulo, no vacio
    - Configurable por variables de entorno
- `password_hash` (string, requerido)
  - Reglas:
    - No nulo
    - Nunca almacenar contrasena en texto plano
- `is_active` (boolean, requerido)
  - Reglas:
    - Debe estar en `true` para autenticar
- `failed_attempts` (integer, requerido)
  - Reglas:
    - No negativo
    - Se resetea en login exitoso
- `first_failed_at` (timestamp with timezone, opcional)
  - Reglas:
    - Marca inicio de ventana de intentos fallidos
- `blocked_until` (timestamp with timezone, opcional)
  - Reglas:
    - Si `now < blocked_until`, la cuenta esta bloqueada
- `updated_at` (timestamp with timezone, requerido)
  - Reglas:
    - Se actualiza en cambios de credencial o estado

## Entidad: Sesion Autenticada (`auth_session`)

### Campos
- `token` (string, PK, requerido)
  - Reglas:
    - No nulo
    - Alta entropia
    - Longitud fija/recomendada para evitar colisiones
- `username` (string, requerido)
  - Reglas:
    - Debe corresponder a la credencial global activa
- `is_active` (boolean, requerido)
  - Reglas:
    - Solo una sesion activa por `username`
- `created_at` (timestamp with timezone, requerido)
  - Reglas:
    - Marca inicio de sesion
- `last_activity_at` (timestamp with timezone, requerido)
  - Reglas:
    - Se refresca en cada request autenticado valido
- `invalidated_at` (timestamp with timezone, opcional)
  - Reglas:
    - Se establece al expirar/cerrar/reemplazar sesion
- `invalidation_reason` (enum, opcional)
  - Reglas:
    - Valores: `LOGOUT`, `EXPIRED`, `REPLACED_BY_NEW_LOGIN`

## Entidad: Evento de Acceso (`auth_audit_event`)

### Campos
- `id` (bigserial, PK, requerido)
- `username` (string, requerido)
- `event_type` (enum/string, requerido)
  - Valores esperados:
    - `LOGIN_SUCCESS`
    - `LOGIN_FAILURE`
    - `ACCOUNT_LOCKED`
    - `LOGOUT`
    - `SESSION_REPLACED`
    - `SESSION_EXPIRED`
- `result_message` (string, requerido)
  - Reglas:
    - Mensaje operativo sin fuga de secretos
- `created_at` (timestamp with timezone, requerido)

## Relaciones
- `Credencial de Acceso Global (1) -> (N) Sesion Autenticada` a lo largo del tiempo.
- `Credencial de Acceso Global (1) -> (N) Evento de Acceso` a lo largo del tiempo.

## Indices y restricciones
- `auth_credential.username`: unico.
- `auth_session.token`: PK unica.
- Indice en `auth_session (username, is_active)` para validar sesion unica.
- Indice en `auth_session (is_active, last_activity_at)` para expiracion por inactividad.
- Indice en `auth_audit_event (created_at)` para consultas temporales.

## Transiciones de estado

### Credencial de Acceso Global
- `ACTIVA` -> `BLOQUEADA_TEMPORALMENTE` (umbral de intentos fallidos alcanzado)
- `BLOQUEADA_TEMPORALMENTE` -> `ACTIVA` (fin de bloqueo y/o login exitoso)

### Sesion Autenticada
- `ACTIVA` -> `INVALIDADA_LOGOUT` (logout explicito)
- `ACTIVA` -> `INVALIDADA_EXPIRACION` (timeout por inactividad)
- `ACTIVA` -> `INVALIDADA_REEMPLAZO` (nuevo login valido)

## Validaciones de operaciones
- Login:
  - Falla si credencial no coincide o esta bloqueada
  - Incrementa contador de intentos fallidos en fallo
  - Resetea contador en exito
  - Invalida sesion activa previa antes de crear nueva
- Validacion de sesion para CRUD:
  - Falla si token no existe o esta inactivo
  - Falla si sesion expiro por inactividad
  - Actualiza `last_activity_at` en request valido
- Logout:
  - Solo invalida token activo
  - Token invalidado no puede reutilizarse
- Auditoria:
  - Todo evento de autenticacion relevante genera registro con timestamp
