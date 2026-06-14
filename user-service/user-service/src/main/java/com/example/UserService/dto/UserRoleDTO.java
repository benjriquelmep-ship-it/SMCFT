// Objeto de transferencia de datos para validar la vinculación de perfiles a un usuario
package com.example.UserService.dto;

// Lo que el cliente manda para asignar un nuevo rol a un usuario
// Separado del UserDTO para mantener responsabilidades claras

import jakarta.validation.constraints.*;
import lombok.Data;
import io.swagger.v3.oas.annotations.media.Schema;

@Data
@Schema(description = "Estructura de entrada requerida para la reasignación o actualización de roles operativos de forma aislada")
public class UserRoleDTO {

    // ID del usuario al que se le asigna el nuevo rol
    @NotNull(message = "El ID del usuario es obligatorio")
    @Schema(
            description = "Identificador único (ID) del usuario en los registros centrales",
            example = "1",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private Long userId;

    // Perfil o privilegio a registrar bajo las opciones del sistema
    @NotBlank(message = "El rol es obligatorio")
    @Pattern(
            regexp = "ADMINISTRADOR|FISCALIZADOR|VIAJERO",
            message = "El rol debe ser ADMINISTRADOR, FISCALIZADOR o VIAJERO"
    )
    @Schema(
            description = "Nuevo nivel de privilegio técnico o perfil institucional a conceder",
            example = "ADMINISTRADOR",
            allowableValues = {"ADMINISTRADOR", "FISCALIZADOR", "VIAJERO"},
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String rol;
}