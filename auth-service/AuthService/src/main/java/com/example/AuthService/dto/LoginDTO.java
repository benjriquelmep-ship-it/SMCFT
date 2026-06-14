// Lo que el cliente manda al endpoint de login
// Solo necesita email y password — nunca más datos de los necesarios
package com.example.AuthService.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import io.swagger.v3.oas.annotations.media.Schema;

@Data
@Schema(description = "Estructura de datos requerida para la solicitud de autenticación y verificación de credenciales de usuario")
public class LoginDTO {

    @NotBlank(message = "El email es obligatorio")
    @Email(message = "El email no tiene formato válido")
    @Schema(
            description = "Correo electrónico institucional o personal del usuario registrado",
            example = "fiscalizador@aduana.cl",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String email;

    @NotBlank(message = "La contraseña es obligatoria")
    @Schema(
            description = "Contraseña secreta del usuario en texto plano para validación criptográfica",
            example = "ClaveSegura123!",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String password;
}