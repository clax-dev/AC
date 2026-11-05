# Tema 1: Lambdas, contado como una historia

> **Cómo usar este archivo:**
> - Léelo **de un tirón**, como un cuento. Aquí no hay casi sintaxis: solo **qué es cada cosa** y **qué problema resuelve**.
> - Cada bloque tiene una **💬 frase clave**. Si te la sabes, sabes el bloque.
> - Ten abierto `visual.pdf` al lado: cada bloque te dice qué página mirar.
> - La sintaxis está en `2_TECNICO.md`, y para comprobarte tienes `3_PREGUNTAS.md`.
> - **Ejercicio del tema:** `ejercicios/src/tema1_lambdas/EjercicioIncidenciasTemplate.java` (y la versión `…Optimizado`).
> - **Demos:** `teoria/src/t1_lambdas/` (L01 a L05).

**La historia entera en tres líneas:**
- Hasta ahora, a un método le pasabas **datos** (un número, un texto, una lista).
- Con las lambdas le puedes pasar **un trozo de comportamiento**: "filtra **así**", "ordena **así**", "haz **esto** con cada uno".
- Es como dejar **una nota con instrucciones** para que otro la use cuando le toque.

---

## 1. ¿Qué es una lambda? Una nota con instrucciones
📄 *PDF, página 2* · 🧪 *Demo `L01_SintaxisLambda`*

> 💬 **"Una lambda es una función sin nombre que se guarda en una variable o se pasa a otro método, y no se ejecuta hasta que alguien la llama."**

- Se escribe `(parámetros) -> resultado`. Por ejemplo, `inc -> inc.id()` significa "dame una incidencia y te devuelvo su id".
- **No se ejecuta al escribirla**: es una nota guardada. Se ejecuta cuando alguien **llama a su método** (`test`, `apply`, `accept`…).
- Si el cuerpo tiene varias líneas, va entre `{ }` y necesita `return`.
- Solo puede usar variables de fuera que **no cambien** después (*efectivamente finales*).

---

## 2. El tipo de la nota: las interfaces funcionales
📄 *PDF, página 3* · 🧪 *Demo `L02_InterfacesFuncionales`*

> 💬 **"`Predicate` decide sí o no, `Function` transforma, `Consumer` hace algo, `Supplier` fabrica y `Comparator` decide quién va antes."**

Una lambda siempre se guarda en una **interfaz funcional**: una interfaz con **un único método abstracto**. Cada una es un tipo de encargo distinto:

| Interfaz | Encargo | Método | Ejemplo del ejercicio |
|---|---|---|---|
| `Predicate<T>` | el **portero**: ¿pasa o no pasa? | `test` → `boolean` | `datosValidos`, `abiertas` |
| `Function<T,R>` | la **máquina**: entra una cosa, sale otra | `apply` → `R` | `resumen` (Incidencia → String) |
| `Consumer<T>` | el **operario**: hace algo y no devuelve nada | `accept` | `auditoria` |
| `Supplier<T>` | la **fábrica**: no recibe nada y te da algo | `get` | un número aleatorio |
| `Comparator<T>` | el **juez**: ¿quién va antes? | `compare` → `int` | `porPrioridad` |

**Truco para elegir:** ¿devuelve `boolean`? → `Predicate`. ¿No devuelve nada? → `Consumer`. ¿No recibe nada? → `Supplier`. ¿Recibe y devuelve algo? → `Function`. (En BioDAM pusiste un `Consumer` donde iba una `Function`.)

**Las notas se combinan:** `datosValidos.and(abiertas)`, `abiertas.negate()`, `resumen.andThen(String::toUpperCase)`.

---

## 3. El juez: `Comparator`
📄 *PDF, página 4* · 🧪 *Demo `L03_Comparator`*

> 💬 **"`compare(a, b)` negativo = `a` va antes; positivo = `a` va después; 0 = empate."**

- Para ordenar de mayor a menor se **intercambian** `a` y `b`, o se añade `.reversed()`.
- La forma moderna se lee casi como una frase: `Comparator.comparingInt(...).reversed().thenComparingInt(Incidencia::id)` = "por prioridad, de mayor a menor, y si empatan, por id".
- En tu ejercicio, `porPrioridad` ordenaba al revés (de BAJA a ALTA) y le faltaba el desempate.

---

## 4. Atajos: referencias a métodos (`::`)
📄 *PDF, página 4* · 🧪 *Demo `L04_ReferenciasMetodo`*

> 💬 **"Si la lambda solo llama a un método que ya existe, escribe solo su nombre: `Clase::metodo`."**

- `x -> System.out.println(x)` → `System.out::println`
- `inc -> inc.id()` → `Incidencia::id`
- `s -> Integer.parseInt(s)` → `Integer::parseInt`
- `() -> new ArrayList<>()` → `ArrayList::new`

---

## 5. Java moderno del ejercicio
📄 *PDF, página 5* · 🧪 *Demo `L05_RecordEnumSwitchFactory`*

> 💬 **"`record` = clase de datos en una línea; `enum` = lista cerrada de valores; `switch` con flecha = sin `break`; Factory = un método que decide qué objeto crear."**

- **`record`**: genera constructor, getters **sin `get`** (`inc.id()`), `equals`, `hashCode` y `toString`. Es **inmutable**.
- **`enum` con atributos**: `ALTA(3)` llama al constructor `Prioridad(int peso)`.
- **`switch` con flecha**: no lleva `break` y puede devolver un valor.
- **Interfaz genérica** `FuenteDatos<T>`: el mismo contrato para cualquier tipo. En el tema 4 (`F03`) se implementa de verdad con un CSV.
- **Factory** (`FuenteDatosFactory.crear("incidencias")`): quien llama solo conoce la interfaz.

---

## 6. Resumen

| Concepto | Frase clave |
|---|---|
| Lambda | Nota con instrucciones; no se ejecuta hasta que alguien la llama |
| Interfaz funcional | Un solo método abstracto |
| `Predicate` / `Function` / `Consumer` / `Supplier` | Portero / máquina / operario / fábrica |
| Combinar | `and`, `or`, `negate`, `andThen` |
| `Comparator` | Negativo = antes · `reversed()` · `thenComparing` |
| `::` | Atajo cuando la lambda solo llama a un método |
| `record` | Datos inmutables, getters sin `get` |

**¿Y ahora?** Repasa `visual.pdf`, haz `3_PREGUNTAS.md` y consulta `2_TECNICO.md` cuando programes.
