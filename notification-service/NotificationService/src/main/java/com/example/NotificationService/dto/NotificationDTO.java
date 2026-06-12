// Este archivo es el DTO de entrada para crear una notificación manual
// DTO = formulario de entrada — define qué datos debe mandar
// el cliente cuando quiere crear una notificación manualmente
// Si falta algún campo obligatorio → responde HTTP 400 automáticamente

package com.example.NotificationService.dto;
import jakarta.validation.constraints.*;
import lombok.Data;
import io.swagger.v3.oas.annotations.media.Schema;

@Data
@Schema(description = "DTO que representa notification")
public class NotificationDTO {

    // Título de la notificación
    @NotBlank(message = "El título es obligatorio")
    @Size(max = 200,
            message = "El título no puede tener más de 200 caracteres")
    @Schema(description = "Titulo", example = "ejemplo", maxLength = 200)
    private String titulo;

    // Mensaje detallado de la notificación
    @NotBlank(message = "El mensaje es obligatorio")
    @Size(max = 500,
            message = "El mensaje no puede tener más de 500 caracteres")
    @Schema(description = "Mensaje", example = "ejemplo", maxLength = 500)
    private String mensaje;

    // Tipo de notificación según su contenido
    @NotBlank(message = "El tipo es obligatorio")
    @Pattern(
            regexp = "ALERTA_DEADLINE|ALERTA_URGENTE|ALERTA_VENCIDO|INFORMATIVA",
            message = "El tipo debe ser ALERTA_DEADLINE, ALERTA_URGENTE, " +
                    "ALERTA_VENCIDO o INFORMATIVA"
    )
    @Schema(description = "Tipo", example = "PARTICULAR")
    private String tipo;

    // ID de la alerta de deadline que originó esta notificación
    // Este campo es OPCIONAL → no tiene @NotNull ni @NotBlank
    @Schema(description = "Deadline Alert Id", example = "1")
    private Long deadlineAlertId;

    // RUT del destinatario al que va dirigida la notificación
    // Si se proporciona, el sistema creará automáticamente un destinatario
    // Este campo es OPCIONAL — si no se manda, la notificación no tiene destinatario asignado
    @Schema(description = "Destinatario Rut", example = "12345678-9")
    private String destinatarioRut;
}