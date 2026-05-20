package com.example.NotificationService.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "notification_recipients")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificationRecipient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // RELACIÓN @ManyToOne — muchos destinatarios pertenecen
    // a una notificación
    @ManyToOne
    @JoinColumn(name = "notification_id", nullable = false)
    @com.fasterxml.jackson.annotation.JsonBackReference
    private Notification notification;

    @Column(name = "rut_destinatario", nullable = false, length = 12)
    private String rutDestinatario;

    @Column(nullable = false, length = 150)
    private String email;

    @Column(nullable = false, length = 100)
    private String nombre;

    @Column(nullable = false)
    private Boolean leida = false;

    @Column(name = "leida_at")
    private LocalDateTime leidaAt;
}