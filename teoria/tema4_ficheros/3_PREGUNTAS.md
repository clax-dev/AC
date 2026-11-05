# Tema 4: Ficheros, preguntas para comprobarte

> **Cómo usar este archivo:**
> - Lee la pregunta, **contesta en voz alta** (o en un papel) y solo después abre la respuesta.
> - Marca con ✅ las que te salen y repite las demás otro día.
> - Las de ⭐ son las que más se preguntan, o en las que más te has equivocado tú.

## Progreso
- [ ] Bloque 1: ficheros, texto y binario (1-3)
- [ ] Bloque 2: rutas, `File` y `Files` (4-12)
- [ ] Bloque 3: leer, escribir y opciones (13-21)
- [ ] Bloque 4: excepciones y seguridad (22-27)
- [ ] Bloque 5: CSV, binario y serialización (28-36)
- [ ] Bloque 6: acceso aleatorio, NIO y Excel (37-44)

---

## Bloque 1: Ficheros, texto y binario

### 1. ¿Por qué hace falta guardar datos en ficheros?
<details><summary>Respuesta</summary>

Porque las variables viven en la **RAM**, que **se borra** al terminar el programa. El **disco** conserva los datos aunque cierres el programa o apagues el ordenador.
</details>

### 2. ⭐ ¿Qué diferencia hay entre un fichero de texto y uno binario?
<details><summary>Respuesta</summary>

Los dos son bytes. En el de **texto**, los bytes representan **letras** y se lee con el Bloc de notas (`.txt`, `.csv`, `.log`). En el **binario** son **datos en bruto** (números, objetos, imágenes) y en el Bloc de notas salen símbolos raros (`.dat`, `.bin`, `.ser`).
</details>

### 3. Si el binario no se puede leer, ¿para qué sirve?
<details><summary>Respuesta</summary>

**Ocupa menos y es más rápido.** Un `int` ocupa siempre 4 bytes; el mismo número como texto puede ocupar muchos más (`2000000` = 7 caracteres).
</details>

---

## Bloque 2: Rutas, `File` y `Files`

### 4. ⭐ ¿Crear un objeto `Path` crea el fichero en el disco?
<details><summary>Respuesta</summary>

**No.** Un `Path` es solo la **dirección**. No toca el disco y no falla aunque el fichero no exista. El disco se toca cuando usas `Files` (`createDirectories`, `write`, `move`…). *"Escribir la dirección en el sobre no construye la casa."*
</details>

### 5. ⭐ ¿Qué es una ruta relativa y con qué se completa?
<details><summary>Respuesta</summary>

Una dirección incompleta (`"datos/mapa.dat"`). Java le pone delante el **directorio de trabajo**, la carpeta desde la que se ejecuta el programa (en tu proyecto, `C:\DAM\Segundo\AC`). Si no encuentra un fichero, imprime `toAbsolutePath()`.
</details>

### 6. ⭐ ¿Qué diferencia principal hay entre `File` y `Files`?
<details><summary>Respuesta</summary>

`File` (antigua, `java.io`) tiene las acciones en el propio objeto y, si algo falla, **devuelve `false` sin decir por qué**. `Files` (moderna, `java.nio.file`) tiene métodos **estáticos** que **lanzan una excepción concreta** (`NoSuchFileException`, `FileAlreadyExistsException`…).
</details>

### 7. ¿Qué devuelve `Paths.get("salida").resolve("app.log")`? ¿Y si no lo guardas en una variable?
<details><summary>Respuesta</summary>

Devuelve la ruta `salida/app.log`. `Path` es **inmutable**: `resolve` no cambia la ruta original, **devuelve otra**. Si no la guardas, no ha pasado nada. Te pasó tres veces en BioDAM.
</details>

### 8. ¿Qué diferencia hay entre `createDirectory` y `createDirectories`?
<details><summary>Respuesta</summary>

`createDirectory` crea **una** carpeta y falla si no existe la carpeta padre o si ya existe. `createDirectories` crea **toda la cadena** que falte y **no falla si ya existe**: es la forma segura.
</details>

### 9. ⭐ Quiero meter `carpeta` dentro de `ejercicios`. ¿Qué destino le paso a `move` o `renameTo`?
<details><summary>Respuesta</summary>

La dirección **final completa, con el nombre**: `.../ejercicios/carpeta`, no `.../ejercicios`. Además, `ejercicios` **tiene que existir**: mover no construye el camino. Y el orden es `move(DESDE, HASTA)`.
</details>

### 10. ¿Por qué mover un fichero dentro del mismo disco es instantáneo aunque pese mucho?
<details><summary>Respuesta</summary>

Porque no se copian los datos: solo se **cambia la dirección** en el índice del disco. Por eso mover y renombrar son la misma operación.
</details>

### 11. ¿Se puede borrar una carpeta con ficheros dentro?
<details><summary>Respuesta</summary>

No directamente: solo se borra **vacía**. Con `Files.delete` salta `DirectoryNotEmptyException`; con `File.delete()` devuelve `false`. Hay que borrar antes el contenido.
</details>

### 12. ⭐ `listFiles()` o `Files.list()` solo dan el primer nivel. ¿Cómo listo todas las subcarpetas?
<details><summary>Respuesta</summary>

Con **recursividad**: recorro el contenido y, si un elemento es carpeta, **llamo al mismo método** con ella (`Listar(archivo, nivel + 1)`). Termina sola cuando ya no hay subcarpetas (**caso base**). Otra opción: `Files.walk(carpeta)`.
</details>

---

## Bloque 3: Leer, escribir y opciones

### 13. ⭐ ¿Por qué hay que cerrar un fichero después de escribir?
<details><summary>Respuesta</summary>

Porque Java guarda lo que escribes en un **búfer** y lo manda al disco por bloques. **Al cerrar se vacía el búfer.** Si no cierras, lo último puede no llegar al disco (en `ejemplo.java`: "sin el close no se escribe nada"). Además, un fichero abierto queda **bloqueado** en Windows.
</details>

### 14. ⭐ ¿Qué es try-with-resources y por qué se usa?
<details><summary>Respuesta</summary>

Es abrir el recurso **dentro de los paréntesis del `try`**: `try (Stream<String> s = Files.lines(p)) { ... }`. Java lo **cierra solo** al salir del bloque, vaya bien o salte una excepción.
</details>

### 15. ¿Cuáles de estos dejan el fichero abierto y necesitan `try ( )`: `readAllLines`, `lines`, `list`, `writeString`?
<details><summary>Respuesta</summary>

**`Files.lines` y `Files.list`**, porque devuelven un `Stream` que lee poco a poco. `readAllLines` y `writeString` abren y cierran ellos solos.
</details>

### 16. ¿Cómo sé que he llegado al final con `BufferedReader.readLine()`?
<details><summary>Respuesta</summary>

Devuelve **`null`**: `while ((linea = br.readLine()) != null)`.
</details>

### 17. ⭐ Escribo en un fichero que ya existe sin pasar opciones. ¿Qué pasa?
<details><summary>Respuesta</summary>

**Lo vacía y escribe desde cero** (equivale a `CREATE` + `TRUNCATE_EXISTING` + `WRITE`). Lo que había se pierde.
</details>

### 18. ⭐ ¿Qué hace `CREATE_NEW` y para qué se usa?
<details><summary>Respuesta</summary>

Crea el fichero, pero si **ya existe se niega** y lanza `FileAlreadyExistsException`. Se usa para la **configuración**: no machacar lo que el usuario cambió. Laboratorio, bloque 1.
</details>

### 19. ⭐ ¿Por qué `CREATE_NEW` es mejor que `if (!Files.exists(p)) Files.write(p, ...)`?
<details><summary>Respuesta</summary>

Porque entre **comprobar** y **escribir** hay un hueco en el que otro programa puede crear el fichero. `CREATE_NEW` lo hace **en un solo paso (atómico)**. Es la trampa de "comprobar y luego actuar" de PSP.
</details>

### 20. ⭐ ¿Qué opciones uso para un log? ¿Por qué `APPEND` solo no basta?
<details><summary>Respuesta</summary>

`CREATE, APPEND`. `APPEND` escribe al final sin borrar, pero **falla si el fichero no existe**; `CREATE` lo crea la primera vez. Con `CREATE_NEW` el log reventaría en la segunda línea.
</details>

### 21. Las líneas del log salen pegadas con `writeString`. ¿Por qué? ¿Y por qué no compila `Files.write(ruta, "texto")`?
<details><summary>Respuesta</summary>

`writeString` **no añade el salto de línea**: hay que sumar `System.lineSeparator()`. `Files.write` es para una **lista** de líneas o un `byte[]`; para un `String` se usa `writeString`.
</details>

---

## Bloque 4: Excepciones y seguridad

### 22. ⭐ ¿De qué excepción heredan las de ficheros? ¿Qué significa que sea "checked"?
<details><summary>Respuesta</summary>

De **`IOException`**. *Checked* significa que Java **obliga** a tratarla: o la atrapas con `try/catch` o la declaras con `throws IOException`.
</details>

### 23. ¿Por qué atrapar `FileAlreadyExistsException` y no `IOException`?
<details><summary>Respuesta</summary>

Porque si pasa **otra cosa** distinta (falta de permisos, disco lleno), no la confundes con la que esperabas. Además, en una cadena de `catch` las concretas van **antes** que las generales.
</details>

### 24. Une cada excepción con su causa: `NoSuchFileException`, `DirectoryNotEmptyException`, `AccessDeniedException`, `EOFException`.
<details><summary>Respuesta</summary>

- `NoSuchFileException`: el fichero o la carpeta no existe.
- `DirectoryNotEmptyException`: borrar una carpeta con contenido.
- `AccessDeniedException`: no hay permiso.
- `EOFException`: se ha llegado al final de un binario leído con `DataInputStream`.
</details>

### 25. ⭐ ¿Qué es el Path Traversal?
<details><summary>Respuesta</summary>

Un ataque en el que el usuario mete **`..`** en una ruta para **salirse** de la carpeta permitida: `datos/../../etc/passwd` empieza por `datos` pero acaba fuera.
</details>

### 26. ⭐ ¿Cómo se evita? ¿En qué orden van `toAbsolutePath` y `normalize`?
<details><summary>Respuesta</summary>

1. Pasar **las dos** rutas (base y candidata) a `toAbsolutePath()` y **luego** `normalize()` (que resuelve los `..`).
2. Comprobar `candidata.startsWith(base)` con el **`startsWith` de `Path`**.
Si no empieza por la base: `SecurityException` (y un `WARN` en el log, en BioDAM).
</details>

### 27. ⭐ ¿Por qué no vale `String.startsWith`?
<details><summary>Respuesta</summary>

Porque compara **letra a letra**: `"salida2/secreto.txt"` empieza por `"salida"` y se colaría. `Path.startsWith` compara **carpeta a carpeta**, y `salida2` no es `salida`.
</details>

---

## Bloque 5: CSV, binario y serialización

### 28. ¿Cómo se guarda y se carga un objeto en CSV?
<details><summary>Respuesta</summary>

**Guardar:** juntar los atributos con el separador (`String.join(";", ...)`), una línea por objeto, con una cabecera arriba. **Cargar:** `skip(1)` para la cabecera, `split(";")` y convertir cada trozo (`Integer.parseInt`, `Double.parseDouble`).
</details>

### 29. `Double.parseDouble("25,90")` falla. ¿Por qué?
<details><summary>Respuesta</summary>

Java espera **punto** decimal. Lanza `NumberFormatException`. Al escribir: `String.format(Locale.ROOT, "%.2f", precio)`.
</details>

### 30. ⭐ ¿Cuál es la regla de oro de `DataInputStream`?
<details><summary>Respuesta</summary>

Leer en el **mismo orden y con los mismos tipos** con que se escribió. El fichero no tiene etiquetas: si te equivocas, **no hay error**, solo números sin sentido, y todo lo de detrás también sale mal.
</details>

### 31. ⭐ ¿Cómo sé que he llegado al final de un fichero con `DataInputStream`?
<details><summary>Respuesta</summary>

Lanza **`EOFException`** (no devuelve `null` ni `-1`). Se lee en un `while (true)` y se captura `EOFException`.
</details>

### 32. ¿Cuánto ocupa en binario `writeInt(5)`, `writeDouble(2.5)`, `writeLong(1)` y `writeUTF("Hola")`?
<details><summary>Respuesta</summary>

4, 8, 8 y **6** bytes (`writeUTF` = 2 bytes de longitud + 4 del texto). Total: 26.
</details>

### 33. ⭐ ¿Qué necesita una clase para poder serializarse?
<details><summary>Respuesta</summary>

`implements Serializable` (interfaz **marca**, sin métodos), y que sus atributos también sean serializables. Si no: `NotSerializableException`.
</details>

### 34. ⭐ ¿Qué es `serialVersionUID`?
<details><summary>Respuesta</summary>

El **número de versión** de la clase. Si cambias la clase y ya no coincide con el del fichero, al leer salta `InvalidClassException`.
</details>

### 35. ⭐ ¿Qué hace `transient`? ¿Qué vale ese atributo al leer?
<details><summary>Respuesta</summary>

Ese atributo **no se guarda** (contraseñas, datos calculados). Al leer vale su **valor por defecto**: `null`, `0` o `false`.
</details>

### 36. ¿Por qué `readObject()` necesita un cast? ¿Cuándo elijo CSV y cuándo serialización?
<details><summary>Respuesta</summary>

Porque devuelve `Object`: `(List<Incidencia>) in.readObject()`. **CSV** si lo tiene que leer una persona, Excel u otro lenguaje. **Serialización** para guardar y recuperar objetos Java cómodamente (solo lo entiende Java, con la misma versión de la clase).
</details>

---

## Bloque 6: Acceso aleatorio, NIO y Excel

### 37. ⭐ ¿Qué diferencia hay entre acceso secuencial y aleatorio?
<details><summary>Respuesta</summary>

**Secuencial:** se lee desde el principio y en orden; para llegar al registro 1000 hay que pasar por los 999 anteriores (como una cinta). **Aleatorio (directo):** saltas a cualquier posición (como un array) con `seek`.
</details>

### 38. ⭐ ¿Qué condición necesita el acceso aleatorio? ¿Cómo se calcula la posición del registro n?
<details><summary>Respuesta</summary>

**Registros de tamaño fijo.** Posición = **n × tamaño del registro** (n empieza en 0). Los textos se rellenan con espacios hasta un tamaño fijo.
</details>

### 39. Un registro tiene `int id`, nombre de 20 caracteres con `writeChars` y `double precio`. ¿Cuánto ocupa? ¿Dónde empieza el registro 3?
<details><summary>Respuesta</summary>

4 + 20×2 + 8 = **52 bytes**. El registro 3 empieza en 3 × 52 = **156**.
</details>

### 40. ¿Qué modos tiene `RandomAccessFile`? ¿Escribir en una posición inserta o sobrescribe?
<details><summary>Respuesta</summary>

`"r"` (solo leer) y `"rw"` (leer y escribir). Escribir **sobrescribe** los bytes que había; por eso solo es seguro cambiar un campo por otro del mismo tamaño.
</details>

### 41. ⭐ ¿Qué papel tienen `FileChannel` y `ByteBuffer`?
<details><summary>Respuesta</summary>

`FileChannel` es la **conexión** (la tubería) con el fichero, en la posición que elijas. `ByteBuffer` es una **caja de bytes** en memoria (el cubo): se llena, se manda por el canal o se vacía después de leer.
</details>

### 42. ⭐ ¿Para qué sirve `flip()`? ¿Qué pasa si se te olvida?
<details><summary>Respuesta</summary>

Pasa la caja de **"modo llenar" a "modo vaciar"**: `limit = position` y `position = 0`. Sin él, el cursor está al final y **no se escribe nada** (o al leer salta `BufferUnderflowException`).
</details>

### 43. ¿Por qué `canal.write(buffer)` va dentro de `while (buffer.hasRemaining())`? ¿Y por qué `MapaBinario` no necesita `flip()`?
<details><summary>Respuesta</summary>

Porque `write` **no garantiza** mandar todos los bytes de una vez. `MapaBinario` usa `ByteBuffer.wrap(Files.readAllBytes(ruta))`: la caja nace **llena y lista para leer** desde la posición 0.
</details>

### 44. ⭐ ¿Qué es Apache POI y cómo se organiza un Excel con él?
<details><summary>Respuesta</summary>

Una **librería externa** para crear ficheros de Office (`org.apache.poi:poi-ooxml`). Estructura: `XSSFWorkbook` (libro) → `Sheet` (hoja) → `Row` (fila) → `Cell` (celda), **numeradas desde 0**. Las fórmulas van **sin `=`** (`B2*C2`) y los números se guardan como número.
</details>
