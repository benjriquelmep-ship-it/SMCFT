// Representa cada inspección sanitaria en la frontera
// Tiene una relación @OneToMany con SanitaryItem
package com.example.SanitaryService.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;
import io.swagger.v3.oas.annotations.media.Schema;

@Entity
@Table(name = "sanitary_inspections")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Entidad del modelo físico que representa el registro general y cabecera de una inspección de control fitozoosanitario en pasos aduaneros")
public class Sanitary {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Identificador único (ID) autoincremental de la orden de inspección", example = "1")
    private Long id;

    // Patente del vehículo inspeccionado
    @Column(nullable = false, length = 10)
    @Schema(
            description = "Placa patente única de control del vehículo fiscalizado en el andén de revisión",
            example = "ABCD-12",
            maxLength = 10,
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String patente;

    // RUT del conductor
    @Column(name = "rut_conductor", nullable = false, length = 12)
    @Schema(
            description = "RUN/RUT de identidad oficial del transportista a cargo de la carga evaluada",
            example = "12345678-9",
            maxLength = 12,
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String rutConductor;

    // RUT del inspector sanitario
    @Column(name = "rut_inspector", nullable = false, length = 12)
    @Schema(
            description = "RUN/RUT del profesional o inspector del servicio de salud que visa la carga",
            example = "9876543-2",
            maxLength = 12,
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String rutInspector;

    // Paso fronterizo
    @Column(name = "paso_fronterizo", nullable = false, length = 100)
    @Schema(
            description = "Glosa identificatoria del complejo fronterizo donde se ejecuta la auditoría de sanidad",
            example = "Los Libertadores",
            maxLength = 100,
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String pasoFronterizo;

    // Fecha y hora de la inspección
    @Column(name = "fecha_inspeccion", nullable = false)
    @Schema(
            description = "Sello de tiempo (Año-Mes-Día y Hora) del inicio formal de la inspección",
            example = "2026-06-12T22:45:00",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private LocalDateTime fechaInspeccion;

    // Resultado de la inspección
    // "APROBADO", "RECHAZADO", "PENDIENTE"
    @Column(nullable = false, length = 30)
    @Schema(
            description = "Resolución general y definitiva de la inspección global (Afecta directamente la autorización de paso de la carga)",
            example = "PENDIENTE",
            maxLength = 30,
            allowableValues = {"APROBADO", "RECHAZADO", "PENDIENTE"},
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String resultado;

    // Observaciones del inspector
    @Column(length = 500)
    @Schema(
            description = "Comentarios conclusivos generales sobre el estado sanitario general de la carga y el transporte",
            example = "Inspección de cabecera iniciada. A la espera del desglose pormenorizado de los lotes de subproductos.",
            maxLength = 500
    )
    private String observaciones;

    // RELACIÓN @OneToMany — una inspección tiene muchos items
    @OneToMany(mappedBy = "sanitary",
            cascade = CascadeType.ALL,
            fetch = FetchType.LAZY)
    @com.fasterxml.jackson.annotation.JsonManagedReference
    @Schema(description = "Colección de ítems y productos individuales sujetos a revisión vinculados a esta cabecera")
    private List<SanitaryItem> items;
}