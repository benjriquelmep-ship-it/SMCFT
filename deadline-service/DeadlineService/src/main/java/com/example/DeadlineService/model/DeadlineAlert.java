// model/DeadlineAlert.java
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

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // RELACIÓN @ManyToOne — muchas alertas pertenecen a un deadline
    @ManyToOne
    @JoinColumn(name = "deadline_id", nullable = false)
    @com.fasterxml.jackson.annotation.JsonBackReference
    private Deadline deadline;

    @Column(nullable = false, length = 500)
    private String mensaje;

    @Column(name = "dias_restantes", nullable = false)
    private Integer diasRestantes;

    // "AVISO", "URGENTE", "VENCIDO"
    @Column(name = "tipo_alerta", nullable = false, length = 30)
    private String tipoAlerta;

    @Column(nullable = false)
    private Boolean enviada = false;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
}