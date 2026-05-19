// service/SanitaryService.java
package com.example.SanitaryService.service;

import com.example.SanitaryService.dto.SanitaryDTO;
import com.example.SanitaryService.dto.VehicleResponseDTO;
import com.example.SanitaryService.model.Sanitary;
import com.example.SanitaryService.repository.SanitaryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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


    private final SanitaryRepository sanitaryRepository;
    private final WebClient webClient;

    // -------------------------------------------------------
    // CRUD BÁSICO
    // -------------------------------------------------------

    public List<Sanitary> obtenerTodas() {
        log.info("Obteniendo todas las inspecciones sanitarias");
        return sanitaryRepository.findAll();
    }

    public Sanitary obtenerPorId(Long id) {
        log.info("Buscando inspección con id: {}", id);
        return sanitaryRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Inspección con id {} no encontrada", id);
                    return new RuntimeException(
                            "Inspección no encontrada con id: " + id);
                });
    }

    // Registrar nueva inspección sanitaria
    public Sanitary registrar(SanitaryDTO dto) {
        log.info("Registrando inspección para vehículo: {}",
                dto.getPatente());

        // COMUNICACIÓN con Vehicle Service
        // Verificar que el vehículo existe
        verificarVehiculoEnVehicleService(dto.getPatente());

        // REGLA DE NEGOCIO: la fecha de inspección no puede ser futura
        if (dto.getFechaInspeccion().isAfter(LocalDateTime.now())) {
            log.warn("Fecha de inspección futura: {}",
                    dto.getFechaInspeccion());
            throw new RuntimeException(
                    "La fecha de inspección no puede ser futura");
        }

        // Mapeo DTO → Entidad
        Sanitary nueva = new Sanitary();
        nueva.setPatente(dto.getPatente().toUpperCase());
        nueva.setRutConductor(dto.getRutConductor());
        nueva.setRutInspector(dto.getRutInspector());
        nueva.setPasoFronterizo(dto.getPasoFronterizo());
        nueva.setFechaInspeccion(dto.getFechaInspeccion());
        nueva.setResultado("PENDIENTE");
        nueva.setObservaciones(dto.getObservaciones());

        Sanitary guardada = sanitaryRepository.save(nueva);
        log.info("Inspección registrada con id: {}", guardada.getId());
        return guardada;
    }

    // Aprobar inspección
    public Sanitary aprobar(Long id, String observaciones) {
        log.info("Aprobando inspección con id: {}", id);
        Sanitary inspeccion = obtenerPorId(id);

        // REGLA DE NEGOCIO: solo se pueden aprobar inspecciones PENDIENTES
        if (!inspeccion.getResultado().equals("PENDIENTE")) {
            log.warn("Inspección {} no está PENDIENTE. Resultado: {}",
                    id, inspeccion.getResultado());
            throw new RuntimeException(
                    "Solo se pueden aprobar inspecciones PENDIENTES. "
                            + "Resultado actual: " + inspeccion.getResultado());
        }

        inspeccion.setResultado("APROBADO");
        inspeccion.setObservaciones(observaciones);

        Sanitary actualizada = sanitaryRepository.save(inspeccion);
        log.info("Inspección {} aprobada correctamente", id);
        return actualizada;
    }

    // Rechazar inspección
    public Sanitary rechazar(Long id, String observaciones) {
        log.info("Rechazando inspección con id: {}", id);
        Sanitary inspeccion = obtenerPorId(id);

        // REGLA DE NEGOCIO: solo se pueden rechazar inspecciones PENDIENTES
        if (!inspeccion.getResultado().equals("PENDIENTE")) {
            log.warn("Inspección {} no está PENDIENTE. Resultado: {}",
                    id, inspeccion.getResultado());
            throw new RuntimeException(
                    "Solo se pueden rechazar inspecciones PENDIENTES. "
                            + "Resultado actual: " + inspeccion.getResultado());
        }

        inspeccion.setResultado("RECHAZADO");
        inspeccion.setObservaciones(observaciones);

        Sanitary actualizada = sanitaryRepository.save(inspeccion);
        log.info("Inspección {} rechazada correctamente", id);
        return actualizada;
    }

    // Eliminar inspección
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

    // -------------------------------------------------------
    // CONSULTAS DERIVADAS
    // -------------------------------------------------------

    public List<Sanitary> obtenerPorPatente(String patente) {
        log.info("Obteniendo inspecciones de la patente: {}", patente);
        return sanitaryRepository.findByPatente(patente);
    }

    public List<Sanitary> obtenerPorConductor(String rutConductor) {
        log.info("Obteniendo inspecciones del conductor: {}",
                rutConductor);
        return sanitaryRepository.findByRutConductor(rutConductor);
    }

    public List<Sanitary> obtenerPorInspector(String rutInspector) {
        log.info("Obteniendo inspecciones del inspector: {}",
                rutInspector);
        return sanitaryRepository.findByRutInspector(rutInspector);
    }

    public List<Sanitary> obtenerPorResultado(String resultado) {
        log.info("Obteniendo inspecciones con resultado: {}", resultado);
        return sanitaryRepository.findByResultado(resultado);
    }

    public List<Sanitary> obtenerPorPasoFronterizo(
            String pasoFronterizo) {
        log.info("Obteniendo inspecciones del paso: {}", pasoFronterizo);
        return sanitaryRepository.findByPasoFronterizo(pasoFronterizo);
    }

    public List<Sanitary> obtenerPorPatenteYResultado(
            String patente, String resultado) {
        log.info("Obteniendo inspecciones de {} con resultado {}",
                patente, resultado);
        return sanitaryRepository.findByPatenteAndResultado(
                patente, resultado);
    }

    public List<Sanitary> obtenerPorRangoFechas(
            LocalDateTime desde, LocalDateTime hasta) {
        log.info("Obteniendo inspecciones entre {} y {}", desde, hasta);
        return sanitaryRepository.findByFechaInspeccionBetween(
                desde, hasta);
    }

    public List<Sanitary> obtenerPorPatenteOrdenadas(String patente) {
        log.info("Obteniendo inspecciones de {} ordenadas", patente);
        return sanitaryRepository
                .findByPatenteOrderByFechaInspeccionDesc(patente);
    }

    public List<Sanitary> obtenerPendientesOrdenadas() {
        log.info("Obteniendo inspecciones pendientes ordenadas");
        return sanitaryRepository
                .findByResultadoOrderByFechaInspeccionAsc("PENDIENTE");
    }

    public List<Sanitary> obtenerUltimasInspecciones() {
        log.info("Obteniendo las últimas 10 inspecciones");
        return sanitaryRepository.findTop10ByOrderByIdDesc();
    }

    public long contarPorResultado(String resultado) {
        log.info("Contando inspecciones con resultado: {}", resultado);
        return sanitaryRepository.countByResultado(resultado);
    }

    // -------------------------------------------------------
    // COMUNICACIÓN CON VEHICLE SERVICE — WebClient
    // -------------------------------------------------------

    private void verificarVehiculoEnVehicleService(String patente) {
        try {
            log.info("Verificando vehículo {} en Vehicle Service",
                    patente);

            webClient.get()
                    // GET http://localhost:8083/api/v1/vehicles/patente/ABC123
                    .uri("/api/v1/vehicles/patente/{patente}", patente)
                    .retrieve()
                    .bodyToMono(VehicleResponseDTO.class)
                    .block();

            log.info("Vehículo {} verificado correctamente", patente);

        } catch (WebClientResponseException.NotFound e) {
            log.warn("Vehículo {} no encontrado en Vehicle Service",
                    patente);
            throw new RuntimeException(
                    "El vehículo con patente " + patente
                            + " no existe en el sistema");

        } catch (Exception e) {
            log.error("Error al comunicarse con Vehicle Service: {}",
                    e.getMessage());
            throw new RuntimeException(
                    "Error al verificar el vehículo. "
                            + "Verifique que Vehicle Service esté corriendo "
                            + "en el puerto 8083");
        }
    }
}