package com.example.UserService.segurity;

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
                        // Auth Service llama este endpoint sin token durante el login
                        .requestMatchers(HttpMethod.GET,
                                "/api/v1/users/email/**").permitAll()

                        // Vehicle Service llama este endpoint sin token
                        // para verificar que el propietario existe
                        .requestMatchers(HttpMethod.GET,
                                "/api/v1/users/rut/**").permitAll()

                        // Crear usuario es público para el registro
                        .requestMatchers(HttpMethod.POST,
                                "/api/v1/users").permitAll()

                        // RUTAS PROTEGIDAS — requieren token válido
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtFilter,
                        UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}