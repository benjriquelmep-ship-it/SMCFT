// dto/UserResponseDTO.java
// Representa la respuesta que llega desde User Service
// cuando Vehicle Service verifica que el propietario existe

package com.example.VehicleService.dto;

import lombok.Data;

// Vehicle Service usa esto para verificar que el RUT del propietario
// existe en User Service antes de registrar un vehículo nuevo
@Data
public class UserResponseDTO {
    private Long id;
    private String rut;
    private String nombre;
    private String email;
    private String rol;
    // Si activo = false el propietario no puede registrar vehículos
    private Boolean activo;
}