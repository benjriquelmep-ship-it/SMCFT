// Configura la seguridad del Border Crossing Service
// A diferencia de otros microservicios TODAS las rutas requieren token
// Solo fiscalizadores y administradores operan aquí
package com.example.BorderCrossingService.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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
                // Desactiva CSRF porque usamos tokens JWT. Con JWT no necesitamos esta protección adicional
                .csrf(csrf -> csrf.disable())

                // Sin sesión en el servidor — cada petición trae su token
                .sessionManagement(session ->
                        session.sessionCreationPolicy(
                                SessionCreationPolicy.STATELESS))

                .authorizeHttpRequests(auth -> auth

                        // Swagger/OpenAPI — público para documentación
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()

                        // Aquí TODO requiere token válido porque: registrar cruces = acción sensible de seguridad..
                        // Solo fiscalizadores y administradores operan aquí
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtFilter,
                        UsernamePasswordAuthenticationFilter.class);

        // Construye y retorna la configuración de seguridad
        return http.build();
    }
}