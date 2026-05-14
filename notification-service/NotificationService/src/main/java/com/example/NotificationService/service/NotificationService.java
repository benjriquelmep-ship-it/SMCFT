// service/NotificationService.java
package com.example.NotificationService.service;

import com.example.NotificationService.dto.DeadlineAlertResponseDTO;
import com.example.NotificationService.dto.NotificationDTO;
import com.example.NotificationService.model.Notification;
import com.example.NotificationService.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private static final Logger log =
            LoggerFactory.getLogger(NotificationService.class);

    private final NotificationRepository notificationRepository;
    private final WebClient webClient;

    // -------------------------------------------------------
    // CRUD BÁSICO
    // -------------------------------------------------------

    public List<Notification> obtenerTodas() {
        log.info("Obteniendo todas las notificaciones");
        return notificationRepository.findAll();
    }

    public Notification obtenerPorId(Long id) {
        log.info("Buscando notificación con id: {}", id);
        return notificationRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Notificación con id {} no encontrada", id);
                    return new RuntimeException(
                            "Notificación no encontrada con id: " + id);
                });
    }

    // Crear notificación manual
    public Notification crear(NotificationDTO dto) {
        log.info("Creando notificación: {}", dto.getTitulo());

        Notification nueva = new Notification();
        nueva.setTitulo(dto.getTitulo());
        nueva.setMensaje(dto.getMensaje());
        nueva.setTipo(dto.getTipo());
        nueva.setDeadlineAlertId(dto.getDeadlineAlertId());
        nueva.setEstado("PENDIENTE");
        nueva.setCreatedAt(LocalDateTime.now());

        Notification guardada = notificationRepository.save(nueva);
        log.info("Notificación creada con id: {}", guardada.getId());
        return guardada;
    }

    // Marcar notificación como enviada
    public Notification marcarComoEnviada(Long id) {
        log.info("Marcando notificación {} como enviada", id);
        Notification notificacion = obtenerPorId(id);

        // REGLA DE NEGOCIO: solo se envían notificaciones PENDIENTES
        if (!notificacion.getEstado().equals("PENDIENTE")) {
            log.warn("Notificación {} no está PENDIENTE. Estado: {}",
                    id, notificacion.getEstado());
            throw new RuntimeException(
                    "Solo se pueden enviar notificaciones PENDIENTES. "
                            + "Estado actual: " + notificacion.getEstado());
        }

        notificacion.setEstado("ENVIADA");
        notificacion.setEnviadaAt(LocalDateTime.now());
        Notification actualizada =
                notificationRepository.save(notificacion);
        log.info("Notificación {} marcada como enviada", id);
        return actualizada;
    }

    // Marcar notificación como error
    public Notification marcarComoError(Long id) {
        log.info("Marcando notificación {} como error", id);
        Notification notificacion = obtenerPorId(id);
        notificacion.setEstado("ERROR");
        Notification actualizada =
                notificationRepository.save(notificacion);
        log.info("Notificación {} marcada como error", id);
        return actualizada;
    }

    // Eliminar notificación
    public void eliminar(Long id) {
        log.info("Eliminando notificación con id: {}", id);
        if (!notificationRepository.existsById(id)) {
            log.warn("Notificación con id {} no encontrada", id);
            throw new RuntimeException(
                    "Notificación no encontrada con id: " + id);
        }
        notificationRepository.deleteById(id);
        log.info("Notificación {} eliminada correctamente", id);
    }

    // Generar notificaciones desde alertas del Deadline Service
    public List<Notification> generarDesdeAlertas() {
        log.info("Generando notificaciones desde Deadline Service");

        try {
            // Consultar alertas no enviadas de Deadline Service
            List<DeadlineAlertResponseDTO> alertas =
                    webClient.get()
                            // GET http://localhost:8087/api/v1/deadline-alerts/no-enviadas
                            .uri("/api/v1/deadline-alerts/no-enviadas")
                            .retrieve()
                            .bodyToFlux(DeadlineAlertResponseDTO.class)
                            .collectList()
                            .block();

            if (alertas == null || alertas.isEmpty()) {
                log.info("No hay alertas pendientes");
                return List.of();
            }

            // Crear una notificación por cada alerta
            alertas.forEach(alerta -> {
                Notification nueva = new Notification();
                nueva.setTitulo("Alerta de Deadline: "
                        + alerta.getTipoAlerta());
                nueva.setMensaje(alerta.getMensaje());
                nueva.setTipo("ALERTA_" + alerta.getTipoAlerta());
                nueva.setDeadlineAlertId(alerta.getId());
                nueva.setEstado("PENDIENTE");
                nueva.setCreatedAt(LocalDateTime.now());
                notificationRepository.save(nueva);

                // Marcar la alerta como enviada en Deadline Service
                marcarAlertaComoEnviada(alerta.getId());

                log.info("Notificación creada para alerta {}",
                        alerta.getId());
            });

            log.info("{} notificaciones generadas", alertas.size());
            return notificationRepository
                    .findByEstadoOrderByIdDesc("PENDIENTE");

        } catch (Exception e) {
            log.error("Error al comunicarse con Deadline Service: {}",
                    e.getMessage());
            throw new RuntimeException(
                    "Error al obtener alertas de Deadline Service. "
                            + "Verifique que Deadline Service esté corriendo "
                            + "en el puerto 8087");
        }
    }

    // -------------------------------------------------------
    // CONSULTAS DERIVADAS
    // -------------------------------------------------------

    public List<Notification> obtenerPorTipo(String tipo) {
        log.info("Obteniendo notificaciones de tipo: {}", tipo);
        return notificationRepository.findByTipo(tipo);
    }

    public List<Notification> obtenerPorEstado(String estado) {
        log.info("Obteniendo notificaciones con estado: {}", estado);
        return notificationRepository.findByEstado(estado);
    }

    public List<Notification> obtenerPendientes() {
        log.info("Obteniendo notificaciones pendientes");
        return notificationRepository
                .findByEstadoOrderByIdDesc("PENDIENTE");
    }

    public List<Notification> obtenerEnviadas() {
        log.info("Obteniendo notificaciones enviadas");
        return notificationRepository
                .findByEstadoOrderByIdDesc("ENVIADA");
    }

    public List<Notification> buscarPorTitulo(String texto) {
        log.info("Buscando notificaciones con título: {}", texto);
        return notificationRepository
                .findByTituloContainingIgnoreCase(texto);
    }

    public List<Notification> obtenerUltimasNotificaciones() {
        log.info("Obteniendo las últimas 10 notificaciones");
        return notificationRepository.findTop10ByOrderByIdDesc();
    }

    public long contarPorEstado(String estado) {
        log.info("Contando notificaciones con estado: {}", estado);
        return notificationRepository.countByEstado(estado);
    }

    public long contarPorTipo(String tipo) {
        log.info("Contando notificaciones de tipo: {}", tipo);
        return notificationRepository.countByTipo(tipo);
    }

    // -------------------------------------------------------
    // COMUNICACIÓN CON DEADLINE SERVICE — WebClient
    // -------------------------------------------------------

    private void marcarAlertaComoEnviada(Long alertaId) {
        try {
            log.info("Marcando alerta {} como enviada en Deadline Service",
                    alertaId);

            webClient.patch()
                    // PATCH http://localhost:8087/api/v1/deadline-alerts/1/enviada
                    .uri("/api/v1/deadline-alerts/{id}/enviada", alertaId)
                    .retrieve()
                    .bodyToMono(Void.class)
                    .block();

            log.info("Alerta {} marcada como enviada", alertaId);

        } catch (Exception e) {
            log.error("Error al marcar alerta {} como enviada: {}",
                    alertaId, e.getMessage());
        }
    }
}