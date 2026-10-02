package tema2_colecciones;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Scanner;

public class ejercicioPlaylist {

    public static void main(String[] args) {
        String entradaUsuario;
        // TODO completar bloque de inicialización
        // Indicamos los tipos de variables correspondientes a cada variable
        // Utilizamos los Map para los métodos que los utilizan, y las listas para las lecturas de datos
        Map<Integer, String> canciones = cargarCanciones();
        ArrayList<Integer> ordenPlaylist = new ArrayList<>();// TODO
        LinkedList<Integer> colaReproduccion = new LinkedList<>();// TODO
        Map<Integer, Integer> reproducciones = cargarReproducciones();

        Scanner teclado = new Scanner(System.in);
        int opcion;

        do {
            mostrarMenu(); // mostramos el menú
            opcion = teclado.nextInt();
            teclado.nextLine();

            switch (opcion) {
                case 1 -> mostrarCanciones(canciones, ordenPlaylist);
                case 2 -> {
                    System.out.print("ID de la canción: ");
                    int id = teclado.nextInt();
                    teclado.nextLine();
                    System.out.println(buscarCancion(canciones, id));
                }
                case 3 -> {
                    System.out.print("ID para añadir a la cola: ");
                    int id = teclado.nextInt();
                    teclado.nextLine();
                    anadirACola(canciones, colaReproduccion, id);
                }
                case 4 -> reproducirSiguiente(canciones, colaReproduccion, reproducciones);
                case 5 -> mostrarArtistas(canciones);
                case 6 -> mostrarMasReproducida(canciones, reproducciones);
                case 0 -> System.out.println("Hasta pronto.");
                default -> System.out.println("Opción no válida.");
            }
        } while (opcion != 0);

        teclado.close();
    }

    private static Map<Integer, String> cargarCanciones() {
        // TODO
        Map<Integer, String> canciones = new HashMap<>(); // variable para almacenar las canciones
        canciones.put(1, "Blinding Lights - The Weeknd - pop");

        return canciones;
    }

    private static Map<Integer, Integer> cargarReproducciones() {
        //TODO
        Map<Integer, Integer> reproducciones = new HashMap<>(); // variable para almacenar las reproducciones;
        reproducciones.put(1, 12);

        return reproducciones;
    }

    private static void mostrarMenu() {
        System.out.println("\n=== MENU PRINCIPAL ===");
        System.out.println("1) Mostrar canciones");
        System.out.println("2) Buscar canción por ID");
        System.out.println("3) Añádir canción a la cola de reproducción por ID");
        System.out.println("4) Reproducir la siguiente canción de la cola");
        System.out.println("5) Mostrar artistas");
        System.out.println("6) Mostrar Mas reproducida");
        System.out.println("0) Salir");
    }

    private static void mostrarCanciones(Map<Integer, String> canciones,
                                         List<Integer> ordenPlaylist) {
        // TODO: recorrer ordenPlaylist y mostrar cada canción.
        for (int i = 0; i < ordenPlaylist.size(); i++) {
            int id = ordenPlaylist.get(i);
            String cancion = canciones.get(id);
            System.out.println("ID: " + id + ", Canción: " + cancion);
        }
    }

    private static String buscarCancion(Map<Integer, String> canciones, int id) {
        // TODO: devolver la canción o un mensaje si no existe.
        if (!canciones.containsKey(id)) {
            return "Canción no encontrada.";
        } if (id < 0 || id >= canciones.size()) {
            return "ID inválido";
        }
        else {
            String cancion = canciones.get(id);
            return "Nombre de la canción: " + cancion;
        }
    }

    private static void anadirACola(Map<Integer, String> canciones,
                                    Queue<Integer> cola, int id) {
        // TODO: comprobar que existe y añadir el ID a la cola.
        if (canciones.containsKey(id)) { // comprueba que existe el ID dentro del "playlist"
            System.out.println("Canción añadida a la cola.");
            cola.add(id);
        } else {
            System.out.println("Canción no encontrada.");
        }
    }

    private static void reproducirSiguiente(Map<Integer, String> canciones,
                                            Queue<Integer> cola,
                                            Map<Integer, Integer> reproducciones) {
        // TODO: extraer el siguiente ID, mostrarlo y aumentar su contador.
        Integer id = cola.poll(); // cogemos el "id" del primer elemento del linkedList
        if (id == null) {
            System.out.println("No hay canciones en la cola.");
            return;
        } else {
            reproducciones.put(id, reproducciones.getOrDefault(id, 0) + 1); // guardamos el número de reproducciones sumándole 1 (evitando que sie s la primera vez que se escuche salte un error)
            System.out.println("Reproduciendo: " + canciones.get(id) + " (Reproducciones: " + reproducciones.get(id) + "ID: " + id + ")");
        }
    }

    private static void mostrarArtistas(Map<Integer, String> canciones) {
        // TODO: obtener los artistas sin repetir usando un HashSet.
    }

    private static void mostrarMasReproducida(Map<Integer, String> canciones,
                                              Map<Integer, Integer> reproducciones) {
        // TODO: localizar el ID con mayor número de reproducciones.
    }
}
