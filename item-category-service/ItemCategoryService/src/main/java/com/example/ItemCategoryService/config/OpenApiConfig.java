package com.example.ItemCategoryService.config; // Asegúrate de que calce con tu ruta real de paquetes

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
                        .title("SMCFT - Item Category Service API")
                        .version("1.0")
                        .description("Microservicio encargado de la gestión, clasificación y parametrización de categorías de mercancías y artículos aduaneros.")
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