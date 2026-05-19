// Configura los WebClients para comunicarse con otros microservicios
// Border Crossing se comunica con Vehicle Service e Item Category Service
package com.example.BorderCrossingService.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    // URL del Vehicle Service desde application.properties
    @Value("${vehicleservice.url}")
    private String vehicleServiceUrl;

    // URL del Item Category Service desde application.properties
    @Value("${itemcategoryservice.url}")
    private String itemCategoryServiceUrl;

    // WebClient para Vehicle Service
    // Usado para verificar el vehículo y actualizar su estado
    @Bean(name = "vehicleWebClient")
    public WebClient vehicleWebClient() {
        return WebClient.builder()
                .baseUrl(vehicleServiceUrl)
                .build();
    }

    // WebClient para Item Category Service
    // Usado para validar las categorías del equipaje declarado
    @Bean(name = "itemCategoryWebClient")
    public WebClient itemCategoryWebClient() {
        return WebClient.builder()
                .baseUrl(itemCategoryServiceUrl)
                .build();
    }
}