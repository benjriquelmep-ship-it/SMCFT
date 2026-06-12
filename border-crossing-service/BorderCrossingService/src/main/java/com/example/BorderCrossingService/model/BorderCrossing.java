// Representa cada salida de vehículo del país
// Tiene una relación @OneToMany con BorderCrossingItem

package com.example.BorderCrossingService.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;
import io.swagger.v3.oas.annotations.media.Schema;

@Entity
@Table(name = "border_crossings")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Entidad que representa border crossing")
public class BorderCrossing {

    // Clave primaria con auto incremento
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Id", example = "1")
    private Long id;

    // Patente del vehículo que sale
    // No FK directa — Vehicle Service maneja los vehículos
    @Column(nullable = false, length = 10)
    @Schema(description = "Patente", example = "ABC-123", maxLength = 10)
    private String patente;

    // RUT del conductor que realiza el cruce
    @Column(name = "rut_conductor", nullable = false, length = 12)
    @Schema(description = "Rut Conductor", example = "12345678-9", maxLength = 12)
    private String rutConductor;

    // País de destino del viaje
    @Column(name = "pais_destino", nullable = false, length = 100)
    @Schema(description = "Pais Destino", example = "ejemplo", maxLength = 100)
    private String paisDestino;

    // Paso fronterizo donde ocurre el cruce
    @Column(name = "paso_fronterizo", nullable = false, length = 100)
    @Schema(description = "Paso Fronterizo", example = "ejemplo", maxLength = 100)
    private String pasoFronterizo;

    // Fecha y hora exacta del cruce
    @Column(name = "fecha_cruce", nullable = false)
    @Schema(description = "Fecha Cruce", example = "2024-01-15")
    private LocalDateTime fechaCruce;

    // Estado del cruce — "PENDIENTE", "AUTORIZADO", "RECHAZADO"
    @Column(nullable = false, length = 30)
    @Schema(description = "Estado", example = "ACTIVO", maxLength = 30)
    private String estado;

    // RUT del fiscalizador que procesó el cruce
    @Column(name = "rut_fiscalizador", length = 12)
    @Schema(description = "Rut Fiscalizador", example = "12345678-9", maxLength = 12)
    private String rutFiscalizador;

    // Observaciones del fiscalizador
    @Column(length = 500)
    @Schema(description = "Observaciones", example = "Observación de ejemplo", maxLength = 500)
    private String observaciones;

    // RELACIÓN @OneToMany — un cruce tiene muchos items de equipaje
    // mappedBy = "borderCrossing" → BorderCrossingItem es el dueño
    // cascade = ALL → guardar/eliminar cruce afecta sus items
    // fetch = LAZY → los items se cargan solo cuando se necesitan
    @OneToMany(mappedBy = "borderCrossing",
            cascade = CascadeType.ALL,
            fetch = FetchType.LAZY)

    // @JsonManagedReference evita ciclo infinito en serialización JSON
    @com.fasterxml.jackson.annotation.JsonManagedReference
    private List<BorderCrossingItem> items;
}