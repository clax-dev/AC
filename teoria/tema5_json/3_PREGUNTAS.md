# Tema 5: JSON, Jackson y APIs web, preguntas para comprobarte

> **Cómo usar este archivo:**
> - Lee la pregunta, **contesta en voz alta** y solo después abre la respuesta.
> - Marca con ✅ las que te salen. Las de ⭐ son las más preguntadas.

## Progreso
- [ ] Bloque 1: JSON (1-4)
- [ ] Bloque 2: Jackson (5-12)
- [ ] Bloque 3: HTTP y APIs (13-20)

---

## Bloque 1: JSON

### 1. ⭐ ¿Qué es JSON y por qué se usa tanto?
<details><summary>Respuesta</summary>

Un formato de **texto** para representar objetos: `{}` para un objeto, `[]` para una lista y `"clave": valor` para cada atributo. Se usa porque es **legible**, lo entiende **cualquier lenguaje** y permite **estructura** (objetos dentro de objetos, listas).
</details>

### 2. ⭐ Compara JSON con CSV y con la serialización de Java.
<details><summary>Respuesta</summary>

CSV es legible y multilenguaje, pero **plano** (no hay objetos dentro de objetos). La serialización guarda objetos enteros, pero es **binaria y solo Java**. JSON es **legible, multilenguaje y con estructura**.
</details>

### 3. ¿Qué tipo Java corresponde a `[ ]` y a `{ }`?
<details><summary>Respuesta</summary>

`[ ]` es una lista → `List` o array. `{ }` es un objeto → una clase Java (o un `JsonNode` / `Map`).
</details>

### 4. ¿Qué tipos de valor admite JSON?
<details><summary>Respuesta</summary>

Texto (`"..."`), número, booleano (`true`/`false`), `null`, objeto (`{}`) y lista (`[]`).
</details>

---

## Bloque 2: Jackson

### 5. ⭐ ¿Qué hace `ObjectMapper`?
<details><summary>Respuesta</summary>

Es el **traductor** de Jackson: `readValue` convierte JSON → objeto (**deserializar**) y `writeValue` / `writeValueAsString` convierte objeto → JSON (**serializar**).
</details>

### 6. ⭐ ¿Qué necesita una clase para que Jackson pueda rellenarla?
<details><summary>Respuesta</summary>

Un **constructor sin parámetros**, **setters** (o atributos públicos) y que los nombres coincidan con las claves del JSON. Para escribir JSON, **getters**.
</details>

### 7. ⭐ El JSON trae una clave que tu clase no tiene. ¿Qué pasa? ¿Cómo se evita?
<details><summary>Respuesta</summary>

Por defecto, `UnrecognizedPropertyException`. Se evita con `mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)` o con `@JsonIgnoreProperties(ignoreUnknown = true)` en la clase. Imprescindible con APIs que devuelven muchos campos.
</details>

### 8. La clave del JSON es `"nota_media"` y tu atributo `notaMedia`. ¿Qué haces?
<details><summary>Respuesta</summary>

`@JsonProperty("nota_media")` encima del atributo.
</details>

### 9. ¿Qué segundo parámetro lleva `readValue`?
<details><summary>Respuesta</summary>

La **clase molde**: `mapper.readValue(json, Pelicula.class)`. Para una lista: `new TypeReference<List<Pelicula>>() {}`.
</details>

### 10. ⭐ ¿Cuándo usarías `readTree` en vez de `readValue`?
<details><summary>Respuesta</summary>

Cuando solo quieres **un par de datos** de un JSON grande, o no conoces bien su forma: lo recorres como un árbol (`get("clave")`, `for` en las listas) sin crear clases.
</details>

### 11. ¿Qué diferencia hay entre `get("x")` y `path("x")` en un `JsonNode`?
<details><summary>Respuesta</summary>

Si la clave no existe, `get` devuelve **`null`** (y el siguiente método da `NullPointerException`); `path` devuelve un nodo vacío y no rompe la cadena.
</details>

### 12. ¿Por qué `getClassLoader().getResource("persona.json")` es mejor que `Paths.get("persona.json")`?
<details><summary>Respuesta</summary>

Porque busca en la carpeta de recursos, que se copia junto a las clases compiladas, y **no depende del directorio de trabajo**. Con `Paths.get`, la ruta relativa depende de desde dónde se ejecute (tema 4).
</details>

---

## Bloque 3: HTTP y APIs

### 13. ⭐ ¿Qué es una API REST? ¿Es PokeAPI una librería?
<details><summary>Respuesta</summary>

Un **servicio en internet** al que se le pregunta con URLs (endpoints) y que contesta normalmente con JSON. PokeAPI **no** es una librería: no se instala, se le hacen peticiones HTTP.
</details>

### 14. ⭐ ¿Qué papel tienen `HttpClient`, `HttpRequest` y `HttpResponse`?
<details><summary>Respuesta</summary>

`HttpClient` es el **cartero** (envía), `HttpRequest` es la **carta** (URL, cabeceras, método) y `HttpResponse` es la **respuesta** (código de estado y cuerpo).
</details>

### 15. ¿Hay que añadir `java.net.http` al `pom.xml`?
<details><summary>Respuesta</summary>

No: viene con Java desde la versión 11. Jackson sí hay que añadirlo.
</details>

### 16. ⭐ ¿Qué significan los códigos 200, 404 y 500?
<details><summary>Respuesta</summary>

**200**: todo bien. **404**: no existe lo que pides (fallo del cliente; por ejemplo, un Pokémon mal escrito). **500**: fallo del servidor. Regla: 2xx bien, 4xx culpa tuya, 5xx culpa del servidor.
</details>

### 17. ¿Por qué hay que comprobar `statusCode()` antes de usar `body()`?
<details><summary>Respuesta</summary>

Porque si hay un error, el cuerpo no es el JSON que esperas (puede ser un mensaje de error o una página HTML). Si se lo das a Jackson, falla con un error confuso.
</details>

### 18. ¿Qué diferencia hay entre `send` y `sendAsync`?
<details><summary>Respuesta</summary>

`send` **espera** a la respuesta (bloqueante). `sendAsync` no espera: devuelve un `CompletableFuture` (como el `Future` de PSP) y la respuesta se recoge después.
</details>

### 19. ¿Qué hace `HttpResponse.BodyHandlers.ofString()`?
<details><summary>Respuesta</summary>

Le dice al cliente que entregue el **cuerpo de la respuesta como un `String`**, que luego se pasa a Jackson.
</details>

### 20. ¿Por qué el `HttpClient` y el `ObjectMapper` se declaran `static final`?
<details><summary>Respuesta</summary>

Porque se crean **una vez** y se reutilizan en todas las peticiones: crearlos es costoso y no cambian.
</details>
