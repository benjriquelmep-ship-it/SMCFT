package com.example.NotificationService.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Value("${deadlineservice.url}")
    private String deadlineServiceUrl;

    // WebClient para Deadline Service
    // Notification Service consulta alertas pendientes
    // y las marca como enviadas
    @Bean
    public WebClient webClient() {
        return WebClient.builder()
                .baseUrl(deadlineServiceUrl)
                .build();
    }
}