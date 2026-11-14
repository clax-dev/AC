package com.example.cliente.mapper;

import com.example.cliente.dto.ClienteResponseDTO;
import com.example.cliente.dto.PedidoDTO;
import com.example.cliente.entity.Cliente;
import com.example.cliente.entity.Pedido;
import org.springframework.stereotype.Component;

@Component
public class PedidoMapper {
    // transforma una entidad a otra transfiriendo los datos de uno a otro (entidad -> DTO | DTO -> entidad)
    public PedidoDTO toDto(Pedido pedido) {
        return new PedidoDTO(
                pedido.getId(),
                pedido.getFecha(),
                pedido.getTotal(),
                pedido.getCliente().getId()
        );
    }

    // crea un objeto pedido
    public Pedido toEntity(PedidoDTO dto, Cliente cliente) {
        return new Pedido(dto.fecha(), dto.total(), cliente);
    }
}
