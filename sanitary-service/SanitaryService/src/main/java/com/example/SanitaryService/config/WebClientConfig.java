package com.example.SanitaryService.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Value("${vehicleservice.url}")
    private String vehicleServiceUrl;

    // WebClient para Vehicle Service
    // Sanitary Service verifica que el vehículo existe
    // antes de registrar una inspección sanitaria
    @Bean
    public WebClient webClient() {
        return WebClient.builder()
                .baseUrl(vehicleServiceUrl)
                .build();
    }
}