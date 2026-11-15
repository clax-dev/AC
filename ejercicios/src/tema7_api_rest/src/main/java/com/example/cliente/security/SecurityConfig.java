package com.example.cliente.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    /**
     * metodo de SpringBoot
     * @return compara contraseñas
     */
    @Bean
    public PasswordEncoder comparadorContrasena() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Normas que Spring aplica a cada peticion
     * 1. Desactiva el CSRF
     * 2. Stateless
     * 3. exige el rol admin para navegar por todas     las rutas, si no error 403
     * 4. usuario y contraseña viajan en la cabecera
     * @param http configuracion de seguridad de Spring
     * @return las reglas de comparacion
     * @throws Exception si no se puede hacer
     */
    @Bean
    public SecurityFilterChain normas(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable())
                .sessionManagement(sesion -> sesion.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(rutas -> rutas
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")
                        .anyRequest().authenticated())
                .httpBasic(Customizer.withDefaults());

        return http.build();
    }
}