package com.example.cliente.config; // CAMBIO: paquete del proyecto (el original era es.dam.accesodatos...)

import org.springframework.boot.EnvironmentPostProcessor;
import org.springframework.boot.SpringApplication;
import org.springframework.core.Ordered;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.PropertiesPropertySource;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;

import java.util.Properties;

/**
 * Se ejecuta al arrancar Spring, ANTES de crear los beans (DataSource, Tomcat...).
 * Lo registra META-INF/spring.factories.
 * Lee la ruta del XML/XSD desde application.properties, delega la lectura en
 * XMLConfigurationLoader y añade las propiedades resultantes al Environment de Spring.
 */
public class XMLEnvironmentPostProcessor
        implements EnvironmentPostProcessor, Ordered {

    @Override
    public void postProcessEnvironment(
            ConfigurableEnvironment environment,
            SpringApplication application
    ) {
        String xmlLocation = environment.getProperty(
                "infraestructura.file",
                "classpath:config/infraestructura.xml"
        );

        String xsdLocation = environment.getProperty(
                "infraestructura.schema",
                "classpath:config/infraestructura.xsd"
        );

        DefaultResourceLoader resourceLoader =
                new DefaultResourceLoader();

        Resource xmlResource =
                resourceLoader.getResource(xmlLocation);

        Resource xsdResource =
                resourceLoader.getResource(xsdLocation);

        if (!xmlResource.exists()) {
            throw new IllegalStateException(
                    "No existe el XML: " + xmlLocation
            );
        }

        if (!xsdResource.exists()) {
            throw new IllegalStateException(
                    "No existe el XSD: " + xsdLocation
            );
        }

        Properties properties =
                XMLConfigurationLoader.load(
                        xmlResource,
                        xsdResource,
                        environment
                );

        // addLast = prioridad más baja: si application.properties define la misma clave, gana el properties
        environment.getPropertySources().addLast(
                new PropertiesPropertySource(
                        "xmlInfrastructure",
                        properties
                )
        );
    }

    @Override
    public int getOrder() {
        return Ordered.LOWEST_PRECEDENCE;
    }
}
