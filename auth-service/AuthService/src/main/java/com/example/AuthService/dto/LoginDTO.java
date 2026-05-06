package com.example.AuthService.dto;

// Lo que el cliente manda al endpoint de login
// Solo necesita email y password — nunca más datos de los necesarios

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginDTO {
    // @NotBlank valida que no sea null, vacío ni solo espacios
    // @Email verifica que tenga formato de email válido (contiene @ y dominio)
    @NotBlank(message = "El email es obligatorio")
    @Email(message = "El email no tiene formato válido")
    private String email;

    // Solo verificamos que no esté vacío
    // No ponemos @Size mínimo porque el usuario ya tiene su contraseña registrada
    @NotBlank(message = "La contraseña es obligatoria")
    private String password;
}
