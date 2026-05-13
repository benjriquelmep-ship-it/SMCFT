// repository/AuditDetailRepository.java
package com.example.AuditService.repository;

import com.example.AuditService.model.AuditDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AuditDetailRepository
        extends JpaRepository<AuditDetail, Long> {

    // Todos los detalles de una auditoría
    // Spring genera: SELECT * FROM audit_details WHERE audit_id = ?
    List<AuditDetail> findByAuditId(Long auditId);

    // Por resultado
    // Spring genera: SELECT * FROM audit_details WHERE resultado = ?
    List<AuditDetail> findByResultado(String resultado);

    // Detalles de una auditoría por resultado
    // Spring genera: SELECT * FROM audit_details
    //                WHERE audit_id = ? AND resultado = ?
    List<AuditDetail> findByAuditIdAndResultado(
            Long auditId, String resultado);

    // Por usuario que realizó la acción
    // Spring genera: SELECT * FROM audit_details WHERE rut_usuario = ?
    List<AuditDetail> findByRutUsuario(String rutUsuario);

    // Acciones sospechosas de un usuario
    // Spring genera: SELECT * FROM audit_details
    //                WHERE rut_usuario = ? AND resultado = 'SOSPECHOSO'
    List<AuditDetail> findByRutUsuarioAndResultado(
            String rutUsuario, String resultado);

    // BETWEEN
    // Detalles en un rango de fechas
    // Spring genera: SELECT * FROM audit_details
    //                WHERE fecha_accion BETWEEN ? AND ?
    List<AuditDetail> findByFechaAccionBetween(
            LocalDateTime desde, LocalDateTime hasta);

    // CONTAINING + IGNORE CASE
    // Búsqueda parcial por acción
    // Spring genera: SELECT * FROM audit_details
    //                WHERE LOWER(accion) LIKE LOWER('%texto%')
    List<AuditDetail> findByAccionContainingIgnoreCase(String accion);

    // ORDENAMIENTO
    // Detalles de una auditoría del más reciente al más antiguo
    // Spring genera: SELECT * FROM audit_details
    //                WHERE audit_id = ? ORDER BY fecha_accion DESC
    List<AuditDetail> findByAuditIdOrderByFechaAccionDesc(Long auditId);

    // TOP
    // Los últimos 10 detalles registrados
    // Spring genera: SELECT * FROM audit_details ORDER BY id DESC LIMIT 10
    List<AuditDetail> findTop10ByOrderByIdDesc();

    // COUNT
    // Contar acciones sospechosas de un usuario
    long countByRutUsuarioAndResultado(String rutUsuario, String resultado);
}