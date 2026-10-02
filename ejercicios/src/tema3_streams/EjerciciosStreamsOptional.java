package tema3_streams;

import java.util.*;
import java.util.function.Predicate;

/**
 * Hoja de ejercicios para completar en clase.
 * Cada método contiene un TODO y se puede resolver con Stream y Optional.
 *
 * (Adaptado de "compact source file" a clase normal para poder ponerlo en un paquete.
 *  El contenido de los ejercicios es el mismo.)
 */
public class EjerciciosStreamsOptional {

    record Producto(String codigo, String nombre, String categoria,
                    double precio, int stock) {
    }

    record Incidencia(int id, String usuario, String descripcion,
                      String prioridad, boolean resuelta) {
    }

    public static void main(String[] args) {
        List<Producto> productos = datosProductos();
        List<Incidencia> incidencias = datosIncidencias();


        System.out.println("Completa los métodos TODO y ejecuta de nuevo.");
        System.out.println("1. " + productosConStockBajo(productos));
        System.out.println("2. " + nombresDeInformatica(productos));
        System.out.println("3. " + precioMedio(productos));
        /*System.out.println("4. " + buscarProducto(productos, "P003"));
        System.out.println("5. " + resumenIncidenciasAbiertas(incidencias));
        */

        // Tarea 1 --> Definir un predicado para productos con stock bajo < 30
        Predicate<Producto> precioMenor30 = p -> p.precio < 30;
        // Tarea 2 --> Comparador para ordenar productos por precio
        Comparator<Producto> porPrecio = Comparator.comparing(Producto::precio);
        // Tarea 3
        // lista de productos
        List<Producto> listaProductos = new java.util.ArrayList<>();
        for (Producto p : productos) {
            if (precioMenor30.test(p)) {
                listaProductos.add(p);
            }
        }
        listaProductos.sort(porPrecio); // sort() ordena los elementos de la lista según el comparador definido
        System.out.println(listaProductos);
    }

    // Ejercicio 1: conservar solo productos con stock inferior a 5.
    static List<Producto> productosConStockBajo(List<Producto> productos) {
        // TODO: usar stream, filter y toList.
        Predicate<Producto> menor5 = p -> p.stock < 5;
        return productos.stream().filter(menor5).toList();
    }

    // Ejercicio 2: obtener los nombres de productos de Informática en mayúsculas.
    static List<String> nombresDeInformatica(List<Producto> productos) {
        // TODO: usar filter, map y toList.
        return productos.stream().filter(p -> p.categoria().contains("Informática")).map(s -> s.nombre().toUpperCase()).toList();
    }

    // Ejercicio 3: calcular el precio medio de todos los productos.
    static OptionalDouble precioMedio(List<Producto> productos) {
        // TODO: usar mapToDouble y average.
        return productos.stream().mapToDouble(pm -> pm.precio).average();
    }

    // Ejercicio 4: localizar un producto por código sin devolver null.
    static Optional<Producto> buscarProducto(
            List<Producto> productos, String codigo) {
        // TODO: usar filter y findFirst.
        return productos
                .stream()
                .filter(p -> p.codigo().equals(codigo)) // filtra los productos que tienen el código igual al parámetro
                // parametro -> p.codigo().equals(codigo) es una expresión lambda que compara el código del producto con el código buscado
                .findFirst(); // no devuelve null, devuelve un Optional<Producto>
    }

    // Ejercicio 5: obtener resúmenes de incidencias abiertas, ordenadas por prioridad.
    static List<String> resumenIncidenciasAbiertas(
            List<Incidencia> incidencias) {
        // TODO: usar filter, sorted, map y toList.
        return incidencias.stream()
                .filter(p -> !p.resuelta()) // filtra las incidencias que devuelven false en resuelta()
                .sorted(Comparator.comparing(Incidencia::prioridad))
                .map(i -> "Incidencia #" + i.id() + " - Usuario: " + i.usuario() + " - Prioridad: " + i.prioridad())
                .toList();
    }

    private static List<Producto> datosProductos() {
        return List.of(
                new Producto("P001", "Teclado", "Informática", 25.90, 12),
                new Producto("P002", "Ratón", "Informática", 14.50, 3),
                new Producto("P003", "Monitor", "Informática", 189.99, 4),
                new Producto("P004", "Silla", "Mobiliario", 120.00, 8),
                new Producto("P005", "Lámpara", "Mobiliario", 32.75, 2)
        );
    }

    private static List<Incidencia> datosIncidencias() {
        return List.of(
                new Incidencia(1, "Ana", "No puede acceder", "ALTA", false),
                new Incidencia(2, "Luis", "Error de impresión", "MEDIA", true),
                new Incidencia(3, "Marta", "Contraseña caducada", "ALTA", false),
                new Incidencia(4, "Pablo", "Consulta sobre el informe", "BAJA", false)
        );
    }

}

