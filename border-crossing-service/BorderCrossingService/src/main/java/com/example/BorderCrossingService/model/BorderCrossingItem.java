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
@Schema(description = "Entidad que representa border crossing item")
public class BorderCrossingItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // RELACIÓN @ManyToOne — muchos items pertenecen a un cruce
    // @JoinColumn define la FK border_crossing_id
    // nullable = false → todo item debe tener un cruce asociado
    @ManyToOne
    @JoinColumn(name = "border_crossing_id", nullable = false)
    // @JsonBackReference evita ciclo infinito en serialización JSON
    @com.fasterxml.jackson.annotation.JsonBackReference
    private BorderCrossing borderCrossing;

    // ID de la categoría del item — viene de Item Category Service
    // No FK directa — desacoplado de Item Category Service
    @Column(name = "categoria_id", nullable = false)
    @Schema(description = "Categoria Id", example = "1")
    private Long categoriaId;

    // Descripción del item declarado por el viajero
    @Column(nullable = false, length = 200)
    @Schema(description = "Descripcion", example = "ejemplo", maxLength = 200)
    private String descripcion;

    // Cantidad del item declarado
    @Column(nullable = false)
    @Schema(description = "Cantidad", example = "1")
    private Integer cantidad;

    // Valor declarado en dólares
    @Column(name = "valor_usd", nullable = false,
            precision = 10, scale = 2)
    @Schema(description = "Valor Usd", example = "15000.50")
    private BigDecimal valorUsd;

    // Si el fiscalizador aprobó este item
    // false por defecto — necesita revisión del fiscalizador
    @Column(nullable = false)
    @Schema(description = "Aprobado", example = "true")
    private Boolean aprobado = false;
}