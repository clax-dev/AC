# Tema 3: Streams y Optional, preguntas para comprobarte

> **Cómo usar este archivo:**
> - Lee la pregunta, **contesta en voz alta** y solo después abre la respuesta.
> - Marca con ✅ las que te salen. Las de ⭐ son las más preguntadas o en las que más te has equivocado.

## Progreso
- [ ] Bloque 1: streams (1-8)
- [ ] Bloque 2: números y Optional (9-14)

---

## Bloque 1: Streams

### 1. ⭐ ¿Qué es un stream?
<details><summary>Respuesta</summary>

Una **cadena de operaciones** sobre una colección: origen → operaciones intermedias → una operación final. Dices **qué** quieres, no **cómo** recorrer.
</details>

### 2. ⭐ ¿Qué pasa si escribo `lista.stream().filter(...).map(...)` sin nada más?
<details><summary>Respuesta</summary>

**No se ejecuta nada**: falta la operación **final** (`toList`, `forEach`, `count`…).
</details>

### 3. ⭐ ¿Un stream modifica la lista original?
<details><summary>Respuesta</summary>

No. Produce un resultado nuevo.
</details>

### 4. ⭐ ¿Puedo usar el mismo stream dos veces?
<details><summary>Respuesta</summary>

No: después de la operación final queda **gastado** (`IllegalStateException`). Para otro cálculo, otro `lista.stream()`. Por eso en BioDAM se lee el CSV a una `List` una vez.
</details>

### 5. ¿Qué interfaz funcional recibe `filter`? ¿Y `map`? ¿Y `sorted`?
<details><summary>Respuesta</summary>

`Predicate`, `Function` y `Comparator`.
</details>

### 6. ¿Para qué sirven `skip(1)` y `distinct()`?
<details><summary>Respuesta</summary>

`skip(1)` se salta el primer elemento (la cabecera de un CSV). `distinct()` quita los repetidos.
</details>

### 7. ⭐ ¿Qué tipo devuelve `count()`?
<details><summary>Respuesta</summary>

**`long`**. Guardarlo en un `int` no compila sin cast.
</details>

### 8. Un `filter(p -> p.categoria().equals("Informatica"))` no deja pasar nada. ¿Qué miro?
<details><summary>Respuesta</summary>

Que el texto sea **exacto**: en los datos es `"Informática"`, con tilde. `equals` distingue tildes y mayúsculas.
</details>

---

## Bloque 2: Números y Optional

### 9. ⭐ ¿Cómo calculo el precio medio de una lista de productos?
<details><summary>Respuesta</summary>

`productos.stream().mapToDouble(Producto::precio).average()`, que devuelve un `OptionalDouble`.
</details>

### 10. ¿Por qué `average()` devuelve `OptionalDouble` y no `double`?
<details><summary>Respuesta</summary>

Porque si el stream está vacío **no hay media**. La caja puede estar vacía.
</details>

### 11. ⭐ ¿Qué es un `Optional` y para qué sirve?
<details><summary>Respuesta</summary>

Una **caja que puede estar vacía**. Sirve para no devolver `null` y obligar a pensar en el caso "no hay nada".
</details>

### 12. ⭐ ¿Qué devuelve `findFirst()`?
<details><summary>Respuesta</summary>

Un `Optional<T>`. Nunca `null`.
</details>

### 13. ¿Qué diferencia hay entre `orElse`, `ifPresent` y `get` en un `Optional`?
<details><summary>Respuesta</summary>

`orElse(x)` da el valor o `x`. `ifPresent(lambda)` ejecuta la lambda solo si hay valor. `get()` da el valor, pero si está vacío lanza `NoSuchElementException`.
</details>

### 14. ¿Qué hace `Optional.map`?
<details><summary>Respuesta</summary>

Transforma lo de dentro **si existe**: `Optional<Producto>` → `p.map(Producto::nombre)` → `Optional<String>`. Si estaba vacío, sigue vacío.
</details>
