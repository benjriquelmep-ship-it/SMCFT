// Este archivo es el DTO de entrada para agregar un destinatario a una notificación
// DTO = formulario de entrada — define qué datos debe mandar
// el cliente cuando quiere agregar a alguien como destinatario
// Si falta algún campo obligatorio → responde HTTP 400 automáticamente
package com.example.NotificationService.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import io.swagger.v3.oas.annotations.media.Schema;

@Data
@Schema(description = "Estructura de datos requerida para anexar e indexar un destinatario físico con sus canales de mensajería activos a una notificación")
public class NotificationRecipientDTO {

    // ID de la notificación a la que pertenece este destinatario
    @NotNull(message = "El ID de la notificación es obligatorio")
    @Schema(
            description = "Identificador único (ID) de la notificación de cabecera a la cual pertenece este destinatario",
            example = "1",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private Long notificationId;

    // RUT del destinatario que debe recibir la notificación
    @NotBlank(message = "El RUT del destinatario es obligatorio")
    @Size(max = 12, message = "El RUT no puede tener más de 12 caracteres")
    @Schema(
            description = "RUN/RUT de identidad legal de la persona que recibirá el correo electrónico",
            example = "12345678-9",
            maxLength = 12,
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String rutDestinatario;

    // Email del destinatario donde se enviará la notificación
    @NotBlank(message = "El email es obligatorio")
    @Email(message = "El email no tiene formato válido")
    @Schema(
            description = "Dirección de correo electrónico principal a donde se despachará la plantilla del correo",
            example = "usuario@ejemplo.cl",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String email;

    // Nombre completo del destinatario
    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 100, message = "El nombre no puede tener más de 100 caracteres")
    @Schema(
            description = "Nombre completo o razón social del ciudadano o transportista destinatario",
            example = "Benjamin Alexis Riquelme Pozo",
            maxLength = 100,
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String nombre;
}