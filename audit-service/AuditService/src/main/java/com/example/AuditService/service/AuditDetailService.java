// service/AuditDetailService.java
package com.example.AuditService.service;

import com.example.AuditService.dto.AuditDetailDTO;
import com.example.AuditService.model.Audit;
import com.example.AuditService.model.AuditDetail;
import com.example.AuditService.repository.AuditDetailRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AuditDetailService {

    private static final Logger log =
            LoggerFactory.getLogger(AuditDetailService.class);

    private final AuditDetailRepository detailRepository;
    private final AuditService auditService;

    // -------------------------------------------------------
    // CRUD BÁSICO
    // -------------------------------------------------------

    public List<AuditDetail> obtenerTodos() {
        log.info("Obteniendo todos los detalles de auditoría");
        return detailRepository.findAll();
    }

    public AuditDetail obtenerPorId(Long id) {
        log.info("Buscando detalle con id: {}", id);
        return detailRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Detalle con id {} no encontrado", id);
                    return new RuntimeException(
                            "Detalle no encontrado con id: " + id);
                });
    }

    // Registrar detalle de auditoría
    public AuditDetail registrar(AuditDetailDTO dto) {
        log.info("Registrando detalle para auditoría: {}",
                dto.getAuditId());

        // REGLA DE NEGOCIO 1: la auditoría debe existir
        Audit auditoria = auditService.obtenerPorId(dto.getAuditId());

        // REGLA DE NEGOCIO 2: solo se registran detalles en
        // auditorías EN_PROCESO
        if (!auditoria.getEstado().equals("EN_PROCESO")) {
            log.warn("No se pueden registrar detalles — estado: {}",
                    auditoria.getEstado());
            throw new RuntimeException(
                    "Solo se pueden registrar detalles en auditorías EN_PROCESO");
        }

        // Mapeo DTO → Entidad
        AuditDetail detalle = new AuditDetail();
        detalle.setAudit(auditoria);
        detalle.setAccion(dto.getAccion());
        detalle.setDescripcion(dto.getDescripcion());
        detalle.setRutUsuario(dto.getRutUsuario());
        detalle.setResultado(dto.getResultado());
        detalle.setIpAddress(dto.getIpAddress());
        detalle.setFechaAccion(dto.getFechaAccion());

        AuditDetail guardado = detailRepository.save(detalle);
        log.info("Detalle de auditoría registrado con id: {}",
                guardado.getId());
        return guardado;
    }

    // Eliminar detalle
    public void eliminar(Long id) {
        log.info("Eliminando detalle con id: {}", id);
        if (!detailRepository.existsById(id)) {
            log.warn("Detalle con id {} no encontrado", id);
            throw new RuntimeException(
                    "Detalle no encontrado con id: " + id);
        }
        detailRepository.deleteById(id);
        log.info("Detalle {} eliminado correctamente", id);
    }

    // -------------------------------------------------------
    // CONSULTAS DERIVADAS
    // -------------------------------------------------------

    public List<AuditDetail> obtenerPorAuditoria(Long auditId) {
        log.info("Obteniendo detalles de auditoría: {}", auditId);
        return detailRepository.findByAuditId(auditId);
    }

    public List<AuditDetail> obtenerPorResultado(String resultado) {
        log.info("Obteniendo detalles con resultado: {}", resultado);
        return detailRepository.findByResultado(resultado);
    }

    public List<AuditDetail> obtenerPorAuditoriaYResultado(
            Long auditId, String resultado) {
        log.info("Obteniendo detalles de {} con resultado {}",
                auditId, resultado);
        return detailRepository.findByAuditIdAndResultado(
                auditId, resultado);
    }

    public List<AuditDetail> obtenerPorUsuario(String rutUsuario) {
        log.info("Obteniendo detalles del usuario: {}", rutUsuario);
        return detailRepository.findByRutUsuario(rutUsuario);
    }

    public List<AuditDetail> obtenerSospechososPorUsuario(
            String rutUsuario) {
        log.info("Obteniendo acciones sospechosas de: {}", rutUsuario);
        return detailRepository.findByRutUsuarioAndResultado(
                rutUsuario, "SOSPECHOSO");
    }

    public List<AuditDetail> obtenerPorRangoFechas(
            LocalDateTime desde, LocalDateTime hasta) {
        log.info("Obteniendo detalles entre {} y {}", desde, hasta);
        return detailRepository.findByFechaAccionBetween(desde, hasta);
    }

    public List<AuditDetail> buscarPorAccion(String accion) {
        log.info("Buscando detalles con acción: {}", accion);
        return detailRepository.findByAccionContainingIgnoreCase(accion);
    }

    public List<AuditDetail> obtenerPorAuditoriaOrdenados(Long auditId) {
        log.info("Obteniendo detalles de {} ordenados", auditId);
        return detailRepository
                .findByAuditIdOrderByFechaAccionDesc(auditId);
    }

    public List<AuditDetail> obtenerUltimosDetalles() {
        log.info("Obteniendo los últimos 10 detalles");
        return detailRepository.findTop10ByOrderByIdDesc();
    }

    public long contarSospechososPorUsuario(String rutUsuario) {
        log.info("Contando acciones sospechosas de: {}", rutUsuario);
        return detailRepository.countByRutUsuarioAndResultado(
                rutUsuario, "SOSPECHOSO");
    }
}