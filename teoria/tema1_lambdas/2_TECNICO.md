# Tema 1: Lambdas, la parte técnica (para consultar)

> **Este archivo no se lee de un tirón: se consulta.** Cada herramienta tiene **plantilla**, **métodos**, **⚠ reglas** y **errores típicos**.
> Si no entiendes **para qué sirve** algo, vuelve a `1_HISTORIA.md`. Métodos sueltos: `../GUIA_LIBRERIAS.md` (apartado 5).

**Índice:**
1. [Sintaxis](#1-sintaxis)
2. [Interfaces funcionales](#2-interfaces-funcionales)
3. [Combinar lambdas](#3-combinar-lambdas)
4. [Dónde se usan sin streams](#4-dónde-se-usan-sin-streams)
5. [`Comparator`](#5-comparator)
6. [Referencias a métodos](#6-referencias-a-métodos)
7. [`record`, `enum`, `switch`, Factory](#7-record-enum-switch-factory)
8. [Imports](#8-imports)

---

## 1. Sintaxis
🧪 `L01_SintaxisLambda`

| Forma | Ejemplo |
|---|---|
| Sin parámetros | `() -> "Hola"` |
| Un parámetro (sin paréntesis) | `inc -> inc.id()` |
| Varios parámetros | `(a, b) -> a + b` |
| Con bloque (lleva `return`) | `inc -> { String d = inc.descripcion().trim(); return d; }` |

### ⚠ Reglas
- Siempre se asigna a una **interfaz funcional** (un solo método abstracto).
- Las variables locales de fuera tienen que ser **efectivamente finales**.
- El parámetro de la lambda es **un** elemento: nombre en **singular** (`linea`, no `lines`).

---

## 2. Interfaces funcionales
🧪 `L02_InterfacesFuncionales` · `java.util.function`

### Plantilla
```java
Predicate<Incidencia> abiertas = inc -> !inc.resuelta();
Function<Incidencia, String> resumen = inc -> "#" + inc.id() + " " + inc.usuario();
Consumer<Incidencia> auditoria = inc -> System.out.println("[AUDIT] " + resumen.apply(inc));
Supplier<Integer> aleatorio = () -> (int) (Math.random() * 2) + 1;

abiertas.test(inc);       // true / false
resumen.apply(inc);       // String
auditoria.accept(inc);    // no devuelve nada
aleatorio.get();          // Integer
```

| Interfaz | Método | Recibe → devuelve | Para qué |
|---|---|---|---|
| `Predicate<T>` | `test` | T → `boolean` | Filtrar, validar |
| `Function<T,R>` | `apply` | T → R | Transformar |
| `Consumer<T>` | `accept` | T → nada | Hacer una acción |
| `Supplier<T>` | `get` | nada → T | Generar |
| `UnaryOperator<T>` | `apply` | T → T | Modificar sin cambiar de tipo |
| `BiFunction<T,U,R>` | `apply` | T, U → R | Combinar dos valores |
| `BiConsumer<T,U>` | `accept` | T, U → nada | Recorrer un `Map` |
| `Comparator<T>` | `compare` | T, T → `int` | Ordenar |
| `Runnable` | `run` | nada → nada | El trabajo de un hilo (PSP) |

### ⚠ Reglas
- Para **llamar** a una lambda: `nombre.metodo(...)`. Para **pasarla** a otro método: solo el nombre, **sin `()`**.

---

## 3. Combinar lambdas
```java
Predicate<Incidencia> urgentes = datosValidos.and(abiertas).and(prioridadAlta);
Predicate<Incidencia> cerradas = abiertas.negate();
Predicate<Incidencia> x        = prioridadAlta.or(cerradas);
Function<Incidencia, String> r2 = resumen.andThen(String::toUpperCase);
Consumer<Incidencia> c2         = auditoria.andThen(notificar);
```

---

## 4. Dónde se usan sin streams
```java
lista.forEach(consumer);          // Consumer
lista.removeIf(predicate);        // Predicate
lista.replaceAll(unaryOperator);  // UnaryOperator
lista.sort(comparator);           // Comparator
map.forEach((k, v) -> ...);       // BiConsumer
map.merge(k, 1, Integer::sum);    // BiFunction
```

---

## 5. `Comparator`
🧪 `L03_Comparator`

### Plantilla
```java
// A mano
Comparator<Incidencia> asc  = (a, b) -> Integer.compare(a.prioridad().peso(), b.prioridad().peso());
Comparator<Incidencia> desc = (a, b) -> Integer.compare(b.prioridad().peso(), a.prioridad().peso());

// Moderna: ALTA primero y, si empatan, el id más pequeño
Comparator<Incidencia> porPrioridad = Comparator
        .comparingInt((Incidencia i) -> i.prioridad().peso())
        .reversed()
        .thenComparingInt(Incidencia::id);

lista.sort(porPrioridad);
```

| `compare(a, b)` devuelve | Significa |
|---|---|
| negativo | `a` va **antes** |
| 0 | empate |
| positivo | `a` va **después** |

| Método | Qué hace |
|---|---|
| `Comparator.comparing(f)` / `comparingInt` / `comparingDouble` | Ordena por ese campo |
| `.reversed()` | Al revés |
| `.thenComparing(...)` | Desempate |
| `Comparator.naturalOrder()` / `reverseOrder()` | Orden natural |

### ⚠ Reglas
- No restes (`a - b`) para comparar: puede desbordar. Usa `Integer.compare`.

---

## 6. Referencias a métodos
🧪 `L04_ReferenciasMetodo`

| Lambda | Referencia | Tipo |
|---|---|---|
| `x -> System.out.println(x)` | `System.out::println` | método de un objeto |
| `inc -> inc.id()` | `Incidencia::id` | método del propio parámetro |
| `s -> Integer.parseInt(s)` | `Integer::parseInt` | método `static` |
| `() -> new ArrayList<>()` | `ArrayList::new` | constructor |

---

## 7. `record`, `enum`, `switch`, Factory
🧪 `L05_RecordEnumSwitchFactory`

```java
record Incidencia(int id, String usuario, Prioridad prioridad, boolean resuelta) { }
inc.id();                                   // getter SIN get

enum Prioridad {
    ALTA(3), MEDIA(2), BAJA(1);
    private final int peso;
    Prioridad(int peso) { this.peso = peso; }
    int peso() { return peso; }
}

String texto = switch (prioridad) {         // switch con flecha: sin break, devuelve valor
    case ALTA -> "Urgente";
    case MEDIA -> "Normal";
    case BAJA -> "Cuando se pueda";
};

interface FuenteDatos<T> { List<T> cargar(); }
FuenteDatos<Incidencia> f = FuenteDatosFactory.crear("incidencias");   // Factory
```

### ⚠ Reglas
- Un `record` es **inmutable**: no tiene setters.
- Desde fuera se usa `inc.usuario()`, no `inc.usuario`.

---

## 8. Imports
```java
import java.util.function.*;     // Predicate, Function, Consumer, Supplier, UnaryOperator, Bi*...
import java.util.Comparator;
```
