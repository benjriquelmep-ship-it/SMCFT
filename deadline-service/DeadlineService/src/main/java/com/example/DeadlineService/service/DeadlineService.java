// Lógica de negocio para los deadlines del sistema fronterizo
// Se comunica con Entry Service para verificar que el ingreso existe
package com.example.DeadlineService.service;

import com.example.DeadlineService.dto.DeadlineDTO;
import com.example.DeadlineService.dto.EntryResponseDTO;
import com.example.DeadlineService.model.Deadline;
import com.example.DeadlineService.repository.DeadlineRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;    // llama a otros microservicios
import org.springframework.web.reactive.function.client.WebClientResponseException; // error HTTP
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class DeadlineService {

    // Accede a la tabla deadlines en la BD
    private final DeadlineRepository deadlineRepository;

    // Cliente HTTP para llamar a Entry Service
    // Se usa para verificar que el ingreso existe antes de registrar el deadline
    private final WebClient webClient;

    // Devuelve todos los deadlines de la BD
    public List<Deadline> obtenerTodos() {
        log.info("Obteniendo todos los deadlines");
        return deadlineRepository.findAll();
    }

    // Busca un deadline por su id
    // Si no existe lanza RuntimeException → HTTP 404
    public Deadline obtenerPorId(Long id) {
        log.info("Buscando deadline con id: {}", id);
        return deadlineRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Deadline con id {} no encontrado", id);
                    return new RuntimeException(
                            "Deadline no encontrado con id: " + id);
                });
    }

    // Registra un nuevo deadline para un vehículo
    public Deadline registrar(DeadlineDTO dto) {
        log.info("Registrando deadline para vehículo: {}",
                dto.getPatente());

        // Llama a Entry Service para verificar que el ingreso existe
        // Si no existe → lanza error → no se registra el deadline
        verificarIngresoEnEntryService(dto.getEntryId());

        // REGLA 1: la fecha límite debe ser posterior a la fecha de ingreso
        // No tiene sentido que el plazo venza antes de que ingrese el vehículo
        if (!dto.getFechaLimite().isAfter(dto.getFechaIngreso())) {
            log.warn("Fecha límite no es posterior a fecha ingreso");
            throw new RuntimeException(
                    "La fecha límite debe ser posterior "
                            + "a la fecha de ingreso");
        }

        // REGLA 2: la fecha límite no puede ser una fecha ya pasada
        // No se puede registrar un deadline que ya venció
        if (dto.getFechaLimite().isBefore(LocalDateTime.now())) {
            log.warn("Fecha límite {} ya pasó", dto.getFechaLimite());
            throw new RuntimeException(
                    "La fecha límite no puede ser una fecha pasada");
        }

        // Mapeo DTO → Entidad
        // Convierte el formulario que llegó en un objeto para guardar en la BD
        Deadline nuevo = new Deadline();
        nuevo.setPatente(dto.getPatente().toUpperCase()); // patente en mayúsculas
        nuevo.setRutConductor(dto.getRutConductor());     // quién conduce
        nuevo.setEntryId(dto.getEntryId());               // FK hacia Entry Service
        nuevo.setFechaIngreso(dto.getFechaIngreso());     // cuándo entró al país
        nuevo.setFechaLimite(dto.getFechaLimite());       // hasta cuándo puede estar
        nuevo.setTipo(dto.getTipo());                     // ADMISION_TEMPORAL o RETORNO_OBLIGATORIO
        nuevo.setEstado("ACTIVO");                        // estado inicial siempre ACTIVO
        nuevo.setObservaciones(dto.getObservaciones());   // comentarios opcionales

        // Guarda en la BD y retorna el deadline con su id generado
        Deadline guardado = deadlineRepository.save(nuevo);
        log.info("Deadline registrado con id: {}", guardado.getId());
        return guardado;
    }

    // Cierra un deadline — el vehículo salió del país antes del plazo
    // Solo funciona si el deadline está ACTIVO
    public Deadline cerrar(Long id, String observaciones) {
        log.info("Cerrando deadline con id: {}", id);
        Deadline deadline = obtenerPorId(id);

        // REGLA: solo se cierran deadlines ACTIVOS
        // Si ya está CERRADO o VENCIDO → no se puede volver a cerrar
        if (!deadline.getEstado().equals("ACTIVO")) {
            log.warn("Deadline {} no está ACTIVO. Estado: {}",
                    id, deadline.getEstado());
            throw new RuntimeException(
                    "Solo se pueden cerrar deadlines en estado ACTIVO. "
                            + "Estado actual: " + deadline.getEstado());
        }

        deadline.setEstado("CERRADO");
        deadline.setObservaciones(observaciones); // motivo del cierre
        Deadline actualizado = deadlineRepository.save(deadline);
        log.info("Deadline {} cerrado correctamente", id);
        return actualizado;
    }

    // Marca un deadline como vencido — el plazo expiró sin salir
    // Solo funciona si el deadline está ACTIVO
    public Deadline vencer(Long id) {
        log.info("Marcando deadline {} como vencido", id);
        Deadline deadline = obtenerPorId(id);

        // REGLA: solo se vencen deadlines ACTIVOS
        // Si ya está VENCIDO o CERRADO → no se puede vencer de nuevo
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

    // Elimina un deadline por su id
    // existsById verifica si existe antes de intentar eliminar
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

    // Calcula cuántos días le quedan al deadline antes de vencer
    // Si el resultado es negativo → el deadline ya venció
    public long calcularDiasRestantes(Long id) {
        log.info("Calculando días restantes del deadline: {}", id);
        Deadline deadline = obtenerPorId(id);
        long dias = ChronoUnit.DAYS.between(
                LocalDateTime.now(), deadline.getFechaLimite());
        log.info("Días restantes para deadline {}: {}", id, dias);
        return dias;
    }

    // Devuelve todos los deadlines de un vehículo específico
    public List<Deadline> obtenerPorPatente(String patente) {
        log.info("Obteniendo deadlines de la patente: {}", patente);
        return deadlineRepository.findByPatente(patente);
    }

    // Devuelve todos los deadlines de un conductor específico
    public List<Deadline> obtenerPorConductor(String rutConductor) {
        log.info("Obteniendo deadlines del conductor: {}",
                rutConductor);
        return deadlineRepository.findByRutConductor(rutConductor);
    }

    // Devuelve deadlines por estado (ACTIVO, VENCIDO, CERRADO)
    public List<Deadline> obtenerPorEstado(String estado) {
        log.info("Obteniendo deadlines con estado: {}", estado);
        return deadlineRepository.findByEstado(estado);
    }

    // Devuelve deadlines por tipo de ingreso
    public List<Deadline> obtenerPorTipo(String tipo) {
        log.info("Obteniendo deadlines de tipo: {}", tipo);
        return deadlineRepository.findByTipo(tipo);
    }

    // Devuelve deadlines ACTIVOS que vencen en los próximos 15 días
    // Útil para generar alertas antes de que venzan
    public List<Deadline> obtenerProximosAVencer() {
        log.info("Obteniendo deadlines activos que vencen en 15 días");
        // Calcula la fecha de aquí a 15 días
        LocalDateTime en15Dias = LocalDateTime.now().plusDays(15);
        return deadlineRepository
                .findByFechaLimiteBeforeAndEstado(en15Dias, "ACTIVO");
    }

    // Devuelve deadlines de un vehículo ordenados del que vence antes
    public List<Deadline> obtenerPorPatenteOrdenados(String patente) {
        log.info("Obteniendo deadlines de {} ordenados", patente);
        return deadlineRepository
                .findByPatenteOrderByFechaLimiteAsc(patente);
    }

    // Devuelve deadlines ACTIVOS ordenados del que vence antes al que vence después
    // Útil para priorizar cuáles atender primero
    public List<Deadline> obtenerActivosOrdenados() {
        log.info("Obteniendo deadlines activos ordenados");
        return deadlineRepository
                .findByEstadoOrderByFechaLimiteAsc("ACTIVO");
    }

    // Devuelve los últimos 10 deadlines registrados en el sistema
    public List<Deadline> obtenerUltimosDeadlines() {
        log.info("Obteniendo los últimos 10 deadlines");
        return deadlineRepository.findTop10ByOrderByIdDesc();
    }

    // Cuenta cuántos deadlines hay con un estado específico
    // Devuelve un número no una lista
    public long contarPorEstado(String estado) {
        log.info("Contando deadlines con estado: {}", estado);
        return deadlineRepository.countByEstado(estado);
    }

    // Verifica que el ingreso existe en Entry Service
    // Se llama antes de registrar un deadline
    // Si no existe → lanza error con mensaje claro
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

            // Entry Service respondió HTTP 404 → el ingreso no existe
            log.warn("Ingreso {} no encontrado en Entry Service",
                    entryId);
            throw new RuntimeException(
                    "El ingreso con id " + entryId
                            + " no existe en el sistema");

        } catch (Exception e) {

            // Cualquier otro error — Entry Service caído o sin conexión
            log.error("Error al comunicarse con Entry Service: {}",
                    e.getMessage());
            throw new RuntimeException(
                    "Error al verificar el ingreso. "
                            + "Verifique que Entry Service esté corriendo "
                            + "en el puerto 8085");
        }
    }
}