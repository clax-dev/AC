package t1_lambdas;

import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * =====================================================================
 *  TEORÍA 1.1 — ¿QUÉ ES UNA LAMBDA? SINTAXIS
 *  Basado en: ejercicios/tema1_lambdas/EjercicioIncidenciasTemplate.java
 * =====================================================================
 *
 * Una lambda es una FUNCIÓN ANÓNIMA (sin nombre) que:
 *   - se puede guardar en una variable,
 *   - se puede pasar como parámetro a un método,
 *   - NO se ejecuta al declararla, sino cuando se llama a su método
 *     (apply, test, accept, get, run...).
 *
 *      (parámetros) -> expresión
 *      (parámetros) -> { sentencias; return valor; }
 *
 * Una lambda SIEMPRE se asigna a una INTERFAZ FUNCIONAL:
 * una interfaz con UN ÚNICO método abstracto (ver L02).
 *
 * ▶ Ejecuta este main y compara la salida con el código.
 */
public class L01_SintaxisLambda {

    // Un record como el de tu ejercicio de incidencias (versión reducida).
    record Incidencia(int id, String usuario, String descripcion) { }

    public static void main(String[] args) {
        Incidencia inc = new Incidencia(1, "Ana", "No puede acceder al sistema");

        // ---------------------------------------------------------------
        // 1) SIN parámetros  ->  ()
        // ---------------------------------------------------------------
        Supplier<String> saludo = () -> "Hola desde una lambda";
        System.out.println(saludo.get());

        // ---------------------------------------------------------------
        // 2) UN parámetro  ->  los paréntesis son opcionales
        //    Igual que en tu ejercicio:  inc -> "#" + inc.id() + ...
        // ---------------------------------------------------------------
        Function<Incidencia, String> resumen = i -> "#" + i.id() + " | " + i.usuario();
        System.out.println(resumen.apply(inc));

        // ---------------------------------------------------------------
        // 3) VARIOS parámetros  ->  paréntesis obligatorios
        // ---------------------------------------------------------------
        BiFunction<Integer, Integer, Integer> suma = (a, b) -> a + b;
        System.out.println("2 + 3 = " + suma.apply(2, 3));

        // ---------------------------------------------------------------
        // 4) Con BLOQUE { }  ->  el return es OBLIGATORIO
        //    Sin llaves, el return es implícito.
        // ---------------------------------------------------------------
        Function<Incidencia, String> resumenLargo = i -> {
            String desc = i.descripcion().trim();     // varias sentencias
            return i.usuario() + ": " + desc;          // return explícito
        };
        System.out.println(resumenLargo.apply(inc));

        // ---------------------------------------------------------------
        // 5) Tipos explícitos (opcional; normalmente Java los deduce)
        // ---------------------------------------------------------------
        BiFunction<Integer, Integer, Integer> multiplica = (Integer a, Integer b) -> a * b;
        System.out.println("4 * 5 = " + multiplica.apply(4, 5));

        // ---------------------------------------------------------------
        // 6) Una lambda puede USAR OTRA lambda
        //    (en tu ejercicio, 'auditoria' usa 'resumen')
        // ---------------------------------------------------------------
        Function<Incidencia, String> auditoria = i -> "AUDITORÍA: " + resumen.apply(i);
        System.out.println(auditoria.apply(inc));

        // ---------------------------------------------------------------
        // 7) REGLA: variables "efectivamente final"
        //    Una lambda solo puede usar variables locales que NO cambian.
        // ---------------------------------------------------------------
        String prefijo = ">> ";                       // no se modifica después -> OK
        Function<String, String> conPrefijo = s -> prefijo + s;
        System.out.println(conPrefijo.apply("texto"));

        // int contador = 0;
        // Runnable r = () -> contador++;            // ❌ NO COMPILA: contador cambia

        // ---------------------------------------------------------------
        // 8) La lambda NO se ejecuta al declararla
        // ---------------------------------------------------------------
        Runnable aviso = () -> System.out.println("¡Ahora sí me ejecuto!");
        System.out.println("La lambda 'aviso' ya existe, pero aún no se ha ejecutado...");
        aviso.run();
    }
}
