package com.example.cliente.repository;

import com.example.cliente.dto.ClienteResponseDTO;
import com.example.cliente.entity.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

public interface PedidoRepository extends JpaRepository<Pedido, Long> {
    List<Pedido> findByClienteId(long id);}
