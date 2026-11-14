# Tema 6: JDBC y JPA/Hibernate, preguntas para comprobarte

> **Cómo usar este archivo:**
> - Lee la pregunta, **contesta en voz alta** y solo después abre la respuesta.
> - Marca con ✅ las que te salen. Las de ⭐ son las más importantes del tema.

## Progreso
- [ ] Bloque 1: Bases de datos y transacciones (1-4)
- [ ] Bloque 2: JDBC (5-10)
- [ ] Bloque 3: JPA y entidades (11-17)
- [ ] Bloque 4: EntityManager y comparativa (18-24)

---

## Bloque 1: Bases de datos y transacciones

### 1. ¿Qué es una clave primaria y qué es una clave foránea?
<details><summary>Respuesta</summary>

La **PK** identifica de forma única cada fila de una tabla. La **FK** es una columna que apunta a la PK de otra tabla, como `pedido.cliente_id` → `cliente.id`.
</details>

### 2. ⭐ ¿Qué es una transacción?
<details><summary>Respuesta</summary>

Una unidad lógica de trabajo **indivisible**: o se confirma entera (**COMMIT**) o se deshace entera (**ROLLBACK**).
</details>

### 3. ⭐ ¿Qué significa ACID?
<details><summary>Respuesta</summary>

- **Atomicidad:** todo o nada.
- **Consistencia:** se respetan siempre las reglas del esquema.
- **Aislamiento:** las transacciones concurrentes no se pisan.
- **Durabilidad:** lo confirmado sobrevive a un fallo.
</details>

### 4. ¿Qué diferencia hay entre `ON DELETE CASCADE` y `ON DELETE RESTRICT`?
<details><summary>Respuesta</summary>

**CASCADE:** al borrar el padre se borran también los hijos (cliente → persona/empresa). **RESTRICT:** no deja borrar el padre si tiene hijos (no puedes borrar un cliente con pedidos).
</details>

---

## Bloque 2: JDBC

### 5. ⭐ ¿Qué es JDBC?
<details><summary>Respuesta</summary>

**No** es una base de datos. Es un estándar y una API (`java.sql`) que permite a Java ejecutar operaciones contra bases de datos relacionales.
</details>

### 6. ¿Para qué sirve el driver JDBC?
<details><summary>Respuesta</summary>

Es la librería concreta de cada motor (PostgreSQL, MySQL) que traduce las llamadas de la API estándar al protocolo de esa base de datos. Si cambias de motor, cambias el driver y no tu código.
</details>

### 7. ⭐ ¿Por qué usar `PreparedStatement` en lugar de `Statement`?
<details><summary>Respuesta</summary>

- **Seguridad:** usa huecos `?` con valores tipados, así que evita la **SQL Injection**.
- **Eficiencia:** la consulta queda precompilada y es más rápida si se repite.

`Statement` concatena Strings y es vulnerable.
</details>

### 8. ¿Qué es la SQL Injection? Pon un ejemplo.
<details><summary>Respuesta</summary>

Meter código SQL a través de un dato del usuario. Con `"WHERE user = '" + input + "'"`, si el usuario escribe `' OR '1'='1`, la condición siempre es verdadera y devuelve todo.
</details>

### 9. ⭐ ¿Qué diferencia hay entre `executeQuery()` y `executeUpdate()`?
<details><summary>Respuesta</summary>

`executeQuery()` es para **SELECT** y devuelve un `ResultSet`. `executeUpdate()` es para **INSERT, UPDATE y DELETE** y devuelve un `int` con las filas afectadas.
</details>

### 10. ¿Por qué se usa try-with-resources en JDBC?
<details><summary>Respuesta</summary>

Porque `Connection`, `PreparedStatement` y `ResultSet` implementan `AutoCloseable`, y hay que cerrarlos siempre para no dejar conexiones abiertas ni fugas de memoria. El try-with-resources los cierra solo.
</details>

---

## Bloque 3: JPA y entidades

### 11. ⭐ ¿Qué es un ORM?
<details><summary>Respuesta</summary>

*Object-Relational Mapping*: la técnica que convierte automáticamente filas de tablas en objetos y al revés.
</details>

### 12. ⭐ ¿Qué diferencia hay entre JPA e Hibernate?
<details><summary>Respuesta</summary>

**JPA** es la **especificación**: define las anotaciones y la API. **Hibernate** es una **implementación** de JPA, el programa que lo hace funcionar.
</details>

### 13. ¿Qué ventajas tiene un ORM frente a JDBC?
<details><summary>Respuesta</summary>

Trabajas con objetos en lugar de tablas, te abstrae del dialecto SQL de cada proveedor y reduce mucho el código repetitivo.
</details>

### 14. ⭐ ¿Qué requisitos tiene una entidad JPA?
<details><summary>Respuesta</summary>

`@Entity`, un atributo con `@Id`, un **constructor vacío**, getters y setters, y que la clase **no sea `final`**.
</details>

### 15. ¿Qué es la "regla de oro" de las entidades?
<details><summary>Respuesta</summary>

Que el DDL de la tabla y las anotaciones Java cuenten **exactamente la misma historia**: mismos nombres, tipos, restricciones y claves.
</details>

### 16. ⭐ En una relación, ¿quién es la "parte propietaria" y qué anotación lleva?
<details><summary>Respuesta</summary>

La entidad cuya tabla **tiene físicamente la FK**. Lleva `@JoinColumn(name="...")`. La parte inversa usa `mappedBy`. En nuestro modelo el dueño es `Pedido`, porque tiene `cliente_id`.
</details>

### 17. ¿Qué anotación usarías para cada relación del modelo?
<details><summary>Respuesta</summary>

- Herencia de Cliente: `@Inheritance(strategy = JOINED)`.
- Persona/Empresa ↔ Cliente: `@OneToOne`.
- Pedido → Cliente: `@ManyToOne`.
- Cliente → lista de Pedidos: `@OneToMany(mappedBy = "cliente")`.
</details>

---

## Bloque 4: EntityManager y comparativa

### 18. ¿Qué diferencia hay entre `EntityManagerFactory` y `EntityManager`?
<details><summary>Respuesta</summary>

La **factory** es muy pesada y se crea **una vez** al arrancar. El **EntityManager** es ligero y se crea **uno por operación**. Es tu interfaz con la base de datos.
</details>

### 19. ¿Qué es el contexto de persistencia?
<details><summary>Respuesta</summary>

Un área de memoria (caché de primer nivel) donde Hibernate gestiona el estado de las entidades. Es como una "mesa de trabajo" donde se preparan los objetos antes de guardarlos en la base de datos.
</details>

### 20. ⭐ Di los cuatro estados de una entidad.
<details><summary>Respuesta</summary>

- **Transient:** recién creada con `new`. Hibernate no la conoce.
- **Managed:** vigilada. Los cambios se guardan solos al hacer commit.
- **Detached:** tiene id, pero la sesión se cerró y los cambios no se guardan.
- **Removed:** marcada para borrarse en el próximo flush o commit.
</details>

### 21. ⭐ ¿Qué método usas para cada operación CRUD?
<details><summary>Respuesta</summary>

Create: `persist()`. Read: `find()` o `createQuery()`. Update: `merge()`. Delete: `remove()`.
</details>

### 22. ¿Qué es JPQL y en qué se diferencia de SQL?
<details><summary>Respuesta</summary>

Es el lenguaje de consultas de JPA. Habla de **clases y atributos** (`SELECT p FROM Pedido p WHERE p.fecha > :fecha`), no de tablas y columnas.
</details>

### 23. ¿Qué diferencia hay entre LAZY y EAGER?
<details><summary>Respuesta</summary>

**LAZY** carga los datos relacionados solo cuando accedes a ellos. **EAGER** los carga inmediatamente, junto con la entidad.
</details>

### 24. ⭐ ¿Cuándo usarías JDBC y cuándo JPA?
<details><summary>Respuesta</summary>

**JDBC:** procesos ultramasivos o de latencia crítica, donde necesitas controlar el SQL al milímetro. **JPA:** el 95 % de las aplicaciones de empresa, porque el código es más limpio. Su riesgo es el problema **N+1** si no entiendes lo que genera.
</details>
