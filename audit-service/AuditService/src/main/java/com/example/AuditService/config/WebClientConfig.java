// config/WebClientConfig.java
package com.example.AuditService.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Value("${userservice.url}")
    private String userServiceUrl;

    // WebClient para User Service
    // Audit Service verifica que el auditor existe
    // antes de registrar una auditoría
    @Bean
    public WebClient webClient() {
        return WebClient.builder()
                .baseUrl(userServiceUrl)
                .build();
    }
}