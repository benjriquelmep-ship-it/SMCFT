// Entidad JPA que representa la tabla "report_details" en la base de datos
package com.example.ReportService.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.math.BigDecimal;

@Entity
@Table(name = "report_details")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReportDetail {

    // Clave primaria autoincremental
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Relación muchos a uno: vincula este detalle con su reporte padre
    @ManyToOne
    @JoinColumn(name = "report_id", nullable = false)
    @com.fasterxml.jackson.annotation.JsonBackReference
    private Report report;

    // Texto explicativo de la línea de detalle
    @Column(nullable = false, length = 300)
    private String descripcion;

    // Valor numérico o métrica del indicador (máx 10 dígitos, 2 decimales)
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal valor;

    // Unidad de medida asociada al valor (ej: "Vehículos", "Pesos")
    @Column(nullable = false, length = 50)
    private String unidad;

    // Clasificación o etiqueta del detalle (ej: "AUTORIZADOS")
    @Column(nullable = false, length = 100)
    private String categoria;
}