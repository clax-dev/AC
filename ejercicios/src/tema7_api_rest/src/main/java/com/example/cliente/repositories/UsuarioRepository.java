package com.example.cliente.repositories;

import com.example.cliente.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    // busca un usuario por su login (puede no existir)
    Optional<Usuario> findByUsuarioLogin(String usuarioLogin);
}
