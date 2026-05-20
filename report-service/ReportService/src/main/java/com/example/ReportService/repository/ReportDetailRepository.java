// Repositorio JPA para gestionar las consultas de la tabla "report_details"
package com.example.ReportService.repository;

import com.example.ReportService.model.ReportDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ReportDetailRepository
        extends JpaRepository<ReportDetail, Long> {

    // Filtra detalles por el ID del reporte padre
    // Spring genera: SELECT * FROM report_details WHERE report_id = ?
    List<ReportDetail> findByReportId(Long reportId);

    // Filtra detalles por su etiqueta o categoría
    // Spring genera: SELECT * FROM report_details WHERE categoria = ?
    List<ReportDetail> findByCategoria(String categoria);

    // Filtra detalles combinando reporte padre y categoría
    // Spring genera: SELECT * FROM report_details
    //                WHERE report_id = ? AND categoria = ?
    List<ReportDetail> findByReportIdAndCategoria(
            Long reportId, String categoria);

    // Busca detalles por coincidencia parcial en la descripción (case-insensitive)
    // Spring genera: SELECT * FROM report_details
    //                WHERE LOWER(descripcion) LIKE LOWER('%texto%')
    List<ReportDetail> findByDescripcionContainingIgnoreCase(
            String descripcion);

    // Obtiene los detalles de un reporte ordenados de mayor a menor valor
    // Spring genera: SELECT * FROM report_details
    //                WHERE report_id = ? ORDER BY valor DESC
    List<ReportDetail> findByReportIdOrderByValorDesc(Long reportId);

    // Devuelve los últimos 10 detalles insertados en el sistema
    // Spring genera: SELECT * FROM report_details ORDER BY id DESC LIMIT 10
    List<ReportDetail> findTop10ByOrderByIdDesc();
}