# Tema 7: APIs REST con Spring Boot, contado como una historia

> **Cómo usar este archivo:**
> - Léelo **de un tirón**, como un cuento. Cada bloque tiene una **💬 frase clave**.
> - Ten abierto `visual.pdf` al lado. La sintaxis está en `2_TECNICO.md`, y para comprobarte tienes `3_PREGUNTAS.md`.
> - Las páginas 📄 son las del PDF de clase (`06_APIs_REST_con_Spring_Boot.pdf`).
> - **Ejercicios del tema:** `ejercicios/src/tema7_api_rest/`.
> - Las anotaciones sueltas están en `../GUIA_LIBRERIAS.md`, apartado 14.

**La historia entera en tres líneas:**
- En el tema 5 le pedías datos a la PokeAPI. **Ahora te cambias de lado: tú eres la PokeAPI.**
- Una API bien hecha funciona como un **restaurante**: cada persona tiene un solo trabajo y nadie se mete en el del otro.
- **Spring Boot** te da el restaurante ya montado, y tú solo pones la carta y las recetas.

> ⚠️ **Dos "clientes" distintos.** En este tema, *cliente HTTP* es quien hace la petición (curl, Postman, React) y *`Cliente`* es la entidad de la base de datos (quien compra). No los confundas.

---

## 1. Te cambias de lado
📄 *PDF, página 1*

> 💬 **"Antes eras quien pedía el JSON. Ahora eres quien lo sirve."**

Todo lo de los temas anteriores vuelve a aparecer, pero junto:

| Lo que ya sabes | Dónde reaparece |
|---|---|
| Tema 3: `Optional` | `findById` devuelve un `Optional` |
| Tema 5: JSON, Jackson y HTTP | La respuesta de tu API; Jackson la convierte **solo** |
| Tema 6: JDBC y JPA | Lo que hay debajo, cuando tu API habla con PostgreSQL |

---

## 2. El restaurante: arquitectura por capas
📄 *PDF, páginas 2 y 3*

> 💬 **"Cada capa tiene un solo trabajo, y la comunicación baja de una en una: el camarero nunca entra en el almacén."**

Imagina que el camarero toma nota, cocina, va a por los tomates y cobra. El día que cambie la receta, habrá que tocarlo todo. Por eso se reparte el trabajo. Eso es la **separación de responsabilidades**.

| En el restaurante | En la API | Su único trabajo |
|---|---|---|
| 🧑 Comensal | Cliente HTTP | Hace el pedido |
| 🤵 Camarero | **Controller** | Recibe la petición y entrega la respuesta |
| 👩‍🍳 Jefa de cocina | **Service** | Aplica las reglas del negocio |
| 📦 Encargado del almacén | **Repository** | Busca y guarda los datos |
| 🏬 Almacén | **PostgreSQL** | Guarda todo físicamente |

La regla de oro son las **capas cerradas**: cada capa solo habla con la de al lado. La ventaja es que puedes probar una capa sin las demás y que un cambio en una no rompe las otras.

---

## 3. Tres patrones con nombre
📄 *PDF, páginas 2, 4, 7 y 8*

> 💬 **"MVC separa quién recibe, quién sabe y quién presenta. DAO y Repository esconden cómo se llega a los datos."**

- **MVC:** el *Controller* recibe, el *Model* son los datos y la *Vista* es lo que ve quien pregunta. En una API REST, la vista es el JSON.
- **DAO y Repository** hacen el mismo tipo de encargo, pero hablan distinto:
  - **DAO** es dar instrucciones por estanterías: *"pasillo 3, balda 2, caja de tomates"*. Escribes el SQL a mano con JDBC y usas `insert()`, `update()` o `delete()`.
  - **Repository** es hablar de lo que quieres: *"dame los tomates"*. Trabajas con entidades como si fueran una colección en memoria: `save()`, `findAll()`, `findByNombre()`.

Son lo mismo que hiciste en el tema 6, ahora con nombre: **JDBC encaja con DAO y JPA con Repository**. Los dos usan interfaces para que el resto del código no dependa de cómo se accede a los datos. Repository solo sube el nivel: hablas de objetos, no de tablas.

---

## 4. Spring Boot: el restaurante ya montado
📄 *PDF, páginas 5 y 6*

> 💬 **"Spring Boot es un food truck: la cocina viaja con el negocio. Jakarta EE es la normativa: tú eliges el local."**

Montar una API a mano sería alquilar el local, instalar la cocina y sacar los permisos antes de freír un huevo. Spring Boot lo trae hecho:
- **Autoconfiguración:** toma las decisiones por defecto para ahorrarte código repetido.
- **Servidor embebido:** Tomcat va dentro de tu aplicación. No hay que instalar nada.

| | **Spring Boot** | **Jakarta EE** |
|---|---|---|
| Qué es | Un framework | Un estándar: define las reglas |
| Idea | "Todo incluido" | "Tú eliges el servidor" |
| Se entrega como | `.jar` que arranca solo | `.war` o `.ear` para un servidor externo |
| Encaja en | Startups, microservicios, cambios rápidos | Banca, seguros, gobierno, sistemas a largo plazo |

---

## 5. DTO y Mapper: nunca sirvas el ingrediente crudo
📄 *PDF, página 9*

> 💬 **"La entidad es el ingrediente del almacén y el DTO es el plato: al cliente HTTP solo le llega el plato."**

La **entidad** (`@Entity`) es exactamente lo que hay en la base de datos. Si la devuelves tal cual, enseñas todo lo que contiene y atas tu API a tus tablas. Por eso entre la cocina y la sala hay dos objetos más:

- **DTO:** una clase sencilla con solo los datos que entran o salen. `ClienteRequestDto` es la **comanda** (lo que llega) y `ClienteResponseDto` es el **plato** (lo que sale).
- **Mapper:** quien **emplata**. Convierte DTO → entidad y entidad → DTO.

---

## 6. Abrir el restaurante
📄 *PDF, páginas 11 a 17*

> 💬 **"Initializr te entrega el local, `application.properties` dice dónde está el almacén y `@SpringBootApplication` abre las puertas."**

- **Spring Initializr** (`start.spring.io`) genera el proyecto. Tú eliges los ingredientes:
  - **Spring Web:** para atender peticiones. Trae Tomcat y Jackson.
  - **Spring Data JPA:** para los repositorios. Ya incluye Hibernate.
  - **PostgreSQL Driver:** el enchufe con la base de datos.
  - **Validation** (para comprobar los datos que llegan) y **DevTools** (reinicia al cambiar código), opcionales.
  - **Lombok** no se usa aquí: para aprender es mejor ver los getters y setters escritos.
- **`application.properties`** es la libreta de la configuración: puerto, dirección de la base de datos, usuario y contraseña.
- **`ddl-auto=validate`** es la opción prudente: Hibernate **comprueba** que las entidades coinciden con las tablas, sin tocar nada. Con `create`, en cambio, **borra** las tablas al arrancar.
- **`@SpringBootApplication`** va en la clase principal. Spring revisa ese paquete y los de debajo buscando a su personal (`@RestController`, `@Service`, `@Repository`). Por eso esa clase va **en el paquete raíz**: lo que quede fuera, no lo encuentra.

---

## 7. El camarero: Controller y verbos HTTP
📄 *PDF, páginas 18 y 19*

> 💬 **"El verbo HTTP dice qué quieres hacer y la URL dice sobre qué."**

| Verbo | Anotación | Para qué | Equivale a |
|---|---|---|---|
| `GET` | `@GetMapping` | Consultar | `SELECT` |
| `POST` | `@PostMapping` | Crear | `INSERT` |
| `PUT` / `PATCH` | `@PutMapping` / `@PatchMapping` | Actualizar | `UPDATE` |
| `DELETE` | `@DeleteMapping` | Borrar | `DELETE` |

`@RestController` indica que esa clase devuelve **datos**, no páginas web. `@RequestMapping("/api/clientes")` fija la dirección común de todos sus métodos.

---

## 8. El encargado del almacén que se escribe solo
📄 *PDF, páginas 12 y 21*

> 💬 **"Escribes una interfaz vacía y Spring pone el trabajador. El nombre del método es el pedido."**

Esta es la parte que parece magia. Solo con esto:
```java
public interface ClienteRepository extends JpaRepository<Cliente, Long> { }
```
ya tienes `findAll()`, `findById()`, `save()`, `delete()`, `count()` y `existsById()`, sin escribir ni una línea más.

Y Spring incluso **entiende los nombres** de los métodos. `findByActivoTrueOrderByCodigoAsc()` se lee casi como una frase: *busca los que tengan `activo` a `true` y ordénalos por `codigo` ascendente*.

---

## 9. Un pedido, de principio a fin
📄 *PDF, páginas 9, 10, 16 y 22*

> 💬 **"La petición baja por las capas y la respuesta sube por el mismo camino."**

```
Comensal      GET /api/clientes
  ↓
Controller    recibe la petición
  ↓
Service       aplica las reglas (y usa el Mapper)
  ↓
Repository    pide los datos a Hibernate
  ↓
PostgreSQL    devuelve las filas
  ↑
... y de vuelta, Jackson convierte el resultado en JSON
```

> **Ojo con la pág. 22:** ahí el Controller llama directamente al Repository. Es un atajo para probar el primer endpoint. La opción 2 de la pág. 23 añade el Service y lo deja como mandan las capas.

---

## 10. El primer susto: el error LAZY
📄 *PDF, página 23*

> 💬 **"`LAZY` trae los pedidos cuando los pides, pero si el almacén ya ha cerrado, no hay quien los traiga."**

La primera prueba con `curl` falla, y es normal. Esto es lo que pasa:
1. Hibernate trae al `Cliente`, pero **no** sus pedidos: la relación es `LAZY` ("ya iré a por ellos si hacen falta").
2. El repositorio termina y la sesión con la base de datos **se cierra**.
3. Jackson va a escribir el JSON, recorre el cliente y se encuentra con `pedidos`.
4. Hay que ir a buscarlos, pero el almacén ya ha cerrado → **error**.

Hay dos salidas:

| | Opción 1: `EAGER` | Opción 2: DTO ✅ |
|---|---|---|
| Idea | Traer siempre los pedidos con el cliente | Devolver solo lo que hace falta |
| Precio | Consultas de más, aunque no los necesites | Algo más de código: DTO y Service |
| Además | Necesita `@JsonIgnore` para evitar un **bucle infinito** (cliente → pedidos → cliente → …) | Controlas qué campos salen en el JSON |

La **opción 2** es la que sigue la regla del bloque 5: la entidad no sale de la cocina.

---

## 11. Resumen

| Concepto | Frase clave |
|---|---|
| Capas | Cada una con un trabajo; hablan solo con la de al lado |
| Controller / Service / Repository | Camarero / cocina / almacén |
| DAO frente a Repository | "Pasillo 3, balda 2" frente a "dame los tomates" |
| Spring Boot | Todo incluido: autoconfiguración y servidor embebido |
| Jakarta EE | Un estándar: define las reglas y tú eliges el servidor |
| Entidad / DTO / Mapper | Ingrediente / plato / quien emplata |
| `application.properties` | La libreta de la configuración |
| `@SpringBootApplication` | En el paquete raíz, para que encuentre al resto |
| Verbos HTTP | `GET` lee, `POST` crea, `PUT` actualiza, `DELETE` borra |
| `JpaRepository` | Interfaz vacía con el CRUD ya hecho |
| Error LAZY | Jackson llega tarde: la sesión ya se ha cerrado |

**¿Y ahora?** Repasa `visual.pdf`, haz `3_PREGUNTAS.md` y abre `2_TECNICO.md` cuando programes. El punto de partida de la práctica está en `ejercicios/src/tema7_api_rest/LEEME.md`.
