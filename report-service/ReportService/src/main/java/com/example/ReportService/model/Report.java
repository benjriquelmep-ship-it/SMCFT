// Entidad JPA que representa la tabla "reports" en la base de datos
package com.example.ReportService.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;
import io.swagger.v3.oas.annotations.media.Schema;

@Entity
@Table(name = "reports")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Entidad del modelo físico que representa la cabecera de un informe gerencial consolidado de auditoría fronteriza")
public class Report {

    // Clave primaria autoincremental
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(
            description = "Identificador único del reporte (Clave primaria autoincremental)",
            example = "1",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    private Long id;

    // Nombre identificativo del reporte
    @Column(nullable = false, length = 200)
    @Schema(
            description = "Título descriptivo u objeto del informe gerencial",
            example = "Informe Cuatrimestral de Tránsito Vehicular y Cargas",
            maxLength = 200,
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String titulo;

    // Tipo de reporte (CRUCE_FRONTERIZO, VEHICULOS, etc.)
    @Column(name = "tipo_reporte", nullable = false, length = 50)
    @Schema(
            description = "Módulo o área analítica origen del estudio estadístico",
            example = "CRUCE_FRONTERIZO",
            maxLength = 50,
            allowableValues = {"CRUCE_FRONTERIZO", "ADMISION_TEMPORAL", "VEHICULOS", "USUARIOS"},
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String tipoReporte;

    // Límite inicial del rango de datos consultados
    @Column(name = "fecha_inicio", nullable = false)
    @Schema(
            description = "Fecha y hora de inicio de la muestra cronológica analizada",
            example = "2026-01-01T00:00:00",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private LocalDateTime fechaInicio;

    // Límite final del rango de datos consultados
    @Column(name = "fecha_fin", nullable = false)
    @Schema(
            description = "Fecha y hora de término o corte de la muestra cronológica analizada",
            example = "2026-04-30T23:59:59",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private LocalDateTime fechaFin;

    // RUN del usuario que solicitó el reporte
    @Column(name = "generado_por", nullable = false, length = 12)
    @Schema(
            description = "RUN/RUT del administrador, jefe de aduana o fiscalizador que emitió el informe",
            example = "12345678-9",
            maxLength = 12,
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String generadoPor;

    // Estado del proceso (GENERANDO, COMPLETADO, ERROR)
    @Column(nullable = false, length = 30)
    @Schema(
            description = "Estado situacional del procesamiento o ciclo de vida del reporte",
            example = "COMPLETADO",
            maxLength = 30,
            allowableValues = {"GENERANDO", "COMPLETADO", "ERROR"},
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String estado;

    // Notas del sistema o justificación de fallas
    @Column(length = 500)
    @Schema(
            description = "Anotaciones de auditoría, observaciones al margen o trazas de error técnico en caso de falla",
            example = "Compilación completada sin anomalías. Datos consolidados desde pasarelas perimetrales.",
            maxLength = 500
    )
    private String observaciones;

    // Relación uno a muchos hacia las líneas de detalle del reporte
    @OneToMany(mappedBy = "report", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @com.fasterxml.jackson.annotation.JsonManagedReference
    @Schema(description = "Colección ordenada de desgloses métricos e indicadores estadísticos que componen el cuerpo del informe")
    private List<ReportDetail> detalles;
}