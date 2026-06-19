// Configura la comunicación HTTP entre microservicios
package com.example.TransactionService.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Value("${userservice.url}")
    private String userServiceUrl;

    // WebClient para User Service
    // Transaction Service verifica que el usuario existe
    // antes de registrar una transacción
    @Bean
    @LoadBalanced
    public WebClient webClient() {
        return WebClient.builder()
                .baseUrl(userServiceUrl)
                .build();
    }
}