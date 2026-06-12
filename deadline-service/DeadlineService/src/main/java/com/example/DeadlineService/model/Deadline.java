package com.example.DeadlineService.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;
import io.swagger.v3.oas.annotations.media.Schema;

@Entity
@Table(name = "deadlines")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Entidad que representa deadline")
public class Deadline {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 10)
    @Schema(description = "Patente", example = "ABC-123", maxLength = 10)
    private String patente;

    @Column(name = "rut_conductor", nullable = false, length = 12)
    @Schema(description = "Rut Conductor", example = "12345678-9", maxLength = 12)
    private String rutConductor;

    // ID del ingreso en Entry Service
    @Column(name = "entry_id", nullable = false)
    @Schema(description = "Entry Id", example = "1")
    private Long entryId;

    @Column(name = "fecha_ingreso", nullable = false)
    @Schema(description = "Fecha Ingreso", example = "2024-01-15")
    private LocalDateTime fechaIngreso;

    @Column(name = "fecha_limite", nullable = false)
    @Schema(description = "Fecha Limite", example = "2024-01-15")
    private LocalDateTime fechaLimite;

    // "ADMISION_TEMPORAL" o "RETORNO_OBLIGATORIO"
    @Column(nullable = false, length = 50)
    @Schema(description = "Tipo", example = "PARTICULAR", maxLength = 50)
    private String tipo;

    // "ACTIVO", "VENCIDO", "CERRADO"
    @Column(nullable = false, length = 30)
    @Schema(description = "Estado", example = "ACTIVO", maxLength = 30)
    private String estado;

    @Column(length = 500)
    @Schema(description = "Observaciones", example = "Observación de ejemplo", maxLength = 500)
    private String observaciones;

    // RELACIÓN @OneToMany — un deadline tiene muchas alertas
    @OneToMany(mappedBy = "deadline",
            cascade = CascadeType.ALL,
            fetch = FetchType.LAZY)
    @com.fasterxml.jackson.annotation.JsonManagedReference
    private List<DeadlineAlert> alertas;
}