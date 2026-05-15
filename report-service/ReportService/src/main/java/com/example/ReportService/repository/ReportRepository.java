// repository/ReportRepository.java
package com.example.ReportService.repository;

import com.example.ReportService.model.Report;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ReportRepository extends JpaRepository<Report, Long> {

    // Spring genera: SELECT * FROM reports WHERE tipo_reporte = ?
    List<Report> findByTipoReporte(String tipoReporte);

    // Spring genera: SELECT * FROM reports WHERE generado_por = ?
    List<Report> findByGeneradoPor(String generadoPor);

    // Spring genera: SELECT * FROM reports WHERE estado = ?
    List<Report> findByEstado(String estado);

    // Spring genera: SELECT * FROM reports
    //                WHERE tipo_reporte = ? AND estado = ?
    List<Report> findByTipoReporteAndEstado(
            String tipoReporte, String estado);

    // Spring genera: SELECT * FROM reports
    //                WHERE generado_por = ? AND estado = ?
    List<Report> findByGeneradoPorAndEstado(
            String generadoPor, String estado);

    // Spring genera: SELECT * FROM reports
    //                WHERE LOWER(titulo) LIKE LOWER('%texto%')
    List<Report> findByTituloContainingIgnoreCase(String texto);

    // Spring genera: SELECT * FROM reports
    //                WHERE fecha_inicio BETWEEN ? AND ?
    List<Report> findByFechaInicioBetween(
            LocalDateTime desde, LocalDateTime hasta);

    // Spring genera: SELECT * FROM reports
    //                WHERE estado = ? ORDER BY id DESC
    List<Report> findByEstadoOrderByIdDesc(String estado);

    // Spring genera: SELECT * FROM reports
    //                WHERE generado_por = ? ORDER BY id DESC
    List<Report> findByGeneradoPorOrderByIdDesc(String generadoPor);

    // Spring genera: SELECT * FROM reports ORDER BY id DESC LIMIT 10
    List<Report> findTop10ByOrderByIdDesc();

    // Spring genera: SELECT COUNT(*) FROM reports WHERE estado = ?
    long countByEstado(String estado);

    // Spring genera: SELECT COUNT(*) FROM reports WHERE tipo_reporte = ?
    long countByTipoReporte(String tipoReporte);
}