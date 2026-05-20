// Representa la tabla deadline_alerts en la base de datos
// Una alerta = aviso que se genera cuando un deadline está próximo a vencer
// Tiene una relación @ManyToOne con Deadline : Muchas alertas pertenecen a un solo deadline
package com.example.DeadlineService.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "deadline_alerts")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeadlineAlert {

    // Id de la alerta — se genera automáticamente en la BD
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // RELACIÓN @ManyToOne — muchas alertas pertenecen a un deadline
    @ManyToOne
    @JoinColumn(name = "deadline_id", nullable = false)
    @com.fasterxml.jackson.annotation.JsonBackReference
    private Deadline deadline;

    // Mensaje descriptivo de la alerta
    @Column(nullable = false, length = 500)
    private String mensaje;

    // Cuántos días le quedaban al deadline cuando se generó la alerta
    @Column(name = "dias_restantes", nullable = false)
    private Integer diasRestantes;

    // Tipo de alerta según la urgencia
    @Column(name = "tipo_alerta", nullable = false, length = 30)
    private String tipoAlerta;

    // Si la alerta ya fue enviada al Notification Service
    // Se inicializa en false porque toda alerta nueva está pendiente
    @Column(nullable = false)
    private Boolean enviada = false;

    // Fecha y hora en que se creó la alerta
    @Column(name = "created_at")
    private LocalDateTime createdAt;
}