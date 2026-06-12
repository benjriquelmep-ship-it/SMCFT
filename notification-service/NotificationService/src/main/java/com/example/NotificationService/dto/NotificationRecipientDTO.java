// Este archivo es el DTO de entrada para agregar un destinatario a una notificación
// DTO = formulario de entrada — define qué datos debe mandar
// el cliente cuando quiere agregar a alguien como destinatario
// Si falta algún campo obligatorio → responde HTTP 400 automáticamente
package com.example.NotificationService.dto;


import jakarta.validation.constraints.*;
import lombok.Data;
import io.swagger.v3.oas.annotations.media.Schema;


@Data
@Schema(description = "DTO que representa notification recipient")
public class NotificationRecipientDTO {

    // ID de la notificación a la que pertenece este destinatario
    @NotNull(message = "El ID de la notificación es obligatorio")
    @Schema(description = "Notification Id", example = "1")
    private Long notificationId;

    // RUT del destinatario que debe recibir la notificación
    @NotBlank(message = "El RUT del destinatario es obligatorio")
    @Size(max = 12,
            message = "El RUT no puede tener más de 12 caracteres")
    @Schema(description = "Rut Destinatario", example = "12345678-9", maxLength = 12)
    private String rutDestinatario;

    // Email del destinatario donde se enviará la notificación
    @NotBlank(message = "El email es obligatorio")
    @Email(message = "El email no tiene formato válido")
    @Schema(description = "Email", example = "usuario@ejemplo.cl")
    private String email;

    // Nombre completo del destinatario
    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 100,
            message = "El nombre no puede tener más de 100 caracteres")
    @Schema(description = "Nombre", example = "Juan Pérez", maxLength = 100)
    private String nombre;
}