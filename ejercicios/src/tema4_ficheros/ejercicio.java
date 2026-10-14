package tema4_ficheros;

import java.io.File;

public class ejercicio {
    static void main() {
        // leer todos los archivos de un directorio, por ejemplo el de teoria/t4_ficheros
        File ruta = new File("C:/DAM/Segundo/AC/teoria"); // ruta que vamos a leer
        if (ruta.isDirectory()) {
            Listar(ruta, 0);
        } else  {
            System.out.println("No se encontró el archivo");
        }
    }

    static void Listar(File f, int nivel) {
        String sangria = "  ".repeat(nivel);
        File[] archivos = f.listFiles();
        for (File archivo : archivos) {
            if (archivo.isDirectory()) {
                System.out.println(sangria + "[DIR]" + archivo.getName());
                Listar(archivo, nivel + 1); // recursividad, se llama a si mismo la funcion
            } else  {
                System.out.println(sangria + "[FILE]" + archivo.getName());
            }
        }
    }
}
