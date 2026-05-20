// Lógica de negocio para las alertas de deadlines
// Genera alertas manuales y automáticas según los días restantes
package com.example.DeadlineService.service;

import com.example.DeadlineService.dto.DeadlineAlertDTO;
import com.example.DeadlineService.model.Deadline;
import com.example.DeadlineService.model.DeadlineAlert;
import com.example.DeadlineService.repository.DeadlineAlertRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class DeadlineAlertService {

    // Accede a la tabla deadline_alerts en la BD
    private final DeadlineAlertRepository alertRepository;

    // Necesario para verificar que el deadline existe antes de crear una alerta
    private final DeadlineService deadlineService;

    // Devuelve todas las alertas de la BD
    public List<DeadlineAlert> obtenerTodas() {
        log.info("Obteniendo todas las alertas");
        return alertRepository.findAll();
    }

    // Busca una alerta por su id
    // Si no existe lanza RuntimeException → HTTP 404
    public DeadlineAlert obtenerPorId(Long id) {
        log.info("Buscando alerta con id: {}", id);
        return alertRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Alerta con id {} no encontrada", id);
                    return new RuntimeException(
                            "Alerta no encontrada con id: " + id);
                });
    }

    // Crea una alerta manual para un deadline específico
    public DeadlineAlert crear(DeadlineAlertDTO dto) {
        log.info("Creando alerta para deadline: {}",
                dto.getDeadlineId());

        // REGLA 1: el deadline debe existir en la BD
        // Si no existe → deadlineService lanza RuntimeException → HTTP 404
        Deadline deadline =
                deadlineService.obtenerPorId(dto.getDeadlineId());

        // REGLA 2: solo se crean alertas para deadlines ACTIVOS
        // Si está VENCIDO o CERRADO → no tiene sentido alertar
        if (!deadline.getEstado().equals("ACTIVO")) {
            log.warn("No se pueden crear alertas — deadline {}",
                    deadline.getEstado());
            throw new RuntimeException(
                    "Solo se pueden crear alertas para deadlines ACTIVOS");
        }

        // Mapeo DTO → Entidad
        DeadlineAlert alerta = new DeadlineAlert();
        alerta.setDeadline(deadline);               // FK hacia el deadline
        alerta.setMensaje(dto.getMensaje());        // texto de la alerta
        alerta.setDiasRestantes(dto.getDiasRestantes()); // días restantes al crear
        alerta.setTipoAlerta(dto.getTipoAlerta());  // AVISO, URGENTE o VENCIDO
        alerta.setEnviada(false);                   // inicia como no enviada
        alerta.setCreatedAt(LocalDateTime.now());   // fecha y hora actual

        DeadlineAlert guardada = alertRepository.save(alerta);
        log.info("Alerta creada con id: {}", guardada.getId());
        return guardada;
    }

    // Genera una alerta automáticamente calculando los días restantes
    public DeadlineAlert generarAlertaAutomatica(Long deadlineId) {
        log.info("Generando alerta automática para deadline: {}",
                deadlineId);

        // Busca el deadline para obtener su fecha límite
        Deadline deadline = deadlineService.obtenerPorId(deadlineId);

        // Calcula cuántos días quedan entre ahora y la fecha límite
        // Si el resultado es negativo → el deadline ya venció
        long diasRestantes = ChronoUnit.DAYS.between(
                LocalDateTime.now(), deadline.getFechaLimite());

        // Variables para el tipo y mensaje de la alerta
        String tipoAlerta;
        String mensaje;

        // Determina el tipo de alerta según los días restantes
        if (diasRestantes <= 0) {
            // El deadline ya venció → alerta VENCIDO
            tipoAlerta = "VENCIDO";
            mensaje = "El deadline del vehículo "
                    + deadline.getPatente()
                    + " ha vencido. Días de retraso: "
                    + Math.abs(diasRestantes);
        } else if (diasRestantes <= 15) {
            // Quedan 15 días o menos → alerta URGENTE
            tipoAlerta = "URGENTE";
            mensaje = "El deadline del vehículo "
                    + deadline.getPatente()
                    + " vence en " + diasRestantes + " días. "
                    + "Tipo: " + deadline.getTipo();
        } else {
            // Quedan más de 15 días → alerta AVISO
            tipoAlerta = "AVISO";
            mensaje = "El deadline del vehículo "
                    + deadline.getPatente()
                    + " vence en " + diasRestantes + " días. "
                    + "Tipo: " + deadline.getTipo();
        }

        // Crea y guarda la alerta automática en la BD
        DeadlineAlert alerta = new DeadlineAlert();
        alerta.setDeadline(deadline);
        alerta.setMensaje(mensaje);
        // (int) convierte el long a int para guardar en la BD
        alerta.setDiasRestantes((int) diasRestantes);
        alerta.setTipoAlerta(tipoAlerta);
        alerta.setEnviada(false);                   // inicia como no enviada
        alerta.setCreatedAt(LocalDateTime.now());

        DeadlineAlert guardada = alertRepository.save(alerta);
        log.info("Alerta {} generada para deadline {}",
                tipoAlerta, deadlineId);
        return guardada;
    }

    // Marca una alerta como enviada al Notification Service
    // Notification Service llama a este método después de procesarla
    public DeadlineAlert marcarComoEnviada(Long id) {
        log.info("Marcando alerta {} como enviada", id);
        DeadlineAlert alerta = obtenerPorId(id);
        // Cambia enviada de false a true
        alerta.setEnviada(true);
        DeadlineAlert actualizada = alertRepository.save(alerta);
        log.info("Alerta {} marcada como enviada", id);
        return actualizada;
    }

    // Elimina una alerta por su id
    // existsById verifica si existe antes de intentar eliminar
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

    // Devuelve todas las alertas de un deadline específico
    public List<DeadlineAlert> obtenerPorDeadline(Long deadlineId) {
        log.info("Obteniendo alertas del deadline: {}", deadlineId);
        return alertRepository.findByDeadlineId(deadlineId);
    }

    // Devuelve todas las alertas que aún no fueron enviadas
    // Notification Service consulta esto para saber qué procesar
    public List<DeadlineAlert> obtenerNoEnviadas() {
        log.info("Obteniendo alertas no enviadas");
        return alertRepository.findByEnviadaFalse();
    }

    // Devuelve todas las alertas que ya fueron enviadas
    public List<DeadlineAlert> obtenerEnviadas() {
        log.info("Obteniendo alertas enviadas");
        return alertRepository.findByEnviadaTrue();
    }

    // Devuelve alertas URGENTES que aún no fueron enviadas
    // Son los deadlines que vencen en 15 días o menos
    public List<DeadlineAlert> obtenerUrgentesNoEnviadas() {
        log.info("Obteniendo alertas urgentes no enviadas");
        return alertRepository
                .findByTipoAlertaAndEnviadaFalse("URGENTE");
    }

    // Devuelve alertas VENCIDO que aún no fueron enviadas
    // Son los deadlines que ya expiraron
    public List<DeadlineAlert> obtenerVencidasNoEnviadas() {
        log.info("Obteniendo alertas vencidas no enviadas");
        return alertRepository
                .findByTipoAlertaAndEnviadaFalse("VENCIDO");
    }

    // Devuelve alertas de un deadline ordenadas por días restantes
    // del que tiene menos días al que tiene más
    public List<DeadlineAlert> obtenerPorDeadlineOrdenadas(
            Long deadlineId) {
        log.info("Obteniendo alertas del deadline {} ordenadas",
                deadlineId);
        return alertRepository
                .findByDeadlineIdOrderByDiasRestantesAsc(deadlineId);
    }

    // Devuelve las últimas 10 alertas registradas en el sistema
    public List<DeadlineAlert> obtenerUltimasAlertas() {
        log.info("Obteniendo las últimas 10 alertas");
        return alertRepository.findTop10ByOrderByIdDesc();
    }

    // Cuenta cuántas alertas no han sido enviadas todavía
    // Devuelve un número no una lista
    public long contarNoEnviadas() {
        log.info("Contando alertas no enviadas");
        return alertRepository.countByEnviadaFalse();
    }
}