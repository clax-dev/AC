package com.example.cliente.mapper;

import com.example.cliente.dto.ClienteRequestDTO;
import com.example.cliente.dto.ClienteResponseDTO;
import com.example.cliente.entity.Cliente;
import org.springframework.stereotype.Component;

@Component
public class ClienteMapper {
    // transforma una entidad a otra transfiriendo los datos de uno a otro (entidad -> DTO | DTO -> entidad)
    public ClienteResponseDTO toDto(Cliente cliente) {
        return new ClienteResponseDTO(
                cliente.getId(),
                cliente.getCodigo(),
                cliente.getTipo(),
                cliente.getFechaAlta(),
                cliente.isActivo()
        );
    }

    // crea un objeto
    public Cliente toEntity(ClienteRequestDTO dto) {
        return new Cliente(dto.codigo(), dto.tipo(), dto.fechaAlta());
    }
}