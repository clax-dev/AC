# Tema 6: JDBC y JPA/Hibernate, contado como una historia

> **Cómo usar este archivo:**
> - Léelo **de un tirón**, como un cuento. Cada bloque tiene una **💬 frase clave**.
> - Ten abierto `visual.pdf` al lado. La sintaxis está en `2_TECNICO.md`, y para comprobarte tienes `3_PREGUNTAS.md`.
> - Las páginas 📄 son las del PDF de clase (`05_JDBC_introJPAHibernate.pdf`).
> - Código del tema: `src/t6_jdbc_jpa/`.

**La historia entera en tres líneas:**
- Java habla en **objetos** y la base de datos habla en **tablas**. Hace falta un puente.
- **JDBC** es el puente a mano: escribes tú el SQL, abres y cierras todo, y pasas cada fila a objeto.
- **JPA/Hibernate** es el puente automático: cada **clase** es una tabla, cada **objeto** es una fila, y el SQL lo escribe él.

---

## 1. El terreno de juego: tablas y transacciones
📄 *PDF, página 2*

> 💬 **"Una transacción es todo o nada: o se guarda el cliente y su pedido, o no se guarda ninguno."**

Una base de datos relacional son **tablas** unidas por claves:
- **PK (clave primaria):** el DNI de cada fila. Es único.
- **FK (clave foránea):** una columna que apunta a la PK de otra tabla. Por ejemplo, `pedido.cliente_id` apunta a `cliente.id`.

Una **transacción** es un bloque de operaciones que va junto. Si todo va bien, se hace **COMMIT** (se confirma). Si algo falla, se hace **ROLLBACK** (se deshace entero).

Las cuatro garantías de una transacción se llaman **ACID**:

| Letra | Nombre | Qué significa |
|---|---|---|
| **A** | Atomicidad | Todo o nada. No hay operaciones a medias. |
| **C** | Consistencia | Las reglas del esquema (UNIQUE, FK…) se cumplen siempre. |
| **I** | Aislamiento | Dos transacciones a la vez no se pisan. *(Te suena de PSP: es concurrencia)* |
| **D** | Durabilidad | Lo confirmado sobrevive aunque se caiga el servidor. |

---

## 2. El modelo de clase: cliente, persona, empresa y pedido
📄 *PDF, página 3*

> 💬 **"Un cliente puede ser persona o empresa, y tiene muchos pedidos."**

Este es el modelo que usaréis **en todo**: JDBC, JPA, la API REST y la seguridad.

```
              cliente (la "superclase")
              id, codigo, tipo, fecha_alta, activo
               │                       │ 1
       ┌───────┴───────┐               │
    persona         empresa            │ N
    (cliente_id)    (cliente_id)     pedido
    1:1             1:1              (cliente_id)
```

- `persona` y `empresa` se unen a `cliente` en **1:1** con `ON DELETE CASCADE`: si borras el cliente, se borra su persona o empresa.
- `pedido` se une a `cliente` en **1:N** con `ON DELETE RESTRICT`: **no** te deja borrar un cliente que tenga pedidos.

La base de datos corre en **Docker** (MySQL o PostgreSQL).

---

## 3. JDBC: el puente a mano
📄 *PDF, página 4*

> 💬 **"JDBC no es una base de datos: es el idioma estándar con el que Java le habla a cualquiera."**

```
Tu código Java  →  API JDBC (java.sql)  →  Driver (PostgreSQL)  →  Base de datos
                   interfaces estándar      la "traducción"
```

- La **API JDBC** son interfaces (`Connection`, `PreparedStatement`, `ResultSet`) que son iguales para todas las bases de datos.
- El **driver** es la librería concreta de cada motor. Si cambias de MySQL a PostgreSQL, cambias el driver y no tu código.

**El precio:** todo es manual. Abres la conexión, escribes el SQL, recorres el resultado fila a fila y lo cierras todo. Es mucho código repetido (*boilerplate*).

---

## 4. Statement vs PreparedStatement
📄 *PDF, página 5*

> 💬 **"Nunca pegues lo que escribe el usuario dentro del SQL. Usa `?`."**

- **`Statement`** concatena Strings: `"WHERE user = '" + input + "'"`. Si alguien escribe `' OR '1'='1`, se cuela en tu consulta. Eso es **SQL Injection**.
- **`PreparedStatement`** usa huecos `?` y luego rellena cada hueco con su tipo (`setString`, `setLong`, `setDate`…). Es **seguro**, y además va **más rápido** si repites la consulta porque la base de datos la deja precompilada.

Y todo lo que se abre se cierra con **try-with-resources**, igual que los ficheros del tema 4.

---

## 5. El salto: ORM, JPA e Hibernate
📄 *PDF, página 8*

> 💬 **"ORM traduce tablas a objetos: tú trabajas con `Cliente`, y él se encarga de la tabla `cliente`."**

**ORM** (*Object-Relational Mapping*) es la técnica de convertir automáticamente filas en objetos y objetos en filas.

- **JPA** es la **norma** (la especificación): dice qué anotaciones y qué métodos existen.
- **Hibernate** es la **implementación**: el programa que de verdad lo hace.

Es como JDBC y su driver: JPA es el "qué" y Hibernate es el "cómo".

¿Para qué sirve?
- Pasas de pensar en **tablas y filas** a pensar en **clases, objetos y herencia**.
- No dependes del dialecto SQL de cada base de datos.
- Escribes **muchísimo menos código**.

---

## 6. La entidad: una clase que es una tabla
📄 *PDF, página 11*

> 💬 **"La tabla SQL y la clase Java tienen que contar exactamente la misma historia."**

Una **entidad** es una clase marcada con `@Entity`. Cada **objeto** de esa clase es una **fila**.

| Java | SQL |
|---|---|
| `@Entity @Table(name="cliente")` | `CREATE TABLE cliente` |
| `@Id @GeneratedValue(IDENTITY)` | `id ... PRIMARY KEY` autoincremental |
| `@Column(name="codigo_negocio", unique=true, nullable=false)` | `codigo_negocio VARCHAR UNIQUE NOT NULL` |

**Requisitos de una entidad:** un constructor vacío, getters y setters, y que la clase **no** sea `final`.

---

## 7. Las relaciones
📄 *PDF, página 12*

> 💬 **"Quien tiene la FK en la tabla es el dueño: lleva `@JoinColumn`. El otro lado lleva `mappedBy`."**

| Anotación | Relación | En nuestro modelo |
|---|---|---|
| `@Inheritance(strategy = JOINED)` | Herencia | `Cliente` es la superclase |
| `@OneToOne` | 1:1 | `Persona` / `Empresa` ↔ `Cliente` |
| `@ManyToOne` / `@OneToMany` | N:1 / 1:N | Muchos `Pedido` → un `Cliente` |

La tabla `pedido` tiene la columna `cliente_id`, así que **`Pedido` es el dueño** y lleva `@JoinColumn(name="cliente_id")`. `Cliente` tiene la lista de pedidos con `@OneToMany(mappedBy = "cliente")`.

---

## 8. El EntityManager y su "mesa de trabajo"
📄 *PDF, páginas 9 y 10*

> 💬 **"El contexto de persistencia es una mesa de trabajo: Hibernate vigila lo que hay encima y lo guarda al hacer commit."**

La configuración va de lo pesado a lo ligero:

```
persistence.xml          → la configuración: credenciales, dialecto y lista de @Entity
EntityManagerFactory     → MUY pesado. Se crea UNA vez al arrancar.
EntityManager            → ligero. Uno por operación. Es tu mando para hablar con la BD.
EntityTransaction        → begin(), commit(), rollback()  (el ACID del punto 1)
```

Un objeto pasa por **cuatro estados**:

```
new Cliente()  ──persist()──▶  MANAGED  ──remove()──▶  REMOVED
TRANSIENT                       (vigilado)               (se borra en el commit)
(Hibernate no lo conoce)          │
                                  │ se cierra la sesión
                                  ▼
                               DETACHED  ──merge()──▶  vuelve a MANAGED
                               (tiene id, pero nadie lo vigila)
```

La magia de **MANAGED**: si haces `cliente.setActivo(false)` sobre un objeto vigilado, Hibernate genera el `UPDATE` **solo** al hacer commit. No llamas a ningún "guardar".

---

## 9. El CRUD con EntityManager
📄 *PDF, página 13*

> 💬 **"persist crea, find lee, merge actualiza, remove borra."**

| Operación | Método | Nota |
|---|---|---|
| **C**reate | `em.persist(obj)` | De TRANSIENT a MANAGED |
| **R**ead | `em.find(Cliente.class, id)` | Busca por PK |
| **R**ead | `em.createQuery(jpql, Cliente.class)` | Búsquedas complejas en **JPQL** |
| **U**pdate | `em.merge(obj)` | Reengancha un DETACHED |
| **D**elete | `em.remove(obj)` | Lo marca para borrarlo en el commit |

**JPQL** se parece a SQL, pero habla de **clases y atributos** en lugar de tablas y columnas: `SELECT c FROM Cliente c WHERE c.activo = true`.

---

## 10. El duelo final: JDBC vs JPA
📄 *PDF, página 16*

> 💬 **"JDBC para controlarlo todo al milímetro; JPA para el 95 % de las aplicaciones."**

| | JDBC | JPA / Hibernate |
|---|---|---|
| Piensas en | Filas y columnas | Objetos, grafos y herencia |
| Abstracción | Baja (SQL a mano) | Alta (API y JPQL) |
| Código | Mucho y repetido | Limpio y corto |
| Rendimiento | Control total del SQL | Automático, con riesgo de **N+1** si no entiendes lo que hace |
| Cuándo | Procesos masivos o donde cada milisegundo cuenta | El 95 % de las aplicaciones de empresa |

**LAZY vs EAGER**, que volverá en el tema 7:
- **LAZY:** carga los datos relacionados **solo cuando los tocas**.
- **EAGER:** los carga **todos de golpe** al principio.

---

## 11. Resumen

- Java habla en objetos y la base de datos en tablas. **JDBC** y **JPA** son dos puentes.
- **JDBC:** `Connection` → `PreparedStatement` con `?` → `ResultSet` con `while (rs.next())`. Todo dentro de try-with-resources.
- **JPA** es la norma e **Hibernate** la implementación. Una **`@Entity`** es una tabla, y cada objeto una fila.
- El dueño de una relación es quien tiene la FK y lleva `@JoinColumn`; el otro lado lleva `mappedBy`.
- Estados: **TRANSIENT → MANAGED → DETACHED / REMOVED**.
- En el tema 7, Spring Data te escribe hasta el `EntityManager`: eso es el **Repository**.
