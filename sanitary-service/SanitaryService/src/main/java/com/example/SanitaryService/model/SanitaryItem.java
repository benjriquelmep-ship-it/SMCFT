// Representa cada item revisado en una inspección sanitaria
// Tiene una relación @ManyToOne con Sanitary

package com.example.SanitaryService.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "sanitary_items")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SanitaryItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // RELACIÓN @ManyToOne — muchos items pertenecen a una inspección
    @ManyToOne
    @JoinColumn(name = "inspection_id", nullable = false)
    @com.fasterxml.jackson.annotation.JsonBackReference
    private Sanitary sanitary;

    // Descripción del item inspeccionado
    @Column(nullable = false, length = 200)
    private String descripcion;

    // Resultado de este item
    // "APROBADO", "RECHAZADO", "NO_APLICA", "PENDIENTE"
    @Column(name = "resultado_item", nullable = false, length = 30)
    private String resultadoItem;

    // Observaciones del item
    @Column(length = 300)
    private String observaciones;
}