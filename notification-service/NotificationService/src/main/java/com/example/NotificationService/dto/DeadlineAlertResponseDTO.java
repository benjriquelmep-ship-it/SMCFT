// Este archivo es el DTO de respuesta del Deadline Service
// Representa los datos que llegan cuando Notification Service
// consulta las alertas pendientes de envío

package com.example.NotificationService.dto;
import lombok.Data;
import java.time.LocalDateTime;
import io.swagger.v3.oas.annotations.media.Schema;

@Data
@Schema(description = "DTO que representa deadline alert")
public class DeadlineAlertResponseDTO {

    // Id de la alerta en Deadline Service
    // Notification Service lo usa para:
    // 1. Guardar el deadlineAlertId en la notificación creada
    // 2. Llamar PATCH /deadline-alerts/{id}/enviada para marcarla
    @Schema(description = "Id", example = "1")
    private Long id;

    // Mensaje descriptivo de la alerta
    // Notification Service usa este mensaje como cuerpo de la notificación
    @Schema(description = "Mensaje", example = "ejemplo")
    private String mensaje;

    // Cuántos días le quedaban al deadline cuando se generó la alerta
    @Schema(description = "Dias Restantes", example = "1")
    private Integer diasRestantes;

    // Tipo de alerta según la urgencia

    @Schema(description = "Tipo Alerta", example = "PARTICULAR")
    private String tipoAlerta;

    // Si la alerta ya fue enviada o no
    // Notification Service consulta solo las alertas con enviada = false
    // GET /api/v1/deadline-alerts/no-enviadas devuelve solo estas
    @Schema(description = "Enviada", example = "true")
    private Boolean enviada;

    // Fecha y hora en que se creó la alerta en Deadline Service
    // Solo informativo — no se usa directamente en la notificación
    @Schema(description = "Created At", example = "2024-01-15")
    private LocalDateTime createdAt;
}