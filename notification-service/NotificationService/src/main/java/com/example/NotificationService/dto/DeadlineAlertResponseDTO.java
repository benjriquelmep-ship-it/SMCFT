// Este archivo es el DTO de respuesta del Deadline Service
// Representa los datos que llegan cuando Notification Service
// consulta las alertas pendientes de envío

package com.example.NotificationService.dto;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class DeadlineAlertResponseDTO {

    // Id de la alerta en Deadline Service
    // Notification Service lo usa para:
    // 1. Guardar el deadlineAlertId en la notificación creada
    // 2. Llamar PATCH /deadline-alerts/{id}/enviada para marcarla
    private Long id;

    // Mensaje descriptivo de la alerta
    // Notification Service usa este mensaje como cuerpo de la notificación
    private String mensaje;

    // Cuántos días le quedaban al deadline cuando se generó la alerta
    private Integer diasRestantes;

    // Tipo de alerta según la urgencia

    private String tipoAlerta;

    // Si la alerta ya fue enviada o no
    // Notification Service consulta solo las alertas con enviada = false
    // GET /api/v1/deadline-alerts/no-enviadas devuelve solo estas
    private Boolean enviada;

    // Fecha y hora en que se creó la alerta en Deadline Service
    // Solo informativo — no se usa directamente en la notificación
    private LocalDateTime createdAt;
}