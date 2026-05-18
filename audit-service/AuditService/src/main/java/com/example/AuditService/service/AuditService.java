// Lógica de negocio para las auditorías
// El Controller recibe la petición y este Service la procesa
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

    // Para registrar mensajes en la consola de IntelliJ
    private static final Logger log =
            LoggerFactory.getLogger(AuditService.class);

    // Accede a la tabla audits en la base de datos
    private final AuditRepository auditRepository;

    // Cliente HTTP para comunicarse con otros microservicios
    // En este caso se usa para llamar al User Service
    private final WebClient webClient;

    // Devuelve todas las auditorías de la BD
    public List<Audit> obtenerTodas() {
        log.info("Obteniendo todas las auditorías");
        return auditRepository.findAll();
    }

    // Busca una auditoría por su id
    // Si no existe lanza RuntimeException → HTTP 404
    public Audit obtenerPorId(Long id) {
        log.info("Buscando auditoría con id: {}", id);
        return auditRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Auditoría con id {} no encontrada", id);
                    return new RuntimeException(
                            "Auditoría no encontrada con id: " + id);
                });
    }

    // Registra una nueva auditoría en la BD
    public Audit registrar(AuditDTO dto) {
        log.info("Registrando auditoría tipo: {}", dto.getTipoAuditoria());

        // Llama al User Service para verificar que el auditor existe
        // y está activo — si no existe o está inactivo → lanza error
        verificarAuditorEnUserService(dto.getRutAuditor());

        // REGLA: la fecha de inicio no puede ser futura
        // Ej: no puedes registrar una auditoría para mañana
        if (dto.getFechaInicio().isAfter(LocalDateTime.now())) {
            log.warn("Fecha de inicio futura: {}", dto.getFechaInicio());
            throw new RuntimeException(
                    "La fecha de inicio no puede ser futura");
        }

        // Mapeo DTO → Entidad
        // Convierte el formulario que llegó en un objeto para guardar en la BD
        Audit nueva = new Audit();
        nueva.setRutAuditor(dto.getRutAuditor());     // quién audita
        nueva.setTipoAuditoria(dto.getTipoAuditoria()); // USUARIO, SISTEMA, etc.
        nueva.setEntidad(dto.getEntidad());           // qué microservicio se audita
        nueva.setEntidadId(dto.getEntidadId());       // id del registro auditado
        nueva.setDescripcion(dto.getDescripcion());   // descripción de la auditoría
        nueva.setEstado("EN_PROCESO");                // estado inicial siempre EN_PROCESO
        nueva.setFechaInicio(dto.getFechaInicio());   // cuándo inició

        // Guarda en la BD y retorna la auditoría con su id generado
        Audit guardada = auditRepository.save(nueva);
        log.info("Auditoría registrada con id: {}", guardada.getId());
        return guardada;
    }

    // Cambia el estado de una auditoría a COMPLETADA
    // Solo funciona si la auditoría está EN_PROCESO
    public Audit completar(Long id) {
        log.info("Completando auditoría con id: {}", id);
        Audit auditoria = obtenerPorId(id);

        // REGLA: solo se completan auditorías EN_PROCESO
        // Si ya está COMPLETADA u OBSERVACION → lanza error
        if (!auditoria.getEstado().equals("EN_PROCESO")) {
            log.warn("Auditoría {} no está EN_PROCESO. Estado: {}",
                    id, auditoria.getEstado());
            throw new RuntimeException(
                    "Solo se pueden completar auditorías EN_PROCESO. "
                            + "Estado actual: " + auditoria.getEstado());
        }

        auditoria.setEstado("COMPLETADA");
        // Registra la fecha y hora exacta en que se completó
        auditoria.setFechaCierre(LocalDateTime.now());

        Audit actualizada = auditRepository.save(auditoria);
        log.info("Auditoría {} completada correctamente", id);
        return actualizada;
    }

    // Cambia el estado a OBSERVACION cuando se encontraron irregularidades
    // Solo funciona si la auditoría está EN_PROCESO
    public Audit marcarObservacion(Long id) {
        log.info("Marcando auditoría {} con observación", id);
        Audit auditoria = obtenerPorId(id);

        // REGLA: solo se marcan auditorías EN_PROCESO
        if (!auditoria.getEstado().equals("EN_PROCESO")) {
            log.warn("Auditoría {} no está EN_PROCESO. Estado: {}",
                    id, auditoria.getEstado());
            throw new RuntimeException(
                    "Solo se pueden marcar auditorías EN_PROCESO. "
                            + "Estado actual: " + auditoria.getEstado());
        }

        auditoria.setEstado("OBSERVACION");
        // Registra la fecha y hora exacta en que se marcó
        auditoria.setFechaCierre(LocalDateTime.now());

        Audit actualizada = auditRepository.save(auditoria);
        log.info("Auditoría {} marcada con observación", id);
        return actualizada;
    }

    // Elimina una auditoría por su id
    // existsById verifica si existe antes de intentar eliminar
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

    // Devuelve todas las auditorías de un auditor específico
    public List<Audit> obtenerPorAuditor(String rutAuditor) {
        log.info("Obteniendo auditorías del auditor: {}", rutAuditor);
        return auditRepository.findByRutAuditor(rutAuditor);
    }

    // Devuelve auditorías por tipo (USUARIO, SISTEMA, etc.)
    public List<Audit> obtenerPorTipo(String tipoAuditoria) {
        log.info("Obteniendo auditorías de tipo: {}", tipoAuditoria);
        return auditRepository.findByTipoAuditoria(tipoAuditoria);
    }

    // Devuelve auditorías por estado (EN_PROCESO, COMPLETADA, OBSERVACION)
    public List<Audit> obtenerPorEstado(String estado) {
        log.info("Obteniendo auditorías con estado: {}", estado);
        return auditRepository.findByEstado(estado);
    }

    // Devuelve auditorías de un microservicio específico
    public List<Audit> obtenerPorEntidad(String entidad) {
        log.info("Obteniendo auditorías de entidad: {}", entidad);
        return auditRepository.findByEntidad(entidad);
    }

    // Devuelve auditorías de un microservicio y un registro específico
    public List<Audit> obtenerPorEntidadYId(String entidad,
                                            Long entidadId) {
        log.info("Obteniendo auditorías de {} id {}", entidad, entidadId);
        return auditRepository.findByEntidadAndEntidadId(
                entidad, entidadId);
    }

    // Devuelve auditorías iniciadas en un rango de fechas
    public List<Audit> obtenerPorRangoFechas(LocalDateTime desde,
                                             LocalDateTime hasta) {
        log.info("Obteniendo auditorías entre {} y {}", desde, hasta);
        return auditRepository.findByFechaInicioBetween(desde, hasta);
    }

    // Devuelve auditorías de un auditor del más reciente al más antiguo
    public List<Audit> obtenerPorAuditorOrdenadas(String rutAuditor) {
        log.info("Obteniendo auditorías de {} ordenadas", rutAuditor);
        return auditRepository
                .findByRutAuditorOrderByFechaInicioDesc(rutAuditor);
    }

    // Devuelve auditorías EN_PROCESO del más antiguo al más reciente
    // Útil para ver cuáles llevan más tiempo sin cerrarse
    public List<Audit> obtenerEnProcesoOrdenadas() {
        log.info("Obteniendo auditorías en proceso ordenadas");
        return auditRepository
                .findByEstadoOrderByFechaInicioAsc("EN_PROCESO");
    }

    // Devuelve las últimas 10 auditorías registradas
    public List<Audit> obtenerUltimasAuditorias() {
        log.info("Obteniendo las últimas 10 auditorías");
        return auditRepository.findTop10ByOrderByIdDesc();
    }

    // Cuenta cuántas auditorías hay con un estado específico
    // Devuelve un número no una lista
    public long contarPorEstado(String estado) {
        log.info("Contando auditorías con estado: {}", estado);
        return auditRepository.countByEstado(estado);
    }

    // Cuenta cuántas auditorías hay de un tipo específico
    public long contarPorTipo(String tipoAuditoria) {
        log.info("Contando auditorías de tipo: {}", tipoAuditoria);
        return auditRepository.countByTipoAuditoria(tipoAuditoria);
    }

    // Verifica que el auditor exista y esté activo en User Service
    // Se llama antes de registrar una auditoría
    private void verificarAuditorEnUserService(String rut) {
        try {
            log.info("Verificando auditor {} en User Service", rut);

            // Llama al User Service con GET /api/v1/users/rut/12345678-9
            UserResponseDTO usuario = webClient.get()
                    .uri("/api/v1/users/rut/{rut}", rut)
                    .retrieve()
                    .bodyToMono(UserResponseDTO.class)
                    .block();

            // REGLA: el auditor debe estar activo en el sistema
            // Si activo = false → no puede crear auditorías
            if (!usuario.getActivo()) {
                log.warn("Auditor {} está inactivo", rut);
                throw new RuntimeException(
                        "El auditor con RUT " + rut
                                + " está inactivo en el sistema");
            }

            log.info("Auditor {} verificado correctamente", rut);

        } catch (WebClientResponseException.NotFound e) {
            // El User Service respondió HTTP 404 → el auditor no existe
            log.warn("Auditor {} no encontrado en User Service", rut);
            throw new RuntimeException(
                    "El auditor con RUT " + rut
                            + " no existe en el sistema");

        } catch (RuntimeException e) {
            // Re-lanza el RuntimeException sin modificarlo
            // Así los errores de las reglas de negocio llegan al Controller
            throw e;

        } catch (Exception e) {
            // Cualquier otro error — probablemente User Service no está corriendo
            log.error("Error al comunicarse con User Service: {}",
                    e.getMessage());
            throw new RuntimeException(
                    "Error al verificar el auditor. "
                            + "Verifique que User Service esté corriendo "
                            + "en el puerto 8082");
        }
    }
}