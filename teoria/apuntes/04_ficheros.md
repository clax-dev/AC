# Tema 4: Ficheros

> **Cómo leer estos apuntes:** cada apartado empieza explicando **la idea**, sin código. El código va al final de cada apartado, como ejemplo pequeño. Si una idea no se entiende, no sigas leyendo el código: vuelve a la explicación.
>
> - **Ejercicios del tema:** `ejercicios/src/tema4_ficheros/` (`ejemplo.java`, `ejercicio.java`, `LaboratorioFicherosTienda.java`)
> - **Demos ejecutables:** `teoria/src/t4_ficheros/` (F01 a F06). Crean sus ficheros en la carpeta `ficheros_demo/`.

---

## 1. ¿Qué es un fichero y para qué lo necesitamos?

Todo lo que guarda un programa en variables (listas, objetos, números…) vive en la **memoria RAM**. La RAM se borra cuando el programa termina: si mañana vuelves a ejecutar la tienda, los productos de ayer ya no están.

Un **fichero** es un trozo del **disco duro** con un nombre. Lo que escribes en el disco **se queda ahí** aunque cierres el programa o apagues el ordenador. Por eso esta asignatura se llama *Acceso a Datos*: aprendes a **guardar** datos en algún sitio permanente y a **recuperarlos** después. Los ficheros son el primer sitio; más adelante vendrán las bases de datos.

Una **carpeta** (o *directorio*) no guarda datos: solo **agrupa** ficheros y otras carpetas, como una caja dentro de otra caja.

### Dos tipos de fichero: texto y binario

Por dentro, todo fichero son **bytes** (números del 0 al 255). Lo que cambia es **cómo se interpretan**:

- **Fichero de texto:** los bytes representan **letras**. Lo puedes abrir con el Bloc de notas y leerlo. Ejemplos: `.txt`, `.csv`, `.properties`, `.log`.
- **Fichero binario:** los bytes representan **datos en bruto** (un número, una imagen, un objeto…). Si lo abres con el Bloc de notas ves símbolos raros, porque no son letras. Ejemplos: `.dat`, `.bin`, `.ser`, `.jpg`, `.xlsx`.

¿Por qué existen los binarios si no se pueden leer? Porque **ocupan menos y son más rápidos**. El número `2000000` como texto son 7 caracteres; como binario (`int`) son siempre 4 bytes.

---

## 2. Una ruta NO es el fichero

Esta es **la idea más importante del tema**, y la causa de muchos de los líos.

Una **ruta** es la **dirección** de un fichero o carpeta: `C:\DAM\Segundo\AC\salida\config\app.properties`. En Java, una ruta se guarda en un objeto (`File` o `Path`), pero **crear ese objeto no crea nada en el disco**. Es como escribir una dirección en un sobre: la casa no aparece por escribirla.

Por eso, trabajar con ficheros siempre tiene **dos momentos**:

1. **Escribir la dirección:** crear el objeto `File` o `Path`. No toca el disco, no tarda nada y no falla aunque el fichero no exista.
2. **Hacer algo con ella:** crear, leer, escribir, mover, borrar o comprobar. **Esto sí** toca el disco, y **esto sí** puede fallar.

```java
Path p = Paths.get("salida", "config");   // 1. solo la dirección: no existe nada todavía
Files.createDirectories(p);               // 2. ahora sí: se crean las carpetas en el disco
```

### Rutas absolutas y relativas

- **Absoluta:** la dirección completa, desde la unidad: `C:/DAM/Segundo/AC/ejercicios/carpeta`. Siempre apunta al mismo sitio.
- **Relativa:** una dirección **incompleta**, como `"carpeta"` o `"salida/config"`. Java la completa añadiendo delante el **directorio de trabajo**.

El **directorio de trabajo** es la carpeta desde la que se ejecuta el programa. En tus proyectos de IntelliJ es **`C:\DAM\Segundo\AC\`**, y por eso `new File("carpeta")` crea `C:\DAM\Segundo\AC\carpeta`.

> **Si "no encuentra el fichero"**, casi siempre es porque la ruta relativa se está completando con un directorio de trabajo distinto del que crees. Imprime la ruta completa (`getAbsolutePath()` o `toAbsolutePath()`) y lo verás.

---

## 3. Dos cajas de herramientas: `File` (antigua) y `Path` + `Files` (moderna)

Java tiene **dos formas** de trabajar con ficheros. Hacen casi lo mismo, pero se escriben distinto, y en clase usáis las dos (`File` en los primeros ejercicios; `Path` y `Files` en el laboratorio).

| | `java.io.File` (antigua, Java 1) | `java.nio.file` (moderna, Java 7+) |
|---|---|---|
| La ruta | un objeto `File` | un objeto `Path` |
| Las operaciones | métodos **del propio objeto**: `f.mkdir()` | métodos **estáticos de `Files`**: `Files.createDirectory(p)` |
| Si algo falla | devuelve `false` y **no te dice por qué** | **lanza una excepción** que dice qué ha pasado |

La gran diferencia está en la última fila. Con `File`, un `mkdir()` que devuelve `false` puede significar "ya existía", "no tengo permiso" o "no existe la carpeta padre", y no hay forma de saberlo. Con `Files`, cada problema es una excepción distinta (`FileAlreadyExistsException`, `NoSuchFileException`, `AccessDeniedException`…).

**Truco para distinguirlas al leer código:** si ves `File` y métodos sobre el objeto, es la antigua; si ves `Files.` con **s**, es la moderna.

---

## 4. Operaciones con carpetas y ficheros

Antes de **leer o escribir el contenido** de un fichero, hay operaciones que tratan los ficheros y carpetas como "cajas cerradas": crearlas, moverlas, borrarlas, ver qué hay dentro.

### Crear

- **Una carpeta:** solo funciona si su carpeta **padre ya existe**. No puedes crear `salida/config` si no existe `salida`.
- **Varias carpetas anidadas de golpe:** crea toda la cadena que falte. Además, si ya existe, no falla. Por eso se considera la forma "segura".
- **Un fichero vacío:** lo crea si no existe.

### Mover y renombrar (son la misma operación)

Mover un fichero es **cambiar su dirección**. Renombrar también es cambiar su dirección, solo que dentro de la misma carpeta. Por eso Java usa **el mismo método** para las dos cosas.

Dos reglas que hay que tener claras:
- El destino es **la dirección final completa, con el nombre incluido**. Para meter `carpeta` dentro de `ejercicios`, el destino es `.../ejercicios/carpeta`, no `.../ejercicios`.
- La carpeta de destino **tiene que existir**. Mover no construye el camino: si la "casa nueva" no existe, la mudanza falla.

Mover dentro del mismo disco es **instantáneo**, aunque la carpeta pese mucho, porque no se copia nada: solo se cambia la dirección en el índice del disco.

### Borrar

Una carpeta **solo se puede borrar si está vacía**. Para borrar una carpeta con cosas dentro, primero hay que borrar su contenido.

### Ver qué hay dentro de una carpeta y recursividad

Listar una carpeta te da **solo el primer nivel**: los ficheros y subcarpetas que tiene directamente dentro. Si quieres todo lo que hay en las subcarpetas, y en las subcarpetas de esas…, no sabes cuántos niveles hay. Para eso se usa la **recursividad**:

> Para recorrer una carpeta: miro cada cosa que tiene dentro. Si es un fichero, lo muestro. Si es una carpeta, **la recorro de la misma forma** (el método se llama a sí mismo).

La recursividad termina sola cuando llega a carpetas que ya no tienen subcarpetas: ese es el **caso base**.

### Comprobar

Existe o no, es carpeta o fichero, cuánto ocupa, si se puede leer o escribir…

### Las dos formas, lado a lado

| Quiero… | Con `File` | Con `Files` |
|---|---|---|
| Crear una carpeta | `f.mkdir()` | `Files.createDirectory(p)` |
| Crear carpetas anidadas | `f.mkdirs()` | `Files.createDirectories(p)` ⭐ |
| Crear un fichero vacío | `f.createNewFile()` | `Files.createFile(p)` |
| Mover o renombrar | `f.renameTo(destino)` | `Files.move(p, destino)` |
| Copiar | — | `Files.copy(p, destino)` |
| Borrar | `f.delete()` | `Files.delete(p)` / `Files.deleteIfExists(p)` |
| ¿Existe? | `f.exists()` | `Files.exists(p)` |
| ¿Es carpeta? | `f.isDirectory()` | `Files.isDirectory(p)` |
| Tamaño | `f.length()` | `Files.size(p)` |
| Contenido de una carpeta | `f.listFiles()` | `Files.list(p)` |
| Una ruta dentro de otra | `new File(carpeta, "nombre")` | `carpeta.resolve("nombre")` |

```java
File carpeta = new File("carpeta");
carpeta.mkdir();                                              // crear
File destino = new File("C:/DAM/Segundo/AC/ejercicios/carpeta");
carpeta.renameTo(destino);                                    // mover (destino con el nombre incluido)
```

**Demo:** `F01_RutasYDirectorios.java`.

---

## 5. Leer y escribir el contenido de un fichero

Ahora sí, el contenido. Toda lectura o escritura sigue siempre **el mismo ciclo de tres pasos**, como usar un grifo:

1. **Abrir** el fichero: le dices a Java qué fichero y para qué (leer o escribir).
2. **Usarlo:** leer o escribir los datos.
3. **Cerrarlo.**

### Por qué es tan importante cerrar

- **Al escribir**, Java no manda cada letra al disco en el momento, porque sería lentísimo. Las va guardando en un **búfer** (una zona de espera en memoria) y las manda al disco por bloques. **Al cerrar** se vacía el búfer. Si no cierras, lo último que escribiste puede **no llegar nunca al disco**.
- **Al leer**, mientras el fichero está abierto, el sistema operativo lo tiene **bloqueado**: en Windows no podrías ni borrarlo.

### `try-with-resources`: cerrar sin tener que acordarte

Si abres el fichero **dentro de los paréntesis de un `try`**, Java lo cierra **automáticamente** al salir del bloque, tanto si todo va bien como si salta un error. Es la forma recomendada siempre:

```java
try (Stream<String> lineas = Files.lines(archivo)) {   // 1. se abre
    lineas.forEach(System.out::println);               // 2. se usa
}                                                      // 3. se cierra SOLO
```

### Escribir: ¿qué pasa si el fichero ya existe?

Cuando escribes en un fichero que **ya tiene contenido**, hay tres comportamientos posibles. Se eligen con las **opciones de apertura** (`StandardOpenOption`):

| Opción | Si NO existe | Si YA existe | Se parece a… | Uso típico |
|---|---|---|---|---|
| *(ninguna)* | lo crea | **lo vacía y escribe desde cero** | "Guardar" encima de un documento | datos que se regeneran cada vez |
| `CREATE_NEW` | lo crea | **se niega**: lanza `FileAlreadyExistsException` | "solo guárdalo si no hay otro con ese nombre" | **configuración**: no machacar lo que el usuario cambió |
| `CREATE` + `APPEND` | lo crea | **escribe al final**, sin borrar nada | añadir una línea a un diario | **logs**: el historial va creciendo |

> `APPEND` **solo** falla si el fichero no existe (no se puede añadir a algo que no existe). Por eso casi siempre va acompañado de `CREATE`, que significa "créalo si hace falta".

#### Por qué `CREATE_NEW` y no "comprobar si existe y luego escribir"

Parece lo mismo, pero no lo es. Entre **comprobar** y **escribir** pasa un instante, y en ese instante otro programa puede crear el fichero, que tú machacarías sin darte cuenta. Es el mismo problema de "comprobar y luego actuar" que viste en PSP con la cola de la cafetería. `CREATE_NEW` hace las dos cosas **en una sola operación** (atómica), así que no hay hueco.

### Leer

Un fichero de texto se puede leer de tres formas, según lo que necesites:

- **Todo de golpe** en un `String` o en una lista de líneas. Es cómodo, pero si el fichero es enorme llena la memoria.
- **Línea a línea**, con un bucle que termina cuando no hay más líneas (la lectura devuelve `null`).
- **Como un stream de líneas.** Es lo mismo que `lista.stream()` de los temas anteriores: puedes usar `filter`, `map`, `forEach`…

### La codificación (UTF-8)

Como un fichero de texto son bytes que representan letras, hace falta un **acuerdo** sobre qué número es cada letra. Ese acuerdo es la **codificación**. Para las letras normales casi todas coinciden, pero para la `ñ` o las tildes no. Si escribes con una codificación y lees con otra, salen cosas como `Ã±`. **Usa siempre UTF-8**: es el estándar y lo que usan por defecto los métodos de `Files`.

```java
List<String> propiedades = List.of("tienda.nombre=PC-DAM", "tienda.moneda=EUR");
Files.write(archivo, propiedades, StandardOpenOption.CREATE_NEW);   // cada elemento = una línea

Files.writeString(log, "Tienda iniciada" + System.lineSeparator(),
        StandardOpenOption.CREATE, StandardOpenOption.APPEND);       // writeString no añade el salto de línea
```

**Demo:** `F02_FicherosTexto.java` · **Laboratorio:** bloque 1 (`CREATE_NEW`) y bloque 2 (`APPEND`).

---

## 6. Excepciones: los avisos del disco

Trabajar con el disco puede salir mal por muchas razones que **no dependen de tu código**: el fichero no existe, ya existe, no tienes permiso, el disco está lleno… Java te avisa lanzando una **excepción**. Todas son "hijas" de `IOException`.

Tienes dos opciones con cada aviso:

- **Atraparlo** con `try/catch`, cuando sabes qué hacer en ese caso. Por ejemplo: "si ya existía, muestro un aviso y sigo".
- **Dejarlo pasar** con `throws IOException` en el método, cuando no sabes qué hacer ahí y prefieres que lo decida quien te llamó.

Atrapa **la excepción concreta** que esperas (`FileAlreadyExistsException`), no una genérica. Así, si pasa **otra cosa distinta** (por ejemplo, falta de permisos), no la confundes con la que esperabas.

| Excepción | Cuándo salta |
|---|---|
| `NoSuchFileException` / `FileNotFoundException` | el fichero o la carpeta no existe |
| `FileAlreadyExistsException` | ya existe (con `CREATE_NEW`, `createFile`, `createDirectory`) |
| `DirectoryNotEmptyException` | intentas borrar una carpeta con contenido |
| `AccessDeniedException` | no tienes permiso |
| `EOFException` | has llegado al final de un fichero binario (apartado 8) |

---

## 7. Seguridad: el "Path Traversal"

Cuando un programa deja que **el usuario** indique una ruta (por ejemplo, "dime qué fichero quieres descargar"), hay que comprobar que esa ruta no se sale de la carpeta permitida.

El truco del atacante es el `..`, que significa "sube una carpeta". La ruta `salida/../../Windows/System32` **empieza** por `salida`, así que parece inofensiva, pero en realidad sube dos niveles y acaba en una carpeta del sistema. A esto se le llama **Path Traversal** ("atravesar" carpetas hacia arriba).

La defensa tiene dos pasos:

1. **Normalizar** las dos rutas: convertirlas en absolutas y **resolver los `..`**. Así se ve a dónde apunta la ruta **de verdad**.
2. **Comprobar** que la ruta del usuario **empieza por** la carpeta permitida, **comparando carpeta a carpeta**, no letra a letra.

¿Por qué no letra a letra? Porque, como texto, `salida2/secreto.txt` "empieza por" `salida`, y se colaría. Comparando carpetas, `salida2` y `salida` son carpetas distintas. El `startsWith` de `Path` compara carpetas; el de `String` compara letras.

```java
Path base      = Paths.get("salida").toAbsolutePath().normalize();
Path candidata = rutaDelUsuario.toAbsolutePath().normalize();
boolean permitida = candidata.startsWith(base);    // Path.startsWith: compara carpeta a carpeta
```

**Laboratorio:** bloque 2, TODO 2.3.

---

## 8. Guardar objetos: cuatro formatos

Hasta aquí has guardado **líneas de texto**. Pero en un programa real quieres guardar **objetos**: productos, incidencias… Hay varias formas, y cada una tiene sus ventajas.

### 8.1 CSV: objetos como líneas de texto

Cada objeto es **una línea**, y sus atributos van separados por un carácter, normalmente `;`. La primera línea suele ser la **cabecera** con los nombres de las columnas.

```
codigo;nombre;precio
P001;Teclado;25.90
```

- **Guardar:** juntas los atributos del objeto con el separador.
- **Cargar:** partes cada línea por el separador (`split`) y conviertes cada trozo a su tipo (`Integer.parseInt`, `Double.parseDouble`).

Lo bueno: es **texto**, así que lo abre Excel y cualquier otro programa o lenguaje. Lo malo: tienes que convertir cada campo a mano. Cuidado con los decimales: en España se escribe `25,90`, pero Java espera `25.90`.

**Demo:** `F03_CSVaObjetos.java`, que implementa de verdad la interfaz `FuenteDatos<T>` de tu ejercicio de incidencias.

### 8.2 Binario con tipos primitivos (`DataOutputStream` / `DataInputStream`)

Escribes los datos **uno detrás de otro, en binario**: un `String`, luego un `double`, luego un `int`… El fichero no guarda "qué es cada cosa"; solo hay bytes seguidos.

Esto tiene una consecuencia muy importante: **hay que leer en el mismo orden y con los mismos tipos** con los que se escribió. Si escribiste `int` y `double` y lees `double` e `int`, obtienes números sin sentido, sin ningún error que te avise.

Además, al llegar al final del fichero, la lectura no devuelve `null` (como en los de texto): **lanza `EOFException`** ("End Of File"). Para leer "hasta el final", se captura esa excepción.

**Demo:** `F04_FicherosBinarios.java`.

### 8.3 Serialización: el objeto entero de golpe

En lugar de guardar los atributos uno a uno, Java convierte **el objeto completo** en bytes (**serializar**) y luego lo reconstruye (**deserializar**). Puedes guardar incluso una lista entera de una vez.

Requisitos y conceptos:
- La clase tiene que **declarar que se puede serializar**: `implements Serializable`. Es una interfaz sin métodos, solo una "marca".
- `serialVersionUID` es un **número de versión** de la clase. Si cambias la clase y el número ya no coincide con el del fichero, Java se niega a leerlo, porque el objeto guardado ya no encaja con la clase nueva.
- Los atributos marcados como `transient` **no se guardan**. Sirve para cosas como contraseñas. Al leer, valen `null`, `0` o `false`.

Lo bueno: es lo más cómodo. Lo malo: **solo lo entiende Java**, y solo con la misma versión de la clase.

**Demo:** `F05_SerializacionObjetos.java`.

### 8.4 Acceso aleatorio: saltar directamente a un registro

Todo lo anterior es **acceso secuencial**: para llegar al producto número 1000 hay que leer los 999 anteriores, como en una cinta de casete.

El **acceso aleatorio** (o directo) te deja **saltar** a cualquier posición del fichero, como en un array. La condición es que todos los registros ocupen **exactamente lo mismo** (registros de **tamaño fijo**). Así, la posición del registro *n* se calcula con una multiplicación:

> posición del registro n = n × tamaño de un registro

¿Y los textos, que tienen longitud variable? Se **rellenan con espacios** hasta un tamaño fijo. Por ejemplo, el nombre siempre ocupa 20 caracteres, aunque sea "Ratón".

La gran ventaja es que puedes **leer o modificar un solo registro** sin tocar el resto del fichero. Por ejemplo, cambiar solo el stock del producto 500.

**Demo:** `F06_AccesoAleatorio.java`.

### ¿Cuál elijo?

| Necesito… | Formato |
|---|---|
| Que lo lea una persona, Excel u otro programa | **CSV** |
| Guardar y recuperar objetos Java rápidamente | **Serialización** |
| Muchos registros e ir directamente a uno o modificarlo | **Acceso aleatorio** |
| Datos numéricos compactos | **Binario** (`Data*Stream`) |

---

## 9. Canales y búferes (`FileChannel` + `ByteBuffer`)

Es la forma "moderna" (NIO) de hacer lo mismo que el acceso aleatorio. Hay dos piezas:

- **`FileChannel`, el canal:** la **conexión** con el fichero. Por ella entran y salen bytes, y puedes elegir **en qué posición** leer o escribir.
- **`ByteBuffer`, el búfer:** una **caja de bytes** en memoria, de tamaño fijo. No escribes directamente en el fichero: primero **llenas la caja**, y luego **mandas la caja** por el canal.

### La parte que más lía: el `flip()`

Un `ByteBuffer` tiene un "cursor" (la **posición**) que avanza a medida que metes datos. Cuando terminas de llenarlo, el cursor está **al final**. Si lo mandases así por el canal, Java empezaría a leer desde el final… y no mandaría nada.

`flip()` **"le da la vuelta"** a la caja: pone el cursor al principio y marca hasta dónde hay datos. Pasa de "modo llenar" a "modo vaciar".

```
1. allocate(44)   → caja vacía de 44 bytes
2. putInt(101)    → mete el ID          (el cursor avanza 4)
3. put(nombre)    → mete el nombre      (el cursor avanza 40)
4. flip()         → "ya he terminado de llenar, ahora léela desde el principio"
5. canal.write(buffer) → se manda al fichero
```

`write` no siempre manda todos los bytes de una vez. Por eso se escribe **en un bucle mientras queden bytes** (`hasRemaining()`).

Para **modificar** un registro concreto, igual que en el acceso aleatorio, te sitúas en su posición (`n × tamaño del registro`) y escribes ahí.

**Laboratorio:** bloque 3.

---

## 10. Excel con Apache POI

Un `.xlsx` no es texto: es un formato complejo. Java no sabe crearlos por sí solo, así que se usa una **librería externa**, **Apache POI**. Hay que añadirla al proyecto (`org.apache.poi:poi-ooxml`); si no, las clases salen en rojo.

POI imita la estructura de Excel, de lo grande a lo pequeño:

```
Libro (XSSFWorkbook)  →  Hoja (Sheet)  →  Fila (Row)  →  Celda (Cell)
```

- Las filas y celdas se numeran **desde 0**: la fila 0 es la fila 1 de Excel y la celda 0 es la columna A.
- Una celda puede guardar un **texto**, un **número** (guárdalo como número, no como texto, para que Excel pueda operar con él) o una **fórmula**, como `B2*C2` (sin el `=`).
- El **estilo** (negrita, color de fondo…) se crea aparte y se aplica a las celdas.
- Al final, el libro se **guarda** escribiéndolo en un fichero, con el mismo ciclo abrir → escribir → cerrar.

**Laboratorio:** bloque 4.

---

## 11. Errores típicos

| Error | Qué pasa | Solución |
|---|---|---|
| No cerrar el fichero | lo escrito no llega al disco o el fichero queda bloqueado | `try-with-resources` |
| Ruta relativa mal entendida | "no encuentra el fichero" | imprimir la ruta absoluta |
| Mover a una carpeta que no existe | `renameTo` devuelve `false` | crear antes la carpeta de destino |
| Destino sin el nombre final | intenta "convertir" la carpeta en otra que ya existe | el destino termina en el nombre: `.../ejercicios/carpeta` |
| Llamar dos veces a `mkdir()` | la segunda devuelve `false` | guardar el resultado en una variable |
| Olvidar `APPEND` | se borra lo que había | `CREATE` + `APPEND` |
| Quitar el `try/catch` de `CREATE_NEW` | la segunda ejecución se rompe | atrapar `FileAlreadyExistsException` |
| Leer un binario en otro orden | datos sin sentido | mismo orden y tipos que al escribir |
| Esperar `null` al final de un binario | nunca llega: salta `EOFException` | capturar `EOFException` |
| Olvidar `flip()` | no se escribe nada | `flip()` antes de `write` |
| Clase sin `Serializable` | `NotSerializableException` | `implements Serializable` |

---

## 12. Glosario

| Palabra | Significado |
|---|---|
| **Ruta** (`Path`, `File`) | la dirección de un fichero o carpeta; escribirla no crea nada |
| **Directorio de trabajo** | la carpeta desde la que se completan las rutas relativas (`C:\DAM\Segundo\AC`) |
| **Búfer** | zona de memoria donde se juntan datos antes de mandarlos al disco |
| **Opción de apertura** | qué hacer si el fichero existe o no existe (`CREATE_NEW`, `APPEND`…) |
| **Atómico** | se hace en un solo paso, sin hueco para que otro se cuele |
| **Secuencial** | leer desde el principio, en orden |
| **Aleatorio / directo** | saltar a cualquier posición |
| **Registro** | los datos de un elemento (un producto) dentro del fichero |
| **Serializar** | convertir un objeto en bytes para guardarlo |
| **Normalizar** | resolver los `..` de una ruta para ver a dónde apunta de verdad |
| **EOF** | *End Of File*: el final del fichero |
