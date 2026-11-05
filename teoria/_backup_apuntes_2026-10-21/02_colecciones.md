# Tema 2: Colecciones (List, Queue, Map, Set)

> **Ejercicio de referencia:** `ejercicios/src/tema2_colecciones/ejercicioPlaylist.java` (el enunciado está en `ENUNCIADO.md`, en la misma carpeta)
> **Código de teoría:** `teoria/src/t2_colecciones/C01_Colecciones.java`

---

## 1. Resumen

| Interfaz | Clase | Característica | Métodos clave |
|---|---|---|---|
| `List` | `ArrayList` | Ordenada, admite repetidos, acceso por índice | `add`, `get(i)`, `size`, `remove`, `sort` |
| `Queue` | `LinkedList` | **FIFO**: el primero que entra es el primero que sale | `add`/`offer`, `poll`, `peek`, `isEmpty` |
| `Map<K,V>` | `HashMap` | Clave → valor, sin claves repetidas | `put`, `get`, `containsKey`, `getOrDefault`, `merge`, `entrySet` |
| `Set` | `HashSet` | **Sin repetidos** y sin orden | `add`, `contains`, `size` |

## 2. ¿Cuál elijo? (pistas del enunciado)

| Necesito… | Uso |
|---|---|
| Buscar por ID | `HashMap` |
| Conservar un orden | `ArrayList` |
| Una cola de reproducción | `LinkedList` usada como `Queue` |
| Valores únicos (por ejemplo, artistas) | `HashSet` |

Buena práctica: declara la variable con la **interfaz** y créala con la **clase**:
```java
List<Integer> orden = new ArrayList<>();
Queue<Integer> cola = new LinkedList<>();
```

## 3. Operaciones típicas

```java
// Map
canciones.get(99);              // null si no existe
canciones.containsKey(99);      // false
canciones.forEach((id, c) -> System.out.println(id + " -> " + c));

// Queue
cola.peek();                    // mira el primero sin sacarlo
cola.poll();                    // saca el primero; null si está vacía
cola.remove();                  // ⚠ lanza excepción si está vacía

// Contador de reproducciones
reproducciones.put(id, reproducciones.getOrDefault(id, 0) + 1);
reproducciones.merge(id, 1, Integer::sum);           // lo mismo, con lambda

// Máximo recorriendo un Map
for (Map.Entry<Integer,Integer> e : reproducciones.entrySet()) { e.getKey(); e.getValue(); }

// Set de artistas a partir de "Título - Artista - género"
String artista = cancion.split(" - ")[1];
artistas.add(artista);          // si ya estaba, no se repite
```

## 4. Cuidado con…

- `List.of(...)` y `Map.of(...)` crean colecciones **inmutables**: `add` y `put` lanzan `UnsupportedOperationException`. Para poder modificarlas usa `new ArrayList<>(List.of(...))`.
- Los IDs de un `Map` **no son índices**. Comprobar `id >= canciones.size()` no sirve para saber si un ID existe; para eso está `containsKey`.
- Ninguna de estas colecciones es **segura para hilos** (se ve en PSP, con los hilos).
