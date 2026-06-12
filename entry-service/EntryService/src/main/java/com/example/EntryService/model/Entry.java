// Representa cada ingreso de vehículo al país
// Tiene una relación @OneToMany con EntryItem

package com.example.EntryService.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;
import io.swagger.v3.oas.annotations.media.Schema;

@Entity
@Table(name = "entries")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Entidad que representa entry")
public class Entry {

    // Clave primaria con auto incremento
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Id", example = "1")
    private Long id;

    // Patente del vehículo que ingresa
    // No FK directa — Vehicle Service maneja los vehículos
    @Column(nullable = false, length = 10)
    @Schema(description = "Patente", example = "ABC-123", maxLength = 10)
    private String patente;

    // RUT del conductor que realiza el ingreso
    @Column(name = "rut_conductor", nullable = false, length = 12)
    @Schema(description = "Rut Conductor", example = "12345678-9", maxLength = 12)
    private String rutConductor;

    // País de origen del viaje
    @Column(name = "pais_origen", nullable = false, length = 100)
    @Schema(description = "Pais Origen", example = "ejemplo", maxLength = 100)
    private String paisOrigen;

    // Paso fronterizo donde ocurre el ingreso
    @Column(name = "paso_fronterizo", nullable = false, length = 100)
    @Schema(description = "Paso Fronterizo", example = "ejemplo", maxLength = 100)
    private String pasoFronterizo;

    // Fecha y hora exacta del ingreso
    @Column(name = "fecha_ingreso", nullable = false)
    @Schema(description = "Fecha Ingreso", example = "2024-01-15")
    private LocalDateTime fechaIngreso;

    // Tipo de ingreso al país
    // "RETORNO" → vehículo chileno que regresa
    // "ADMISION_TEMPORAL" → vehículo extranjero que entra temporalmente
    @Column(name = "tipo_ingreso", nullable = false, length = 30)
    @Schema(description = "Tipo Ingreso", example = "PARTICULAR", maxLength = 30)
    private String tipoIngreso;

    // Estado del ingreso — "PENDIENTE", "AUTORIZADO", "RECHAZADO"
    @Column(nullable = false, length = 30)
    @Schema(description = "Estado", example = "ACTIVO", maxLength = 30)
    private String estado;

    // RUT del fiscalizador que procesó el ingreso
    @Column(name = "rut_fiscalizador", length = 12)
    @Schema(description = "Rut Fiscalizador", example = "12345678-9", maxLength = 12)
    private String rutFiscalizador;

    // Observaciones del fiscalizador
    @Column(length = 500)
    @Schema(description = "Observaciones", example = "Observación de ejemplo", maxLength = 500)
    private String observaciones;

    // RELACIÓN @OneToMany — un ingreso tiene muchos items declarados
    @OneToMany(mappedBy = "entry",
            cascade = CascadeType.ALL,
            fetch = FetchType.LAZY)
    @com.fasterxml.jackson.annotation.JsonManagedReference
    private List<EntryItem> items;
}