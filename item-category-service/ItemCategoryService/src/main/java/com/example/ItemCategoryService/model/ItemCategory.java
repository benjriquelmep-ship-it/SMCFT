// Representa las categorías de equipaje del sistema fronterizo
// Tiene una relación @OneToMany con Item
// Border Crossing Service consulta este microservicio para validar

package com.example.ItemCategoryService.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.math.BigDecimal;
import java.util.List;
import io.swagger.v3.oas.annotations.media.Schema;

@Entity
@Table(name = "item_categories")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Entidad que representa item category")
public class ItemCategory {

    // Clave primaria con auto incremento
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Id", example = "1")
    private Long id;

    // Nombre único de la categoría
    @Column(nullable = false, unique = true, length = 100)
    @Schema(description = "Nombre", example = "Juan Pérez", maxLength = 100)
    private String nombre;

    // Descripción detallada
    @Column(nullable = false, length = 500)
    @Schema(description = "Descripcion", example = "ejemplo", maxLength = 500)
    private String descripcion;

    // Si requiere declaración especial en aduanas
    @Column(name = "requiere_declaracion", nullable = false)
    @Schema(description = "Requiere Declaracion", example = "true")
    private Boolean requiereDeclaracion = false;

    // Límite de valor en USD sin impuestos
    // null = sin límite establecido
    @Column(name = "limite_valor_usd", precision = 10, scale = 2)
    @Schema(description = "Limite Valor Usd", example = "15000.50")
    private BigDecimal limiteValorUsd;

    // Si la categoría está activa
    // Border Crossing Service solo acepta categorías activas
    @Column(nullable = false)
    @Schema(description = "Activo", example = "true")
    private Boolean activo = true;

    // RELACIÓN @OneToMany — una categoría tiene muchos items específicos
    @OneToMany(mappedBy = "category",
            cascade = CascadeType.ALL,
            fetch = FetchType.LAZY)
    @com.fasterxml.jackson.annotation.JsonManagedReference
    private List<Item> items;
}