package com.example.cliente.service;

import com.example.cliente.dto.AdminResumeDTO;
import com.example.cliente.repositories.ClienteRepository;
import com.example.cliente.repositories.PedidoRepository;
import com.example.cliente.repositories.PermisosClienteRepository;
import com.example.cliente.repositories.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class AdminService {
    private final ClienteRepository clienteRepository;
    private final PedidoRepository pedidoRepository;
    private final UsuarioRepository usuarioRepository;
    private final PermisosClienteRepository permisosClienteRepository;

    public AdminService(ClienteRepository clienteRepository, PedidoRepository pedidoRepository, UsuarioRepository usuarioRepository, PermisosClienteRepository permisosClienteRepository) {
        this.clienteRepository = clienteRepository;
        this.pedidoRepository = pedidoRepository;
        this.usuarioRepository = usuarioRepository;
        this.permisosClienteRepository = permisosClienteRepository;
    }

    public AdminResumeDTO resumen() {
        return new AdminResumeDTO(
                clienteRepository.count(),
                pedidoRepository.count(),
                usuarioRepository.count(),
                permisosClienteRepository.count());
    }

}
