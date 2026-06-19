//webclientconfig sirve para comunicarse con el user_service
package com.example.AuditService.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.web.reactive.function.client.WebClient;


@Configuration
public class WebClientConfig {

    //lee la URL del User Service desde application.properties
    @Value("${userservice.url}")
    private String userServiceUrl;


    @Bean
    @LoadBalanced
    public WebClient webClient() {
        //hace el llamado al user service
        return WebClient.builder()
                //guarda la URL base para no tener que escribirla completa cada vez que hagas una llamada
                .baseUrl(userServiceUrl)
                .build();
    }
}