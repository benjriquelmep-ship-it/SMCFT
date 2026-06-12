// Este archivo es el DTO de entrada para crear una notificación manual
// DTO = formulario de entrada — define qué datos debe mandar
// el cliente cuando quiere crear una notificación manualmente
// Si falta algún campo obligatorio → responde HTTP 400 automáticamente

package com.example.NotificationService.dto;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class NotificationDTO {

    // Título de la notificación
    @NotBlank(message = "El título es obligatorio")
    @Size(max = 200,
            message = "El título no puede tener más de 200 caracteres")
    private String titulo;

    // Mensaje detallado de la notificación
    @NotBlank(message = "El mensaje es obligatorio")
    @Size(max = 500,
            message = "El mensaje no puede tener más de 500 caracteres")
    private String mensaje;

    // Tipo de notificación según su contenido
    @NotBlank(message = "El tipo es obligatorio")
    @Pattern(
            regexp = "ALERTA_DEADLINE|ALERTA_URGENTE|ALERTA_VENCIDO|INFORMATIVA",
            message = "El tipo debe ser ALERTA_DEADLINE, ALERTA_URGENTE, " +
                    "ALERTA_VENCIDO o INFORMATIVA"
    )
    private String tipo;

    // ID de la alerta de deadline que originó esta notificación
    // Este campo es OPCIONAL → no tiene @NotNull ni @NotBlank
    private Long deadlineAlertId;

    // RUT del destinatario al que va dirigida la notificación
    // Si se proporciona, el sistema creará automáticamente un destinatario
    // Este campo es OPCIONAL — si no se manda, la notificación no tiene destinatario asignado
    private String destinatarioRut;
}