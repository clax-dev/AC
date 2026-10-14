# Tarea Semanal: BioDAM — Ingesta y Auditoría de Datos

**Entrega:** miércoles 14/10/2026, 23:59 · ZIP: `DAM2-AD-BioDAM_apellido-nombre.zip` (código `.java` + `pom.xml` + capturas de consola y del log)

## Checklist (rúbrica, sobre 10)

- [ ] **B1 (1.0)** Log `salida/sistema_logs/auditoria_yyyyMMddHHmmss.log`, uno nuevo por arranque
- [ ] **B1 (1.0)** Cada línea empieza por `[yyyy-MM-dd HH:mm:ss.SSS]` + nivel `INFO` / `WARN` / `ERROR`
- [ ] **B2 (1.5)** Path Traversal: `toAbsolutePath().normalize()` + `Path.startsWith(datos/)` → `WARN` + `SecurityException`
- [ ] **B2** Ruta válida pero no existe (`Files.exists`) → `ERROR` y terminar
- [ ] **B3 (2.0)** Fichero abierto con `try (...)` (try-with-resources)
- [ ] **B3 (1.5)** CERO `for` / `while` / `do-while` sobre las líneas del CSV
- [ ] **B3 (1.0)** `skip(1)`, `filter`, `distinct`, `sorted`, `average`
  - [ ] Total de registros (sin cabecera)
  - [ ] Diagnósticos únicos de pacientes `ACTIVO`, ordenados alfabéticamente
  - [ ] Edad media (`double`) de los pacientes con "Diabetes"
- [ ] **B4 (1.0)** Log `INFO` de fin + `Files.move` a `salida/procesados/` con `StandardCopyOption.REPLACE_EXISTING`
- [ ] **(1.0)** Métodos pequeños con una sola responsabilidad + Javadoc

## Enunciado original

(Ver el enunciado completo que te pasó el profesor.)
