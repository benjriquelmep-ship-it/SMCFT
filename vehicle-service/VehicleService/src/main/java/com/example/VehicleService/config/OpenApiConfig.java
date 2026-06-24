package com.example.VehicleService.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        final String securitySchemeName = "bearerAuth";
        return new OpenAPI()
                .addServersItem(new Server().url("http://localhost:2020"))
                .info(new Info()
                        .title("SMCFT - Vehicle Service API")
                        .version("1.0")
                        .description("Microservicio encargado del registro, control técnico y validación de propietarios de vehículos en la frontera.")
                        .contact(new Contact()
                                .name("Benjamin Riquelme")
                                .email("benjamin@ejemplo.cl")))
                .schemaRequirement(securitySchemeName, new SecurityScheme()
                        .name(securitySchemeName)
                        .type(SecurityScheme.Type.HTTP)
                        .scheme("bearer")
                        .bearerFormat("JWT"));
    }
}