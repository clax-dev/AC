package tema4_ficheros;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.*;

//import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Stream;

public class LaboratorioFicherosTienda {

    private static final int BYTES_REGISTRO = Integer.BYTES + 40; // ID (4 bytes) + Nombre (40 bytes)

    public static void main(String[] args) {
        try {
            System.out.println("=== BLOQUE 1: CONFIGURACIÓN ===");
            Path config = Bloque1_Configuracion();

            System.out.println("\n=== BLOQUE 2: LOGS Y SEGURIDAD ===");
            Bloque2_LogsYSeguridad(config);

            System.out.println("\n=== BLOQUE 3: BINARIO Y CANALES ===");
            Bloque3_CanalesYBinario();

            System.out.println("\n=== BLOQUE 4: REPORTES EXCEL ===");
            Bloque4_ReporteExcel();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * OBJETIVO: Trabajar con Path, Files y opciones de apertura segura.
     * TAREA ALUMNO: Crear un archivo de configuración clave=valor sin machacar datos previos.
     */
    private static Path Bloque1_Configuracion() throws IOException, FileAlreadyExistsException {
        Path carpeta = Paths.get("salida", "config"); // lo mismo que Paths.get(salida/config)
        // TODO: 1.1 Crear directorios necesarios de forma segura usando Files
        Files.createDirectories(carpeta);
        Path archivoConfig = carpeta.resolve("app.properties");

        // TODO: 1.2 Definir una lista de propiedades (clave=valor) para la tienda
        // Ejemplo: "tienda.nombre=PC-DAM", "tienda.timeout=60", "tienda.moneda=EUR"
        List<String> propiedades = List.of("tienda.nombre=PC-DAM", "tienda.timeout=60", "tienda.moneda=EUR"); // formato

        // TODO: 1.3 Escribir el archivo exigiendo que sea de nueva creación (CREATE_NEW)
        // Capturar la excepción específica si el archivo ya existe para proteger los datos.
        // Escribir aquí
        Files.write(archivoConfig, propiedades, StandardOpenOption.CREATE_NEW);
        System.out.println("[B1] Archivo creado correctamente.");

        // TODO: 1.4 Leer y mostrar las líneas usando try-with-resources con un Stream
        try (Stream<String> lineas = Files.lines(archivoConfig)) {
            lineas.forEach(System.out::println);
        }
        return archivoConfig;
    }

    /**
     * OBJETIVO: Implementar escrituras incrementales (APPEND) y mitigar Path Traversal.
     */
    private static void Bloque2_LogsYSeguridad(Path rutaSolicitada) throws IOException {
        Path carpetaLogs = Paths.get("salida", "logs");
        Files.createDirectories(carpetaLogs);

        // Obtener fecha y hora actual
        LocalDateTime ahora = LocalDateTime.now();

        // Formateadores de fecha y hora
        DateTimeFormatter formatoNombreFichero = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        // TODO 2.1: incluir datetime en el nombre del fichero tienda_YYYYMMddHHmmss.log
        // Path archivoLog = carpetaLogs.resolve(nombreArchivo);

        // TODO: 2.2 Escribir una línea de log usando StandardOpenOption.APPEND y CREATE si no existe

        // TODO: 2.3 VALIDACIÓN DE SEGURIDAD (Mitigar Path Traversal)
        // Comprobar si la rutaSolicitada está dentro de la carpeta permitida (salida)
        Path basePermitida = Paths.get("salida").toAbsolutePath().normalize();
        Path candidata = rutaSolicitada.toAbsolutePath().normalize();

        // Comprobar con un condicional basado en componentes de ruta
        if (false /* TODO: condición */) {
            System.out.println("[B2] ACCESO PERMITIDO a la ruta.");
        } else {
            System.out.println("[B2] ALERTA DE SEGURIDAD: Intento de escape de directorio.");
        }
    }

    /**
     * OBJETIVO: Dominar FileChannel y ByteBuffer para lecturas/escrituras posicionales fijas.
     */
    private static void Bloque3_CanalesYBinario() throws IOException {
        Path archivoBin = Paths.get("salida", "productos.bin");

        // TODO: 3.1 Escribir el producto "SSD 1TB" (ID: 101) en un FileChannel usando un ByteBuffer corporativo
        // El tamaño del registro debe ser exactamente BYTES_REGISTRO
        try (FileChannel canal = FileChannel.open(archivoBin, StandardOpenOption.CREATE, StandardOpenOption.WRITE)) {
            ByteBuffer buffer = ByteBuffer.allocate(BYTES_REGISTRO);
            // Insertar ID, rellenar texto a 40 bytes fijos, hacer flip y escribir en bucle.
        }

        // TODO: 3.2 Modificar el registro saltando directamente a su posición (Acceso posicional)
        // Cambiar el nombre del producto directamente en el fichero sin reescribir todo
        try (FileChannel canal = FileChannel.open(archivoBin, StandardOpenOption.WRITE)) {
            // Posicionarse y sobreescribir de forma exacta
        }
    }

    /**
     * OBJETIVO: Automatizar la creación de informes enriquecidos con Apache POI.
     */
    private static void Bloque4_ReporteExcel() throws IOException {
        Path rutaExcel = Paths.get("salida", "reporte_tienda.xlsx");

        // TODO: 4.1 Instanciar un libro XSSFWorkbook y crear una hoja llamada "Inventario"
        try (XSSFWorkbook libro = new XSSFWorkbook()) {

            // TODO: 4.2 Crear una fila de cabecera y aplicar un estilo básico (Negrita, color)

            // TODO: 4.3 Añadir un producto con datos numéricos nativos (precio, unidades)

            // TODO: 4.4 Insertar una celda de tipo fórmula que calcule el total (Precio * Unidades)

            // TODO: 4.5 Ajustar tamaño de columnas automáticamente y guardar con un OutputStream de NIO
        }
    }
}

