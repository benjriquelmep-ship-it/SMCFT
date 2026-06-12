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
@Schema(description = "Entidad que representa report")
public class Report {

    // Clave primaria autoincremental
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Id", example = "1")
    private Long id;

    // Nombre identificativo del reporte
    @Column(nullable = false, length = 200)
    @Schema(description = "Titulo", example = "ejemplo", maxLength = 200)
    private String titulo;

    // Tipo de reporte (CRUCE_FRONTERIZO, VEHICULOS, etc.)
    @Column(name = "tipo_reporte", nullable = false, length = 50)
    @Schema(description = "Tipo Reporte", example = "PARTICULAR", maxLength = 50)
    private String tipoReporte;

    // Límite inicial del rango de datos consultados
    @Column(name = "fecha_inicio", nullable = false)
    @Schema(description = "Fecha Inicio", example = "2024-01-15")
    private LocalDateTime fechaInicio;

    // Límite final del rango de datos consultados
    @Column(name = "fecha_fin", nullable = false)
    @Schema(description = "Fecha Fin", example = "2024-01-15")
    private LocalDateTime fechaFin;

    // RUN del usuario que solicitó el reporte
    @Column(name = "generado_por", nullable = false, length = 12)
    @Schema(description = "Generado Por", example = "ejemplo", maxLength = 12)
    private String generadoPor;

    // Estado del proceso (GENERANDO, COMPLETADO, ERROR)
    @Column(nullable = false, length = 30)
    @Schema(description = "Estado", example = "ACTIVO", maxLength = 30)
    private String estado;

    // Notas del sistema o justificación de fallas
    @Column(length = 500)
    @Schema(description = "Observaciones", example = "Observación de ejemplo", maxLength = 500)
    private String observaciones;

    // Relación uno a muchos hacia las líneas de detalle del reporte
    @OneToMany(mappedBy = "report",
            cascade = CascadeType.ALL,
            fetch = FetchType.LAZY)
    @com.fasterxml.jackson.annotation.JsonManagedReference
    private List<ReportDetail> detalles;
}