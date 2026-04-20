# Quickstart: CRUD de Empleados

## Prerrequisitos
- Java 17
- Docker (entorno Manjaro compatible)
- Maven 3.9+

## 1) Levantar PostgreSQL en Docker

```bash
docker run --name empleado-postgres \
  -e POSTGRES_DB=empleadosdb \
  -e POSTGRES_USER=empleado_user \
  -e POSTGRES_PASSWORD=empleado_pass \
  -p 5432:5432 \
  -d postgres:16
```

## 2) Variables de entorno

Configurar variables para la aplicación:

- `DB_HOST=localhost`
- `DB_PORT=5432`
- `DB_NAME=empleadosdb`
- `DB_USER=empleado_user`
- `DB_PASSWORD=empleado_pass`
- `BASIC_AUTH_USER=admin`
- `BASIC_AUTH_PASSWORD=admin123`

## 3) Ejecutar la aplicación

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
- OpenAPI JSON: `GET /v3/api-docs`

## 5) Probar flujo CRUD mínimo
1. Crear empleado (`POST /api/empleados`) sin enviar `clave`
2. Tomar la `clave` generada con formato `EMP-<autonumérico>` de la respuesta
3. Consultar por clave (`GET /api/empleados/{clave}`)
4. Actualizar (`PUT /api/empleados/{clave}`)
5. Eliminar (`DELETE /api/empleados/{clave}`)

Todos los endpoints CRUD requieren HTTP Basic Auth.

## Ejemplos de requests

### Crear empleado

```bash
curl -u admin:admin123 -X POST http://localhost:8080/api/empleados \
  -H "Content-Type: application/json" \
  -d '{
    "nombre": "Daniel Perez",
    "direccion": "Calle Principal 123",
    "telefono": "555-1234"
  }'
```

### Consultar empleado

```bash
curl -u admin:admin123 http://localhost:8080/api/empleados/EMP-1
```

### Actualizar empleado

```bash
curl -u admin:admin123 -X PUT http://localhost:8080/api/empleados/EMP-1 \
  -H "Content-Type: application/json" \
  -d '{
    "nombre": "Daniel P.",
    "direccion": "Calle Secundaria 77",
    "telefono": "555-9999"
  }'
```

### Eliminar empleado

```bash
curl -u admin:admin123 -X DELETE http://localhost:8080/api/empleados/EMP-1
```
