// Configura la seguridad del Entry Service
// Define qué rutas necesitan token y cuáles son públicas
// GET de ingresos es público para que Deadline Service
// pueda verificar ingresos sin token
package com.example.EntryService.security;

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

    // Filtro que verifica el token en cada petición
    private final JwtFilter jwtFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http)
            throws Exception {
        http
                // Desactiva CSRF porque usamos tokens JWT
                // Con JWT no necesitamos esta protección adicional
                .csrf(csrf -> csrf.disable())

                // Sin sesión en el servidor — cada petición trae su token
                .sessionManagement(session ->
                        session.sessionCreationPolicy(
                                SessionCreationPolicy.STATELESS))

                .authorizeHttpRequests(auth -> auth

                        // GET de todos los ingresos es público
                        // Deadline Service llama a:
                        // GET /api/v1/entries/1
                        // para verificar que el ingreso existe
                        // antes de registrar un deadline
                        // Sin esta ruta pública Deadline Service
                        // no podría verificar ingresos
                        // El ** significa cualquier ruta después
                        // Ej: /api/v1/entries → público
                        //     /api/v1/entries/1 → público
                        //     /api/v1/entries/patente/ABC123 → público
                        .requestMatchers(HttpMethod.GET,
                                "/api/v1/entries/**").permitAll()

                        // Cualquier otra petición que no sea GET
                        // requiere que el usuario esté autenticado
                        // Ej: POST /api/v1/entries → registrar ingreso
                        //     PATCH /api/v1/entries/1/autorizar → autorizar
                        //     PATCH /api/v1/entries/1/rechazar  → rechazar
                        //     DELETE /api/v1/entries/1 → eliminar
                        .anyRequest().authenticated()
                )

                // JwtFilter se ejecuta ANTES del filtro por defecto de Spring
                // El orden es:
                // 1. JwtFilter verifica el token
                // 2. UsernamePasswordAuthenticationFilter (filtro de Spring)
                .addFilterBefore(jwtFilter,
                        UsernamePasswordAuthenticationFilter.class);

        // Construye y retorna la configuración de seguridad
        return http.build();
    }
}