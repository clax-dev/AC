package t4_ficheros;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

/**
 * =====================================================================
 *  TEORÍA 4.1 — RUTAS Y DIRECTORIOS: Path, Files (y la clase antigua File)
 * =====================================================================
 *
 *  Antes de leer o escribir, hay que saber DÓNDE está el fichero.
 *
 *  Java tiene dos APIs:
 *    - java.io.File      -> la ANTIGUA (Java 1). La verás en apuntes y código viejo.
 *    - java.nio.file     -> la MODERNA (Java 7+): Path + Files. Úsala por defecto.
 *
 *  PATH = una ruta (no el fichero en sí; puede que ni exista todavía)
 *      Path p = Path.of("ficheros_demo", "datos.txt");   // ficheros_demo/datos.txt
 *
 *  RUTA RELATIVA vs ABSOLUTA
 *      "ficheros_demo/datos.txt"        -> relativa al DIRECTORIO DE TRABAJO
 *      "C:\\DAM\\Segundo\\AC\\datos.txt" -> absoluta
 *      ⚠ En IntelliJ, el directorio de trabajo suele ser la carpeta del MÓDULO (aquí: teoria/).
 *        Si "no encuentra el fichero", casi siempre es por esto: imprime p.toAbsolutePath().
 *
 *  FILES = métodos estáticos para trabajar con rutas
 *      Files.exists(p)  Files.isDirectory(p)  Files.size(p)
 *      Files.createDirectories(p)  Files.createFile(p)  Files.delete(p)  Files.deleteIfExists(p)
 *      Files.list(dir)  -> Stream<Path> con el contenido de un directorio (¡streams otra vez!)
 *      Files.copy(a, b)  Files.move(a, b)
 *
 *  Casi todo lanza IOException (comprobada) -> try/catch o throws.
 *
 *  ▶ Todas las demos de este tema trabajan dentro de la carpeta "ficheros_demo" (está en .gitignore).
 */
public class F01_RutasYDirectorios {

    public static void main(String[] args) throws IOException {
        Path dir = Path.of("ficheros_demo");
        Path fichero = dir.resolve("prueba.txt");          // resolve = "dentro de dir"

        // ==============================================================
        // 1) Información de una ruta
        // ==============================================================
        System.out.println("Ruta relativa:  " + fichero);
        System.out.println("Ruta absoluta:  " + fichero.toAbsolutePath());
        System.out.println("Nombre:         " + fichero.getFileName());
        System.out.println("Carpeta padre:  " + fichero.getParent());
        System.out.println("¿Existe?        " + Files.exists(fichero));

        // ==============================================================
        // 2) Crear carpeta y fichero
        // ==============================================================
        Files.createDirectories(dir);                       // no falla si ya existe
        if (!Files.exists(fichero)) {
            Files.createFile(fichero);                      // ⚠ falla si ya existe
        }
        Files.writeString(fichero, "Hola ficheros");
        System.out.println("\nCreado. ¿Existe? " + Files.exists(fichero)
                + " | ¿Es carpeta? " + Files.isDirectory(fichero)
                + " | Tamaño: " + Files.size(fichero) + " bytes");

        // ==============================================================
        // 3) Listar una carpeta (devuelve un Stream -> try-with-resources)
        // ==============================================================
        Files.createDirectories(dir.resolve("subcarpeta"));
        System.out.println("\nContenido de " + dir + ":");
        try (Stream<Path> contenido = Files.list(dir)) {
            contenido.forEach(p -> System.out.println("  " + (Files.isDirectory(p) ? "[DIR] " : "      ")
                    + p.getFileName()));
        }

        // ==============================================================
        // 4) Copiar, mover y borrar
        // ==============================================================
        Path copia = dir.resolve("copia.txt");
        Files.copy(fichero, copia, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
        System.out.println("\nCopia creada: " + Files.exists(copia));
        Files.deleteIfExists(copia);
        Files.deleteIfExists(dir.resolve("subcarpeta"));    // solo borra carpetas VACÍAS
        System.out.println("Copia y subcarpeta borradas: " + !Files.exists(copia));

        // ==============================================================
        // 5) La clase antigua File (para reconocerla)
        // ==============================================================
        File antiguo = new File("ficheros_demo/prueba.txt");
        System.out.println("\n[java.io.File] existe=" + antiguo.exists() + " tamaño=" + antiguo.length());
        Path convertido = antiguo.toPath();                 // de File a Path
        System.out.println("Convertido a Path: " + convertido);
    }
}
