package t1_lambdas;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * =====================================================================
 *  TEORÍA 1.4 — REFERENCIAS A MÉTODOS ( :: )
 * =====================================================================
 *
 *  Si una lambda SOLO llama a un método que ya existe, se puede abreviar:
 *
 *  | Tipo                          | Lambda                      | Referencia             |
 *  |-------------------------------|-----------------------------|------------------------|
 *  | Método estático               | x -> Integer.parseInt(x)    | Integer::parseInt      |
 *  | Método de un objeto concreto  | x -> System.out.println(x)  | System.out::println    |
 *  | Método del propio parámetro   | inc -> inc.id()             | Incidencia::id         |
 *  | Constructor                   | () -> new ArrayList<>()     | ArrayList::new         |
 */
public class L04_ReferenciasMetodo {

    record Incidencia(int id, String usuario) { }

    static String formatear(Incidencia i) {
        return "[" + i.id() + "] " + i.usuario();
    }

    public static void main(String[] args) {
        List<Incidencia> lista = List.of(new Incidencia(1, "Ana"), new Incidencia(2, "Luis"));

        // 1) Método de un objeto concreto: System.out::println
        lista.forEach(i -> System.out.println(i));   // lambda
        lista.forEach(System.out::println);          // equivalente

        // 2) Método del propio parámetro (getter del record)
        Function<Incidencia, String> usuario1 = i -> i.usuario();
        Function<Incidencia, String> usuario2 = Incidencia::usuario;
        System.out.println(usuario1.apply(lista.get(0)) + " == " + usuario2.apply(lista.get(0)));

        // 3) Método estático (propio o de Java)
        Function<Incidencia, String> fmt = L04_ReferenciasMetodo::formatear;
        Function<String, Integer> aNumero = Integer::parseInt;
        System.out.println(fmt.apply(lista.get(1)) + " | parseInt(\"42\") = " + aNumero.apply("42"));

        // 4) Constructor
        Supplier<List<String>> nuevaLista = ArrayList::new;
        List<String> nombres = nuevaLista.get();
        nombres.add("Ana");
        System.out.println("Lista creada con ArrayList::new -> " + nombres);
    }
}
