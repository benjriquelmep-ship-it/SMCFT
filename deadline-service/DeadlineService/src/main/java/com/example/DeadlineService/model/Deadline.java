package com.example.DeadlineService.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;
import io.swagger.v3.oas.annotations.media.Schema;

@Entity
@Table(name = "deadlines")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Entidad del modelo físico que representa el ciclo de vida y control cronológico de la permanencia legal de un vehículo")
public class Deadline {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Clave primaria autoincremental del registro de control temporal", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @Column(nullable = false, length = 10)
    @Schema(
            description = "Placa patente única de la unidad automotriz sujeta al límite temporal de estadía",
            example = "ABCD-12",
            maxLength = 10,
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String patente;

    @Column(name = "rut_conductor", nullable = false, length = 12)
    @Schema(
            description = "RUN/RUT oficial o pasaporte de la persona conductora responsable civil y aduanera del bien",
            example = "12345678-9",
            maxLength = 12,
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String rutConductor;

    // ID del ingreso en Entry Service
    @Column(name = "entry_id", nullable = false)
    @Schema(
            description = "Identificador correlativo del trámite de acceso origen provisto por el Entry Service",
            example = "1",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private Long entryId;

    @Column(name = "fecha_ingreso", nullable = false)
    @Schema(
            description = "Sello temporal cronológico exacto que marca el inicio del cómputo de días autorizados",
            example = "2026-06-12T23:30:00",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private LocalDateTime fechaIngreso;

    @Column(name = "fecha_limite", nullable = false)
    @Schema(
            description = "Fecha fatal y hora de vencimiento definitivo del permiso de internación del vehículo",
            example = "2026-09-12T23:30:00",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private LocalDateTime fechaLimite;

    // "ADMISION_TEMPORAL" o "RETORNO_OBLIGATORIO"
    @Column(nullable = false, length = 50)
    @Schema(
            description = "Régimen legal aduanero aplicable que estipula el origen del control temporal",
            example = "ADMISION_TEMPORAL",
            maxLength = 50,
            allowableValues = {"ADMISION_TEMPORAL", "RETORNO_OBLIGATORIO"},
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String tipo;

    // "ACTIVO", "VENCIDO", "CERRADO"
    @Column(nullable = false, length = 30)
    @Schema(
            description = "Estado situacional actual de la vigencia del plazo de control",
            example = "ACTIVO",
            maxLength = 30,
            allowableValues = {"ACTIVO", "VENCIDO", "CERRADO"},
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String estado;

    @Column(length = 500)
    @Schema(
            description = "Glosas marginales o anotaciones administrativas complementarias descritas por el sistema",
            example = "Vehículo ingresado bajo régimen de turismo extranjero. Prórrogas sujetas a control central.",
            maxLength = 500
    )
    private String observaciones;

    // RELACIÓN @OneToMany — un deadline tiene muchas alertas
    @OneToMany(mappedBy = "deadline",
            cascade = CascadeType.ALL,
            fetch = FetchType.LAZY)
    @com.fasterxml.jackson.annotation.JsonManagedReference
    @Schema(description = "Nómina desglosada de las alertas cronológicas gatilladas por proximidad o infracción de este plazo")
    private List<DeadlineAlert> alertas;
}