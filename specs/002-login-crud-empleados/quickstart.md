# Quickstart: Login para CRUD de Empleados

## Prerrequisitos
- Java 17
- Docker + Docker Compose
- Maven 3.9+

## 1) Levantar PostgreSQL (y opcion backend) con Docker Compose

Desde `docker/postgres/`:

```bash
docker compose up -d postgres
```

Si tambien quieres levantar backend por compose:

```bash
docker compose up -d
```

## 2) Variables de entorno

Configurar variables para base de datos y autenticacion:

- `DB_HOST=localhost`
- `DB_PORT=5432`
- `DB_NAME=empleadosdb`
- `DB_USER=empleado_user`
- `DB_PASSWORD=empleado_pass`
- `BASIC_AUTH_USER=admin`
- `BASIC_AUTH_PASSWORD=admin123`
- `AUTH_SESSION_TIMEOUT_MINUTES=30`
- `AUTH_MAX_FAILED_ATTEMPTS=5`
- `AUTH_FAILED_ATTEMPT_WINDOW_MINUTES=15`
- `AUTH_LOCK_MINUTES=15`

## 3) Ejecutar aplicacion local

```bash
SPRING_PROFILES_ACTIVE=dev mvn spring-boot:run
```

En Windows PowerShell:

```powershell
$env:SPRING_PROFILES_ACTIVE="dev"; mvn spring-boot:run
```

## 4) Verificar endpoints base
- Health: `GET /actuator/health`
- Swagger UI: `GET /swagger-ui/index.html`
- OpenAPI: `GET /v3/api-docs`

## 5) Flujo minimo de autenticacion + CRUD

1. Login por `POST /auth/login` con usuario/contrasena configurados.
2. Tomar token bearer de la respuesta.
3. Consumir CRUD de empleados con header `Authorization: Bearer <token>`.
4. Ejecutar `POST /auth/logout` con el mismo token.
5. Confirmar que el token invalidado ya no puede usar CRUD.

## 6) Escenarios de seguridad recomendados

### Bloqueo temporal por intentos fallidos
1. Enviar credenciales invalidas 5 veces dentro de 15 minutos.
2. Verificar respuesta de cuenta bloqueada.
3. Reintentar durante el bloqueo y validar rechazo.

### Sesion unica
1. Login exitoso y guardar `tokenA`.
2. Login exitoso nuevamente y guardar `tokenB`.
3. Verificar que `tokenA` deja de autorizar CRUD y `tokenB` queda activo.

## Ejemplos de requests

### Login

```bash
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "usuario": "admin",
    "contrasena": "admin123"
  }'
```

### Listar empleados con token

```bash
curl http://localhost:8080/api/empleados \
  -H "Authorization: Bearer <TOKEN>"
```

### Logout

```bash
curl -X POST http://localhost:8080/auth/logout \
  -H "Authorization: Bearer <TOKEN>"
```
