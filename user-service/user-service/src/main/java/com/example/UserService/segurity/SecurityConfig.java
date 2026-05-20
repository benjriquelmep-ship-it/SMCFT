// Configuración centralizada de seguridad y control de accesos HTTP
package com.example.UserService.segurity;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtFilter jwtFilter;

    // Define las reglas de autorización y los filtros de la aplicación
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http)
            throws Exception {
        http
                // Desactiva CSRF por no usar estados de sesión (cookies)
                .csrf(csrf -> csrf.disable())
                // Configura la API como Stateless (sin estado ni sesiones en servidor)
                .sessionManagement(session ->
                        session.sessionCreationPolicy(
                                SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth

                        // RUTAS PÚBLICAS
                        // Auth Service llama este endpoint sin token durante el login
                        .requestMatchers(HttpMethod.GET,
                                "/api/v1/users/email/**").permitAll()

                        // Vehicle Service llama este endpoint sin token
                        // para verificar que el propietario existe
                        .requestMatchers(HttpMethod.GET,
                                "/api/v1/users/rut/**").permitAll()

                        // Crear usuario es público para el registro
                        .requestMatchers(HttpMethod.POST,
                                "/api/v1/users").permitAll()

                        // RUTAS PROTEGIDAS — requieren token válido
                        .anyRequest().authenticated()
                )
                // Registra el filtro JWT antes de la validación por defecto de Spring
                .addFilterBefore(jwtFilter,
                        UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}