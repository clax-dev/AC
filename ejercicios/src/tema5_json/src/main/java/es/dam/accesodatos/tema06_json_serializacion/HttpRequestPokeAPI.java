package es.dam.accesodatos.tema06_json_serializacion;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

public class HttpRequestPokeAPI {
    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static final HttpClient CLIENTE = HttpClient.newHttpClient();

    public static void main(String[] args) {
        try {
            // Determinar el pokémon a buscar (por argumento o por defecto charmander)
            String nombrePokemon = args.length > 0 ? args[0] : "charmander";

            // Construir la URL de la PokeAPI
            StringBuilder url = new StringBuilder();
            url.append("https://pokeapi.co/api/v2/pokemon/")
                    .append(nombrePokemon.toLowerCase().trim());

            // Construir la petición HTTP correctamente usando java.net.http.HttpRequest
            HttpRequest peticion = HttpRequest.newBuilder()
                    .uri(URI.create(url.toString()))
                    .header("Accept", "application/json")
                    .header("User-Agent", "DAM2-AccesoDatos/1.0")
                    .GET()
                    .build();

            // Enviar la petición
            HttpResponse<String> respuesta = CLIENTE.send(
                    peticion, HttpResponse.BodyHandlers.ofString());

            // Validar el estado de la respuesta de la API
            if (respuesta.statusCode() < 200 || respuesta.statusCode() >= 300) {
                throw new IOException("HTTP " + respuesta.statusCode()
                        + " al consultar " + url);
            }

            // Imprimir el JSON completo devuelto por el servidor
            //System.out.println(respuesta.body());

            // Es el JSON entero metido en una variable
            JsonNode json = MAPPER.readTree(respuesta.body());

            System.out.println("Por Arbol:");
            System.out.println("ID:" + json.get("id").asInt());
            System.out.println("NOMBRE: " + json.get("name").asText());
            for (JsonNode habilidad : json.get("abilities")) {
                System.out.println(" - " + habilidad.get("ability").get("name").asText()   // CORREGIDO: bajar a "name"
                        + " -> " + habilidad.get("ability").get("url").asText());
            }

            System.out.println("Por clases");
            pokemon p = MAPPER.treeToValue(json, pokemon.class);   // CORREGIDO: el JSON entero ES el pokemon
            System.out.println("ID: " + p.id());
            System.out.println("NOMBRE: " + p.name());
            for (pokemonAbilityWrapper h : p.abilities()) {        // CORREGIDO: un for, como en el árbol
                System.out.println(" - " + h.ability().name() + " -> " + h.ability().url());
            }


        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

    // CORREGIDO: @JsonIgnoreProperties en los tres, porque el JSON trae muchos más campos
    @JsonIgnoreProperties(ignoreUnknown = true)
    record pokemon(int id, String name, List<pokemonAbilityWrapper> abilities) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    record pokemonAbilityWrapper(ability ability) {}   // CORREGIDO: el campo se llama como la clave "ability"

    @JsonIgnoreProperties(ignoreUnknown = true)
    record ability(String name, String url) {}
}
