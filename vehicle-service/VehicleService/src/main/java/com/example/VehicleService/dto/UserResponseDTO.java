// Objeto de transferencia de datos que procesa la respuesta entrante desde User Service
package com.example.VehicleService.dto;

import lombok.Data;
import io.swagger.v3.oas.annotations.media.Schema;

// Vehicle Service usa esto para verificar que el RUT del propietario
// existe en User Service antes de registrar un vehículo nuevo
@Data
@Schema(description = "DTO que representa user")
public class UserResponseDTO {
    // Clave primaria única del usuario en el microservicio origen
    @Schema(description = "Id", example = "1")
    private Long id;

    // Identificador único del ciudadano chileno (RUT)
    @Schema(description = "Rut", example = "12345678-9")
    private String rut;

    // Nombre completo o razón social del titular
    @Schema(description = "Nombre", example = "Juan Pérez")
    private String nombre;

    // Correo electrónico principal de la cuenta
    @Schema(description = "Email", example = "usuario@ejemplo.cl")
    private String email;

    // Nivel de permisos o rol asignado en el sistema
    @Schema(description = "Rol", example = "VIAJERO")
    private String rol;

    // Si activo = false el propietario no puede registrar vehículos
    @Schema(description = "Activo", example = "true")
    private Boolean activo;
}