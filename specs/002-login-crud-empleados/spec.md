# Feature Specification: Login para CRUD de Empleados

**Feature Branch**: `002-login-crud-empleados`  
**Created**: 2026-03-26  
**Status**: Draft  
**Input**: User description: "agrega un login con el usuario y una contraseña para el crud de empleados"

## Clarifications

### Session 2026-03-26

- Q: ¿Qué modelo de cuentas debe usar el login en esta primera version? → A: Una sola cuenta global (usuario y contraseña) configurable para acceder al CRUD.
- Q: Para el caso de intento simultaneo de login con la misma cuenta global, ¿qué comportamiento deseas? → A: Permitir solo una sesion y cerrar automaticamente la sesion anterior.

## User Scenarios & Testing *(mandatory)*

### User Story 1 - Iniciar sesion para acceder al modulo (Priority: P1)

Como usuario autorizado, quiero iniciar sesion con usuario y contrasena para poder entrar al modulo de empleados.

**Why this priority**: Sin autenticacion no hay control de acceso al CRUD, lo que genera riesgo directo sobre datos sensibles de empleados.

**Independent Test**: Puede probarse intentando iniciar sesion con credenciales validas e invalidas, verificando que solo las validas otorguen acceso.

**Acceptance Scenarios**:

1. **Given** un usuario activo con credenciales validas, **When** ingresa su usuario y contrasena, **Then** el sistema concede acceso al modulo de empleados.
2. **Given** un usuario o contrasena incorrectos, **When** intenta iniciar sesion, **Then** el sistema rechaza el acceso y muestra un mensaje de error generico.
3. **Given** que existe una sesion activa de la credencial global, **When** se inicia una nueva sesion valida, **Then** el sistema invalida la sesion anterior y mantiene activa solo la nueva.

---

### User Story 2 - Ejecutar CRUD con sesion valida (Priority: P2)

Como usuario autenticado, quiero crear, consultar, actualizar y eliminar empleados para operar el proceso de gestion de personal.

**Why this priority**: El objetivo de negocio es operar el CRUD; el login solo es valioso si habilita estas acciones de manera segura.

**Independent Test**: Puede probarse iniciando sesion y ejecutando cada operacion CRUD, verificando que sin sesion activa no se permita ninguna.

**Acceptance Scenarios**:

1. **Given** un usuario con sesion activa, **When** realiza una operacion CRUD valida, **Then** el sistema procesa la operacion normalmente.
2. **Given** un usuario sin sesion activa, **When** intenta ejecutar cualquier operacion CRUD, **Then** el sistema bloquea la accion y solicita autenticarse.

---

### User Story 3 - Proteger contra intentos fallidos repetidos (Priority: P3)

Como responsable del sistema, quiero limitar intentos fallidos de acceso para reducir riesgo de uso no autorizado.

**Why this priority**: Aumenta la seguridad del acceso, aunque depende de que el flujo base de autenticacion ya exista.

**Independent Test**: Puede probarse ingresando credenciales incorrectas de forma consecutiva hasta alcanzar el umbral de bloqueo temporal.

**Acceptance Scenarios**:

1. **Given** un usuario activo, **When** supera el numero maximo de intentos fallidos en la ventana definida, **Then** el sistema bloquea temporalmente nuevos intentos para ese usuario.
2. **Given** un usuario temporalmente bloqueado, **When** intenta iniciar sesion durante el periodo de bloqueo, **Then** el sistema rechaza el acceso e informa que debe esperar para reintentar.

### Edge Cases

- Intento de inicio de sesion con usuario o contrasena vacios.
- Intento de acceso al CRUD con una sesion expirada por inactividad.
- Intento simultaneo de inicio de sesion de la credencial global desde dos ubicaciones: la sesion anterior debe cerrarse automaticamente.
- Usuario desactivado que intenta autenticarse con credenciales correctas.
- Reintento de acceso inmediatamente despues de finalizar sesion voluntariamente.

## Requirements *(mandatory)*

### Functional Requirements

- **FR-001**: El sistema MUST requerir autenticacion con usuario y contrasena antes de permitir acceso al CRUD de empleados.
- **FR-002**: El sistema MUST validar que las credenciales correspondan a una unica cuenta global configurable antes de conceder acceso.
- **FR-003**: El sistema MUST impedir cualquier operacion de alta, consulta, actualizacion o eliminacion de empleados cuando no exista sesion autenticada valida.
- **FR-004**: El sistema MUST mantener una sesion autenticada por un periodo maximo de 30 minutos de inactividad.
- **FR-005**: El sistema MUST permitir cerrar sesion de forma explicita y, al hacerlo, revocar el acceso inmediato al CRUD.
- **FR-006**: El sistema MUST mostrar mensajes de error de autenticacion sin revelar si fallo el usuario o la contrasena.
- **FR-007**: El sistema MUST bloquear temporalmente una cuenta durante 15 minutos despues de 5 intentos fallidos dentro de 15 minutos.
- **FR-008**: El sistema MUST registrar los eventos de inicio de sesion exitoso, inicio de sesion fallido, bloqueo de cuenta y cierre de sesion con marca de tiempo.
- **FR-009**: El sistema MUST restablecer el contador de intentos fallidos cuando un inicio de sesion sea exitoso.
- **FR-010**: El sistema MUST permitir operar las funciones CRUD completas una vez autenticado, sin solicitar credenciales nuevamente mientras la sesion siga vigente.
- **FR-011**: El sistema MUST exponer la configuracion de la unica credencial de acceso sin requerir pantalla de alta, baja o administracion de usuarios.
- **FR-012**: El sistema MUST permitir solo una sesion activa para la credencial global y cerrar automaticamente cualquier sesion anterior al iniciar una nueva sesion valida.

### Key Entities *(include if feature involves data)*

- **Credencial de Acceso Global**: Representa la unica identidad que puede iniciar sesion, con atributos como usuario configurado, secreto de autenticacion, intentos fallidos acumulados y fin de bloqueo temporal.
- **Sesion Autenticada**: Representa el periodo de acceso autorizado, con atributos como usuario asociado, hora de inicio, ultima actividad, hora de expiracion y estado de vigencia.
- **Sesion Autenticada**: Representa el periodo de acceso autorizado, con atributos como usuario asociado, hora de inicio, ultima actividad, hora de expiracion, estado de vigencia y motivo de invalidacion (expiracion, cierre voluntario o reemplazo por nueva sesion).
- **Evento de Acceso**: Representa un registro de auditoria de autenticacion, con atributos como tipo de evento, usuario asociado, fecha/hora y resultado.

## Assumptions

- El alcance del feature se limita al control de acceso del CRUD de empleados con una unica credencial global configurable y no incluye gestion administrativa de usuarios.
- Existe exactamente una credencial inicial configurada para operar el sistema al momento del despliegue.
- Todos los usuarios autenticados tienen el mismo nivel de permisos sobre el CRUD de empleados en esta primera version.

## Success Criteria *(mandatory)*

### Measurable Outcomes

- **SC-001**: Al menos 95% de usuarios autorizados completa el inicio de sesion en menos de 30 segundos en su primer intento.
- **SC-002**: El 100% de intentos de operacion CRUD sin sesion valida es rechazado.
- **SC-003**: El 100% de cuentas que exceden el umbral de intentos fallidos queda temporalmente bloqueado de acuerdo con la politica definida.
- **SC-004**: Al menos 90% de usuarios de prueba completa una tarea end-to-end (iniciar sesion y registrar un empleado) sin asistencia externa.
