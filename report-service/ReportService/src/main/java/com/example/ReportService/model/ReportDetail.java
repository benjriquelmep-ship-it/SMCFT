// model/ReportDetail.java
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

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // RELACIÓN @ManyToOne — muchos detalles pertenecen a un reporte
    @ManyToOne
    @JoinColumn(name = "report_id", nullable = false)
    @com.fasterxml.jackson.annotation.JsonBackReference
    private Report report;

    @Column(nullable = false, length = 300)
    private String descripcion;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal valor;

    @Column(nullable = false, length = 50)
    private String unidad;

    @Column(nullable = false, length = 100)
    private String categoria;
}