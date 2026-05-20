// Configura el WebClient para comunicarse con Vehicle Service
// Entry Service actualiza el estado del vehículo al registrar un ingreso
package com.example.EntryService.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    // URL del Vehicle Service desde application.properties
    @Value("${vehicleservice.url}")
    private String vehicleServiceUrl;

    // WebClient para Vehicle Service
    // Entry Service lo usa para:
    // 1. Verificar el estado actual del vehículo
    // 2. Actualizar el estado a EN_TERRITORIO_NACIONAL al ingresar
    // 3. Actualizar el estado a ADMISION_TEMPORAL si es extranjero
    @Bean
    public WebClient webClient() {
        return WebClient.builder()
                .baseUrl(vehicleServiceUrl)
                .build();
    }
}