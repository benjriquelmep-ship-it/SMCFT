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
@Schema(description = "Entidad del modelo físico que representa una línea, producto o lote específico analizado dentro de una fiscalización de salud pública")
public class SanitaryItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Clave primaria autoincremental del ítem inspeccionado", example = "1")
    private Long id;

    // RELACIÓN @ManyToOne — muchos items pertenecen a una inspección
    @ManyToOne
    @JoinColumn(name = "inspection_id", nullable = false)
    @com.fasterxml.jackson.annotation.JsonBackReference
    @Schema(description = "Instancia de la inspección fitosanitaria de cabecera a la cual pertenece este elemento", requiredMode = Schema.RequiredMode.REQUIRED)
    private Sanitary sanitary;

    // Descripción del item inspeccionado
    @Column(nullable = false, length = 200)
    @Schema(
            description = "Descripción detallada del tipo de cargamento, espécimen o subproducto revisado en frontera",
            example = "Cajones de manzanas e insumos hortofrutícolas a granel",
            maxLength = 200,
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String descripcion;

    // Resultado de este item
    // "APROBADO", "RECHAZADO", "NO_APLICA", "PENDIENTE"
    @Column(name = "resultado_item", nullable = false, length = 30)
    @Schema(
            description = "Dictamen técnico unitario aplicado al lote o elemento analizado",
            example = "APROBADO",
            maxLength = 30,
            allowableValues = {"APROBADO", "RECHAZADO", "NO_APLICA", "PENDIENTE"},
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String resultadoItem;

    // Observaciones del item
    @Column(length = 300)
    @Schema(
            description = "Notas técnicas específicas del inspector o hallazgos biológicos particulares del lote",
            example = "Sin presencia de plagas ni vectores biológicos activos. Certificación SAG al día.",
            maxLength = 300
    )
    private String observaciones;
}