// service/DeadlineAlertService.java
package com.example.DeadlineService.service;

import com.example.DeadlineService.dto.DeadlineAlertDTO;
import com.example.DeadlineService.model.Deadline;
import com.example.DeadlineService.model.DeadlineAlert;
import com.example.DeadlineService.repository.DeadlineAlertRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class DeadlineAlertService {


    private final DeadlineAlertRepository alertRepository;
    private final DeadlineService deadlineService;

    // -------------------------------------------------------
    // CRUD BÁSICO
    // -------------------------------------------------------

    public List<DeadlineAlert> obtenerTodas() {
        log.info("Obteniendo todas las alertas");
        return alertRepository.findAll();
    }

    public DeadlineAlert obtenerPorId(Long id) {
        log.info("Buscando alerta con id: {}", id);
        return alertRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Alerta con id {} no encontrada", id);
                    return new RuntimeException(
                            "Alerta no encontrada con id: " + id);
                });
    }

    public DeadlineAlert crear(DeadlineAlertDTO dto) {
        log.info("Creando alerta para deadline: {}",
                dto.getDeadlineId());

        // REGLA DE NEGOCIO 1: el deadline debe existir
        Deadline deadline =
                deadlineService.obtenerPorId(dto.getDeadlineId());

        // REGLA DE NEGOCIO 2: solo para deadlines ACTIVOS
        if (!deadline.getEstado().equals("ACTIVO")) {
            log.warn("No se pueden crear alertas — deadline {}",
                    deadline.getEstado());
            throw new RuntimeException(
                    "Solo se pueden crear alertas para deadlines ACTIVOS");
        }

        DeadlineAlert alerta = new DeadlineAlert();
        alerta.setDeadline(deadline);
        alerta.setMensaje(dto.getMensaje());
        alerta.setDiasRestantes(dto.getDiasRestantes());
        alerta.setTipoAlerta(dto.getTipoAlerta());
        alerta.setEnviada(false);
        alerta.setCreatedAt(LocalDateTime.now());

        DeadlineAlert guardada = alertRepository.save(alerta);
        log.info("Alerta creada con id: {}", guardada.getId());
        return guardada;
    }

    public DeadlineAlert generarAlertaAutomatica(Long deadlineId) {
        log.info("Generando alerta automática para deadline: {}",
                deadlineId);

        Deadline deadline = deadlineService.obtenerPorId(deadlineId);

        long diasRestantes = ChronoUnit.DAYS.between(
                LocalDateTime.now(), deadline.getFechaLimite());

        String tipoAlerta;
        String mensaje;

        if (diasRestantes <= 0) {
            tipoAlerta = "VENCIDO";
            mensaje = "El deadline del vehículo "
                    + deadline.getPatente()
                    + " ha vencido. Días de retraso: "
                    + Math.abs(diasRestantes);
        } else if (diasRestantes <= 15) {
            tipoAlerta = "URGENTE";
            mensaje = "El deadline del vehículo "
                    + deadline.getPatente()
                    + " vence en " + diasRestantes + " días. "
                    + "Tipo: " + deadline.getTipo();
        } else {
            tipoAlerta = "AVISO";
            mensaje = "El deadline del vehículo "
                    + deadline.getPatente()
                    + " vence en " + diasRestantes + " días. "
                    + "Tipo: " + deadline.getTipo();
        }

        DeadlineAlert alerta = new DeadlineAlert();
        alerta.setDeadline(deadline);
        alerta.setMensaje(mensaje);
        alerta.setDiasRestantes((int) diasRestantes);
        alerta.setTipoAlerta(tipoAlerta);
        alerta.setEnviada(false);
        alerta.setCreatedAt(LocalDateTime.now());

        DeadlineAlert guardada = alertRepository.save(alerta);
        log.info("Alerta {} generada para deadline {}",
                tipoAlerta, deadlineId);
        return guardada;
    }

    public DeadlineAlert marcarComoEnviada(Long id) {
        log.info("Marcando alerta {} como enviada", id);
        DeadlineAlert alerta = obtenerPorId(id);
        alerta.setEnviada(true);
        DeadlineAlert actualizada = alertRepository.save(alerta);
        log.info("Alerta {} marcada como enviada", id);
        return actualizada;
    }

    public void eliminar(Long id) {
        log.info("Eliminando alerta con id: {}", id);
        if (!alertRepository.existsById(id)) {
            log.warn("Alerta con id {} no encontrada", id);
            throw new RuntimeException(
                    "Alerta no encontrada con id: " + id);
        }
        alertRepository.deleteById(id);
        log.info("Alerta {} eliminada correctamente", id);
    }

    // -------------------------------------------------------
    // CONSULTAS DERIVADAS
    // -------------------------------------------------------

    public List<DeadlineAlert> obtenerPorDeadline(Long deadlineId) {
        log.info("Obteniendo alertas del deadline: {}", deadlineId);
        return alertRepository.findByDeadlineId(deadlineId);
    }

    public List<DeadlineAlert> obtenerNoEnviadas() {
        log.info("Obteniendo alertas no enviadas");
        return alertRepository.findByEnviadaFalse();
    }

    public List<DeadlineAlert> obtenerEnviadas() {
        log.info("Obteniendo alertas enviadas");
        return alertRepository.findByEnviadaTrue();
    }

    public List<DeadlineAlert> obtenerUrgentesNoEnviadas() {
        log.info("Obteniendo alertas urgentes no enviadas");
        return alertRepository
                .findByTipoAlertaAndEnviadaFalse("URGENTE");
    }

    public List<DeadlineAlert> obtenerVencidasNoEnviadas() {
        log.info("Obteniendo alertas vencidas no enviadas");
        return alertRepository
                .findByTipoAlertaAndEnviadaFalse("VENCIDO");
    }

    public List<DeadlineAlert> obtenerPorDeadlineOrdenadas(
            Long deadlineId) {
        log.info("Obteniendo alertas del deadline {} ordenadas",
                deadlineId);
        return alertRepository
                .findByDeadlineIdOrderByDiasRestantesAsc(deadlineId);
    }

    public List<DeadlineAlert> obtenerUltimasAlertas() {
        log.info("Obteniendo las últimas 10 alertas");
        return alertRepository.findTop10ByOrderByIdDesc();
    }

    public long contarNoEnviadas() {
        log.info("Contando alertas no enviadas");
        return alertRepository.countByEnviadaFalse();
    }
}