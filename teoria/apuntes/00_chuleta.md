# Chuleta rápida de AC (Acceso a Datos)

> ¿No recuerdas qué hace un método o de qué librería sale? → `05_guia_librerias.md`

## Lambdas
- La sintaxis es `(params) -> cuerpo`, y siempre se asigna a una **interfaz funcional** (con un solo método abstracto).
- **Function** transforma, **Predicate** filtra, **Consumer** actúa, **Supplier** genera y **Comparator** ordena.
- Se llaman con `apply`, `test`, `accept`, `get` y `compare`. Si las **pasas** a otro método, va solo el nombre, sin `()`.
- Los predicados se combinan con `.and()`, `.or()` y `.negate()`; las Function y los Consumer, con `.andThen()`.
- Las variables locales que use una lambda tienen que ser **efectivamente finales**.
- Atajo: `Clase::metodo`, solo cuando la lambda llama a un único método.

## Comparator
- `compare(a, b)` devuelve: negativo → `a` va antes · 0 → empate · positivo → `a` va después.
- `Comparator.comparingInt(...)` / `comparingDouble(...)` / `comparing(...)` → `.reversed()` → `.thenComparing(...)`.

## Colecciones
- Para buscar por ID usa **HashMap**; para mantener un orden, **ArrayList**; para una cola FIFO, **LinkedList**; para valores únicos, **HashSet**.
- `poll()` devuelve null si la cola está vacía; `getOrDefault`/`merge` sirven para contar.
- `List.of(...)` es **inmutable**: si la necesitas modificar, cópiala con `new ArrayList<>(...)`.

## Streams
- La cadena es `stream()` → `filter` / `map` / `sorted` → `toList` / `forEach` / `findFirst` / `average`.
- Sin una operación final no se ejecuta nada, y la lista original no cambia.
- `findFirst()` devuelve un `Optional`, nunca `null`. `average()` devuelve un `OptionalDouble`.

## Ficheros
- Rutas: `Path.of("carpeta", "fichero")`, y para el resto `Files.exists`, `createDirectories`, `deleteIfExists`… Si no lo encuentra, imprime `toAbsolutePath()`.
- **Todo lo que se abre va en `try (...)`** (try-with-resources), para que se cierre solo.
- Texto: `BufferedWriter` / `BufferedReader` (`readLine()` devuelve `null` al final) o `Files.lines(p)`, que es un stream. Usa siempre `UTF_8`.
- CSV: objeto → `String.join(";", ...)`; línea → `split(";")` + `parseInt`/`parseDouble`. Escribe los decimales con `Locale.ROOT`.
- Binario: `DataOutputStream` / `DataInputStream`; se lee en el **mismo orden** y el final del fichero lanza `EOFException`.
- Objetos enteros: `implements Serializable` + `ObjectOutputStream.writeObject` / `(Cast) readObject()`. Lo `transient` no se guarda.
- Acceso aleatorio: `RandomAccessFile` + registros de tamaño fijo + `seek(n * TAMAÑO)`.
