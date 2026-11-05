# Tema 3: Streams y Optional, contado como una historia

> **Cómo usar este archivo:**
> - Léelo **de un tirón**, como un cuento. Cada bloque tiene una **💬 frase clave**.
> - Ten abierto `visual.pdf` al lado. La sintaxis está en `2_TECNICO.md`, y para comprobarte tienes `3_PREGUNTAS.md`.
> - **Ejercicio del tema:** `ejercicios/src/tema3_streams/EjerciciosStreamsOptional.java`. También los usaste en BioDAM (bloque 3, "cero bucles").
> - **Demo:** `teoria/src/t3_streams/S01_StreamsOptional.java`.

**La historia entera en tres líneas:**
- Con un `for` le dices a Java **cómo** recorrer la lista, paso a paso.
- Con un **stream** le dices **qué** quieres: "quédate con estos, transfórmalos así, ordénalos y dámelos en una lista".
- Es una **cadena de montaje** donde cada puesto es una lambda del tema 1.

---

## 1. El stream: una cadena de montaje
📄 *PDF, página 2*

> 💬 **"Origen → puestos intermedios → un puesto final. Sin puesto final, la cadena no arranca."**

```
lista.stream()  →  filter  →  map  →  sorted  →  toList()
   origen          intermedias (devuelven otro stream)   final (da el resultado)
```

- **Origen:** `lista.stream()`, `Files.lines(ruta)` (tema 4)…
- **Intermedias:** `filter` (el portero: `Predicate`), `map` (la máquina: `Function`), `sorted` (el juez: `Comparator`), `distinct`, `skip`, `limit`. Devuelven **otro stream**, así que se encadenan.
- **Final:** `toList`, `forEach`, `count`, `findFirst`, `anyMatch`, `average`… **Hasta que no hay una final, no se ejecuta nada.**
- **No modifica** la lista original: produce un resultado nuevo.
- **Un stream solo se usa una vez.** Si necesitas otro cálculo, haz otro `lista.stream()`.

---

## 2. Números: `mapToDouble` y la media
📄 *PDF, página 3*

> 💬 **"Para sumar o hacer la media, primero pasa a un stream de números con `mapToDouble` o `mapToInt`."**

- `productos.stream().mapToDouble(Producto::precio).average()` devuelve un **`OptionalDouble`**, porque si la lista está vacía **no hay media**.
- `sum()`, `max()`, `min()` y `average()` solo existen en los streams numéricos.
- `count()` devuelve **`long`**.

---

## 3. `Optional`: una caja que puede estar vacía
📄 *PDF, página 3*

> 💬 **"En vez de devolver `null`, devuelvo una caja. Antes de usar lo de dentro, miro si hay algo."**

- `findFirst()` no devuelve `null`: devuelve un `Optional<T>`.
- `isPresent()` (¿hay algo?), `orElse(otro)` (lo de dentro o uno por defecto), `ifPresent(lambda)` (haz esto solo si hay algo), `map` (transforma lo de dentro si existe).
- Así se acaban los `NullPointerException` por olvidar comprobar.

---

## 4. Las trampas
📄 *PDF, página 4*

> 💬 **"`equals` distingue mayúsculas y tildes; el parámetro de la lambda va en singular; `count` es `long`."**

- `"Informática"` con tilde y `"Diabetes"` con la misma mayúscula que en los datos: si no coincide exacto, el `filter` no deja pasar nada y la media sale vacía.
- `equals` es exacto; `contains` es más flojo (`"Prediabetes"` contiene `"diabetes"`).
- El parámetro de la lambda es **un** elemento: `linea -> …`, no `lines -> …`.

---

## 5. Resumen

| Concepto | Frase clave |
|---|---|
| Stream | Cadena de montaje: origen → intermedias → final |
| Sin final | No se ejecuta nada |
| Lista original | No cambia |
| Un solo uso | Para otro cálculo, otro `stream()` |
| `filter` / `map` / `sorted` | Portero / máquina / juez |
| Media | `mapToDouble(...).average()` → `OptionalDouble` |
| `Optional` | Caja que puede estar vacía; nunca `null` |

**¿Y ahora?** Repasa `visual.pdf`, haz `3_PREGUNTAS.md` y consulta `2_TECNICO.md` cuando programes.
