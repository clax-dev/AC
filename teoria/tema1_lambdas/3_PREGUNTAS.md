# Tema 1: Lambdas, preguntas para comprobarte

> **Cómo usar este archivo:**
> - Lee la pregunta, **contesta en voz alta** (o en un papel) y solo después abre la respuesta.
> - Marca con ✅ las que te salen y repite las demás otro día.
> - Las de ⭐ son las que más se preguntan, o en las que más te has equivocado tú.

## Progreso
- [ ] Bloque 1: qué es una lambda (1-4)
- [ ] Bloque 2: interfaces funcionales (5-10)
- [ ] Bloque 3: Comparator y referencias (11-14)
- [ ] Bloque 4: Java moderno (15-17)

---

## Bloque 1: Qué es una lambda

### 1. ⭐ ¿Qué es una lambda?
<details><summary>Respuesta</summary>

Una **función anónima** (sin nombre) que se guarda en una variable o se pasa a un método. Se escribe `(parámetros) -> resultado`.
</details>

### 2. ⭐ Escribo `Predicate<Incidencia> abiertas = inc -> !inc.resuelta();`. ¿Se ejecuta en ese momento?
<details><summary>Respuesta</summary>

No. Solo se **guarda**. Se ejecuta cuando alguien llama a su método: `abiertas.test(inc)`, o cuando se la pasas a `filter`, `removeIf`…
</details>

### 3. ¿Cuándo hacen falta las llaves y el `return`?
<details><summary>Respuesta</summary>

Cuando el cuerpo tiene **más de una sentencia**: `inc -> { String d = inc.descripcion().trim(); return d; }`.
</details>

### 4. ¿Qué variables de fuera puede usar una lambda?
<details><summary>Respuesta</summary>

Solo las **efectivamente finales**: las que no cambian después de asignarse.
</details>

---

## Bloque 2: Interfaces funcionales

### 5. ⭐ ¿Qué es una interfaz funcional?
<details><summary>Respuesta</summary>

Una interfaz con **un único método abstracto**. Es el "tipo" en el que se guarda una lambda.
</details>

### 6. ⭐ Une cada interfaz con su método: `Predicate`, `Function`, `Consumer`, `Supplier`, `Comparator`.
<details><summary>Respuesta</summary>

`test`, `apply`, `accept`, `get` y `compare`.
</details>

### 7. ⭐ Una lambda recibe una línea de texto y devuelve su longitud. ¿Qué interfaz uso?
<details><summary>Respuesta</summary>

`Function<String, Integer>`: recibe algo y devuelve otra cosa. Si devolviera `boolean` sería `Predicate`; si no devolviera nada, `Consumer`.
</details>

### 8. ¿Y una que no recibe nada y devuelve un número aleatorio?
<details><summary>Respuesta</summary>

`Supplier<Integer>`, con su método `get()`.
</details>

### 9. ¿Cómo combino dos `Predicate`? ¿Y cómo los niego?
<details><summary>Respuesta</summary>

`p1.and(p2)`, `p1.or(p2)` y `p1.negate()`. Las `Function` y los `Consumer` se encadenan con `andThen`.
</details>

### 10. ¿Qué diferencia hay entre `filtrar(lista, abiertas)` y `filtrar(lista, abiertas())`?
<details><summary>Respuesta</summary>

La primera **pasa la lambda** (correcto). La segunda intenta **llamar** a un método `abiertas()`, que no existe: para pasar una lambda va solo el nombre, sin `()`.
</details>

---

## Bloque 3: Comparator y referencias

### 11. ⭐ ¿Qué significa que `compare(a, b)` devuelva un número negativo?
<details><summary>Respuesta</summary>

Que `a` va **antes** que `b`. Positivo: después. Cero: empate.
</details>

### 12. ⭐ ¿Cómo ordeno de mayor a menor prioridad y, si empatan, por id?
<details><summary>Respuesta</summary>

```java
Comparator.comparingInt((Incidencia i) -> i.prioridad().peso()).reversed().thenComparingInt(Incidencia::id)
```
</details>

### 13. Convierte en referencia a método: `x -> System.out.println(x)` y `s -> Integer.parseInt(s)`.
<details><summary>Respuesta</summary>

`System.out::println` e `Integer::parseInt`.
</details>

### 14. ¿Cuándo se puede usar `::`?
<details><summary>Respuesta</summary>

Cuando la lambda **solo llama a un método que ya existe**, pasándole lo mismo que recibe.
</details>

---

## Bloque 4: Java moderno

### 15. ⭐ ¿Qué genera un `record`? ¿Cómo se llama su getter?
<details><summary>Respuesta</summary>

Constructor, getters, `equals`, `hashCode` y `toString`. El getter se llama **igual que el campo, sin `get`**: `inc.id()`. Es inmutable.
</details>

### 16. ¿Qué ventaja tiene el `switch` con flecha?
<details><summary>Respuesta</summary>

No necesita `break` (no "se cae" al siguiente caso) y puede **devolver un valor**.
</details>

### 17. ¿Qué es el patrón Factory?
<details><summary>Respuesta</summary>

Un método que **decide qué clase concreta crear** (`FuenteDatosFactory.crear("incidencias")`). Quien lo llama solo conoce la **interfaz**.
</details>
