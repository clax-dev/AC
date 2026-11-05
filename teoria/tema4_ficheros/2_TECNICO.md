# Tema 4: Ficheros, la parte técnica (para consultar)

> **Este archivo no se lee de un tirón: se consulta.** Cuando estés programando y no recuerdes cómo se escribe algo, búscalo aquí.
> Cada herramienta tiene siempre las mismas partes: **plantilla** (cópiala), **métodos**, **⚠ reglas** y **errores típicos**.
> Si no entiendes **para qué sirve** algo, vuelve a `1_HISTORIA.md`. Si buscas un método suelto, `../GUIA_LIBRERIAS.md` (apartados 7 y 8).

**Índice:**
1. [`Path`: la dirección](#1-path-la-dirección)
2. [`Files`: crear, mover, copiar, borrar, listar](#2-files-crear-mover-copiar-borrar-listar)
3. [`File` (la antigua) y recursividad](#3-file-la-antigua-y-recursividad)
4. [Escribir texto](#4-escribir-texto)
5. [Leer texto](#5-leer-texto)
6. [Opciones de apertura y de copia](#6-opciones-de-apertura-y-de-copia)
7. [Excepciones](#7-excepciones)
8. [⭐ Plantilla: validar una ruta (Path Traversal)](#8--plantilla-validar-una-ruta-path-traversal)
9. [⭐ Plantilla: log con fecha (BioDAM)](#9--plantilla-log-con-fecha-biodam)
10. [CSV ↔ objetos](#10-csv--objetos)
11. [Binario: `DataOutputStream` / `DataInputStream`](#11-binario-dataoutputstream--datainputstream)
12. [Serialización](#12-serialización)
13. [Acceso aleatorio: `RandomAccessFile`](#13-acceso-aleatorio-randomaccessfile)
14. [NIO: `ByteBuffer` + `FileChannel`](#14-nio-bytebuffer--filechannel)
15. [Excel con Apache POI](#15-excel-con-apache-poi)
16. [⭐ Cálculos con binarios: tamaños, posiciones y hexadecimal](#16--cálculos-con-binarios-tamaños-posiciones-y-hexadecimal)
17. [Imports](#17-imports)

---

## 1. `Path`: la dirección
🧪 `F01_RutasYDirectorios`

### Plantilla
```java
Path carpeta = Paths.get("salida", "config");      // salida/config   (Path.of hace lo mismo)
Path fichero = carpeta.resolve("app.properties");   // salida/config/app.properties
System.out.println(fichero.toAbsolutePath());       // C:\DAM\Segundo\AC\salida\config\app.properties
```

### Métodos (ninguno toca el disco)
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

### ⚠ Reglas
- `Path` es **inmutable**: `resolve`, `normalize` y `toAbsolutePath` **devuelven otra ruta**. Guárdala: `Path nueva = p.resolve(x);`.
- `carpeta.resolve(rutaAbsoluta)` **ignora la carpeta** y devuelve la absoluta. Para meter un fichero en otra carpeta: `carpeta.resolve(ruta.getFileName())`.
- Las rutas relativas se completan con el **directorio de trabajo** (`C:\DAM\Segundo\AC`).

---

## 2. `Files`: crear, mover, copiar, borrar, listar
🧪 `F01_RutasYDirectorios` · 📝 BioDAM bloque 4

### Plantilla
```java
Files.createDirectories(carpeta);                                   // crea la cadena; no falla si ya existe
Files.move(origen, destino, StandardCopyOption.REPLACE_EXISTING);   // move(DESDE, HASTA)
Files.copy(origen, copia, StandardCopyOption.REPLACE_EXISTING);
Files.deleteIfExists(fichero);

try (Stream<Path> contenido = Files.list(carpeta)) {                // list deja abierto: try ( )
    contenido.forEach(p -> System.out.println(p.getFileName()));
}
```

### Métodos (todos `static`; casi todos lanzan `IOException`)
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

### Errores típicos
| Error | Qué pasa |
|---|---|
| `move(destino, origen)` | Al revés: intenta mover lo que no existe |
| Destino = solo la carpeta | Intenta "convertir" el fichero en esa carpeta (que ya existe) |
| Mover un fichero que sigue abierto (Windows) | `FileSystemException`: ciérralo antes |

---

## 3. `File` (la antigua) y recursividad
📝 `ejemplo.java`, `ejercicio.java`

### Plantilla
```java
File c = new File("carpeta");
boolean creada = c.mkdir();                    // false si ya existía (o cualquier otro fallo)
File destino = new File("C:/DAM/Segundo/AC/ejercicios/carpeta");   // con el nombre final
boolean movida = c.renameTo(destino);
```

### Recorrer una carpeta entera (recursividad)
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

### `File` frente a `Files`
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

### ⚠ Reglas
- Llamar dos veces a `mkdir()` → la segunda da `false`. Guarda el resultado en una variable.
- `FileWriter` sin `close()` → **no se escribe nada** (se queda en el búfer).

---

## 4. Escribir texto
🧪 `F02_FicherosTexto` · 📝 Laboratorio bloques 1 y 2

### Plantillas
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

### Métodos
| Método | Escribe | Ojo |
|---|---|---|
| `Files.write(p, lista, op...)` | Una línea por elemento | Para `List<String>` o `byte[]` |
| `Files.writeString(p, texto, op...)` | Un `String` | **No** pone `\n` |
| `Files.newBufferedWriter(p, cs, op...)` | Devuelve un `BufferedWriter` | `try ( )` |
| `bw.write(t)` / `bw.newLine()` | Texto / salto de línea | |

### ⚠ Reglas
- `Files.write(ruta, unString)` **no compila**: para un `String` es `writeString`.
- Sin opciones, **vacía** el fichero si ya existía.

---

## 5. Leer texto
🧪 `F02_FicherosTexto` · 📝 BioDAM bloque 3

### Plantillas
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

### Métodos
| Método | Devuelve | ¿Hay que cerrar? |
|---|---|---|
| `Files.readAllLines(p)` | `List<String>` | No |
| `Files.readString(p)` | `String` | No |
| `Files.lines(p)` | `Stream<String>` | **Sí: `try ( )`** |
| `Files.newBufferedReader(p)` | `BufferedReader` | **Sí** |
| `br.readLine()` | `String`, o **`null`** al final | |

### ⚠ Reglas
- Un stream **solo se recorre una vez**. Si necesitas varios cálculos, lee a una `List` (`readAllLines` o `lines(...).toList()`) y haz `lista.stream()` cada vez. Es lo que hiciste en `csvIntoList`.
- `count()` devuelve **`long`**.
- `readAllLines` con un fichero enorme llena la memoria; ahí, `lines` o `BufferedReader`.

---

## 6. Opciones de apertura y de copia

### `StandardOpenOption` (escribir: `write`, `writeString`, `newBufferedWriter`, `FileChannel.open`)
| Opción | Efecto |
|---|---|
| *(ninguna)* | Crea, o **vacía** si existía (= `CREATE` + `TRUNCATE_EXISTING` + `WRITE`) |
| `CREATE` | Créalo si no existe |
| `CREATE_NEW` | Créalo; si existe, **`FileAlreadyExistsException`** |
| `APPEND` | Escribe **al final** (falla si no existe → con `CREATE`) |
| `TRUNCATE_EXISTING` | Vacíalo antes de escribir |
| `READ` / `WRITE` | Abrir para leer / para escribir (sobre todo en `FileChannel`) |

### `StandardCopyOption` (`copy`, `move`)
| Opción | Efecto |
|---|---|
| `REPLACE_EXISTING` | Si el destino existe, **lo sobrescribe** |
| `ATOMIC_MOVE` | El movimiento en un solo paso (todo o nada) |
| `COPY_ATTRIBUTES` | Copia también fechas y permisos |

### ¿Cuál uso?
| Caso | Opciones |
|---|---|
| Configuración que no se debe machacar | `CREATE_NEW` + `catch (FileAlreadyExistsException e)` |
| Log que crece | `CREATE, APPEND` |
| Informe que se regenera | ninguna |
| Mover a procesados aunque ya esté | `move(..., REPLACE_EXISTING)` |

---

## 7. Excepciones

### Plantilla
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

### ⚠ Reglas
- `catch` de la más **concreta** a la más **general**: si pones `IOException` primero, las de debajo no compilan.
- `throw` **lanza ahora**; `throws` en la firma **avisa** de que puede lanzarse.
- Pon siempre un **mensaje**: `new SecurityException("Ruta fuera de datos/: " + ruta)`.

---

## 8. ⭐ Plantilla: validar una ruta (Path Traversal)
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

### ⚠ Reglas
- Primero `toAbsolutePath()` y **luego** `normalize()`.
- La **base** es la carpeta permitida, no el log ni el fichero.

---

## 9. ⭐ Plantilla: log con fecha (BioDAM)

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

### ⚠ Reglas
- Para nombres de fichero: **solo dígitos** (`/` crea carpetas y `:` no se permite en Windows).
- `MM` = mes · `mm` = minutos · `HH` = 0–23 · `hh` = 1–12 · `SSS` = milisegundos.
- `now()` **dentro** del método que lo usa (si no, todas las líneas tienen la misma hora).
- El molde se **aplica**: `fecha.format(molde)`, no `"..." + molde`.

---

## 10. CSV ↔ objetos
🧪 `F03_CSVaObjetos`

### Plantilla
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

### ⚠ Reglas
- `split` devuelve `String[]`: los trozos **siguen siendo texto** → `parseInt` / `parseDouble`.
- `Double.parseDouble("25,90")` → `NumberFormatException`: usa punto (`Locale.ROOT`).
- Separador `;` si los textos pueden llevar comas.
- `equals` distingue mayúsculas: copia el texto **exacto** de los datos (`"Diabetes"`, `"Informática"` con tilde).

---

## 11. Binario: `DataOutputStream` / `DataInputStream`
🧪 `F04_FicherosBinarios`

### Plantilla
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

### Tamaños
| Tipo | Bytes | Escribir | Leer |
|---|---|---|---|
| `byte` / `boolean` | 1 | `writeByte` / `writeBoolean` | `readByte` / `readBoolean` |
| `short` / `char` | 2 | `writeShort` / `writeChar` | `readShort` / `readChar` |
| `int` / `float` | 4 | `writeInt` / `writeFloat` | `readInt` / `readFloat` |
| `long` / `double` | 8 | `writeLong` / `writeDouble` | `readLong` / `readDouble` |
| `String` | **2 + n** | `writeUTF` | `readUTF` |

### Tres formas de guardar un texto en binario
| Forma | En el fichero | Leer |
|---|---|---|
| `writeUTF(t)` | 2 bytes de longitud + texto | `readUTF()` |
| `writeInt(b.length)` + `write(b)` | 4 bytes de longitud + texto | `readInt()` → `byte[n]` → `readFully` → `new String(b, UTF_8)` |
| Tamaño fijo (relleno de espacios) | siempre N bytes | leer N y `trim()` |

### ⚠ Reglas
- El fichero **no sabe** qué hay: leer `int` donde había `long` no da error, da basura, y lo de detrás también.
- Final = **`EOFException`** (no `null`, no `-1`). Con `InputStream.read(bufer)`, en cambio, el final es **`-1`**.
- `readShort`/`readByte` tienen **signo** → `readUnsignedShort`/`readUnsignedByte`.
- Abrir un `OutputStream` **vacía** el fichero.

---

## 12. Serialización
🧪 `F05_SerializacionObjetos`

### Plantilla
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

### ⚠ Reglas
| Concepto | Qué hace |
|---|---|
| `implements Serializable` | Interfaz **marca** (sin métodos). Sin ella: `NotSerializableException` |
| Atributos | También tienen que ser serializables (`String`, `int`, `List`… lo son) |
| `serialVersionUID` | Versión de la clase. Si no coincide: `InvalidClassException` |
| `transient` | No se guarda. Al leer: `null`, `0`, `false` |
| `readObject()` | Lanza también `ClassNotFoundException` |

### CSV frente a serialización
| CSV | Serialización |
|---|---|
| Texto legible, lo abre Excel | Binario, solo Java |
| Conviertes cada campo a mano | El objeto entero de golpe |
| Sirve entre lenguajes | Solo Java y la misma versión de la clase |

---

## 13. Acceso aleatorio: `RandomAccessFile`
🧪 `F06_AccesoAleatorio`

### Plantilla
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

### Métodos
| Método | Qué hace |
|---|---|
| `new RandomAccessFile(f, "r" / "rw")` | Abrir para leer / leer y escribir |
| `seek(pos)` | Mueve el cursor al byte `pos` (el primero es 0) |
| `getFilePointer()` | Dónde está el cursor |
| `length()` | Tamaño → nº de registros = `length() / TAM` |
| `setLength(n)` | Corta o alarga |
| `readXxx` / `writeXxx` | Los mismos que `Data*Stream` |

### ⚠ Reglas
- **Registros de tamaño fijo**: posición = **n × TAM** (n empieza en 0).
- Escribir **sobrescribe**, no inserta. Solo es seguro cambiar un campo por otro **del mismo tamaño**.
- Pide `String` o `File`: si tienes un `Path`, `ruta.toFile()`.

---

## 14. NIO: `ByteBuffer` + `FileChannel`
📝 Laboratorio bloque 3 · `MapaBinario`

### Plantilla: escribir un registro de tamaño fijo
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

### Plantilla: modificar en una posición
```java
try (FileChannel canal = FileChannel.open(archivo, StandardOpenOption.WRITE)) {
    ByteBuffer nuevo = ByteBuffer.wrap(Arrays.copyOf("NVMe 2TB".getBytes(StandardCharsets.UTF_8), 40));
    canal.write(nuevo, (long) n * BYTES_REGISTRO + Integer.BYTES);   // salta id, pisa el nombre
}
```

### Plantilla: leer un binario pequeño (sin canal)
```java
ByteBuffer bb = ByteBuffer.wrap(Files.readAllBytes(ruta));     // caja llena, sin flip
int flag = bb.getInt();
int marca = Short.toUnsignedInt(bb.getShort());                // sin signo: 0xCAFE
System.out.println(Integer.toHexString(marca));
```

### `ByteBuffer`
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

### `FileChannel`
| Método | Qué hace |
|---|---|
| `FileChannel.open(p, op...)` | Abrir (`READ`, `WRITE`, `CREATE`…) |
| `read(buf)` | Llena la caja; devuelve bytes leídos o **`-1`** al final |
| `write(buf)` | Escribe de `position` a `limit` |
| `read(buf, pos)` / `write(buf, pos)` | En esa posición del fichero |
| `position(n)` / `size()` / `truncate(n)` | Mover / tamaño / cortar |

### ⚠ Reglas
- **Sin `flip()` no se escribe nada** (o se lee basura). `BufferUnderflowException` / `BufferOverflowException` = olvidaste el `flip` o la caja es pequeña.
- `ByteBuffer` **no tiene `getUTF`**: longitud → `byte[]` → `new String(b, UTF_8)`.
- Si el texto nuevo es más corto y no truncas, quedan bytes viejos al final.

---

## 15. Excel con Apache POI
📝 Laboratorio bloque 4 · Dependencia: `org.apache.poi:poi-ooxml`

### Plantilla
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

### ⚠ Reglas
- Todo **desde 0**: `createRow(0)` es la fila 1 y `createCell(0)` es la columna A.
- Fórmula **sin `=`**. Los números como `double`, no como `String`.

---

## 16. ⭐ Cálculos con binarios: tamaños, posiciones y hexadecimal
📝 `MapaBinario`, Laboratorio bloque 3, `F06`

### 1. Tamaño de un registro = sumar sus campos
| Tipo | Bytes |
|---|---|
| `byte`, `boolean` | 1 |
| `short`, `char` | 2 |
| `int`, `float` | 4 |
| `long`, `double` | 8 |

### 2. ¿Y si hay un `String`? Depende de cómo se escribió
| Forma | Cuenta | ¿Tamaño fijo? |
|---|---|---|
| Relleno a N caracteres con `writeChars` (`F06`) | **N × 2** (cada `char` = 2) → 20 caracteres = 40 bytes | ✅ sí |
| Relleno a N bytes (Laboratorio: nombre de 40 bytes) | **N** (lo que diga el enunciado) | ✅ sí |
| `writeUTF` | **2** (la longitud) **+ los bytes del texto** → `"Hola"` = 2 + 4 = 6 | ❌ no: cada texto mide distinto |

Para el acceso aleatorio los textos **se rellenan hasta un tamaño fijo**. Con `writeUTF` cada registro mide distinto, la fórmula `n × tamaño` ya no sirve y hay que leer en orden desde el principio.

### 3. Las fórmulas
| Quiero… | Cálculo |
|---|---|
| Dónde empieza el registro n | **n × tamaño del registro** (el primero es el registro 0) |
| Dónde empieza un campo del registro n | **(n × tamaño) + lo que ocupan los campos de delante** |
| Último byte del registro n | inicio + tamaño − 1 (se cuenta desde 0) |
| Tamaño del `ByteBuffer` | lo que mide lo que vas a leer o escribir (el registro o solo un campo) |
| Cuántos registros hay | `Files.size(f) / tamaño` · `raf.length() / tamaño` |

> 💬 *"Primero salto al registro (n × tamaño) y luego sumo lo que ocupan los campos de delante."* Como en el cine: primero la fila y luego las butacas de delante.

### 4. Hexadecimal → decimal
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

### 5. Ejemplo resuelto: `inventario.dat`
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

### 6. Las trampas
- **Leer con otro orden o tipo no da error.** Leer el registro 0 con `readInt()`, `readShort()`, `readDouble()` da 10 ✅, 16441 ❌ y basura ❌: en cuanto un tipo no coincide, se descoloca todo lo de detrás.
- **El orden de los campos no está en el fichero:** lo sabes porque te lo da el enunciado o porque lo escribiste tú (el orden de tus `write`). Se lee en el mismo orden (FIFO, secuencial).
- **El signo:** `readShort()` / `getShort()` de `0xCAFE` sale **negativo** → `Short.toUnsignedInt(...)` o `readUnsignedShort()`.
- **Faltan bytes:** si pides más bytes de los que quedan → `EOFException` (`Data*Stream`) o `BufferUnderflowException` (`ByteBuffer`).

---

## 17. Imports
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
