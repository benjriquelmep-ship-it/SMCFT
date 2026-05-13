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

    private final JwtFilter jwtFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http)
            throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session ->
                        session.sessionCreationPolicy(
                                SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth

                        // RUTAS PÚBLICAS
                        // Border Crossing Service llama GET /api/v1/item-categories/{id}
                        // sin token para validar categorías de equipaje
                        .requestMatchers(HttpMethod.GET,
                                "/api/v1/item-categories/**").permitAll()

                        // GET de items también es público
                        // para consultas de los demás microservicios
                        .requestMatchers(HttpMethod.GET,
                                "/api/v1/items/**").permitAll()

                        // RUTAS PROTEGIDAS — requieren token válido
                        // POST, PUT, PATCH, DELETE requieren token
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtFilter,
                        UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}