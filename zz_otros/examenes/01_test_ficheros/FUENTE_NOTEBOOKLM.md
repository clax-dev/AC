# Acceso a Datos (DAM 2º) · Tema 4: Ficheros en Java

Fuente de estudio para generar preguntas tipo test. Temario: rutas (Path, Paths, File, Files), rutas absolutas y relativas, crear/mover/copiar/borrar/listar ficheros y carpetas, recursividad, lectura y escritura de ficheros de texto, try-with-resources, búfer, codificación UTF-8, opciones de apertura (StandardOpenOption, StandardCopyOption), excepciones de E/S, Path Traversal, fechas con DateTimeFormatter, CSV, ficheros binarios (DataInputStream, DataOutputStream), serialización (ObjectInputStream, ObjectOutputStream, Serializable, serialVersionUID, transient), acceso aleatorio (RandomAccessFile), NIO (ByteBuffer, FileChannel), cálculos con binarios (tamaño de registro, posición de un registro y de un campo, hexadecimal a decimal) y Excel con Apache POI.

Nota: «F01…F06» son programas de ejemplo de clase; «Laboratorio», «BioDAM», «MapaBinario», «ejemplo.java» y «ejercicio.java» son ejercicios de clase.

# PARTE 1: EXPLICACIÓN

**La historia entera en tres líneas:**
- Todo lo que guarda tu programa en variables vive en la **RAM**, y la RAM **se borra** al cerrar el programa.
- Un **fichero** es un trozo del **disco** con nombre: lo que escribes ahí **se queda**. Por eso la asignatura se llama *Acceso a Datos*.
- En este tema aprendes a **encontrar** un fichero (rutas), a **leerlo y escribirlo** (texto y binario) y a **guardar objetos** en él.

---

### 1. RAM, disco, texto y binario

> 💬 **"La RAM se olvida al cerrar; el disco se acuerda. Un fichero de texto son letras; uno binario son datos en bruto."**

- **Fichero** = datos guardados en el disco con un nombre. **Carpeta** (directorio) = una caja que **agrupa** ficheros y otras carpetas; no guarda datos.
- Por dentro **todo fichero son bytes** (números del 0 al 255). Lo que cambia es **cómo se interpretan**:
  - **Texto** (`.txt`, `.csv`, `.log`, `.properties`): los bytes son **letras**. Se lee con el Bloc de notas.
  - **Binario** (`.dat`, `.bin`, `.ser`, `.jpg`, `.xlsx`): los bytes son **datos en bruto**. En el Bloc de notas salen símbolos raros.
- ¿Por qué usar binario si no se lee? Porque **ocupa menos y es más rápido**: el número `2000000` como texto son 7 caracteres; como `int` binario son **siempre 4 bytes**.

---

### 2. Una ruta NO es el fichero
🧪 *Demo `F01_RutasYDirectorios`*

> 💬 **"Escribir la dirección en el sobre no construye la casa."**

Es **la idea más importante del tema**.
- Una **ruta** es la **dirección** de un fichero o carpeta. En Java se guarda en un objeto `Path` (moderno) o `File` (antiguo).
- **Crear el objeto `Path` no toca el disco**: no crea nada, no tarda nada y **no falla** aunque el fichero no exista.
- Por eso siempre hay **dos momentos**:
  1. **Escribir la dirección** (`Paths.get(...)`, `Path.of(...)`, `resolve(...)`): no toca el disco.
  2. **Hacer algo con ella** (`Files.createDirectories`, `Files.write`, `Files.move`…): **sí** toca el disco y **sí** puede fallar.

**Rutas absolutas y relativas:**
- **Absoluta:** la dirección completa desde la unidad: `C:/DAM/Segundo/AC/datos/mapa.dat`.
- **Relativa:** incompleta (`"datos/mapa.dat"`). Java le pone delante el **directorio de trabajo**, que es la carpeta desde la que se ejecuta el programa (en tu proyecto, `C:\DAM\Segundo\AC`).

> **Si "no encuentra el fichero":** casi siempre la ruta relativa se completa con un directorio de trabajo distinto del que crees. Imprime `toAbsolutePath()` y lo verás.

**Las trampas de `Path` que ya te han pasado (BioDAM):**
- `resolve` **devuelve** una ruta nueva: si no la guardas en una variable, no ha pasado nada. `Path` no cambia nunca (es **inmutable**), igual que `String`.
- **Una variable por cosa:** `carpetaLogs` para la carpeta y `archivoLog` para el fichero. Si las mezclas, `createDirectories` acaba creando una *carpeta* que se llama `auditoria_….log`.

---

### 3. Dos cajas de herramientas: `File` (antigua) y `Path` + `Files` (moderna)

> 💬 **"`File` se queja con un `false` y no dice por qué; `Files` lanza una excepción que te dice qué ha pasado."**

| | `java.io.File` (Java 1) | `java.nio.file` (Java 7+) |
|---|---|---|
| La dirección | un objeto `File` | un objeto `Path` |
| Las acciones | métodos **del propio objeto**: `f.mkdir()` | métodos **estáticos de `Files`**: `Files.createDirectory(p)` |
| Si algo falla | devuelve **`false`**, sin decir por qué | **lanza una excepción** concreta |

- Con `File`, un `mkdir()` que da `false` puede ser "ya existía", "no tengo permiso" o "no existe la carpeta padre": **no hay forma de saberlo**.
- Con `Files`, cada problema es una excepción distinta: `FileAlreadyExistsException`, `NoSuchFileException`, `AccessDeniedException`…
- **Truco para leer código:** `File` + métodos sobre el objeto = antigua. `Files.` **con s** = moderna.
- Se pasa de una a otra con `ruta.toFile()` y `fichero.toPath()`.

---

### 4. Trabajar con cajas cerradas: crear, mover, borrar y listar
🧪 *Demo `F01`* · 📝 *`ejemplo.java`, `ejercicio.java`*

> 💬 **"Mover es cambiar la dirección; el destino lleva el nombre final y su carpeta tiene que existir. Una carpeta solo se borra vacía."**

Antes de tocar el **contenido**, hay operaciones que tratan ficheros y carpetas como cajas cerradas:
- **Crear una carpeta** solo funciona si su **padre ya existe**. **Crear varias anidadas** (`createDirectories` / `mkdirs`) crea toda la cadena y **no falla si ya existe**: es la forma segura.
- **Mover y renombrar son la misma operación** (`Files.move` / `renameTo`): las dos cambian la dirección.
  - El destino es la **dirección completa, con el nombre incluido**: `.../ejercicios/carpeta`, no `.../ejercicios`.
  - La carpeta de destino **tiene que existir**: mover no construye el camino.
  - El orden es siempre **`move(DESDE, HASTA)`**. En BioDAM lo pusiste al revés.
  - Dentro del mismo disco es **instantáneo**: no se copia nada, solo cambia la dirección.
- **Borrar:** una carpeta **solo se borra si está vacía** (si no, `DirectoryNotEmptyException`).
- **Listar** una carpeta da **solo el primer nivel**. Para entrar en las subcarpetas se usa **recursividad**: "si es carpeta, la recorro de la misma forma". Termina sola cuando ya no hay subcarpetas (el **caso base**). Es tu `ejercicio.java` con `Listar(archivo, nivel + 1)`.

---

### 5. Leer y escribir: abrir, usar y cerrar
🧪 *Demo `F02_FicherosTexto`*

> 💬 **"Como un grifo: abrir, usar y cerrar. Lo que se abre va en `try ( )` y se cierra solo."**

Toda lectura o escritura tiene **tres pasos**: **abrir** el fichero, **usarlo** y **cerrarlo**.

**¿Por qué es tan importante cerrar?**
- **Al escribir**, Java no manda cada letra al disco: las junta en un **búfer** (una zona de espera en memoria) y las manda por bloques. **Al cerrar se vacía el búfer.** Si no cierras, lo último puede **no llegar nunca al disco**. Lo viste en `ejemplo.java`: *"sin el close no se escribe nada"*.
- **Al leer**, mientras está abierto, el sistema lo tiene **bloqueado**: en Windows no podrías ni borrarlo ni moverlo.

**`try-with-resources`:** si abres el recurso **dentro de los paréntesis del `try`**, Java lo cierra **solo** al salir del bloque, tanto si va bien como si salta un error. Es la forma recomendada **siempre**.
- Ojo: `Files.lines(p)` y `Files.list(p)` **dejan el fichero abierto** (devuelven un stream): van en `try ( )`. En cambio `readAllLines`, `readString`, `write` y `writeString` abren y cierran ellos solos.

**Tres formas de leer un texto:**
- **Todo de golpe** (`readAllLines`, `readString`): cómodo, pero un fichero enorme llena la memoria.
- **Línea a línea** (`BufferedReader.readLine()`): el bucle termina cuando devuelve **`null`**.
- **Como un stream** (`Files.lines`): `filter`, `map`, `count`… igual que en el tema 3. Es lo que pedía BioDAM ("cero bucles").

**La codificación:** hace falta un acuerdo sobre qué número es cada letra. Para la `ñ` y las tildes no todas coinciden: si escribes con una y lees con otra, sale `Ã±`. **Usa siempre UTF-8** (es lo que usa `Files` por defecto).

---

### 6. Escribir en un fichero que ya existe: las opciones de apertura
📝 *Laboratorio, bloques 1 y 2 · BioDAM, bloque 1*

> 💬 **"Sin opciones lo machaca; `CREATE_NEW` se niega; `CREATE` + `APPEND` añade al final."**

| Opción | Si NO existe | Si YA existe | Uso típico |
|---|---|---|---|
| *(ninguna)* | lo crea | **lo vacía y escribe desde cero** | datos que se regeneran |
| `CREATE_NEW` | lo crea | **se niega**: `FileAlreadyExistsException` | **configuración**: no machacar lo que cambió el usuario |
| `CREATE` + `APPEND` | lo crea | **escribe al final** sin borrar | **logs**: el historial crece |

- `APPEND` solo **falla si el fichero no existe**. Por eso va casi siempre con `CREATE` ("créalo si hace falta").
- **¿Por qué `CREATE_NEW` y no "comprobar si existe y luego escribir"?** Porque entre **comprobar** y **escribir** hay un hueco en el que otro puede crear el fichero. Es la misma trampa de **"comprobar y luego actuar"** de PSP. `CREATE_NEW` lo hace **en un solo paso (atómico)**.
- `writeString` **no añade el salto de línea**: hay que sumar `System.lineSeparator()`. `write` con una lista sí pone una línea por elemento.

---

### 7. Los avisos del disco: las excepciones

> 💬 **"El disco puede fallar por cosas que no dependen de tu código. Atrapa la excepción concreta que esperas, no una genérica."**

- Todas son hijas de **`IOException`**, que es **checked**: Java te **obliga** a tratarla.
- Dos opciones: **atraparla** con `try/catch` (si sabes qué hacer: "si ya existía, aviso y sigo") o **dejarla pasar** con `throws IOException` (que decida quien te llamó).
- Atrapa la **concreta** (`FileAlreadyExistsException`): así, si pasa **otra** cosa (falta de permisos), no la confundes.

| Excepción | Cuándo salta |
|---|---|
| `NoSuchFileException` / `FileNotFoundException` | no existe el fichero o la carpeta |
| `FileAlreadyExistsException` | ya existe (`CREATE_NEW`, `createFile`, `createDirectory`, `move`/`copy` sin `REPLACE_EXISTING`) |
| `DirectoryNotEmptyException` | borrar una carpeta con contenido |
| `AccessDeniedException` | no tienes permiso |
| `EOFException` | final de un fichero binario leído con `Data*Stream` |

---

### 8. Seguridad: el Path Traversal
📝 *Laboratorio, TODO 2.3 · BioDAM, bloque 2*

> 💬 **"`..` sube una carpeta. Normaliza las dos rutas y compara carpeta a carpeta, nunca letra a letra."**

Si **el usuario** te da una ruta, tienes que comprobar que no se sale de la carpeta permitida.
- El truco del atacante es el **`..`** ("sube una carpeta"). `datos/../../etc/passwd` **empieza** por `datos`, pero en realidad sube dos niveles y sale fuera.
- **La defensa, en dos pasos:**
  1. **Normalizar** las dos rutas: primero `toAbsolutePath()` y **luego** `normalize()` (que resuelve los `..`). En ese orden y **en las dos**.
  2. **Comprobar** con `Path.startsWith(base)`, que compara **carpeta a carpeta**.
- ¿Por qué no `String.startsWith`? Porque como texto, `"salida2/secreto.txt"` empieza por `"salida"`, y se colaría. Como `Path`, `salida2` y `salida` son carpetas distintas.
- Para **probar** un ataque, la ruta tiene que **salirse** de verdad: `datos/essd/ppp` está **dentro** de `datos`.

---

### 9. Guardar objetos: cuatro formatos
🧪 *Demos `F03`, `F04`, `F05`, `F06`*

> 💬 **"CSV para personas y Excel; `Data*Stream` para números compactos; serialización para objetos Java enteros; acceso aleatorio para ir directo a un registro."**

**9.1 CSV: objetos como líneas de texto** (`F03`). Cada objeto es una línea, con los campos separados por `;`. La primera línea es la **cabecera** (`skip(1)` para saltarla).
- **Guardar:** juntar campos (`String.join(";", ...)`). **Cargar:** `split(";")` y convertir cada trozo (`Integer.parseInt`, `Double.parseDouble`).
- Ventaja: es texto, lo abre cualquiera. Inconveniente: conviertes cada campo a mano. Los decimales con **punto** (`Locale.ROOT`), no con coma.

**9.2 Binario con tipos primitivos** (`DataOutputStream` / `DataInputStream`, `F04`). Escribes datos **seguidos, sin etiquetas**: un `String`, un `double`, un `int`…
- **Regla de oro:** se lee en el **mismo orden y con los mismos tipos** con que se escribió. Si no, salen números sin sentido **sin ningún error**, y todo lo de detrás también sale mal.
- Al final del fichero no hay `null`: salta **`EOFException`**.

**9.3 Serialización: el objeto entero de golpe** (`ObjectOutputStream` / `ObjectInputStream`, `F05`). Java convierte el objeto completo en bytes (**serializar**) y lo reconstruye (**deserializar**). Puedes guardar una **lista entera** de una vez.
- La clase tiene que ser **`implements Serializable`** (una "marca" sin métodos). Si no: `NotSerializableException`.
- **`serialVersionUID`** = número de versión de la clase. Si no coincide con el del fichero: `InvalidClassException`.
- **`transient`** = ese atributo **no se guarda** (contraseñas). Al leer vale `null`, `0` o `false`.
- `readObject()` devuelve `Object`: hace falta un **cast**. Solo lo entiende **Java**.

**9.4 Acceso aleatorio** (`RandomAccessFile`, `F06`). Lo anterior es **secuencial**: para llegar al registro 1000 lees los 999 de antes, como una cinta de casete. El acceso **aleatorio (directo)** salta a cualquier posición, como un array.
- Condición: **registros de tamaño fijo**. Así, **posición = n × tamaño del registro**.
- Los textos se **rellenan con espacios** hasta un tamaño fijo.
- Ventaja: **leer o modificar un solo registro** sin tocar el resto.

---

### 10. Un binario por dentro
📝 *`MapaBinario`*

> 💬 **"Un binario es una fila de bytes sin etiquetas: tienes que saber qué tipo va en cada sitio y cuánto ocupa."**

- Tamaños: `byte` y `boolean` 1 · `short` y `char` 2 · `int` y `float` 4 · `long` y `double` 8 · `writeUTF` = **2 bytes de longitud + el texto**.
- Los números se guardan con el byte más importante primero (*big-endian*): el `int` 52 son `00 00 00 34`.
- En los visores, cada byte sale en **hexadecimal** (`00` a `FF`). En Java: `0xCAFE`, `Integer.toHexString(n)`.
- **Trampa del signo:** `readShort()` / `getShort()` devuelven con signo. `0xCAFE` sale **negativo** y `== 0xCAFE` da `false`. Solución: `readUnsignedShort()` o `Short.toUnsignedInt(s)`. Es lo que hiciste en `MapaBinario` con el flag de inicio.
- **Comprobar:** `Files.size(ruta)` y comparar con la suma de lo que ocupa cada campo.
- **Antes de modificar un binario, haz una copia**: no hay "deshacer".

---

### 11. Canales y búferes: la forma NIO (`FileChannel` + `ByteBuffer`)
📝 *Laboratorio, bloque 3 · `MapaBinario`*

> 💬 **"El canal es la tubería y el `ByteBuffer` es el cubo. Llenas el cubo, le das la vuelta con `flip()` y lo vacías por la tubería."**

- **`FileChannel`:** la **conexión** con el fichero; puedes elegir **en qué posición** leer o escribir.
- **`ByteBuffer`:** una **caja de bytes** de tamaño fijo en memoria. No escribes en el fichero: **llenas la caja** y **mandas la caja**.
- **El `flip()`:** al llenar, el cursor (`position`) avanza hasta el final. Si mandas la caja así, no se manda nada. `flip()` **pone el cursor al principio** y marca hasta dónde hay datos: pasa de **"modo llenar"** a **"modo vaciar"**.
- `write` puede no mandarlo todo de una vez: se repite **mientras** `hasRemaining()`.
- Atajo para leer un fichero pequeño: `Files.readAllBytes` + `ByteBuffer.wrap(...)`: caja **ya llena** y lista para leer, sin canal y sin `flip()`. Es lo que usa `MapaBinario`.
- Escribir en una posición **sobrescribe**, no inserta. Si un campo cambia de tamaño, todo lo de detrás se descoloca.

---

### 12. Excel con Apache POI
📝 *Laboratorio, bloque 4*

> 💬 **"Libro → hoja → fila → celda, y todo empieza en 0."**

- Un `.xlsx` no es texto, y Java no sabe crearlo solo: se usa la **librería externa** Apache POI (`org.apache.poi:poi-ooxml`). Sin ella, las clases salen en rojo.
- Estructura: `XSSFWorkbook` (libro) → `Sheet` (hoja) → `Row` (fila) → `Cell` (celda). Filas y celdas **desde 0** (la celda 0 es la columna A).
- Una celda guarda **texto**, **número** (como número, para que Excel opere) o **fórmula** (`B2*C2`, **sin** el `=`).
- El **estilo** (negrita, color) se crea aparte y se aplica. Al final se **guarda** con el ciclo abrir → escribir → cerrar.

---

### 13. Resumen

| Concepto | Frase clave |
|---|---|
| RAM / disco | La RAM se olvida; el disco se acuerda |
| Texto / binario | Letras / datos en bruto (ocupa menos) |
| Ruta | Escribir la dirección no construye la casa |
| Relativa | Se completa con el directorio de trabajo: imprime `toAbsolutePath()` |
| `File` / `Files` | `false` sin explicación / excepción concreta |
| Mover | Mismo método que renombrar; destino con nombre; `move(DESDE, HASTA)` |
| Borrar carpeta | Solo vacía |
| Abrir/usar/cerrar | `try ( )` y se cierra solo; sin cerrar, el búfer no llega al disco |
| Opciones | Nada = machaca · `CREATE_NEW` = se niega · `CREATE`+`APPEND` = añade |
| Excepciones | Hijas de `IOException` (checked); atrapa la concreta |
| Path Traversal | `toAbsolutePath().normalize()` en las dos + `Path.startsWith` |
| CSV | Personas y Excel; `split` + `parse` |
| `Data*Stream` | Mismo orden y tipos; fin = `EOFException` |
| Serialización | `Serializable`, `serialVersionUID`, `transient`, cast |
| Acceso aleatorio | Tamaño fijo: posición = n × tamaño |
| `ByteBuffer` | Llenar → `flip()` → vaciar |
| POI | Libro → hoja → fila → celda, desde 0 |

**¿Y ahora?**
1. Repasa el `visual.pdf` entero.
2. Haz `3_PREGUNTAS.md` tapando las respuestas.
3. Cuando necesites escribir código, abre `2_TECNICO.md` (y `GUIA_LIBRERIAS.md` para cualquier método).

---

# PARTE 2: SINTAXIS Y MÉTODOS

---

### 1. `Path`: la dirección
🧪 `F01_RutasYDirectorios`

#### Plantilla
```java
Path carpeta = Paths.get("salida", "config");      // salida/config   (Path.of hace lo mismo)
Path fichero = carpeta.resolve("app.properties");   // salida/config/app.properties
System.out.println(fichero.toAbsolutePath());       // C:\DAM\Segundo\AC\salida\config\app.properties
```

#### Métodos (ninguno toca el disco)
| Método | Qué hace | Ejemplo |
|---|---|---|
| `Paths.get("a","b")` / `Path.of("a","b")` | Crea la ruta `a/b` | |
| `p.resolve("x")` | Añade `x` al final | `a/b` → `a/b/x` |
| `p.getFileName()` | El último trozo (nombre) | `app.properties` |
| `p.getParent()` | La carpeta que lo contiene | `salida/config` |
| `p.toAbsolutePath()` | Completa desde la unidad | `C:\…\app.properties` |
| `p.normalize()` | Resuelve `.` y `..` | `a/b/../c` → `a/c` |
| `p.startsWith(otra)` | ¿Empieza por esa ruta, **carpeta a carpeta**? | |
| `p.toFile()` | `Path` → `File` | |
| `p.toString()` | La ruta como texto | |

#### ⚠ Reglas
- `Path` es **inmutable**: `resolve`, `normalize` y `toAbsolutePath` **devuelven otra ruta**. Guárdala: `Path nueva = p.resolve(x);`.
- `carpeta.resolve(rutaAbsoluta)` **ignora la carpeta** y devuelve la absoluta. Para meter un fichero en otra carpeta: `carpeta.resolve(ruta.getFileName())`.
- Las rutas relativas se completan con el **directorio de trabajo** (`C:\DAM\Segundo\AC`).

---

### 2. `Files`: crear, mover, copiar, borrar, listar
🧪 `F01_RutasYDirectorios` · 📝 BioDAM bloque 4

#### Plantilla
```java
Files.createDirectories(carpeta);                                   // crea la cadena; no falla si ya existe
Files.move(origen, destino, StandardCopyOption.REPLACE_EXISTING);   // move(DESDE, HASTA)
Files.copy(origen, copia, StandardCopyOption.REPLACE_EXISTING);
Files.deleteIfExists(fichero);

try (Stream<Path> contenido = Files.list(carpeta)) {                // list deja abierto: try ( )
    contenido.forEach(p -> System.out.println(p.getFileName()));
}
```

#### Métodos (todos `static`; casi todos lanzan `IOException`)
| Método | Qué hace | Ojo |
|---|---|---|
| `exists(p)` / `notExists(p)` | ¿Existe? | No lanza excepción |
| `isDirectory(p)` / `isRegularFile(p)` | ¿Carpeta? ¿Fichero? | |
| `isReadable(p)` / `isWritable(p)` | ¿Permisos? | |
| `size(p)` | Tamaño en bytes → `long` | |
| `getLastModifiedTime(p)` | Última modificación → `FileTime` | |
| `createDirectory(p)` | **Una** carpeta | Falla si no existe el padre o si ya existe |
| `createDirectories(p)` ⭐ | Toda la cadena | **No falla** si ya existe |
| `createFile(p)` | Fichero vacío | Falla si ya existe |
| `move(origen, destino, op...)` | Mover o renombrar | Destino **con el nombre**; su carpeta tiene que existir |
| `copy(origen, destino, op...)` | Copiar | Sin `REPLACE_EXISTING` falla si existe |
| `delete(p)` | Borrar | `NoSuchFileException` si no existe; carpeta solo vacía |
| `deleteIfExists(p)` | Borrar si existe → `boolean` | |
| `list(carpeta)` | Primer nivel → `Stream<Path>` | **`try ( )`** |
| `walk(carpeta)` | Todos los niveles → `Stream<Path>` | **`try ( )`** |
| `mismatch(a, b)` | `-1` si son iguales byte a byte | |

#### Errores típicos
| Error | Qué pasa |
|---|---|
| `move(destino, origen)` | Al revés: intenta mover lo que no existe |
| Destino = solo la carpeta | Intenta "convertir" el fichero en esa carpeta (que ya existe) |
| Mover un fichero que sigue abierto (Windows) | `FileSystemException`: ciérralo antes |

---

### 3. `File` (la antigua) y recursividad
📝 `ejemplo.java`, `ejercicio.java`

#### Plantilla
```java
File c = new File("carpeta");
boolean creada = c.mkdir();                    // false si ya existía (o cualquier otro fallo)
File destino = new File("C:/DAM/Segundo/AC/ejercicios/carpeta");   // con el nombre final
boolean movida = c.renameTo(destino);
```

#### Recorrer una carpeta entera (recursividad)
```java
static void listar(File carpeta, int nivel) {
    File[] hijos = carpeta.listFiles();        // primer nivel (null si no es carpeta)
    for (File h : hijos) {
        System.out.println("  ".repeat(nivel) + (h.isDirectory() ? "[DIR] " : "[FILE] ") + h.getName());
        if (h.isDirectory()) {
            listar(h, nivel + 1);              // se llama a sí mismo; para cuando no hay subcarpetas
        }
    }
}
```

#### `File` frente a `Files`
| Quiero… | `File` (devuelve `boolean`) | `Files` (lanza excepción) |
|---|---|---|
| Crear una carpeta | `f.mkdir()` | `Files.createDirectory(p)` |
| Crear anidadas | `f.mkdirs()` | `Files.createDirectories(p)` |
| Crear fichero vacío | `f.createNewFile()` | `Files.createFile(p)` |
| Mover / renombrar | `f.renameTo(destino)` | `Files.move(p, destino)` |
| Borrar | `f.delete()` | `Files.delete(p)` |
| ¿Existe? | `f.exists()` | `Files.exists(p)` |
| ¿Carpeta? / ¿fichero? | `f.isDirectory()` / `f.isFile()` | `Files.isDirectory(p)` / `isRegularFile(p)` |
| Tamaño | `f.length()` (**0** si no existe) | `Files.size(p)` (excepción si no existe) |
| Última modificación | `f.lastModified()` (ms desde 1970) | `Files.getLastModifiedTime(p)` |
| Contenido | `f.listFiles()` → `File[]` | `Files.list(p)` → `Stream<Path>` |
| Nombre / ruta completa | `f.getName()` / `f.getAbsolutePath()` | `p.getFileName()` / `p.toAbsolutePath()` |
| Una dentro de otra | `new File(carpeta, "x")` | `carpeta.resolve("x")` |
| Convertir | `f.toPath()` | `p.toFile()` |

#### ⚠ Reglas
- Llamar dos veces a `mkdir()` → la segunda da `false`. Guarda el resultado en una variable.
- `FileWriter` sin `close()` → **no se escribe nada** (se queda en el búfer).

---

### 4. Escribir texto
🧪 `F02_FicherosTexto` · 📝 Laboratorio bloques 1 y 2

#### Plantillas
```java
// Una lista → una línea por elemento
Files.write(archivo, List.of("a=1", "b=2"), StandardOpenOption.CREATE_NEW);

// Un String (NO añade salto de línea)
Files.writeString(log, "Tienda iniciada" + System.lineSeparator(),
        StandardOpenOption.CREATE, StandardOpenOption.APPEND);

// Poco a poco (mucho texto)
try (BufferedWriter bw = Files.newBufferedWriter(archivo, StandardCharsets.UTF_8)) {
    bw.write("línea 1");
    bw.newLine();
}
```

#### Métodos
| Método | Escribe | Ojo |
|---|---|---|
| `Files.write(p, lista, op...)` | Una línea por elemento | Para `List<String>` o `byte[]` |
| `Files.writeString(p, texto, op...)` | Un `String` | **No** pone `\n` |
| `Files.newBufferedWriter(p, cs, op...)` | Devuelve un `BufferedWriter` | `try ( )` |
| `bw.write(t)` / `bw.newLine()` | Texto / salto de línea | |

#### ⚠ Reglas
- `Files.write(ruta, unString)` **no compila**: para un `String` es `writeString`.
- Sin opciones, **vacía** el fichero si ya existía.

---

### 5. Leer texto
🧪 `F02_FicherosTexto` · 📝 BioDAM bloque 3

#### Plantillas
```java
List<String> lineas = Files.readAllLines(p);          // todo, en lista (no hay que cerrar)
String todo = Files.readString(p);                    // todo, en un String

try (BufferedReader br = Files.newBufferedReader(p)) { // línea a línea
    String linea;
    while ((linea = br.readLine()) != null) {          // null = fin
        System.out.println(linea);
    }
}

try (Stream<String> s = Files.lines(p)) {              // como stream (tema 3)
    long activos = s.skip(1).filter(l -> l.contains("ACTIVO")).count();
}
```

#### Métodos
| Método | Devuelve | ¿Hay que cerrar? |
|---|---|---|
| `Files.readAllLines(p)` | `List<String>` | No |
| `Files.readString(p)` | `String` | No |
| `Files.lines(p)` | `Stream<String>` | **Sí: `try ( )`** |
| `Files.newBufferedReader(p)` | `BufferedReader` | **Sí** |
| `br.readLine()` | `String`, o **`null`** al final | |

#### ⚠ Reglas
- Un stream **solo se recorre una vez**. Si necesitas varios cálculos, lee a una `List` (`readAllLines` o `lines(...).toList()`) y haz `lista.stream()` cada vez. Es lo que hiciste en `csvIntoList`.
- `count()` devuelve **`long`**.
- `readAllLines` con un fichero enorme llena la memoria; ahí, `lines` o `BufferedReader`.

---

### 6. Opciones de apertura y de copia

#### `StandardOpenOption` (escribir: `write`, `writeString`, `newBufferedWriter`, `FileChannel.open`)
| Opción | Efecto |
|---|---|
| *(ninguna)* | Crea, o **vacía** si existía (= `CREATE` + `TRUNCATE_EXISTING` + `WRITE`) |
| `CREATE` | Créalo si no existe |
| `CREATE_NEW` | Créalo; si existe, **`FileAlreadyExistsException`** |
| `APPEND` | Escribe **al final** (falla si no existe → con `CREATE`) |
| `TRUNCATE_EXISTING` | Vacíalo antes de escribir |
| `READ` / `WRITE` | Abrir para leer / para escribir (sobre todo en `FileChannel`) |

#### `StandardCopyOption` (`copy`, `move`)
| Opción | Efecto |
|---|---|
| `REPLACE_EXISTING` | Si el destino existe, **lo sobrescribe** |
| `ATOMIC_MOVE` | El movimiento en un solo paso (todo o nada) |
| `COPY_ATTRIBUTES` | Copia también fechas y permisos |

#### ¿Cuál uso?
| Caso | Opciones |
|---|---|
| Configuración que no se debe machacar | `CREATE_NEW` + `catch (FileAlreadyExistsException e)` |
| Log que crece | `CREATE, APPEND` |
| Informe que se regenera | ninguna |
| Mover a procesados aunque ya esté | `move(..., REPLACE_EXISTING)` |

---

### 7. Excepciones

#### Plantilla
```java
try {
    Files.write(archivoConfig, propiedades, StandardOpenOption.CREATE_NEW);
    System.out.println("Creado");
} catch (FileAlreadyExistsException e) {          // la CONCRETA que esperas
    System.out.println("Ya existía: no lo toco");
}
```

| Excepción | Paquete | Cuándo |
|---|---|---|
| `IOException` | `java.io` | La madre de todas. **Checked** |
| `NoSuchFileException` | `java.nio.file` | No existe (con `Files`) |
| `FileNotFoundException` | `java.io` | No existe (con las clases antiguas) |
| `FileAlreadyExistsException` | `java.nio.file` | Ya existe |
| `DirectoryNotEmptyException` | `java.nio.file` | Borrar carpeta con contenido |
| `AccessDeniedException` | `java.nio.file` | Sin permiso |
| `EOFException` | `java.io` | Fin de un binario con `Data*Stream` / `ObjectInputStream` |
| `NotSerializableException` | `java.io` | Clase sin `Serializable` |
| `InvalidClassException` | `java.io` | `serialVersionUID` distinto |
| `InvalidPathException` | `java.nio.file` | Caracteres no válidos en la ruta (`:` en Windows) — **unchecked** |
| `SecurityException` | `java.lang` | La lanzas tú en el Path Traversal — **unchecked** |

#### ⚠ Reglas
- `catch` de la más **concreta** a la más **general**: si pones `IOException` primero, las de debajo no compilan.
- `throw` **lanza ahora**; `throws` en la firma **avisa** de que puede lanzarse.
- Pon siempre un **mensaje**: `new SecurityException("Ruta fuera de datos/: " + ruta)`.

---

### 8. ⭐ Plantilla: validar una ruta (Path Traversal)
📝 Laboratorio TODO 2.3 · BioDAM `validatePath`

```java
Path base      = Paths.get("datos").toAbsolutePath().normalize();      // 1. absoluta
Path candidata = Paths.get(rutaUsuario).toAbsolutePath().normalize();  // 2. luego normalize, EN LAS DOS

if (!candidata.startsWith(base)) {                                     // 3. Path.startsWith: carpeta a carpeta
    throw new SecurityException("Intento de path traversal: " + rutaUsuario);
}
if (!Files.exists(candidata)) {
    // ruta segura pero el fichero no existe
}
```

| Ruta del usuario | Resultado |
|---|---|
| `datos/diagnosticos_2026.csv` | ✅ dentro |
| `datos/../../etc/passwd` | ❌ se sale |
| `datos/essd/ppp` | ✅ dentro (aunque no exista) |
| `datos2/secreto.txt` | ❌ con `Path.startsWith`; ✅ (¡mal!) con `String.startsWith` |

#### ⚠ Reglas
- Primero `toAbsolutePath()` y **luego** `normalize()`.
- La **base** es la carpeta permitida, no el log ni el fichero.

---

### 9. ⭐ Plantilla: log con fecha (BioDAM)

```java
DateTimeFormatter paraNombre = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");          // sin / ni :
Path carpetaLogs = Paths.get("salida", "sistema_logs");
Files.createDirectories(carpetaLogs);
Path archivoLog = carpetaLogs.resolve("auditoria_" + LocalDateTime.now().format(paraNombre) + ".log");

static void writeLog(String nivel, String mensaje) throws IOException {
    DateTimeFormatter paraLinea = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
    String linea = "[" + LocalDateTime.now().format(paraLinea) + "] " + nivel + " " + mensaje
            + System.lineSeparator();
    Files.writeString(archivoLog, linea, StandardOpenOption.CREATE, StandardOpenOption.APPEND);
}
```

#### ⚠ Reglas
- Para nombres de fichero: **solo dígitos** (`/` crea carpetas y `:` no se permite en Windows).
- `MM` = mes · `mm` = minutos · `HH` = 0–23 · `hh` = 1–12 · `SSS` = milisegundos.
- `now()` **dentro** del método que lo usa (si no, todas las líneas tienen la misma hora).
- El molde se **aplica**: `fecha.format(molde)`, no `"..." + molde`.

---

### 10. CSV ↔ objetos
🧪 `F03_CSVaObjetos`

#### Plantilla
```java
record Producto(String codigo, String nombre, double precio, int stock) { }

// GUARDAR: objeto → línea
List<String> lineas = new ArrayList<>();
lineas.add("codigo;nombre;precio;stock");                                  // cabecera
for (Producto p : productos) {
    lineas.add(String.join(";", p.codigo(), p.nombre(),
            String.format(Locale.ROOT, "%.2f", p.precio()),                // punto decimal
            String.valueOf(p.stock())));
}
Files.write(fichero, lineas);

// CARGAR: línea → objeto
try (Stream<String> s = Files.lines(fichero)) {
    List<Producto> cargados = s.skip(1)                                    // fuera la cabecera
            .filter(l -> !l.isBlank())
            .map(l -> {
                String[] c = l.split(";");
                return new Producto(c[0], c[1], Double.parseDouble(c[2]), Integer.parseInt(c[3]));
            })
            .toList();
}
```

#### ⚠ Reglas
- `split` devuelve `String[]`: los trozos **siguen siendo texto** → `parseInt` / `parseDouble`.
- `Double.parseDouble("25,90")` → `NumberFormatException`: usa punto (`Locale.ROOT`).
- Separador `;` si los textos pueden llevar comas.
- `equals` distingue mayúsculas: copia el texto **exacto** de los datos (`"Diabetes"`, `"Informática"` con tilde).

---

### 11. Binario: `DataOutputStream` / `DataInputStream`
🧪 `F04_FicherosBinarios`

#### Plantilla
```java
try (DataOutputStream out = new DataOutputStream(
        new BufferedOutputStream(Files.newOutputStream(fichero)))) {
    out.writeUTF("P001");          // 2 + 4 bytes
    out.writeDouble(25.90);        // 8 bytes
    out.writeInt(12);              // 4 bytes
}

try (DataInputStream in = new DataInputStream(
        new BufferedInputStream(Files.newInputStream(fichero)))) {
    while (true) {
        String codigo = in.readUTF();      // MISMO orden y MISMOS tipos
        double precio = in.readDouble();
        int stock = in.readInt();
    }
} catch (EOFException fin) {
    // fin del fichero: es lo normal
}
```

#### Tamaños
| Tipo | Bytes | Escribir | Leer |
|---|---|---|---|
| `byte` / `boolean` | 1 | `writeByte` / `writeBoolean` | `readByte` / `readBoolean` |
| `short` / `char` | 2 | `writeShort` / `writeChar` | `readShort` / `readChar` |
| `int` / `float` | 4 | `writeInt` / `writeFloat` | `readInt` / `readFloat` |
| `long` / `double` | 8 | `writeLong` / `writeDouble` | `readLong` / `readDouble` |
| `String` | **2 + n** | `writeUTF` | `readUTF` |

#### Tres formas de guardar un texto en binario
| Forma | En el fichero | Leer |
|---|---|---|
| `writeUTF(t)` | 2 bytes de longitud + texto | `readUTF()` |
| `writeInt(b.length)` + `write(b)` | 4 bytes de longitud + texto | `readInt()` → `byte[n]` → `readFully` → `new String(b, UTF_8)` |
| Tamaño fijo (relleno de espacios) | siempre N bytes | leer N y `trim()` |

#### ⚠ Reglas
- El fichero **no sabe** qué hay: leer `int` donde había `long` no da error, da basura, y lo de detrás también.
- Final = **`EOFException`** (no `null`, no `-1`). Con `InputStream.read(bufer)`, en cambio, el final es **`-1`**.
- `readShort`/`readByte` tienen **signo** → `readUnsignedShort`/`readUnsignedByte`.
- Abrir un `OutputStream` **vacía** el fichero.

---

### 12. Serialización
🧪 `F05_SerializacionObjetos`

#### Plantilla
```java
record Incidencia(int id, String usuario) implements Serializable {
    @Serial private static final long serialVersionUID = 1L;
}

try (ObjectOutputStream out = new ObjectOutputStream(Files.newOutputStream(fichero))) {
    out.writeObject(lista);                                   // la lista entera de golpe
}

try (ObjectInputStream in = new ObjectInputStream(Files.newInputStream(fichero))) {
    List<Incidencia> leidas = (List<Incidencia>) in.readObject();   // devuelve Object → CAST
}
```

#### ⚠ Reglas
| Concepto | Qué hace |
|---|---|
| `implements Serializable` | Interfaz **marca** (sin métodos). Sin ella: `NotSerializableException` |
| Atributos | También tienen que ser serializables (`String`, `int`, `List`… lo son) |
| `serialVersionUID` | Versión de la clase. Si no coincide: `InvalidClassException` |
| `transient` | No se guarda. Al leer: `null`, `0`, `false` |
| `readObject()` | Lanza también `ClassNotFoundException` |

#### CSV frente a serialización
| CSV | Serialización |
|---|---|
| Texto legible, lo abre Excel | Binario, solo Java |
| Conviertes cada campo a mano | El objeto entero de golpe |
| Sirve entre lenguajes | Solo Java y la misma versión de la clase |

---

### 13. Acceso aleatorio: `RandomAccessFile`
🧪 `F06_AccesoAleatorio`

#### Plantilla
```java
static final int LONG_NOMBRE = 20;                              // caracteres
static final int TAM = 4 + LONG_NOMBRE * 2 + 8 + 4;             // id + nombre (writeChars: 2 bytes/char) + precio + stock = 56

try (RandomAccessFile raf = new RandomAccessFile(fichero.toFile(), "rw")) {
    raf.seek((long) n * TAM);                                   // ir al registro n
    raf.writeInt(id);
    raf.writeChars(String.format("%-20.20s", nombre));          // relleno a 20
    raf.writeDouble(precio);
    raf.writeInt(stock);

    raf.seek((long) n * TAM + 4 + LONG_NOMBRE * 2 + 8);         // ir SOLO al campo stock
    raf.writeInt(50);                                           // sobrescribe 4 bytes
}
```

#### Métodos
| Método | Qué hace |
|---|---|
| `new RandomAccessFile(f, "r" / "rw")` | Abrir para leer / leer y escribir |
| `seek(pos)` | Mueve el cursor al byte `pos` (el primero es 0) |
| `getFilePointer()` | Dónde está el cursor |
| `length()` | Tamaño → nº de registros = `length() / TAM` |
| `setLength(n)` | Corta o alarga |
| `readXxx` / `writeXxx` | Los mismos que `Data*Stream` |

#### ⚠ Reglas
- **Registros de tamaño fijo**: posición = **n × TAM** (n empieza en 0).
- Escribir **sobrescribe**, no inserta. Solo es seguro cambiar un campo por otro **del mismo tamaño**.
- Pide `String` o `File`: si tienes un `Path`, `ruta.toFile()`.

---

### 14. NIO: `ByteBuffer` + `FileChannel`
📝 Laboratorio bloque 3 · `MapaBinario`

#### Plantilla: escribir un registro de tamaño fijo
```java
static final int BYTES_REGISTRO = Integer.BYTES + 40;          // id (4) + nombre (40)

try (FileChannel canal = FileChannel.open(archivo,
        StandardOpenOption.CREATE, StandardOpenOption.WRITE)) {
    ByteBuffer buffer = ByteBuffer.allocate(BYTES_REGISTRO);   // 1. caja vacía
    buffer.putInt(101);                                        // 2. llenar
    byte[] nombre = Arrays.copyOf("SSD 1TB".getBytes(StandardCharsets.UTF_8), 40);  // relleno a 40
    buffer.put(nombre);
    buffer.flip();                                             // 3. modo llenar → modo vaciar
    while (buffer.hasRemaining()) {                            // 4. write puede no mandarlo todo
        canal.write(buffer);
    }
}
```

#### Plantilla: modificar en una posición
```java
try (FileChannel canal = FileChannel.open(archivo, StandardOpenOption.WRITE)) {
    ByteBuffer nuevo = ByteBuffer.wrap(Arrays.copyOf("NVMe 2TB".getBytes(StandardCharsets.UTF_8), 40));
    canal.write(nuevo, (long) n * BYTES_REGISTRO + Integer.BYTES);   // salta id, pisa el nombre
}
```

#### Plantilla: leer un binario pequeño (sin canal)
```java
ByteBuffer bb = ByteBuffer.wrap(Files.readAllBytes(ruta));     // caja llena, sin flip
int flag = bb.getInt();
int marca = Short.toUnsignedInt(bb.getShort());                // sin signo: 0xCAFE
System.out.println(Integer.toHexString(marca));
```

#### `ByteBuffer`
| Método | Qué hace |
|---|---|
| `allocate(n)` | Caja vacía de `n` bytes (para llenar) |
| `wrap(byte[])` | Caja llena con ese array (para leer) |
| `putInt` / `putLong` / `putShort((short) x)` / `put(byte[])` | Meter (avanza `position`) |
| `getInt` / `getLong` / `getShort` / `get(byte[])` | Sacar (avanza `position`) |
| `getInt(i)` / `putInt(i, x)` | En la posición `i`, **sin** mover el cursor |
| **`flip()`** | `limit = position`, `position = 0`: llenar → vaciar |
| `rewind()` | `position = 0` |
| `clear()` | Lista para llenar otra vez |
| `hasRemaining()` / `remaining()` | ¿Quedan bytes entre `position` y `limit`? |
| `position()` / `limit()` / `capacity()` | Las tres marcas |

#### `FileChannel`
| Método | Qué hace |
|---|---|
| `FileChannel.open(p, op...)` | Abrir (`READ`, `WRITE`, `CREATE`…) |
| `read(buf)` | Llena la caja; devuelve bytes leídos o **`-1`** al final |
| `write(buf)` | Escribe de `position` a `limit` |
| `read(buf, pos)` / `write(buf, pos)` | En esa posición del fichero |
| `position(n)` / `size()` / `truncate(n)` | Mover / tamaño / cortar |

#### ⚠ Reglas
- **Sin `flip()` no se escribe nada** (o se lee basura). `BufferUnderflowException` / `BufferOverflowException` = olvidaste el `flip` o la caja es pequeña.
- `ByteBuffer` **no tiene `getUTF`**: longitud → `byte[]` → `new String(b, UTF_8)`.
- Si el texto nuevo es más corto y no truncas, quedan bytes viejos al final.

---

### 15. Excel con Apache POI
📝 Laboratorio bloque 4 · Dependencia: `org.apache.poi:poi-ooxml`

#### Plantilla
```java
try (XSSFWorkbook libro = new XSSFWorkbook();
     OutputStream out = Files.newOutputStream(rutaExcel)) {
    Sheet hoja = libro.createSheet("Inventario");

    CellStyle negrita = libro.createCellStyle();
    Font f = libro.createFont();
    f.setBold(true);
    negrita.setFont(f);

    Row cabecera = hoja.createRow(0);                     // fila 1 de Excel
    Cell c0 = cabecera.createCell(0);                     // columna A
    c0.setCellValue("Producto");
    c0.setCellStyle(negrita);

    Row fila = hoja.createRow(1);
    fila.createCell(0).setCellValue("SSD 1TB");
    fila.createCell(1).setCellValue(89.90);               // número, no texto
    fila.createCell(2).setCellValue(3);
    fila.createCell(3).setCellFormula("B2*C2");           // SIN el =

    hoja.autoSizeColumn(0);
    libro.write(out);                                     // guardar
}
```

#### ⚠ Reglas
- Todo **desde 0**: `createRow(0)` es la fila 1 y `createCell(0)` es la columna A.
- Fórmula **sin `=`**. Los números como `double`, no como `String`.

---

### 16. ⭐ Cálculos con binarios: tamaños, posiciones y hexadecimal
📝 `MapaBinario`, Laboratorio bloque 3, `F06`

#### 1. Tamaño de un registro = sumar sus campos
| Tipo | Bytes |
|---|---|
| `byte`, `boolean` | 1 |
| `short`, `char` | 2 |
| `int`, `float` | 4 |
| `long`, `double` | 8 |

#### 2. ¿Y si hay un `String`? Depende de cómo se escribió
| Forma | Cuenta | ¿Tamaño fijo? |
|---|---|---|
| Relleno a N caracteres con `writeChars` (`F06`) | **N × 2** (cada `char` = 2) → 20 caracteres = 40 bytes | ✅ sí |
| Relleno a N bytes (Laboratorio: nombre de 40 bytes) | **N** (lo que diga el enunciado) | ✅ sí |
| `writeUTF` | **2** (la longitud) **+ los bytes del texto** → `"Hola"` = 2 + 4 = 6 | ❌ no: cada texto mide distinto |

Para el acceso aleatorio los textos **se rellenan hasta un tamaño fijo**. Con `writeUTF` cada registro mide distinto, la fórmula `n × tamaño` ya no sirve y hay que leer en orden desde el principio.

#### 3. Las fórmulas
| Quiero… | Cálculo |
|---|---|
| Dónde empieza el registro n | **n × tamaño del registro** (el primero es el registro 0) |
| Dónde empieza un campo del registro n | **(n × tamaño) + lo que ocupan los campos de delante** |
| Último byte del registro n | inicio + tamaño − 1 (se cuenta desde 0) |
| Tamaño del `ByteBuffer` | lo que mide lo que vas a leer o escribir (el registro o solo un campo) |
| Cuántos registros hay | `Files.size(f) / tamaño` · `raf.length() / tamaño` |

> 💬 *"Primero salto al registro (n × tamaño) y luego sumo lo que ocupan los campos de delante."* Como en el cine: primero la fila y luego las butacas de delante.

#### 4. Hexadecimal → decimal
Cada posición hacia la izquierda vale **×16** (en decimal, ×10):
```
posiciones:   ... 4096   256   16   1
0x1E   = 1×16 + 14                       = 30
0xFF   = 15×16 + 15                      = 255   (el máximo de un byte)
0xCAFE = 12×4096 + 10×256 + 15×16 + 14   = 51966
```
- A = 10, B = 11, C = 12, D = 13, **E = 14**, F = 15. Y `0x10` = 16.
- Los ceros de delante no suman: `00 00 00 0A` = `0x0A` = 10.
- Cada byte son **2 cifras hex**; un `int` se ve como 4 parejas.
- El hex es solo **cómo se muestran** los bytes: en el disco hay bits. `println` muestra decimal; `Integer.toHexString(n)` muestra hex; `0x64` en el código es lo mismo que `100`.
- El tamaño depende del **tipo**, no del valor: el 100 ocupa 1, 2, 4 u 8 bytes según lo escribas con `writeByte`, `writeShort`, `writeInt` o `writeLong`. Si el número no cabe en el tipo, se corta sin avisar (`short` llega hasta 32 767).

#### 5. Ejemplo resuelto: `inventario.dat`
Estructura de cada registro: **código** (`int`), **precio** (`double`), **stock** (`short`).
```
00 00 00 0A │ 40 39 00 00 00 00 00 00 │ 00 1E ║ 00 00 00 FF │ 40 24 00 00 00 00 00 00 │ 00 05
  código    │        precio           │ stock ║   código    │         precio          │ stock
└──────────────── registro 0 ───────────────┘ ║ └──────────────── registro 1 ──────────────────┘
```
| Pregunta | Respuesta |
|---|---|
| Tamaño de un registro | 4 + 8 + 2 = **14** |
| Inicio del registro 1 / registro 7 | 1 × 14 = **14** / 7 × 14 = **98** |
| Código del registro 0 | `0x0A` = **10** |
| Stock del registro 0 | `0x1E` = 1×16 + 14 = **30** |
| Código del registro 1 | `0xFF` = **255** |
| Byte del stock del registro 1 | 1 × 14 + 4 + 8 = **26** → `canal.position(26)`, `ByteBuffer.allocate(2)` |
| Precio del registro 1 | 1 × 14 + 4 = **18** |

```java
ByteBuffer b = ByteBuffer.allocate(2);       // cambiar el stock del registro 1 a 50
b.putShort((short) 50);
b.flip();
canal.write(b, 26);
```

#### 6. Las trampas
- **Leer con otro orden o tipo no da error.** Leer el registro 0 con `readInt()`, `readShort()`, `readDouble()` da 10 ✅, 16441 ❌ y basura ❌: en cuanto un tipo no coincide, se descoloca todo lo de detrás.
- **El orden de los campos no está en el fichero:** lo sabes porque te lo da el enunciado o porque lo escribiste tú (el orden de tus `write`). Se lee en el mismo orden (FIFO, secuencial).
- **El signo:** `readShort()` / `getShort()` de `0xCAFE` sale **negativo** → `Short.toUnsignedInt(...)` o `readUnsignedShort()`.
- **Faltan bytes:** si pides más bytes de los que quedan → `EOFException` (`Data*Stream`) o `BufferUnderflowException` (`ByteBuffer`).

---

### 17. Imports
```java
import java.nio.file.*;                    // Path, Paths, Files, StandardOpenOption, StandardCopyOption,
                                           // NoSuchFileException, FileAlreadyExistsException...
import java.io.*;                          // File, IOException, EOFException, BufferedReader/Writer,
                                           // Data*Stream, Object*Stream, RandomAccessFile, Serializable
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.stream.Stream;
```
> ⚠ Al autocompletar, **mira el paquete**: `java.util.List`, no `javax.swing.JList` (te pasó en BioDAM).

---

# PARTE 3: PREGUNTAS CON RESPUESTA

---

### Bloque 1: Ficheros, texto y binario

#### 1. ¿Por qué hace falta guardar datos en ficheros?
**Respuesta:**

Porque las variables viven en la **RAM**, que **se borra** al terminar el programa. El **disco** conserva los datos aunque cierres el programa o apagues el ordenador.


#### 2. ⭐ ¿Qué diferencia hay entre un fichero de texto y uno binario?
**Respuesta:**

Los dos son bytes. En el de **texto**, los bytes representan **letras** y se lee con el Bloc de notas (`.txt`, `.csv`, `.log`). En el **binario** son **datos en bruto** (números, objetos, imágenes) y en el Bloc de notas salen símbolos raros (`.dat`, `.bin`, `.ser`).


#### 3. Si el binario no se puede leer, ¿para qué sirve?
**Respuesta:**

**Ocupa menos y es más rápido.** Un `int` ocupa siempre 4 bytes; el mismo número como texto puede ocupar muchos más (`2000000` = 7 caracteres).


---

### Bloque 2: Rutas, `File` y `Files`

#### 4. ⭐ ¿Crear un objeto `Path` crea el fichero en el disco?
**Respuesta:**

**No.** Un `Path` es solo la **dirección**. No toca el disco y no falla aunque el fichero no exista. El disco se toca cuando usas `Files` (`createDirectories`, `write`, `move`…). *"Escribir la dirección en el sobre no construye la casa."*


#### 5. ⭐ ¿Qué es una ruta relativa y con qué se completa?
**Respuesta:**

Una dirección incompleta (`"datos/mapa.dat"`). Java le pone delante el **directorio de trabajo**, la carpeta desde la que se ejecuta el programa (en tu proyecto, `C:\DAM\Segundo\AC`). Si no encuentra un fichero, imprime `toAbsolutePath()`.


#### 6. ⭐ ¿Qué diferencia principal hay entre `File` y `Files`?
**Respuesta:**

`File` (antigua, `java.io`) tiene las acciones en el propio objeto y, si algo falla, **devuelve `false` sin decir por qué**. `Files` (moderna, `java.nio.file`) tiene métodos **estáticos** que **lanzan una excepción concreta** (`NoSuchFileException`, `FileAlreadyExistsException`…).


#### 7. ¿Qué devuelve `Paths.get("salida").resolve("app.log")`? ¿Y si no lo guardas en una variable?
**Respuesta:**

Devuelve la ruta `salida/app.log`. `Path` es **inmutable**: `resolve` no cambia la ruta original, **devuelve otra**. Si no la guardas, no ha pasado nada. Te pasó tres veces en BioDAM.


#### 8. ¿Qué diferencia hay entre `createDirectory` y `createDirectories`?
**Respuesta:**

`createDirectory` crea **una** carpeta y falla si no existe la carpeta padre o si ya existe. `createDirectories` crea **toda la cadena** que falte y **no falla si ya existe**: es la forma segura.


#### 9. ⭐ Quiero meter `carpeta` dentro de `ejercicios`. ¿Qué destino le paso a `move` o `renameTo`?
**Respuesta:**

La dirección **final completa, con el nombre**: `.../ejercicios/carpeta`, no `.../ejercicios`. Además, `ejercicios` **tiene que existir**: mover no construye el camino. Y el orden es `move(DESDE, HASTA)`.


#### 10. ¿Por qué mover un fichero dentro del mismo disco es instantáneo aunque pese mucho?
**Respuesta:**

Porque no se copian los datos: solo se **cambia la dirección** en el índice del disco. Por eso mover y renombrar son la misma operación.


#### 11. ¿Se puede borrar una carpeta con ficheros dentro?
**Respuesta:**

No directamente: solo se borra **vacía**. Con `Files.delete` salta `DirectoryNotEmptyException`; con `File.delete()` devuelve `false`. Hay que borrar antes el contenido.


#### 12. ⭐ `listFiles()` o `Files.list()` solo dan el primer nivel. ¿Cómo listo todas las subcarpetas?
**Respuesta:**

Con **recursividad**: recorro el contenido y, si un elemento es carpeta, **llamo al mismo método** con ella (`Listar(archivo, nivel + 1)`). Termina sola cuando ya no hay subcarpetas (**caso base**). Otra opción: `Files.walk(carpeta)`.


---

### Bloque 3: Leer, escribir y opciones

#### 13. ⭐ ¿Por qué hay que cerrar un fichero después de escribir?
**Respuesta:**

Porque Java guarda lo que escribes en un **búfer** y lo manda al disco por bloques. **Al cerrar se vacía el búfer.** Si no cierras, lo último puede no llegar al disco (en `ejemplo.java`: "sin el close no se escribe nada"). Además, un fichero abierto queda **bloqueado** en Windows.


#### 14. ⭐ ¿Qué es try-with-resources y por qué se usa?
**Respuesta:**

Es abrir el recurso **dentro de los paréntesis del `try`**: `try (Stream<String> s = Files.lines(p)) { ... }`. Java lo **cierra solo** al salir del bloque, vaya bien o salte una excepción.


#### 15. ¿Cuáles de estos dejan el fichero abierto y necesitan `try ( )`: `readAllLines`, `lines`, `list`, `writeString`?
**Respuesta:**

**`Files.lines` y `Files.list`**, porque devuelven un `Stream` que lee poco a poco. `readAllLines` y `writeString` abren y cierran ellos solos.


#### 16. ¿Cómo sé que he llegado al final con `BufferedReader.readLine()`?
**Respuesta:**

Devuelve **`null`**: `while ((linea = br.readLine()) != null)`.


#### 17. ⭐ Escribo en un fichero que ya existe sin pasar opciones. ¿Qué pasa?
**Respuesta:**

**Lo vacía y escribe desde cero** (equivale a `CREATE` + `TRUNCATE_EXISTING` + `WRITE`). Lo que había se pierde.


#### 18. ⭐ ¿Qué hace `CREATE_NEW` y para qué se usa?
**Respuesta:**

Crea el fichero, pero si **ya existe se niega** y lanza `FileAlreadyExistsException`. Se usa para la **configuración**: no machacar lo que el usuario cambió. Laboratorio, bloque 1.


#### 19. ⭐ ¿Por qué `CREATE_NEW` es mejor que `if (!Files.exists(p)) Files.write(p, ...)`?
**Respuesta:**

Porque entre **comprobar** y **escribir** hay un hueco en el que otro programa puede crear el fichero. `CREATE_NEW` lo hace **en un solo paso (atómico)**. Es la trampa de "comprobar y luego actuar" de PSP.


#### 20. ⭐ ¿Qué opciones uso para un log? ¿Por qué `APPEND` solo no basta?
**Respuesta:**

`CREATE, APPEND`. `APPEND` escribe al final sin borrar, pero **falla si el fichero no existe**; `CREATE` lo crea la primera vez. Con `CREATE_NEW` el log reventaría en la segunda línea.


#### 21. Las líneas del log salen pegadas con `writeString`. ¿Por qué? ¿Y por qué no compila `Files.write(ruta, "texto")`?
**Respuesta:**

`writeString` **no añade el salto de línea**: hay que sumar `System.lineSeparator()`. `Files.write` es para una **lista** de líneas o un `byte[]`; para un `String` se usa `writeString`.


---

### Bloque 4: Excepciones y seguridad

#### 22. ⭐ ¿De qué excepción heredan las de ficheros? ¿Qué significa que sea "checked"?
**Respuesta:**

De **`IOException`**. *Checked* significa que Java **obliga** a tratarla: o la atrapas con `try/catch` o la declaras con `throws IOException`.


#### 23. ¿Por qué atrapar `FileAlreadyExistsException` y no `IOException`?
**Respuesta:**

Porque si pasa **otra cosa** distinta (falta de permisos, disco lleno), no la confundes con la que esperabas. Además, en una cadena de `catch` las concretas van **antes** que las generales.


#### 24. Une cada excepción con su causa: `NoSuchFileException`, `DirectoryNotEmptyException`, `AccessDeniedException`, `EOFException`.
**Respuesta:**

- `NoSuchFileException`: el fichero o la carpeta no existe.
- `DirectoryNotEmptyException`: borrar una carpeta con contenido.
- `AccessDeniedException`: no hay permiso.
- `EOFException`: se ha llegado al final de un binario leído con `DataInputStream`.


#### 25. ⭐ ¿Qué es el Path Traversal?
**Respuesta:**

Un ataque en el que el usuario mete **`..`** en una ruta para **salirse** de la carpeta permitida: `datos/../../etc/passwd` empieza por `datos` pero acaba fuera.


#### 26. ⭐ ¿Cómo se evita? ¿En qué orden van `toAbsolutePath` y `normalize`?
**Respuesta:**

1. Pasar **las dos** rutas (base y candidata) a `toAbsolutePath()` y **luego** `normalize()` (que resuelve los `..`).
2. Comprobar `candidata.startsWith(base)` con el **`startsWith` de `Path`**.
Si no empieza por la base: `SecurityException` (y un `WARN` en el log, en BioDAM).


#### 27. ⭐ ¿Por qué no vale `String.startsWith`?
**Respuesta:**

Porque compara **letra a letra**: `"salida2/secreto.txt"` empieza por `"salida"` y se colaría. `Path.startsWith` compara **carpeta a carpeta**, y `salida2` no es `salida`.


---

### Bloque 5: CSV, binario y serialización

#### 28. ¿Cómo se guarda y se carga un objeto en CSV?
**Respuesta:**

**Guardar:** juntar los atributos con el separador (`String.join(";", ...)`), una línea por objeto, con una cabecera arriba. **Cargar:** `skip(1)` para la cabecera, `split(";")` y convertir cada trozo (`Integer.parseInt`, `Double.parseDouble`).


#### 29. `Double.parseDouble("25,90")` falla. ¿Por qué?
**Respuesta:**

Java espera **punto** decimal. Lanza `NumberFormatException`. Al escribir: `String.format(Locale.ROOT, "%.2f", precio)`.


#### 30. ⭐ ¿Cuál es la regla de oro de `DataInputStream`?
**Respuesta:**

Leer en el **mismo orden y con los mismos tipos** con que se escribió. El fichero no tiene etiquetas: si te equivocas, **no hay error**, solo números sin sentido, y todo lo de detrás también sale mal.


#### 31. ⭐ ¿Cómo sé que he llegado al final de un fichero con `DataInputStream`?
**Respuesta:**

Lanza **`EOFException`** (no devuelve `null` ni `-1`). Se lee en un `while (true)` y se captura `EOFException`.


#### 32. ¿Cuánto ocupa en binario `writeInt(5)`, `writeDouble(2.5)`, `writeLong(1)` y `writeUTF("Hola")`?
**Respuesta:**

4, 8, 8 y **6** bytes (`writeUTF` = 2 bytes de longitud + 4 del texto). Total: 26.


#### 33. ⭐ ¿Qué necesita una clase para poder serializarse?
**Respuesta:**

`implements Serializable` (interfaz **marca**, sin métodos), y que sus atributos también sean serializables. Si no: `NotSerializableException`.


#### 34. ⭐ ¿Qué es `serialVersionUID`?
**Respuesta:**

El **número de versión** de la clase. Si cambias la clase y ya no coincide con el del fichero, al leer salta `InvalidClassException`.


#### 35. ⭐ ¿Qué hace `transient`? ¿Qué vale ese atributo al leer?
**Respuesta:**

Ese atributo **no se guarda** (contraseñas, datos calculados). Al leer vale su **valor por defecto**: `null`, `0` o `false`.


#### 36. ¿Por qué `readObject()` necesita un cast? ¿Cuándo elijo CSV y cuándo serialización?
**Respuesta:**

Porque devuelve `Object`: `(List<Incidencia>) in.readObject()`. **CSV** si lo tiene que leer una persona, Excel u otro lenguaje. **Serialización** para guardar y recuperar objetos Java cómodamente (solo lo entiende Java, con la misma versión de la clase).


---

### Bloque 6: Acceso aleatorio, NIO y Excel

#### 37. ⭐ ¿Qué diferencia hay entre acceso secuencial y aleatorio?
**Respuesta:**

**Secuencial:** se lee desde el principio y en orden; para llegar al registro 1000 hay que pasar por los 999 anteriores (como una cinta). **Aleatorio (directo):** saltas a cualquier posición (como un array) con `seek`.


#### 38. ⭐ ¿Qué condición necesita el acceso aleatorio? ¿Cómo se calcula la posición del registro n?
**Respuesta:**

**Registros de tamaño fijo.** Posición = **n × tamaño del registro** (n empieza en 0). Los textos se rellenan con espacios hasta un tamaño fijo.


#### 39. Un registro tiene `int id`, nombre de 20 caracteres con `writeChars` y `double precio`. ¿Cuánto ocupa? ¿Dónde empieza el registro 3?
**Respuesta:**

4 + 20×2 + 8 = **52 bytes**. El registro 3 empieza en 3 × 52 = **156**.


#### 40. ¿Qué modos tiene `RandomAccessFile`? ¿Escribir en una posición inserta o sobrescribe?
**Respuesta:**

`"r"` (solo leer) y `"rw"` (leer y escribir). Escribir **sobrescribe** los bytes que había; por eso solo es seguro cambiar un campo por otro del mismo tamaño.


#### 41. ⭐ ¿Qué papel tienen `FileChannel` y `ByteBuffer`?
**Respuesta:**

`FileChannel` es la **conexión** (la tubería) con el fichero, en la posición que elijas. `ByteBuffer` es una **caja de bytes** en memoria (el cubo): se llena, se manda por el canal o se vacía después de leer.


#### 42. ⭐ ¿Para qué sirve `flip()`? ¿Qué pasa si se te olvida?
**Respuesta:**

Pasa la caja de **"modo llenar" a "modo vaciar"**: `limit = position` y `position = 0`. Sin él, el cursor está al final y **no se escribe nada** (o al leer salta `BufferUnderflowException`).


#### 43. ¿Por qué `canal.write(buffer)` va dentro de `while (buffer.hasRemaining())`? ¿Y por qué `MapaBinario` no necesita `flip()`?
**Respuesta:**

Porque `write` **no garantiza** mandar todos los bytes de una vez. `MapaBinario` usa `ByteBuffer.wrap(Files.readAllBytes(ruta))`: la caja nace **llena y lista para leer** desde la posición 0.


#### 44. ⭐ ¿Qué es Apache POI y cómo se organiza un Excel con él?
**Respuesta:**

Una **librería externa** para crear ficheros de Office (`org.apache.poi:poi-ooxml`). Estructura: `XSSFWorkbook` (libro) → `Sheet` (hoja) → `Row` (fila) → `Cell` (celda), **numeradas desde 0**. Las fórmulas van **sin `=`** (`B2*C2`) y los números se guardan como número.
