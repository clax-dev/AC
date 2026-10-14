package t4_ficheros;

import java.io.IOException;
import java.io.NotSerializableException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serial;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * =====================================================================
 *  TEORÍA 4.5 — SERIALIZACIÓN: guardar OBJETOS enteros en un fichero
 * =====================================================================
 *
 *  SERIALIZAR   = convertir un objeto en bytes para guardarlo (o enviarlo por la red)
 *  DESERIALIZAR = reconstruir el objeto a partir de esos bytes
 *
 *      ObjectOutputStream.writeObject(obj)     ObjectInputStream.readObject()  -> devuelve Object: hay que hacer CAST
 *
 *  REQUISITOS
 *    1. La clase tiene que implementar Serializable (es una interfaz "marca": no tiene métodos).
 *       Si no -> NotSerializableException.
 *    2. Todos sus atributos también tienen que ser serializables (String, int, List... lo son).
 *    3. serialVersionUID: "número de versión" de la clase. Si cambias la clase y no coincide
 *       con la del fichero -> InvalidClassException. Decláralo tú para controlarlo.
 *
 *  TRANSIENT: un atributo marcado transient NO se guarda (contraseñas, cachés, datos calculados).
 *             Al leer, vale su valor por defecto (null, 0, false).
 *
 *  Truco: guardar la LISTA entera de una vez (ArrayList es serializable) es más fácil que objeto a objeto.
 *
 *  | CSV (4.3)                         | Serialización (aquí)                    |
 *  |-----------------------------------|-----------------------------------------|
 *  | legible, lo abre Excel            | binario, solo lo entiende Java          |
 *  | tienes que convertir cada campo   | se guarda el objeto entero en 1 línea   |
 *  | sirve entre lenguajes             | solo Java (y misma versión de la clase) |
 */
public class F05_SerializacionObjetos {

    // Un record serializable: basta con "implements Serializable"
    record Incidencia(int id, String usuario, String descripcion, String prioridad, boolean resuelta)
            implements Serializable {
        @Serial
        private static final long serialVersionUID = 1L;
    }

    // Una clase normal con un atributo transient
    static class Usuario implements Serializable {
        @Serial
        private static final long serialVersionUID = 1L;
        private final String nombre;
        private transient String password;                  // NO se guarda

        Usuario(String nombre, String password) { this.nombre = nombre; this.password = password; }

        @Override
        public String toString() { return "Usuario{nombre=" + nombre + ", password=" + password + "}"; }
    }

    // Una clase que NO es serializable (para ver el error)
    static class Cafetera {
        String marca = "Genérica";
    }

    @SuppressWarnings("unchecked")
    public static void main(String[] args) throws IOException, ClassNotFoundException {
        Path dir = Path.of("ficheros_demo");
        Files.createDirectories(dir);

        // ==============================================================
        // 1) Guardar y cargar una LISTA de objetos de golpe
        // ==============================================================
        Path fichero = dir.resolve("incidencias.ser");
        List<Incidencia> incidencias = new ArrayList<>(List.of(
                new Incidencia(1, "Ana", "No puede acceder", "ALTA", false),
                new Incidencia(2, "Luis", "Error de impresión", "MEDIA", true)));

        try (ObjectOutputStream out = new ObjectOutputStream(Files.newOutputStream(fichero))) {
            out.writeObject(incidencias);
        }
        System.out.println("Guardadas " + incidencias.size() + " incidencias en " + fichero
                + " (" + Files.size(fichero) + " bytes)");

        List<Incidencia> leidas;
        try (ObjectInputStream in = new ObjectInputStream(Files.newInputStream(fichero))) {
            leidas = (List<Incidencia>) in.readObject();     // readObject devuelve Object -> CAST
        }
        System.out.println("Leídas: " + leidas);
        System.out.println("¿Iguales? " + leidas.equals(incidencias));

        // ==============================================================
        // 2) transient: la contraseña no se guarda
        // ==============================================================
        Path fUsuario = dir.resolve("usuario.ser");
        try (ObjectOutputStream out = new ObjectOutputStream(Files.newOutputStream(fUsuario))) {
            out.writeObject(new Usuario("ana", "secreto123"));
        }
        try (ObjectInputStream in = new ObjectInputStream(Files.newInputStream(fUsuario))) {
            System.out.println("\nUsuario leído: " + in.readObject() + "   <- password es null (transient)");
        }

        // ==============================================================
        // 3) Error típico: la clase no implementa Serializable
        // ==============================================================
        try (ObjectOutputStream out = new ObjectOutputStream(Files.newOutputStream(dir.resolve("error.ser")))) {
            out.writeObject(new Cafetera());
        } catch (NotSerializableException e) {
            System.out.println("\nError controlado: NotSerializableException -> " + e.getMessage());
        }
    }
}
