package com.example.AuditService.dto;

import lombok.Data;
import io.swagger.v3.oas.annotations.media.Schema;

@Data
@Schema(description = "DTO que representa user")
public class UserResponseDTO {
    // ID del usuario en la base de datos del User Service
    @Schema(description = "Id", example = "1")
    private Long id;
    // RUT del usuario
    @Schema(description = "Rut", example = "12345678-9")
    private String rut;
    // Nombre completo del usuario
    @Schema(description = "Nombre", example = "Juan Pérez")
    private String nombre;
    // Nombre completo del usuario
    @Schema(description = "Email", example = "usuario@ejemplo.cl")
    private String email;
    // Rol del usuario en el sistema
    @Schema(description = "Rol", example = "VIAJERO")
    private String rol;
    // Indica si el usuario está activo en el sistema
    @Schema(description = "Activo", example = "true")
    private Boolean activo;
}