// Configuración centralizada de seguridad y control de accesos HTTP para el Vehicle Service
package com.example.VehicleService.segurity;

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
                // Configura la API como Stateless (sin estado ni sesiones en el servidor)
                .sessionManagement(session ->
                        session.sessionCreationPolicy(
                                SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth

                        // RUTAS PÚBLICAS
                        // Border Crossing Service llama este endpoint sin token
                        // para verificar el vehículo antes de registrar un cruce
                        .requestMatchers(HttpMethod.GET,
                                "/api/v1/vehicles/patente/**").permitAll()

                        // Border Crossing y Entry Service llaman este endpoint
                        // sin token para actualizar el estado del vehículo
                        .requestMatchers(HttpMethod.PATCH,
                                "/api/v1/vehicles/patente/*/estado").permitAll()

                        // RUTAS PROTEGIDAS — requieren token válido
                        .anyRequest().authenticated()
                )
                // Registra el filtro JWT antes de la validación por defecto de Spring
                .addFilterBefore(jwtFilter,
                        UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}