// Lógica de negocio del Sanitary Service
// Se comunica con Vehicle Service para verificar que el vehículo existe
// antes de registrar una inspección sanitaria
package com.example.SanitaryService.service;

import com.example.SanitaryService.dto.SanitaryDTO;
import com.example.SanitaryService.dto.VehicleResponseDTO;
import com.example.SanitaryService.model.Sanitary;
import com.example.SanitaryService.repository.SanitaryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;                         e
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class SanitaryService {

    // Accede a la tabla sanitary_inspections en la BD
    private final SanitaryRepository sanitaryRepository;

    // Cliente HTTP para llamar a Vehicle Service
    // Se usa para verificar que el vehículo existe
    // antes de registrar la inspección
    private final WebClient webClient;

    // Devuelve todas las inspecciones sanitarias de la BD
    public List<Sanitary> obtenerTodas() {
        log.info("Obteniendo todas las inspecciones sanitarias");
        return sanitaryRepository.findAll();
    }

    // Busca una inspección por su id
    // Si no existe lanza RuntimeException → HTTP 404
    // SanitaryItemService llama a este método para verificar inspecciones
    public Sanitary obtenerPorId(Long id) {
        log.info("Buscando inspección con id: {}", id);
        return sanitaryRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Inspección con id {} no encontrada", id);
                    return new RuntimeException(
                            "Inspección no encontrada con id: " + id);
                });
    }

    // Registra una nueva inspección sanitaria para un vehículo
    public Sanitary registrar(SanitaryDTO dto) {
        log.info("Registrando inspección para vehículo: {}",
                dto.getPatente());

        // Llama a Vehicle Service para verificar que el vehículo existe
        // A diferencia de Entry Service, Sanitary Service NO verifica el estado
        // del vehículo — solo verifica que exista en el sistema
        verificarVehiculoEnVehicleService(dto.getPatente());

        // REGLA: la fecha de inspección no puede ser futura
        // No se puede registrar una inspección que aún no ocurrió
        if (dto.getFechaInspeccion().isAfter(LocalDateTime.now())) {
            log.warn("Fecha de inspección futura: {}",
                    dto.getFechaInspeccion());
            throw new RuntimeException(
                    "La fecha de inspección no puede ser futura");
        }

        // Mapeo DTO → Entidad
        // Convierte el formulario que llegó en un objeto para guardar en la BD
        Sanitary nueva = new Sanitary();
        nueva.setPatente(dto.getPatente().toUpperCase()); // patente en mayúsculas
        nueva.setRutConductor(dto.getRutConductor());     // quién conduce
        nueva.setRutInspector(dto.getRutInspector());     // inspector del SAG
        nueva.setPasoFronterizo(dto.getPasoFronterizo()); // dónde se realiza
        nueva.setFechaInspeccion(dto.getFechaInspeccion()); // cuándo se hizo
        // Resultado inicial siempre PENDIENTE — el inspector lo procesará después
        nueva.setResultado("PENDIENTE");
        nueva.setObservaciones(dto.getObservaciones());   // comentarios opcionales

        // Guarda en la BD y retorna la inspección con su id generado
        Sanitary guardada = sanitaryRepository.save(nueva);
        log.info("Inspección registrada con id: {}", guardada.getId());
        return guardada;
    }

    // El inspector aprueba la inspección — resultado APROBADO
    // Solo funciona si la inspección está PENDIENTE
    public Sanitary aprobar(Long id, String observaciones) {
        log.info("Aprobando inspección con id: {}", id);
        Sanitary inspeccion = obtenerPorId(id);

        // REGLA: solo se pueden aprobar inspecciones PENDIENTES
        // Si ya fue APROBADA o RECHAZADA → no se puede volver a procesar
        if (!inspeccion.getResultado().equals("PENDIENTE")) {
            log.warn("Inspección {} no está PENDIENTE. Resultado: {}",
                    id, inspeccion.getResultado());
            throw new RuntimeException(
                    "Solo se pueden aprobar inspecciones PENDIENTES. "
                            + "Resultado actual: " + inspeccion.getResultado());
        }

        inspeccion.setResultado("APROBADO");
        inspeccion.setObservaciones(observaciones); // motivo de la aprobación

        Sanitary actualizada = sanitaryRepository.save(inspeccion);
        log.info("Inspección {} aprobada correctamente", id);
        return actualizada;
    }

    // El inspector rechaza la inspección — resultado RECHAZADO
    // Solo funciona si la inspección está PENDIENTE
    // El vehículo NO puede cruzar la frontera
    public Sanitary rechazar(Long id, String observaciones) {
        log.info("Rechazando inspección con id: {}", id);
        Sanitary inspeccion = obtenerPorId(id);

        // REGLA: solo se pueden rechazar inspecciones PENDIENTES
        if (!inspeccion.getResultado().equals("PENDIENTE")) {
            log.warn("Inspección {} no está PENDIENTE. Resultado: {}",
                    id, inspeccion.getResultado());
            throw new RuntimeException(
                    "Solo se pueden rechazar inspecciones PENDIENTES. "
                            + "Resultado actual: " + inspeccion.getResultado());
        }

        inspeccion.setResultado("RECHAZADO");
        inspeccion.setObservaciones(observaciones); // motivo del rechazo

        Sanitary actualizada = sanitaryRepository.save(inspeccion);
        log.info("Inspección {} rechazada correctamente", id);
        return actualizada;
    }

    // Elimina una inspección por su id
    // existsById verifica si existe antes de intentar eliminar
    public void eliminar(Long id) {
        log.info("Eliminando inspección con id: {}", id);
        if (!sanitaryRepository.existsById(id)) {
            log.warn("Inspección con id {} no encontrada", id);
            throw new RuntimeException(
                    "Inspección no encontrada con id: " + id);
        }
        sanitaryRepository.deleteById(id);
        log.info("Inspección {} eliminada correctamente", id);
    }

    // Devuelve todas las inspecciones de un vehículo específico
    public List<Sanitary> obtenerPorPatente(String patente) {
        log.info("Obteniendo inspecciones de la patente: {}", patente);
        return sanitaryRepository.findByPatente(patente);
    }

    // Devuelve todas las inspecciones de un conductor específico
    public List<Sanitary> obtenerPorConductor(String rutConductor) {
        log.info("Obteniendo inspecciones del conductor: {}",
                rutConductor);
        return sanitaryRepository.findByRutConductor(rutConductor);
    }

    // Devuelve todas las inspecciones realizadas por un inspector específico
    public List<Sanitary> obtenerPorInspector(String rutInspector) {
        log.info("Obteniendo inspecciones del inspector: {}",
                rutInspector);
        return sanitaryRepository.findByRutInspector(rutInspector);
    }

    // Devuelve inspecciones por resultado (PENDIENTE, APROBADO, RECHAZADO)
    public List<Sanitary> obtenerPorResultado(String resultado) {
        log.info("Obteniendo inspecciones con resultado: {}", resultado);
        return sanitaryRepository.findByResultado(resultado);
    }

    // Devuelve todas las inspecciones de un paso fronterizo específico
    public List<Sanitary> obtenerPorPasoFronterizo(
            String pasoFronterizo) {
        log.info("Obteniendo inspecciones del paso: {}", pasoFronterizo);
        return sanitaryRepository.findByPasoFronterizo(pasoFronterizo);
    }

    // Devuelve inspecciones de un vehículo con un resultado específico
    public List<Sanitary> obtenerPorPatenteYResultado(
            String patente, String resultado) {
        log.info("Obteniendo inspecciones de {} con resultado {}",
                patente, resultado);
        return sanitaryRepository.findByPatenteAndResultado(
                patente, resultado);
    }

    // Devuelve inspecciones en un rango de fechas
    public List<Sanitary> obtenerPorRangoFechas(
            LocalDateTime desde, LocalDateTime hasta) {
        log.info("Obteniendo inspecciones entre {} y {}", desde, hasta);
        return sanitaryRepository.findByFechaInspeccionBetween(
                desde, hasta);
    }

    // Devuelve inspecciones de un vehículo del más reciente al más antiguo
    public List<Sanitary> obtenerPorPatenteOrdenadas(String patente) {
        log.info("Obteniendo inspecciones de {} ordenadas", patente);
        return sanitaryRepository
                .findByPatenteOrderByFechaInspeccionDesc(patente);
    }

    // Devuelve inspecciones PENDIENTES del más antiguo al más reciente
    // Útil para procesar inspecciones en orden de llegada
    public List<Sanitary> obtenerPendientesOrdenadas() {
        log.info("Obteniendo inspecciones pendientes ordenadas");
        return sanitaryRepository
                .findByResultadoOrderByFechaInspeccionAsc("PENDIENTE");
    }

    // Devuelve las últimas 10 inspecciones del sistema
    public List<Sanitary> obtenerUltimasInspecciones() {
        log.info("Obteniendo las últimas 10 inspecciones");
        return sanitaryRepository.findTop10ByOrderByIdDesc();
    }

    // Cuenta cuántas inspecciones hay con un resultado específico
    public long contarPorResultado(String resultado) {
        log.info("Contando inspecciones con resultado: {}", resultado);
        return sanitaryRepository.countByResultado(resultado);
    }

    // Verifica que el vehículo existe en Vehicle Service
    // Solo verifica existencia — NO verifica el estado del vehículo
    // A diferencia de Entry y Border Crossing que sí verifican el estado
    private void verificarVehiculoEnVehicleService(String patente) {
        try {
            log.info("Verificando vehículo {} en Vehicle Service",
                    patente);
            webClient.get()
                    .uri("/api/v1/vehicles/patente/{patente}", patente)
                    .retrieve()
                    .bodyToMono(VehicleResponseDTO.class)
                    .block();

            log.info("Vehículo {} verificado correctamente", patente);

        } catch (WebClientResponseException.NotFound e) {
            // Vehicle Service respondió HTTP 404 → el vehículo no existe
            log.warn("Vehículo {} no encontrado en Vehicle Service",
                    patente);
            throw new RuntimeException(
                    "El vehículo con patente " + patente
                            + " no existe en el sistema");

        } catch (Exception e) {
            // Cualquier otro error — Vehicle Service caído o sin conexión
            log.error("Error al comunicarse con Vehicle Service: {}",
                    e.getMessage());
            throw new RuntimeException(
                    "Error al verificar el vehículo. "
                            + "Verifique que Vehicle Service esté corriendo "
                            + "en el puerto 8083");
        }
    }
}