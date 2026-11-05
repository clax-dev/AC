# Tema 7: APIs REST con Spring Boot, preguntas para comprobarte

> **Cómo usar este archivo:**
> - Lee la pregunta, **contesta en voz alta** y solo después abre la respuesta.
> - Marca con ✅ las que te salen. Las de ⭐ son las más importantes del tema.

## Progreso
- [ ] Bloque 1: Capas y patrones (1-7)
- [ ] Bloque 2: Spring Boot y el proyecto (8-13)
- [ ] Bloque 3: Controller y Repository (14-20)
- [ ] Bloque 4: DTO y error LAZY (21-26)

---

## Bloque 1: Capas y patrones

### 1. ⭐ ¿Qué es la separación de responsabilidades?
<details><summary>Respuesta</summary>

Dividir la aplicación en partes con **un único propósito** cada una. Así el código es predecible y un cambio queda aislado en su parte.
</details>

### 2. ⭐ Di las capas de una API y qué hace cada una.
<details><summary>Respuesta</summary>

**Controller:** recibe la petición y devuelve la respuesta. **Service:** aplica la lógica de negocio. **Repository:** guarda y recupera datos. **Base de datos:** los almacena físicamente.
</details>

### 3. ⭐ ¿Qué significa "capas cerradas"?
<details><summary>Respuesta</summary>

Que la comunicación va **de capa en capa**, de arriba abajo: la presentación nunca accede directamente a la persistencia. El camarero no entra en el almacén.
</details>

### 4. ¿Qué ventajas tiene aislar las capas?
<details><summary>Respuesta</summary>

Mejora el mantenimiento y las pruebas: cada capa se puede probar por separado usando *mocks* (objetos simulados), y un cambio en una no afecta a las demás.
</details>

### 5. ¿Qué hace cada parte de MVC en una API REST?
<details><summary>Respuesta</summary>

El **Controlador** gestiona las solicitudes, el **Modelo** representa los datos y la **Vista** da formato a la respuesta. En una API, la vista suele ser el JSON.
</details>

### 6. ⭐ ¿En qué se diferencian DAO y Repository?
<details><summary>Respuesta</summary>

**DAO** se centra en la mecánica de la base de datos: JDBC, SQL y métodos como `insert()` o `update()`. **Repository** se centra en el dominio: trata los datos como una colección de objetos, con `save()` o `findAll()`.

Analogía: "pasillo 3, balda 2" frente a "dame los tomates".
</details>

### 7. ¿Qué tienen en común DAO y Repository?
<details><summary>Respuesta</summary>

Los dos usan **interfaces** para desacoplar el código (inversión de dependencias): el resto de la aplicación no sabe cómo se accede a los datos. Repository solo eleva la abstracción al modelo de dominio.
</details>

---

## Bloque 2: Spring Boot y el proyecto

### 8. ⭐ ¿Qué dos cosas hacen rápido a Spring Boot?
<details><summary>Respuesta</summary>

La **autoconfiguración**, que elimina código repetido, y el **servidor embebido**, que evita instalar Tomcat aparte.
</details>

### 9. ⭐ Compara Spring Boot y Jakarta EE.
<details><summary>Respuesta</summary>

**Spring Boot** es un framework "todo incluido": genera un `.jar` autónomo. **Jakarta EE** es un estándar que define las reglas: genera un `.war` o `.ear` que se sube a un servidor externo (WildFly, Payara, TomEE).

Spring Boot encaja en startups y microservicios; Jakarta EE, en grandes sistemas a largo plazo.
</details>

### 10. ¿Qué dependencias son imprescindibles para esta API y por qué?
<details><summary>Respuesta</summary>

**Spring Web** (endpoints, servidor y Jackson), **Spring Data JPA** (repositorios e Hibernate) y **PostgreSQL Driver** (conexión con la base de datos).
</details>

### 11. ⭐ ¿Qué hace `ddl-auto` con cada valor?
<details><summary>Respuesta</summary>

`validate` comprueba que entidades y tablas coinciden. `update` intenta modificar la estructura. `create` **borra** y crea las tablas al arrancar. `create-drop` las crea y las elimina al cerrar.
</details>

### 12. ¿Qué tres anotaciones agrupa `@SpringBootApplication`?
<details><summary>Respuesta</summary>

`@Configuration` (la clase puede tener configuración), `@EnableAutoConfiguration` (configura servidor, JPA, Jackson…) y `@ComponentScan` (busca los componentes).
</details>

### 13. ⭐ ¿Por qué la clase principal debe estar en el paquete raíz?
<details><summary>Respuesta</summary>

Porque `@ComponentScan` busca en ese paquete **y en los de debajo**. Una clase que quede fuera no se detecta.
</details>

---

## Bloque 3: Controller y Repository

### 14. ⭐ ¿Qué verbo HTTP y qué anotación usas para cada operación CRUD?
<details><summary>Respuesta</summary>

Leer: `GET`, `@GetMapping`. Crear: `POST`, `@PostMapping`. Actualizar: `PUT` o `PATCH`, `@PutMapping` o `@PatchMapping`. Borrar: `DELETE`, `@DeleteMapping`.
</details>

### 15. ¿Qué hace `@RestController`?
<details><summary>Respuesta</summary>

Indica que la clase es un controlador web cuyos métodos devuelven **datos**, normalmente convertidos a JSON, en lugar de páginas HTML.
</details>

### 16. ¿Para qué sirve `@RequestMapping` en una clase?
<details><summary>Respuesta</summary>

Define la **ruta base** de todos sus endpoints. Con `@RequestMapping("/api/clientes")`, un `@GetMapping("/{id}")` responde en `/api/clientes/{id}`.
</details>

### 17. ⭐ ¿Qué diferencia hay entre `@PathVariable`, `@RequestParam` y `@RequestBody`?
<details><summary>Respuesta</summary>

`@PathVariable` coge un trozo de la ruta (`/clientes/7`). `@RequestParam` coge un parámetro tras el `?` (`?tipo=EMPRESA`). `@RequestBody` convierte el JSON del cuerpo en un objeto.
</details>

### 18. ¿Para qué sirve `@ResponseStatus`?
<details><summary>Respuesta</summary>

Para cambiar el código que se devuelve si todo va bien. Por ejemplo, `@ResponseStatus(HttpStatus.CREATED)` devuelve **201** en lugar del 200.
</details>

### 19. ⭐ ¿Quién implementa `ClienteRepository` y qué significan `Cliente` y `Long`?
<details><summary>Respuesta</summary>

La implementación la genera **Spring automáticamente**. `Cliente` es la entidad que gestiona y `Long` es el tipo de su id.
</details>

### 20. ¿Qué consulta genera `findByActivoTrueOrderByCodigoAsc()`?
<details><summary>Respuesta</summary>

Aproximadamente:
```sql
SELECT * FROM cliente WHERE activo = true ORDER BY codigo ASC;
```
</details>

---

## Bloque 4: DTO y error LAZY

### 21. ⭐ ¿Qué es un DTO y qué diferencia tiene con una entidad?
<details><summary>Respuesta</summary>

Un objeto sencillo con los datos que **entran o salen** de la API. La entidad representa una tabla y lleva anotaciones JPA; el DTO no.
</details>

### 22. ⭐ ¿Por qué no se devuelven directamente las entidades?
<details><summary>Respuesta</summary>

Porque así controlas qué campos se exponen en el JSON y tu API no queda atada a las tablas. Además evitas problemas como el error LAZY y el bucle infinito.
</details>

### 23. ¿Qué hace el Mapper y quién lo usa?
<details><summary>Respuesta</summary>

Convierte DTO → entidad y entidad → DTO. Lo usa el **Service**.
</details>

### 24. ⭐ Explica el viaje de un `POST` por la API.
<details><summary>Respuesta</summary>

1. El cliente HTTP envía un `RequestDto`.
2. El Controller lo pasa al Service.
3. El Service usa el Mapper para crear la entidad.
4. El Repository la guarda con Hibernate.
5. El Service convierte la entidad guardada en `ResponseDto`.
6. El Controller la devuelve y Jackson la convierte en JSON.
</details>

### 25. ⭐ ¿Por qué falla el primer `GET /api/clientes`?
<details><summary>Respuesta</summary>

Hibernate carga el `Cliente`, pero `pedidos` es `LAZY`. Cuando Jackson va a serializarlo, la sesión ya está cerrada y no se pueden traer los pedidos.
</details>

### 26. ⭐ ¿Qué dos soluciones hay y cuál es mejor?
<details><summary>Respuesta</summary>

**`EAGER`:** trae siempre los pedidos, y necesita `@JsonIgnore` en `Pedido.cliente` para evitar un bucle infinito. **DTO:** el Service convierte las entidades en objetos de respuesta.

Es mejor el **DTO**: no hace consultas de más y controla qué campos se exponen.
</details>
