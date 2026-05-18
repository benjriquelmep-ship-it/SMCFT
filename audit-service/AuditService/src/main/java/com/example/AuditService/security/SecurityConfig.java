// Este archivo configura la seguridad del Audit Service
// Define qué rutas necesitan token y cuáles son públicas
package com.example.AuditService.security;

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
                .csrf(csrf -> csrf.disable())

                // Sin sesión en el servidor — cada petición trae su token
                .sessionManagement(session ->
                        session.sessionCreationPolicy(
                                SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth

                        // RUTAS PÚBLICAS
                        // GET de auditorías es público para consultas
                        .requestMatchers(HttpMethod.GET,
                                "/api/v1/audits/**").permitAll()
                        .requestMatchers(HttpMethod.GET,
                                "/api/v1/audit-details/**").permitAll()

                        // RUTAS PROTEGIDAS
                        // POST, PATCH, DELETE requieren token
                        .anyRequest().authenticated()
                )

                // Así el token se verifica primero en cada petición
                .addFilterBefore(jwtFilter,
                        UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}