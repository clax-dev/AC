# Tema 6: JDBC y JPA/Hibernate, la parte técnica (para consultar)

> **Cómo usar este archivo:**
> - **No es para leerlo de seguido.** Búscalo cuando estés programando.
> - La explicación de *por qué* funciona así está en `1_HISTORIA.md`.

---

## 1. JDBC: conexión y try-with-resources

```java
String url = "jdbc:postgresql://localhost:5432/basededatos";

try (Connection cn = DriverManager.getConnection(url, "usuario", "password");
     PreparedStatement ps = cn.prepareStatement(sql)) {
    // usar ps
} catch (SQLException e) {
    e.printStackTrace();
}
```

| Clase / interfaz (`java.sql`) | Para qué sirve |
|---|---|
| `DriverManager` | Abre la conexión con `getConnection(url, user, pass)` |
| `Connection` | La conexión abierta |
| `PreparedStatement` | Una consulta con huecos `?` |
| `ResultSet` | El resultado de un SELECT, que se recorre fila a fila |
| `SQLException` | La excepción de todo lo que falla en JDBC |

---

## 2. JDBC: INSERT, UPDATE y DELETE → `executeUpdate()`

```java
String sql = "INSERT INTO pedido (fecha, total, cliente_id) VALUES (?, ?, ?)";

try (Connection cn = DriverManager.getConnection(url, user, pass);
     PreparedStatement ps = cn.prepareStatement(sql)) {

    ps.setDate(1, Date.valueOf(LocalDate.now()));   // los huecos empiezan en 1
    ps.setBigDecimal(2, new BigDecimal("125.50"));
    ps.setLong(3, 1L);

    int filas = ps.executeUpdate();   // devuelve cuántas filas ha tocado
}
```

---

## 3. JDBC: SELECT → `executeQuery()` y `while (rs.next())`

```java
String sql = "SELECT id, fecha, total FROM pedido WHERE fecha > ?";

try (Connection cn = DriverManager.getConnection(url, user, pass);
     PreparedStatement ps = cn.prepareStatement(sql)) {

    ps.setDate(1, Date.valueOf(LocalDate.of(2026, 2, 15)));   // LocalDate → java.sql.Date

    try (ResultSet rs = ps.executeQuery()) {
        while (rs.next()) {                     // avanza una fila; false cuando no quedan
            long id = rs.getLong("id");
            LocalDate fecha = rs.getDate("fecha").toLocalDate();
            BigDecimal total = rs.getBigDecimal("total");
            // aquí "mapeas" la fila a un objeto Pedido
        }
    }
}
```

| Método | Cuándo |
|---|---|
| `executeQuery()` | SELECT. Devuelve un `ResultSet`. |
| `executeUpdate()` | INSERT, UPDATE o DELETE. Devuelve un `int` con las filas afectadas. |

---

## 4. JDBC: transacción a mano

```java
cn.setAutoCommit(false);        // por defecto cada sentencia se confirma sola
try {
    // varias sentencias...
    cn.commit();
} catch (SQLException e) {
    cn.rollback();
}
```

---

## 5. Entidad JPA

```java
@Entity
@Table(name = "pedido")
public class Pedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)   // autoincremental de la BD
    private Long id;

    @Column(nullable = false)
    private LocalDate fecha;

    @Column(nullable = false, precision = 10, scale = 2)  // NUMERIC(10,2)
    private BigDecimal total;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id", nullable = false)    // Pedido tiene la FK → es el DUEÑO
    private Cliente cliente;

    public Pedido() { }    // OBLIGATORIO: constructor vacío

    // getters y setters...
}
```

**Requisitos:** `@Entity`, un `@Id`, el constructor vacío, getters y setters, y que la clase no sea `final`.

---

## 6. Relaciones

```java
// Lado "uno" (Cliente): NO tiene la FK → mappedBy apunta al ATRIBUTO del otro lado
@OneToMany(mappedBy = "cliente")
private List<Pedido> pedidos = new ArrayList<>();

// Lado "muchos" (Pedido): SÍ tiene la FK → @JoinColumn con el nombre de la COLUMNA
@ManyToOne
@JoinColumn(name = "cliente_id")
private Cliente cliente;

// 1:1 (Persona → Cliente)
@OneToOne
@JoinColumn(name = "cliente_id", unique = true)
private Cliente cliente;

// Herencia en la superclase
@Entity
@Inheritance(strategy = InheritanceType.JOINED)   // o SINGLE_TABLE
public class Cliente { ... }
```

| Anotación | Relación |
|---|---|
| `@OneToOne` | 1:1 |
| `@OneToMany` | 1:N (el lado "uno", que tiene la lista) |
| `@ManyToOne` | N:1 (el lado "muchos", que tiene la FK) |
| `@JoinColumn(name="...")` | El **dueño**: nombre de la columna FK |
| `mappedBy = "..."` | El lado **inverso**: nombre del **atributo** en el dueño |
| `fetch = FetchType.LAZY` / `EAGER` | Cargar las relaciones al usarlas o de golpe |

---

## 7. Configuración: `persistence.xml` (sin Spring)

Va en `src/main/resources/META-INF/persistence.xml`:

```xml
<persistence-unit name="miUnidad">
    <class>es.dam.Cliente</class>
    <class>es.dam.Pedido</class>
    <properties>
        <property name="jakarta.persistence.jdbc.url" value="jdbc:postgresql://localhost:5432/basededatos"/>
        <property name="jakarta.persistence.jdbc.user" value="usuario"/>
        <property name="jakarta.persistence.jdbc.password" value="password"/>
        <property name="hibernate.dialect" value="org.hibernate.dialect.PostgreSQLDialect"/>
        <property name="hibernate.show_sql" value="true"/>
    </properties>
</persistence-unit>
```

---

## 8. EntityManager: plantilla de uso

```java
// UNA vez en toda la aplicación (es muy pesado)
EntityManagerFactory emf = Persistence.createEntityManagerFactory("miUnidad");

// UNO por operación
try (EntityManager em = emf.createEntityManager()) {
    EntityTransaction tx = em.getTransaction();
    try {
        tx.begin();
        // ... operaciones ...
        tx.commit();
    } catch (Exception e) {
        tx.rollback();
    }
}
```

---

## 9. CRUD con EntityManager

```java
// CREATE
Pedido p = new Pedido();                       // TRANSIENT
p.setCliente(em.find(Cliente.class, 1L));
em.persist(p);                                 // → MANAGED (INSERT al hacer commit)

// READ por PK
Cliente c = em.find(Cliente.class, 1L);        // null si no existe

// READ con JPQL y parámetro con nombre
List<Pedido> lista = em.createQuery(
        "SELECT p FROM Pedido p WHERE p.fecha > :fecha", Pedido.class)
    .setParameter("fecha", LocalDate.of(2026, 2, 15))
    .getResultList();

// UPDATE de un objeto MANAGED: basta con el setter
c.setActivo(false);                            // UPDATE automático al hacer commit

// UPDATE de un objeto DETACHED
Cliente gestionado = em.merge(clienteSuelto);

// DELETE
em.remove(c);                                  // tiene que estar MANAGED
```

**JPQL usa clases y atributos**, no tablas: `Pedido p` y `p.fecha`, no `pedido` y `fecha`.

---

## 10. Estados de una entidad

| Estado | Cómo llega | ¿Hibernate lo vigila? |
|---|---|---|
| TRANSIENT | `new` | ❌ No |
| MANAGED | `persist()`, `find()`, `merge()` | ✅ Sí: los cambios se guardan solos |
| DETACHED | Se cerró el EntityManager | ❌ No: los cambios se pierden |
| REMOVED | `remove()` | Se borra en el commit |

---

## 11. Errores típicos

| Error | Causa |
|---|---|
| `No suitable driver found` | Falta la dependencia del driver o la URL está mal escrita |
| `ResultSet` vacío, o el primer `getX` falla | No has llamado a `rs.next()` antes de leer |
| `Parameter index out of range` | Los `?` empiezan en **1**, no en 0 |
| `No default constructor for entity` | A la entidad le falta el constructor vacío |
| Borrar un cliente con pedidos falla | La FK tiene `ON DELETE RESTRICT`. Es lo esperado. |
| `LazyInitializationException` | Accedes a una relación LAZY con la sesión ya cerrada (ver tema 7) |
| `remove()` lanza `IllegalArgumentException` | El objeto está DETACHED. Haz `merge()` o `find()` antes. |

---

## 12. Imports

```java
import java.sql.*;                  // Connection, DriverManager, PreparedStatement, ResultSet, SQLException, Date
import java.math.BigDecimal;
import java.time.LocalDate;
import jakarta.persistence.*;       // Entity, Id, Column, ManyToOne, EntityManager, Persistence...
```
