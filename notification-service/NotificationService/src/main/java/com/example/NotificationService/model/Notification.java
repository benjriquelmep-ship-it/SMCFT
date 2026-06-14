package com.example.NotificationService.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;
import io.swagger.v3.oas.annotations.media.Schema;

@Entity
@Table(name = "notifications")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Entidad del modelo físico que representa la cabecera de un mensaje o alerta despachada por el sistema")
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Clave primaria autoincremental de la notificación", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @Column(nullable = false, length = 200)
    @Schema(
            description = "Asunto o título institucional de la notificación",
            example = "Aviso de Vencimiento de Admisión Temporal",
            maxLength = 200,
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String titulo;

    @Column(nullable = false, length = 500)
    @Schema(
            description = "Cuerpo detallado o contenido completo del mensaje enviado",
            example = "Se le informa que el plazo de permanencia para su vehículo expira en 48 horas.",
            maxLength = 500,
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String mensaje;

    // "ALERTA_DEADLINE", "ALERTA_URGENTE", "ALERTA_VENCIDO", "INFORMATIVA"
    @Column(nullable = false, length = 50)
    @Schema(
            description = "Categoría o nivel de criticidad del mensaje",
            example = "ALERTA_DEADLINE",
            maxLength = 50,
            allowableValues = {"ALERTA_DEADLINE", "ALERTA_URGENTE", "ALERTA_VENCIDO", "INFORMATIVA"},
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String tipo;

    // ID de la alerta de deadline que originó esta notificación
    @Column(name = "deadline_alert_id")
    @Schema(
            description = "Identificador único de la alerta de plazo origen en el Deadline Service",
            example = "1"
    )
    private Long deadlineAlertId;

    // "PENDIENTE", "ENVIADA", "ERROR"
    @Column(nullable = false, length = 30)
    @Schema(
            description = "Estado situacional del despacho de la notificación",
            example = "PENDIENTE",
            maxLength = 30,
            allowableValues = {"PENDIENTE", "ENVIADA", "ERROR"},
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String estado;

    @Column(name = "created_at")
    @Schema(
            description = "Fecha y hora de registro de la notificación en la plataforma",
            example = "2026-06-12T23:45:00",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private LocalDateTime createdAt;

    @Column(name = "enviada_at")
    @Schema(
            description = "Sello temporal exacto en el que se confirmó el despacho del mensaje",
            example = "2026-06-12T23:46:12"
    )
    private LocalDateTime enviadaAt;

    // RELACIÓN @OneToMany — una notificación tiene muchos destinatarios
    @OneToMany(mappedBy = "notification",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY)
    @com.fasterxml.jackson.annotation.JsonManagedReference
    @Schema(description = "Colección ordenada de usuarios y canales de destino asignados a este mensaje")
    private List<NotificationRecipient> destinatarios;
}