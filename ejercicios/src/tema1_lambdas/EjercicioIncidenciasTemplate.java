package tema1_lambdas;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

public class EjercicioIncidenciasTemplate {
    public static void main(String[] args) {
        FuenteDatos<?> fuenteGenerica = FuenteDatosFactory.crear("incidencias");

        @SuppressWarnings("unchecked") // La fábrica devuelve una interfaz
        FuenteDatos<Incidencia> fuente = (FuenteDatos<Incidencia>) fuenteGenerica;

        List<Incidencia> incidencias = fuente.cargar();

        // TODO Function: transformación de un objeto a un texto.
        // Genera un String tipo: #1 | Ana | ALTA | No puede acceder al sistema
        Function<Incidencia, String> resumen = inc -> "#" + inc.id() + " | " + inc.usuario() + " | " + inc.prioridad() + " | " + inc.descripcion();

        // TODO - Muestra por pantalla todas las incidencias usando lambda
        incidencias.forEach(inc -> System.out.println(resumen.apply(inc))); // apply() dispara la función resumen para cada incidencia

        // TODO Predicate: validación y filtrado.
        // Verifica que lso datos no sean null ni estén vacíos
        Predicate<Incidencia> datosValidos = valido -> valido.usuario != null && !valido.usuario.isEmpty() && valido.descripcion != null && !valido.descripcion.isEmpty();
        Predicate<Incidencia> abiertas = estado -> !estado.resuelta; // Verifica si la incidencia está abierta (no resuelta)
        Predicate<Incidencia> prioridadAlta = prioridad -> prioridad.prioridad() == Prioridad.ALTA; // Verifica si la prioridad de la incidencia es alta

        // TODO Consumer: acción que no devuelve ningún resultado.
        // Imprime por pantalla: AUDITORÍA: procesada incidencia --> mostrar incidencia
        Consumer<Incidencia> auditoria = inc -> System.out.println("AUDITORÍA: procesada incidencia " + resumen.apply(inc));

        // Comparator: primero las prioridades más altas y, en caso de empate,
        // el identificador más pequeño.

        Comparator<Incidencia> porPrioridad = (inc1, inc2) -> {
            // -1 inc1 es menor que inc2, 0 son iguales, 1 inc1 es mayor que inc2
            if (inc2.prioridad().peso() < inc1.prioridad().peso()) {
                return 1; //inc1 es mayor que inc2
            } else if (inc2.prioridad().peso() > inc1.prioridad().peso()) {
                return -1; // inc1 es menor que inc2
            } else {
                return 0; // prioridad igual, no hace nada.
            }
        };

        // Mostrar y procesar las incidencias abiertas y con prioridad alta
        System.out.println("--------- Incidencias abiertas de prioridad alta ---------:");
        List<Incidencia> resultadoAlto = new ArrayList<>();

        crearIncidencias(incidencias, resultadoAlto, Prioridad.ALTA); // método que filtra las incidencias abiertas y con prioridad alta

        resultadoAlto.sort(porPrioridad); // sort() ordena los elementos de la lista según el comparador definido
        resultadoAlto.forEach(auditoria); // muestra por pantalla las incidencias abiertas y con prioridad alta
        fuente.guardar(resultadoAlto); // guardar las incidencias abiertas y con prioridad alta en la fuente de datos

        // TODO: mostrar y procesar las incidencias con prioridad media
        System.out.println("incidencias con prioridad media:");
        List<Incidencia> resultadoMedio = new ArrayList<>(); // lista para guardar las incidencias con prioridad media

        crearIncidencias(incidencias, resultadoMedio, Prioridad.MEDIA); // método que filtra las incidencias con prioridad media

        resultadoMedio.sort(porPrioridad); // sort() ordena los elementos de la lista según el comparador definido
        resultadoMedio.forEach(auditoria); // muestra por pantalla las incidencias con prioridad media
        fuente.guardar(resultadoMedio); // guardar las incidencias con prioridad media en la fuente de datos


        /*/ mostrar prioridades baja -> EXTRA
        System.out.println("incidencias con prioridad baja:");
        List<Incidencia> resultadoBajo = new ArrayList<>(); // lista para guardar las incidencias con prioridad baja

        crearIncidencias(incidencias, resultadoBajo, Prioridad.BAJA); // método que filtra las incidencias con prioridad baja

        resultadoBajo.sort(porPrioridad); // sort() ordena los elementos de la lista según el comparador definido
        resultadoBajo.forEach(auditoria); // muestra por pantalla las incidencias con prioridad baja
        fuente.guardar(resultadoBajo);
        */

    }

    // método para mostrar incidencias con un resumen personalizado
    public static void crearIncidencias(List<Incidencia> inc, List<Incidencia> nueva_lista, Prioridad prioridad) {
        for (Incidencia i : inc) {
            if (i.prioridad() == prioridad) {
                nueva_lista.add(i);
            }
        }
    }

    // -------------------------------------------------------------------------
    // EJERCICIO BASE: importación, validación y tratamiento de incidencias
    // -------------------------------------------------------------------------

    public record Incidencia(
            int id,
            String usuario,
            String descripcion,
            Prioridad prioridad,
            boolean resuelta
    ) {
    }

    public enum Prioridad {
        BAJA(1), MEDIA(2), ALTA(3);

        private final int peso;

        Prioridad(int peso) {
            this.peso = peso;
        }

        public int peso() {
            return peso;
        }
    }

    /**
     * Interfaz común para cualquier fuente de datos.
     */
    public interface FuenteDatos<T> {
        List<T> cargar();

        void guardar(List<T> datos);
    }

    /**
     * Simula una fuente de datos que podría ser un fichero CSV o JSON.
     */
    public static class FuenteIncidencias implements FuenteDatos<Incidencia> {


        public List<Incidencia> cargar() {
            return List.of(
                    new Incidencia(1, "Ana", " No puede acceder al sistema ", Prioridad.ALTA, false),
                    new Incidencia(2, "Luis", "Error de impresión", Prioridad.MEDIA, true),
                    new Incidencia(3, "Marta", "Contraseña caducada", Prioridad.ALTA, false),
                    new Incidencia(4, "Pablo", "Consulta sobre el informe", Prioridad.BAJA, false),
                    new Incidencia(5, "Sara", "El programa se cierra", Prioridad.ALTA, true),
                    new Incidencia(6, "Sonia", " No puede acceder al sistema ", Prioridad.ALTA, false),
                    new Incidencia(7, "Álvaro", "Error de impresión", Prioridad.BAJA, true),
                    new Incidencia(8, "Mateo", "Contraseña caducada", Prioridad.ALTA, true),
                    new Incidencia(9, "Manuel", "Consulta sobre el informe", Prioridad.BAJA, true),
                    new Incidencia(10, "Davinia", "El programa se cierra", Prioridad.ALTA, true)
            );
        }


        public void guardar(List<Incidencia> datos) {
            System.out.println("Guardadas " + datos.size() + " incidencias procesadas.");
        }
    }

    /**
     * Fábrica sencilla: selecciona la clase según el tipo de dato solicitado.
     */
    public static class FuenteDatosFactory {

        public static FuenteDatos<?> crear(String tipo) {
            return switch (tipo.toLowerCase()) {
                case "incidencias" -> new FuenteIncidencias();
                default -> throw new IllegalArgumentException(
                        "No existe una fuente para el tipo: " + tipo);
            };
        }
    }
}
