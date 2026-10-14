# Guía de librerías: qué es cada cosa y dónde vive

> **Para qué sirve este apunte:** cuando veas un método y no recuerdes qué hace, búscalo aquí (`Ctrl+F`). Es un **diccionario**, no una lección: no hace falta leerlo de arriba abajo.
>
> **Regla de oro:** nadie se sabe las librerías de memoria, tampoco los programadores con 20 años de experiencia. Lo que sí se aprende es **a buscar rápido**. El apartado 2 te enseña a hacerlo sin salir de IntelliJ.

---

## 1. La idea: el lenguaje y las librerías son dos cosas distintas

Lo que te frustra tiene explicación: en Java se mezclan **dos cosas** que el año pasado no se notaban.

| | El **lenguaje** | Las **librerías** (la API de Java) |
|---|---|---|
| Qué es | La gramática: `if`, `for`, `class`, `try`, `return`, `static`… | Código que **ya escribió alguien** y que tú usas: `Files`, `Path`, `LocalDateTime`… |
| Cuánto hay | Unas 60 palabras. Se acaban aprendiendo todas | **Miles** de clases. Nadie las conoce todas |
| Cómo se aprende | Con práctica, como las matemáticas | **Buscando** cuando hace falta, y con el uso las más comunes se quedan |

Las librerías están organizadas como un **archivador**:

```
paquete (el cajón)          →  clase (la carpeta)    →  método (la hoja)
java.nio.file                  Files                    exists(...)
```

Por eso cada fichero empieza con `import`: le dices a Java **de qué cajón** sacas cada clase. Si ves `import java.time.LocalDateTime;`, ya sabes que `LocalDateTime` es algo de **fechas y horas** (`time`), aunque no recuerdes nada más.

### Los cajones que has usado en AC

| Paquete | De qué va | Apartado |
|---|---|---|
| `java.lang` | Lo básico: `String`, `Integer`, `System`, excepciones. **No hace falta importarlo** | 3 |
| `java.util` | Colecciones (`List`, `Map`, `Set`…), `Optional` | 4 |
| `java.util.function` | Las interfaces de las lambdas (`Function`, `Predicate`…) | 5 |
| `java.util.stream` | Streams | 6 |
| `java.nio.file` | Rutas y ficheros, la forma **moderna** (`Path`, `Files`) | 7 |
| `java.io` | Ficheros, la forma **antigua** (`File`, `BufferedReader`…) | 8 |
| `java.time` | Fechas y horas | 9 |
| `java.nio` / `java.nio.channels` | `ByteBuffer`, `FileChannel` | 10 |
| `org.apache.poi` | Excel. **No es de Java**: es una librería externa | 10 |

---

## 2. Cómo buscar sin salir de IntelliJ

| Atajo | Qué hace | Cuándo usarlo |
|---|---|---|
| **`Ctrl+Q`** | Muestra la documentación oficial (Javadoc) del método o la clase donde está el cursor | "¿Qué hacía esto?" ⭐ |
| **`Ctrl+P`** | Muestra qué parámetros acepta el método, con el cursor dentro de los `( )` | "¿Qué le tengo que pasar?" |
| **`Ctrl+Espacio`** | Autocompletar: lista **todos** los métodos disponibles. Escribe `Files.` y espera | "¿Qué puedo hacer con esto?" ⭐ |
| `Ctrl+B` o `Ctrl+clic` | Abre el código fuente de la clase | Para curiosear |
| `Alt+Enter` | Arreglos rápidos: añadir el `import`, envolver en `try/catch`… | Cuando algo sale en rojo |
| `Ctrl+F12` | Lista los métodos del fichero actual | Para moverte por tu propio código |

**En internet**, la documentación oficial de Java 21 está en `docs.oracle.com/en/java/javase/21/docs/api/`. Busca el nombre de la clase.

### Cómo leer la "firma" de un método

`Ctrl+Q` te enseña cosas como esta. Parece un jeroglífico, pero siempre tiene las mismas piezas:

```
public static  Stream<String>  lines( Path path )  throws IOException
       ──┬───  ───────┬──────  ──┬──  ────┬────    ────────┬─────────
         │            │          │        │                └─ puede fallar con esta excepción: try/catch o throws
         │            │          │        └─ lo que le tienes que pasar
         │            │          └─ nombre del método
         │            └─ lo que te DEVUELVE (lo que guardarías en una variable)
         └─ static: se llama con el nombre de la CLASE (Files.lines(...)), sin hacer new
```

- **`static`** → `Clase.metodo()`: `Files.exists(p)`, `Integer.parseInt(t)`, `LocalDateTime.now()`.
- **Sin `static`** → `objeto.metodo()`: `ruta.normalize()`, `texto.split(";")`, `fecha.format(f)`.
- **`void`** → no devuelve nada; no lo guardes en una variable.
- **`OpenOption... options`** (con tres puntos) → puedes pasar **cero, una o varias** opciones separadas por comas.

---

## 3. `java.lang`: lo básico (no necesita `import`)

### `String` (texto)

| Método | Qué hace | Devuelve | Trampa |
|---|---|---|---|
| `t.split(";")` | Parte el texto por el separador | `String[]` (array) | Los trozos se cogen con `[0]`, `[1]`… y **siguen siendo texto** |
| `t.equals(otro)` | ¿Son exactamente iguales? | `boolean` | **Nunca compares textos con `==`** |
| `t.equalsIgnoreCase(otro)` | Igual, pero sin distinguir mayúsculas | `boolean` | |
| `t.contains("abc")` | ¿Contiene ese trozo? | `boolean` | Menos exacto que `equals`: `"Prediabetes"` contiene `"diabetes"` |
| `t.startsWith("abc")` | ¿Empieza por esas **letras**? | `boolean` | Compara **letra a letra**. No vale para rutas (ver `Path.startsWith`) |
| `t.trim()` / `t.strip()` | Quita los espacios del principio y del final | `String` | Devuelve un texto **nuevo**: el original no cambia |
| `t.isBlank()` / `t.isEmpty()` | ¿Está vacío? (`isBlank` también da `true` si solo hay espacios) | `boolean` | |
| `t.toUpperCase()` / `toLowerCase()` | Convierte a mayúsculas o minúsculas | `String` | |
| `String.join(";", a, b, c)` | Une textos con un separador | `String` | `static` |
| `String.format("%s tiene %d años", n, e)` | Rellena una plantilla | `String` | `%s` texto, `%d` entero, `%.2f` decimal con 2 cifras |

### Convertir texto a número

| Método | Qué hace | Trampa |
|---|---|---|
| `Integer.parseInt("45")` | `"45"` → `45` (`int`) | Si el texto no es un número, lanza `NumberFormatException` |
| `Double.parseDouble("2.5")` | `"2.5"` → `2.5` (`double`) | Usa punto, no coma |
| `String.valueOf(45)` | Al revés: número → texto | |

### `System`

| | Qué hace |
|---|---|
| `System.out.println(x)` | Escribe en la consola y salta de línea |
| `System.err.println(x)` | Igual, pero en la salida de errores (sale en rojo) |
| `System.lineSeparator()` | El salto de línea del sistema (`\r\n` en Windows) |
| `System.exit(0)` | Termina el programa (0 = todo bien; otro número = error) |

### Excepciones de `java.lang`

Todas son **unchecked** (heredan de `RuntimeException`): Java no te obliga a ponerles `try/catch` ni `throws`.

| Excepción | Cuándo se usa |
|---|---|
| `IllegalArgumentException` | Te han pasado un parámetro que no vale |
| `IllegalStateException` | El objeto no está en un estado que permita hacer eso |
| `NumberFormatException` | `parseInt` o `parseDouble` con un texto que no es un número |
| `NullPointerException` | Has usado algo que vale `null` |
| `SecurityException` | Se ha detectado una violación de seguridad |

`throw` y `throws` no son lo mismo:
- **`throw new XxxException("mensaje");`** → **lanza** la excepción ahora mismo. Va dentro del código.
- **`throws XxxException`** → **avisa** en la cabecera del método de que puede lanzarla.

---

## 4. `java.util`: colecciones y Optional

| Clase | Para qué | Métodos clave |
|---|---|---|
| `List` / `ArrayList` | Lista ordenada, con duplicados | `add`, `get(i)`, `size`, `remove`, `contains` |
| `LinkedList` (como cola) | Cola FIFO | `offer`, `poll` (devuelve `null` si está vacía), `peek` |
| `Map` / `HashMap` | Clave → valor | `put`, `get`, `containsKey`, `getOrDefault`, `merge`, `keySet`, `values` |
| `Set` / `HashSet` | Sin duplicados (y sin orden) | `add` (devuelve `false` si ya estaba), `contains` |
| `TreeSet` | Sin duplicados **y ordenado** | Igual que `Set` |
| `List.of(...)`, `Map.of(...)` | Crean colecciones **inmutables** | Si intentas `add`, lanza `UnsupportedOperationException` |
| `Optional<T>` | Una caja que puede estar vacía (en vez de `null`) | `isPresent`, `orElse`, `ifPresent`, `map` |
| `OptionalDouble` | Igual, para un `double` (lo devuelve `average()`) | `getAsDouble()`, `orElse(0.0)`, `isPresent()` |
| `Comparator` | Cómo ordenar | `comparing`, `comparingInt`, `reversed`, `thenComparing`, `Comparator.naturalOrder()` |

> Si haces `getAsDouble()` sobre un `OptionalDouble` **vacío**, lanza `NoSuchElementException`. Si puede estar vacío, usa `orElse`.

---

## 5. `java.util.function`: las interfaces de las lambdas

| Interfaz | Recibe → devuelve | Se ejecuta con | Para qué |
|---|---|---|---|
| `Function<T, R>` | `T` → `R` | `apply(t)` | **Transformar** |
| `Predicate<T>` | `T` → `boolean` | `test(t)` | **Filtrar o validar** |
| `Consumer<T>` | `T` → nada | `accept(t)` | **Hacer algo** (imprimir, guardar…) |
| `Supplier<T>` | nada → `T` | `get()` | **Fabricar o generar** |
| `BiFunction<T, U, R>` | `T, U` → `R` | `apply(t, u)` | Transformar a partir de **dos** valores |

Más detalle en `01_lambdas.md`.

---

## 6. `java.util.stream`: Streams

### Cómo se crea un stream

| Desde | Cómo |
|---|---|
| Una colección | `lista.stream()` |
| Un fichero de texto | `Files.lines(ruta)` → `Stream<String>`, una línea por elemento. **Hay que cerrarlo** (va en `try (...)`) |
| Unos valores sueltos | `Stream.of(a, b, c)` |
| Un array | `Arrays.stream(array)` |

### Operaciones intermedias (devuelven **otro stream**: se pueden encadenar)

| Operación | Qué hace |
|---|---|
| `filter(Predicate)` | Se queda con los elementos que cumplen la condición |
| `map(Function)` | Transforma cada elemento en otro (puede ser de otro tipo) |
| `mapToInt(...)` / `mapToDouble(...)` | Transforma a un stream de **números** (`IntStream` / `DoubleStream`), que tiene `sum`, `average`… |
| `distinct()` | Quita los duplicados (usa `equals`) |
| `sorted()` | Ordena en orden natural (alfabético para `String`) |
| `sorted(Comparator)` | Ordena con el criterio que le pases |
| `skip(n)` | Se salta los `n` primeros |
| `limit(n)` | Se queda solo con los `n` primeros |
| `peek(Consumer)` | Mira cada elemento sin cambiarlo (útil para depurar) |

### Operaciones finales (las que **ejecutan** todo y dan el resultado)

| Operación | Devuelve | Ojo |
|---|---|---|
| `toList()` | `List<T>` inmutable | Java 16+ |
| `collect(Collectors.toList())` | `List<T>` modificable | La forma antigua |
| `collect(Collectors.joining(", "))` | Un `String` con todo unido | Solo con streams de `String` |
| `count()` | `long` | No es `int` |
| `forEach(Consumer)` | nada | |
| `findFirst()` | `Optional<T>` | |
| `anyMatch` / `allMatch` / `noneMatch` | `boolean` | |
| `average()` | `OptionalDouble` | Solo en `IntStream` / `DoubleStream` |
| `sum()`, `max()`, `min()` | número / `Optional` | Solo en `IntStream` / `DoubleStream` |

> ⚠️ **Un stream solo se puede recorrer UNA vez.** Después de su operación final queda "gastado": si lo usas otra vez, lanza `IllegalStateException`. Una `List` sí se puede recorrer las veces que quieras con `lista.stream()`.

---

## 7. `java.nio.file`: rutas y ficheros (la forma moderna)

### `Path`: la **dirección** (no toca el disco)

| Método | Qué hace | Devuelve |
|---|---|---|
| `Path.of("a", "b")` o `Paths.get("a", "b")` | Crea la ruta `a/b`. Las dos hacen lo mismo | `Path` |
| `p.resolve("x")` | Añade `x` al final de la ruta (`a/b` → `a/b/x`) | `Path` |
| `p.toAbsolutePath()` | Completa la ruta desde la unidad (`C:\…`) | `Path` |
| `p.normalize()` | Resuelve los `.` y `..` (`a/b/../c` → `a/c`) | `Path` |
| `p.startsWith(otraRuta)` | ¿Empieza por esa ruta, **carpeta a carpeta**? | `boolean` |
| `p.getFileName()` | Solo el último trozo (el nombre del fichero) | `Path` |
| `p.getParent()` | La carpeta que lo contiene | `Path` |
| `p.toString()` | La ruta como texto | `String` |

### `Files`: las **acciones** (sí tocan el disco; casi todas lanzan `IOException`)

Todos son `static`: `Files.metodo(...)`.

| Método | Qué hace | Ojo |
|---|---|---|
| `exists(p)` / `notExists(p)` | ¿Existe? | No lanza excepción |
| `isDirectory(p)` / `isRegularFile(p)` | ¿Es carpeta? ¿Es fichero? | |
| `createDirectory(p)` | Crea **una** carpeta | Falla si no existe la carpeta padre, o si ya existe |
| `createDirectories(p)` ⭐ | Crea toda la cadena de carpetas que falte | **No falla** si ya existe |
| `createFile(p)` | Crea un fichero vacío | Falla si ya existe |
| `lines(p)` | Lee el fichero como `Stream<String>` | **Hay que cerrarlo**: `try (...)` |
| `readAllLines(p)` | Lee todas las líneas de golpe | `List<String>`. No hace falta cerrar nada |
| `readString(p)` | Lee todo el fichero en un solo texto | `String` |
| `write(p, lista, opciones...)` | Escribe una línea por cada elemento de la lista | |
| `writeString(p, texto, opciones...)` | Escribe un texto | **No añade el salto de línea** |
| `copy(origen, destino, opciones...)` | Copia | |
| `move(origen, destino, opciones...)` | Mueve o renombra | Ver las reglas de abajo ⬇ |
| `delete(p)` / `deleteIfExists(p)` | Borra | Una carpeta solo se borra si está vacía |
| `list(carpeta)` | Contenido de una carpeta (solo el primer nivel) | `Stream<Path>`: también hay que cerrarlo |
| `size(p)` | Tamaño en bytes | `long` |

**Reglas de `copy` y `move`:**
- El destino es la ruta **completa, con el nombre del fichero incluido**, no solo la carpeta.
- La carpeta de destino **tiene que existir**.
- Si el destino ya existe y no pasas ninguna opción, lanza `FileAlreadyExistsException`.
- En Windows, **no se puede mover un fichero que sigue abierto**.

### Opciones (se pasan al final, separadas por comas)

`StandardOpenOption`, para **escribir** (`write`, `writeString`, `newBufferedWriter`):

| Opción | Efecto |
|---|---|
| *(ninguna)* | Lo crea, o **lo vacía** si ya existía |
| `CREATE` | Créalo si no existe |
| `CREATE_NEW` | Créalo; si ya existe, **falla** |
| `APPEND` | Escribe **al final**, sin borrar lo que había |
| `TRUNCATE_EXISTING` | Vacíalo antes de escribir |

`StandardCopyOption`, para **copiar o mover** (`copy`, `move`):

| Opción | Efecto |
|---|---|
| `REPLACE_EXISTING` | Si el destino ya existe, **lo sobrescribe** |
| `ATOMIC_MOVE` | El movimiento se hace en un solo paso (o todo o nada) |
| `COPY_ATTRIBUTES` | Copia también las fechas y los permisos |

### Excepciones de ficheros (todas heredan de `IOException`, que es **checked**: Java te obliga a tratarlas)

`NoSuchFileException` · `FileAlreadyExistsException` · `DirectoryNotEmptyException` · `AccessDeniedException`

---

## 8. `java.io`: ficheros (la forma antigua)

| Clase | Para qué |
|---|---|
| `File` | Ruta + acciones en el mismo objeto (`exists()`, `mkdir()`, `renameTo()`…). Si algo falla devuelve `false`, sin decir por qué |
| `BufferedReader` | Leer texto línea a línea: `readLine()` devuelve `null` al final |
| `BufferedWriter` | Escribir texto: `write(...)` + `newLine()` |
| `DataOutputStream` / `DataInputStream` | Binario con tipos primitivos: `writeInt` / `readInt`… **Se lee en el mismo orden** en que se escribió |
| `ObjectOutputStream` / `ObjectInputStream` | Serializar objetos enteros: `writeObject` / `readObject` |
| `RandomAccessFile` | Acceso aleatorio: `seek(posición)` |
| `IOException` | La madre de todos los errores de entrada/salida |
| `EOFException` | Has llegado al final de un fichero binario |

---

## 9. `java.time`: fechas y horas

| Clase | Qué representa | Ejemplo |
|---|---|---|
| `LocalDate` | Solo la fecha | 2026-10-13 |
| `LocalTime` | Solo la hora | 17:48:05.123 |
| `LocalDateTime` | Fecha + hora | 2026-10-13T17:48:05.123 |
| `DateTimeFormatter` | El **molde** para convertir una fecha en texto (o al revés) | |

| Método | Qué hace |
|---|---|
| `LocalDateTime.now()` | La fecha y hora de **este instante** (`static`) |
| `DateTimeFormatter.ofPattern("…")` | Crea un molde a partir de un patrón (`static`) |
| `fecha.format(molde)` o `molde.format(fecha)` | Fecha → texto. Las dos formas hacen lo mismo |
| `LocalDate.parse(texto, molde)` | Texto → fecha |

> Cada vez que llamas a `now()` obtienes un instante **distinto**. Si necesitas el **mismo** instante en dos sitios, llama a `now()` una vez y guárdalo en una variable.

### Las letras del patrón (⚠️ mayúsculas y minúsculas significan cosas distintas)

| Letra | Significa | Ejemplo |
|---|---|---|
| `yyyy` | año | 2026 |
| `MM` | **mes** (M mayúscula) | 10 |
| `dd` | día del mes | 13 |
| `HH` | hora **0–23** (H mayúscula) | 17 |
| `hh` | hora 1–12 (h minúscula; necesita `a` para AM/PM) | 05 |
| `mm` | **minutos** (m minúscula) | 48 |
| `ss` | segundos | 05 |
| `SSS` | **milisegundos** (S mayúscula) | 123 |
| `E` / `EEEE` | día de la semana | mar. / martes |
| `'texto'` | texto literal, entre comillas simples | `'T'` |

Cualquier otro símbolo (`-`, `:`, espacio, `.`, `/`) aparece tal cual en el resultado.

> Los dos errores más típicos: confundir `MM` (mes) con `mm` (minutos), y `HH` (0–23) con `hh` (1–12). **Compila sin errores**, pero sale una fecha equivocada.

---

## 10. Otras que has visto

| Clase | Paquete | Para qué |
|---|---|---|
| `ByteBuffer` | `java.nio` | Una caja de bytes: `allocate(n)`, `putInt`, `put`, **`flip()`** antes de escribirla |
| `FileChannel` | `java.nio.channels` | La conexión con el fichero: `open(p, opciones)`, `write(buffer)`, `position(n)` |
| `XSSFWorkbook` → `Sheet` → `Row` → `Cell` | `org.apache.poi` (externa) | Excel: libro → hoja → fila → celda |

---

## 11. Palabras del **lenguaje** que más lían

| Palabra | Qué significa |
|---|---|
| `static` | Pertenece a la **clase**, no a un objeto. Se usa sin `new` |
| `final` | No se puede cambiar (una variable, un método o una clase) |
| `void` | El método no devuelve nada |
| `new` | Crea un objeto |
| `var` | "Java, adivina tú el tipo" (solo en variables locales) |
| `record` | Una clase de datos en una línea, con constructor y getters automáticos |
| `enum` | Una lista cerrada de valores (`ALTA`, `MEDIA`, `BAJA`) |
| `try (recurso) { }` | try-with-resources: cierra el recurso solo al salir del bloque |
| `try { } catch (X e) { }` | Si dentro salta `X`, ejecuta el `catch` |
| `finally { }` | Se ejecuta **siempre**, haya error o no |
| `throw` / `throws` | Lanzar ahora / avisar de que se puede lanzar (ver apartado 3) |
| `->` | Una lambda |
| `::` | Una referencia a un método (atajo de una lambda) |
