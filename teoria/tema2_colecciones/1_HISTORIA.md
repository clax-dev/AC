# Tema 2: Colecciones, contado como una historia

> **Cómo usar este archivo:**
> - Léelo **de un tirón**, como un cuento. Cada bloque tiene una **💬 frase clave**.
> - Ten abierto `visual.pdf` al lado. La sintaxis está en `2_TECNICO.md`, y para comprobarte tienes `3_PREGUNTAS.md`.
> - **Ejercicio del tema:** `ejercicios/src/tema2_colecciones/ejercicioPlaylist.java` (enunciado en `ENUNCIADO.md`).
> - **Demo:** `teoria/src/t2_colecciones/C01_Colecciones.java`.

**La historia entera en tres líneas:**
- Un array tiene tamaño fijo y solo sabe "posición 0, 1, 2…".
- Las **colecciones** son contenedores que crecen solos, y cada una está pensada para **una forma de usar los datos**.
- Elegir bien la colección es la mitad del ejercicio: en SoundWave (la playlist) usas **las cuatro**.

---

## 1. Cuatro formas de guardar cosas
📄 *PDF, página 2*

> 💬 **"`List` es una fila con orden; `Queue` es una cola del súper; `Map` es una agenda nombre → dato; `Set` es una lista de invitados sin repetidos."**

| Interfaz | Clase | Analogía | Se caracteriza por… |
|---|---|---|---|
| `List` | `ArrayList` | una **fila numerada** | orden, admite repetidos, acceso por índice |
| `Queue` | `LinkedList` | la **cola del súper** | **FIFO**: el primero que entra es el primero que sale |
| `Map<K,V>` | `HashMap` | una **agenda** | clave → valor; claves únicas; búsqueda rapidísima por clave |
| `Set` | `HashSet` | la **lista de invitados** | **sin repetidos**, sin orden |

---

## 2. ¿Cuál elijo? Las pistas del enunciado
📄 *PDF, página 3*

> 💬 **"Busco por ID → `Map`. Necesito un orden → `List`. Turnos → `Queue`. Únicos → `Set`."**

En SoundWave:
- **Buscar una canción por su ID** → `HashMap<Integer, String>`.
- **El orden de la playlist** → `ArrayList<Integer>`.
- **La cola de reproducción** → `LinkedList` usada como `Queue`.
- **Los artistas únicos** → `HashSet<String>`.
- **Contar reproducciones** → otro `HashMap<Integer, Integer>` (id → veces).

**Buena práctica:** declara con la **interfaz** y crea con la **clase**: `List<Integer> orden = new ArrayList<>();`. Así puedes cambiar la clase sin tocar el resto.

---

## 3. Las trampas
📄 *PDF, página 4*

> 💬 **"Los IDs de un `Map` no son posiciones; `List.of` no se puede modificar; `poll` te avisa con `null`."**

- **Los IDs de un `Map` no son índices.** Comprobar `id >= canciones.size()` no sirve para saber si existe; para eso está `containsKey` (te pasó en `buscarCancion`).
- **`List.of(...)` y `Map.of(...)` son inmutables**: `add` o `put` lanzan `UnsupportedOperationException`. Para modificarla: `new ArrayList<>(List.of(...))`.
- **Sacar de una cola:** `poll()` devuelve `null` si está vacía; `remove()` lanza una excepción. `peek()` mira sin sacar.
- **Contar:** `map.merge(id, 1, Integer::sum)` o `getOrDefault(id, 0) + 1`.
- **Ninguna de estas colecciones es segura para hilos** (lo ves en PSP: `ConcurrentLinkedQueue`, `ConcurrentHashMap`).

---

## 4. Resumen

| Necesito… | Colección |
|---|---|
| Orden y acceso por posición | `ArrayList` |
| Turnos FIFO | `LinkedList` como `Queue` |
| Buscar por clave | `HashMap` |
| Sin repetidos | `HashSet` |
| ¿Existe la clave? | `containsKey` |
| Contar apariciones | `merge(k, 1, Integer::sum)` |

**¿Y ahora?** Repasa `visual.pdf`, haz `3_PREGUNTAS.md` y consulta `2_TECNICO.md` cuando programes.
