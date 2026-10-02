# AC: Acceso a Datos (DAM 2º)

Proyecto de IntelliJ con **dos módulos**: ejercicios y teoría (la misma estructura que PSP).

```
AC/
├── ejercicios/                      ← TUS EJERCICIOS
│   └── src/
│       ├── tema1_lambdas/       EjercicioIncidenciasTemplate.java
│       │                        EjercicioIncidenciasTemplateOptimizado.java (versión de referencia)
│       ├── tema2_colecciones/   ejercicioPlaylist.java + ENUNCIADO.md
│       └── tema3_streams/       EjerciciosStreamsOptional.java
│
├── teoria/                          ← TEORÍA (basada en los ejercicios)
│   ├── apuntes/                     apuntes en Markdown
│   │   ├── 00_chuleta.md            ★ resumen para repasar rápido
│   │   ├── 01_lambdas.md
│   │   ├── 02_colecciones.md
│   │   ├── 03_streams_optional.md
│   │   └── 04_errores_frecuentes.md ★ fallos encontrados en tus ejercicios
│   └── src/                         ejemplos EJECUTABLES con la teoría en comentarios
│       ├── t1_lambdas/      L01 sintaxis · L02 interfaces funcionales · L03 Comparator
│       │                    L04 referencias a métodos · L05 record/enum/switch/Factory
│       ├── t2_colecciones/  C01 List, Queue, Map, Set
│       └── t3_streams/      S01 streams y Optional
│
└── _backup_original/
    └── mis_versiones/               tu versión de incidencias antes de optimizar
```

> **Los hilos están en el proyecto PSP** (`C:\DAM\Segundo\PSP`).

## Cómo trabajar

1. **Abrir:** en IntelliJ, `File → Open…` y elige la carpeta `C:\DAM\Segundo\AC`.
2. **Estudiar un tema:** lee `teoria/apuntes/0X_*.md` y ejecuta los ejemplos de `teoria/src/tX_*` con el ▶ verde que aparece junto al `main`.
3. **Hacer el ejercicio:** abre `ejercicios/src/temaX_*` y busca los `TODO` (**View → Tool Windows → TODO**).
4. **Repasar:** `00_chuleta.md` y `04_errores_frecuentes.md`.

| Tema | Teoría (apuntes) | Teoría (código) | Ejercicio |
|---|---|---|---|
| 1. Lambdas | `01_lambdas.md` | `t1_lambdas/` | `tema1_lambdas/EjercicioIncidenciasTemplate` |
| 2. Colecciones | `02_colecciones.md` | `t2_colecciones/` | `tema2_colecciones/ejercicioPlaylist` |
| 3. Streams y Optional | `03_streams_optional.md` | `t3_streams/` | `tema3_streams/EjerciciosStreamsOptional` |

**Para un tema nuevo:** crea `ejercicios/src/tema4_<nombre>/`, `teoria/src/t4_<nombre>/` y `teoria/apuntes/0X_<nombre>.md`, y añade una fila a la tabla.

**JDK:** openjdk-25 · **Codificación:** UTF-8
