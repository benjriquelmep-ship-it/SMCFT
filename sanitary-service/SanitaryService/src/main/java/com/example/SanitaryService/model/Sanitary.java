// Representa cada inspección sanitaria en la frontera
// Tiene una relación @OneToMany con SanitaryItem

package com.example.SanitaryService.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "sanitary_inspections")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Sanitary {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Patente del vehículo inspeccionado
    @Column(nullable = false, length = 10)
    private String patente;

    // RUT del conductor
    @Column(name = "rut_conductor", nullable = false, length = 12)
    private String rutConductor;

    // RUT del inspector sanitario
    @Column(name = "rut_inspector", nullable = false, length = 12)
    private String rutInspector;

    // Paso fronterizo
    @Column(name = "paso_fronterizo", nullable = false, length = 100)
    private String pasoFronterizo;

    // Fecha y hora de la inspección
    @Column(name = "fecha_inspeccion", nullable = false)
    private LocalDateTime fechaInspeccion;

    // Resultado de la inspección
    // "APROBADO", "RECHAZADO", "PENDIENTE"
    @Column(nullable = false, length = 30)
    private String resultado;

    // Observaciones del inspector
    @Column(length = 500)
    private String observaciones;

    // RELACIÓN @OneToMany — una inspección tiene muchos items
    // mappedBy = "sanitary" → SanitaryItem es el dueño
    // cascade = ALL → guardar/eliminar inspección afecta sus items
    // fetch = LAZY → los items se cargan solo cuando se necesitan
    @OneToMany(mappedBy = "sanitary",
            cascade = CascadeType.ALL,
            fetch = FetchType.LAZY)
    @com.fasterxml.jackson.annotation.JsonManagedReference
    private List<SanitaryItem> items;
}