# Quickstart: CRUD de Departamentos Relacionado con Empleados

## Prerrequisitos
- Java 17
- Docker + Docker Compose
- Maven 3.9+ (o build por Docker Compose si `mvn` no esta disponible)

## 1) Levantar PostgreSQL para desarrollo

Desde `docker/postgres/`:

```bash
docker compose up -d postgres
```

Opcional: levantar tambien backend por compose.

```bash
docker compose up -d
```

## 2) Configurar variables de entorno

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

## 3) Aplicar migraciones nuevas del feature

Migraciones esperadas para este feature:
- `V4__create_departamento_table.sql`
- `V5__create_empleado_departamento_historial.sql`

Si ejecutas la app con Flyway habilitado, se aplican automaticamente al iniciar.

## 4) Ejecutar aplicacion

Linux/macOS:

```bash
SPRING_PROFILES_ACTIVE=dev mvn spring-boot:run
```

Windows PowerShell:

```powershell
$env:SPRING_PROFILES_ACTIVE="dev"; mvn spring-boot:run
```

Alternativa cuando no hay Maven local:

```bash
cd docker/postgres
docker compose build empleado-service
docker compose up -d empleado-service
```

## 5) Verificar endpoints base
- Health: `GET /actuator/health`
- Swagger UI: `GET /swagger-ui/index.html`
- OpenAPI: `GET /v3/api-docs`

## 6) Flujo minimo de departamentos

1. Login: `POST /auth/login` y obtener token bearer.
2. Crear departamento: `POST /api/departamentos`.
3. Listar activos: `GET /api/departamentos`.
4. Asignar empleado: `PUT /api/empleados/{clave}/departamento`.
5. Consultar detalle departamento: `GET /api/departamentos/{id}` y validar `totalEmpleadosActivos`.
6. Reasignar empleado a otro departamento y confirmar historial.
7. Intentar inactivar departamento con empleados activos y validar `409`.
8. Desvincular/reasignar empleados e inactivar departamento exitosamente (`DELETE` logico).

## 7) Casos recomendados de validacion

### Unicidad de nombre activo
1. Crear `Ventas`.
2. Intentar crear `ventas`.
3. Verificar respuesta `409 CONFLICT`.

### Filtro de estado
1. Inactivar un departamento sin empleados.
2. Consultar `GET /api/departamentos` (no debe incluir inactivos).
3. Consultar `GET /api/departamentos?estado=TODOS` (debe incluir activos e inactivos).

### Una sola asignacion activa por empleado
1. Asignar empleado `EMP-1` al departamento A.
2. Reasignar `EMP-1` al departamento B.
3. Verificar que solo existe una fila activa (`fecha_fin IS NULL`) para `EMP-1`.

## 8) Ejemplos de requests

### Crear departamento

```bash
curl -X POST http://localhost:8080/api/departamentos \
  -H "Authorization: Bearer <TOKEN>" \
  -H "Content-Type: application/json" \
  -d '{
    "nombre": "Ventas",
    "descripcion": "Equipo comercial"
  }'
```

### Asignar empleado a departamento

```bash
curl -X PUT http://localhost:8080/api/empleados/EMP-1/departamento \
  -H "Authorization: Bearer <TOKEN>" \
  -H "Content-Type: application/json" \
  -d '{
    "departamentoId": 1
  }'
```

### Inactivar departamento

```bash
curl -X DELETE http://localhost:8080/api/departamentos/1 \
  -H "Authorization: Bearer <TOKEN>"
```
