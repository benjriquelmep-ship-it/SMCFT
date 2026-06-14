// Representa cada item declarado en un ingreso al país
// Tiene una relación @ManyToOne con Entry
package com.example.EntryService.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.math.BigDecimal;
import io.swagger.v3.oas.annotations.media.Schema;

@Entity
@Table(name = "entry_items")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Entidad del modelo físico que representa una línea de mercancía o artículo individual declarado en el ingreso")
public class EntryItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Identificador único (ID) de la línea de artículo declarada", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    // RELACIÓN @ManyToOne — muchos items pertenecen a un ingreso
    @ManyToOne
    @JoinColumn(name = "entry_id", nullable = false)
    @com.fasterxml.jackson.annotation.JsonBackReference
    @Schema(description = "Instancia de la cabecera de ingreso al país a la cual queda acoplado este artículo", requiredMode = Schema.RequiredMode.REQUIRED)
    private Entry entry;

    // Descripción del item declarado por el viajero
    @Column(nullable = false, length = 200)
    @Schema(
            description = "Glosa explícita o nombre comercial del bien declarado por el transportista",
            example = "Televisor LED de 55 pulgadas",
            maxLength = 200,
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String descripcion;

    // Cantidad del item
    @Column(nullable = false)
    @Schema(
            description = "Volumen o cantidad física de unidades del mismo bien ingresadas",
            example = "1",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private Integer cantidad;

    // Valor declarado en dólares
    @Column(name = "valor_usd", nullable = false, precision = 10, scale = 2)
    @Schema(
            description = "Valoración comercial estimada del bien expresada en dólares americanos (USD)",
            example = "450.00",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private BigDecimal valorUsd;

    // Si el fiscalizador aprobó este item
    @Column(nullable = false)
    @Schema(
            description = "Flag de control aduanero que indica si la línea de mercancía fue validada de forma conforme por el aforo técnico",
            example = "true",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private Boolean aprobado = false;
}