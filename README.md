# AC: Acceso a Datos (DAM 2º)

Proyecto de IntelliJ con **dos módulos**: ejercicios y teoría (la misma estructura que PSP).

```
AC/
├── ejercicios/                      ← TUS EJERCICIOS
│   ├── datos/                   ficheros de entrada que da la profesora (mapa.dat, mapa_UTF.dat)
│   └── src/
│       ├── tema1_lambdas/       EjercicioIncidenciasTemplate.java
│       │                        EjercicioIncidenciasTemplateOptimizado.java (versión de referencia)
│       ├── tema2_colecciones/   ejercicioPlaylist.java + ENUNCIADO.md
│       ├── tema3_streams/       EjerciciosStreamsOptional.java
│       ├── tema4_ficheros/      ejemplo.java · ejercicio.java · LaboratorioFicherosTienda.java
│       │                        MapaBinario.java (lee ejercicios/datos/*.dat)
│       ├── tema5_json/          proyecto Maven propio: LectorFileJSONJackson · HttpRequestPokeAPI · PersonaJSON (excluido del módulo)
│       ├── tema6_jdbc_jpa/      JdbcRetos · JpaRetos · entidades
│       └── tema7_api_rest/      proyecto Maven propio con Spring Boot (excluido del módulo)
│   └── tareas_semanales/        ← EXCLUIDA del módulo: cada tarea es un proyecto Maven aislado
│       └── T01_BioDAM/          pom.xml + src/main/java + datos/ + ENUNCIADO.md
│
├── teoria/                          ← TEORÍA: una carpeta por tema, siempre con los mismos 4 archivos
│   ├── tema1_lambdas/               1_HISTORIA.md   el tema contado como una historia (empieza aquí)
│   ├── tema2_colecciones/           2_TECNICO.md    sintaxis y plantillas, para consultar
│   ├── tema3_streams/               3_PREGUNTAS.md  autoevaluación con la respuesta tapada
│   ├── tema4_ficheros/              visual.pdf      diagramas (visual.html = su fuente)
│   ├── tema5_json/                  (JSON, Jackson y HTTP)
│   ├── tema6_jdbc_jpa/              (JDBC y JPA/Hibernate, pendiente)
│   ├── tema7_api_rest/              (APIs REST con Spring Boot)
│   ├── CHULETA.md                   ★ las frases clave de todos los temas
│   ├── ERRORES_FRECUENTES.md        ★ los fallos de tus ejercicios
│   ├── GUIA_LIBRERIAS.md            ★ diccionario: qué hace cada clase/método y dónde vive
│   ├── _backup_apuntes_2026-10-21/  los apuntes antiguos, por si acaso
│   └── src/                         ejemplos EJECUTABLES con la teoría en comentarios
│       ├── t1_lambdas/      L01 sintaxis · L02 interfaces funcionales · L03 Comparator
│       │                    L04 referencias a métodos · L05 record/enum/switch/Factory
│       ├── t2_colecciones/  C01 List, Queue, Map, Set
│       ├── t3_streams/      S01 streams y Optional
│       └── t4_ficheros/     F01 rutas (Path/Files) · F02 texto · F03 CSV ↔ objetos
│                            F04 binarios · F05 serialización · F06 acceso aleatorio
│
├── zz_otros/                        ← LO QUE NO ES TEORÍA NI EJERCICIOS
│   ├── entregas/                    prácticas entregadas (carpeta y ZIP)
│   ├── examenes/                    preparación de exámenes y tests
│   │   └── 01_test_ficheros/        diapositivas.pdf · FUENTE_NOTEBOOKLM.md · LEEME.md
│   └── lib/                         JAR de Jackson, Hibernate, MySQL y PostgreSQL (los usa ejercicios/)
│
└── _backup_original/
    └── mis_versiones/               tu versión de incidencias antes de optimizar
```

> **Los hilos están en el proyecto PSP** (`C:\DAM\Segundo\PSP`).

## Cómo trabajar

1. **Abrir:** en IntelliJ, `File → Open…` y elige la carpeta `C:\DAM\Segundo\AC`.
2. **Estudiar un tema:** en `teoria/temaX_*/`, lee `1_HISTORIA.md` con `visual.pdf` al lado, ejecuta las demos de `teoria/src/tX_*` (▶ verde junto al `main`) y comprueba con `3_PREGUNTAS.md`. `2_TECNICO.md` es para consultar cuando programes.
3. **Hacer el ejercicio:** abre `ejercicios/src/temaX_*` y busca los `TODO` (**View → Tool Windows → TODO**).
4. **Repasar:** `teoria/CHULETA.md` y `teoria/ERRORES_FRECUENTES.md`.
5. **¿Qué hacía este método?** → `teoria/GUIA_LIBRERIAS.md` o `Ctrl+Q` en IntelliJ.

| Tema | Teoría | Teoría (código) | Ejercicio |
|---|---|---|---|
| 1. Lambdas | `tema1_lambdas/` | `t1_lambdas/` | `tema1_lambdas/EjercicioIncidenciasTemplate` |
| 2. Colecciones | `tema2_colecciones/` | `t2_colecciones/` | `tema2_colecciones/ejercicioPlaylist` |
| 3. Streams y Optional | `tema3_streams/` | `t3_streams/` | `tema3_streams/EjerciciosStreamsOptional` |
| 4. Ficheros | `tema4_ficheros/` | `t4_ficheros/` | `tema4_ficheros/LaboratorioFicherosTienda`, `MapaBinario` |
| 5. JSON, Jackson y HTTP | `tema5_json/` | — | `tema5_json/` (proyecto Maven) |
| 6. JDBC y JPA/Hibernate | `tema6_jdbc_jpa/` (pendiente) | `t6_jdbc_jpa/` (pendiente) | `tema6_jdbc_jpa/` |
| 7. APIs REST con Spring Boot | `tema7_api_rest/` | `t7_api_rest/` (pendiente) | `tema7_api_rest/` (proyecto Maven) |

**Para un tema nuevo:** crea `ejercicios/src/temaN_<nombre>/`, `teoria/src/tN_<nombre>/` y `teoria/temaN_<nombre>/` (con 1_HISTORIA, 2_TECNICO, 3_PREGUNTAS y visual), y añade una fila a la tabla.

**JDK:** openjdk-25 · **Codificación:** UTF-8
