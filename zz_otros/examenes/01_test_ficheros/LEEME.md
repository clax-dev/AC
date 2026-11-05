# Test de ficheros (jueves 22-oct-2026)

| Archivo | Para qué |
|---|---|
| `diapositivas.pdf` | 22 diapositivas de repaso (fuente: `diapositivas.html`) |
| `FUENTE_NOTEBOOKLM.md` | El tema 4 completo en un solo archivo de texto: explicación + sintaxis + 44 preguntas con respuesta |

La teoría completa está en `teoria/tema4_ficheros/` (1_HISTORIA, 2_TECNICO, 3_PREGUNTAS y visual.pdf).

## Cómo usarlo en NotebookLM

1. Crea un cuaderno nuevo y sube **`diapositivas.pdf`** y **`FUENTE_NOTEBOOKLM.md`**. Si no te deja subir el `.md`, cámbiale la extensión a `.txt`.
2. Opcional: sube también `teoria/tema4_ficheros/visual.pdf`.
3. Pídele el test en el chat, por ejemplo:

```
Hazme un test de 20 preguntas tipo test sobre ficheros en Java, con 4 opciones
(a, b, c, d) y una sola correcta. Mezcla preguntas teóricas (conceptos,
diferencias entre clases, qué excepción salta) y preguntas sobre fragmentos
de código (qué imprime, qué falla, qué opción de apertura usar). No me des
las respuestas hasta que te conteste. Después, corrígeme y explícame cada fallo
citando la diapositiva.
```

Otras peticiones útiles:
- *"Solo preguntas de las trampas típicas: verdadero o falso con justificación."*
- *"10 preguntas de código: dime qué pasa al ejecutar este fragmento."*
- *"Pregúntame más sobre lo que he fallado."*
- También puedes usar el botón de **Cuestionario / Tarjetas** del panel Studio.

## Si te queda poco tiempo
1. Diapositivas 20 y 21 (trampas de verdadero o falso).
2. Diapositivas 9, 10 y 11 (opciones de apertura, excepciones y Path Traversal).
3. Diapositivas 14 a 17 (binario, serialización, acceso aleatorio y ByteBuffer).
