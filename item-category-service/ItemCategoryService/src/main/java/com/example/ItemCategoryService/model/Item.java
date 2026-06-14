// Representa cada item específico dentro de una categoría
// Tiene una relación @ManyToOne con ItemCategory
package com.example.ItemCategoryService.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import io.swagger.v3.oas.annotations.media.Schema;

@Entity
@Table(name = "items")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Entidad del modelo físico que representa un artículo, bien o mercancía específica indexada en el catálogo aduanero")
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Clave primaria autoincremental del artículo", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    // RELACIÓN @ManyToOne — muchos items pertenecen a una categoría
    @ManyToOne
    @JoinColumn(name = "categoria_id", nullable = false)
    @com.fasterxml.jackson.annotation.JsonBackReference
    @Schema(description = "Instancia de la categoría arancelaria de cabecera a la cual pertenece este artículo", requiredMode = Schema.RequiredMode.REQUIRED)
    private ItemCategory category;

    // Nombre del item específico
    @Column(nullable = false, length = 100)
    @Schema(
            description = "Denominación comercial o nombre técnico del artículo",
            example = "Notebook Corporativo",
            maxLength = 100,
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String nombre;

    // Descripción del item
    @Column(nullable = false, length = 300)
    @Schema(
            description = "Especificaciones técnicas básicas o características distintivas del bien",
            example = "Pantalla de 14 pulgadas, procesador i7, 16GB RAM para uso personal.",
            maxLength = 300,
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String descripcion;

    // Unidad de medida
    @Column(nullable = false, length = 30)
    @Schema(
            description = "Unidad física de medida o empaque para cuantificar las existencias en control",
            example = "Unidades",
            maxLength = 30,
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String unidad;

    // Si el item está activo en el sistema
    @Column(nullable = false)
    @Schema(
            description = "Estado de vigencia lógica del artículo para su selección en los andenes de revisión",
            example = "true",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private Boolean activo = true;
}