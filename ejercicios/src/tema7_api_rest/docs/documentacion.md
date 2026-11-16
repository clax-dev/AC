# Aseguramiento y control de acceso en la API REST con Spring Security

**Alumno: Guillermo Prat Astilero**
**Asignatura:** Acceso a Datos y PSP (DAM2)
**Fecha: 13-11-2026 - 16-11-2026**

---

## 1. Arquitectura de seguridad

La API ya estaba organizada por capas (controller, service, mapper, repository, dto y entity). Encima de esa estructura se ha añadido Spring Security, sin cambiarla: todo lo nuevo de seguridad está junto en el paquete `security`, y las tablas nuevas tienen sus entidades y repositorios en los paquetes de siempre.

La seguridad actúa antes de que la petición llegue al controller. Si quien pide no se identifica o no tiene permiso, la petición se corta ahí y nunca llega al código de la API. Si pasa, el servicio solo le devuelve los datos que tiene asignados.

### 1.1 Piezas añadidas

| Pieza | Paquete | Qué hace |
|---|---|---|
| `SecurityConfig` | `security` | Es el reglamento de la API, reúne las normas que dictan quién puede entrar y a donde (rutas).|
| `UsuarioDetailsService` | `security` | Cuando alguien intenta entrar, lo busca en la base de datos y entrega a Spring sus datos: usuario, contraseña guardada y rol.|
| `UsuarioActual` | `security` | Lee quién ha hecho la petición: su nombre de usuario y si es admin o no. La usa el servicio para saber qué datos enseñar.|
| `AdminController` | `controller` | Atiende la ruta `/api/admin/dashboard`, que solo puede abrir el admin. No hace el trabajo: se lo pide al servicio.|
| `AdminService` | `service` | Cuenta cuántos clientes, pedidos, usuarios y permisos hay y lo devuelve como resumen (`AdminResumeDTO`).|
| `UsuarioRepository` | `repositories` | Es el que va a la tabla de usuarios a buscar a una persona por su nombre de usuario.|
| `PermisosClienteRepository` | `repositories` | Es el que va a la tabla de permisos a buscar los datos que dicen qué clientes puede ver cada usuario.|
| `Usuario`, `Rol`, `PermisoCliente` | `entity` | Son los moldes: dicen qué forma tiene en Java cada fila de las tablas nuevas (usuarios, roles y permisos).|

### 1.2 Papel del repositorio de usuarios

`UsuarioRepository` es el que conecta la seguridad con la base de datos. Lo usa `UsuarioDetailsService` cada vez que alguien intenta entrar.

Solo tiene un método, `findByUsuarioLogin`: le das un nombre de usuario y va a la tabla de usuarios a buscarlo. Pueden pasar dos cosas:

- Si el usuario no existe, se avisa a Spring y la petición se rechaza con un 401.
- Si existe, se entregan a Spring sus datos: usuario, contraseña guardada y rol.

El repositorio no comprueba la contraseña, solo trae lo que hay guardado. Quien la compara es Spring.

### 1.3 Papel de la clase de configuración

`SecurityConfig` es el reglamento de la API. Toda la configuración de seguridad está junta en esta clase, así que si hay que cambiar una norma se cambia en un solo sitio.

Dentro tiene dos piezas que Spring recoge al arrancar (los `@Bean`):

| Pieza | Qué hace |
|---|---|
| `comparadorContrasena()` | Le dice a Spring que compare las contraseñas con BCrypt, que es como están guardadas en la base de datos. |
| `normas()` | Reúne las normas que se aplican a cada petición antes de dejarla pasar. |

Las normas son cuatro:

| Norma | Qué significa |
|---|---|
| Sin CSRF | Se apaga una protección que solo sirve cuando hay cookies de sesión. Esta API no las usa. |
| Stateless | El servidor no recuerda a nadie. Cada petición tiene que identificarse de nuevo. |
| Rutas | Las rutas de `/api/admin/` son solo para el rol `ADMIN`. Para el resto basta con estar identificado. |
| HTTP Basic | El usuario y la contraseña viajan en la cabecera de cada petición, sin formulario de login. |

Si alguien no se identifica recibe un 401. Si se identifica pero no tiene el rol que pide la ruta, recibe un 403.

### 1.4 Filtrado de datos en el servicio

Pasar la puerta no da acceso a todo. Una vez dentro, `ClienteService` pregunta a `UsuarioActual` quién es el usuario y decide qué enseñarle:

| Ruta | Si es admin | Si no es admin |
|---|---|---|
| `GET /api/clientes` | Todos los clientes | Solo los clientes que tiene asignados en `client_permission` |
| `GET /api/clientes/activos` | Todos los activos | Solo los activos que tiene asignados |
| `GET /api/clientes/empresa` | Todas las empresas | Solo las empresas que tiene asignadas |
| `GET /api/clientes/codigo/{codigo}` | Cualquier cliente | El cliente solo si lo tiene asignado. Si no, 404 |
| `GET /api/clientes/{id}/pedidos` | Los pedidos de cualquier cliente | Los pedidos solo si el cliente es suyo. Si no, 404 |

Por eso Ana y Luis tienen el mismo rol pero ven clientes distintos: lo que los diferencia no es el rol, sino sus filas en la tabla de permisos.

Hay dos formas de filtrar, según lo que pide la ruta:

| La ruta pide... | Qué hacemos si no es suyo | Ejemplo |
|---|---|---|
| **Un** cliente concreto | Devolvemos 404, para no desvelar si ese cliente existe o no | `/codigo/{codigo}`, `/{id}/pedidos` |
| Una **lista** | Devolvemos solo los suyos. Si no tiene ninguno, la lista vacía (no es un error, simplemente no hay ninguno para él) | `/api/clientes`, `/activos`, `/empresa` |

---

## 2. Modelo de datos

Se han añadido tres tablas a las cuatro que ya tenía la API (`cliente`, `persona`, `empresa` y `pedido`). Se crean y se rellenan con el script `sql/db_script.sql`, que se puede ejecutar varias veces: borra las tres tablas y las vuelve a crear.

| Tabla | Columnas | Para qué sirve |
|---|---|---|
| `rol` | `id`, `tipo_rol` | Guarda los dos roles: `ADMIN` y `USER`. |
| `usuario` | `id`, `id_rol`, `usuario_login`, `contrasena` | Guarda quién puede entrar: su nombre, su contraseña (hasheada con BCrypt, nunca en claro) y su rol. |
| `client_permission` | `id`, `usuario`, `codigo_cliente`, `permiso` | Dice qué clientes puede ver cada usuario. Una fila por cada cliente permitido, con el permiso `READ`. |

Cómo se relacionan:

```
   rol  1 ──── N  usuario  1 ──── N  client_permission  N ──── 1  cliente
  (id)           (id_rol)                                            (codigo)
                (usuario_login) ◄── usuario       codigo_cliente ──►
```

| Relación | Columna | Apunta a |
|---|---|---|
| Un rol lo tienen muchos usuarios | `usuario.id_rol` | `rol.id` |
| Un usuario tiene muchos permisos | `client_permission.usuario` | `usuario.usuario_login` |
| Un cliente aparece en muchos permisos | `client_permission.codigo_cliente` | `cliente.codigo` |

Datos de prueba:

| Usuario | Rol | Clientes que tiene asignados |
|---|---|---|
| `ana` | USER | CLI-001, CLI-003 |
| `luis` | USER | CLI-002 |
| `admin` | ADMIN | Ninguno (como es admin, ve todos) |

---

## 3. Cambios respecto a la entrega anterior

### 3.1 Archivos nuevos

| Archivo | Para qué |
|---|---|
| `sql/db_script.sql` | Crea las tablas `rol`, `usuario` y `client_permission` e inserta los usuarios y permisos de prueba. |
| `security/SecurityConfig.java` | El reglamento: BCrypt y las cuatro normas. |
| `security/UsuarioDetailsService.java` | Busca en la base de datos a quien intenta entrar. |
| `security/UsuarioActual.java` | Dice quién ha hecho la petición y si es admin. |
| `entity/Usuario.java`, `Rol.java`, `PermisoCliente.java`, `TipoRol.java`, `TipoPermiso.java` | Los moldes de las tablas nuevas y los valores permitidos (`ADMIN`/`USER` y `READ`). |
| `repositories/UsuarioRepository.java`, `PermisosClienteRepository.java` | Buscan usuarios y permisos en la base de datos. El de permisos tiene un método para cada caso: todos los del usuario, los de clientes activos, los de un tipo, y dos que confirman si tiene asignado un cliente (por id o por código). |
| `controller/AdminController.java` | La ruta `/api/admin/dashboard`, solo para el admin. |
| `service/AdminService.java` | Prepara el resumen que enseña el dashboard. |
| `dto/AdminResumeDTO.java` | La forma del resumen: número de clientes, pedidos, usuarios y permisos. |

### 3.2 Archivos modificados

| Archivo | Qué ha cambiado |
|---|---|
| `pom.xml` | Se añade la dependencia `spring-boot-starter-security`. |
| `service/ClienteService.java` | Los métodos que devuelven listas (`listarClientes()`, `listarActivos()` y `buscarClienteEmpresa()`) ya no devuelven todo: el admin ve todos y el resto solo los que tiene asignados. Los que piden un cliente concreto (`buscarPorCodigo()` y `pedidosPorId()`) comprueban antes si realmente lo tiene asignado y, si no, devuelven 404. |
| `application.properties` | Se añade `spring.web.error.include-stacktrace=never`, para que las respuestas de error no enseñen el error interno de Java. |
| Paquete `repository` → `repositories` | Se renombra el paquete. `ClienteRepository` y `PedidoRepository` cambian de sitio, pero su contenido es el mismo. |

---

## 4. Decisiones de diseño

| Decisión | Por qué |
|---|---|
| Roles guardados como `ADMIN` / `USER` | Spring añade solo el prefijo `ROLE_`, así que en la aplicación son `ROLE_ADMIN` y `ROLE_USER`, como pide el enunciado. |
| `client_permission` enlaza por nombre de usuario y por código de cliente | El enunciado pide asociar a cada usuario con los códigos de cliente que puede ver (por ejemplo, CLI-001). |
| Columna `id` propia en `client_permission` | Así la entidad es igual de sencilla que las demás. Un `UNIQUE` impide repetir el mismo permiso. |
| Luis tiene `CLI-002` y no `CLI-005` | El enunciado nombra `CLI-005` en un punto, pero ese cliente no existe. Se usa `CLI-002`, como dicen la población de datos y el inventario de pruebas. |
| `UserDetailsService` como clase propia (`UsuarioDetailsService`) en `security` | Funciona igual que declararlo con `@Bean`, pero se lee mejor y queda junto al resto de la seguridad. |
| Clase `UsuarioActual` aparte | Saber quién es el usuario y si es admin hace falta en varios métodos. Así se escribe una sola vez y se reutiliza. |
| El admin ve todos los clientes | El enunciado no dice qué ve el admin. Lo normal es que un administrador vea todo, sin necesitar filas de permisos. |
| 404 (y no 403) al pedir un cliente ajeno o sus pedidos | Para no desvelar si ese cliente existe o no. Ana recibe lo mismo si pide un cliente de otro que si pide uno que no existe: para ella ese cliente no "existe" porque no es suyo. |
| Lista vacía (y no 404) en las rutas que devuelven listas | Si pides varios, te damos los tuyos, aunque sean ninguno. No tener ninguna empresa asignada no es un error. |
| Filtrar también `/activos`, `/empresa` y `/codigo/{codigo}` | El enunciado solo pide filtrar `/api/clientes` y los pedidos, pero sin esto un usuario podía ver clientes de otro entrando por esas rutas. |
| Las consultas filtran en la base de datos | Cada caso tiene su método en `PermisosClienteRepository`, así la base de datos solo devuelve los clientes del usuario en vez de traerlos todos y descartar después. |
| Dashboard de admin con un resumen | Sirve para demostrar que el admin sí entra en `/api/admin/**` (200) y un usuario normal no (403). El controller atiende y el servicio hace el recuento. |
| Anotaciones JPA en las entidades nuevas | Se valoró separar la configuración en otro archivo, pero se descartó para seguir igual que las entidades que ya existían. |
| Paquete `repositories` en plural | Contiene varios repositorios. |

---

## 5. Pila de llamadas de `GET /api/clientes`

Recorrido de una petición de Ana a `GET /api/clientes`, desde que sale de `curl` hasta que vuelve la respuesta:

```
  curl -u ana:ana1234 http://localhost:8080/api/clientes
                         │
                         ▼
 ╔═══════════════════════════════════════════════════════════════╗
 ║            CADENA DE FILTROS DE SPRING SECURITY               ║
 ║                                                               ║
 ║  1. HTTP Basic: lee usuario y contraseña de la cabecera       ║
 ║        │                                                      ║
 ║        │   ¿viene sin credenciales? ───────────────► 401      ║
 ║        ▼                                                      ║
 ║  2. UsuarioDetailsService.loadUserByUsername("ana")           ║
 ║        │                                                      ║
 ║        └──► UsuarioRepository.findByUsuarioLogin("ana")       ║
 ║                   │                                           ║
 ║                   ▼                                           ║
 ║             ┌───────────────┐                                 ║
 ║             │  PostgreSQL   │  tabla usuario                  ║
 ║             └───────────────┘                                 ║
 ║        │   ¿no existe? ────────────────────────────► 401      ║
 ║        ▼                                                      ║
 ║  3. BCrypt compara la contraseña con la guardada              ║
 ║        │   ¿no coincide? ──────────────────────────► 401      ║
 ║        ▼                                                      ║
 ║  4. Normas de rutas: /api/clientes pide estar identificado    ║
 ║        │   (si fuera /api/admin/** sin rol ADMIN) ─► 403      ║
 ╚════════╪══════════════════════════════════════════════════════╝
          ▼
 ┌────────────────────────────────────────────────────────────────┐
 │  ClienteController.listarClientes()                            │
 └────────────────────────────────────────────────────────────────┘
          ▼
 ┌────────────────────────────────────────────────────────────────┐
 │  ClienteService.listarClientes()        FILTRO DE PERMISOS     │
 │                                                                │
 │  UsuarioActual.esAdmin()?                                      │
 │     ├── sí → ClienteRepository.findAll()   (todos)             │
 │     └── no → UsuarioActual.login() = "ana"                     │
 │              PermisosClienteRepository                         │
 │                 .findByUsuarioUsuarioLogin("ana")              │
 │                       │                                        │
 │                       ▼                                        │
 │                 ┌───────────────┐                              │
 │                 │  PostgreSQL   │  tabla client_permission     │
 │                 └───────────────┘                              │
 │              → CLI-001, CLI-003                                │
 │  ClienteMapper.toDto() de cada cliente                         │
 └────────────────────────────────────────────────────────────────┘
          ▼
   HTTP/1.1 200 OK
   [{"codigo":"CLI-001",...},{"codigo":"CLI-003",...}]
```

En palabras: la petición pasa primero por la seguridad, que busca a Ana en la base de datos y comprueba su contraseña. Si algo falla, responde 401; si la ruta fuera de administración, 403. Si todo está bien, la petición llega al controller, y el servicio pregunta quién es el usuario: como Ana no es admin, busca sus permisos y le devuelve solo sus dos clientes con un 200.

---

## 6. Inventario de pruebas de seguridad

Las cuatro pruebas que pide el enunciado, ejecutadas con `curl -i` para ver las cabeceras de la respuesta:

| # | Escenario | Comando | Esperado | Obtenido | Evidencia |
|---|---|---|---|---|---|
| 1 | `GET /api/clientes` sin credenciales | `curl.exe -i http://localhost:8080/api/clientes` | 401 Unauthorized | ✅ 401, con la cabecera `WWW-Authenticate: Basic` | `evidencias/01_sin_credenciales_y_ana_admin.png` |
| 2 | Ruta de administración con un usuario `ROLE_USER` | `curl.exe -i -u ana:ana1234 http://localhost:8080/api/admin/dashboard` | 403 Forbidden | ✅ 403 | `evidencias/01_sin_credenciales_y_ana_admin.png` |
| 3 | Ana en `GET /api/clientes` | `curl.exe -i -u ana:ana1234 http://localhost:8080/api/clientes` | 200 OK con CLI-001 y CLI-003 | ✅ 200 con CLI-001 y CLI-003 | `evidencias/02_ana_y_luis_clientes.png` |
| 4 | Luis en `GET /api/clientes` | `curl.exe -i -u luis:luis1234 http://localhost:8080/api/clientes` | 200 OK con CLI-002 | ✅ 200 con CLI-002 | `evidencias/02_ana_y_luis_clientes.png` |

En ninguna respuesta aparece la cabecera `Set-Cookie`: el servidor no crea sesión, como pide la norma stateless.

Pruebas adicionales (sin captura). Todas las rutas probadas con los tres usuarios:

| Ruta | Ana | Luis | Admin |
|---|---|---|---|
| `/api/clientes` | 200: CLI-001, CLI-003 | 200: CLI-002 | 200: los tres |
| `/api/clientes/activos` | 200: CLI-001 | 200: CLI-002 | 200: CLI-001, CLI-002 |
| `/api/clientes/empresa` | 200: lista vacía `[]` | 200: CLI-002 | 200: CLI-002 |
| `/api/clientes/codigo/CLI-001` | 200 | 404 | 200 |
| `/api/clientes/codigo/CLI-002` | 404 | 200 | 200 |
| `/api/clientes/1/pedidos` (cliente de Ana) | 200 | 404 | 200 |
| `/api/clientes/2/pedidos` (cliente de Luis) | 404 | 200 | 200 |
| `/api/admin/dashboard` | 403 | 403 | 200 con el resumen |

Si Ana pide un cliente que no existe (`/api/clientes/codigo/CLI-999` o `/api/clientes/999/pedidos`) recibe el mismo 404 que con un cliente de Luis, así que no puede saber cuál existe y cuál no.

El volcado de la base de datos (estructura y datos de las 7 tablas) está en `evidencias/volcado_basededatos.sql`.

### Observaciones

- **Errata del enunciado:** en un punto dice que Luis debe ver `CLI-005`, pero ese cliente no existe en los datos. Se ha usado `CLI-002`, como indican la población de datos y el inventario de pruebas.
- **Rutas de escritura:** los `POST` (crear cliente y crear pedido) exigen estar identificado, pero no se filtran por permisos. El enunciado solo habla de permisos de lectura (`READ`).

---

## 7. Cómo ejecutarlo

1. Crear la base de datos `basededatos` en PostgreSQL y ejecutar el script de clase con las tablas de la API.
2. Ejecutar `sql/db_script.sql` para crear las tablas de seguridad y los usuarios de prueba.
3. En `src/main/resources/application.properties`, poner la contraseña del usuario `postgres` propio en `spring.datasource.password`.
4. Arrancar `ClienteApplication` y probar con `curl`.

Usuarios de prueba: `ana` / `ana1234`, `luis` / `luis1234` y `admin` / `admin1234`.
