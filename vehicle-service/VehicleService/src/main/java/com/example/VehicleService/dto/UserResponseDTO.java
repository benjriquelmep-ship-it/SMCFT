// Objeto de transferencia de datos que procesa la respuesta entrante desde User Service
package com.example.VehicleService.dto;

import lombok.Data;

// Vehicle Service usa esto para verificar que el RUT del propietario
// existe en User Service antes de registrar un vehículo nuevo
@Data
public class UserResponseDTO {
    // Clave primaria única del usuario en el microservicio origen
    private Long id;

    // Identificador único del ciudadano chileno (RUT)
    private String rut;

    // Nombre completo o razón social del titular
    private String nombre;

    // Correo electrónico principal de la cuenta
    private String email;

    // Nivel de permisos o rol asignado en el sistema
    private String rol;

    // Si activo = false el propietario no puede registrar vehículos
    private Boolean activo;
}