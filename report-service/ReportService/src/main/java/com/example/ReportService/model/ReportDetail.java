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
@Schema(description = "Entidad del modelo físico que representa una línea de desglose métrico o indicador KPI inyectado dentro de un reporte padre")
public class ReportDetail {

    // Clave primaria autoincremental
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Identificador único de la línea de detalle (Clave primaria)", example = "1")
    private Long id;

    // Relación muchos a uno: vincula este detalle con su reporte padre
    @ManyToOne
    @JoinColumn(name = "report_id", nullable = false)
    @com.fasterxml.jackson.annotation.JsonBackReference
    @Schema(description = "Instancia del reporte consolidador o cabecera padre al cual se acopla este registro métrico", requiredMode = Schema.RequiredMode.REQUIRED)
    private Report report;

    // Texto explicativo de la línea de detalle
    @Column(nullable = false, length = 300)
    @Schema(
            description = "Glosa explicativa o descripción del indicador o variable estadística calculada",
            example = "Total de vehículos de carga pesada autorizados para tránsito internacional",
            maxLength = 300,
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String descripcion;

    // Valor numérico o métrica del indicador (máx 10 dígitos, 2 decimales)
    @Column(nullable = false, precision = 10, scale = 2)
    @Schema(
            description = "Monto acumulado, valor cuantitativo o cálculo matemático final de la métrica",
            example = "1450.00",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private BigDecimal valor;

    // Unidad de medida asociada al valor (ej: "Vehículos", "Pesos")
    @Column(nullable = false, length = 50)
    @Schema(
            description = "Unidad analítica o métrica que califica al valor cuantitativo",
            example = "Vehículos",
            maxLength = 50,
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String unidad;

    // Clasificación o etiqueta del detalle (ej: "AUTORIZADOS")
    @Column(nullable = false, length = 100)
    @Schema(
            description = "Criterio contable de agrupación o categoría de la métrica",
            example = "AUTORIZADOS",
            maxLength = 100,
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String categoria;
}