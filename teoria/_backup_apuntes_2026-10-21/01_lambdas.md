# Tema 1: Expresiones lambda e interfaces funcionales

> **Ejercicio de referencia:** `ejercicios/src/tema1_lambdas/EjercicioIncidenciasTemplate.java`
> **Código de teoría:** `teoria/src/t1_lambdas/` (archivos L01 a L05; ejecuta cada `main`)

---

## 1. ¿Qué es una lambda?

Una lambda es una **función anónima**: no tiene nombre, se guarda en una variable o se pasa como parámetro, y **solo se ejecuta cuando se llama a su método**.

```java
(parámetros) -> expresión
(parámetros) -> { sentencias; return valor; }
```

| Forma | Ejemplo |
|---|---|
| Sin parámetros | `() -> "Hola"` |
| Un parámetro (sin paréntesis) | `inc -> inc.id()` |
| Varios parámetros | `(a, b) -> a + b` |
| Con bloque (necesita `return`) | `inc -> { String d = inc.descripcion().trim(); return d; }` |

**Reglas:**
- Una lambda siempre se asigna a una **interfaz funcional**, es decir, una interfaz con **un único método abstracto**.
- Solo puede usar variables locales que sean **efectivamente finales** (que no cambian después de asignarse).
- Una lambda puede usar otra lambda. En el ejercicio, `auditoria` llama a `resumen.apply(inc)`.

---

## 2. Interfaces funcionales (`java.util.function`)

| Interfaz | Método | Recibe → devuelve | Para qué sirve | En el ejercicio |
|---|---|---|---|---|
| `Function<T,R>` | `apply` | T → R | **Transformar** | `resumen` (Incidencia → String) |
| `Predicate<T>` | `test` | T → boolean | **Validar o filtrar** | `datosValidos`, `abiertas`, `prioridadAlta` |
| `Consumer<T>` | `accept` | T → nada | **Hacer una acción** | `auditoria` |
| `Supplier<T>` | `get` | nada → T | **Generar o crear** | `() -> (int)(Math.random()*2)+1` |
| `UnaryOperator<T>` | `apply` | T → T | Modificar sin cambiar de tipo | `s -> s.trim()` |
| `BiFunction<T,U,R>` | `apply` | T, U → R | Combinar dos valores | `(a, b) -> a + b` |
| `BiConsumer<T,U>` | `accept` | T, U → nada | Recorrer un Map | `map.forEach((k, v) -> ...)` |
| `Comparator<T>` | `compare` | T, T → int | **Ordenar** | `porPrioridad` |
| `Runnable` | `run` | nada → nada | **El trabajo de un hilo** | `Camarero` (asignatura PSP) |

**Truco para elegir:**
- ¿Devuelve un boolean? → `Predicate`
- ¿No devuelve nada? → `Consumer`
- ¿No recibe nada? → `Supplier`
- ¿Recibe algo y devuelve algo? → `Function`

### Combinar lambdas

```java
Predicate<Incidencia> urgentes = datosValidos.and(abiertas).and(prioridadAlta);
Predicate<Incidencia> cerradas = abiertas.negate();
Predicate<Incidencia> x        = prioridadAlta.or(cerradas);

Function<Incidencia,String> r2 = resumen.andThen(String::toUpperCase);
Consumer<Incidencia> c2        = auditoria.andThen(notificar);
```

### Dónde se usan sin streams

```java
lista.forEach(consumer);         // Consumer
lista.removeIf(predicate);       // Predicate
lista.replaceAll(unaryOperator); // UnaryOperator
lista.sort(comparator);          // Comparator
map.forEach((k, v) -> ...);      // BiConsumer
map.merge(k, 1, Integer::sum);   // BiFunction
```

---

## 3. Comparator

`compare(a, b)` devuelve:
- **negativo**: `a` va antes que `b`
- **0**: son iguales
- **positivo**: `a` va después que `b`

```java
// Ascendente
(a, b) -> Integer.compare(a.peso(), b.peso())
// Descendente (se intercambian a y b)
(a, b) -> Integer.compare(b.peso(), a.peso())

// Forma moderna: primero ALTA y, si empatan, el id más pequeño
Comparator.comparingInt((Incidencia i) -> i.prioridad().peso())
          .reversed()
          .thenComparingInt(Incidencia::id);
```

> ⚠ En el ejercicio, el `porPrioridad` manual ordena de **BAJA a ALTA**, al revés de lo que se pide, y le falta el desempate por id. Lo tienes explicado en `L03_Comparator.java`.

---

## 4. Referencias a métodos (`::`)

| Lambda | Referencia |
|---|---|
| `x -> System.out.println(x)` | `System.out::println` |
| `inc -> inc.id()` | `Incidencia::id` |
| `s -> Integer.parseInt(s)` | `Integer::parseInt` |
| `() -> new ArrayList<>()` | `ArrayList::new` |

---

## 5. Java moderno que aparece en el ejercicio

- **`record`**: una clase inmutable. Genera el constructor, los getters **sin `get`** (`inc.id()`), `equals`, `hashCode` y `toString`.
  - ⚠ Desde fuera del record se accede con `inc.usuario()`. Escribir `inc.usuario` (el campo directo) solo funciona porque el código está dentro de la misma clase exterior.
- **`enum` con atributos**: `ALTA(3)` llama al constructor `Prioridad(int peso)`.
- **`switch` con flecha**: no lleva `break` y puede devolver un valor (`return switch (...) { case ... -> ...; }`).
- **Interfaz genérica** `FuenteDatos<T>`: el mismo contrato sirve para cualquier tipo de dato.
- **Patrón Factory** (`FuenteDatosFactory.crear("incidencias")`): un método decide qué clase concreta crear, y quien lo llama solo conoce la interfaz.

Todo esto lo tienes con ejemplos en `L05_RecordEnumSwitchFactory.java`.
