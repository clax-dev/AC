package com.example.cliente.dto;

import com.example.cliente.entity.TipoCliente;

import java.time.LocalDate;

/** Copia temporal con los datos que la API devuelve al cliente
 * evita exponer directamente la entidad Cliente de la BD.
 * Se crea para responder a la petición y no modifica la base de datos.
 * El record crea el constructor
 * @param id
 * @param codigo
 * @param tipo
 * @param fechaAlta
 * @param activo
 */
public record ClienteResponseDTO(
        Long id,
        String codigo,
        TipoCliente tipo,
        LocalDate fechaAlta,
        boolean activo
) {
}