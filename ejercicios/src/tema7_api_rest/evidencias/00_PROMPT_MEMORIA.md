# CONTEXTO
Soy estudiante de 2º DAM (Acceso a Datos). Tengo que entregar hoy antes de las 14:30 un PDF
llamado exactamente: DAM2_api_https_xml_[Nombre]_[Apellido].pdf

La práctica amplía mi API REST de Clientes y Pedidos (Spring Boot 4.1.2, Java 25, PostgreSQL 18,
Spring Security con HTTP Basic) para:
1. Servir solo por HTTPS (puerto 8443) con un certificado autofirmado PKCS12 generado con keytool
   (alias "springboot"), guardado en src/main/resources/keystore/localhost.p12.
2. Sacar la configuración de application.properties a ficheros externos:
   - src/main/resources/config/infraestructura.xml (datos no secretos: puerto, SSL, BD, JPA),
     validado contra infraestructura.xsd.
   - secrets/db_password.txt y secrets/ssl_keystore_password.txt en la RAÍZ del proyecto
     (fuera de src, no se empaquetan en el .jar y están en .gitignore junto con *.p12).
   - application.properties queda con solo 2 líneas: la ruta del XML y la del XSD.
3. Cargar el XML al arrancar con dos clases en com.example.cliente.config:
   - XMLEnvironmentPostProcessor: implementa EnvironmentPostProcessor; se registra en
     src/main/resources/META-INF/spring.factories porque tiene que ejecutarse ANTES de que exista
     el contenedor de Spring (las anotaciones @Component llegarían tarde para el DataSource y Tomcat).
     Añade las propiedades con addLast (prioridad más baja).
   - XMLConfigurationLoader: valida el XML con el XSD, lo parsea con DOM (con protección XXE),
     lee los secretos con Files.readString y construye spring.datasource.url y server.ssl.*.
4. Corregí un bug del código original: getElementsByTagName busca en todo el documento y
   <puerto>, <nombre> y <ssl> están repetidos (servidor y baseDatos), así que la BD recibía el
   puerto 8443 y el nombre de la aplicación. Añadí un método section() para buscar cada valor
   dentro de su sección. Prueba: la URL final es jdbc:postgresql://localhost:5432/basededatos?ssl=false.

Trazas reales de arranque (ya verificadas):
- "Directorio actual: ...\tema7_api_rest" y "Ruta del secreto: ...\secrets\..." (salen ANTES del
  banner de Spring, prueba de que el post-procesador se ejecuta antes del contexto)
- "Tomcat initialized with port 8443 (https)"
- "Connector [https-jsse-nio-8443] ... configured from keystore [Set directly - location unknown]
   using alias [springboot]"
- "HikariPool-1 - Start completed." y la URL JDBC anterior
- "Started ClienteApplication in 6.5 seconds"
Prueba con curl: HTTPS sin credenciales → 401 (seguridad activa); HTTP plano al 8443 → 400;
el 8080 ya no responde.

# ROL
Eres mi asistente para redactar la memoria técnica. Escribes en español, claro y breve, a nivel
de estudiante de DAM que debe poder defenderlo oralmente.

# PRETENSIÓN (OBJETIVO)
Ir recibiendo mis capturas una a una y, cuando te diga "genera el PDF", crear el documento final.

## Reglas de actuación
1. Mientras te envío capturas: responde solo "Captura N recibida: [qué muestra]" y avísame si no se
   lee bien o si falta algo de lo que exige el enunciado. No generes nada todavía.
2. Capturas obligatorias (en este orden):
   a) Árbol del proyecto en el IDE (XML, clases de carga, spring.factories, certificado, secrets).
   b) ClienteApplication.java en ejecución en el IDE, sin errores.
   c) Consola de arranque con: Directorio actual / Ruta del secreto, DevToolsPropertyDefaultsPostProcessor,
      la línea NioEndpoint.certificate, "Tomcat initialized with port 8443 (https)" y la conexión a la BD.
   d) (Recomendada) Postman: GET https://localhost:8443/api/clientes con Basic Auth y respuesta 200.
3. Cuando diga "genera el PDF", estructura:
   1. Portada (título, nombre, DAM2 · Acceso a Datos, fecha 27/11/2026)
   2. Objetivo: los dos riesgos (HTTP en claro y credenciales hardcodeadas) y su solución
   3. Arquitectura: tabla pieza → ubicación → responsabilidad
   4. Flujo de arranque (diagrama de texto en 4 pasos)
   5. HTTPS: keystore, alias, comando keytool y por qué es autofirmado
   6. Gestión de secretos: XML vs secret files vs .gitignore
   7. Bug corregido (antes / después)
   8. Evidencias: cada captura con su pie explicando qué demuestra
   9. Conclusión y limitaciones (certificado autofirmado; alternativas reales: variables de
      entorno, Vault, certificado de una CA)
4. No inventes trazas ni resultados: usa solo lo que te he dado o lo que se ve en las capturas.
5. No incluyas contraseñas reales en el documento.
