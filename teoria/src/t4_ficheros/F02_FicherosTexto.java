package t4_ficheros;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.stream.Stream;

/**
 * =====================================================================
 *  TEORÍA 4.2 — FICHEROS DE TEXTO: escribir y leer líneas
 * =====================================================================
 *
 *  FICHERO DE TEXTO = caracteres legibles (puedes abrirlo con el Bloc de notas).
 *  Siempre indica la CODIFICACIÓN: StandardCharsets.UTF_8 (si no, las tildes y la ñ pueden romperse).
 *
 *  FORMAS DE ESCRIBIR
 *    - Files.writeString(p, texto)             -> todo de golpe (sobrescribe)
 *    - Files.write(p, listaDeLineas)           -> una línea por elemento
 *    - BufferedWriter (Files.newBufferedWriter) -> poco a poco, eficiente para mucho texto
 *    - StandardOpenOption.APPEND               -> AÑADIR al final en vez de sobrescribir
 *
 *  FORMAS DE LEER
 *    - Files.readString(p)                     -> todo en un String
 *    - Files.readAllLines(p)                   -> List<String> (ojo con ficheros enormes)
 *    - BufferedReader.readLine()               -> línea a línea; devuelve null al final
 *    - Files.lines(p)                          -> Stream<String>: filter, map... ¡como en el tema 3!
 *
 *  TRY-WITH-RESOURCES: lo que se abre, se cierra SOLO al salir del bloque, aunque haya excepción.
 *      try (BufferedReader br = Files.newBufferedReader(p)) {
 *          ...
 *      }   // aquí se cierra automáticamente
 *  ⚠ Si un fichero no se cierra, lo escrito puede NO llegar al disco (se queda en el buffer).
 */
public class F02_FicherosTexto {

    public static void main(String[] args) throws IOException {
        Path dir = Path.of("ficheros_demo");
        Files.createDirectories(dir);
        Path log = dir.resolve("incidencias.log");

        // ==============================================================
        // 1) Escribir con BufferedWriter (sobrescribe el fichero)
        // ==============================================================
        try (BufferedWriter bw = Files.newBufferedWriter(log, StandardCharsets.UTF_8)) {
            bw.write("#1 | Ana | ALTA | No puede acceder al sistema");
            bw.newLine();                                  // salto de línea del sistema
            bw.write("#2 | Luis | MEDIA | Error de impresión");
            bw.newLine();
        }                                                  // se cierra (y se vuelca al disco) aquí

        // ==============================================================
        // 2) AÑADIR al final (APPEND) sin borrar lo anterior
        // ==============================================================
        Files.write(log,
                List.of("#3 | Marta | ALTA | Contraseña caducada", "#4 | Pablo | BAJA | Consulta sobre el informe"),
                StandardCharsets.UTF_8, StandardOpenOption.APPEND);

        // ==============================================================
        // 3) Leer línea a línea con BufferedReader (la forma "clásica")
        // ==============================================================
        System.out.println("=== BufferedReader ===");
        try (BufferedReader br = Files.newBufferedReader(log, StandardCharsets.UTF_8)) {
            String linea;
            int n = 0;
            while ((linea = br.readLine()) != null) {      // null = fin del fichero
                System.out.println(++n + ": " + linea);
            }
        }

        // ==============================================================
        // 4) Leer todo de golpe
        // ==============================================================
        List<String> lineas = Files.readAllLines(log, StandardCharsets.UTF_8);
        System.out.println("\n=== readAllLines === " + lineas.size() + " líneas");

        // ==============================================================
        // 5) Files.lines + STREAMS: solo las de prioridad ALTA, en mayúsculas
        //    (Files.lines abre el fichero -> también va en try-with-resources)
        // ==============================================================
        System.out.println("\n=== Files.lines + filter/map ===");
        try (Stream<String> stream = Files.lines(log, StandardCharsets.UTF_8)) {
            stream.filter(l -> l.contains("| ALTA |"))
                  .map(String::toUpperCase)
                  .forEach(System.out::println);
        }

        // ==============================================================
        // 6) Qué pasa si el fichero no existe
        // ==============================================================
        try {
            Files.readAllLines(dir.resolve("no_existe.txt"));
        } catch (java.nio.file.NoSuchFileException e) {
            System.out.println("\nError controlado: no existe " + e.getMessage());
        }
    }
}
