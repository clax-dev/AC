package com.example.cliente.lector;

import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;

/**
 * Lee ficheros CSV con NIO
 * no sabe si el fichero es de clientes o de pedidos, solo lo lee y separa las columnas
 * pasar los datos a entidades lo hace MigracionService
 */
@Component
public class LectorCsv {

    // separador de columnas (Excel en español exporta el CSV con ;)
    private static final String SEPARADOR = ";";

    /**
     * metodo que lee el fichero y devuelve sus filas separadas en columnas
     * 1. abre el fichero linea a linea (Files.lines), sin cargarlo entero
     * 2. se salta la cabecera
     * 3. quita las lineas vacias
     * 4. separa cada linea por ; (el -1 hace que no se pierdan las columnas vacias del final)
     * @param ruta ruta del fichero CSV
     * @return lista de filas, cada fila es un array con sus columnas
     * @throws UncheckedIOException si no encuentra el fichero o no lo puede leer
     */
    public List<String[]> leer(Path ruta) {
        try (Stream<String> lineas = Files.lines(ruta, StandardCharsets.UTF_8)) {
            return lineas
                    .skip(1)                                  // fuera la cabecera
                    .filter(linea -> !linea.isBlank())        // fuera lineas vacias
                    .map(linea -> linea.split(SEPARADOR, -1)) // linea -> columnas
                    .toList();
        } catch (IOException e) {
            throw new UncheckedIOException("No se puede leer el fichero " + ruta, e);
        }
    }
}
