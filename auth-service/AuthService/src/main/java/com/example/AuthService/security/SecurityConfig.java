// Configura qué rutas necesitan token y cuáles son públicas
// Define las reglas de seguridad del Auth Service
package com.example.AuthService.security;

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

    // Inyectamos nuestro filtro JWT para agregarlo a la cadena de Spring Security
    private final JwtFilter jwtFilter;

    // Spring ejecuta este método al iniciar para configurar la seguridad
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // Desactivar CSRF porque usamos JWT, no sesiones con formularios
                .csrf(csrf -> csrf.disable())

                // Sin estado — cada petición debe traer su propio token JWT
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                .authorizeHttpRequests(auth -> auth
                        // Rutas públicas — no requieren token
                        .requestMatchers("/api/v1/auth/login").permitAll()
                        .requestMatchers("/api/v1/auth/validar").permitAll()
                        .requestMatchers("/api/v1/auth/rol").permitAll()
                        // Todas las demás rutas requieren token válido
                        .anyRequest().authenticated()
                )

                // Nuestro JwtFilter se ejecuta ANTES del filtro de Spring
                // Primero verifica el token, luego Spring verifica los permisos
                .addFilterBefore(jwtFilter,
                        UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
