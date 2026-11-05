# Chuleta de AC: las frases clave de todos los temas

> Para repasar en 5 minutos antes de un examen. Si una frase no te dice nada, vuelve al `1_HISTORIA.md` de su tema.
> ¿No recuerdas qué hace un método o de qué paquete sale? → `GUIA_LIBRERIAS.md`.

## Tema 1: Lambdas → `tema1_lambdas/`
| Concepto | Frase clave | Cómo se escribe |
|---|---|---|
| Lambda | Nota con instrucciones; no se ejecuta hasta que alguien la llama | `(params) -> cuerpo` |
| Interfaz funcional | Un solo método abstracto | — |
| `Predicate` / `Function` / `Consumer` / `Supplier` | Portero / máquina / operario / fábrica | `test` / `apply` / `accept` / `get` |
| Combinar | Las notas se encadenan | `and`, `or`, `negate`, `andThen` |
| Pasar una lambda | Solo el nombre, sin `()` | `filtrar(lista, abiertas)` |
| `Comparator` | Negativo = `a` va antes | `comparingInt(...).reversed().thenComparing(...)` |
| `::` | Atajo si solo llama a un método | `Incidencia::id`, `System.out::println` |
| `record` | Datos inmutables, getters sin `get` | `inc.id()` |

## Tema 2: Colecciones → `tema2_colecciones/`
| Concepto | Frase clave | Cómo se escribe |
|---|---|---|
| Elegir | ID → `Map` · orden → `List` · turnos → `Queue` · únicos → `Set` | `new HashMap<>()`… |
| Declarar | Con la interfaz, crear con la clase | `List<Integer> l = new ArrayList<>();` |
| ¿Existe la clave? | Los IDs no son índices | `containsKey(id)` |
| Contar | Sumar 1 a lo que hubiera | `merge(id, 1, Integer::sum)` |
| Cola vacía | `poll` avisa con `null` | `poll()` / `peek()` |
| `List.of` | Inmutable | `new ArrayList<>(List.of(...))` |

## Tema 3: Streams y Optional → `tema3_streams/`
| Concepto | Frase clave | Cómo se escribe |
|---|---|---|
| Stream | Cadena de montaje: origen → intermedias → final | `stream().filter().map().toList()` |
| Sin final | No se ejecuta nada | — |
| Un solo uso | Para otro cálculo, otro `stream()` | — |
| Media | Primero a números | `mapToDouble(...).average()` → `OptionalDouble` |
| `count` | Devuelve `long` | — |
| `Optional` | Caja que puede estar vacía; nunca `null` | `findFirst()`, `orElse`, `ifPresent` |

## Tema 4: Ficheros → `tema4_ficheros/`
| Concepto | Frase clave | Cómo se escribe |
|---|---|---|
| Ruta | Escribir la dirección no construye la casa | `Paths.get("a", "b")`, `resolve` (¡guárdalo!) |
| Relativa | Se completa con el directorio de trabajo | `toAbsolutePath()` para ver dónde apunta |
| `File` / `Files` | `false` sin explicación / excepción concreta | `f.mkdir()` / `Files.createDirectory(p)` |
| Crear carpetas | La cadena entera, sin fallar si ya existe | `Files.createDirectories(p)` |
| Mover | Mismo método que renombrar; destino con nombre | `Files.move(DESDE, HASTA, REPLACE_EXISTING)` |
| Borrar carpeta | Solo vacía | `DirectoryNotEmptyException` |
| Abrir y cerrar | Lo que se abre va en `try ( )` y se cierra solo | `try (Stream<String> s = Files.lines(p))` |
| Opciones | Nada = machaca · `CREATE_NEW` = se niega · `CREATE`+`APPEND` = añade | `StandardOpenOption.*` |
| `writeString` | No pone salto de línea | `+ System.lineSeparator()` |
| Excepciones | Hijas de `IOException` (checked); atrapa la concreta | `catch (FileAlreadyExistsException e)` |
| Path Traversal | Normaliza las dos y compara carpeta a carpeta | `toAbsolutePath().normalize()` + `Path.startsWith` |
| CSV | Para personas y Excel | `split(";")` + `parseInt`; `Locale.ROOT` |
| `Data*Stream` | Mismo orden y tipos; el final es una excepción | `readInt()`… `catch (EOFException e)` |
| Serialización | Objeto entero; solo Java | `Serializable`, `serialVersionUID`, `transient`, cast |
| Acceso aleatorio | Tamaño fijo: posición = n × tamaño | `RandomAccessFile(f, "rw")` + `seek` |
| `ByteBuffer` | Llenar → `flip()` → vaciar | `allocate`, `put`, `flip`, `while (hasRemaining()) canal.write(b)` |
| POI | Libro → hoja → fila → celda, desde 0 | `XSSFWorkbook` … fórmula sin `=` |
| Fechas en nombres | Solo dígitos; `MM` mes, `mm` minutos | `ofPattern("yyyyMMddHHmmss")` |

## Tema 5: JSON, Jackson y APIs web → `tema5_json/`
| Concepto | Frase clave | Cómo se escribe |
|---|---|---|
| JSON | Texto con forma de objeto: `{}` objeto, `[]` lista | `"clave": valor` |
| Jackson | El traductor JSON ↔ objeto (librería externa, en el `pom.xml`) | `mapper.readValue(json, Clase.class)` / `writeValue` |
| Clase molde | Constructor vacío + getters/setters, mismos nombres | `@JsonProperty("clave")` si cambia |
| Claves desconocidas | Fallan, salvo que las ignores | `FAIL_ON_UNKNOWN_PROPERTIES, false` |
| Árbol | Datos sueltos sin crear clases | `readTree(json).get("x").asText()` |
| API REST | Una URL = una pregunta; contesta JSON. PokeAPI no es una librería | `https://pokeapi.co/api/v2/pokemon/...` |
| HTTP | Cartero, carta, respuesta (`java.net.http`, viene con Java 11+) | `HttpClient` · `HttpRequest` · `HttpResponse` |
| Código de estado | 2xx bien · 4xx culpa tuya · 5xx del servidor | `if (r.statusCode() != 200)` antes de `body()` |

## Tema 7: APIs REST con Spring Boot → `tema7_api_rest/`
| Concepto | Frase clave | Cómo se escribe |
|---|---|---|
| Capas | Un trabajo por capa; el camarero nunca entra en el almacén | Controller → Service → Repository |
| DAO / Repository | "Pasillo 3, balda 2" / "dame los tomates" | `insert()` / `save()` |
| Spring Boot | Food truck: el servidor va dentro | `.jar` que arranca solo |
| Entidad / DTO / Mapper | Ingrediente / plato / quien emplata | El Controller solo ve DTOs |
| Verbos HTTP | El verbo dice qué, la URL dice sobre qué | `@GetMapping` · `@PostMapping` · `@PutMapping` · `@DeleteMapping` |
| Repository | Interfaz vacía: Spring pone el trabajador | `extends JpaRepository<Cliente, Long>` |
| Error LAZY | Jackson llega tarde: la sesión ya se ha cerrado | Devolver un DTO |

## Las 4 reglas que sirven para todo
1. **Si un método no es `void`, te entrega algo: recógelo** (`Path f = ruta.resolve(x);`).
2. **Lo que se abre, se cierra**: `try ( )`.
3. **Entre comprobar y actuar hay un hueco**: hazlo en un paso (`CREATE_NEW`, `poll`).
4. **Copia el texto exacto de los datos**: `equals` distingue mayúsculas y tildes.
