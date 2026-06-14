// Representa cada item de equipaje declarado en un cruce
// Tiene una relación @ManyToOne con BorderCrossing
package com.example.BorderCrossingService.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.math.BigDecimal;
import io.swagger.v3.oas.annotations.media.Schema;

@Entity
@Table(name = "border_crossing_items")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Entidad del modelo físico que representa una línea de mercancía o artículo individual declarado en el equipaje de salida")
public class BorderCrossingItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Identificador único (ID) autoincremental de la línea de equipaje", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    // RELACIÓN @ManyToOne — muchos items pertenecen a un cruce
    @ManyToOne
    @JoinColumn(name = "border_crossing_id", nullable = false)
    @com.fasterxml.jackson.annotation.JsonBackReference
    @Schema(description = "Instancia de la cabecera de cruce fronterizo a la cual queda vinculada este artículo", requiredMode = Schema.RequiredMode.REQUIRED)
    private BorderCrossing borderCrossing;

    // ID de la categoría del item — viene de Item Category Service
    @Column(name = "categoria_id", nullable = false)
    @Schema(
            description = "Identificador indexado de la categoría arancelaria de origen externa",
            example = "1",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private Long categoriaId;

    // Descripción del item declarado por el viajero
    @Column(nullable = false, length = 200)
    @Schema(
            description = "Glosa descriptiva o nombre comercial del bien declarado por el transportista",
            example = "Cámara fotográfica profesional",
            maxLength = 200,
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String descripcion;

    // Cantidad del item declarado
    @Column(nullable = false)
    @Schema(
            description = "Volumen o unidades físicas del mismo artículo declaradas en el andén",
            example = "1",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private Integer cantidad;

    // Valor declarado en dólares
    @Column(name = "valor_usd", nullable = false, precision = 10, scale = 2)
    @Schema(
            description = "Valoración comercial estimada del bien expresada en dólares americanos (USD)",
            example = "850.50",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private BigDecimal valorUsd;

    // Si el fiscalizador aprobó este item
    @Column(nullable = false)
    @Schema(
            description = "Flag de control aduanero que indica si la línea de mercancía fue validada de forma conforme por el aforo físico",
            example = "true",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private Boolean aprobado = false;
}