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
@Schema(description = "Entidad que representa entry item")
public class EntryItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // RELACIÓN @ManyToOne — muchos items pertenecen a un ingreso
    // @JoinColumn define la FK entry_id
    // nullable = false → todo item debe tener un ingreso asociado
    @ManyToOne
    @JoinColumn(name = "entry_id", nullable = false)
    // @JsonBackReference evita ciclo infinito en serialización JSON
    @com.fasterxml.jackson.annotation.JsonBackReference
    private Entry entry;

    // Descripción del item declarado por el viajero
    @Column(nullable = false, length = 200)
    @Schema(description = "Descripcion", example = "ejemplo", maxLength = 200)
    private String descripcion;

    // Cantidad del item
    @Column(nullable = false)
    @Schema(description = "Cantidad", example = "1")
    private Integer cantidad;

    // Valor declarado en dólares
    @Column(name = "valor_usd", nullable = false,
            precision = 10, scale = 2)
    @Schema(description = "Valor Usd", example = "15000.50")
    private BigDecimal valorUsd;

    // Si el fiscalizador aprobó este item
    @Column(nullable = false)
    @Schema(description = "Aprobado", example = "true")
    private Boolean aprobado = false;
}