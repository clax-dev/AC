# PoC S&S Ibérica: migración de datos legacy y seguridad RBAC

**Autor:** Guillermo Prat Astillero · DAM2 · Acceso a Datos

## 1. Diagrama E2E (Fichero → BD → API → Pantalla)

```text
 pedidos_clientes_poc.xlsx
        │  exportado a CSV (texto plano, separador ;)
        ▼
 datos/clientes.csv   datos/pedidos.csv
        │
        ▼
 LectorCsv  (Java NIO: Files.lines → skip cabecera → split ";")
        │  List<String[]>
        ▼
 MigracionService  (@Transactional: todo o nada)
   1. clientes: MoldeCliente indica qué columna es qué
      PERSONA → cliente + persona      EMPRESA → cliente + empresa
   2. pedidos: busca el cliente por su código (findByCodigo) y lo enlaza
        │  JPA / Hibernate (repositorios)
        ▼
 PostgreSQL: cliente · persona · empresa · pedido
        │
        ▼
 API REST + Spring Security (HTTP Basic, BCrypt, roles)
        │  JSON
        ▼
 Pantalla / curl
```

La migración se lanza con `POST /api/admin/migracion`, que solo puede usar ADMIN.

## 2. Carga de datos verificada

| Tabla | Filas en el Excel | Filas migradas |
|---|---|---|
| cliente | 6 | 6 |
| persona | 3 | 3 |
| empresa | 3 | 3 |
| pedido | 7 | 7 |

Respuesta de la API: `Migracion completada: 6 clientes y 7 pedidos` (HTTP 200).

Decisiones tomadas con los datos:

- `CLI-002` (Lucía) no tiene email: se ha permitido `NULL` en `persona.email` para no perder el cliente.
- Las celdas vacías se guardan como `NULL`, no como texto vacío.
- `CLI-005` se migra con `activo = false`, como en el origen.
- El identificador legacy `ID_PEDIDO` (`PED-1001`...) no tiene columna en el modelo actual; el pedido recibe un id nuevo de la base de datos.

## 3. Roles (RBAC)

| Rol | Usuario de prueba | Puede |
|---|---|---|
| ADMIN (Dirección) | `admin` | Todo, incluido `/api/admin/**` y la migración |
| SALES (comerciales) | `comercial` | Consultar clientes y pedidos, dar de alta pedidos |
| USER (marketing) | `ana`, `luis` | Solo consultar clientes y pedidos (los que tiene permitidos) |

Las reglas están en `SecurityConfig`; cualquier ruta sin regla se deniega (`denyAll`, 403).

## 4. Pruebas de seguridad ejecutadas

| # | Usuario | Petición | Esperado | Obtenido |
|---|---|---|---|---|
| 1 | sin credenciales | `GET /api/clientes` | 401 | 401 |
| 2 | admin | `GET /api/clientes` | 200 (los 6) | 200 |
| 3 | admin | `GET /api/admin/dashboard` | 200 | 200 |
| 4 | admin | `POST /api/clientes` | 200 | 200 |
| 5 | comercial | `GET /api/clientes` | 200 (los 6) | 200 |
| 6 | comercial | `GET /api/clientes/1/pedidos` | 200 | 200 |
| 7 | comercial | `POST /api/clientes/1/pedidos` | 200 | 200 |
| 8 | comercial | `POST /api/clientes` | 403 | 403 |
| 9 | comercial | `GET /api/admin/dashboard` | 403 | 403 |
| 10 | ana | `GET /api/clientes` | 200 (CLI-001 y CLI-003) | 200 |
| 11 | luis | `GET /api/clientes` | 200 (CLI-002) | 200 |
| 12 | ana | `POST /api/clientes/1/pedidos` | 403 | 403 |
| 13 | ana | `POST /api/admin/migracion` | 403 | 403 |

## 5. Ejecución

1. Crear las tablas de negocio (cliente, persona, empresa, pedido) en PostgreSQL.
2. Ejecutar `sql/db_script.sql` (roles y usuarios, sin el bloque final de permisos).
3. Arrancar la API: `mvnw spring-boot:run`.
4. Migrar: `curl -u admin:<contraseña> -X POST http://localhost:8080/api/admin/migracion`.
5. Ejecutar el bloque final de permisos de `sql/db_script.sql`.
