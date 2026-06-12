package com.example.AuthService.dto;

// Lo que el cliente recibe después de un login exitoso
// El cliente debe guardar el token y enviarlo en cada petición posterior

import lombok.AllArgsConstructor;
import lombok.Data;
import io.swagger.v3.oas.annotations.media.Schema;

@AllArgsConstructor
@Data
@Schema(description = "DTO que representa token")
public class TokenResponseDTO {

    // Token JWT que el cliente guarda y envía en cada petición
    // Header de cada petición: Authorization: Bearer eyJhbGci...
    private String token;

    // Rol del usuario para que el frontend sepa qué pantallas mostrar
    @Schema(description = "Rol", example = "VIAJERO")
    private String rol;

    // Mensaje de confirmación para el cliente
    @Schema(description = "Mensaje", example = "ejemplo")
    private String mensaje;
}
