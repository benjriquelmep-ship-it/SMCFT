// model/ItemCategory.java
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

// @Entity le dice a JPA que esta clase representa una tabla en MySQL
@Entity
// @Table define el nombre exacto de la tabla
@Table(name = "item_categories")
// Lombok genera getters, setters, toString, equals y hashCode
@Data
// JPA necesita constructor vacío para crear instancias al leer de la BD
@NoArgsConstructor
// Permite crear objetos con todos los valores en una línea
@AllArgsConstructor
public class ItemCategory {

    // Clave primaria con auto incremento
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Nombre único de la categoría
    @Column(nullable = false, unique = true, length = 100)
    private String nombre;

    // Descripción detallada
    @Column(nullable = false, length = 500)
    private String descripcion;

    // Si requiere declaración especial en aduanas
    @Column(name = "requiere_declaracion", nullable = false)
    private Boolean requiereDeclaracion = false;

    // Límite de valor en USD sin impuestos
    // null = sin límite establecido
    @Column(name = "limite_valor_usd", precision = 10, scale = 2)
    private BigDecimal limiteValorUsd;

    // Si la categoría está activa
    // Border Crossing Service solo acepta categorías activas
    @Column(nullable = false)
    private Boolean activo = true;

    // RELACIÓN @OneToMany — una categoría tiene muchos items específicos
    // mappedBy = "category" → Item es el dueño de la relación
    // cascade = ALL → guardar/eliminar categoría afecta sus items
    // fetch = LAZY → los items se cargan solo cuando se necesitan
    @OneToMany(mappedBy = "category",
            cascade = CascadeType.ALL,
            fetch = FetchType.LAZY)
    // @JsonManagedReference evita ciclo infinito en serialización JSON
    @com.fasterxml.jackson.annotation.JsonManagedReference
    private List<Item> items;
}