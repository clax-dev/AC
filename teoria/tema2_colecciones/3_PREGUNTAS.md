# Tema 2: Colecciones, preguntas para comprobarte

> **Cómo usar este archivo:**
> - Lee la pregunta, **contesta en voz alta** y solo después abre la respuesta.
> - Marca con ✅ las que te salen. Las de ⭐ son las más preguntadas o en las que más te has equivocado.

## Progreso
- [ ] Bloque 1: las cuatro colecciones (1-5)
- [ ] Bloque 2: métodos y trampas (6-12)

---

## Bloque 1: Las cuatro colecciones

### 1. ⭐ ¿Qué diferencia hay entre `List`, `Queue`, `Map` y `Set`?
<details><summary>Respuesta</summary>

`List`: orden, repetidos y acceso por índice. `Queue`: FIFO (el primero que entra sale primero). `Map`: clave → valor, claves únicas. `Set`: sin repetidos y sin orden.
</details>

### 2. ⭐ En SoundWave, ¿qué colección usas para buscar una canción por su ID?
<details><summary>Respuesta</summary>

`HashMap<Integer, String>`: busca por clave al instante.
</details>

### 3. ¿Y para los artistas únicos? ¿Y para la cola de reproducción?
<details><summary>Respuesta</summary>

`HashSet<String>` para los artistas (no repite). `LinkedList` usada como `Queue` para la cola.
</details>

### 4. ¿Qué significa FIFO?
<details><summary>Respuesta</summary>

*First In, First Out*: el primero que entra es el primero que sale, como la cola del súper.
</details>

### 5. ¿Por qué se escribe `List<Integer> l = new ArrayList<>();` y no `ArrayList<Integer> l = …`?
<details><summary>Respuesta</summary>

Porque declarar con la **interfaz** permite cambiar la clase concreta sin tocar el resto del código.
</details>

---

## Bloque 2: Métodos y trampas

### 6. ⭐ ¿Qué diferencia hay entre `poll()` y `remove()` en una cola vacía?
<details><summary>Respuesta</summary>

`poll()` devuelve **`null`**; `remove()` lanza `NoSuchElementException`. `peek()` mira el primero sin sacarlo.
</details>

### 7. ⭐ ¿Por qué `if (id >= canciones.size())` no sirve para saber si existe un ID en un `Map`?
<details><summary>Respuesta</summary>

Porque las claves de un `Map` **no son posiciones**: puedes tener las claves 1, 7 y 300 con `size() = 3`. Se usa `containsKey(id)`.
</details>

### 8. ¿Qué devuelve `map.get(clave)` si la clave no existe?
<details><summary>Respuesta</summary>

`null`. Con `getOrDefault(clave, valorPorDefecto)` eliges qué devolver.
</details>

### 9. ⭐ ¿Cómo cuento las reproducciones de cada canción?
<details><summary>Respuesta</summary>

`reproducciones.merge(id, 1, Integer::sum)`, o `reproducciones.put(id, reproducciones.getOrDefault(id, 0) + 1)`.
</details>

### 10. ⭐ ¿Qué pasa con `List.of("a").add("b")`?
<details><summary>Respuesta</summary>

`UnsupportedOperationException`: `List.of` crea una lista **inmutable**. Para poder modificarla: `new ArrayList<>(List.of(...))`.
</details>

### 11. ¿Qué devuelve `set.add(x)` si `x` ya estaba?
<details><summary>Respuesta</summary>

`false`, y no lo añade otra vez.
</details>

### 12. ¿Cómo recorro un `Map` con sus claves y valores?
<details><summary>Respuesta</summary>

`for (Map.Entry<K, V> e : map.entrySet()) { e.getKey(); e.getValue(); }` o `map.forEach((k, v) -> ...)`.
</details>
