// Lógica de negocio para los detalles de auditoría
// El Controller recibe la petición y este Service la procesa
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

    // Para registrar mensajes en la consola de IntelliJ
    private static final Logger log =
            LoggerFactory.getLogger(AuditDetailService.class);

    // Accede a la tabla audit_details en la base de datos
    private final AuditDetailRepository detailRepository;

    // Necesario para verificar que la auditoría existe
    // antes de registrar un detalle
    private final AuditService auditService;

    // Devuelve todos los detalles de la BD
    public List<AuditDetail> obtenerTodos() {
        log.info("Obteniendo todos los detalles de auditoría");
        return detailRepository.findAll();
    }

    // Busca un detalle por su id
    // Si no existe lanza RuntimeException → GlobalExceptionHandler
    // responde HTTP 404 automáticamente
    public AuditDetail obtenerPorId(Long id) {
        log.info("Buscando detalle con id: {}", id);
        return detailRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Detalle con id {} no encontrado", id);
                    return new RuntimeException(
                            "Detalle no encontrado con id: " + id);
                });
    }

    // Registra una nueva acción dentro de una auditoría
    public AuditDetail registrar(AuditDetailDTO dto) {
        log.info("Registrando detalle para auditoría: {}",
                dto.getAuditId());

        // REGLA DE NEGOCIO 1: la auditoría debe existir
        // Si no existe → auditService lanza RuntimeException → HTTP 404
        Audit auditoria = auditService.obtenerPorId(dto.getAuditId());

        // REGLA 2: solo se pueden agregar detalles a auditorías EN_PROCESO
        // Si está COMPLETADA u OBSERVACION → no se puede agregar más detalles
        if (!auditoria.getEstado().equals("EN_PROCESO")) {
            log.warn("No se pueden registrar detalles — estado: {}",
                    auditoria.getEstado());
            throw new RuntimeException(
                    "Solo se pueden registrar detalles en auditorías EN_PROCESO");
        }

        // Mapeo DTO → Entidad
        // Convierte el formulario que llegó en un objeto para guardar en la BD
        AuditDetail detalle = new AuditDetail();
        detalle.setAudit(auditoria); // FK hacia la auditoría
        detalle.setAccion(dto.getAccion()); // Ej: "LOGIN"
        detalle.setDescripcion(dto.getDescripcion()); // descripción de la acción
        detalle.setRutUsuario(dto.getRutUsuario()); // quién hizo la acción
        detalle.setResultado(dto.getResultado()); // EXITOSO, FALLIDO o SOSPECHOSO
        detalle.setIpAddress(dto.getIpAddress()); // IP desde donde se hizo
        detalle.setFechaAccion(dto.getFechaAccion()); // cuándo se hizo

        // Guarda en la BD y retorna el detalle con su id generado
        AuditDetail guardado = detailRepository.save(detalle);
        log.info("Detalle de auditoría registrado con id: {}",
                guardado.getId());
        return guardado;
    }

    // Eliminar detalle por su id
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

    // Devuelve todos los detalles de una auditoría específica
    public List<AuditDetail> obtenerPorAuditoria(Long auditId) {
        log.info("Obteniendo detalles de auditoría: {}", auditId);
        return detailRepository.findByAuditId(auditId);
    }

    // Devuelve detalles por resultado (EXITOSO, FALLIDO o SOSPECHOSO)
    public List<AuditDetail> obtenerPorResultado(String resultado) {
        log.info("Obteniendo detalles con resultado: {}", resultado);
        return detailRepository.findByResultado(resultado);
    }

    // Devuelve detalles de una auditoría filtrados por resultado
    public List<AuditDetail> obtenerPorAuditoriaYResultado(
            Long auditId, String resultado) {
        log.info("Obteniendo detalles de {} con resultado {}",
                auditId, resultado);
        return detailRepository.findByAuditIdAndResultado(
                auditId, resultado);
    }

    // Devuelve todas las acciones realizadas por un usuario
    public List<AuditDetail> obtenerPorUsuario(String rutUsuario) {
        log.info("Obteniendo detalles del usuario: {}", rutUsuario);
        return detailRepository.findByRutUsuario(rutUsuario);
    }

    // Devuelve solo las acciones SOSPECHOSAS de un usuario
    public List<AuditDetail> obtenerSospechososPorUsuario(
            String rutUsuario) {
        log.info("Obteniendo acciones sospechosas de: {}", rutUsuario);
        return detailRepository.findByRutUsuarioAndResultado(
                rutUsuario, "SOSPECHOSO");
    }

    // Devuelve detalles registrados en un rango de fechas
    public List<AuditDetail> obtenerPorRangoFechas(
            LocalDateTime desde, LocalDateTime hasta) {
        log.info("Obteniendo detalles entre {} y {}", desde, hasta);
        return detailRepository.findByFechaAccionBetween(desde, hasta);
    }

    // Busca detalles cuya acción contenga el texto buscado
    public List<AuditDetail> buscarPorAccion(String accion) {
        log.info("Buscando detalles con acción: {}", accion);
        return detailRepository.findByAccionContainingIgnoreCase(accion);
    }

    // Devuelve detalles de una auditoría ordenados del más reciente al más antiguo
    public List<AuditDetail> obtenerPorAuditoriaOrdenados(Long auditId) {
        log.info("Obteniendo detalles de {} ordenados", auditId);
        return detailRepository
                .findByAuditIdOrderByFechaAccionDesc(auditId);
    }

    // Devuelve los últimos 10 detalles registrados en el sistema
    public List<AuditDetail> obtenerUltimosDetalles() {
        log.info("Obteniendo los últimos 10 detalles");
        return detailRepository.findTop10ByOrderByIdDesc();
    }

    // Cuenta cuántas acciones SOSPECHOSAS tiene un usuario
    public long contarSospechososPorUsuario(String rutUsuario) {
        log.info("Contando acciones sospechosas de: {}", rutUsuario);
        return detailRepository.countByRutUsuarioAndResultado(
                rutUsuario, "SOSPECHOSO");
    }
}