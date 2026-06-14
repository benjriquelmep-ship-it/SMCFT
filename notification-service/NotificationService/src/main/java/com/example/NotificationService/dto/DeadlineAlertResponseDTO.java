// Este archivo es el DTO de respuesta del Deadline Service
// Representa los datos que llegan cuando Notification Service
// consulta las alertas pendientes de envío
package com.example.NotificationService.dto;

import lombok.Data;
import java.time.LocalDateTime;
import io.swagger.v3.oas.annotations.media.Schema;

@Data
@Schema(description = "Payload de salida que importa las alertas de plazos e infracciones temporales pendientes de notificación desde el Deadline Service")
public class DeadlineAlertResponseDTO {

    // Id de la alerta en Deadline Service
    @Schema(
            description = "Identificador único (ID) de la alerta de plazo originada en el microservicio origen",
            example = "1"
    )
    private Long id;

    // Mensaje descriptivo de la alerta
    @Schema(
            description = "Mensaje explicativo o glosa del plazo (Útil como cuerpo principal para el despacho del correo electrónico)",
            example = "Alerta: Faltan menos de 48 horas para el vencimiento de la admisión temporal del vehículo."
    )
    private String mensaje;

    // Cuántos días le quedaban al deadline cuando se generó la alerta
    @Schema(
            description = "Cantidad de días calendario restantes antes del vencimiento formal del trámite o permiso fronterizo",
            example = "2"
    )
    private Integer diasRestantes;

    // Tipo de alerta según la urgencia
    @Schema(
            description = "Clasificación operativa de la alerta basada en el nivel de criticidad del vencimiento",
            example = "CRÍTICA"
    )
    private String tipoAlerta;

    // Si la alerta ya fue enviada o no
    @Schema(
            description = "Bandera de control lógico que determina si la alerta fue despachada y notificada con éxito al usuario",
            example = "false"
    )
    private Boolean enviada;

    // Fecha y hora en que se creó la alerta en Deadline Service
    @Schema(
            description = "Sello temporal crudo (Fecha y hora) de la creación original del evento de alerta en el origen",
            example = "2026-06-12T14:30:00"
    )
    private LocalDateTime createdAt;
}