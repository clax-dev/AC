package com.example.cliente.security;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;


@Component
public class UsuarioActual {

    // devuelve el nombre del usuario que ha hecho la peticion
    public String login() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

    // devuelve true si el usuario conectado tiene el rol ADMIN
    public boolean esAdmin() {
        return SecurityContextHolder.getContext().getAuthentication()
                .getAuthorities()
                .stream()
                .anyMatch(rol -> rol.getAuthority().equals("ROLE_ADMIN"));
    }
}