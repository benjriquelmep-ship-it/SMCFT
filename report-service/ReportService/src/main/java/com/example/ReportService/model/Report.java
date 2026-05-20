// Entidad JPA que representa la tabla "reports" en la base de datos
package com.example.ReportService.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "reports")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Report {

    // Clave primaria autoincremental
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Nombre identificativo del reporte
    @Column(nullable = false, length = 200)
    private String titulo;

    // Tipo de reporte (CRUCE_FRONTERIZO, VEHICULOS, etc.)
    @Column(name = "tipo_reporte", nullable = false, length = 50)
    private String tipoReporte;

    // Límite inicial del rango de datos consultados
    @Column(name = "fecha_inicio", nullable = false)
    private LocalDateTime fechaInicio;

    // Límite final del rango de datos consultados
    @Column(name = "fecha_fin", nullable = false)
    private LocalDateTime fechaFin;

    // RUN del usuario que solicitó el reporte
    @Column(name = "generado_por", nullable = false, length = 12)
    private String generadoPor;

    // Estado del proceso (GENERANDO, COMPLETADO, ERROR)
    @Column(nullable = false, length = 30)
    private String estado;

    // Notas del sistema o justificación de fallas
    @Column(length = 500)
    private String observaciones;

    // Relación uno a muchos hacia las líneas de detalle del reporte
    @OneToMany(mappedBy = "report",
            cascade = CascadeType.ALL,
            fetch = FetchType.LAZY)
    @com.fasterxml.jackson.annotation.JsonManagedReference
    private List<ReportDetail> detalles;
}