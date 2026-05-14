// model/Deadline.java
package com.example.DeadlineService.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "deadlines")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Deadline {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 10)
    private String patente;

    @Column(name = "rut_conductor", nullable = false, length = 12)
    private String rutConductor;

    // ID del ingreso en Entry Service
    @Column(name = "entry_id", nullable = false)
    private Long entryId;

    @Column(name = "fecha_ingreso", nullable = false)
    private LocalDateTime fechaIngreso;

    @Column(name = "fecha_limite", nullable = false)
    private LocalDateTime fechaLimite;

    // "ADMISION_TEMPORAL" o "RETORNO_OBLIGATORIO"
    @Column(nullable = false, length = 50)
    private String tipo;

    // "ACTIVO", "VENCIDO", "CERRADO"
    @Column(nullable = false, length = 30)
    private String estado;

    @Column(length = 500)
    private String observaciones;

    // RELACIÓN @OneToMany — un deadline tiene muchas alertas
    @OneToMany(mappedBy = "deadline",
            cascade = CascadeType.ALL,
            fetch = FetchType.LAZY)
    @com.fasterxml.jackson.annotation.JsonManagedReference
    private List<DeadlineAlert> alertas;
}