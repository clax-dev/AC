package t1_lambdas;

import java.util.List;

/**
 * =====================================================================
 *  TEORÍA 1.5 — JAVA MODERNO QUE APARECE EN LOS EJERCICIOS
 *  record · enum con atributos · switch con flecha · interfaz genérica · Factory
 *  Basado en: EjercicioIncidenciasTemplate (FuenteDatos, FuenteDatosFactory...)
 * =====================================================================
 */
public class L05_RecordEnumSwitchFactory {

    // -----------------------------------------------------------------
    // RECORD: clase INMUTABLE de datos. Java genera automáticamente:
    //   - constructor con todos los campos
    //   - "getters" con el nombre del campo:  id(), usuario()  (¡sin get!)
    //   - equals(), hashCode(), toString()
    // Los campos son private final: no hay setters.
    // -----------------------------------------------------------------
    record Incidencia(int id, String usuario, Prioridad prioridad) {
        // Se puede validar en un "constructor compacto"
        Incidencia {
            if (id <= 0) throw new IllegalArgumentException("id debe ser positivo");
        }
    }

    // -----------------------------------------------------------------
    // ENUM con atributos: cada constante llama al constructor
    // -----------------------------------------------------------------
    enum Prioridad {
        BAJA(1), MEDIA(2), ALTA(3);

        private final int peso;

        Prioridad(int peso) { this.peso = peso; }   // constructor privado implícito

        public int peso() { return peso; }
    }

    // -----------------------------------------------------------------
    // INTERFAZ GENÉRICA <T>: el mismo contrato sirve para cualquier tipo
    // -----------------------------------------------------------------
    interface FuenteDatos<T> {
        List<T> cargar();
        void guardar(List<T> datos);
    }

    static class FuenteIncidencias implements FuenteDatos<Incidencia> {
        public List<Incidencia> cargar() {
            return List.of(new Incidencia(1, "Ana", Prioridad.ALTA),
                           new Incidencia(2, "Luis", Prioridad.MEDIA));
        }
        public void guardar(List<Incidencia> datos) {
            System.out.println("Guardadas " + datos.size() + " incidencias.");
        }
    }

    // -----------------------------------------------------------------
    // PATRÓN FACTORY: un método decide QUÉ clase crear según un parámetro.
    // El que llama solo conoce la INTERFAZ, no la clase concreta.
    // -----------------------------------------------------------------
    static class FuenteDatosFactory {
        static FuenteDatos<?> crear(String tipo) {
            // SWITCH CON FLECHA (Java 14+): sin break, y puede DEVOLVER un valor
            return switch (tipo.toLowerCase()) {
                case "incidencias" -> new FuenteIncidencias();
                default -> throw new IllegalArgumentException("No existe la fuente: " + tipo);
            };
        }
    }

    public static void main(String[] args) {
        Incidencia a = new Incidencia(1, "Ana", Prioridad.ALTA);
        Incidencia b = new Incidencia(1, "Ana", Prioridad.ALTA);

        System.out.println("toString automático: " + a);
        System.out.println("getter: a.usuario() = " + a.usuario());
        System.out.println("equals automático: a.equals(b) = " + a.equals(b));

        // enum
        for (Prioridad p : Prioridad.values()) {
            System.out.println(p + " pesa " + p.peso() + " (ordinal " + p.ordinal() + ")");
        }
        System.out.println("Prioridad.valueOf(\"ALTA\") = " + Prioridad.valueOf("ALTA"));

        // switch con flecha como expresión
        Prioridad p = Prioridad.MEDIA;
        String color = switch (p) {
            case ALTA -> "rojo";
            case MEDIA -> "amarillo";
            case BAJA -> "verde";
        };
        System.out.println("Color de " + p + ": " + color);

        // Factory + cast (igual que en el ejercicio)
        @SuppressWarnings("unchecked")
        FuenteDatos<Incidencia> fuente = (FuenteDatos<Incidencia>) FuenteDatosFactory.crear("incidencias");
        List<Incidencia> datos = fuente.cargar();
        fuente.guardar(datos);

        // Constructor compacto validando
        try {
            new Incidencia(-1, "X", Prioridad.BAJA);
        } catch (IllegalArgumentException e) {
            System.out.println("Error controlado: " + e.getMessage());
        }
    }
}
