// Configura la comunicación HTTP entre microservicios
package com.example.SanitaryService.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Value("${vehicleservice.url}")
    private String vehicleServiceUrl;

    // WebClient para User Service
    // Transaction Service verifica que el usuario existe
    // antes de registrar una transacción
    @Bean
    @LoadBalanced
    public WebClient webClient() {
        return WebClient.builder()
                .baseUrl(vehicleServiceUrl)
                .build();
    }
}