package com.example.AuditService.dto;

import lombok.Data;

@Data
public class UserResponseDTO {
    // ID del usuario en la base de datos del User Service
    private Long id;
    // RUT del usuario
    private String rut;
    // Nombre completo del usuario
    private String nombre;
    // Nombre completo del usuario
    private String email;
    // Rol del usuario en el sistema
    private String rol;
    // Indica si el usuario está activo en el sistema
    private Boolean activo;
}