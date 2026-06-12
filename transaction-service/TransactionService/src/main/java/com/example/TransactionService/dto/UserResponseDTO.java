// Estructura de datos que mapea la respuesta recibida del User Service
package com.example.TransactionService.dto;

import lombok.Data;
import io.swagger.v3.oas.annotations.media.Schema;

@Data
@Schema(description = "DTO que representa user")
public class UserResponseDTO {
    // Identificador único del usuario en el microservicio origen
    @Schema(description = "Id", example = "1")
    private Long id;

    // RUN o documento de identidad oficial del usuario
    @Schema(description = "Rut", example = "12345678-9")
    private String rut;

    // Nombre completo o razón social registrada
    @Schema(description = "Nombre", example = "Juan Pérez")
    private String nombre;

    // Dirección de correo electrónico principal
    @Schema(description = "Email", example = "usuario@ejemplo.cl")
    private String email;

    // Perfil o rol asignado en el sistema (ej: FISCALIZADOR, ADMIN)
    @Schema(description = "Rol", example = "VIAJERO")
    private String rol;

    // Estado de habilitación de la cuenta en el sistema
    @Schema(description = "Activo", example = "true")
    private Boolean activo;
}