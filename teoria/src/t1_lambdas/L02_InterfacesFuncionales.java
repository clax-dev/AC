package t1_lambdas;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

/**
 * =====================================================================
 *  TEORÍA 1.2 — INTERFACES FUNCIONALES (java.util.function)
 *  Basado en: ejercicios/tema1_lambdas/EjercicioIncidenciasTemplate.java
 * =====================================================================
 *
 *  | Interfaz          | Método        | Recibe -> Devuelve | Uso típico          |
 *  |-------------------|---------------|--------------------|---------------------|
 *  | Function<T,R>     | apply(t)      | T -> R             | TRANSFORMAR         |
 *  | Predicate<T>      | test(t)       | T -> boolean       | VALIDAR / FILTRAR   |
 *  | Consumer<T>       | accept(t)     | T -> (nada)        | HACER UNA ACCIÓN    |
 *  | Supplier<T>       | get()         | (nada) -> T        | GENERAR / CREAR     |
 *  | UnaryOperator<T>  | apply(t)      | T -> T             | modificar mismo tipo|
 *  | BiFunction<T,U,R> | apply(t,u)    | T,U -> R           | dos entradas        |
 *  | BiConsumer<T,U>   | accept(t,u)   | T,U -> (nada)      | Map.forEach         |
 *  | Comparator<T>     | compare(a,b)  | T,T -> int         | ORDENAR (ver L03)   |
 *  | Runnable          | run()         | (nada) -> (nada)   | HILOS (ver PSP)     |
 *
 *  TRUCO para recordar:
 *     ¿Devuelve boolean?           -> Predicate
 *     ¿No devuelve nada?           -> Consumer
 *     ¿No recibe nada?             -> Supplier
 *     ¿Recibe algo y devuelve algo?-> Function
 */
public class L02_InterfacesFuncionales {

    // Mismo modelo que en EjercicioIncidenciasTemplate
    enum Prioridad {
        BAJA(1), MEDIA(2), ALTA(3);
        private final int peso;
        Prioridad(int peso) { this.peso = peso; }
        int peso() { return peso; }
    }

    record Incidencia(int id, String usuario, String descripcion,
                      Prioridad prioridad, boolean resuelta) { }

    static List<Incidencia> datos() {
        return List.of(
                new Incidencia(1, "Ana", " No puede acceder al sistema ", Prioridad.ALTA, false),
                new Incidencia(2, "Luis", "Error de impresión", Prioridad.MEDIA, true),
                new Incidencia(3, "Marta", "Contraseña caducada", Prioridad.ALTA, false),
                new Incidencia(4, "", "Consulta sobre el informe", Prioridad.BAJA, false),
                new Incidencia(5, "Sara", "El programa se cierra", Prioridad.ALTA, true)
        );
    }

    public static void main(String[] args) {
        List<Incidencia> incidencias = datos();

        // =============================================================
        // 1) FUNCTION<T, R>  — TRANSFORMA un objeto en otra cosa
        // =============================================================
        Function<Incidencia, String> resumen =
                inc -> "#" + inc.id() + " | " + inc.usuario() + " | "
                        + inc.prioridad() + " | " + inc.descripcion().trim();

        System.out.println("== Function ==");
        System.out.println(resumen.apply(incidencias.get(0)));

        // Encadenar Functions: andThen (primero esta, luego la otra)
        Function<String, String> mayus = String::toUpperCase;
        Function<Incidencia, String> resumenMayus = resumen.andThen(mayus);
        System.out.println(resumenMayus.apply(incidencias.get(0)));

        // =============================================================
        // 2) PREDICATE<T>  — devuelve TRUE/FALSE (validar / filtrar)
        // =============================================================
        Predicate<Incidencia> datosValidos = inc ->
                inc.usuario() != null && !inc.usuario().isBlank()
                        && inc.descripcion() != null && !inc.descripcion().isBlank();

        // ⚠ "abiertas" = NO resueltas  ->  hay que NEGAR resuelta
        Predicate<Incidencia> abiertas = inc -> !inc.resuelta();
        Predicate<Incidencia> prioridadAlta = inc -> inc.prioridad() == Prioridad.ALTA;

        System.out.println("\n== Predicate ==");
        System.out.println("¿#1 es válida? " + datosValidos.test(incidencias.get(0)));
        System.out.println("¿#4 es válida? " + datosValidos.test(incidencias.get(3)) + " (usuario vacío)");

        // Combinar predicados: and / or / negate
        Predicate<Incidencia> urgentes = datosValidos.and(abiertas).and(prioridadAlta);
        Predicate<Incidencia> cerradas = abiertas.negate();
        Predicate<Incidencia> altaOCerrada = prioridadAlta.or(cerradas);

        for (Incidencia inc : incidencias) {
            System.out.printf("#%d  urgente=%-5b cerrada=%-5b alta-o-cerrada=%b%n",
                    inc.id(), urgentes.test(inc), cerradas.test(inc), altaOCerrada.test(inc));
        }

        // Los predicados se usan mucho con removeIf:
        List<Incidencia> copia = new ArrayList<>(incidencias);
        copia.removeIf(cerradas);                // elimina las que cumplan el predicado
        System.out.println("Tras removeIf(cerradas) quedan: " + copia.size());

        // =============================================================
        // 3) CONSUMER<T>  — hace una ACCIÓN, no devuelve nada
        // =============================================================
        Consumer<Incidencia> auditoria =
                inc -> System.out.println("AUDITORÍA: procesada incidencia " + resumen.apply(inc));

        System.out.println("\n== Consumer ==");
        incidencias.forEach(auditoria);          // forEach recibe un Consumer

        // Encadenar Consumers: andThen
        Consumer<Incidencia> notificar = inc -> System.out.println("   -> email enviado a " + inc.usuario());
        Consumer<Incidencia> auditarYNotificar = auditoria.andThen(notificar);
        for (Incidencia inc : incidencias) {
            if (urgentes.test(inc)) {
                auditarYNotificar.accept(inc);
            }
        }

        // =============================================================
        // 4) SUPPLIER<T>  — no recibe nada, DEVUELVE/GENERA algo
        // =============================================================
        Supplier<List<Incidencia>> fuente = L02_InterfacesFuncionales::datos;
        Supplier<Integer> aleatorio1o2 = () -> (int) (Math.random() * 2) + 1; // como en Cafeteria

        System.out.println("\n== Supplier ==");
        System.out.println("Cargadas " + fuente.get().size() + " incidencias");
        System.out.println("Número aleatorio (1 o 2): " + aleatorio1o2.get());

        // =============================================================
        // 5) UNARYOPERATOR<T>  — Function donde entrada y salida son del mismo tipo
        // =============================================================
        UnaryOperator<String> limpiar = s -> s.trim().replaceAll("\\s+", " ");
        System.out.println("\n== UnaryOperator ==");
        System.out.println("[" + limpiar.apply("  No   puede   acceder  ") + "]");

        // =============================================================
        // 6) BICONSUMER<T, U>  — dos entradas, sin salida (Map.forEach)
        // =============================================================
        BiConsumer<Integer, String> mostrar = (id, texto) -> System.out.println(id + " -> " + texto);
        System.out.println("\n== BiConsumer ==");
        mostrar.accept(1, "Blinding Lights");

        // =============================================================
        // 7) INTERFAZ FUNCIONAL PROPIA
        //    Cualquier interfaz con UN solo método abstracto vale.
        //    @FunctionalInterface hace que el compilador lo compruebe.
        // =============================================================
        Calculadora suma = (a, b) -> a + b;
        Calculadora max = Math::max;
        System.out.println("\n== Interfaz propia ==");
        System.out.println("suma(3,4) = " + suma.calcular(3, 4) + ", max(3,4) = " + max.calcular(3, 4));
    }

    @FunctionalInterface
    interface Calculadora {
        int calcular(int a, int b);   // único método abstracto
    }
}
