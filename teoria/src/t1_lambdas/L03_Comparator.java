package t1_lambdas;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * =====================================================================
 *  TEORÍA 1.3 — COMPARATOR: ORDENAR CON LAMBDAS
 *  Basado en: el Comparator "porPrioridad" de EjercicioIncidenciasTemplate
 * =====================================================================
 *
 *  int compare(a, b) devuelve:
 *      NEGATIVO  ->  a va ANTES que b
 *      CERO      ->  son iguales (no se mueven entre sí)
 *      POSITIVO  ->  a va DESPUÉS que b
 *
 *  Por defecto, si devuelves (a - b) el orden es ASCENDENTE (menor a mayor).
 *  Para DESCENDENTE: (b - a)  o  .reversed()
 *
 *  ⚠ Error típico: en el ejercicio se quería "primero las prioridades más altas"
 *    pero el comparator devolvía 1 cuando inc1 pesaba más -> inc1 iba DETRÁS
 *    -> quedaba ordenado de BAJA a ALTA (al revés de lo pedido).
 */
public class L03_Comparator {

    enum Prioridad {
        BAJA(1), MEDIA(2), ALTA(3);
        private final int peso;
        Prioridad(int peso) { this.peso = peso; }
        int peso() { return peso; }
    }

    record Incidencia(int id, String usuario, Prioridad prioridad) { }

    public static void main(String[] args) {
        List<Incidencia> lista = new ArrayList<>(List.of(
                new Incidencia(4, "Pablo", Prioridad.BAJA),
                new Incidencia(3, "Marta", Prioridad.ALTA),
                new Incidencia(2, "Luis", Prioridad.MEDIA),
                new Incidencia(1, "Ana", Prioridad.ALTA),
                new Incidencia(5, "Sara", Prioridad.MEDIA)
        ));

        // -------------------------------------------------------------
        // 1) Forma "manual" con if/else (como en tu ejercicio)
        //    Ordena ASCENDENTE por peso: BAJA, MEDIA, ALTA
        // -------------------------------------------------------------
        Comparator<Incidencia> ascManual = (a, b) -> {
            if (a.prioridad().peso() < b.prioridad().peso()) return -1;  // a antes
            if (a.prioridad().peso() > b.prioridad().peso()) return 1;   // a después
            return 0;                                                    // empate
        };
        lista.sort(ascManual);
        imprimir("1) Ascendente manual", lista);

        // -------------------------------------------------------------
        // 2) Más corto: Integer.compare (evita errores de signo)
        //    Para DESCENDENTE se intercambian a y b.
        // -------------------------------------------------------------
        Comparator<Incidencia> desc = (a, b) -> Integer.compare(b.prioridad().peso(), a.prioridad().peso());
        lista.sort(desc);
        imprimir("2) Descendente con Integer.compare(b, a)", lista);

        // -------------------------------------------------------------
        // 3) Forma MODERNA: Comparator.comparing... + reversed + thenComparing
        //    "Primero prioridad más alta y, si empatan, id más pequeño"
        //    (lo que pedía el enunciado del ejercicio)
        // -------------------------------------------------------------
        Comparator<Incidencia> porPrioridad = Comparator
                .comparingInt((Incidencia i) -> i.prioridad().peso())
                .reversed()                              // ALTA primero
                .thenComparingInt(Incidencia::id);       // desempate: id menor primero
        lista.sort(porPrioridad);
        imprimir("3) Prioridad DESC, luego id ASC", lista);

        // -------------------------------------------------------------
        // 4) Ordenar por texto (orden alfabético) y orden natural
        // -------------------------------------------------------------
        lista.sort(Comparator.comparing(Incidencia::usuario));
        imprimir("4) Por usuario (alfabético)", lista);

        // Los enum tienen orden natural = orden de declaración (BAJA, MEDIA, ALTA)
        lista.sort(Comparator.comparing(Incidencia::prioridad, Comparator.reverseOrder()));
        imprimir("5) Por enum en orden inverso", lista);
    }

    static void imprimir(String titulo, List<Incidencia> lista) {
        System.out.println("\n" + titulo);
        lista.forEach(i -> System.out.println("   #" + i.id() + " " + i.prioridad() + " " + i.usuario()));
    }
}
