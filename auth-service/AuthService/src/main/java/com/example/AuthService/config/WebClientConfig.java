package com.example.AuthService.config;

// Configura el cliente HTTP que Auth Service usa para hablar con User Service

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    // Lee la URL del User Service desde application.properties
    // userservice.url=http://localhost:8082
    @Value("${userservice.url}")
    private String userServiceUrl;

    // @Bean registra este WebClient para que Spring lo inyecte en AuthService
    @Bean
    public WebClient webClient() {
        return WebClient.builder()
                // URL base del User Service
                // Cada llamada agrega la ruta específica a esta base
                // Ej: .uri("/api/users/email/{email}") resulta en:
                //     http://localhost:8082/api/v1/users/email/juan@gmail.com
                .baseUrl(userServiceUrl)
                .build();
    }
}
