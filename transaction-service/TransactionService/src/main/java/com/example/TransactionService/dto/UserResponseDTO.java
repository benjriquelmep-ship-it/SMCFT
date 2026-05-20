// Estructura de datos que mapea la respuesta recibida del User Service
package com.example.TransactionService.dto;

import lombok.Data;

@Data
public class UserResponseDTO {
    // Identificador único del usuario en el microservicio origen
    private Long id;

    // RUN o documento de identidad oficial del usuario
    private String rut;

    // Nombre completo o razón social registrada
    private String nombre;

    // Dirección de correo electrónico principal
    private String email;

    // Perfil o rol asignado en el sistema (ej: FISCALIZADOR, ADMIN)
    private String rol;

    // Estado de habilitación de la cuenta en el sistema
    private Boolean activo;
}