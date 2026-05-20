// Repositorio JPA para gestionar las consultas de la tabla "reports"
package com.example.ReportService.repository;

import com.example.ReportService.model.Report;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ReportRepository extends JpaRepository<Report, Long> {

    // Filtra los reportes por su categoría o tipo
    // Spring genera: SELECT * FROM reports WHERE tipo_reporte = ?
    List<Report> findByTipoReporte(String tipoReporte);

    // Filtra los reportes creados por el RUT de un usuario
    // Spring genera: SELECT * FROM reports WHERE generado_por = ?
    List<Report> findByGeneradoPor(String generadoPor);

    // Filtra los reportes según su estado actual
    // Spring genera: SELECT * FROM reports WHERE estado = ?
    List<Report> findByEstado(String estado);

    // Combina filtros: Tipo de reporte + Estado
    // Spring genera: SELECT * FROM reports
    //                WHERE tipo_reporte = ? AND estado = ?
    List<Report> findByTipoReporteAndEstado(
            String tipoReporte, String estado);

    // Combina filtros: RUT del creador + Estado
    // Spring genera: SELECT * FROM reports
    //                WHERE generado_por = ? AND estado = ?
    List<Report> findByGeneradoPorAndEstado(
            String generadoPor, String estado);

    // Busca reportes por coincidencia parcial en el título (case-insensitive)
    // Spring genera: SELECT * FROM reports
    //                WHERE LOWER(titulo) LIKE LOWER('%texto%')
    List<Report> findByTituloContainingIgnoreCase(String texto);

    // Obtiene reportes cuya fecha de inicio esté dentro de un rango específico
    // Spring genera: SELECT * FROM reports
    //                WHERE fecha_inicio BETWEEN ? AND ?
    List<Report> findByFechaInicioBetween(
            LocalDateTime desde, LocalDateTime hasta);

    // Obtiene reportes de un estado específico del más nuevo al más antiguo
    // Spring genera: SELECT * FROM reports
    //                WHERE estado = ? ORDER BY id DESC
    List<Report> findByEstadoOrderByIdDesc(String estado);

    // Obtiene reportes de un usuario específico del más nuevo al más antiguo
    // Spring genera: SELECT * FROM reports
    //                WHERE generado_por = ? ORDER BY id DESC
    List<Report> findByGeneradoPorOrderByIdDesc(String generadoPor);

    // Devuelve los últimos 10 reportes registrados en la base de datos
    // Spring genera: SELECT * FROM reports ORDER BY id DESC LIMIT 10
    List<Report> findTop10ByOrderByIdDesc();

    // Cuenta la cantidad total de reportes que tienen un estado específico
    // Spring genera: SELECT COUNT(*) FROM reports WHERE estado = ?
    long countByEstado(String estado);

    // Cuenta la cantidad total de reportes pertenecientes a un tipo específico
    // Spring genera: SELECT COUNT(*) FROM reports WHERE tipo_reporte = ?
    long countByTipoReporte(String tipoReporte);
}