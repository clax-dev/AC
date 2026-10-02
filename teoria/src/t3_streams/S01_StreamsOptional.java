package t3_streams;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

/**
 * =====================================================================
 *  TEORÍA 3 — STREAMS Y OPTIONAL
 *  Basado en: ejercicios/tema3_streams/EjerciciosStreamsOptional.java
 * =====================================================================
 *
 *  Un STREAM es una "tubería" de operaciones sobre los elementos de una colección.
 *  Cada operación recibe una LAMBDA — por eso se ven después de las interfaces funcionales.
 *
 *      lista.stream()                     <- origen
 *           .filter(Predicate)            <- intermedia: se queda con los que cumplen
 *           .map(Function)                <- intermedia: transforma cada elemento
 *           .sorted(Comparator)           <- intermedia: ordena
 *           .toList() / forEach / count   <- FINAL: produce el resultado
 *
 *  - NO modifica la lista original.
 *  - Sin operación final, no se ejecuta nada.
 *
 *  OPTIONAL<T>: "caja" que puede tener un valor o estar vacía. Evita devolver null.
 *      opt.isPresent() / isEmpty()
 *      opt.orElse(valorPorDefecto)
 *      opt.ifPresent(Consumer)
 *      opt.map(Function)
 *
 *  (Los ejemplos usan datos distintos a los del ejercicio para no darte la solución.)
 */
public class S01_StreamsOptional {

    record Alumno(String nombre, String curso, double nota) { }

    public static void main(String[] args) {
        List<Alumno> alumnos = List.of(
                new Alumno("Ana", "DAM", 8.5),
                new Alumno("Luis", "DAW", 4.0),
                new Alumno("Marta", "DAM", 6.2),
                new Alumno("Pablo", "ASIR", 9.1)
        );

        // filter + toList  (Predicate)
        List<Alumno> aprobados = alumnos.stream()
                .filter(a -> a.nota() >= 5)
                .toList();
        System.out.println("Aprobados: " + aprobados.size());

        // filter + map + toList  (Predicate + Function)
        List<String> nombresDam = alumnos.stream()
                .filter(a -> a.curso().equals("DAM"))
                .map(a -> a.nombre().toLowerCase())
                .toList();
        System.out.println("Nombres DAM: " + nombresDam);

        // sorted + map  (Comparator + Function)
        List<String> ranking = alumnos.stream()
                .sorted(Comparator.comparingDouble(Alumno::nota).reversed())
                .map(a -> a.nombre() + " (" + a.nota() + ")")
                .toList();
        System.out.println("Ranking: " + ranking);

        // mapToDouble + average  ->  OptionalDouble
        double media = alumnos.stream()
                .mapToDouble(Alumno::nota)
                .average()
                .orElse(0);
        System.out.printf("Media: %.2f%n", media);

        // filter + findFirst  ->  Optional
        Optional<Alumno> encontrado = alumnos.stream()
                .filter(a -> a.nombre().equals("Marta"))
                .findFirst();
        Optional<Alumno> noExiste = alumnos.stream()
                .filter(a -> a.nombre().equals("Zoe"))
                .findFirst();

        System.out.println("Marta: " + encontrado.map(Alumno::curso).orElse("no encontrada"));
        System.out.println("Zoe:   " + noExiste.map(Alumno::curso).orElse("no encontrada"));
        encontrado.ifPresent(a -> System.out.println("ifPresent -> " + a));
    }
}
