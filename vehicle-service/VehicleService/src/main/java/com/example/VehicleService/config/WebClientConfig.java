// Configuración para la instanciación de WebClient orientado a la comunicación reactiva e interservicio
package com.example.VehicleService.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    // Lee la URL del User Service desde application.properties
    @Value("${userservice.url}")
    private String userServiceUrl;

    @Bean
    public WebClient webClient() {
        return WebClient.builder()
                // URL base del User Service
                // Ej: .uri("/api/v1/users/rut/{rut}") resulta en:
                //     http://localhost:8082/api/v1/users/rut/12345678-9
                .baseUrl(userServiceUrl)
                .build();
    }
}