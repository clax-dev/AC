package com.example.cliente.config; // CAMBIO: paquete del proyecto (el original era es.dam.accesodatos...)

import org.springframework.core.env.Environment;
import org.springframework.core.io.Resource;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.SchemaFactory;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

/**
 * Traduce infraestructura.xml a propiedades de Spring:
 * 1) valida el XML contra el XSD, 2) lo parsea con DOM,
 * 3) lee las contraseñas de los secret files, 4) devuelve un Properties.
 */
public final class XMLConfigurationLoader {

    private XMLConfigurationLoader() {
    }

    public static Properties load(
            Resource xmlResource,
            Resource xsdResource,
            Environment environment
    ) {
        try (
                InputStream xmlInput = xmlResource.getInputStream();
                InputStream xsdInput = xsdResource.getInputStream()
        ) {
            validateXml(xmlInput, xsdInput);

            try (InputStream input = xmlResource.getInputStream()) {
                DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                factory.setNamespaceAware(false);

                // Protección contra ataques XXE (entidades externas en el XML)
                factory.setFeature(
                        "http://apache.org/xml/features/disallow-doctype-decl",
                        true
                );

                factory.setFeature(
                        "http://xml.org/sax/features/external-general-entities",
                        false
                );

                factory.setFeature(
                        "http://xml.org/sax/features/external-parameter-entities",
                        false
                );

                factory.setXIncludeAware(false);
                factory.setExpandEntityReferences(false);

                Document document = factory.newDocumentBuilder().parse(input);

                Element root = document.getDocumentElement();

                // CAMBIO: localizar cada sección para buscar DENTRO de ella.
                // getElementsByTagName recorre todo el documento, y <puerto>, <nombre> y <ssl>
                // aparecen repetidos (servidor y baseDatos): sin esto se cogía siempre el primero.
                Element aplicacion = section(root, "aplicacion");
                Element servidor = section(root, "servidor");
                Element ssl = section(servidor, "ssl");
                Element baseDatos = section(root, "baseDatos");
                Element jpa = section(root, "jpa");

                Properties properties = new Properties();

                properties.setProperty(
                        "spring.application.name",
                        value(aplicacion, "nombre") // CAMBIO: root -> aplicacion
                );

                properties.setProperty(
                        "server.port",
                        value(servidor, "puerto") // CAMBIO: root -> servidor
                );

                properties.setProperty(
                        "server.ssl.enabled",
                        value(ssl, "enabled") // CAMBIO: root -> ssl
                );

                properties.setProperty(
                        "server.ssl.key-store",
                        value(ssl, "key-store")
                );

                properties.setProperty(
                        "server.ssl.key-store-password",
                        readSecretFile(
                                ssl,
                                "key-store-passwordFile"
                        )
                );

                properties.setProperty(
                        "server.ssl.key-store-type",
                        value(ssl, "key-store-type")
                );

                properties.setProperty(
                        "server.ssl.key-alias",
                        value(ssl, "key-alias")
                );

                properties.setProperty(
                        "server.ssl.protocol",
                        value(ssl, "protocol")
                );

                // CAMBIO: todos los datos de la BD se leen dentro de <baseDatos>
                String motor = value(baseDatos, "motor");
                String host = value(baseDatos, "host");
                String port = value(baseDatos, "puerto");
                String database = value(baseDatos, "nombre");
                String username = value(baseDatos, "usuario");
                String password = readSecretFile(
                        baseDatos,
                        "passwordFile"
                );
                String dbSsl = value(baseDatos, "ssl");

                properties.setProperty(
                        "spring.datasource.url",
                        buildJdbcUrl(
                                motor,
                                host,
                                port,
                                database,
                                dbSsl
                        )
                );

                properties.setProperty(
                        "spring.datasource.username",
                        username
                );

                properties.setProperty(
                        "spring.datasource.password",
                        password
                );

                properties.setProperty(
                        "spring.datasource.driver-class-name",
                        driverFor(motor)
                );

                properties.setProperty(
                        "spring.jpa.hibernate.ddl-auto",
                        value(jpa, "ddl-auto") // CAMBIO: root -> jpa
                );

                properties.setProperty(
                        "spring.jpa.show-sql",
                        value(jpa, "show-sql")
                );

                properties.setProperty(
                        "spring.jpa.properties.hibernate.format_sql",
                        value(jpa, "format-sql")
                );

                properties.setProperty(
                        "spring.jpa.open-in-view",
                        value(jpa, "open-in-view")
                );

                return properties;
            }

        } catch (Exception exception) {
            throw new IllegalStateException(
                    "No se ha podido cargar la configuración XML",
                    exception
            );
        }
    }

    private static void validateXml(
            InputStream xmlInput,
            InputStream xsdInput
    ) throws Exception {
        SchemaFactory schemaFactory =
                SchemaFactory.newInstance(
                        XMLConstants.W3C_XML_SCHEMA_NS_URI
                );

        schemaFactory.setProperty(
                XMLConstants.ACCESS_EXTERNAL_DTD,
                ""
        );

        schemaFactory.setProperty(
                XMLConstants.ACCESS_EXTERNAL_SCHEMA,
                ""
        );

        var schema = schemaFactory.newSchema(
                new StreamSource(xsdInput)
        );

        schema.newValidator().validate(
                new StreamSource(xmlInput)
        );
    }

    // CAMBIO: método nuevo. Devuelve el elemento-sección (p.ej. <baseDatos>) dentro de 'parent'.
    private static Element section(
            Element parent,
            String sectionName
    ) {
        var nodes = parent.getElementsByTagName(sectionName);

        if (nodes.getLength() == 0) {
            throw new IllegalArgumentException(
                    "Falta la sección XML: " + sectionName
            );
        }

        return (Element) nodes.item(0);
    }

    private static String value(
            Element parent,
            String elementName
    ) {
        var nodes = parent.getElementsByTagName(elementName);

        if (nodes.getLength() == 0) {
            throw new IllegalArgumentException(
                    "Falta el elemento XML: " + elementName
            );
        }

        return nodes.item(0)
                .getTextContent()
                .trim();
    }

    private static String readSecretFile(
            Element parent,
            String elementName
    ) {
        String path = value(parent, elementName);
        System.out.println(
                "Directorio actual: "
                        + System.getProperty("user.dir")
        );

        System.out.println(
                "Ruta del secreto: "
                        + Path.of(path).toAbsolutePath()
        );

        try {
            String secret = Files.readString(
                    Path.of(path)
            ).trim();

            if (secret.isBlank()) {
                throw new IllegalStateException(
                        "El secreto está vacío: " + path
                );
            }

            return secret;

        } catch (Exception exception) {
            throw new IllegalStateException(
                    "No se puede leer el secreto: " + path,
                    exception
            );
        }
    }

    private static String driverFor(String motor) {
        return switch (motor.toUpperCase()) {
            case "POSTGRESQL" -> "org.postgresql.Driver";
            case "MYSQL" -> "com.mysql.cj.jdbc.Driver";
            default -> throw new IllegalArgumentException(
                    "Motor no soportado: " + motor
            );
        };
    }

    private static String buildJdbcUrl(
            String motor,
            String host,
            String port,
            String database,
            String ssl
    ) {
        return switch (motor.toUpperCase()) {
            case "POSTGRESQL" ->
                    "jdbc:postgresql://"
                            + host
                            + ":"
                            + port
                            + "/"
                            + database
                            + "?ssl="
                            + ssl;

            case "MYSQL" ->
                    "jdbc:mysql://"
                            + host
                            + ":"
                            + port
                            + "/"
                            + database
                            + "?serverTimezone=Europe/Madrid"
                            + "&useSSL="
                            + ssl;

            default -> throw new IllegalArgumentException(
                    "Motor no soportado: " + motor
            );
        };
    }
}
