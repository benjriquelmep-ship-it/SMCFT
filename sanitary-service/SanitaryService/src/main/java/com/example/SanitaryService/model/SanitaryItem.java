// Representa cada item revisado en una inspección sanitaria
// Tiene una relación @ManyToOne con Sanitary

package com.example.SanitaryService.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import io.swagger.v3.oas.annotations.media.Schema;

@Entity
@Table(name = "sanitary_items")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Entidad que representa sanitary item")
public class SanitaryItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // RELACIÓN @ManyToOne — muchos items pertenecen a una inspección
    @ManyToOne
    @JoinColumn(name = "inspection_id", nullable = false)
    @com.fasterxml.jackson.annotation.JsonBackReference
    private Sanitary sanitary;

    // Descripción del item inspeccionado
    @Column(nullable = false, length = 200)
    @Schema(description = "Descripcion", example = "ejemplo", maxLength = 200)
    private String descripcion;

    // Resultado de este item
    // "APROBADO", "RECHAZADO", "NO_APLICA", "PENDIENTE"
    @Column(name = "resultado_item", nullable = false, length = 30)
    @Schema(description = "Resultado Item", example = "ejemplo", maxLength = 30)
    private String resultadoItem;

    // Observaciones del item
    @Column(length = 300)
    @Schema(description = "Observaciones", example = "Observación de ejemplo", maxLength = 300)
    private String observaciones;
}