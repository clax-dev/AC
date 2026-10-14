package t4_ficheros;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * =====================================================================
 *  TEORÍA 4.6 — ACCESO ALEATORIO: RandomAccessFile
 * =====================================================================
 *
 *  ACCESO SECUENCIAL (todo lo anterior): para llegar al registro 1000 hay que leer los 999 de antes.
 *  ACCESO ALEATORIO (directo): saltas directamente a la posición que quieras, como en un array.
 *
 *  La clave: REGISTROS DE TAMAÑO FIJO. Si cada registro ocupa siempre los mismos bytes,
 *  la posición del registro n es:     posición = n * TAMAÑO_REGISTRO
 *
 *      RandomAccessFile raf = new RandomAccessFile("fichero.dat", "rw");   // "r" = leer, "rw" = leer y escribir
 *      raf.seek(posicion);        // mover el "cursor" a ese byte
 *      raf.getFilePointer();      // ¿dónde está el cursor?
 *      raf.length();              // tamaño del fichero en bytes
 *      raf.writeInt / readInt / writeDouble / readDouble ...   (igual que Data*Stream)
 *
 *  ⚠ Los String tienen longitud variable -> se rellenan hasta un tamaño fijo (aquí, 20 caracteres).
 *     Cada char ocupa 2 bytes con writeChars.
 *
 *  Diseño del registro de esta demo (producto):
 *      id      int      4 bytes
 *      nombre  20 chars 40 bytes
 *      precio  double   8 bytes
 *      stock   int      4 bytes
 *      ─────────────────────────
 *      TOTAL           56 bytes
 *
 *  Ventaja: puedes LEER o MODIFICAR un registro concreto sin reescribir el fichero entero.
 */
public class F06_AccesoAleatorio {

    static final int LONG_NOMBRE = 20;                       // caracteres
    static final int TAM_REGISTRO = 4 + LONG_NOMBRE * 2 + 8 + 4; // 56 bytes

    public static void main(String[] args) throws IOException {
        Path dir = Path.of("ficheros_demo");
        Files.createDirectories(dir);
        Path fichero = dir.resolve("productos_aleatorio.dat");
        Files.deleteIfExists(fichero);

        try (RandomAccessFile raf = new RandomAccessFile(fichero.toFile(), "rw")) {

            // 1) Escribir 4 registros seguidos
            escribirRegistro(raf, 0, 1, "Teclado", 25.90, 12);
            escribirRegistro(raf, 1, 2, "Ratón", 14.50, 3);
            escribirRegistro(raf, 2, 3, "Monitor", 189.99, 4);
            escribirRegistro(raf, 3, 4, "Silla", 120.00, 8);
            System.out.println("Fichero: " + raf.length() + " bytes = "
                    + raf.length() / TAM_REGISTRO + " registros de " + TAM_REGISTRO + " bytes");

            // 2) Leer DIRECTAMENTE el registro 2 (el tercero), sin pasar por los anteriores
            System.out.println("\nRegistro 2 -> " + leerRegistro(raf, 2));

            // 3) MODIFICAR solo el stock del registro 1 (Ratón): saltar a su campo stock
            long posStock = 1L * TAM_REGISTRO + 4 + LONG_NOMBRE * 2 + 8;
            raf.seek(posStock);
            raf.writeInt(50);
            System.out.println("Stock del Ratón cambiado a 50 -> " + leerRegistro(raf, 1));

            // 4) Recorrer todos
            System.out.println("\n=== Todos los registros ===");
            long n = raf.length() / TAM_REGISTRO;
            for (int i = 0; i < n; i++) {
                System.out.println("  [" + i + "] " + leerRegistro(raf, i));
            }
        }
    }

    static void escribirRegistro(RandomAccessFile raf, int n, int id, String nombre, double precio, int stock)
            throws IOException {
        raf.seek((long) n * TAM_REGISTRO);                    // ir a la posición del registro n
        raf.writeInt(id);
        raf.writeChars(ajustar(nombre));                      // siempre 20 chars = 40 bytes
        raf.writeDouble(precio);
        raf.writeInt(stock);
    }

    static String leerRegistro(RandomAccessFile raf, int n) throws IOException {
        raf.seek((long) n * TAM_REGISTRO);
        int id = raf.readInt();
        StringBuilder nombre = new StringBuilder();
        for (int i = 0; i < LONG_NOMBRE; i++) {
            nombre.append(raf.readChar());
        }
        double precio = raf.readDouble();
        int stock = raf.readInt();
        return String.format("id=%d, nombre=%s, precio=%.2f, stock=%d", id, nombre.toString().trim(), precio, stock);
    }

    // Recorta o rellena con espacios hasta LONG_NOMBRE caracteres
    static String ajustar(String texto) {
        return String.format("%-" + LONG_NOMBRE + "." + LONG_NOMBRE + "s", texto);
    }
}
