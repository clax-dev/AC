package t4_ficheros;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * =====================================================================
 *  TEORÍA 4.4 — FICHEROS BINARIOS: bytes y tipos primitivos
 * =====================================================================
 *
 *  FICHERO BINARIO = bytes "en bruto". No se lee con el Bloc de notas (salen símbolos raros).
 *  Ventaja: ocupa menos y es más rápido (un int SIEMPRE ocupa 4 bytes, sea 7 o 2.000.000).
 *
 *  | Texto (tema 4.2)            | Binario (aquí)                    |
 *  |-----------------------------|-----------------------------------|
 *  | Reader / Writer             | InputStream / OutputStream        |
 *  | trabaja con CARACTERES      | trabaja con BYTES                 |
 *  | legible por personas        | legible solo por programas        |
 *
 *  DataOutputStream / DataInputStream: escriben y leen TIPOS PRIMITIVOS
 *      writeInt(5)  writeDouble(2.5)  writeUTF("texto")  writeBoolean(true)
 *      readInt()    readDouble()      readUTF()          readBoolean()
 *
 *  ⚠ REGLA DE ORO: se lee EXACTAMENTE en el mismo ORDEN y con los mismos TIPOS con que se escribió.
 *     Si escribes int, double, String y lees double, int, String -> basura (o excepción).
 *
 *  ⚠ FIN DEL FICHERO: los readXxx() no devuelven null; lanzan EOFException.
 *     Para leer "hasta el final" se captura esa excepción (o se guarda antes cuántos registros hay).
 */
public class F04_FicherosBinarios {

    public static void main(String[] args) throws IOException {
        Path dir = Path.of("ficheros_demo");
        Files.createDirectories(dir);
        Path fichero = dir.resolve("productos.dat");

        // ==============================================================
        // 1) ESCRIBIR registros: código (String), precio (double), stock (int)
        // ==============================================================
        try (DataOutputStream out = new DataOutputStream(
                new BufferedOutputStream(Files.newOutputStream(fichero)))) {
            escribir(out, "P001", 25.90, 12);
            escribir(out, "P002", 14.50, 3);
            escribir(out, "P003", 189.99, 4);
        }
        System.out.println("Escrito " + fichero + " -> " + Files.size(fichero) + " bytes");

        // ==============================================================
        // 2) LEER hasta el final (EOFException marca el fin)
        // ==============================================================
        System.out.println("\n=== Lectura ===");
        int total = 0;
        try (DataInputStream in = new DataInputStream(
                new BufferedInputStream(Files.newInputStream(fichero)))) {
            while (true) {
                String codigo = in.readUTF();               // MISMO orden que al escribir
                double precio = in.readDouble();
                int stock = in.readInt();
                System.out.printf("  %s | %.2f € | stock %d%n", codigo, precio, stock);
                total++;
            }
        } catch (EOFException fin) {
            System.out.println("  (fin del fichero: " + total + " registros)");
        }

        // ==============================================================
        // 3) Copiar un fichero byte a byte con un búfer (cómo funciona por dentro Files.copy)
        // ==============================================================
        Path copia = dir.resolve("productos_copia.dat");
        try (InputStream in = Files.newInputStream(fichero);
             OutputStream out = Files.newOutputStream(copia)) {   // dos recursos en el mismo try
            byte[] bufer = new byte[1024];
            int leidos;
            while ((leidos = in.read(bufer)) != -1) {     // -1 = fin del fichero
                out.write(bufer, 0, leidos);
            }
        }
        System.out.println("\nCopia: " + Files.size(copia) + " bytes (¿igual? "
                + (Files.mismatch(fichero, copia) == -1) + ")");

        // ==============================================================
        // 4) Así se ve un binario si lo abres como texto
        // ==============================================================
        System.out.println("\nComo texto (ilegible): " + new String(Files.readAllBytes(fichero)).substring(0, 20) + "...");
    }

    static void escribir(DataOutputStream out, String codigo, double precio, int stock) throws IOException {
        out.writeUTF(codigo);
        out.writeDouble(precio);
        out.writeInt(stock);
    }
}
