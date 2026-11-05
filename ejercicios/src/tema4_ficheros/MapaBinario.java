package tema4_ficheros;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Ejercicio: leer los ficheros binarios mapa.dat y mapa_UTF.dat que ha dado la profesora.
 * <p>
 * Los ficheros están en: ejercicios/datos/   (la ruta es relativa al directorio de trabajo: C:\DAM\Segundo\AC)
 * <p>
 * Teoría de apoyo: teoria/apuntes/04_ficheros.md (apartados 1 y 8.2) y la demo teoria/src/t4_ficheros/F04_FicherosBinarios.java
 * Regla de oro: leer en el MISMO orden y con los MISMOS tipos con los que se escribió.
 */
public class MapaBinario {

    public static void main(String[] args) throws IOException {
        Path ruta = Paths.get("ejercicios", "datos", "mapa.dat");
        byte[] b = Files.readAllBytes(ruta); // guardamos el fichero en la memoria
        ByteBuffer bb = ByteBuffer.wrap(b); // cubo en el que metemos la info
        System.out.println("información interna del cubo -> " + bb); // muestra el tamaño del cubo
        int f = bb.getInt();
        short ff = bb.getShort();
        System.out.println("FLAG DE INICIO -> " + Integer.toHexString(f) + " " + Integer.toHexString(Short.toUnsignedInt(ff)));
        int x = bb.getInt();
        int y = bb.getInt();
        System.out.println("X -> " + x +  " Y -> " + y);
    }
}
