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
@Schema(description = "Entidad del modelo físico que representa la cabecera y el registro general de un tránsito o cruce de salida en frontera")
public class BorderCrossing {

    // Clave primaria con auto incremento
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Clave primaria autoincremental de la orden de cruce fronterizo", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    // Patente del vehículo que sale
    @Column(nullable = false, length = 10)
    @Schema(
            description = "Placa patente única identificatoria de la unidad vehicular sujeta al control de salida",
            example = "ABCD-12",
            maxLength = 10,
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String patente;

    // RUT del conductor que realiza el cruce
    @Column(name = "rut_conductor", nullable = false, length = 12)
    @Schema(
            description = "RUN/RUT de identidad legal o pasaporte del conductor titular del tránsito vehicular",
            example = "12345678-9",
            maxLength = 12,
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String rutConductor;

    // País de destino del viaje
    @Column(name = "pais_destino", nullable = false, length = 100)
    @Schema(
            description = "Nación soberana hacia donde se dirige y destina de forma internacional la unidad vehicular",
            example = "Argentina",
            maxLength = 100,
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String paisDestino;

    // Paso fronterizo donde ocurre el cruce
    @Column(name = "paso_fronterizo", nullable = false, length = 100)
    @Schema(
            description = "Nombre oficial del complejo aduanero adscrito al perímetro donde se realiza el andén",
            example = "Los Libertadores",
            maxLength = 100,
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String pasoFronterizo;

    // Fecha y hora exacta del cruce
    @Column(name = "fecha_cruce", nullable = false)
    @Schema(
            description = "Sello temporal cronológico formal en el que se procesa la solicitud de cruce",
            example = "2026-06-12T23:30:00",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private LocalDateTime fechaCruce;

    // Estado del cruce — "PENDIENTE", "AUTORIZADO", "RECHAZADO"
    @Column(nullable = false, length = 30)
    @Schema(
            description = "Situación resolutiva actual de la fiscalización del trámite de salida",
            example = "PENDIENTE",
            maxLength = 30,
            allowableValues = {"PENDIENTE", "AUTORIZADO", "RECHAZADO"},
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String estado;

    // RUT del fiscalizador que procesó el cruce
    @Column(name = "rut_fiscalizador", length = 12)
    @Schema(
            description = "RUN/RUT oficial del funcionario aduanero que visa e inyecta la firma digital a la salida",
            example = "9876543-2",
            maxLength = 12
    )
    private String rutFiscalizador;

    // Observaciones del fiscalizador
    @Column(length = 500)
    @Schema(
            description = "Comentarios adicionales, glosas de aforo o justificaciones institucionales del dictamen en andén",
            example = "Cruce autorizado conforme. Documentación aduanera vehicular en regla y visada.",
            maxLength = 500
    )
    private String observaciones;

    // RELACIÓN @OneToMany — un cruce tiene muchos items de equipaje
    @OneToMany(mappedBy = "borderCrossing",
            cascade = CascadeType.ALL,
            fetch = FetchType.LAZY)
    @com.fasterxml.jackson.annotation.JsonManagedReference
    @Schema(description = "Colección desagregada de las líneas de artículos y equipajes declarados para este cruce")
    private List<BorderCrossingItem> items;
}