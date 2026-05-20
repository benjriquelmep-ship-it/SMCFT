// Este archivo es el DTO de entrada para agregar un destinatario a una notificación
// DTO = formulario de entrada — define qué datos debe mandar
// el cliente cuando quiere agregar a alguien como destinatario
// Si falta algún campo obligatorio → responde HTTP 400 automáticamente
package com.example.NotificationService.dto;


import jakarta.validation.constraints.*;
import lombok.Data;


@Data
public class NotificationRecipientDTO {

    // ID de la notificación a la que pertenece este destinatario
    @NotNull(message = "El ID de la notificación es obligatorio")
    private Long notificationId;

    // RUT del destinatario que debe recibir la notificación
    @NotBlank(message = "El RUT del destinatario es obligatorio")
    @Size(max = 12,
            message = "El RUT no puede tener más de 12 caracteres")
    private String rutDestinatario;

    // Email del destinatario donde se enviará la notificación
    @NotBlank(message = "El email es obligatorio")
    @Email(message = "El email no tiene formato válido")
    private String email;

    // Nombre completo del destinatario
    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 100,
            message = "El nombre no puede tener más de 100 caracteres")
    private String nombre;
}