package com.example.cliente.security;

import com.example.cliente.entity.Usuario;
import com.example.cliente.repositories.UsuarioRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service // Spring recoge a la clase como Bean
public class UsuarioDetailsService implements UserDetailsService {
    private final UsuarioRepository usuarioRepository;

    public UsuarioDetailsService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    /**
     * Metodo que comprueba y carga los datos del usuario introducido en el
     * inicio de sesión
     * @param usuarioLogin usuario introducido como atributo y que identificamos
     * @return datos del usuario
     * @throws UsernameNotFoundException
     */
    @Override
    public UserDetails loadUserByUsername(String usuarioLogin) throws UsernameNotFoundException {
        // Comprobacion de la existencia del usuario introducido como atributo
        Usuario user = usuarioRepository.findByUsuarioLogin(usuarioLogin)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + usuarioLogin));

        // devolver los datos del usuario en forma de builder (.build();)
        return User.withUsername(user.getUsuarioLogin())
                .password(user.getContrasena())
                .roles(user.getRol().getTipoRol().name())
                .build();
    }



    public UsuarioRepository getUsuarioRepository() {
        return usuarioRepository;
    }


}

