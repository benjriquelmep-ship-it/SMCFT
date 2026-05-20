// Configura la seguridad del Deadline Service
// Define qué rutas necesitan token y cuáles son públicas
// Tiene rutas públicas especiales para que Notification Service
// pueda consultar y marcar alertas sin token
package com.example.DeadlineService.security;

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
                // Desactiva CSRF porque usamos tokens JWT
                .csrf(csrf -> csrf.disable())

                // Sin sesión en el servidor — cada petición trae su token
                .sessionManagement(session ->
                        session.sessionCreationPolicy(
                                SessionCreationPolicy.STATELESS))

                .authorizeHttpRequests(auth -> auth

                        // GET de deadlines es público
                        // Notification Service y otros microservicios
                        .requestMatchers(HttpMethod.GET,
                                "/api/v1/deadlines/**").permitAll()

                        // GET de alertas es público
                        // Notification Service consulta alertas no enviadas
                        // para saber qué alertas debe procesar
                        .requestMatchers(HttpMethod.GET,
                                "/api/v1/deadline-alerts/**").permitAll()

                        // PATCH /api/v1/deadline-alerts/1/enviada es público
                        // Notification Service marca alertas como enviadas
                        // después de procesarlas — sin necesitar token
                        .requestMatchers(HttpMethod.PATCH,
                                "/api/v1/deadline-alerts/*/enviada").permitAll()

                        // Cualquier otra petición que no sea las de arriba
                        // requiere que el usuario esté autenticado
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