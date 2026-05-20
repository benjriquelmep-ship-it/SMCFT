// Objeto de transferencia de datos para validar el registro y actualización de usuarios
package com.example.UserService.dto;

// Lo que el cliente manda en el body del POST/PUT

import jakarta.validation.constraints.*;
import lombok.Data;

@Data   // Lombok genera getters y setters
public class UserDTO {
    // Documento de identidad único (RUN chileno)
    @NotBlank(message = "El RUT es obligatorio")
    @Size(max = 12, message = "El RUT no puede tener más de 12 caracteres")
    private String rut;

    // Nombre completo o razón social del usuario
    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 100, message = "El nombre no puede tener más de 100 caracteres")
    private String nombre;

    // Dirección de correo electrónico principal
    @NotBlank(message = "El email es obligatorio")
    @Email(message = "El email no tiene formato válido")
    private String email;

    // Clave de acceso secreta que será encriptada
    @NotBlank(message = "La contraseña es obligatoria")
    @Size(min = 6, message = "La contraseña debe tener al menos 6 caracteres")
    private String password;

    // Tipo de perfil asignado según expresión regular
    @NotBlank(message = "El rol es obligatorio")
    @Pattern(regexp = "ADMINISTRADOR|FISCALIZADOR|VIAJERO",
            message = "El rol debe ser ADMINISTRADOR, FISCALIZADOR o VIAJERO")
    private String rol;
}