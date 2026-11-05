# T7: API REST de clientes y pedidos

Carpeta preparada para los ejercicios del tema 7. De momento no hay código.

Esta carpeta es un **proyecto Maven propio**, con su `pom.xml` y sus librerías de Spring. Por eso está excluida del módulo `ejercicios` (en `ejercicios.iml`): si no, IntelliJ intentaría compilarla sin Spring.
Teoría: `teoria/tema7_api_rest/`.

## Cuando empieces

1. Genera el proyecto en [start.spring.io](https://start.spring.io) con estos valores (PDF, pág. 11):

   | Opción | Valor |
   |---|---|
   | Project / Language | Maven / Java |
   | Spring Boot | 4.1.2 |
   | Packaging / Configuration | Jar / Properties |
   | Java | 25 |
   | Dependencias | Spring Web · Spring Data JPA · PostgreSQL Driver · Validation · DevTools |

2. Copia **el contenido** del ZIP aquí dentro, de modo que `pom.xml` quede justo al lado de este `LEEME.md`.
3. En IntelliJ, clic derecho en ese `pom.xml` → **Add as Maven Project**. La carpeta pasa a ser un módulo con Spring.
4. Tu código va en `src/main/java`, dentro de esta carpeta.
5. Rellena `src/main/resources/application.properties` con los datos de tu PostgreSQL (PDF, pág. 15).
6. Prueba `http://localhost:8080/hola` (PDF, pág. 18).

## Cosas del PDF que conviene aclarar en clase

- **Nombre del paquete:** cambia según la página (`es.dam.psp.apibackend.clientes` en la 11, `es.dam.clientesAPI` en la 16). Usa el que indique la práctica.
- **Persona y Empresa:** la pág. 8 mezcla herencia (`@Inheritance`) con `@OneToOne`, y el diagrama de la pág. 10 dibuja herencia. Tus entidades del tema 6 usan `@OneToOne`.
- **Clase principal:** las págs. 8 y 9 dicen que `MainJpaApp` inicializa el `EntityManagerFactory`, pero con Spring Boot eso lo hace la autoconfiguración (pág. 17).
