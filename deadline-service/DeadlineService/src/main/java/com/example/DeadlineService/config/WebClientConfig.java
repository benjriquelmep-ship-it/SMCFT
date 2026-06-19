// Configura el WebClient para comunicarse con Entry Service
// Entry Service tiene los ingresos al país que originan los deadlines
package com.example.DeadlineService.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    // Lee la URL de Entry Service desde application.properties
    // Deadline Service la usa para verificar que el ingreso existe antes de registrar un deadline
    @Value("${entryservice.url}")
    private String entryServiceUrl;

    //spring guarda este WebClient para inyectarlo automáticamente en DeadlineService cuando lo necesite
    @Bean
    @LoadBalanced
    public WebClient webClient() {

        // Define la URL base de todas las peticiones
        return WebClient.builder()
                .baseUrl(entryServiceUrl)

                // Termina la construcción y retorna el WebClient listo
                .build();
    }
}