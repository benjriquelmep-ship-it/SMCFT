package com.example.AuthService.dto;

// Representa la respuesta que llega desde User Service
// cuando Auth Service le pregunta GET /api/v1/users/email/{email}
// Auth Service usa este objeto para verificar credenciales
// SIN tener su propia tabla de usuarios

import lombok.Data;

@Data

public class UserResponseDTO {
    private Long id;
    private String rut;
    private String nombre;
    private String email;

    // Contraseña que viene de User Service
    // Auth Service la compara con la que mandó el cliente en LoginDTO
    private String password;

    // Rol que se incluye en el token JWT después del login exitoso
    private String rol;

    // Si activo = false el usuario no puede hacer login
    // aunque la contraseña sea correcta
    private Boolean activo;
}
