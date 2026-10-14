# Errores frecuentes (sacados de tus ejercicios de AC)

Revisado por última vez el 2 de octubre. Los ✅ ya los has corregido tú.

| # | Archivo | Problema | Pista |
|---|---|---|---|
| ✅ | `tema1_lambdas/EjercicioIncidenciasTemplate.java` | ~~`abiertas` devolvía las resueltas~~. Ya está corregido con `!estado.resuelta`. | — |
| 1 | Mismo archivo | `crearIncidencias` solo mira la prioridad: **no usa** `datosValidos` ni `abiertas`, así que en "abiertas de prioridad alta" salen también las resueltas. | Pásale un `Predicate` al método, o mira `EjercicioIncidenciasTemplateOptimizado.java`. |
| 2 | Mismo archivo | El `Comparator porPrioridad` ordena de BAJA a ALTA, y el enunciado pide ALTA primero. Además le falta el desempate por id. | `teoria/src/t1_lambdas/L03_Comparator.java` |
| 3 | Mismo archivo | `isEmpty()` da por válido un texto que solo tiene espacios. | `isBlank()` |
| 4 | `tema2_colecciones/ejercicioPlaylist.java` | `buscarCancion` comprueba `id >= canciones.size()`, pero los IDs de un Map no son índices. | Con `containsKey` basta. |
| 5 | Mismo archivo | `ordenPlaylist` nunca se llena, así que la opción 1 no muestra nada. Solo se carga una canción. | Rellena el Map y la List en la carga. |
| 6 | Mismo archivo | `mostrarArtistas` y `mostrarMasReproducida` siguen vacíos (TODO). | `teoria/src/t2_colecciones/C01_Colecciones.java` |
| 7 | `tema3_streams/EjerciciosStreamsOptional.java` | En el ejercicio 2, `"Informática"` tiene que llevar **tilde** para coincidir con los datos. Además, `equals` es más exacto que `contains`. | — |
