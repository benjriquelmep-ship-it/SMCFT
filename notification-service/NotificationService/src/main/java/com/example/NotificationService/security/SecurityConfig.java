// Configura la seguridad del Notification Service
// Define qué rutas necesitan token y cuáles son públicas
// GET es público para consultar notificaciones
// PATCH /leida es público para que los destinatarios marquen sin token
package com.example.NotificationService.security;

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

    // Spring ejecuta este método al iniciar para configurar la seguridad
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

                        // GET de notificaciones es público
                        // Cualquiera puede consultar las notificaciones
                        // Ej: /api/v1/notifications        → público
                        //     /api/v1/notifications/1      → público
                        //     /api/v1/notifications/pendientes → público
                        .requestMatchers(HttpMethod.GET,
                                "/api/v1/notifications/**").permitAll()

                        // GET de destinatarios también es público
                        // Los viajeros pueden consultar sus notificaciones
                        // sin necesitar token
                        // Ej: /api/v1/notification-recipients/destinatario/12345678-9
                        //     → el viajero ve sus notificaciones pendientes
                        .requestMatchers(HttpMethod.GET,
                                "/api/v1/notification-recipients/**").permitAll()

                        // PATCH /leida es público
                        // El viajero puede marcar su notificación como leída
                        // sin necesitar token — mejor experiencia de usuario
                        // * = cualquier id
                        // Ej: /notification-recipients/1/leida → público
                        //     /notification-recipients/5/leida → público
                        .requestMatchers(HttpMethod.PATCH,
                                "/api/v1/notification-recipients/*/leida").permitAll()

                        // Swagger/OpenAPI — público para documentación
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()

                        // Cualquier otra petición que no sea las de arriba
                        // requiere que el usuario esté autenticado
                        // Ej: POST /notifications → crear notificación
                        //     POST /notifications/generar-desde-alertas
                        //     PATCH /notifications/1/enviada
                        //     PATCH /notifications/1/error
                        //     DELETE /notifications/1
                        //     POST /notification-recipients → agregar destinatario
                        //     DELETE /notification-recipients/1
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