// Representa cada inspección sanitaria en la frontera
// Tiene una relación @OneToMany con SanitaryItem

package com.example.SanitaryService.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;
import io.swagger.v3.oas.annotations.media.Schema;

@Entity
@Table(name = "sanitary_inspections")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Entidad que representa sanitary")
public class Sanitary {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Patente del vehículo inspeccionado
    @Column(nullable = false, length = 10)
    @Schema(description = "Patente", example = "ABC-123", maxLength = 10)
    private String patente;

    // RUT del conductor
    @Column(name = "rut_conductor", nullable = false, length = 12)
    @Schema(description = "Rut Conductor", example = "12345678-9", maxLength = 12)
    private String rutConductor;

    // RUT del inspector sanitario
    @Column(name = "rut_inspector", nullable = false, length = 12)
    @Schema(description = "Rut Inspector", example = "12345678-9", maxLength = 12)
    private String rutInspector;

    // Paso fronterizo
    @Column(name = "paso_fronterizo", nullable = false, length = 100)
    @Schema(description = "Paso Fronterizo", example = "ejemplo", maxLength = 100)
    private String pasoFronterizo;

    // Fecha y hora de la inspección
    @Column(name = "fecha_inspeccion", nullable = false)
    @Schema(description = "Fecha Inspeccion", example = "2024-01-15")
    private LocalDateTime fechaInspeccion;

    // Resultado de la inspección
    // "APROBADO", "RECHAZADO", "PENDIENTE"
    @Column(nullable = false, length = 30)
    @Schema(description = "Resultado", example = "ejemplo", maxLength = 30)
    private String resultado;

    // Observaciones del inspector
    @Column(length = 500)
    @Schema(description = "Observaciones", example = "Observación de ejemplo", maxLength = 500)
    private String observaciones;

    // RELACIÓN @OneToMany — una inspección tiene muchos items
    @OneToMany(mappedBy = "sanitary",
            cascade = CascadeType.ALL,
            fetch = FetchType.LAZY)
    @com.fasterxml.jackson.annotation.JsonManagedReference
    private List<SanitaryItem> items;
}