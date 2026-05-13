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
                .addFilterBefore(jwtFilter,
                        UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}