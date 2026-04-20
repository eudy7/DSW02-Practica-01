# empleado-service

Servicio backend Spring Boot 3 (Java 17) para CRUD de empleados con clave generada `EMP-<autonumerico>` y autenticacion por sesion Bearer.

## Alcance de plataforma

El repositorio permite arquitectura fullstack modular: backend y frontend en
modulos separados.

Si se implementa frontend, debe cumplir:

- Angular 21 como framework de frontend.
- TLS obligatorio fuera de entornos locales.
- Flujo de login con correo y contrasena.

## Variables de entorno

- `DB_HOST` (default: `localhost`)
- `DB_PORT` (default: `5432`)
- `DB_NAME` (default: `empleadosdb`)
- `DB_USER` (default: `empleado_user`)
- `DB_PASSWORD` (default: `empleado_pass`)
- `BASIC_AUTH_USER` (default: `admin`)
- `BASIC_AUTH_PASSWORD` (default: `admin123`)
- `AUTH_SESSION_TIMEOUT_MINUTES` (default: `30`)
- `AUTH_MAX_FAILED_ATTEMPTS` (default: `5`)
- `AUTH_FAILED_ATTEMPT_WINDOW_MINUTES` (default: `15`)
- `AUTH_LOCK_MINUTES` (default: `15`)

Variables para usuarios semilla con roles (feature de roles):

- `AUTH_SEED_ADMIN_USER` (default: `admin`)
- `AUTH_SEED_ADMIN_PASSWORD` (default: `admin123`)
- `AUTH_SEED_EMPLEADO_USER` (default: `empleado`) 
- `AUTH_SEED_EMPLEADO_PASSWORD` (default: `empleado123`)

## Arranque rápido

1. Levantar PostgreSQL (Docker) desde la carpeta `docker/postgres`:

```bash
cd docker/postgres
docker compose up -d
```

2. Configurar variables de entorno
3. Ejecutar en perfil `dev`:
```bash
SPRING_PROFILES_ACTIVE=dev mvn spring-boot:run
```

En Windows PowerShell:

```powershell
$env:SPRING_PROFILES_ACTIVE="dev"; mvn spring-boot:run
```

## Endpoints relevantes

- Swagger UI: `/swagger-ui/index.html`
- OpenAPI: `/v3/api-docs`
- Health: `/actuator/health`

## Modulo de departamentos

El servicio incluye CRUD de departamentos con baja logica y relacion historica
empleado-departamento.

Endpoints principales:

- `GET /api/departamentos?includeInactive=true|false`
- `POST /api/departamentos`
- `GET /api/departamentos/{departamentoId}`
- `PUT /api/departamentos/{departamentoId}`
- `DELETE /api/departamentos/{departamentoId}` (baja logica)
- `GET /api/departamentos/{departamentoId}/empleados`
- `PUT /api/empleados/{clave}/departamento`
- `DELETE /api/empleados/{clave}/departamento`

Reglas clave:

- Nombre de departamento unico entre activos (case-insensitive).
- Un empleado tiene cero o una asignacion activa.
- Reasignar cierra el periodo activo anterior y crea uno nuevo.
- No se puede inactivar un departamento con empleados activos asociados.

## Flujo de autenticacion

1. Login:

```http
POST /auth/login
Content-Type: application/json

{
	"usuario": "admin",
	"contrasena": "admin123"
}
```

2. Usar el token retornado en cada request protegida:

```http
Authorization: Bearer <token>
```

3. Cerrar sesion:

```http
POST /auth/logout
Authorization: Bearer <token>
```

## Roles y permisos (en progreso)

## Roles y permisos

- `ADMIN`: CRUD completo de empleados y departamentos, y asignacion/remocion de empleado-departamento.
- `EMPLEADO`: lectura del perfil propio completo y lectura limitada de terceros (`clave`, `nombre`, `departamento actual`).
- Endpoints de departamentos y movimiento manual (`/api/departamentos/**` y `/api/empleados/*/departamento`) estan restringidos a `ADMIN`.
