// repository/AuditRepository.java
package com.example.AuditService.repository;

import com.example.AuditService.model.Audit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AuditRepository extends JpaRepository<Audit, Long> {

    // IGUALDAD BÁSICA
    // Spring genera: SELECT * FROM audits WHERE rut_auditor = ?
    List<Audit> findByRutAuditor(String rutAuditor);

    // Spring genera: SELECT * FROM audits WHERE tipo_auditoria = ?
    List<Audit> findByTipoAuditoria(String tipoAuditoria);

    // Spring genera: SELECT * FROM audits WHERE entidad = ?
    List<Audit> findByEntidad(String entidad);

    // POR ESTADO
    // Spring genera: SELECT * FROM audits WHERE estado = ?
    List<Audit> findByEstado(String estado);

    // AND
    // Auditorías de un auditor con estado específico
    // Spring genera: SELECT * FROM audits
    //                WHERE rut_auditor = ? AND estado = ?
    List<Audit> findByRutAuditorAndEstado(
            String rutAuditor, String estado);

    // Auditorías de un tipo con estado específico
    // Spring genera: SELECT * FROM audits
    //                WHERE tipo_auditoria = ? AND estado = ?
    List<Audit> findByTipoAuditoriaAndEstado(
            String tipoAuditoria, String estado);

    // Por entidad y ID del registro auditado
    // Spring genera: SELECT * FROM audits
    //                WHERE entidad = ? AND entidad_id = ?
    List<Audit> findByEntidadAndEntidadId(String entidad, Long entidadId);

    // BETWEEN
    // Auditorías en un rango de fechas
    // Spring genera: SELECT * FROM audits
    //                WHERE fecha_inicio BETWEEN ? AND ?
    List<Audit> findByFechaInicioBetween(
            LocalDateTime desde, LocalDateTime hasta);

    // CONTAINING + IGNORE CASE
    // Búsqueda parcial por descripción
    // Spring genera: SELECT * FROM audits
    //                WHERE LOWER(descripcion) LIKE LOWER('%texto%')
    List<Audit> findByDescripcionContainingIgnoreCase(String texto);

    // ORDENAMIENTO
    // Auditorías del más reciente al más antiguo
    // Spring genera: SELECT * FROM audits
    //                WHERE rut_auditor = ? ORDER BY fecha_inicio DESC
    List<Audit> findByRutAuditorOrderByFechaInicioDesc(String rutAuditor);

    // Auditorías en proceso ordenadas
    // Spring genera: SELECT * FROM audits
    //                WHERE estado = ? ORDER BY fecha_inicio ASC
    List<Audit> findByEstadoOrderByFechaInicioAsc(String estado);

    // TOP
    // Las últimas 10 auditorías
    // Spring genera: SELECT * FROM audits ORDER BY id DESC LIMIT 10
    List<Audit> findTop10ByOrderByIdDesc();

    // COUNT
    // Contar auditorías por estado
    // Spring genera: SELECT COUNT(*) FROM audits WHERE estado = ?
    long countByEstado(String estado);

    // Contar por tipo
    // Spring genera: SELECT COUNT(*) FROM audits WHERE tipo_auditoria = ?
    long countByTipoAuditoria(String tipoAuditoria);
}