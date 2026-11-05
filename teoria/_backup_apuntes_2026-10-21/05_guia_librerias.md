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

### 7.1 Ficheros binarios con NIO: la idea

Con NIO no se lee "directamente del fichero" como con `DataInputStream`. Hay **dos piezas**:

| Pieza | Paquete | Qué es | Analogía |
|---|---|---|---|
| `FileChannel` | `java.nio.channels` | La **conexión** con el fichero. Por ella entran y salen bytes, y puedes elegir **en qué posición** | Una tubería |
| `ByteBuffer` | `java.nio` | Una **caja de bytes en memoria**. De ella sacas o en ella metes `int`, `long`, `short`… | El cubo que llenas o vacías |

- **Para leer:** el canal **vuelca** bytes del fichero en la caja → `flip()` → sacas los datos de la caja con `getInt()`, `getLong()`…
- **Para escribir:** metes los datos en la caja con `putInt()`, `putLong()`… → `flip()` → el canal **vuelca** la caja en el fichero.

> **Atajo para ficheros pequeños**, como uno de pocos bytes: `Files.readAllBytes(ruta)` te da todo el fichero en un `byte[]`, y `ByteBuffer.wrap(ese array)` lo convierte en una caja ya llena y lista para leer, **sin canal y sin `flip()`**. Para escribir, al revés: llenas una caja y la pasas a `byte[]`, o la escribes con un canal.

Los tamaños son los mismos que en la tabla de 8.1 (`int` = 4 bytes, `long` = 8, `short` = 2…), y también se guardan en orden *big-endian*.

### 7.2 `ByteBuffer`: la caja de bytes

**Cómo se crea:**

| Método (`static`) | Qué hace |
|---|---|
| `ByteBuffer.allocate(n)` | Crea una caja **vacía** de `n` bytes, lista para **llenar** |
| `ByteBuffer.wrap(byte[] array)` | Envuelve un array que ya tiene datos: caja **llena**, lista para **leer** (desde la posición 0) |

**Las tres "marcas" de la caja** (es lo que más lía):

| Marca | Qué es | Método para verla |
|---|---|---|
| `position` | El cursor: dónde se va a leer o escribir **el siguiente** dato. Avanza solo con cada `get`/`put` | `position()` |
| `limit` | Hasta dónde se puede leer o escribir | `limit()` |
| `capacity` | Tamaño total de la caja (no cambia nunca) | `capacity()` |

| Método | Qué hace | Cuándo |
|---|---|---|
| `flip()` | `limit = position` y `position = 0`: pasa de **"modo llenar"** a **"modo leer"** | Después de llenar la caja (con `put` o con `canal.read`) y **antes** de sacar datos o escribirla en el canal |
| `rewind()` | `position = 0` (no toca `limit`) | Para volver a leer desde el principio |
| `clear()` | Deja la caja lista para **llenarla** de nuevo (no borra los bytes) | Para reutilizarla |
| `remaining()` / `hasRemaining()` | Cuántos bytes quedan entre `position` y `limit` / ¿queda alguno? | Bucles de escritura y comprobaciones |
| `position(n)` | Mueve el cursor al byte `n` | Saltar a un campo concreto |
| `array()` | El `byte[]` que hay por debajo | Para pasar la caja a `Files.write` |

**Sacar datos** (leen en `position` y la avanzan):

| Método | Lee | Devuelve |
|---|---|---|
| `getInt()` | 4 bytes | `int` |
| `getLong()` | 8 bytes | `long` |
| `getShort()` | 2 bytes | `short` (**con signo**: ver la trampa de 8.1) |
| `get()` | 1 byte | `byte` (con signo) |
| `get(byte[] destino)` | `destino.length` bytes, y los copia en el array | el propio buffer |
| `getDouble()` / `getFloat()` / `getChar()` | 8 / 4 / 2 bytes | |

**Meter datos** (escriben en `position` y la avanzan):

`putInt(n)` · `putLong(n)` · `putShort((short) n)` · `put((byte) n)` · `put(byte[] array)` · `putDouble(d)`…

> ⚠️ `putShort` y `put` piden un `short` y un `byte`: si les pasas un literal como `0xCAFE`, que es un `int`, hay que hacer el *cast* `(short) 0xCAFE`.

**Versiones con posición fija (absolutas).** Llevan un índice delante y **no mueven el cursor**:

| Método | Qué hace |
|---|---|
| `getInt(indice)` · `getLong(indice)` · `getShort(indice)`… | Lee el dato que empieza en el byte `indice` |
| `putInt(indice, n)` · `putLong(indice, n)`… | **Sobrescribe** el dato que empieza en el byte `indice` |

> 💡 Útiles para **modificar un campo** de tamaño fijo dentro de una caja que ya tiene todo el fichero: sobrescribes justo esos bytes sin tocar el resto.

**Sin signo:**

| Método (`static`) | Qué hace |
|---|---|
| `Short.toUnsignedInt(s)` | Un `short` leído como 0–65535 (por ejemplo, para comparar con `0xCAFE`) |
| `Byte.toUnsignedInt(b)` | Un `byte` leído como 0–255 |
| `Integer.toUnsignedLong(i)` | Un `int` leído sin signo |

> ⚠️ **`ByteBuffer` no tiene `readUTF`/`getUTF`.** Un texto se lee "a mano": primero su longitud (`getShort()` o `getInt()`, según cuántos bytes ocupe la longitud en tu fichero), después un `byte[]` de ese tamaño con `get(array)`, y luego `new String(array, StandardCharsets.UTF_8)`. Ver 8.4.
> ⚠️ Si haces `get`/`put` más allá del `limit`: `BufferUnderflowException` (leer) o `BufferOverflowException` (escribir). Casi siempre es porque **se te olvidó el `flip()`** o porque reservaste una caja demasiado pequeña.

### 7.3 `FileChannel`: la conexión con el fichero

**Cómo se abre:** `FileChannel.open(ruta, opciones...)`, en `try-with-resources`. Las opciones son las `StandardOpenOption` de arriba, más estas dos:

| Opción | Significa |
|---|---|
| `READ` | Abrirlo para leer |
| `WRITE` | Abrirlo para escribir |

Se combinan: `READ, WRITE` (leer y modificar), `CREATE, WRITE, TRUNCATE_EXISTING` (escribirlo de cero)…

| Método | Qué hace | Devuelve |
|---|---|---|
| `read(buffer)` | Llena la caja con bytes del fichero, desde la posición actual del canal | Cuántos bytes ha leído, o **`-1`** si ya estaba al final |
| `write(buffer)` | Escribe en el fichero lo que hay entre `position` y `limit` de la caja | Cuántos bytes ha escrito |
| `read(buffer, pos)` / `write(buffer, pos)` | Igual, pero en la posición `pos` del fichero, sin mover la posición del canal | `int` |
| `position()` / `position(n)` | Ver o mover la posición del canal en el fichero | `long` / el canal |
| `size()` | Tamaño del fichero | `long` |
| `truncate(n)` | Corta el fichero a `n` bytes | el canal |

> ⚠️ `write` puede no escribirlo todo de una vez: lo correcto es repetirlo **mientras** la caja tenga bytes pendientes (`hasRemaining()`).
> ⚠️ Escribir en una posición **sobrescribe** los bytes que había, no "inserta". Si un campo cambia de tamaño (por ejemplo, un texto más largo o más corto), todo lo que va detrás se descoloca: hay que **reescribir el fichero** desde ahí, o entero. Ver 8.4.
> ⚠️ Si el texto nuevo es **más corto** y reescribes sobre el mismo fichero sin `TRUNCATE_EXISTING` o `truncate`, quedan **bytes viejos al final**.

**Pasar de texto a bytes y al revés** (lo necesitas para los textos): `texto.getBytes(StandardCharsets.UTF_8)` y `new String(bytes, StandardCharsets.UTF_8)`. Ver 8.4.

**Escribir un `byte[]` entero sin canal:** `Files.write(ruta, bytes, opciones...)`.

### 7.4 Información de un fichero, y pasar de `File` a `Path` (y al revés)

`File` (la clase antigua) y `Path` (la moderna) representan lo mismo: **la dirección de un fichero**. Se puede pasar de una a otra sin problema:

| Tengo… | Quiero… | Método |
|---|---|---|
| un `Path` | un `File` | `ruta.toFile()` |
| un `File` | un `Path` | `fichero.toPath()` |

**Información con `Files` (si tienes un `Path`):**

| Método | Qué devuelve | Ojo |
|---|---|---|
| `Files.exists(p)` | ¿Existe? → `boolean` | |
| `Files.size(p)` | Tamaño en **bytes** → `long` | Lanza `IOException` |
| `Files.getLastModifiedTime(p)` | Fecha de la última modificación → `FileTime` | Al imprimirlo sale en formato `2026-10-16T08:30:00Z` |
| `Files.isReadable(p)` / `isWritable(p)` | ¿Se puede leer? ¿Escribir? → `boolean` | |
| `Files.isRegularFile(p)` / `isDirectory(p)` | ¿Es un fichero normal? ¿Una carpeta? | |
| `Files.lines(p).count()` | Número de líneas → `long` | `lines` deja el fichero abierto: va en `try (...)` |
| `Files.readAllLines(p).size()` | Número de líneas → `int` | Más fácil: no hace falta cerrar nada |
| `p.getFileName()` | Solo el nombre: `pacientes.csv` | |
| `p.toAbsolutePath()` | La ruta completa: `C:\…\pacientes.csv` | |

**Información con `File` (si tienes un `File`):**

| Método | Qué devuelve | Ojo |
|---|---|---|
| `f.exists()` | ¿Existe? → `boolean` | |
| `f.length()` | Tamaño en **bytes** → `long` | Si no existe, devuelve **0** (no da error) |
| `f.lastModified()` | Última modificación → `long` en **milisegundos** desde 1970 | Para leerla: `new Date(f.lastModified())` o `Instant.ofEpochMilli(...)` |
| `f.canRead()` / `canWrite()` | ¿Se puede leer? ¿Escribir? | |
| `f.isFile()` / `isDirectory()` | ¿Es un fichero? ¿Una carpeta? | |
| `f.getName()` | Solo el nombre | |
| `f.getAbsolutePath()` | La ruta completa → `String` | |
| `f.getParent()` | La carpeta que lo contiene → `String` | |

> 💡 **¿Cuál uso?** Si el método te da un `Path`, usa `Files`; si te da un `File`, usa sus propios métodos. Y si necesitas los dos, conviértelo con `toFile()` / `toPath()`.

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

> **Si te piden NIO (la forma nueva), usa los apartados 7.1–7.3** (`ByteBuffer` + `FileChannel`). Los apartados 8.1 (tamaños, hexadecimal, signo), 8.4 (textos en binario) y 8.6 (comprobar) **sirven para las dos formas**. Los 8.2, 8.3 y 8.5 son la forma antigua, por si la ves en apuntes viejos.

### 8.1 Antes de nada: cómo es un fichero binario por dentro

Un fichero binario es una **fila de bytes, uno detrás de otro, sin separadores ni etiquetas**. No pone "aquí empieza un int": solo hay bytes. Para leerlo bien tienes que saber **qué tipo** va en cada sitio y **cuántos bytes ocupa**.

**Lo que ocupa cada tipo** (con `DataOutputStream`, `DataInputStream` y `RandomAccessFile`):

| Tipo | Bytes | Se escribe con | Se lee con |
|---|---|---|---|
| `byte` | 1 | `writeByte` | `readByte` |
| `boolean` | 1 | `writeBoolean` | `readBoolean` |
| `short` | 2 | `writeShort` | `readShort` |
| `char` | 2 | `writeChar` | `readChar` |
| `int` | 4 | `writeInt` | `readInt` |
| `float` | 4 | `writeFloat` | `readFloat` |
| `long` | 8 | `writeLong` | `readLong` |
| `double` | 8 | `writeDouble` | `readDouble` |
| `String` con `writeUTF` | **2** (la longitud) **+ los bytes del texto** | `writeUTF` | `readUTF` |

> Los números se guardan con el byte **más importante primero** (*big-endian*). El `int` 52 son los 4 bytes `00 00 00 34`.

**Los números en hexadecimal.** En los visores de binarios cada byte se muestra con dos cifras en **base 16** (de `00` a `FF`). Las letras A–F valen de 10 a 15.

| Para… | Escribe | Ejemplo |
|---|---|---|
| Escribir un número en hexadecimal en Java | prefijo `0x` | `0x34` vale 52; `0xCAFE` vale 51966 |
| Un `long` en hexadecimal | sufijo `L` | `0x12345678L` |
| Mostrar un número en hexadecimal | `Integer.toHexString(n)` · `Long.toHexString(n)` | `Integer.toHexString(255)` → `"ff"` |
| Mostrar con formato | `String.format("%X", n)` · `"%02X"` (2 cifras) | `String.format("%04X", 0xCAFE)` → `"CAFE"` |
| Comparar con un valor esperado | `==` con el literal | `valor == 0xCAFE` |

> ⚠️ **Trampa del signo.** `readShort()` y `readByte()` devuelven números **con signo**. Si el bit más alto está a 1 (por ejemplo, `0xCAFE` o cualquier byte `≥ 0x80`), sale **negativo**. Entonces `readShort() == 0xCAFE` da `false`, porque lee -13570 y compara con 51966. Las soluciones son `readUnsignedShort()` / `readUnsignedByte()`, que devuelven siempre positivo, o comparar con `(short) 0xCAFE`.

**Cómo "mirar dentro" de un binario:**
- Instala el plugin **BinEd** en IntelliJ (Settings → Plugins) y abre el fichero con él.
- O desde Java: `Files.readAllBytes(ruta)` devuelve un `byte[]`; recórrelo e imprime cada byte con `String.format("%02X ", b)`.
- `Files.size(ruta)` te dice cuántos bytes tiene en total. Sumar lo que ocupa cada campo y comparar con ese total es la mejor forma de comprobar que has entendido la estructura.

### 8.2 `DataInputStream`: leer tipos primitivos de un binario

**Paquete:** `java.io`. **Cómo se crea:** envuelve un flujo de bytes; normalmente un `FileInputStream` o `Files.newInputStream(ruta)`, y opcionalmente un `BufferedInputStream` en medio para que vaya más rápido. Va en `try-with-resources`.

```java
new DataInputStream(new FileInputStream("ruta"))
new DataInputStream(new BufferedInputStream(Files.newInputStream(ruta)))
```

| Método | Lee… | Devuelve | Trampa |
|---|---|---|---|
| `readInt()` | 4 bytes | `int` | |
| `readLong()` | 8 bytes | `long` | |
| `readShort()` | 2 bytes | `short` (**con signo**) | Ver la trampa del signo ⬆ |
| `readUnsignedShort()` | 2 bytes | `int` de 0 a 65535 | |
| `readByte()` / `readUnsignedByte()` | 1 byte | `byte` / `int` 0–255 | |
| `readBoolean()` | 1 byte | `boolean` | |
| `readDouble()` / `readFloat()` | 8 / 4 bytes | `double` / `float` | |
| `readUTF()` | 2 bytes de longitud + ese número de bytes | `String` | **Solo** sirve si el texto se escribió con `writeUTF`. Si se escribió de otra forma, lee basura o lanza una excepción |
| `readFully(byte[] b)` | exactamente `b.length` bytes, y los mete en `b` | nada (rellena el array) | Si no quedan bastantes bytes, `EOFException` |
| `readNBytes(int n)` | hasta `n` bytes | `byte[]` | |
| `skipBytes(int n)` | se salta `n` bytes | cuántos ha saltado | |
| `available()` | — | bytes que quedan por leer (aprox.) | Útil para depurar, no para controlar el bucle |
| `close()` | — | — | Lo hace solo el `try (...)` |

> ⚠️ **No sabe lo que hay en el fichero.** Si llamas a `readInt()` donde se escribió un `long`, no da error: lee 4 bytes y te da un número sin sentido. **Y todo lo que leas después también saldrá mal**, porque te has descolocado. Si ves números absurdos, el fallo suele estar en el campo **anterior**.
> ⚠️ Al final del fichero no devuelve `null` ni `-1`: lanza **`EOFException`**.

### 8.3 `DataOutputStream`: escribir tipos primitivos en un binario

**Cómo se crea:** igual, envolviendo un `FileOutputStream` o `Files.newOutputStream(ruta)`. Al abrirlo **vacía el fichero**, salvo que uses el modo añadir.

| Método | Escribe | Ocupa |
|---|---|---|
| `writeInt(n)` | un `int` | 4 bytes |
| `writeLong(n)` | un `long` | 8 bytes |
| `writeShort(n)` | los 2 bytes bajos de `n` | 2 bytes |
| `writeByte(n)` / `writeBoolean(b)` | un byte | 1 byte |
| `writeDouble(d)` / `writeFloat(f)` | un decimal | 8 / 4 bytes |
| `writeUTF(texto)` | longitud (2 bytes) + texto en UTF-8 | 2 + n bytes |
| `write(byte[] b)` | los bytes del array tal cual, **sin longitud delante** | `b.length` bytes |
| `writeBytes(texto)` | un byte por carácter, **sin longitud delante** | n bytes (⚠️ estropea la `ñ` y las tildes) |
| `size()` | — | cuántos bytes lleva escritos este stream |
| `flush()` | fuerza a mandar al disco lo que hay en el búfer | — |

> ⚠️ Para que otro programa lo pueda leer, hay que escribir **en el mismo orden y con los mismos tipos** con los que se va a leer.
> ⚠️ Un literal `long` necesita la `L` al final cuando es grande: `9999L`, `0x12345678L`.

### 8.4 Tres formas de guardar un texto en binario

El problema: un texto puede tener cualquier longitud, así que **quien lee necesita saber dónde acaba**. Por eso casi siempre se guarda **la longitud delante**.

| Forma | Cómo queda en el fichero | Cómo se lee |
|---|---|---|
| `writeUTF(texto)` | **2 bytes** de longitud + los bytes del texto | `readUTF()` |
| `writeInt(bytes.length)` + `write(bytes)` | **4 bytes** de longitud + los bytes del texto | `readInt()` para la longitud → crear un `byte[]` de ese tamaño → `readFully(array)` → `new String(array, StandardCharsets.UTF_8)` |
| Tamaño fijo (por ejemplo, 20 caracteres rellenos de espacios) | siempre los mismos bytes | leer siempre ese número de bytes y `trim()` |

**Convertir entre texto y bytes:**

| Método | Qué hace | Devuelve |
|---|---|---|
| `texto.getBytes(StandardCharsets.UTF_8)` | Texto → bytes | `byte[]` |
| `new String(bytes, StandardCharsets.UTF_8)` | Bytes → texto | `String` |
| `bytes.length` | Cuántos bytes ocupa (⚠️ con `ñ` o tildes son **más** que caracteres) | `int` |

> ⚠️ Si cambias un texto por otro de **distinta longitud**, el fichero entero cambia de tamaño: todo lo que va detrás **se desplaza**. Por eso un texto no se puede "sobrescribir en su sitio" si cambia de longitud; hay que **reescribir el fichero** desde ese punto (o entero).

### 8.5 `RandomAccessFile`: leer y escribir en cualquier posición

**Paquete:** `java.io`. **Cómo se crea:** `new RandomAccessFile(ruta, modo)`, con modo `"r"` (solo leer) o `"rw"` (leer y escribir). La ruta puede ser un `String` o un `File`; si tienes un `Path`, usa `ruta.toFile()`. Tiene **los mismos** `readInt`, `writeInt`, `readUTF`, `writeLong`… que los `Data*Stream`.

| Método | Qué hace | Devuelve |
|---|---|---|
| `seek(pos)` | Mueve el "cursor" al byte `pos` (el primero es el 0) | — |
| `getFilePointer()` | ¿En qué byte está el cursor ahora? | `long` |
| `length()` | Tamaño del fichero | `long` |
| `setLength(n)` | Corta (o alarga) el fichero a `n` bytes | — |
| `readXxx()` / `writeXxx(...)` | Como en `DataInputStream` / `DataOutputStream` | |

> 💡 El truco del cursor: después de **leer** un campo, el cursor queda justo **detrás** de él. Si guardas `getFilePointer()` **antes** de leer un campo, ya sabes su posición para volver con `seek` y sobrescribirlo.
> ⚠️ Escribir **sobrescribe** lo que había en esa posición (no "inserta"). Si escribes un `int` (4 bytes), pisa exactamente 4 bytes.
> ⚠️ Solo es seguro sobrescribir un campo por otro **del mismo tamaño** (`int` por `int`, `long` por `long`). Para textos que cambian de longitud, ver 8.4.

### 8.6 Útiles para comprobar el resultado

| Método | Qué hace | Devuelve |
|---|---|---|
| `Files.size(ruta)` | Tamaño en bytes | `long` |
| `Files.readAllBytes(ruta)` | Todo el fichero a un array | `byte[]` |
| `Files.mismatch(a, b)` | Compara dos ficheros byte a byte | `-1` si son idénticos; si no, la posición del primer byte distinto |
| `Files.copy(origen, copia, StandardCopyOption.REPLACE_EXISTING)` | Hacer una **copia de seguridad** antes de modificar un fichero | `Path` |
| `Arrays.equals(a, b)` | ¿Dos `byte[]` tienen el mismo contenido? | `boolean` |

> 💡 **Antes de modificar un binario, haz una copia.** Si te equivocas escribiendo, el original queda estropeado y no hay "deshacer".

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
| `ByteBuffer` | `java.nio` | Una caja de bytes: `allocate(n)`, `putInt`, `put`, **`flip()`** antes de escribirla. **Detalle en 7.2** |
| `FileChannel` | `java.nio.channels` | La conexión con el fichero: `open(p, opciones)`, `write(buffer)`, `position(n)`. **Detalle en 7.3** |
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
