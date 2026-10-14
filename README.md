# AC: Acceso a Datos (DAM 2º)

Proyecto de IntelliJ con **dos módulos**: ejercicios y teoría (la misma estructura que PSP).

```
AC/
├── ejercicios/                      ← TUS EJERCICIOS
│   └── src/
│       ├── tema1_lambdas/       EjercicioIncidenciasTemplate.java
│       │                        EjercicioIncidenciasTemplateOptimizado.java (versión de referencia)
│       ├── tema2_colecciones/   ejercicioPlaylist.java + ENUNCIADO.md
│       ├── tema3_streams/       EjerciciosStreamsOptional.java
│       └── tema4_ficheros/      (vacía: aquí irán los ejercicios del tema)
│   └── tareas_semanales/        ← EXCLUIDA del módulo: cada tarea es un proyecto Maven aislado
│       └── T01_BioDAM/          pom.xml + src/main/java + datos/ + ENUNCIADO.md
│
├── teoria/                          ← TEORÍA (basada en los ejercicios)
│   ├── apuntes/                     apuntes en Markdown
│   │   ├── 00_chuleta.md            ★ resumen para repasar rápido
│   │   ├── 01_lambdas.md
│   │   ├── 02_colecciones.md
│   │   ├── 03_streams_optional.md
│   │   ├── 04_ficheros.md
│   │   ├── 0_errores_frecuentes.md  ★ fallos encontrados en tus ejercicios
│   │   └── 05_guia_librerias.md     ★ diccionario: qué hace cada clase/método y dónde vive
│   └── src/                         ejemplos EJECUTABLES con la teoría en comentarios
│       ├── t1_lambdas/      L01 sintaxis · L02 interfaces funcionales · L03 Comparator
│       │                    L04 referencias a métodos · L05 record/enum/switch/Factory
│       ├── t2_colecciones/  C01 List, Queue, Map, Set
│       ├── t3_streams/      S01 streams y Optional
│       └── t4_ficheros/     F01 rutas (Path/Files) · F02 texto · F03 CSV ↔ objetos
│                            F04 binarios · F05 serialización · F06 acceso aleatorio
│
└── _backup_original/
    └── mis_versiones/               tu versión de incidencias antes de optimizar
```

> **Los hilos están en el proyecto PSP** (`C:\DAM\Segundo\PSP`).

## Cómo trabajar

1. **Abrir:** en IntelliJ, `File → Open…` y elige la carpeta `C:\DAM\Segundo\AC`.
2. **Estudiar un tema:** lee `teoria/apuntes/0X_*.md` y ejecuta los ejemplos de `teoria/src/tX_*` con el ▶ verde que aparece junto al `main`.
3. **Hacer el ejercicio:** abre `ejercicios/src/temaX_*` y busca los `TODO` (**View → Tool Windows → TODO**).
4. **Repasar:** `00_chuleta.md` y `0_errores_frecuentes.md`.
5. **¿Qué hacía este método?** → `05_guia_librerias.md` o `Ctrl+Q` en IntelliJ.

| Tema | Teoría (apuntes) | Teoría (código) | Ejercicio |
|---|---|---|---|
| 1. Lambdas | `01_lambdas.md` | `t1_lambdas/` | `tema1_lambdas/EjercicioIncidenciasTemplate` |
| 2. Colecciones | `02_colecciones.md` | `t2_colecciones/` | `tema2_colecciones/ejercicioPlaylist` |
| 3. Streams y Optional | `03_streams_optional.md` | `t3_streams/` | `tema3_streams/EjerciciosStreamsOptional` |
| 4. Ficheros | `04_ficheros.md` | `t4_ficheros/` | `tema4_ficheros/` (pendiente) |

**Para un tema nuevo:** crea `ejercicios/src/tema5_<nombre>/`, `teoria/src/t5_<nombre>/` y `teoria/apuntes/0X_<nombre>.md`, y añade una fila a la tabla.

**JDK:** openjdk-25 · **Codificación:** UTF-8
