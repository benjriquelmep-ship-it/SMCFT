package com.example.NotificationService.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;
import io.swagger.v3.oas.annotations.media.Schema;

@Entity
@Table(name = "notification_recipients")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Entidad del modelo físico que representa el canal de destino y bitácora de lectura de un usuario asignado a un mensaje")
public class NotificationRecipient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Identificador único (ID) del registro del destinatario", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    // RELACIÓN @ManyToOne — muchos destinatarios pertenecen a una notificación
    @ManyToOne
    @JoinColumn(name = "notification_id", nullable = false)
    @com.fasterxml.jackson.annotation.JsonIgnoreProperties({"destinatarios"})
    @Schema(description = "Instancia de la notificación de cabecera asociada a este registro de envío", requiredMode = Schema.RequiredMode.REQUIRED)
    private Notification notification;

    @Column(name = "rut_destinatario", nullable = false, length = 12)
    @Schema(
            description = "RUN/RUT de identidad legal del usuario receptor",
            example = "12345678-9",
            maxLength = 12,
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String rutDestinatario;

    @Column(nullable = false, length = 150)
    @Schema(
            description = "Dirección de correo electrónico principal del destinatario",
            example = "usuario@ejemplo.cl",
            maxLength = 150,
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String email;

    @Column(nullable = false, length = 100)
    @Schema(
            description = "Nombre completo o razón social del titular del destino",
            example = "Benjamin Alexis Riquelme Pozo",
            maxLength = 100,
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String nombre;

    @Column(nullable = false)
    @Schema(
            description = "Determina si el usuario ya abrió o acusó recibo de la notificación en su bandeja",
            example = "false",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private Boolean leida = false;

    @Column(name = "leida_at")
    @Schema(
            description = "Sello temporal exacto en el que el ciudadano marcó como leída la alerta",
            example = "2026-06-12T23:50:00"
    )
    private LocalDateTime leidaAt;
}