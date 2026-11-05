# Tema 3: Streams y Optional, la parte técnica (para consultar)

> **Este archivo no se lee de un tirón: se consulta.** Si no entiendes **para qué sirve** algo, vuelve a `1_HISTORIA.md`. Métodos sueltos: `../GUIA_LIBRERIAS.md` (apartado 6).

**Índice:**
1. [Plantilla](#1-plantilla)
2. [Crear un stream](#2-crear-un-stream)
3. [Operaciones intermedias](#3-operaciones-intermedias)
4. [Operaciones finales](#4-operaciones-finales)
5. [Streams numéricos](#5-streams-numéricos)
6. [`Optional`](#6-optional)
7. [Los ejercicios de la hoja](#7-los-ejercicios-de-la-hoja)
8. [Imports](#8-imports)

🧪 `S01_StreamsOptional` · 📝 `EjerciciosStreamsOptional`, BioDAM

---

## 1. Plantilla
```java
List<String> resumen = incidencias.stream()
        .filter(i -> !i.resuelta())                         // Predicate
        .sorted(Comparator.comparingInt((Incidencia i) -> i.prioridad().peso()).reversed())
        .map(i -> "#" + i.id() + " " + i.usuario())         // Function
        .toList();                                          // FINAL
```

---

## 2. Crear un stream
| Origen | Código |
|---|---|
| Una lista | `lista.stream()` |
| Unos valores | `Stream.of(a, b, c)` |
| Un array | `Arrays.stream(array)` |
| Las líneas de un fichero | `Files.lines(ruta)` (en `try ( )`, tema 4) |
| Números | `IntStream.range(0, 10)` |

---

## 3. Operaciones intermedias (devuelven otro stream)
| Método | Recibe | Qué hace |
|---|---|---|
| `filter(p)` | `Predicate` | Se queda con los que cumplen |
| `map(f)` | `Function` | Transforma cada elemento |
| `sorted()` / `sorted(c)` | — / `Comparator` | Ordena |
| `distinct()` | — | Quita repetidos |
| `skip(n)` | — | Se salta los `n` primeros (la cabecera de un CSV) |
| `limit(n)` | — | Se queda con los `n` primeros |
| `mapToInt(f)` / `mapToDouble(f)` | — | Pasa a stream numérico |
| `peek(c)` | `Consumer` | Mira sin cambiar (para depurar) |

---

## 4. Operaciones finales (ejecutan todo)
| Método | Devuelve |
|---|---|
| `toList()` | `List<T>` (inmutable) |
| `collect(Collectors.toList())` | `List<T>` |
| `forEach(c)` | nada |
| `count()` | **`long`** |
| `findFirst()` / `findAny()` | `Optional<T>` |
| `anyMatch(p)` / `allMatch(p)` / `noneMatch(p)` | `boolean` |
| `min(c)` / `max(c)` | `Optional<T>` |
| `reduce(0, Integer::sum)` | el acumulado |
| `collect(Collectors.joining(", "))` | `String` |
| `collect(Collectors.groupingBy(f))` | `Map<K, List<T>>` |

### ⚠ Reglas
- Sin operación final **no se ejecuta nada**.
- Un stream **solo se recorre una vez** (`IllegalStateException`).
- La lista original **no cambia**.

---

## 5. Streams numéricos
```java
OptionalDouble media = productos.stream()
        .mapToDouble(Producto::precio)
        .average();
double valor = media.orElse(0.0);

// Pasar OptionalDouble a Optional<Double>
Optional<Double> m = media.isPresent() ? Optional.of(media.getAsDouble()) : Optional.empty();
```
| Método | Devuelve |
|---|---|
| `sum()` | `double` / `int` |
| `average()` | `OptionalDouble` |
| `max()` / `min()` | `OptionalDouble` / `OptionalInt` |

---

## 6. `Optional`
```java
Optional<Producto> p = productos.stream()
        .filter(x -> x.codigo().equals("P002"))
        .findFirst();

p.isPresent();                     // ¿hay algo?
p.isEmpty();                       // ¿está vacía?
p.orElse(porDefecto);              // el valor o uno por defecto
p.orElseThrow();                   // el valor o NoSuchElementException
p.ifPresent(x -> System.out.println(x));
p.map(Producto::nombre);           // Optional<String>
Optional.empty();  Optional.of(x);  Optional.ofNullable(x);
```

### ⚠ Reglas
- `get()` sobre un `Optional` vacío lanza `NoSuchElementException`: mejor `orElse` o `ifPresent`.
- `Optional.of(null)` lanza `NullPointerException`; para valores que pueden ser `null`, `ofNullable`.

---

## 7. Los ejercicios de la hoja
| Ejercicio | Operaciones |
|---|---|
| 1. Stock bajo | `filter` + `toList` |
| 2. Nombres de Informática en mayúsculas | `filter` + `map` + `toList` |
| 3. Precio medio | `mapToDouble` + `average` |
| 4. Buscar por código | `filter` + `findFirst` |
| 5. Resumen de incidencias abiertas | `filter` + `sorted` + `map` + `toList` |
| BioDAM | `skip(1)`, `filter`, `map`, `distinct`, `sorted`, `average`, `count` |

---

## 8. Imports
```java
import java.util.*;               // List, Optional, OptionalDouble, Comparator
import java.util.stream.*;        // Stream, IntStream, Collectors
```
