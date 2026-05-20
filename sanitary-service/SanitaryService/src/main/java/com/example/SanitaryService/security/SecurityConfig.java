// Configura la seguridad del Sanitary Service
// Define qué rutas necesitan token y cuáles son públicas
// GET es público para consultar inspecciones
// POST, PUT, PATCH, DELETE requieren token — solo inspectores autorizados
package com.example.SanitaryService.security;

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
    // Se inyecta automáticamente por @RequiredArgsConstructor
    private final JwtFilter jwtFilter;

    // Spring ejecuta este método al iniciar para configurar la seguridad
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http)
            throws Exception {
        http
                .csrf(csrf -> csrf.disable())

                // Sin sesión en el servidor — cada petición trae su token
                // STATELESS = el servidor no recuerda a los usuarios
                // entre peticiones — correcto para APIs REST con JWT
                .sessionManagement(session ->
                        session.sessionCreationPolicy(
                                SessionCreationPolicy.STATELESS))

                .authorizeHttpRequests(auth -> auth

                        // GET de inspecciones es público
                        // Cualquiera puede consultar las inspecciones
                        // El ** significa cualquier ruta después
                        // Ej: /api/v1/sanitary              → público
                        //     /api/v1/sanitary/1            → público
                        //     /api/v1/sanitary/patente/ABC123 → público
                        //     /api/v1/sanitary/items         → público
                        //     /api/v1/sanitary/ultimas       → público
                        .requestMatchers(HttpMethod.GET,
                                "/api/v1/sanitary/**").permitAll()

                        // Cualquier otra petición que no sea GET
                        // requiere que el usuario esté autenticado
                        // Ej: POST /api/v1/sanitary → registrar inspección
                        //     PATCH /api/v1/sanitary/1/aprobar → aprobar
                        //     PATCH /api/v1/sanitary/1/rechazar → rechazar
                        //     DELETE /api/v1/sanitary/1 → eliminar
                        //     POST /api/v1/sanitary/items → agregar item
                        //     PUT  /api/v1/sanitary/items/1 → actualizar item
                        //     DELETE /api/v1/sanitary/items/1 → eliminar item
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