# PostgreSQL + Backend local (Docker)

Desde esta carpeta ejecuta:

```bash
docker compose up -d
```

Esto levantará:

- `postgres` en `localhost:5432`
- `empleado-service` en `localhost:8080`

Para detener:

```bash
docker compose down
```

Para ver logs:

```bash
docker compose logs -f
```

Verificación rápida:

```bash
curl http://localhost:8080/actuator/health
```
