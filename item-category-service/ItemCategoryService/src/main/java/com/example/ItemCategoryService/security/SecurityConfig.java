// Configura la seguridad del Item Category Service
// Define qué rutas necesitan token y cuáles son públicas
// GET es público para que Border Crossing Service
// pueda consultar categorías sin token
package com.example.ItemCategoryService.security;

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

                        // GET de categorías es público
                        // Border Crossing Service llama a:
                        // GET /api/v1/item-categories/1
                        // para verificar que la categoría existe y está activa
                        // antes de agregar un item a un cruce
                        // Sin esta ruta pública Border Crossing Service
                        // no podría validar categorías
                        // El ** significa cualquier ruta después
                        // Ej: /api/v1/item-categories   → público
                        //     /api/v1/item-categories/1 → público
                        //     /api/v1/item-categories/activas → público
                        .requestMatchers(HttpMethod.GET,
                                "/api/v1/item-categories/**").permitAll()

                        // GET de items también es público
                        // Otros microservicios pueden consultar items
                        // disponibles sin necesitar token
                        // Ej: /api/v1/items           → público
                        //     /api/v1/items/1         → público
                        //     /api/v1/items/activos   → público
                        .requestMatchers(HttpMethod.GET,
                                "/api/v1/items/**").permitAll()

                        // Swagger/OpenAPI — público para documentación
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()

                        // Cualquier otra petición que no sea GET
                        // requiere que el usuario esté autenticado
                        // Ej: POST /api/v1/item-categories → crear categoría
                        //     PUT  /api/v1/item-categories/1 → actualizar
                        //     PATCH /api/v1/item-categories/1/desactivar
                        //     DELETE /api/v1/item-categories/1 → eliminar
                        //     POST /api/v1/items → crear item
                        //     PUT  /api/v1/items/1 → actualizar item
                        //     PATCH /api/v1/items/1/desactivar
                        //     DELETE /api/v1/items/1 → eliminar item
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