package tema1_lambdas;

import java.util.Comparator;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

public class EjercicioIncidenciasTemplateOptimizado {
    public static void main(String[] args) {
        FuenteDatos<?> fuenteGenerica = FuenteDatosFactory.crear("incidencias");

        @SuppressWarnings("unchecked") // La fábrica devuelve una interfaz
        FuenteDatos<Incidencia> fuente = (FuenteDatos<Incidencia>) fuenteGenerica;

        List<Incidencia> incidencias = fuente.cargar();

        // Function: transformación de un objeto a un texto.
        // Genera un String tipo: #1 | Ana | ALTA | No puede acceder al sistema
        Function<Incidencia, String> resumen = inc -> "#" + inc.id() + " | " + inc.usuario() + " | "
                + inc.prioridad() + " | " + inc.descripcion().trim(); // trim() quita los espacios sobrantes

        // Muestra por pantalla todas las incidencias: map aplica la Function a cada una
        incidencias.stream().map(resumen).forEach(System.out::println);

        // Predicate: validación y filtrado.
        Predicate<Incidencia> datosValidos = inc -> esTextoValido(inc.usuario()) && esTextoValido(inc.descripcion());
        Predicate<Incidencia> abiertas = inc -> !inc.resuelta(); // abierta = NO resuelta

        // Consumer: acción que no devuelve ningún resultado.
        Consumer<Incidencia> auditoria = inc -> System.out.println("AUDITORÍA: procesada incidencia " + resumen.apply(inc));

        // Comparator: primero las prioridades más altas y, en caso de empate, el identificador más pequeño.
        Comparator<Incidencia> porPrioridad = Comparator
                .comparingInt((Incidencia inc) -> inc.prioridad().peso())
                .reversed()                              // ALTA primero
                .thenComparingInt(Incidencia::id);       // empate -> id menor primero

        // Cada bloque solo cambia el título y el filtro: el resto lo hace procesar()
        procesar("Incidencias abiertas de prioridad alta", incidencias,
                datosValidos.and(abiertas).and(conPrioridad(Prioridad.ALTA)), porPrioridad, auditoria, fuente);

        procesar("Incidencias de prioridad media", incidencias,
                datosValidos.and(conPrioridad(Prioridad.MEDIA)), porPrioridad, auditoria, fuente);

        // EXTRA: prioridad baja -> ahora es una sola llamada
        // procesar("Incidencias de prioridad baja", incidencias,
        //         datosValidos.and(conPrioridad(Prioridad.BAJA)), porPrioridad, auditoria, fuente);
    }

    // Filtra, ordena, muestra y guarda. Recibe el comportamiento (filtro, orden, acción) como lambdas.
    static void procesar(String titulo, List<Incidencia> incidencias, Predicate<Incidencia> filtro,
                         Comparator<Incidencia> orden, Consumer<Incidencia> accion,
                         FuenteDatos<Incidencia> fuente) {
        System.out.println("\n--------- " + titulo + " ---------");
        List<Incidencia> resultado = incidencias.stream()
                .filter(filtro)
                .sorted(orden)
                .toList();
        resultado.forEach(accion);
        fuente.guardar(resultado);
    }

    // Devuelve un Predicate que comprueba la prioridad indicada (sustituye a prioridadAlta, prioridadMedia...)
    static Predicate<Incidencia> conPrioridad(Prioridad prioridad) {
        return inc -> inc.prioridad() == prioridad;
    }

    // Un texto es válido si no es null y no está vacío ni solo con espacios
    static boolean esTextoValido(String texto) {
        return texto != null && !texto.isBlank();
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
