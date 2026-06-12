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
@Schema(description = "Entidad que representa notification recipient")
public class NotificationRecipient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // RELACIÓN @ManyToOne — muchos destinatarios pertenecen
    // a una notificación
    @ManyToOne
    @JoinColumn(name = "notification_id", nullable = false)
    @com.fasterxml.jackson.annotation.JsonIgnoreProperties({"destinatarios"})
    private Notification notification;

    @Column(name = "rut_destinatario", nullable = false, length = 12)
    @Schema(description = "Rut Destinatario", example = "12345678-9", maxLength = 12)
    private String rutDestinatario;

    @Column(nullable = false, length = 150)
    @Schema(description = "Email", example = "usuario@ejemplo.cl", maxLength = 150)
    private String email;

    @Column(nullable = false, length = 100)
    @Schema(description = "Nombre", example = "Juan Pérez", maxLength = 100)
    private String nombre;

    @Column(nullable = false)
    @Schema(description = "Leida", example = "true", maxLength = 100)
    private Boolean leida = false;

    @Column(name = "leida_at")
    @Schema(description = "Leida At", example = "2024-01-15")
    private LocalDateTime leidaAt;
}