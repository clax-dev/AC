# Aseguramiento y control de acceso en la API REST con Spring Security

**Alumno: Guillermo Prat Astilero**
**Asignatura:** Acceso a Datos y PSP (DAM2)
**Fecha: 13-11-2026 - 16-11-2026**

<!--
ESQUELETO. Cada apartado tiene una nota como esta que dice que hay que contar.
Borra las notas cuando lo hayas escrito.
-->

---

## 1. Arquitectura de seguridad

<!--
Que contar (5-8 lineas):
- Que se ha añadido Spring Security sobre la arquitectura por capas que ya existia.
- Que todo lo nuevo de seguridad esta en el paquete security.
- En que momento actua la seguridad: antes de llegar al controller.
-->

### 1.1 Piezas añadidas

<!-- Rellena la columna "Que hace" con tus palabras, una frase por fila. -->

| Pieza | Paquete | Qué hace |
|---|---|---|
| `SecurityConfig` | `security` | Es el reglamento de la API, reúne las normas que dictan quién puede entrar y a donde (rutas).|
| `UsuarioDetailsService` | `security` | Cuando alguien intenta entrar, lo busca en la base de datos y entrega a Spring sus datos: usuario, contraseña guardada y rol.|
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

---

## 2. Modelo de datos

<!--
Que contar (2-3 lineas): que tablas se han añadido y para que sirve cada una.
Debajo, la imagen del diseño de tablas.
-->

![Diseño de las tablas](img/tablas.png)

| Tabla | Para qué sirve |
|---|---|
| `rol` | |
| `usuario` | |
| `client_permission` | |

---

## 3. Cambios respecto a la entrega anterior

<!-- La evaluacion es sobre el diferencial, asi que este apartado importa. -->

### 3.1 Archivos nuevos

| Archivo | Motivo |
|---|---|
| `sql/db_script.sql` | |
| `security/SecurityConfig.java` | |
| `security/UsuarioDetailsService.java` | |
| `entity/Usuario.java`, `Rol.java`, `PermisoCliente.java`, `TipoRol.java`, `TipoPermiso.java` | |
| `repositories/UsuarioRepository.java`, `PermisosClienteRepository.java` | |
| <!-- AdminController, cuando exista --> | |

### 3.2 Archivos modificados

| Archivo | Qué ha cambiado |
|---|---|
| `pom.xml` | |
| `service/ClienteService.java` | <!-- PENDIENTE: filtrado de clientes y pedidos por usuario --> |
| Paquete `repository` → `repositories` | |

---

## 4. Decisiones de diseño

<!--
Una linea por decision: que elegiste y por que.
Añade las que tomes mas adelante (por ejemplo, que ve el admin).
-->

| Decisión | Motivo |
|---|---|
| Roles guardados como `ADMIN` / `USER` | |
| `client_permission` enlaza por login y por código de cliente | |
| Columna `id` propia en `client_permission` | |
| Luis tiene `CLI-002` y no `CLI-005` | |
| `UserDetailsService` como clase `@Service` en `security` | |
| Anotaciones JPA en las entidades nuevas | |
| Paquete `repositories` en plural | |

---

## 5. Pila de llamadas de `GET /api/clientes`

<!--
El enunciado pide que el diagrama refleje:
1. El paso por la cadena de filtros de Spring Security ANTES del controller.
2. La verificacion de la autenticacion contra la base de datos.
3. El Service aplicando el filtro de permisos.
4. La respuesta HTTP devuelta (incluye las salidas 401 y 403).
Puede ser una imagen o una foto limpia de un esquema a mano.
-->

![Diagrama de la petición](img/diagrama.png)

<!-- 3-4 lineas explicando el recorrido con tus palabras. -->

---

## 6. Inventario de pruebas de seguridad

<!-- Rellenar "Obtenido" y enlazar cada captura de la carpeta evidencias. -->

| # | Escenario | Comando | Esperado | Obtenido | Evidencia |
|---|---|---|---|---|---|
| 1 | `GET /api/clientes` sin credenciales | | 401 Unauthorized | | |
| 2 | Ruta de administración con un usuario `ROLE_USER` | | 403 Forbidden | | |
| 3 | Ana en `GET /api/clientes` | | 200 OK con CLI-001 y CLI-003 | | |
| 4 | Luis en `GET /api/clientes` | | 200 OK con CLI-002 | | |

### Observaciones

<!--
- La errata del enunciado: menciona CLI-005 para Luis, cliente que no existe;
  se usa CLI-002 como indican la poblacion de datos y este inventario.
-->
