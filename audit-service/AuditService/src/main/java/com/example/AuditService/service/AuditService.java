// service/AuditService.java
package com.example.AuditService.service;

import com.example.AuditService.dto.AuditDTO;
import com.example.AuditService.dto.UserResponseDTO;
import com.example.AuditService.model.Audit;
import com.example.AuditService.repository.AuditRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AuditService {

    private static final Logger log =
            LoggerFactory.getLogger(AuditService.class);

    private final AuditRepository auditRepository;
    private final WebClient webClient;

    // -------------------------------------------------------
    // CRUD BÁSICO
    // -------------------------------------------------------

    public List<Audit> obtenerTodas() {
        log.info("Obteniendo todas las auditorías");
        return auditRepository.findAll();
    }

    public Audit obtenerPorId(Long id) {
        log.info("Buscando auditoría con id: {}", id);
        return auditRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Auditoría con id {} no encontrada", id);
                    return new RuntimeException(
                            "Auditoría no encontrada con id: " + id);
                });
    }

    // Registrar nueva auditoría
    public Audit registrar(AuditDTO dto) {
        log.info("Registrando auditoría tipo: {}", dto.getTipoAuditoria());

        // COMUNICACIÓN con User Service
        // Verificar que el auditor existe
        verificarAuditorEnUserService(dto.getRutAuditor());

        // REGLA DE NEGOCIO: la fecha de inicio no puede ser futura
        if (dto.getFechaInicio().isAfter(LocalDateTime.now())) {
            log.warn("Fecha de inicio futura: {}", dto.getFechaInicio());
            throw new RuntimeException(
                    "La fecha de inicio no puede ser futura");
        }

        // Mapeo DTO → Entidad
        Audit nueva = new Audit();
        nueva.setRutAuditor(dto.getRutAuditor());
        nueva.setTipoAuditoria(dto.getTipoAuditoria());
        nueva.setEntidad(dto.getEntidad());
        nueva.setEntidadId(dto.getEntidadId());
        nueva.setDescripcion(dto.getDescripcion());
        nueva.setEstado("EN_PROCESO");
        nueva.setFechaInicio(dto.getFechaInicio());

        Audit guardada = auditRepository.save(nueva);
        log.info("Auditoría registrada con id: {}", guardada.getId());
        return guardada;
    }

    // Completar auditoría
    public Audit completar(Long id) {
        log.info("Completando auditoría con id: {}", id);
        Audit auditoria = obtenerPorId(id);

        // REGLA DE NEGOCIO: solo se pueden completar auditorías EN_PROCESO
        if (!auditoria.getEstado().equals("EN_PROCESO")) {
            log.warn("Auditoría {} no está EN_PROCESO. Estado: {}",
                    id, auditoria.getEstado());
            throw new RuntimeException(
                    "Solo se pueden completar auditorías EN_PROCESO. "
                            + "Estado actual: " + auditoria.getEstado());
        }

        auditoria.setEstado("COMPLETADA");
        auditoria.setFechaCierre(LocalDateTime.now());

        Audit actualizada = auditRepository.save(auditoria);
        log.info("Auditoría {} completada correctamente", id);
        return actualizada;
    }

    // Marcar auditoría con observación
    public Audit marcarObservacion(Long id) {
        log.info("Marcando auditoría {} con observación", id);
        Audit auditoria = obtenerPorId(id);

        // REGLA DE NEGOCIO: solo se pueden marcar auditorías EN_PROCESO
        if (!auditoria.getEstado().equals("EN_PROCESO")) {
            log.warn("Auditoría {} no está EN_PROCESO. Estado: {}",
                    id, auditoria.getEstado());
            throw new RuntimeException(
                    "Solo se pueden marcar auditorías EN_PROCESO. "
                            + "Estado actual: " + auditoria.getEstado());
        }

        auditoria.setEstado("OBSERVACION");
        auditoria.setFechaCierre(LocalDateTime.now());

        Audit actualizada = auditRepository.save(auditoria);
        log.info("Auditoría {} marcada con observación", id);
        return actualizada;
    }

    // Eliminar auditoría
    public void eliminar(Long id) {
        log.info("Eliminando auditoría con id: {}", id);
        if (!auditRepository.existsById(id)) {
            log.warn("Auditoría con id {} no encontrada", id);
            throw new RuntimeException(
                    "Auditoría no encontrada con id: " + id);
        }
        auditRepository.deleteById(id);
        log.info("Auditoría {} eliminada correctamente", id);
    }

    // -------------------------------------------------------
    // CONSULTAS DERIVADAS
    // -------------------------------------------------------

    public List<Audit> obtenerPorAuditor(String rutAuditor) {
        log.info("Obteniendo auditorías del auditor: {}", rutAuditor);
        return auditRepository.findByRutAuditor(rutAuditor);
    }

    public List<Audit> obtenerPorTipo(String tipoAuditoria) {
        log.info("Obteniendo auditorías de tipo: {}", tipoAuditoria);
        return auditRepository.findByTipoAuditoria(tipoAuditoria);
    }

    public List<Audit> obtenerPorEstado(String estado) {
        log.info("Obteniendo auditorías con estado: {}", estado);
        return auditRepository.findByEstado(estado);
    }

    public List<Audit> obtenerPorEntidad(String entidad) {
        log.info("Obteniendo auditorías de entidad: {}", entidad);
        return auditRepository.findByEntidad(entidad);
    }

    public List<Audit> obtenerPorEntidadYId(String entidad,
                                            Long entidadId) {
        log.info("Obteniendo auditorías de {} id {}", entidad, entidadId);
        return auditRepository.findByEntidadAndEntidadId(
                entidad, entidadId);
    }

    public List<Audit> obtenerPorRangoFechas(LocalDateTime desde,
                                             LocalDateTime hasta) {
        log.info("Obteniendo auditorías entre {} y {}", desde, hasta);
        return auditRepository.findByFechaInicioBetween(desde, hasta);
    }

    public List<Audit> obtenerPorAuditorOrdenadas(String rutAuditor) {
        log.info("Obteniendo auditorías de {} ordenadas", rutAuditor);
        return auditRepository
                .findByRutAuditorOrderByFechaInicioDesc(rutAuditor);
    }

    public List<Audit> obtenerEnProcesoOrdenadas() {
        log.info("Obteniendo auditorías en proceso ordenadas");
        return auditRepository
                .findByEstadoOrderByFechaInicioAsc("EN_PROCESO");
    }

    public List<Audit> obtenerUltimasAuditorias() {
        log.info("Obteniendo las últimas 10 auditorías");
        return auditRepository.findTop10ByOrderByIdDesc();
    }

    public long contarPorEstado(String estado) {
        log.info("Contando auditorías con estado: {}", estado);
        return auditRepository.countByEstado(estado);
    }

    public long contarPorTipo(String tipoAuditoria) {
        log.info("Contando auditorías de tipo: {}", tipoAuditoria);
        return auditRepository.countByTipoAuditoria(tipoAuditoria);
    }

    // -------------------------------------------------------
    // COMUNICACIÓN CON USER SERVICE — WebClient
    // -------------------------------------------------------

    private void verificarAuditorEnUserService(String rut) {
        try {
            log.info("Verificando auditor {} en User Service", rut);

            UserResponseDTO usuario = webClient.get()
                    // GET http://localhost:8082/api/v1/users/rut/12345678-9
                    .uri("/api/v1/users/rut/{rut}", rut)
                    .retrieve()
                    .bodyToMono(UserResponseDTO.class)
                    .block();

            // REGLA DE NEGOCIO: el auditor debe estar activo
            if (!usuario.getActivo()) {
                log.warn("Auditor {} está inactivo", rut);
                throw new RuntimeException(
                        "El auditor con RUT " + rut
                                + " está inactivo en el sistema");
            }

            log.info("Auditor {} verificado correctamente", rut);

        } catch (WebClientResponseException.NotFound e) {
            log.warn("Auditor {} no encontrado en User Service", rut);
            throw new RuntimeException(
                    "El auditor con RUT " + rut
                            + " no existe en el sistema");

        } catch (RuntimeException e) {
            throw e;

        } catch (Exception e) {
            log.error("Error al comunicarse con User Service: {}",
                    e.getMessage());
            throw new RuntimeException(
                    "Error al verificar el auditor. "
                            + "Verifique que User Service esté corriendo "
                            + "en el puerto 8082");
        }
    }
}