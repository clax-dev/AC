package t4_ficheros;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.stream.Stream;

/**
 * =====================================================================
 *  TEORÍA 4.3 — CSV <-> OBJETOS: guardar y cargar records en un fichero
 *  Une TODO lo de AC: records (tema 1), colecciones (tema 2), streams (tema 3) y ficheros (tema 4).
 * =====================================================================
 *
 *  CSV = "valores separados por comas" (o por ;). Una línea = un objeto; la 1ª suele ser la CABECERA.
 *
 *      codigo;nombre;categoria;precio;stock
 *      P001;Teclado;Informática;25.90;12
 *
 *  GUARDAR (objeto -> línea):   unir los campos con el separador
 *  CARGAR  (línea -> objeto):   linea.split(";") -> convertir cada trozo a su tipo -> new Producto(...)
 *
 *  Es justo lo que hacía la interfaz FuenteDatos<T> del ejercicio de incidencias (tema 1):
 *  "Simula una fuente de datos que podría ser un fichero CSV o JSON". Aquí la implementamos DE VERDAD.
 *
 *  ⚠ Cuidados con CSV
 *    - Usa ";" si los textos pueden llevar comas.
 *    - Números decimales: escribe con Locale.ROOT (punto) para que Double.parseDouble lo entienda.
 *    - Salta la cabecera: .skip(1)
 *    - Líneas vacías o mal formadas -> fíltralas o captura NumberFormatException.
 */
public class F03_CSVaObjetos {

    record Producto(String codigo, String nombre, String categoria, double precio, int stock) { }

    // La misma interfaz del ejercicio de incidencias
    interface FuenteDatos<T> {
        List<T> cargar() throws IOException;
        void guardar(List<T> datos) throws IOException;
    }

    // Implementación REAL con un fichero CSV
    static class FuenteProductosCSV implements FuenteDatos<Producto> {
        private static final String SEP = ";";
        private static final String CABECERA = "codigo;nombre;categoria;precio;stock";
        private final Path fichero;

        FuenteProductosCSV(Path fichero) { this.fichero = fichero; }

        @Override
        public void guardar(List<Producto> productos) throws IOException {
            List<String> lineas = new ArrayList<>();
            lineas.add(CABECERA);
            for (Producto p : productos) {
                lineas.add(String.join(SEP, p.codigo(), p.nombre(), p.categoria(),
                        String.format(Locale.ROOT, "%.2f", p.precio()),   // punto decimal siempre
                        String.valueOf(p.stock())));
            }
            Files.write(fichero, lineas, StandardCharsets.UTF_8);
        }

        @Override
        public List<Producto> cargar() throws IOException {
            try (Stream<String> lineas = Files.lines(fichero, StandardCharsets.UTF_8)) {
                return lineas
                        .skip(1)                                    // fuera la cabecera
                        .filter(l -> !l.isBlank())                  // fuera líneas vacías
                        .map(FuenteProductosCSV::aProducto)         // línea -> Producto (Function)
                        .toList();
            }
        }

        // Una línea de texto -> un objeto Producto
        private static Producto aProducto(String linea) {
            String[] c = linea.split(SEP);
            return new Producto(c[0], c[1], c[2], Double.parseDouble(c[3]), Integer.parseInt(c[4]));
        }
    }

    public static void main(String[] args) throws IOException {
        Files.createDirectories(Path.of("ficheros_demo"));
        FuenteDatos<Producto> fuente = new FuenteProductosCSV(Path.of("ficheros_demo", "productos.csv"));

        // 1) GUARDAR una lista de objetos en el CSV
        List<Producto> productos = List.of(
                new Producto("P001", "Teclado", "Informática", 25.90, 12),
                new Producto("P002", "Ratón", "Informática", 14.50, 3),
                new Producto("P003", "Monitor", "Informática", 189.99, 4),
                new Producto("P004", "Silla", "Mobiliario", 120.00, 8),
                new Producto("P005", "Lámpara", "Mobiliario", 32.75, 2));
        fuente.guardar(productos);
        System.out.println("=== Contenido del CSV ===");
        Files.readAllLines(Path.of("ficheros_demo", "productos.csv")).forEach(System.out::println);

        // 2) CARGAR: del fichero a objetos otra vez
        List<Producto> cargados = fuente.cargar();
        System.out.println("\nCargados " + cargados.size() + " productos. ¿Iguales a los originales? "
                + cargados.equals(productos));

        // 3) Ya son objetos: se trabaja con streams como en el tema 3
        System.out.println("\n=== Stock bajo (< 5), del más barato al más caro ===");
        cargados.stream()
                .filter(p -> p.stock() < 5)
                .sorted(Comparator.comparingDouble(Producto::precio))
                .forEach(p -> System.out.println("  " + p.nombre() + " (" + p.precio() + " €, stock " + p.stock() + ")"));
    }
}
