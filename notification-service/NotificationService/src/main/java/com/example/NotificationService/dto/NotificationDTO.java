// Este archivo es el DTO de entrada para crear una notificación manual
// DTO = formulario de entrada — define qué datos debe mandar
// el cliente cuando quiere crear una notificación manualmente
// Si falta algún campo obligatorio → responde HTTP 400 automáticamente
package com.example.NotificationService.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import io.swagger.v3.oas.annotations.media.Schema;

@Data
@Schema(description = "Estructura de datos requerida para el alta, registro y posterior despacho de una nueva notificación en el sistema fronterizo")
public class NotificationDTO {

    // Título de la notificación
    @NotBlank(message = "El título es obligatorio")
    @Size(max = 200, message = "El título no puede tener más de 200 caracteres")
    @Schema(
            description = "Asunto o cabecera institucional del mensaje de la notificación",
            example = "Aviso de Vencimiento de Admisión Temporal",
            maxLength = 200,
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String titulo;

    // Mensaje detallado de la notificación
    @NotBlank(message = "El mensaje es obligatorio")
    @Size(max = 500, message = "El mensaje no puede tener más de 500 caracteres")
    @Schema(
            description = "Cuerpo detallado o glosa explicativa con las instrucciones y contenido de la alerta",
            example = "Estimado usuario, le informamos que el plazo máximo para la salida de su vehículo expira próximamente.",
            maxLength = 500,
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String mensaje;

    // Tipo de notificación según su contenido
    @NotBlank(message = "El tipo es obligatorio")
    @Pattern(
            regexp = "ALERTA_DEADLINE|ALERTA_URGENTE|ALERTA_VENCIDO|INFORMATIVA",
            message = "El tipo debe ser ALERTA_DEADLINE, ALERTA_URGENTE, ALERTA_VENCIDO o INFORMATIVA"
    )
    @Schema(
            description = "Naturaleza, nivel de criticidad o canal temático al que pertenece el mensaje",
            example = "ALERTA_DEADLINE",
            allowableValues = {"ALERTA_DEADLINE", "ALERTA_URGENTE", "ALERTA_VENCIDO", "INFORMATIVA"},
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String tipo;

    // ID de la alerta de deadline que originó esta notificación
    @Schema(
            description = "Identificador opcional (ID) de la alerta de plazo origen del Deadline Service para trazabilidad",
            example = "1"
    )
    private Long deadlineAlertId;

    // RUT del destinatario al que va dirigida la notificación
    @Schema(
            description = "RUN/RUT oficial del usuario, conductor o transportista al cual va asignado el mensaje",
            example = "12345678-9"
    )
    private String destinatarioRut;
}