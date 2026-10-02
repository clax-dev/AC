# Tema 3: Streams y Optional

> **Ejercicio de referencia:** `ejercicios/src/tema3_streams/EjerciciosStreamsOptional.java`
> **Código de teoría:** `teoria/src/t3_streams/S01_StreamsOptional.java` (usa otros datos para no darte la solución)

---

## Stream = una tubería de lambdas

```java
lista.stream()              // origen
     .filter(Predicate)     // intermedia: se queda con los elementos que cumplen
     .map(Function)         // intermedia: transforma cada elemento
     .sorted(Comparator)    // intermedia: ordena
     .toList();             // FINAL: produce el resultado
```

- **No modifica** la lista original.
- Si no hay operación final, no se ejecuta nada.
- Operaciones finales habituales: `toList()`, `forEach()`, `count()`, `findFirst()`, `anyMatch()`.
- Para números: `mapToDouble(...)` seguido de `.average()`, `.sum()` o `.max()`.

## Optional = una caja que puede estar vacía

Sirve para **no devolver `null`**.

```java
Optional<Producto> p = lista.stream().filter(...).findFirst();
p.isPresent();               // ¿tiene valor?
p.orElse(otro);              // el valor, o el que indiques por defecto
p.ifPresent(x -> ...);       // ejecuta la lambda solo si hay valor
p.map(Producto::nombre);     // transforma el contenido si existe
```

## Qué operaciones pide cada ejercicio de la hoja

| Ejercicio | Operaciones |
|---|---|
| 1. Stock bajo | `filter` + `toList` |
| 2. Nombres de Informática | `filter` + `map` + `toList` |
| 3. Precio medio | `mapToDouble` + `average` (devuelve `OptionalDouble`, que hay que pasar a `Optional<Double>`) |
| 4. Buscar por código | `filter` + `findFirst` |
| 5. Resumen de incidencias abiertas | `filter` + `sorted` + `map` + `toList` |
