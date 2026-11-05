package es.dam.accesodatos.tema06_json_serializacion;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;

/*
https://www.arquitecturajava.com/java-json-con-jackson/
 */
public class LectorFileJSONJackson {

    public static void main(String[] args) throws URISyntaxException {

        LectorFileJSONJackson lector = new LectorFileJSONJackson();

        // Uso correcto si el archivo está en src/main/resources/persona.json
        File fichero = lector.getFileFromResource("persona.json");
        System.out.println(fichero);

        ObjectMapper mapper = new ObjectMapper();
        // Desactivar el fallo por propiedades desconocidas
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        try {
            PersonaJSON persona = mapper.readValue(fichero, PersonaJSON.class);
            System.out.println(persona);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private File getFileFromResource(String fichero) throws URISyntaxException {

        ClassLoader cargador = getClass().getClassLoader();
        URL resource = cargador.getResource(fichero);
        if (resource == null) {
            throw new IllegalArgumentException("fichero no encontrado - " + fichero);
        } else {
            return new File(resource.toURI());
        }
    }
}