// security/SecurityConfig.java
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
                        // Notification Service consulta alertas sin token
                        .requestMatchers(HttpMethod.GET,
                                "/api/v1/deadlines/**").permitAll()
                        .requestMatchers(HttpMethod.GET,
                                "/api/v1/deadline-alerts/**").permitAll()
                        // Notification Service marca alertas como enviadas
                        .requestMatchers(HttpMethod.PATCH,
                                "/api/v1/deadline-alerts/*/enviada").permitAll()

                        // RUTAS PROTEGIDAS
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtFilter,
                        UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}