package tema4_ficheros;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class ejemplo {
    static void main() {
        //File f = new File("fiyol.txt");
        /*
        try {
            f.createNewFile();
            FileWriter fw = new FileWriter(f);
            fw.write("Hola fiyol");
            fw.close(); // sin el file close no se escribe nada

            FileReader fr = new FileReader(f);
            System.out.println(fr.readAllAsString());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        */
        File c = new File("carpeta");
        File destino = new File("C:/DAM/Segundo/AC/ejercicios/carpeta");
        boolean creada = c.mkdir();
        System.out.println("La carpeta se ha creado -> " + creada);

        boolean movida = c.renameTo(destino);
    }
}
