package com.example.cliente.repositories;

import com.example.cliente.entity.PermisoCliente;
import com.example.cliente.entity.TipoCliente;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PermisosClienteRepository extends JpaRepository<PermisoCliente, Long> {
    // devuelve todos los permisos de un usuario, buscando por su login
    List<PermisoCliente> findByUsuarioUsuarioLogin(String usuarioLogin);

    // Select que confirma si tienes ese usuario o no aceptado
    boolean existsByUsuarioUsuarioLoginAndClienteId(String usuarioLogin, Long clienteId);

    // usuario asignado al codigo
    boolean existsByUsuarioUsuarioLoginAndClienteCodigo(String usuarioLogin, String codigo);

    // permisos, ordenados por codigo, cuyo cliente esta activo
    List<PermisoCliente> findByUsuarioUsuarioLoginAndClienteActivoTrueOrderByClienteCodigoAsc(String usuarioLogin);

    // permisos del usuario cuyo cliente es de tipo empresa o persona
    List<PermisoCliente> findByUsuarioUsuarioLoginAndClienteTipo(String usuarioLogin, TipoCliente tipo);
}
