# Data Model: CRUD de Empleados

## Entidad: Empleado

### Campos
- `clave_prefijo` (string, parte 1 de PK compuesta, requerido)
  - Reglas:
    - No nulo, no vacío
    - Valor fijo: `EMP`
- `clave_numero` (integer, parte 2 de PK compuesta, requerido)
  - Reglas:
    - No nulo
    - Entero positivo
    - Autonumérico al crear
    - Unicidad por combinación con `clave_prefijo`
- `clave` (string derivado para exposición externa)
  - Reglas:
    - Formato: `EMP-<autonumérico>`
    - Solo lectura para API
- `nombre` (string, requerido)
  - Reglas:
    - No nulo, no vacío
    - Longitud máxima: 100
- `direccion` (string, requerido)
  - Reglas:
    - No nulo, no vacío
    - Longitud máxima: 100
- `telefono` (string, requerido)
  - Reglas:
    - No nulo, no vacío
    - Longitud máxima: 100

## Relaciones
- No hay relaciones con otras entidades en este alcance.

## Índices y restricciones
- PK compuesta: (`clave_prefijo`, `clave_numero`)
- Restricción de unicidad lógica: `clave` derivada en formato `EMP-<autonumérico>`
- Restricciones de longitud: `nombre <= 100`, `direccion <= 100`, `telefono <= 100`

## Transiciones de estado
- `NoExiste` -> `Creado` (alta)
- `Creado` -> `Actualizado` (modificación de nombre/direccion/telefono)
- `Creado` -> `Eliminado` (baja)
- `Actualizado` -> `Eliminado` (baja)

## Validaciones de operaciones
- Create:
  - Genera `clave_prefijo = EMP`
  - Genera `clave_numero` autonumérico
  - Falla si ocurre colisión de PK compuesta
  - Falla si cualquier campo requerido está vacío
  - Falla si `nombre`, `direccion` o `telefono` exceden 100 caracteres
- Read:
  - Devuelve `No encontrado` si la `clave` no existe
  - Falla por validación si la clave no cumple formato `EMP-<autonumérico>`
- Update:
  - Falla si la `clave` no existe
  - Falla si se intenta cambiar `clave_prefijo` o `clave_numero`
  - Falla si `nombre`, `direccion` o `telefono` exceden 100 caracteres
- Delete:
  - Falla con `No encontrado` si la `clave` no existe
