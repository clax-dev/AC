package com.example.cliente.repositories;

import com.example.cliente.entity.PermisoCliente;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PermisosClienteRepository extends JpaRepository<PermisoCliente, Long> {
    // devuelve todos los permisos de un usuario, buscando por su login
    List<PermisoCliente> findByUsuarioUsuarioLogin(String usuarioLogin);
}
