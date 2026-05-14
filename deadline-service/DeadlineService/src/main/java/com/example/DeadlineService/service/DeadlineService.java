// service/DeadlineService.java
package com.example.DeadlineService.service;

import com.example.DeadlineService.dto.DeadlineDTO;
import com.example.DeadlineService.dto.EntryResponseDTO;
import com.example.DeadlineService.model.Deadline;
import com.example.DeadlineService.repository.DeadlineRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DeadlineService {

    private static final Logger log =
            LoggerFactory.getLogger(DeadlineService.class);

    private final DeadlineRepository deadlineRepository;
    private final WebClient webClient;

    // -------------------------------------------------------
    // CRUD BÁSICO
    // -------------------------------------------------------

    public List<Deadline> obtenerTodos() {
        log.info("Obteniendo todos los deadlines");
        return deadlineRepository.findAll();
    }

    public Deadline obtenerPorId(Long id) {
        log.info("Buscando deadline con id: {}", id);
        return deadlineRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Deadline con id {} no encontrado", id);
                    return new RuntimeException(
                            "Deadline no encontrado con id: " + id);
                });
    }

    public Deadline registrar(DeadlineDTO dto) {
        log.info("Registrando deadline para vehículo: {}",
                dto.getPatente());

        // COMUNICACIÓN con Entry Service
        verificarIngresoEnEntryService(dto.getEntryId());

        // REGLA DE NEGOCIO 1: fecha límite posterior a fecha ingreso
        if (!dto.getFechaLimite().isAfter(dto.getFechaIngreso())) {
            log.warn("Fecha límite no es posterior a fecha ingreso");
            throw new RuntimeException(
                    "La fecha límite debe ser posterior "
                            + "a la fecha de ingreso");
        }

        // REGLA DE NEGOCIO 2: fecha límite no puede ser pasada
        if (dto.getFechaLimite().isBefore(LocalDateTime.now())) {
            log.warn("Fecha límite {} ya pasó", dto.getFechaLimite());
            throw new RuntimeException(
                    "La fecha límite no puede ser una fecha pasada");
        }

        Deadline nuevo = new Deadline();
        nuevo.setPatente(dto.getPatente().toUpperCase());
        nuevo.setRutConductor(dto.getRutConductor());
        nuevo.setEntryId(dto.getEntryId());
        nuevo.setFechaIngreso(dto.getFechaIngreso());
        nuevo.setFechaLimite(dto.getFechaLimite());
        nuevo.setTipo(dto.getTipo());
        nuevo.setEstado("ACTIVO");
        nuevo.setObservaciones(dto.getObservaciones());

        Deadline guardado = deadlineRepository.save(nuevo);
        log.info("Deadline registrado con id: {}", guardado.getId());
        return guardado;
    }

    public Deadline cerrar(Long id, String observaciones) {
        log.info("Cerrando deadline con id: {}", id);
        Deadline deadline = obtenerPorId(id);

        // REGLA DE NEGOCIO: solo se cierran deadlines ACTIVOS
        if (!deadline.getEstado().equals("ACTIVO")) {
            log.warn("Deadline {} no está ACTIVO. Estado: {}",
                    id, deadline.getEstado());
            throw new RuntimeException(
                    "Solo se pueden cerrar deadlines en estado ACTIVO. "
                            + "Estado actual: " + deadline.getEstado());
        }

        deadline.setEstado("CERRADO");
        deadline.setObservaciones(observaciones);
        Deadline actualizado = deadlineRepository.save(deadline);
        log.info("Deadline {} cerrado correctamente", id);
        return actualizado;
    }

    public Deadline vencer(Long id) {
        log.info("Marcando deadline {} como vencido", id);
        Deadline deadline = obtenerPorId(id);

        // REGLA DE NEGOCIO: solo se vencen deadlines ACTIVOS
        if (!deadline.getEstado().equals("ACTIVO")) {
            log.warn("Deadline {} no está ACTIVO. Estado: {}",
                    id, deadline.getEstado());
            throw new RuntimeException(
                    "Solo se pueden vencer deadlines en estado ACTIVO. "
                            + "Estado actual: " + deadline.getEstado());
        }

        deadline.setEstado("VENCIDO");
        Deadline actualizado = deadlineRepository.save(deadline);
        log.info("Deadline {} marcado como vencido", id);
        return actualizado;
    }

    public void eliminar(Long id) {
        log.info("Eliminando deadline con id: {}", id);
        if (!deadlineRepository.existsById(id)) {
            log.warn("Deadline con id {} no encontrado", id);
            throw new RuntimeException(
                    "Deadline no encontrado con id: " + id);
        }
        deadlineRepository.deleteById(id);
        log.info("Deadline {} eliminado correctamente", id);
    }

    public long calcularDiasRestantes(Long id) {
        log.info("Calculando días restantes del deadline: {}", id);
        Deadline deadline = obtenerPorId(id);
        long dias = ChronoUnit.DAYS.between(
                LocalDateTime.now(), deadline.getFechaLimite());
        log.info("Días restantes para deadline {}: {}", id, dias);
        return dias;
    }

    // -------------------------------------------------------
    // CONSULTAS DERIVADAS
    // -------------------------------------------------------

    public List<Deadline> obtenerPorPatente(String patente) {
        log.info("Obteniendo deadlines de la patente: {}", patente);
        return deadlineRepository.findByPatente(patente);
    }

    public List<Deadline> obtenerPorConductor(String rutConductor) {
        log.info("Obteniendo deadlines del conductor: {}",
                rutConductor);
        return deadlineRepository.findByRutConductor(rutConductor);
    }

    public List<Deadline> obtenerPorEstado(String estado) {
        log.info("Obteniendo deadlines con estado: {}", estado);
        return deadlineRepository.findByEstado(estado);
    }

    public List<Deadline> obtenerPorTipo(String tipo) {
        log.info("Obteniendo deadlines de tipo: {}", tipo);
        return deadlineRepository.findByTipo(tipo);
    }

    public List<Deadline> obtenerProximosAVencer() {
        log.info("Obteniendo deadlines activos que vencen en 15 días");
        LocalDateTime en15Dias = LocalDateTime.now().plusDays(15);
        return deadlineRepository
                .findByFechaLimiteBeforeAndEstado(en15Dias, "ACTIVO");
    }

    public List<Deadline> obtenerPorPatenteOrdenados(String patente) {
        log.info("Obteniendo deadlines de {} ordenados", patente);
        return deadlineRepository
                .findByPatenteOrderByFechaLimiteAsc(patente);
    }

    public List<Deadline> obtenerActivosOrdenados() {
        log.info("Obteniendo deadlines activos ordenados");
        return deadlineRepository
                .findByEstadoOrderByFechaLimiteAsc("ACTIVO");
    }

    public List<Deadline> obtenerUltimosDeadlines() {
        log.info("Obteniendo los últimos 10 deadlines");
        return deadlineRepository.findTop10ByOrderByIdDesc();
    }

    public long contarPorEstado(String estado) {
        log.info("Contando deadlines con estado: {}", estado);
        return deadlineRepository.countByEstado(estado);
    }

    // -------------------------------------------------------
    // COMUNICACIÓN CON ENTRY SERVICE — WebClient
    // -------------------------------------------------------

    private void verificarIngresoEnEntryService(Long entryId) {
        try {
            log.info("Verificando ingreso {} en Entry Service", entryId);

            webClient.get()
                    .uri("/api/v1/entries/{id}", entryId)
                    .retrieve()
                    .bodyToMono(EntryResponseDTO.class)
                    .block();

            log.info("Ingreso {} verificado correctamente", entryId);

        } catch (WebClientResponseException.NotFound e) {
            log.warn("Ingreso {} no encontrado en Entry Service",
                    entryId);
            throw new RuntimeException(
                    "El ingreso con id " + entryId
                            + " no existe en el sistema");

        } catch (Exception e) {
            log.error("Error al comunicarse con Entry Service: {}",
                    e.getMessage());
            throw new RuntimeException(
                    "Error al verificar el ingreso. "
                            + "Verifique que Entry Service esté corriendo "
                            + "en el puerto 8085");
        }
    }
}