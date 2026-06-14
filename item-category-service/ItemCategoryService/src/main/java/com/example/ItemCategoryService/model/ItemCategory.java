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
@Schema(description = "Entidad del modelo físico que representa una categoría arancelaria, sección de equipaje o subpartida de control en frontera")
public class ItemCategory {

    // Clave primaria con auto incremento
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Identificador único (ID) autoincremental de la categoría arancelaria", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    // Nombre único de la categoría
    @Column(nullable = false, unique = true, length = 100)
    @Schema(
            description = "Nombre único descriptor de la categoría de equipaje o subpartida",
            example = "Electrónicos de Consumo Personal",
            maxLength = 100,
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String nombre;

    // Descripción detallada
    @Column(nullable = false, length = 500)
    @Schema(
            description = "Glosa extensa con los alcances técnicos, restricciones y normativas de la categoría",
            example = "Dispositivos móviles, computadores portátiles y accesorios tecnológicos sin fines comerciales directos.",
            maxLength = 500,
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String descripcion;

    // Si requiere declaración especial en aduanas
    @Column(name = "requiere_declaracion", nullable = false)
    @Schema(
            description = "Flag de control aduanero que indica si el tipo de mercancía obliga a un levantamiento de declaración jurada",
            example = "true",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private Boolean requiereDeclaracion = false;

    // Límite de valor en USD sin impuestos
    @Column(name = "limite_valor_usd", precision = 10, scale = 2)
    @Schema(
            description = "Monto de franquicia máxima permitida expresada en dólares americanos (USD) antes de aplicar gravámenes arancelarios",
            example = "500.00"
    )
    private BigDecimal limiteValorUsd;

    // Si la categoría está activa
    @Column(nullable = false)
    @Schema(
            description = "Flag de vigencia de la categoría (El Border Crossing Service solo admitirá clasificaciones activas)",
            example = "true",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private Boolean activo = true;

    // RELACIÓN @OneToMany — una categoría tiene muchos items específicos
    @OneToMany(mappedBy = "category",
            cascade = CascadeType.ALL,
            fetch = FetchType.LAZY)
    @com.fasterxml.jackson.annotation.JsonManagedReference
    @Schema(description = "Colección desagregada de artículos específicos indexados bajo esta subpartida arancelaria")
    private List<Item> items;
}