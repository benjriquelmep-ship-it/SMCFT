package com.example.NotificationService.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Value("${deadlineservice.url}")
    private String deadlineServiceUrl;

    @Value("${userservice.url}")
    private String userServiceUrl;

    // WebClient para Deadline Service
    // Notification Service consulta alertas pendientes
    // y las marca como enviadas
    @Bean
    @LoadBalanced
    public WebClient webClient() {
        return WebClient.builder()
                .baseUrl(deadlineServiceUrl)
                .build();
    }

    // WebClient para User Service
    // Notification Service consulta datos de usuarios (RUT, email, nombre)
    // para crear destinatarios de notificaciones
    @Bean
    @LoadBalanced
    public WebClient userServiceWebClient() {
        return WebClient.builder()
                .baseUrl(userServiceUrl)
                .build();
    }
}