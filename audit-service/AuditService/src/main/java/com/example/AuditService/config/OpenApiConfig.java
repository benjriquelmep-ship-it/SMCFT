package com.example.AuditService.config; // Valida que coincida con tu paquete real

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
        final String securitySchemeName = "bearerAuth";

        return new OpenAPI()
                .info(new Info()
                        .title("SMCFT - Audit Service API")
                        .version("1.0")
                        .description("Microservicio analítico de auditoría e integridad. Encargado del almacenamiento inmutable, indexación e historial forense de operaciones y eventos del ecosistema fronterizo.")
                        .contact(new Contact()
                                .name("Benjamin Riquelme")
                                .email("benjamin@ejemplo.cl")))
                .addSecurityItem(new SecurityRequirement().addList(securitySchemeName))
                .schemaRequirement(securitySchemeName, new SecurityScheme()
                        .name(securitySchemeName)
                        .type(SecurityScheme.Type.HTTP)
                        .scheme("bearer")
                        .bearerFormat("JWT"));
    }
}