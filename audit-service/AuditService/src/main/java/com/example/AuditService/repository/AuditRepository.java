package com.example.AuditService.repository;

import com.example.AuditService.model.Audit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AuditRepository extends JpaRepository<Audit, Long> {

    // Devuelve todas las auditorías de un auditor específico
    List<Audit> findByRutAuditor(String rutAuditor);

    // Devuelve todas las auditorías de un tipo específico
    List<Audit> findByTipoAuditoria(String tipoAuditoria);

    // Devuelve todas las auditorías de una entidad específica
    List<Audit> findByEntidad(String entidad);

    // Devuelve todas las auditorías con un estado específico
    List<Audit> findByEstado(String estado);

    // Devuelve auditorías de un auditor con un estado específico
    List<Audit> findByRutAuditorAndEstado(
            String rutAuditor, String estado);

    // Devuelve auditorías de un tipo con un estado específico
    List<Audit> findByTipoAuditoriaAndEstado(
            String tipoAuditoria, String estado);

    // Devuelve auditorías de una entidad y un registro específico
    List<Audit> findByEntidadAndEntidadId(String entidad, Long entidadId);

    // Devuelve auditorías iniciadas en un rango de fechas
    List<Audit> findByFechaInicioBetween(
            LocalDateTime desde, LocalDateTime hasta);

    // Busca auditorías cuya descripción contenga el texto buscado
    List<Audit> findByDescripcionContainingIgnoreCase(String texto);

    // Auditorías del más reciente al más antiguo
    List<Audit> findByRutAuditorOrderByFechaInicioDesc(String rutAuditor);

    // Devuelve auditorías en proceso ordenadas por fecha
    // de la más antigua a la más reciente
    List<Audit> findByEstadoOrderByFechaInicioAsc(String estado);

    // Las últimas 10 auditorías
    List<Audit> findTop10ByOrderByIdDesc();

    // Cuenta cuántas auditorías hay con un estado específico
    long countByEstado(String estado);

    // Cuenta cuántas auditorías hay de un tipo específico
    long countByTipoAuditoria(String tipoAuditoria);
}