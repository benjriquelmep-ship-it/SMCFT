// Lo que el cliente recibe después de un login exitoso
// El cliente debe guardar el token y enviarlo en cada petición posterior
package com.example.AuthService.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import io.swagger.v3.oas.annotations.media.Schema;

@AllArgsConstructor
@Data
@Schema(description = "Payload de salida que transporta el token de seguridad y los privilegios asignados al usuario")
public class TokenResponseDTO {

    @Schema(
            description = "Token JWT firmado digitalmente que debe adjuntarse en las cabeceras HTTP (Authorization: Bearer <token>)",
            example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String token;

    @Schema(
            description = "Rol operativo asignado para el control de vistas en la interfaz de usuario",
            example = "FISCALIZADOR",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String rol;

    @Schema(
            description = "Mensaje informativo sobre el resultado conforme del proceso de autenticación",
            example = "Autenticación concedida con éxito. Bienvenido al sistema de control fronterizo.",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String mensaje;
}