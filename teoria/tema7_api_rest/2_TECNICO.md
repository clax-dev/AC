# Tema 7: APIs REST con Spring Boot, la parte técnica (para consultar)

> **Este archivo no se lee de un tirón: se consulta.** Si no entiendes **para qué sirve** algo, vuelve a `1_HISTORIA.md`.
> Anotaciones y métodos sueltos: `../GUIA_LIBRERIAS.md` (apartado 14).
> Los ejemplos de DTO, Mapper y Service usan `Producto` a propósito: son el molde, y la versión de `Cliente` te toca a ti.

**Índice:**
1. [Crear el proyecto](#1-crear-el-proyecto)
2. [`application.properties`](#2-applicationproperties)
3. [Paquetes y clase principal](#3-paquetes-y-clase-principal)
4. [Entidades](#4-entidades)
5. [Repository](#5-repository)
6. [Controller](#6-controller)
7. [⭐ Plantilla: DTO, Mapper y Service](#7--plantilla-dto-mapper-y-service)
8. [Validación](#8-validación)
9. [Probar con curl](#9-probar-con-curl)
10. [⚠ El error LAZY y el bucle infinito](#10--el-error-lazy-y-el-bucle-infinito)
11. [Errores típicos](#11-errores-típicos)
12. [Imports](#12-imports)

---

## 1. Crear el proyecto
📄 *PDF, páginas 11 a 14*

En `start.spring.io`: **Maven · Java 25 · Jar · Properties · Spring Boot 4.1.2**.

| Dependencia | ¿Hace falta? | Qué aporta |
|---|---|---|
| Spring Web | Sí | `@RestController`, mappings, servidor embebido y Jackson |
| Spring Data JPA | Sí | `JpaRepository`, Hibernate y `jakarta.persistence` |
| PostgreSQL Driver | Sí | La conexión JDBC con PostgreSQL |
| Validation | Recomendable | `@Valid`, `@NotBlank` |
| DevTools | Opcional | Reinicio automático. No se usa en producción |
| Lombok | **No** | En esta práctica se escriben getters y setters a mano |

⚠ **No añadas Hibernate ni `jakarta.persistence-api` a mano:** ya vienen con Spring Data JPA.

---

## 2. `application.properties`
📄 *PDF, página 15*

Está en `src/main/resources/application.properties`:
```properties
spring.application.name=clientes-api
server.port=8080

spring.datasource.url=jdbc:postgresql://localhost:5432/NOMBRE_BD
spring.datasource.username=TU_USUARIO
spring.datasource.password=TU_CONTRASENA
spring.datasource.driver-class-name=org.postgresql.Driver

spring.jpa.hibernate.ddl-auto=validate
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
spring.jpa.open-in-view=false
```

| Valor de `ddl-auto` | Comportamiento |
|---|---|
| `validate` | ✅ Comprueba que las entidades coinciden con las tablas |
| `update` | Intenta modificar la estructura |
| `create` | ⚠ **Borra** y crea las tablas al arrancar |
| `create-drop` | ⚠ Crea las tablas y las elimina al cerrar |

- `show-sql` y `format_sql` enseñan en consola el SQL que genera Hibernate.
- `open-in-view=false` cierra la sesión antes de escribir el JSON. Por eso aparece el error del [apartado 10](#10--el-error-lazy-y-el-bucle-infinito).

---

## 3. Paquetes y clase principal
📄 *PDF, páginas 16 y 17*

```
es.dam.clientesAPI
├── ClientesApplication.java     ← en la raíz
├── controller
├── service
├── repository
├── entity
├── dto
└── mapper
```

```java
@SpringBootApplication
public class ClientesApplication {
    public static void main(String[] args) {
        SpringApplication.run(ClientesApplication.class, args);
    }
}
```

| `@SpringBootApplication` agrupa | Qué hace |
|---|---|
| `@Configuration` | La clase puede contener configuración |
| `@EnableAutoConfiguration` | Configura servidor, Spring MVC, Jackson, JPA, Hibernate, datasource y repositorios |
| `@ComponentScan` | Busca `@RestController`, `@Service`, `@Repository` y `@Component` |

⚠ `@ComponentScan` mira **ese paquete y los de debajo**. Lo que esté fuera no se detecta.

---

## 4. Entidades
📄 *PDF, páginas 8 y 20*

Son las del tema 6, sin cambios:
```java
@Entity @Table(name = "cliente")
public class Cliente {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 20)
    private String codigo;

    @Enumerated(EnumType.STRING) @Column(nullable = false)
    private TipoCliente tipo;

    @Column(name = "fecha_alta", nullable = false)
    private LocalDate fechaAlta;

    @Column(nullable = false)
    private boolean activo = true;

    @OneToMany(mappedBy = "cliente")
    private List<Pedido> pedidos = new ArrayList<>();

    public Cliente() {}                    // ⚠ OBLIGATORIO para JPA
    // constructor con datos, getters y setters
}
```
```java
@Entity @Table(name = "pedido")
public class Pedido {
    // id, fecha y total
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente;
}
```

---

## 5. Repository
📄 *PDF, páginas 12 y 21*

```java
public interface ClienteRepository extends JpaRepository<Cliente, Long> {
    Optional<Cliente> findByCodigo(String codigo);
    List<Cliente> findByActivoTrueOrderByCodigoAsc();
}
```
`JpaRepository<Cliente, Long>` significa: *entidad `Cliente`, con un id de tipo `Long`*. No hay que implementar nada.

| Heredado | Devuelve |
|---|---|
| `findAll()` | `List<Cliente>` |
| `findById(id)` | `Optional<Cliente>` |
| `save(cliente)` | La entidad guardada (crea o actualiza) |
| `delete(cliente)` | `void` |
| `count()` | `long` |
| `existsById(id)` | `boolean` |

**Consultas por el nombre del método:**

| Trozo | Significa |
|---|---|
| `findBy` | Empieza la búsqueda |
| `Codigo` | `WHERE codigo = ?` |
| `ActivoTrue` | `WHERE activo = true` |
| `OrderByCodigoAsc` | `ORDER BY codigo ASC` |

⚠ Los nombres son los de los **atributos Java**, no los de las columnas: `findByFechaAlta`, no `findByFecha_alta`.

---

## 6. Controller
📄 *PDF, páginas 18, 19 y 22*

```java
@RestController
@RequestMapping("/api/clientes")
public class ClienteController {

    private final ClienteRepository clienteRepository;

    public ClienteController(ClienteRepository clienteRepository) {
        this.clienteRepository = clienteRepository;      // Spring lo pasa solo
    }

    @GetMapping                                          // GET /api/clientes
    public List<Cliente> listarClientes() {
        return clienteRepository.findAll();
    }

    @GetMapping("/{id}")                                 // GET /api/clientes/7
    public Cliente buscarClientePorId(@PathVariable Long id) {
        return clienteRepository.findById(id).orElse(null);
    }
}
```
> ⚠ Esta es la **primera prueba** de clase: expone la entidad, se salta el Service y falla con el [error LAZY](#10--el-error-lazy-y-el-bucle-infinito). La versión final usa el apartado 7.

| De dónde sale el dato | Anotación | Ejemplo |
|---|---|---|
| Un trozo de la URL | `@PathVariable` | `/api/clientes/7` |
| Parámetro tras `?` | `@RequestParam` | `/api/clientes?tipo=EMPRESA` |
| El JSON del cuerpo | `@RequestBody` | Un `POST` o `PUT` |

Para devolver **201 Created** al crear (por defecto es 200 OK):
```java
@PostMapping
@ResponseStatus(HttpStatus.CREATED)
public ProductoResponseDto crear(@RequestBody ProductoRequestDto dto) { ... }
```

**Cuando el id no existe** (esto no está en el PDF): con `.orElse(null)` la respuesta es **200 sin contenido**, aunque el recurso no exista. Para responder 404 se puede lanzar una excepción:
```java
.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
```

---

## 7. ⭐ Plantilla: DTO, Mapper y Service
📄 *PDF, páginas 9, 16 y 23*

```
Controller  ← recibe y devuelve DTOs
   ↓
Service     ← usa el Mapper
   ↓
Repository  ← recibe y devuelve entidades
```

**DTO:** un `record` es lo más corto. Una clase normal con getters también vale.
```java
public record ProductoRequestDto(String nombre, BigDecimal precio) { }
public record ProductoResponseDto(Long id, String nombre) { }
```

**Mapper:**
```java
@Component
public class ProductoMapper {
    public Producto aEntidad(ProductoRequestDto dto) {
        return new Producto(dto.nombre(), dto.precio());
    }
    public ProductoResponseDto aDto(Producto p) {
        return new ProductoResponseDto(p.getId(), p.getNombre());
    }
}
```

**Service:**
```java
@Service
public class ProductoService {
    private final ProductoRepository repo;
    private final ProductoMapper mapper;

    public ProductoService(ProductoRepository repo, ProductoMapper mapper) {
        this.repo = repo;
        this.mapper = mapper;
    }

    @Transactional(readOnly = true)
    public List<ProductoResponseDto> listar() {
        return repo.findAll().stream().map(mapper::aDto).toList();
    }

    @Transactional
    public ProductoResponseDto crear(ProductoRequestDto dto) {
        Producto guardado = repo.save(mapper.aEntidad(dto));
        return mapper.aDto(guardado);
    }
}
```

**Controller:** solo habla con el Service.
```java
@RestController
@RequestMapping("/api/productos")
public class ProductoController {
    private final ProductoService service;

    public ProductoController(ProductoService service) {
        this.service = service;
    }

    @GetMapping
    public List<ProductoResponseDto> listar() {
        return service.listar();
    }
}
```

---

## 8. Validación
📄 *PDF, página 13*

Las reglas van en el DTO que entra, y `@Valid` las activa en el Controller:
```java
public record ProductoRequestDto(@NotBlank String nombre, BigDecimal precio) { }

@PostMapping
public ProductoResponseDto crear(@Valid @RequestBody ProductoRequestDto dto) { ... }
```
⚠ Sin `@Valid`, la regla `@NotBlank` no se comprueba. Si un dato no es válido, la API responde **400 Bad Request**.

---

## 9. Probar con curl
📄 *PDF, páginas 18 y 22*

```bash
curl http://localhost:8080/hola
```
```bash
curl http://localhost:8080/api/clientes
```
```bash
curl http://localhost:8080/api/clientes/1
```
Crear algo con un `POST` (en la terminal Bash de IntelliJ o Git Bash):
```bash
curl -X POST http://localhost:8080/api/productos -H "Content-Type: application/json" -d '{"nombre":"Teclado","precio":25.50}'
```

| Código | Significa |
|---|---|
| **200** OK | Todo bien |
| **201** Created | Recurso creado |
| **400** Bad Request | Datos no válidos o JSON mal escrito |
| **404** Not Found | La ruta o el recurso no existe |
| **415** Unsupported Media Type | Falta `Content-Type: application/json` |
| **500** Internal Server Error | Excepción en tu código |

---

## 10. ⚠ El error LAZY y el bucle infinito
📄 *PDF, página 23*

**Qué pasa:** `pedidos` es `LAZY` y Jackson intenta leerlo con la sesión ya cerrada. Salta una `LazyInitializationException`.

**Opción 1, `EAGER`** (rápida, pero trae los pedidos siempre):
```java
// En Cliente
@OneToMany(mappedBy = "cliente", fetch = FetchType.EAGER)
private List<Pedido> pedidos = new ArrayList<>();

// En Pedido: corta el bucle Cliente → pedidos → Cliente → …
@ManyToOne(fetch = FetchType.LAZY)
@JoinColumn(name = "cliente_id", nullable = false)
@JsonIgnore
private Cliente cliente;
```

**Opción 2, DTO** ✅: pasos en orden:
1. Crear el DTO de respuesta.
2. Crear el Service, que convierte las entidades en DTO.
3. Cambiar el Controller para que use el Service y devuelva el DTO.

La plantilla es la del [apartado 7](#7--plantilla-dto-mapper-y-service).

---

## 11. Errores típicos

| Síntoma | Causa probable |
|---|---|
| `LazyInitializationException` | Lees una relación `LAZY` con la sesión cerrada |
| JSON infinito o `Infinite recursion` | Relación en los dos sentidos: usa DTO o `@JsonIgnore` |
| 404 en una ruta que has escrito | URL distinta, o el Controller está fuera del paquete raíz |
| `No qualifying bean of type…` | Falta `@Service` o `@Component`, o la clase está fuera del paquete raíz |
| `Schema-validation: missing table/column` | Con `validate`, la entidad no coincide con la tabla |
| `Connection refused` | PostgreSQL no está arrancado, o el puerto es otro |
| `password authentication failed` | Usuario o contraseña mal en `application.properties` |
| `Port 8080 was already in use` | Ya tienes otra instancia arrancada |
| 415 en un `POST` | Falta la cabecera `Content-Type: application/json` |
| `No property 'x' found for type` | El nombre del método del repositorio no coincide con el atributo |

---

## 12. Imports

```java
// Arranque
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

// Controller
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

// Service y Mapper
import org.springframework.stereotype.Service;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

// Repository
import org.springframework.data.jpa.repository.JpaRepository;

// Entidades
import jakarta.persistence.*;

// Validación
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;

// Jackson
import com.fasterxml.jackson.annotation.JsonIgnore;
```
