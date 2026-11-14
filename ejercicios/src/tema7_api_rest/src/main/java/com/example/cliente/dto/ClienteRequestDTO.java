package com.example.cliente.dto;

import com.example.cliente.entity.TipoCliente;
import java.time.LocalDate;

/** Copia temporal con los datos que llegan a la API para crear un cliente
 * evita recibir directamente la entidad Cliente de la BD.
 * No incluye id ni activo: el ID lo genera la base de datos y activo lo asigna Cliente.
 * El record crea el constructor
 * @param codigo
 * @param tipo
 * @param fechaAlta
 */
public record ClienteRequestDTO(
        String codigo,
        TipoCliente tipo,
        LocalDate fechaAlta
) { }
