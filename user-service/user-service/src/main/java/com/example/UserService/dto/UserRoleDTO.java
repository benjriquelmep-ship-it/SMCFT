package com.example.UserService.dto;

// Lo que el cliente manda para asignar un nuevo rol a un usuario
// Separado del UserDTO para mantener responsabilidades claras

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class UserRoleDTO {
    // ID del usuario al que se le asigna el nuevo rol
    // @NotNull porque es Long (número) — @NotBlank solo aplica a String
    @NotNull(message = "El ID del usuario es obligatorio")
    private Long userId;

    // Nuevo rol a asignar al usuario
    @NotBlank(message = "El rol es obligatorio")
    @Pattern(
            regexp = "ADMINISTRADOR|FISCALIZADOR|VIAJERO",
            message = "El rol debe ser ADMINISTRADOR, FISCALIZADOR o VIAJERO"
    )
    private String rol;
}
