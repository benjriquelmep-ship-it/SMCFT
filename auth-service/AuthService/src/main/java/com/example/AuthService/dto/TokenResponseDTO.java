package com.example.AuthService.dto;

// Lo que el cliente recibe después de un login exitoso
// El cliente debe guardar el token y enviarlo en cada petición posterior

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class TokenResponseDTO {
    // Token JWT que el cliente guarda y envía en cada petición
    // Header de cada petición: Authorization: Bearer eyJhbGci...
    private String token;

    // Rol del usuario para que el frontend sepa qué pantallas mostrar
    // Ej: FISCALIZADOR → panel de fiscalización
    //     VIAJERO      → formularios de declaración
    private String rol;

    // Mensaje de confirmación para el cliente
    private String mensaje;
}
