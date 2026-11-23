package com.example.cliente.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
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
     * 3. revisa las rutas de arriba a abajo y aplica la primera que coincide
     *    - /error: libre, para ver los errores reales
     *    - /api/admin: solo disponible para ADMIN
     *    - consultar clientes y pedidos (GET), disponible para: ADMIN, SALES y USER
     *    - crear pedidos (POST), disponible para: ADMIN y SALES
     *    - el resto de /api/clientes (crear, editar y borrar clientes), disponible para: solo ADMIN
     *    - /hola, disponible para: cualquier usuario logueado
     *    - cualquier otra ruta: denegada, error 403
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
                        // ruta de error de Spring, libre para que se vea el error real
                        .requestMatchers("/error").permitAll()
                        // pantalla del frontend (MVP): la pagina es publica, los datos piden login
                        .requestMatchers("/", "/index.html").permitAll()
                        // panel de administracion, solo ADMIN
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")
                        // consultar clientes y pedidos, los tres roles
                        .requestMatchers(HttpMethod.GET, "/api/clientes/**").hasAnyRole("ADMIN", "SALES", "USER")
                        // crear pedidos, ADMIN y SALES (va antes que la siguiente)
                        .requestMatchers(HttpMethod.POST, "/api/clientes/*/pedidos").hasAnyRole("ADMIN", "SALES")
                        // crear, editar y borrar clientes, solo ADMIN
                        .requestMatchers("/api/clientes/**").hasRole("ADMIN")
                        // ruta de prueba, cualquiera con login
                        .requestMatchers("/hola").authenticated()
                        // el resto denegado por defecto (403)
                        .anyRequest().denyAll())
                .httpBasic(Customizer.withDefaults());

        return http.build();
    }
}