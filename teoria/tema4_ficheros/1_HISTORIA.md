# Tema 4: Ficheros, contado como una historia

> **Cómo usar este archivo:**
> - Léelo **de un tirón**, como un cuento. Aquí casi no hay sintaxis: solo **qué es cada cosa** y **qué problema resuelve**.
> - Cada bloque tiene una **💬 frase clave**. Si te la sabes, sabes el bloque.
> - Ten abierto `visual.pdf` al lado: cada bloque te dice qué página mirar.
> - La sintaxis está en `2_TECNICO.md`, y para comprobarte tienes `3_PREGUNTAS.md`.
> - **Ejercicios del tema:** `ejercicios/src/tema4_ficheros/` (`ejemplo`, `ejercicio`, `LaboratorioFicherosTienda`, `MapaBinario`) y la tarea `T01_BioDAM`.
> - **Demos:** `teoria/src/t4_ficheros/` (F01 a F06). Crean sus ficheros en `ficheros_demo/`.

**La historia entera en tres líneas:**
- Todo lo que guarda tu programa en variables vive en la **RAM**, y la RAM **se borra** al cerrar el programa.
- Un **fichero** es un trozo del **disco** con nombre: lo que escribes ahí **se queda**. Por eso la asignatura se llama *Acceso a Datos*.
- En este tema aprendes a **encontrar** un fichero (rutas), a **leerlo y escribirlo** (texto y binario) y a **guardar objetos** en él.

---

## 1. RAM, disco, texto y binario
📄 *PDF, página 2*

> 💬 **"La RAM se olvida al cerrar; el disco se acuerda. Un fichero de texto son letras; uno binario son datos en bruto."**

- **Fichero** = datos guardados en el disco con un nombre. **Carpeta** (directorio) = una caja que **agrupa** ficheros y otras carpetas; no guarda datos.
- Por dentro **todo fichero son bytes** (números del 0 al 255). Lo que cambia es **cómo se interpretan**:
  - **Texto** (`.txt`, `.csv`, `.log`, `.properties`): los bytes son **letras**. Se lee con el Bloc de notas.
  - **Binario** (`.dat`, `.bin`, `.ser`, `.jpg`, `.xlsx`): los bytes son **datos en bruto**. En el Bloc de notas salen símbolos raros.
- ¿Por qué usar binario si no se lee? Porque **ocupa menos y es más rápido**: el número `2000000` como texto son 7 caracteres; como `int` binario son **siempre 4 bytes**.

---

## 2. Una ruta NO es el fichero
📄 *PDF, página 3* · 🧪 *Demo `F01_RutasYDirectorios`*

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

## 3. Dos cajas de herramientas: `File` (antigua) y `Path` + `Files` (moderna)
📄 *PDF, página 4*

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

## 4. Trabajar con cajas cerradas: crear, mover, borrar y listar
📄 *PDF, página 5* · 🧪 *Demo `F01`* · 📝 *`ejemplo.java`, `ejercicio.java`*

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

## 5. Leer y escribir: abrir, usar y cerrar
📄 *PDF, página 6* · 🧪 *Demo `F02_FicherosTexto`*

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

## 6. Escribir en un fichero que ya existe: las opciones de apertura
📄 *PDF, página 7* · 📝 *Laboratorio, bloques 1 y 2 · BioDAM, bloque 1*

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

## 7. Los avisos del disco: las excepciones
📄 *PDF, página 7*

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

## 8. Seguridad: el Path Traversal
📄 *PDF, página 8* · 📝 *Laboratorio, TODO 2.3 · BioDAM, bloque 2*

> 💬 **"`..` sube una carpeta. Normaliza las dos rutas y compara carpeta a carpeta, nunca letra a letra."**

Si **el usuario** te da una ruta, tienes que comprobar que no se sale de la carpeta permitida.
- El truco del atacante es el **`..`** ("sube una carpeta"). `datos/../../etc/passwd` **empieza** por `datos`, pero en realidad sube dos niveles y sale fuera.
- **La defensa, en dos pasos:**
  1. **Normalizar** las dos rutas: primero `toAbsolutePath()` y **luego** `normalize()` (que resuelve los `..`). En ese orden y **en las dos**.
  2. **Comprobar** con `Path.startsWith(base)`, que compara **carpeta a carpeta**.
- ¿Por qué no `String.startsWith`? Porque como texto, `"salida2/secreto.txt"` empieza por `"salida"`, y se colaría. Como `Path`, `salida2` y `salida` son carpetas distintas.
- Para **probar** un ataque, la ruta tiene que **salirse** de verdad: `datos/essd/ppp` está **dentro** de `datos`.

---

## 9. Guardar objetos: cuatro formatos
📄 *PDF, página 9* · 🧪 *Demos `F03`, `F04`, `F05`, `F06`*

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

## 10. Un binario por dentro
📄 *PDF, página 10* · 📝 *`MapaBinario`*

> 💬 **"Un binario es una fila de bytes sin etiquetas: tienes que saber qué tipo va en cada sitio y cuánto ocupa."**

- Tamaños: `byte` y `boolean` 1 · `short` y `char` 2 · `int` y `float` 4 · `long` y `double` 8 · `writeUTF` = **2 bytes de longitud + el texto**.
- Los números se guardan con el byte más importante primero (*big-endian*): el `int` 52 son `00 00 00 34`.
- En los visores, cada byte sale en **hexadecimal** (`00` a `FF`). En Java: `0xCAFE`, `Integer.toHexString(n)`.
- **Trampa del signo:** `readShort()` / `getShort()` devuelven con signo. `0xCAFE` sale **negativo** y `== 0xCAFE` da `false`. Solución: `readUnsignedShort()` o `Short.toUnsignedInt(s)`. Es lo que hiciste en `MapaBinario` con el flag de inicio.
- **Comprobar:** `Files.size(ruta)` y comparar con la suma de lo que ocupa cada campo.
- **Antes de modificar un binario, haz una copia**: no hay "deshacer".

---

## 11. Canales y búferes: la forma NIO (`FileChannel` + `ByteBuffer`)
📄 *PDF, página 10* · 📝 *Laboratorio, bloque 3 · `MapaBinario`*

> 💬 **"El canal es la tubería y el `ByteBuffer` es el cubo. Llenas el cubo, le das la vuelta con `flip()` y lo vacías por la tubería."**

- **`FileChannel`:** la **conexión** con el fichero; puedes elegir **en qué posición** leer o escribir.
- **`ByteBuffer`:** una **caja de bytes** de tamaño fijo en memoria. No escribes en el fichero: **llenas la caja** y **mandas la caja**.
- **El `flip()`:** al llenar, el cursor (`position`) avanza hasta el final. Si mandas la caja así, no se manda nada. `flip()` **pone el cursor al principio** y marca hasta dónde hay datos: pasa de **"modo llenar"** a **"modo vaciar"**.
- `write` puede no mandarlo todo de una vez: se repite **mientras** `hasRemaining()`.
- Atajo para leer un fichero pequeño: `Files.readAllBytes` + `ByteBuffer.wrap(...)`: caja **ya llena** y lista para leer, sin canal y sin `flip()`. Es lo que usa `MapaBinario`.
- Escribir en una posición **sobrescribe**, no inserta. Si un campo cambia de tamaño, todo lo de detrás se descoloca.

---

## 12. Excel con Apache POI
📄 *PDF, página 11* · 📝 *Laboratorio, bloque 4*

> 💬 **"Libro → hoja → fila → celda, y todo empieza en 0."**

- Un `.xlsx` no es texto, y Java no sabe crearlo solo: se usa la **librería externa** Apache POI (`org.apache.poi:poi-ooxml`). Sin ella, las clases salen en rojo.
- Estructura: `XSSFWorkbook` (libro) → `Sheet` (hoja) → `Row` (fila) → `Cell` (celda). Filas y celdas **desde 0** (la celda 0 es la columna A).
- Una celda guarda **texto**, **número** (como número, para que Excel opere) o **fórmula** (`B2*C2`, **sin** el `=`).
- El **estilo** (negrita, color) se crea aparte y se aplica. Al final se **guarda** con el ciclo abrir → escribir → cerrar.

---

## 13. Resumen

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
