package com.example.UserService.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        final String securitySchemeName = "bearerAuth"; // Esquema de seguridad coincidente con la guía
        return new OpenAPI()
                .info(new Info()
                        .title("SMCFT - User Service API")
                        .version("1.0")
                        .description("Microservicio encargado de la gestión de usuarios, roles y perfiles aduaneros.")
                        .contact(new Contact()
                                .name("Benjamin Riquelme")
                                .email("benjamin@ejemplo.cl"))) // Ajustado con tus datos de desarrollo
                .addSecurityItem(new SecurityRequirement().addList(securitySchemeName))
                .schemaRequirement(securitySchemeName, new SecurityScheme()
                        .name(securitySchemeName)
                        .type(SecurityScheme.Type.HTTP)
                        .scheme("bearer")
                        .bearerFormat("JWT"));
    }
}