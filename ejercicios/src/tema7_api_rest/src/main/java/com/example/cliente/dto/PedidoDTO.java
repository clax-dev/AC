package com.example.cliente.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

/** Copia temporal con los datos de un pedido que devuelve la API
 * evita exponer directamente la entidad Pedido de la BD.
 * NO incluye objeto Cliente completo (solo ID).
 * @param id
 * @param fecha
 * @param total
 * @param clienteId
 */
public record PedidoDTO(
        Integer id,
        LocalDate fecha,
        BigDecimal total,
        Long clienteId
) { }
