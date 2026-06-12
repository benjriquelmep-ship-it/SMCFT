package com.example.AuthService.dto;

// Representa la respuesta que llega desde User Service
// cuando Auth Service le pregunta GET /api/v1/users/email/{email}
// Auth Service usa este objeto para verificar credenciales
// SIN tener su propia tabla de usuarios

import lombok.Data;
import io.swagger.v3.oas.annotations.media.Schema;

@Data

@Schema(description = "DTO que representa user")
public class UserResponseDTO {
    @Schema(description = "Id", example = "1")
    private Long id;
    @Schema(description = "Rut", example = "12345678-9")
    private String rut;
    @Schema(description = "Nombre", example = "Juan Pérez")
    private String nombre;
    @Schema(description = "Email", example = "usuario@ejemplo.cl")
    private String email;

    // Contraseña que viene de User Service
    // Auth Service la compara con la que mandó el cliente en LoginDTO
    @Schema(description = "Password", example = "********")
    private String password;

    // Rol que se incluye en el token JWT después del login exitoso
    @Schema(description = "Rol", example = "VIAJERO")
    private String rol;

    // Si activo = false el usuario no puede hacer login
    // aunque la contraseña sea correcta
    @Schema(description = "Activo", example = "true")
    private Boolean activo;
}
