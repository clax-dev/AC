package biodam;

import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static java.nio.file.Files.createDirectories;

/**
 * Tarea semanal: Sistema de Ingesta Asíncrona y Auditoría de Datos (BioDAM).
 * <p>
 * El programa coge un CSV con diagnósticos de pacientes, comprueba que la ruta es segura,
 * saca unas estadísticas y al final mueve el fichero a procesados. Todo lo que pasa se va
 * apuntando en un log con su hora exacta.
 * <p>
 * Va por bloques, y cada bloque tiene sus métodos:
 * <ul>
 *     <li><b>Bloque 1 (logs):</b> {@link #prepareLog()} crea un log nuevo cada vez que arranca
 *     el programa y {@link #writeLog(String, String)} escribe las líneas con INFO, WARN o ERROR.</li>
 *     <li><b>Bloque 2 (seguridad):</b> {@link #validatePath(String, Path)} mira que la ruta no se
 *     salga de <code>datos/</code> (path traversal) y que el fichero exista.</li>
 *     <li><b>Bloque 3 (streams):</b> {@link #csvIntoList(Path)} lee el CSV una sola vez y los otros
 *     tres métodos sacan los cálculos de esa lista, sin bucles, solo con streams y lambdas.</li>
 *     <li><b>Bloque 4 (cierre):</b> {@link #moveToProcessed(Path)} mueve el CSV a
 *     <code>salida/procesados/</code> y lo sobrescribe si ya estaba.</li>
 * </ul>
 */
public class BioDAM {
    /** Ruta del log de esta ejecución. La rellena prepareLog() y la usa writeLog() para escribir. */
    private static Path fileLog;

    /**
     * Arranca el programa y va llamando a los métodos de cada bloque en orden.
     *
     * @param args no se usan
     * @throws IOException si falla algo al leer, escribir o mover ficheros
     */
    public static void main(String[] args) throws IOException {
        // ── BLOQUE 1: logs ── preparamos el log de esta ejecución
        prepareLog();
        writeLog("INFO", "Aplicacion iniciada");

        // ── BLOQUE 2: seguridad ── validamos la ruta que pide el usuario (simulada)
        Path rutaValidada = validatePath("datos/diagnosticos_2026.csv", Paths.get("datos"));

        // ── BLOQUE 3: streams ── leemos el CSV una sola vez y sacamos los cálculos
        List<String> lines = csvIntoList(rutaValidada);
        System.out.println("Total de registros: " + countElements(lines));
        System.out.println("Diagnosticos activos: " + activeDiagnoses(lines));
        System.out.println("Edad media de diabetes: " + diabetesAverageAge(lines));

        // ── BLOQUE 4: cierre ── apuntamos que ha ido bien y movemos el CSV a procesados
        writeLog("INFO", "Importacion completada correctamente");
        moveToProcessed(rutaValidada);
    }

    /**
     * Prepara el log de esta ejecución. Saca el nombre con la fecha y hora de ahora
     * (<code>auditoria_yyyyMMddHHmmss.log</code>), crea las carpetas <code>salida/sistema_logs</code>
     * y guarda la ruta en {@link #fileLog}.
     * <p>
     * No crea el fichero: eso lo hace writeLog() la primera vez que escribe.
     *
     * @throws IOException si no se pueden crear las carpetas
     */
    private static void prepareLog() throws IOException {
        // variable declaration
        DateTimeFormatter f = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        LocalDateTime d = LocalDateTime.now();

        // creacion del archivo .log
        String currentDate = d.format(f); // formato -> yyyyMMddHHmmss
        String file = "auditoria_" + currentDate + ".log";

        // decimos que ruta es -> salida/sistema_logs
        Path folderLog = Paths.get("salida", "sistema_logs");
        fileLog = folderLog.resolve(file); // esto hace que quede salida/sistema_logs/auditoria_yyyyMMddHHmmss.log

        // creamos las carpetas
        createDirectories(folderLog);

        System.out.println("Log de esta ejecucion -> " + fileLog);
    }

    /**
     * Escribe una línea en el log con este formato:
     * <code>[yyyy-MM-dd HH:mm:ss.SSS] NIVEL mensaje</code>.
     * <p>
     * Usa CREATE y APPEND, así que la primera vez crea el fichero y luego va añadiendo
     * las líneas al final sin borrar las de antes.
     *
     * @param n nivel del mensaje: INFO, WARN o ERROR
     * @param m el mensaje que queremos apuntar
     * @throws IOException si no se puede escribir en el log
     */
    private static void writeLog(String n, String m) throws IOException {
        // variable declaration
        LocalDateTime d = LocalDateTime.now();
        DateTimeFormatter f = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
        String mensaje = "[" + d.format(f) + "] " + n + " " + m + System.lineSeparator(); // salto de línea: writeString no lo añade solo

        // create and modify the log, we create one if the file dont exist with CREAT and APPEND.
        Files.writeString(fileLog, mensaje, StandardOpenOption.CREATE, StandardOpenOption.APPEND);
    }

    /**
     * Comprueba que la ruta que pide el usuario es segura y que el fichero existe.
     * <ol>
     *     <li>Pasa las dos rutas a absolutas y normalizadas (quita los <code>..</code>) y mira con
     *     startsWith que la del usuario esté dentro de la base. Si se sale es un path traversal:
     *     apunta un WARN y lanza SecurityException.</li>
     *     <li>Si la ruta está bien pero el fichero no existe, apunta un ERROR y cierra el programa.</li>
     * </ol>
     *
     * @param rutaUsuarioString la ruta que escribe el usuario, por ejemplo <code>datos/diagnosticos_2026.csv</code>
     * @param basePath la carpeta permitida, en este caso <code>datos</code>
     * @return la ruta ya validada, absoluta y normalizada
     * @throws SecurityException si la ruta se sale de la carpeta permitida
     * @throws IOException si no se puede escribir en el log
     */
    private static Path validatePath(String rutaUsuarioString, Path basePath) throws IOException {
        //variable declaration/paths
        Path userPath = Paths.get(rutaUsuarioString);

        // validator
        if (!userPath.toAbsolutePath().normalize().startsWith(basePath.toAbsolutePath().normalize())) {
            // if the path isnt the same as the base path
            writeLog("WARN", "Intento de path traversal bloqueado: " + rutaUsuarioString);
            throw new SecurityException("Ruta fuera de la carpeta permitida: " + rutaUsuarioString);
        }
        if (!Files.exists(userPath)) {
            // if the path dont exist
            writeLog("ERROR", "Fichero no encontrado: " + rutaUsuarioString);
            System.exit(1);
        }
        writeLog("INFO", "Ruta valida: " + rutaUsuarioString);
        return userPath.toAbsolutePath().normalize();
    }

    /**
     * Lee el CSV y lo mete en una lista, quitando la cabecera con skip(1).
     * <p>
     * Lo abrimos con try-with-resources para que el fichero se cierre solo al acabar.
     * Lo leemos una sola vez y los tres métodos de cálculo usan esta lista.
     *
     * @param userPath la ruta del CSV (la que devuelve validatePath)
     * @return las filas del CSV sin la cabecera, por ejemplo <code>"102;45;Hipertensión;ACTIVO"</code>
     * @throws IOException si no se puede leer el fichero
     */
    private static List<String> csvIntoList(Path userPath) throws IOException {
        // try-with-resources to open and close the file once the method is finish
        // we introduce de data from datos into a list
        try (Stream<String> lines = Files.lines(userPath)) {
            return lines.skip(1).toList();
        }
    }

    /**
     * Cuenta cuántos registros hay (sin la cabecera, que ya la quitó csvIntoList).
     *
     * @param lines las filas del CSV
     * @return el número de registros
     */
    private static long countElements(List <String> lines) {
        return lines.stream().count();
    }

    /**
     * Saca los diagnósticos de los pacientes ACTIVO, sin repetir y en orden alfabético.
     * <p>
     * filter se queda con los ACTIVO (columna 3), map coge el diagnóstico (columna 2),
     * distinct quita los repetidos y sorted los ordena.
     *
     * @param lines las filas del CSV
     * @return la lista de diagnósticos únicos y ordenados
     */
    private static List<String> activeDiagnoses(List<String> lines) {
        Predicate<String> status = linea -> linea.split(";")[3].equals("ACTIVO");
        return lines.stream()
                .filter(status)
                .map(linea -> linea.split(";")[2])
                .distinct()
                .sorted()
                .toList();
    }

    /**
     * Calcula la edad media de los pacientes con diagnóstico "Diabetes" (activos o no).
     * <p>
     * filter se queda con los de Diabetes, mapToInt pasa la edad (columna 1) de texto a número
     * y average hace la media. Como average devuelve un OptionalDouble, con orElse sacamos el número.
     *
     * @param lines las filas del CSV
     * @return la edad media, o 0.0 si no hay ningún paciente con Diabetes
     */
    private static double diabetesAverageAge(List<String> lines) {
        return lines.stream()
                .filter(l1 -> l1.split(";")[2].equals("Diabetes"))
                .mapToInt(l1 -> Integer.parseInt(l1.split(";")[1]))
                .average()
                .orElse(0.0); //si no hay nada
    }

    /**
     * Mueve el CSV ya procesado a <code>salida/procesados/</code>.
     * <p>
     * Primero crea la carpeta si no existe, luego monta la ruta de destino con el mismo nombre
     * del fichero (getFileName) y lo mueve con REPLACE_EXISTING, para que si ya había uno con
     * ese nombre de una semana anterior lo sobrescriba.
     *
     * @param userPath la ruta del CSV que queremos mover
     * @throws IOException si no se puede crear la carpeta o mover el fichero
     */
    private static void moveToProcessed(Path userPath) throws IOException {
        Path folderProcess = Paths.get("salida", "procesados"); // salida/procesados
        createDirectories(folderProcess);
        Path location = folderProcess.resolve(userPath.getFileName()); // give us the last piece of the path
        // move files to the path
        Files.move(userPath, location, StandardCopyOption.REPLACE_EXISTING); // replace if the file already exists
    }

}
