// Repository = la clase que habla directamente con la base de datos
package com.example.AuditService.repository;

import com.example.AuditService.model.AuditDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AuditDetailRepository
        extends JpaRepository<AuditDetail, Long> {

    // Devuelve todos los detalles que pertenecen a una auditoría
    List<AuditDetail> findByAuditId(Long auditId);

    // Devuelve todos los detalles con un resultado específico
    List<AuditDetail> findByResultado(String resultado);

    // Devuelve los detalles de una auditoría filtrados por resultado
    List<AuditDetail> findByAuditIdAndResultado(
            Long auditId, String resultado);

    // Devuelve todas las acciones realizadas por un usuario específico
    List<AuditDetail> findByRutUsuario(String rutUsuario);

    // Devuelve las acciones sospechosas de un usuario específico
    List<AuditDetail> findByRutUsuarioAndResultado(
            String rutUsuario, String resultado);

    // Devuelve los detalles registrados en un rango de fechas
    List<AuditDetail> findByFechaAccionBetween(
            LocalDateTime desde, LocalDateTime hasta);

    // Busca detalles cuya acción contenga el texto buscado
    List<AuditDetail> findByAccionContainingIgnoreCase(String accion);

    // Detalles de una auditoría del más reciente al más antiguo
    List<AuditDetail> findByAuditIdOrderByFechaAccionDesc(Long auditId);

    // Los últimos 10 detalles registrados
    List<AuditDetail> findTop10ByOrderByIdDesc();

    // Contar acciones sospechosas de un usuario
    long countByRutUsuarioAndResultado(String rutUsuario, String resultado);
}