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
@Schema(description = "Entidad del modelo físico que representa la cabecera y el registro general de un ingreso vehicular o flujo migratorio en frontera")
public class Entry {

    // Clave primaria con auto incremento
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Clave primaria autoincremental del registro de ingreso", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    // Patente del vehículo que ingresa
    @Column(nullable = false, length = 10)
    @Schema(
            description = "Placa patente única de identificación de la unidad vehicular que solicita acceso",
            example = "ABCD-12",
            maxLength = 10,
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String patente;

    // RUT del conductor que realiza el ingreso
    @Column(name = "rut_conductor", nullable = false, length = 12)
    @Schema(
            description = "RUN/RUT oficial o pasaporte del conductor a cargo del vehículo fiscalizado",
            example = "12345678-9",
            maxLength = 12,
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String rutConductor;

    // País de origen del viaje
    @Column(name = "pais_origen", nullable = false, length = 100)
    @Schema(
            description = "Nación soberana de origen o procedencia de la unidad vehicular controlada",
            example = "Argentina",
            maxLength = 100,
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String paisOrigen;

    // Paso fronterizo donde ocurre el ingreso
    @Column(name = "paso_fronterizo", nullable = false, length = 100)
    @Schema(
            description = "Nombre oficial del complejo aduanero de control perimetral donde se ejecuta el andén de revisión",
            example = "Los Libertadores",
            maxLength = 100,
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String pasoFronterizo;

    // Fecha y hora exacta del ingreso
    @Column(name = "fecha_ingreso", nullable = false)
    @Schema(
            description = "Sello de tiempo formal (Año-Mes-Día y Hora) en que se tramitó el acceso",
            example = "2026-06-12T23:30:00",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private LocalDateTime fechaIngreso;

    // Tipo de ingreso al país
    @Column(name = "tipo_ingreso", nullable = false, length = 30)
    @Schema(
            description = "Régimen o destinación aduanera aplicada al vehículo para autorizar su ingreso",
            example = "ADMISION_TEMPORAL",
            maxLength = 30,
            allowableValues = {"RETORNO", "ADMISION_TEMPORAL"},
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String tipoIngreso;

    // Estado del ingreso — "PENDIENTE", "AUTORIZADO", "RECHAZADO"
    @Column(nullable = false, length = 30)
    @Schema(
            description = "Situación resolutiva actual de la fiscalización del trámite de ingreso",
            example = "AUTORIZADO",
            maxLength = 30,
            allowableValues = {"PENDIENTE", "AUTORIZADO", "RECHAZADO"},
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String estado;

    // RUT del fiscalizador que procesó el ingreso
    @Column(name = "rut_fiscalizador", length = 12)
    @Schema(
            description = "RUN/RUT oficial del funcionario aduanero que valida y firma el ingreso en caseta",
            example = "9876543-2",
            maxLength = 12
    )
    private String rutFiscalizador;

    // Observaciones del fiscalizador
    @Column(length = 500)
    @Schema(
            description = "Anotaciones al margen, comentarios del aforo o justificaciones administrativas del dictamen",
            example = "Unidad vehicular autorizada conforme al régimen de admisión temporal. Equipaje sujeto a revisión ordinaria.",
            maxLength = 500
    )
    private String observaciones;

    // RELACIÓN @OneToMany — un ingreso tiene muchos items declarados
    @OneToMany(mappedBy = "entry",
            cascade = CascadeType.ALL,
            fetch = FetchType.LAZY)
    @com.fasterxml.jackson.annotation.JsonManagedReference
    @Schema(description = "Colección desagregada de artículos y bienes declarados y transportados en este ingreso")
    private List<EntryItem> items;
}