// Representa cada item declarado en un ingreso al país
// Tiene una relación @ManyToOne con Entry

package com.example.EntryService.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.math.BigDecimal;

@Entity
@Table(name = "entry_items")
@Data
@NoArgsConstructor
@AllArgsConstructor
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
    private String descripcion;

    // Cantidad del item
    @Column(nullable = false)
    private Integer cantidad;

    // Valor declarado en dólares
    @Column(name = "valor_usd", nullable = false,
            precision = 10, scale = 2)
    private BigDecimal valorUsd;

    // Si el fiscalizador aprobó este item
    @Column(nullable = false)
    private Boolean aprobado = false;
}