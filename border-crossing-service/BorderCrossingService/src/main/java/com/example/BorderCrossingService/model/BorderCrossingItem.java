// model/BorderCrossingItem.java
// Representa cada item de equipaje declarado en un cruce
// Tiene una relación @ManyToOne con BorderCrossing
// Cumple con IE 2.2.3 — relaciones entre entidades

package com.example.BorderCrossingService.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.math.BigDecimal;

@Entity
@Table(name = "border_crossing_items")
@Data
@NoArgsConstructor
@AllArgsConstructor
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
    private Long categoriaId;

    // Descripción del item declarado por el viajero
    @Column(nullable = false, length = 200)
    private String descripcion;

    // Cantidad del item declarado
    @Column(nullable = false)
    private Integer cantidad;

    // Valor declarado en dólares
    @Column(name = "valor_usd", nullable = false,
            precision = 10, scale = 2)
    private BigDecimal valorUsd;

    // Si el fiscalizador aprobó este item
    // false por defecto — necesita revisión del fiscalizador
    @Column(nullable = false)
    private Boolean aprobado = false;
}