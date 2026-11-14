package com.example.cliente.dto;

/** Copia temporal con los datos que llegan a la API para crear un pedido junto a su cliente.
 * Si el cliente no existe, el Service lo crea antes de asociarle el pedido y seguidamente lo asocia.
 * @param cliente datos del cliente
 * @param pedido datos del pedido
 */
public record ClientePedidoDTO(
        ClienteRequestDTO cliente,
        PedidoDTO pedido
) { }
