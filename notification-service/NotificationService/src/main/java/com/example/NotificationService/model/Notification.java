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
@Schema(description = "Entidad que representa notification")
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    @Schema(description = "Titulo", example = "ejemplo", maxLength = 200)
    private String titulo;

    @Column(nullable = false, length = 500)
    @Schema(description = "Mensaje", example = "ejemplo", maxLength = 500)
    private String mensaje;

    // "ALERTA_DEADLINE", "ALERTA_URGENTE",
    // "ALERTA_VENCIDO", "INFORMATIVA"
    @Column(nullable = false, length = 50)
    @Schema(description = "Tipo", example = "PARTICULAR", maxLength = 50)
    private String tipo;

    // ID de la alerta de deadline que originó esta notificación
    @Column(name = "deadline_alert_id")
    @Schema(description = "Deadline Alert Id", example = "1")
    private Long deadlineAlertId;

    // "PENDIENTE", "ENVIADA", "ERROR"
    @Column(nullable = false, length = 30)
    @Schema(description = "Estado", example = "ACTIVO", maxLength = 30)
    private String estado;

    @Column(name = "created_at")
    @Schema(description = "Created At", example = "2024-01-15", maxLength = 30)
    private LocalDateTime createdAt;

    @Column(name = "enviada_at")
    @Schema(description = "Enviada At", example = "2024-01-15")
    private LocalDateTime enviadaAt;

    // RELACIÓN @OneToMany — una notificación tiene muchos destinatarios
    @OneToMany(mappedBy = "notification",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY)
    @com.fasterxml.jackson.annotation.JsonManagedReference
    private List<NotificationRecipient> destinatarios;
}