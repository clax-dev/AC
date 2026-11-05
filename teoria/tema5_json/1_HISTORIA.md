# Tema 5: JSON, Jackson y APIs web, contado como una historia

> **Cómo usar este archivo:**
> - Léelo **de un tirón**, como un cuento. Cada bloque tiene una **💬 frase clave**.
> - Ten abierto `visual.pdf` al lado. La sintaxis está en `2_TECNICO.md`, y para comprobarte tienes `3_PREGUNTAS.md`.
> - **Ejercicios del tema:** `ejercicios/src/tema5_json/` → `LectorFileJSONJackson` (leer un `.json` del disco) y `HttpRequestPokeAPI` (pedir un JSON a internet).
> - Los métodos sueltos están en `../GUIA_LIBRERIAS.md`, apartados 12 (Jackson) y 13 (`java.net.http`).

**La historia entera en tres líneas:**
- En el tema 4 guardabas objetos en CSV (texto, pero plano) o serializados (cómodo, pero **solo Java** lo entiende).
- **JSON** es el punto medio: es **texto**, lo lee cualquier lenguaje y **sí tiene estructura** (objetos dentro de objetos, listas…). Es el idioma en el que hablan casi todas las aplicaciones de internet.
- **Jackson** traduce JSON ↔ objetos Java, y **`HttpClient`** va a buscar ese JSON a internet.

---

## 1. JSON: un CSV con estructura
📄 *PDF, página 2*

> 💬 **"JSON es texto con forma de objeto: llaves `{}` para un objeto, corchetes `[]` para una lista y `"clave": valor` para cada atributo."**

```json
{
  "nombre": "Ana",
  "edad": 30,
  "activa": true,
  "aficiones": ["leer", "nadar"],
  "direccion": { "ciudad": "Madrid", "cp": "28001" }
}
```
| Pieza | En JSON | En Java |
|---|---|---|
| Objeto | `{ ... }` | un objeto (`PersonaJSON`) |
| Atributo | `"clave": valor` | un atributo (`nombre`) |
| Lista | `[ ... ]` | una `List` o un array |
| Texto / número / booleano / nulo | `"Ana"` / `30` / `true` / `null` | `String` / `int` / `boolean` / `null` |

**Frente a lo del tema 4:**
| | CSV | Serialización | **JSON** |
|---|---|---|---|
| ¿Legible? | sí | no (binario) | **sí** |
| ¿Otros lenguajes? | sí | **solo Java** | **sí** |
| ¿Objetos dentro de objetos y listas? | no (plano) | sí | **sí** |

---

## 2. Jackson: el traductor
📄 *PDF, página 3* · 📝 *`LectorFileJSONJackson`*

> 💬 **"`ObjectMapper` es el traductor: `readValue` pasa de JSON a objeto y `writeValue` de objeto a JSON."**

- Java no sabe leer JSON por sí solo: se usa una **librería externa**, Jackson (`jackson-databind`). Por eso el ejercicio va en un proyecto con `pom.xml`, como POI.
- La pieza central es **`ObjectMapper`**:
  - **Deserializar** (JSON → objeto): `mapper.readValue(fichero, PersonaJSON.class)`.
  - **Serializar** (objeto → JSON): `mapper.writeValue(fichero, persona)`.
- Es lo mismo que `ObjectOutputStream` del tema 4, pero en **texto** que entiende todo el mundo.
- Jackson es la más usada en las empresas (Spring Boot la trae de serie). La alternativa es **Gson**, de Google.

---

## 3. Las reglas del molde (la clase Java)
📄 *PDF, página 3*

> 💬 **"Jackson empareja cada clave del JSON con el atributo del mismo nombre. Necesita un constructor vacío y getters/setters."**

Mira `PersonaJSON`: tiene justo lo que Jackson necesita.
- **Constructor vacío** (`public PersonaJSON(){}`): Jackson crea el objeto vacío y luego lo rellena.
- **Setters** (`setNombre`…) para **leer** el JSON, y **getters** (`getNombre`…) para **escribirlo**.
- Los **nombres tienen que coincidir**: la clave `"nombre"` va a `setNombre`. Si en el JSON es `"base_experience"` y en Java `baseExperience`, se avisa con `@JsonProperty("base_experience")`.
- **¿Y si el JSON trae claves que tu clase no tiene?** Por defecto Jackson **falla** (`UnrecognizedPropertyException`). Por eso la profesora pone `FAIL_ON_UNKNOWN_PROPERTIES = false`: **"ignora lo que no conozcas"**. Es imprescindible con APIs como PokeAPI, que devuelven cientos de campos y tú solo quieres tres.

---

## 4. Dos formas de leer: el molde o el mapa
📄 *PDF, página 4*

> 💬 **"Si conoces la forma, usa un molde (`readValue` a una clase). Si solo quieres un par de datos de un JSON enorme, recórrelo como un árbol (`readTree`)."**

- **Con molde (POJO):** creas una clase con los campos que te interesan → `readValue` → tienes un objeto con getters. Ordenado y con tipos.
- **Como árbol (`JsonNode`):** `readTree` te da el JSON como un árbol y vas bajando: `raiz.get("direccion").get("ciudad").asText()`. Las listas se recorren con un `for`. Útil para explorar o sacar un dato suelto sin crear clases.

---

## 5. APIs web: pedir el JSON a internet
📄 *PDF, página 5* · 📝 *`HttpRequestPokeAPI`*

> 💬 **"Una API web es un servidor al que le haces una pregunta con una URL y te contesta con JSON. `HttpClient` es el cartero."**

- **API REST:** un servicio en internet con **direcciones (endpoints)**. Cada URL es una pregunta: `https://pokeapi.co/api/v2/pokemon/charmander` = "dame los datos de Charmander".
- **PokeAPI** es una API pública y gratuita: no se instala, solo se le pregunta.
- **El cartero son tres clases de `java.net.http`** (vienen con Java 11+, no hay que instalar nada):
  1. **`HttpClient`** → el cartero (se crea **una vez** y se reutiliza).
  2. **`HttpRequest`** → la carta: URL, cabeceras y método (`GET` = "dame datos").
  3. **`HttpResponse`** → la respuesta: el **código de estado** y el **cuerpo** (el JSON en texto).
- **Siempre se mira el código de estado antes de usar el cuerpo:**
  | Código | Significa |
  |---|---|
  | **2xx** (200) | ✅ todo bien |
  | **4xx** (404) | ❌ **tu** culpa: no existe (un Pokémon mal escrito) |
  | **5xx** (500) | ❌ culpa **del servidor** |
- Una respuesta puede tardar: `send` **espera** (bloqueante). `sendAsync` no espera y te da un "ticket" (`CompletableFuture`), la misma idea que el `Future` de PSP.

**El viaje completo del ejercicio:**
```
URL → HttpRequest → cliente.send() → HttpResponse (código + JSON en texto) → Jackson → objetos Java
```

---

## 6. Resumen

| Concepto | Frase clave |
|---|---|
| JSON | Texto con forma de objeto: `{}` objeto, `[]` lista |
| Frente a CSV y serialización | Legible, multilenguaje **y** con estructura |
| `ObjectMapper` | El traductor: `readValue` / `writeValue` |
| La clase molde | Constructor vacío + getters/setters, mismos nombres |
| Claves desconocidas | Fallan, salvo `FAIL_ON_UNKNOWN_PROPERTIES = false` |
| `readTree` / `JsonNode` | Recorrer el JSON como un árbol, sin crear clases |
| API REST | Una URL = una pregunta; contesta con JSON |
| `HttpClient` / `HttpRequest` / `HttpResponse` | El cartero / la carta / la respuesta |
| Código de estado | 2xx bien · 4xx culpa tuya · 5xx culpa del servidor |

**¿Y ahora?** Repasa `visual.pdf`, haz `3_PREGUNTAS.md` y abre `2_TECNICO.md` cuando programes.
