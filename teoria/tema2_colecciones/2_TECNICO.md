# Tema 2: Colecciones, la parte técnica (para consultar)

> **Este archivo no se lee de un tirón: se consulta.** Si no entiendes **para qué sirve** algo, vuelve a `1_HISTORIA.md`. Métodos sueltos: `../GUIA_LIBRERIAS.md` (apartado 4).

**Índice:**
1. [`List` / `ArrayList`](#1-list--arraylist)
2. [`Queue` / `LinkedList`](#2-queue--linkedlist)
3. [`Map` / `HashMap`](#3-map--hashmap)
4. [`Set` / `HashSet`](#4-set--hashset)
5. [Inmutables: `List.of`, `Map.of`](#5-inmutables-listof-mapof)
6. [Imports](#6-imports)

🧪 Todo en `C01_Colecciones` · 📝 `ejercicioPlaylist`

---

## 1. `List` / `ArrayList`
```java
List<Integer> orden = new ArrayList<>();
orden.add(3);                 // al final
orden.get(0);                 // por índice
orden.size();
orden.remove(Integer.valueOf(3));   // por valor (remove(0) = por índice)
orden.contains(3);
orden.sort(Comparator.naturalOrder());
for (int id : orden) { ... }
```

| Método | Qué hace |
|---|---|
| `add(x)` / `add(i, x)` | Añadir al final / en la posición `i` |
| `get(i)` / `set(i, x)` | Leer / cambiar la posición `i` |
| `remove(i)` / `remove(objeto)` | Quitar por índice / por valor |
| `size()` / `isEmpty()` / `contains(x)` | Tamaño / ¿vacía? / ¿está? |
| `indexOf(x)` | Posición, o `-1` |
| `sort(c)` / `removeIf(p)` / `forEach(c)` | Con lambdas (tema 1) |

---

## 2. `Queue` / `LinkedList`
```java
Queue<Integer> cola = new LinkedList<>();
cola.offer(5);                // añadir al final (add también vale)
Integer siguiente = cola.poll();   // saca el primero; null si está vacía
cola.peek();                  // mira el primero sin sacarlo
```

| Método | Si está vacía |
|---|---|
| `poll()` | devuelve `null` ✅ |
| `remove()` | lanza `NoSuchElementException` |
| `peek()` | devuelve `null` |
| `element()` | lanza excepción |

---

## 3. `Map` / `HashMap`
```java
Map<Integer, String> canciones = new HashMap<>();
canciones.put(1, "Blinding Lights - The Weeknd - pop");
canciones.get(99);                       // null si no existe
canciones.containsKey(99);               // false
canciones.getOrDefault(99, "No existe");

Map<Integer, Integer> reproducciones = new HashMap<>();
reproducciones.merge(id, 1, Integer::sum);                                    // contar
reproducciones.put(id, reproducciones.getOrDefault(id, 0) + 1);               // lo mismo

for (Map.Entry<Integer, Integer> e : reproducciones.entrySet()) {
    e.getKey(); e.getValue();
}
canciones.forEach((id, c) -> System.out.println(id + " -> " + c));
```

| Método | Qué hace |
|---|---|
| `put(k, v)` | Guarda (si la clave existía, **sustituye** el valor) |
| `get(k)` | El valor, o `null` |
| `getOrDefault(k, d)` | El valor, o `d` |
| `containsKey(k)` / `containsValue(v)` | ¿Está? |
| `remove(k)` | Quita |
| `keySet()` / `values()` / `entrySet()` | Claves / valores / parejas |
| `merge(k, v, f)` | Si no está, pone `v`; si está, combina con `f` |

### ⚠ Reglas
- Las claves **no son índices**: `containsKey`, nunca `id < size()`.
- `HashMap` **no guarda orden**. Para orden de inserción: `LinkedHashMap`; ordenado por clave: `TreeMap`.

---

## 4. `Set` / `HashSet`
```java
Set<String> artistas = new HashSet<>();
String artista = cancion.split(" - ")[1];   // "Título - Artista - género"
artistas.add(artista);                       // si ya estaba, no se repite (devuelve false)
artistas.contains("Queen");
```
- Sin orden. Para orden alfabético: `TreeSet`.

---

## 5. Inmutables: `List.of`, `Map.of`
```java
List<String> fija = List.of("a", "b");
fija.add("c");                                  // ❌ UnsupportedOperationException
List<String> editable = new ArrayList<>(List.of("a", "b"));   // ✅
```

---

## 6. Imports
```java
import java.util.*;     // List, ArrayList, Queue, LinkedList, Map, HashMap, Set, HashSet
```
