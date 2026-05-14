// security/SecurityConfig.java
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
                        // GET de notificaciones público para consultas
                        .requestMatchers(HttpMethod.GET,
                                "/api/v1/notifications/**").permitAll()
                        .requestMatchers(HttpMethod.GET,
                                "/api/v1/notification-recipients/**").permitAll()
                        // Destinatarios pueden marcar como leídas sin token
                        .requestMatchers(HttpMethod.PATCH,
                                "/api/v1/notification-recipients/*/leida").permitAll()

                        // RUTAS PROTEGIDAS
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtFilter,
                        UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}