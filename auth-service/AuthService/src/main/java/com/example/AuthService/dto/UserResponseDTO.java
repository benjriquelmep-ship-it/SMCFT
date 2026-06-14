// Representa la respuesta que llega desde User Service
// cuando Auth Service le pregunta GET /api/v1/users/email/{email}
// Auth Service usa este objeto para verificar credenciales
// SIN tener su propia tabla de usuarios
package com.example.AuthService.dto;

import lombok.Data;
import io.swagger.v3.oas.annotations.media.Schema;

@Data
@Schema(description = "Payload de integración remota que expone los datos registrales de identidad del usuario provenientes del User Service")
public class UserResponseDTO {

    @Schema(description = "Identificador único (ID) del usuario en la base de datos central", example = "1")
    private Long id;

    @Schema(description = "RUN/RUT oficial o pasaporte del usuario", example = "12345678-9")
    private String rut;

    @Schema(description = "Nombre completo o denominación oficial del usuario", example = "Juan Pérez")
    private String nombre;

    @Schema(description = "Correo electrónico que actúa como identificador de login único", example = "fiscalizador@aduana.cl")
    private String email;

    @Schema(description = "Hash criptográfico de la contraseña del usuario almacenado en el User Service", example = "$2a$10$R9h/l74...")
    private String password;

    @Schema(description = "Rol o perfil operativo asignado al usuario", example = "FISCALIZADOR")
    private String rol;

    @Schema(description = "Flag de habilitación administrativa lógica para permitir o denegar el inicio de sesión", example = "true")
    private Boolean activo;
}