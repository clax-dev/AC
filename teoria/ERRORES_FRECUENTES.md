# Errores frecuentes (sacados de tus ejercicios de AC)

Revisado por última vez el 14 de octubre. Los ✅ ya los has corregido tú.

| # | Archivo | Problema | Pista |
|---|---|---|---|
| ✅ | `tema1_lambdas/EjercicioIncidenciasTemplate.java` | ~~`abiertas` devolvía las resueltas~~. Ya está corregido con `!estado.resuelta`. | — |
| 1 | Mismo archivo | `crearIncidencias` solo mira la prioridad: **no usa** `datosValidos` ni `abiertas`, así que en "abiertas de prioridad alta" salen también las resueltas. | Pásale un `Predicate` al método, o mira `EjercicioIncidenciasTemplateOptimizado.java`. |
| 2 | Mismo archivo | El `Comparator porPrioridad` ordena de BAJA a ALTA, y el enunciado pide ALTA primero. Además le falta el desempate por id. | `teoria/src/t1_lambdas/L03_Comparator.java` |
| 3 | Mismo archivo | `isEmpty()` da por válido un texto que solo tiene espacios. | `isBlank()` |
| 4 | `tema2_colecciones/ejercicioPlaylist.java` | `buscarCancion` comprueba `id >= canciones.size()`, pero los IDs de un Map no son índices. | Con `containsKey` basta. |
| 5 | Mismo archivo | `ordenPlaylist` nunca se llena, así que la opción 1 no muestra nada. Solo se carga una canción. | Rellena el Map y la List en la carga. |
| 6 | Mismo archivo | `mostrarArtistas` y `mostrarMasReproducida` siguen vacíos (TODO). | `teoria/src/t2_colecciones/C01_Colecciones.java` |
| 7 | `tema3_streams/EjerciciosStreamsOptional.java` | En el ejercicio 2, `"Informática"` tiene que llevar **tilde** para coincidir con los datos. Además, `equals` es más exacto que `contains`. | — |

---

## Tarea semanal T01 BioDAM (14 de octubre)

Todos están corregidos ✅ en la versión entregada. Están aquí para que no se repitan.

### Métodos y datos (los que más tiempo costaron)

| # | Problema | Regla para que no vuelva a pasar |
|---|---|---|
| 8 | `ruta.resolve(nombre);` sin guardar el resultado: la ruta no cambiaba (pasó 3 veces). | **Mira el tipo de retorno** con `Ctrl+P` o `Ctrl+Q`. Si no es `void`, el método **te entrega algo** y tienes que recogerlo: `Path nueva = ruta.resolve(nombre);`. Pasa lo mismo con `String` (`toUpperCase`, `trim`…) y `LocalDateTime` (`format`). |
| 9 | Guardar el resultado de `resolve` **en la misma variable** de la carpeta: `createDirectories` creó una *carpeta* llamada `auditoria_….log`. | **Una variable por cosa**: `carpetaLogs` para la carpeta y `archivoLog` para el fichero. Que el nombre diga lo que es. |
| 10 | Declarar el atributo `private static` **dentro** de un método. | Los atributos van dentro de la clase pero **fuera de todos los métodos**. |
| 11 | `Path fileLog = …` dentro de un método cuando ya existía el atributo `fileLog`: se crea una variable local nueva y el atributo se queda en `null`. | Para dar valor a un atributo, **sin el tipo delante**: `fileLog = …`. |
| 12 | `nivel` como atributo para pasar `"INFO"`/`"WARN"` a `writeLog`: valía `null` y en el log salía `null`. | **Lo que cambia en cada llamada es un parámetro**, no un atributo: `writeLog("WARN", "…")`. El atributo es para lo que es siempre igual y usan muchos métodos (`fileLog`). |
| 13 | No saber "qué lista" recibe un método: se pensaba que la lista se quedaba **dentro** de `csvIntoList`. | El `return` **saca** la lista del método. El `main` la guarda (`List<String> filas = csvIntoList(ruta);`) y se la pasa al siguiente (`countElements(filas)`). Como en mates: `y = f(x)` → `g(y)`. |
| 14 | Confundir el **log** (`fileLog`, donde se escribe) con el **CSV** (`rutaValidada`, lo que se lee). Se llegó a pasar el log a `csvIntoList`. | Antes de pasar una ruta, pregúntate: **¿este es el fichero que leo o el que escribo?** |
| 15 | Un método `void` que debía devolver la lista: los datos se perdían al terminar. | Si otro método necesita el resultado, el método **devuelve** algo: cambia `void` por el tipo (`List<String>`). |

### Ficheros y fechas

| # | Problema | Regla |
|---|---|---|
| 16 | Pegar el molde en el String (`"auditoria_" + f`) en vez de la fecha formateada: salía `Value(DayOfMonth,2)…` y `InvalidPathException` por el `:`. | El molde se **aplica** a la fecha: `fecha.format(molde)`. |
| 17 | Patrón `dd/MM/yyyy HH:mm:ss` para un nombre de fichero: `/` crea carpetas y `:` no se permite en Windows. | Para nombres de fichero, solo dígitos: `yyyyMMddHHmmss`. |
| 18 | `LocalDateTime.now()` en un atributo: todas las líneas del log tenían la hora del arranque. | El `now()` va **dentro** del método que lo necesita, para que coja la hora de ese momento. |
| 19 | `createFile` dentro de `writeLog` y `CREATE_NEW`: revienta en la segunda línea porque el fichero ya existe. | Para logs: `writeString(ruta, texto, CREATE, APPEND)`. Crea la primera vez y añade al final las siguientes. |
| 20 | `Files.write(ruta, String, …)` no compila. | Para un `String`: `writeString`. `write` es para listas o bytes. |
| 21 | Las líneas del log salían pegadas. | `writeString` no añade el salto: `+ System.lineSeparator()`. |
| 22 | `toFile().exists()` (clase antigua `File`). | La moderna, NIO: `Files.exists(ruta)`. |
| 23 | `folder.resolve(rutaAbsoluta)` devolvía la ruta original: `resolve` ignora la carpeta si le pasas una ruta completa. | Para meter un fichero en otra carpeta: `carpeta.resolve(ruta.getFileName())`. |
| 24 | `Files.move(destino, origen)`: los argumentos al revés. | Siempre `move(DESDE, HASTA, opciones)`. |

### Seguridad

| # | Problema | Regla |
|---|---|---|
| 25 | `.normalize().toAbsolutePath()`: los `..` iniciales no se resolvían. | Primero `toAbsolutePath()` y **luego** `normalize()`, en las **dos** rutas. |
| 26 | Pasar como base el `fileLog` en vez de `Paths.get("datos")`: todo daba `WARN`. | La base es la **carpeta permitida**. |
| 27 | Probar el ataque con `datos/essd/ppp`: está **dentro** de `datos/`, así que da `ERROR`, no `WARN`. | Un path traversal tiene que **salirse** de la carpeta: `datos/../../etc/passwd`. |
| 28 | Tres llamadas seguidas a `validatePath` para probar: solo salía la primera. | `throw` y `System.exit` **paran el programa**. Una prueba por ejecución. |
| 29 | `throw new SecurityException()` sin mensaje: en consola no se sabía qué había pasado. | Pon siempre un mensaje: `new SecurityException("… " + ruta)`. |

### Streams, lambdas e IntelliJ

| # | Problema | Regla |
|---|---|---|
| 30 | `JList` (componente gráfico de Swing) en vez de `List`, más un *cast* que propuso IntelliJ. Compila, pero revienta al ejecutar. | Al autocompletar, **mira el paquete** (`java.util`, no `javax.swing`). Si IntelliJ te ofrece un *cast* para quitar un error, el tipo está mal. |
| 31 | `Consumer<String>` para la lambda del `map`. | ¿Devuelve `true`/`false`? → `Predicate`. ¿Devuelve **otra cosa**? → `Function`. ¿No devuelve **nada**? → `Consumer`. |
| 32 | Parámetro de la lambda con el mismo nombre que el del método (`lines -> …` dentro de un método que recibe `lines`). | El parámetro de la lambda es **un** elemento: nombre en singular (`linea`). |
| 33 | `"DIABETES"` en vez de `"Diabetes"`: el filtro no dejaba pasar nada y la media salía 0.0. | `equals` distingue mayúsculas. Copia el texto **exacto** de los datos. |
| 34 | `count()` en un método `int`. | `count()` devuelve `long`. |
| 35 | IntelliJ pisó los cambios hechos desde fuera con su versión en memoria. | Si IntelliJ pregunta, elige **"Load File System Changes"**. |
