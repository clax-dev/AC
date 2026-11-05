# Tema 5: JSON, Jackson y APIs web, la parte técnica (para consultar)

> **Este archivo no se lee de un tirón: se consulta.** Si no entiendes **para qué sirve** algo, vuelve a `1_HISTORIA.md`.
> Métodos sueltos: `../GUIA_LIBRERIAS.md` (apartados 12 y 13).

**Índice:**
1. [Montar el proyecto (Jackson)](#1-montar-el-proyecto-jackson)
2. [La clase molde (POJO)](#2-la-clase-molde-pojo)
3. [JSON → objeto: `readValue`](#3-json--objeto-readvalue)
4. [Objeto → JSON: `writeValue`](#4-objeto--json-writevalue)
5. [Listas y objetos dentro de objetos](#5-listas-y-objetos-dentro-de-objetos)
6. [Recorrer el JSON como árbol: `JsonNode`](#6-recorrer-el-json-como-árbol-jsonnode)
7. [Leer un fichero de `resources`](#7-leer-un-fichero-de-resources)
8. [⭐ Plantilla: petición HTTP con `HttpClient`](#8--plantilla-petición-http-con-httpclient)
9. [Códigos de estado y errores](#9-códigos-de-estado-y-errores)
10. [Imports](#10-imports)

---

## 1. Montar el proyecto (Jackson)
Jackson es una librería **externa**: hace falta en el `pom.xml` (ya está en `ejercicios/src/tema5_json`).
```xml
<dependency>
    <groupId>com.fasterxml.jackson.core</groupId>
    <artifactId>jackson-databind</artifactId>
    <version>2.17.2</version>
</dependency>
```
`jackson-databind` trae solas `jackson-core` y `jackson-annotations`. `java.net.http` **no** se añade: viene con Java 11+.

---

## 2. La clase molde (POJO)
📝 `PersonaJSON`

```java
@JsonIgnoreProperties(ignoreUnknown = true)       // (opcional) ignora claves que no tenga la clase
public class Pelicula {
    private String titulo;
    private int anio;
    @JsonProperty("nota_media")                    // la clave del JSON se llama distinto
    private double notaMedia;

    public Pelicula() { }                          // ⚠ OBLIGATORIO para leer

    public String getTitulo() { return titulo; }   // getters → para escribir JSON
    public void setTitulo(String titulo) { this.titulo = titulo; }   // setters → para leer JSON
    // ... resto de getters y setters
}
```

### ⚠ Reglas
| Regla | Si no se cumple |
|---|---|
| Constructor **sin parámetros** | `InvalidDefinitionException` ("no Creators… exist") |
| Setters (o atributos `public`) para leer | el campo se queda en `null` / `0`, o error |
| Getters para escribir | ese campo no sale en el JSON |
| Mismo nombre que la clave | el campo no se rellena → `@JsonProperty("clave")` |
| Clave en el JSON que la clase no tiene | `UnrecognizedPropertyException` → `ignoreUnknown` o `FAIL_ON_UNKNOWN_PROPERTIES = false` |

> Un `record` también vale como molde (Jackson 2.12+): `record Pelicula(String titulo, int anio) {}`.

### Anotaciones útiles
| Anotación | Dónde | Qué hace |
|---|---|---|
| `@JsonIgnoreProperties(ignoreUnknown = true)` | clase | Ignora las claves del JSON que no estén en la clase |
| `@JsonProperty("clave_json")` | atributo | Empareja con una clave de nombre distinto |
| `@JsonIgnore` | atributo | No lo lee ni lo escribe (como `transient`) |

---

## 3. JSON → objeto: `readValue`
```java
ObjectMapper mapper = new ObjectMapper();                       // se crea una vez y se reutiliza
mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

Pelicula p1 = mapper.readValue(new File("pelicula.json"), Pelicula.class);   // desde un fichero
Pelicula p2 = mapper.readValue(textoJson, Pelicula.class);                  // desde un String
```
- El segundo parámetro es **la clase molde** (`Clase.class`).
- Lanza `IOException` (o sus hijas `JsonProcessingException`, `UnrecognizedPropertyException`…).

---

## 4. Objeto → JSON: `writeValue`
```java
mapper.writeValue(new File("salida.json"), pelicula);                        // a un fichero
String json = mapper.writeValueAsString(pelicula);                           // a un String (una línea)
String bonito = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(pelicula);  // con sangría
```

---

## 5. Listas y objetos dentro de objetos
```json
{ "titulo": "Up", "generos": ["animación", "aventura"], "director": { "nombre": "Pete Docter" } }
```
```java
public class Pelicula {
    private String titulo;
    private List<String> generos;      // [ ... ]  → List
    private Director director;         // { ... }  → otra clase molde
    // constructor vacío + getters/setters
}
```
**Un JSON que es una lista entera** (`[ {...}, {...} ]`):
```java
List<Pelicula> lista = mapper.readValue(json, new TypeReference<List<Pelicula>>() {});
Pelicula[] array     = mapper.readValue(json, Pelicula[].class);        // alternativa con array
```
> `List<Pelicula>.class` no existe (los genéricos se borran al compilar): por eso se usa `TypeReference`.

---

## 6. Recorrer el JSON como árbol: `JsonNode`
Para sacar datos sueltos **sin crear clases**.
```java
JsonNode raiz = mapper.readTree(textoJson);

String titulo = raiz.get("titulo").asText();                 // texto
int anio      = raiz.get("anio").asInt();                     // número
String dir    = raiz.get("director").get("nombre").asText();  // bajar niveles

for (JsonNode genero : raiz.get("generos")) {                // recorrer una lista [ ]
    System.out.println(genero.asText());
}
```
| Método | Qué hace |
|---|---|
| `get("clave")` | El hijo con esa clave; **`null`** si no existe |
| `path("clave")` | Igual, pero si no existe devuelve un nodo vacío (no `null`): no rompe la cadena |
| `get(i)` | El elemento `i` de una lista |
| `asText()` / `asInt()` / `asDouble()` / `asBoolean()` | El valor convertido |
| `isArray()` / `isObject()` / `size()` | ¿Es lista? ¿Es objeto? ¿Cuántos elementos? |
| `has("clave")` | ¿Existe esa clave? |

### ⚠ Reglas
- `get("x")` de una clave que no existe → `null` → el siguiente `.asText()` da `NullPointerException`. Con `path("x")` no pasa.
- `toString()` de un nodo devuelve el JSON; `asText()` devuelve el valor sin comillas.

---

## 7. Leer un fichero de `resources`
📝 `LectorFileJSONJackson`
```java
URL recurso = getClass().getClassLoader().getResource("persona.json");   // busca en src/main/resources
if (recurso == null) throw new IllegalArgumentException("no encontrado");
File fichero = new File(recurso.toURI());
```
- `src/main/resources` es la carpeta de **recursos** de Maven: al compilar se copia junto a las clases.
- Ventaja frente a `Paths.get("persona.json")`: **no depende del directorio de trabajo** (tema 4).
- Si la carpeta no está marcada como *Resources Root*, `getResource` devuelve `null`.

---

## 8. ⭐ Plantilla: petición HTTP con `HttpClient`
📝 `HttpRequestPokeAPI`

```java
private static final HttpClient CLIENTE = HttpClient.newHttpClient();    // 1. el cartero (uno para todo)

HttpRequest peticion = HttpRequest.newBuilder()                          // 2. la carta
        .uri(URI.create("https://api.ejemplo.com/v1/peliculas/42"))
        .header("Accept", "application/json")
        .GET()
        .build();

HttpResponse<String> respuesta = CLIENTE.send(peticion,                  // 3. enviar y ESPERAR
        HttpResponse.BodyHandlers.ofString());                          //    el cuerpo como String

if (respuesta.statusCode() < 200 || respuesta.statusCode() >= 300) {     // 4. ¿ha ido bien?
    throw new IOException("HTTP " + respuesta.statusCode());
}
String json = respuesta.body();                                          // 5. el JSON en texto → Jackson
```

### Las tres clases
| Clase | Es… | Métodos |
|---|---|---|
| `HttpClient` | el cartero | `newHttpClient()`, `send(req, handler)`, `sendAsync(req, handler)` |
| `HttpRequest` | la carta | `newBuilder()`, `.uri(URI)`, `.header(k, v)`, `.GET()`, `.POST(...)`, `.timeout(Duration)`, `.build()` |
| `HttpResponse<T>` | la respuesta | `statusCode()`, `body()`, `headers()` |

### Partes de una URL
```
https://pokeapi.co/api/v2/pokemon/charmander?limit=20&offset=0
└esquema┘└─ host ─┘└──── ruta (endpoint) ────┘└─ parámetros ─┘
```

### ⚠ Reglas
- `send` lanza `IOException` (sin red, servidor caído) e `InterruptedException`.
- `URI.create` con espacios o caracteres raros → `IllegalArgumentException`: limpia la entrada (`trim()`, `toLowerCase()`).
- El `HttpClient` se crea **una vez** (`static final`) y se reutiliza.
- `sendAsync` devuelve un `CompletableFuture<HttpResponse<T>>`: no bloquea (como el `Future` de PSP).

---

## 9. Códigos de estado y errores
| Código | Nombre | Cuándo |
|---|---|---|
| 200 | OK | Todo bien |
| 201 | Created | Se ha creado algo (tras un `POST`) |
| 400 | Bad Request | La petición está mal formada |
| 401 / 403 | Unauthorized / Forbidden | Falta permiso o clave |
| **404** | **Not Found** | **No existe** (un nombre mal escrito) |
| 429 | Too Many Requests | Demasiadas peticiones seguidas |
| 500 | Internal Server Error | Fallo del servidor |

| Excepción | Cuándo |
|---|---|
| `IOException` | Sin conexión, o la lanzas tú si el código no es 2xx |
| `UnrecognizedPropertyException` | Clave del JSON que la clase no tiene (y no la ignoras) |
| `InvalidDefinitionException` | La clase no tiene constructor vacío |
| `JsonParseException` | El texto no es un JSON válido (por ejemplo, una página de error HTML) |

---

## 10. Imports
```java
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
```
