// config/WebClientConfig.java
package com.example.ReportService.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {
    // URL base remota (leída desde application.properties)
    @Value("${bordercrossingservice.url}")
    private String borderCrossingServiceUrl;

    // WebClient para Border Crossing Service
    // Report Service obtiene datos de cruces
    // para generar reportes de actividad fronteriza
    @Bean
    public WebClient webClient() {
        // Inicializa el constructor de WebClient y le asigna la URL base del microservicio remoto
        return WebClient.builder()
                .baseUrl(borderCrossingServiceUrl)
                .build();
    }
}