// Representa cada item específico dentro de una categoría
// Tiene una relación @ManyToOne con ItemCategory

package com.example.ItemCategoryService.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "items")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // RELACIÓN @ManyToOne — muchos items pertenecen a una categoría
    // @JoinColumn define la FK categoria_id
    // nullable = false → todo item debe tener una categoría
    @ManyToOne
    @JoinColumn(name = "categoria_id", nullable = false)
    // @JsonBackReference evita ciclo infinito en serialización JSON
    @com.fasterxml.jackson.annotation.JsonBackReference
    private ItemCategory category;

    // Nombre del item específico
    @Column(nullable = false, length = 100)
    private String nombre;

    // Descripción del item
    @Column(nullable = false, length = 300)
    private String descripcion;

    // Unidad de medida
    @Column(nullable = false, length = 30)
    private String unidad;

    // Si el item está activo en el sistema
    @Column(nullable = false)
    private Boolean activo = true;
}