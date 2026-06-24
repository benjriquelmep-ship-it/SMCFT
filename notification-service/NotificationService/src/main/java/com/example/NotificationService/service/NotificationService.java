package com.example.NotificationService.service;

import com.example.NotificationService.dto.DeadlineAlertResponseDTO;
import com.example.NotificationService.dto.NotificationDTO;
import com.example.NotificationService.model.Notification;
import com.example.NotificationService.model.NotificationRecipient;
import com.example.NotificationService.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final WebClient webClient;
    private final WebClient userServiceWebClient;

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

    public Notification crear(NotificationDTO dto) {
        log.info("Creando notificación: {}", dto.getTitulo());

        Notification nueva = new Notification();
        nueva.setTitulo(dto.getTitulo());
        nueva.setMensaje(dto.getMensaje());
        nueva.setTipo(dto.getTipo());
        nueva.setDeadlineAlertId(dto.getDeadlineAlertId());
        nueva.setEstado("PENDIENTE");
        nueva.setCreatedAt(LocalDateTime.now());

        String rut = dto.getDestinatarioRut();
        if (rut != null && !rut.isBlank()) {
            try {
                log.info("Buscando destinatario con RUT: {}", rut);
                Map<String, Object> userData = userServiceWebClient.get()
                        .uri("/api/v1/users/rut/{rut}", rut)
                        .retrieve()
                        .bodyToMono(Map.class)
                        .block();

                if (userData != null) {
                    NotificationRecipient destinatario = new NotificationRecipient();
                    destinatario.setNotification(nueva);
                    destinatario.setRutDestinatario(rut);
                    destinatario.setEmail((String) userData.getOrDefault("email", ""));
                    destinatario.setNombre((String) userData.getOrDefault("nombre", ""));
                    destinatario.setLeida(false);

                    List<NotificationRecipient> destinatarios = new ArrayList<>();
                    destinatarios.add(destinatario);
                    nueva.setDestinatarios(destinatarios);
                    log.info("Destinatario asignado: {} - {}", rut, destinatario.getNombre());
                }
            } catch (Exception e) {
                log.error("Error al obtener datos del usuario con RUT {}: {}", rut, e.getMessage());
            }
        }

        Notification guardada = notificationRepository.save(nueva);
        log.info("Notificación creada con id: {}", guardada.getId());
        return guardada;
    }

    public Notification actualizar(Long id, NotificationDTO dto) {
        log.info("Actualizando notificación {}", id);
        Notification notificacion = obtenerPorId(id);
        notificacion.setTitulo(dto.getTitulo());
        notificacion.setMensaje(dto.getMensaje());
        notificacion.setTipo(dto.getTipo());

        if (notificacion.getDestinatarios() != null) {
            notificacion.getDestinatarios().clear();
        }

        String rut = dto.getDestinatarioRut();
        if (rut != null && !rut.isBlank()) {
            try {
                Map<String, Object> userData = userServiceWebClient.get()
                        .uri("/api/v1/users/rut/{rut}", rut)
                        .retrieve()
                        .bodyToMono(Map.class)
                        .block();
                if (userData != null) {
                    NotificationRecipient destinatario = new NotificationRecipient();
                    destinatario.setNotification(notificacion);
                    destinatario.setRutDestinatario(rut);
                    destinatario.setEmail((String) userData.getOrDefault("email", ""));
                    destinatario.setNombre((String) userData.getOrDefault("nombre", ""));
                    destinatario.setLeida(false);
                    notificacion.getDestinatarios().add(destinatario);
                    log.info("Destinatario actualizado: {} - {}", rut, destinatario.getNombre());
                }
            } catch (Exception e) {
                log.error("Error al obtener datos del usuario con RUT {}: {}", rut, e.getMessage());
            }
        }

        Notification actualizada = notificationRepository.save(notificacion);
        log.info("Notificación {} actualizada", id);
        return actualizada;
    }

    public Notification marcarComoEnviada(Long id) {
        log.info("Marcando notificación {} como enviada", id);
        Notification notificacion = obtenerPorId(id);

        // REGLA: solo se pueden enviar notificaciones PENDIENTES
        // Si ya fue ENVIADA o tuvo ERROR → no se puede volver a enviar
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

    public Notification marcarComoError(Long id) {
        log.info("Marcando notificación {} como error", id);
        Notification notificacion = obtenerPorId(id);
        notificacion.setEstado("ERROR");
        Notification actualizada =
                notificationRepository.save(notificacion);
        log.info("Notificación {} marcada como error", id);
        return actualizada;
    }

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

    public List<Notification> generarDesdeAlertas() {
        log.info("Generando notificaciones desde Deadline Service");

        try {
            List<DeadlineAlertResponseDTO> alertas =
                    webClient.get()
                            .uri("/api/v1/deadline-alerts/no-enviadas")
                            .retrieve()
                            .bodyToFlux(DeadlineAlertResponseDTO.class)
                            .collectList()
                            .block();

            if (alertas == null || alertas.isEmpty()) {
                log.info("No hay alertas pendientes");
                return List.of();
            }

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

    private void marcarAlertaComoEnviada(Long alertaId) {
        try {
            log.info("Marcando alerta {} como enviada en Deadline Service",
                    alertaId);

            webClient.patch()
                    .uri("/api/v1/deadline-alerts/{id}/enviada", alertaId)
                    .retrieve()
                    .bodyToMono(Void.class)
                    .block();

            log.info("Alerta {} marcada como enviada", alertaId);

        } catch (Exception e) {
            // Si falla no lanza excepción — solo registra el error
            // La notificación ya fue creada y no debe deshacerse
            // por un error al marcar la alerta
            log.error("Error al marcar alerta {} como enviada: {}",
                    alertaId, e.getMessage());
        }
    }
}