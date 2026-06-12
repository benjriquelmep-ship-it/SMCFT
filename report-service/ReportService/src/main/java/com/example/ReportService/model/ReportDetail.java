// Entidad JPA que representa la tabla "report_details" en la base de datos
package com.example.ReportService.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.math.BigDecimal;
import io.swagger.v3.oas.annotations.media.Schema;

@Entity
@Table(name = "report_details")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Entidad que representa report detail")
public class ReportDetail {

    // Clave primaria autoincremental
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Id", example = "1")
    private Long id;

    // Relación muchos a uno: vincula este detalle con su reporte padre
    @ManyToOne
    @JoinColumn(name = "report_id", nullable = false)
    @com.fasterxml.jackson.annotation.JsonBackReference
    private Report report;

    // Texto explicativo de la línea de detalle
    @Column(nullable = false, length = 300)
    @Schema(description = "Descripcion", example = "ejemplo", maxLength = 300)
    private String descripcion;

    // Valor numérico o métrica del indicador (máx 10 dígitos, 2 decimales)
    @Column(nullable = false, precision = 10, scale = 2)
    @Schema(description = "Valor", example = "15000.50")
    private BigDecimal valor;

    // Unidad de medida asociada al valor (ej: "Vehículos", "Pesos")
    @Column(nullable = false, length = 50)
    @Schema(description = "Unidad", example = "ejemplo", maxLength = 50)
    private String unidad;

    // Clasificación o etiqueta del detalle (ej: "AUTORIZADOS")
    @Column(nullable = false, length = 100)
    @Schema(description = "Categoria", example = "ejemplo", maxLength = 100)
    private String categoria;
}