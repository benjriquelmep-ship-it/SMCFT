// model/Notification.java
package com.example.NotificationService.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "notifications")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String titulo;

    @Column(nullable = false, length = 500)
    private String mensaje;

    // "ALERTA_DEADLINE", "ALERTA_URGENTE",
    // "ALERTA_VENCIDO", "INFORMATIVA"
    @Column(nullable = false, length = 50)
    private String tipo;

    // ID de la alerta de deadline que originó esta notificación
    @Column(name = "deadline_alert_id")
    private Long deadlineAlertId;

    // "PENDIENTE", "ENVIADA", "ERROR"
    @Column(nullable = false, length = 30)
    private String estado;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "enviada_at")
    private LocalDateTime enviadaAt;

    // RELACIÓN @OneToMany — una notificación tiene muchos destinatarios
    @OneToMany(mappedBy = "notification",
            cascade = CascadeType.ALL,
            fetch = FetchType.LAZY)
    @com.fasterxml.jackson.annotation.JsonManagedReference
    private List<NotificationRecipient> destinatarios;
}