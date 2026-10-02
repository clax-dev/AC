package t2_colecciones;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

/**
 * =====================================================================
 *  TEORÍA 2 — COLECCIONES: List, Queue, Map, Set (+ lambdas)
 *  Basado en: ejercicios/tema2_colecciones/ejercicioPlaylist.java
 * =====================================================================
 *
 *  | Interfaz | Clase      | Característica                      | Métodos clave                     |
 *  |----------|------------|-------------------------------------|-----------------------------------|
 *  | List     | ArrayList  | ordenada, admite repetidos, índice  | add, get(i), size, remove, sort   |
 *  | Queue    | LinkedList | FIFO (1º en entrar, 1º en salir)    | add/offer, poll, peek, isEmpty    |
 *  | Map<K,V> | HashMap    | clave -> valor, claves únicas       | put, get, containsKey,            |
 *  |          |            |                                     | getOrDefault, merge, entrySet     |
 *  | Set      | HashSet    | SIN repetidos, sin orden            | add, contains, size               |
 *
 *  ¿CUÁL ELIJO? (pistas del enunciado de la playlist)
 *    - Buscar por ID rápido         -> HashMap
 *    - Conservar un orden           -> ArrayList
 *    - Cola de reproducción         -> LinkedList como Queue
 *    - Valores únicos (artistas)    -> HashSet
 *
 *  BUENA PRÁCTICA: declara con la INTERFAZ y crea con la CLASE
 *      List<Integer> orden = new ArrayList<>();     ✔
 *      ArrayList<Integer> orden = new ArrayList<>(); (funciona, pero menos flexible)
 */
public class C01_Colecciones {

    public static void main(String[] args) {

        // ============================================================
        // MAP: id -> "Título - Artista - género"
        // ============================================================
        Map<Integer, String> canciones = new HashMap<>();
        canciones.put(1, "Blinding Lights - The Weeknd - pop");
        canciones.put(2, "Bohemian Rhapsody - Queen - rock");
        canciones.put(3, "Save Your Tears - The Weeknd - pop");

        System.out.println("get(2)          = " + canciones.get(2));
        System.out.println("get(99)         = " + canciones.get(99) + "   <- si no existe devuelve null");
        System.out.println("containsKey(99) = " + canciones.containsKey(99));

        // Recorrer un Map con lambda (BiConsumer: clave, valor)
        canciones.forEach((id, cancion) -> System.out.println("  " + id + " -> " + cancion));

        // ============================================================
        // LIST: orden de la playlist (guarda IDs)
        // ============================================================
        List<Integer> ordenPlaylist = new ArrayList<>();
        ordenPlaylist.add(3);
        ordenPlaylist.add(1);
        ordenPlaylist.add(2);

        System.out.println("\nPlaylist en orden:");
        for (int id : ordenPlaylist) {                          // for-each
            System.out.println("  " + id + ": " + canciones.get(id));
        }
        ordenPlaylist.forEach(id -> System.out.println("  (lambda) " + canciones.get(id)));

        // ============================================================
        // QUEUE: cola de reproducción (FIFO)
        // ============================================================
        Queue<Integer> cola = new LinkedList<>();
        cola.add(2);
        cola.add(1);
        System.out.println("\npeek() mira sin sacar: " + cola.peek());
        System.out.println("poll() saca el primero: " + cola.poll());
        System.out.println("poll() saca el primero: " + cola.poll());
        System.out.println("poll() con cola vacía: " + cola.poll() + "   <- null, NO lanza excepción");
        // ⚠ remove() con cola vacía SÍ lanza NoSuchElementException

        // ============================================================
        // CONTADORES con Map: getOrDefault y merge
        // ============================================================
        Map<Integer, Integer> reproducciones = new HashMap<>();
        reproducciones.put(1, reproducciones.getOrDefault(1, 0) + 1);  // forma clásica
        reproducciones.merge(1, 1, Integer::sum);                       // forma con lambda
        reproducciones.merge(2, 1, Integer::sum);                       // si no existe, pone 1
        System.out.println("\nReproducciones: " + reproducciones);

        // Buscar el máximo recorriendo entrySet
        int idMax = -1, max = -1;
        for (Map.Entry<Integer, Integer> e : reproducciones.entrySet()) {
            if (e.getValue() > max) {
                max = e.getValue();
                idMax = e.getKey();
            }
        }
        System.out.println("Más reproducida: " + canciones.get(idMax) + " (" + max + ")");

        // ============================================================
        // SET: artistas sin repetir
        // ============================================================
        Set<String> artistas = new HashSet<>();
        for (String c : canciones.values()) {
            String artista = c.split(" - ")[1];   // "Título - Artista - género" -> posición 1
            artistas.add(artista);                // si ya estaba, no se añade
        }
        System.out.println("\nArtistas únicos: " + artistas);

        // ============================================================
        // LAMBDAS útiles en colecciones
        // ============================================================
        List<Integer> nums = new ArrayList<>(List.of(5, 3, 8, 1));
        nums.removeIf(n -> n < 4);                // Predicate
        nums.replaceAll(n -> n * 10);             // UnaryOperator
        nums.sort((a, b) -> b - a);               // Comparator descendente
        System.out.println("\nremoveIf/replaceAll/sort -> " + nums);

        // ⚠ List.of(...) crea una lista INMUTABLE: add/remove lanzan UnsupportedOperationException
        // Para poder modificarla: new ArrayList<>(List.of(...))
    }
}
