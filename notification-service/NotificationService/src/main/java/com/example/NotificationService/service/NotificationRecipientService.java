// service/NotificationRecipientService.java
package com.example.NotificationService.service;

import com.example.NotificationService.dto.NotificationRecipientDTO;
import com.example.NotificationService.model.Notification;
import com.example.NotificationService.model.NotificationRecipient;
import com.example.NotificationService.repository.NotificationRecipientRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationRecipientService {


    private final NotificationRecipientRepository recipientRepository;
    private final NotificationService notificationService;

    // -------------------------------------------------------
    // CRUD BÁSICO
    // -------------------------------------------------------

    public List<NotificationRecipient> obtenerTodos() {
        log.info("Obteniendo todos los destinatarios");
        return recipientRepository.findAll();
    }

    public NotificationRecipient obtenerPorId(Long id) {
        log.info("Buscando destinatario con id: {}", id);
        return recipientRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Destinatario con id {} no encontrado", id);
                    return new RuntimeException(
                            "Destinatario no encontrado con id: " + id);
                });
    }

    // Agregar destinatario a una notificación
    public NotificationRecipient agregar(NotificationRecipientDTO dto) {
        log.info("Agregando destinatario a notificación: {}",
                dto.getNotificationId());

        // REGLA DE NEGOCIO 1: la notificación debe existir
        Notification notificacion =
                notificationService.obtenerPorId(dto.getNotificationId());

        // REGLA DE NEGOCIO 2: solo se agregan destinatarios
        // a notificaciones PENDIENTES
        if (!notificacion.getEstado().equals("PENDIENTE")) {
            log.warn("No se pueden agregar destinatarios — estado: {}",
                    notificacion.getEstado());
            throw new RuntimeException(
                    "Solo se pueden agregar destinatarios "
                            + "a notificaciones PENDIENTES");
        }

        NotificationRecipient destinatario = new NotificationRecipient();
        destinatario.setNotification(notificacion);
        destinatario.setRutDestinatario(dto.getRutDestinatario());
        destinatario.setEmail(dto.getEmail());
        destinatario.setNombre(dto.getNombre());
        destinatario.setLeida(false);

        NotificationRecipient guardado =
                recipientRepository.save(destinatario);
        log.info("Destinatario agregado con id: {}", guardado.getId());
        return guardado;
    }

    // Marcar notificación como leída
    public NotificationRecipient marcarComoLeida(Long id) {
        log.info("Marcando notificación como leída para destinatario {}",
                id);
        NotificationRecipient destinatario = obtenerPorId(id);
        destinatario.setLeida(true);
        destinatario.setLeidaAt(LocalDateTime.now());
        NotificationRecipient actualizado =
                recipientRepository.save(destinatario);
        log.info("Notificación marcada como leída para destinatario {}",
                id);
        return actualizado;
    }

    // Eliminar destinatario
    public void eliminar(Long id) {
        log.info("Eliminando destinatario con id: {}", id);
        if (!recipientRepository.existsById(id)) {
            log.warn("Destinatario con id {} no encontrado", id);
            throw new RuntimeException(
                    "Destinatario no encontrado con id: " + id);
        }
        recipientRepository.deleteById(id);
        log.info("Destinatario {} eliminado correctamente", id);
    }

    // -------------------------------------------------------
    // CONSULTAS DERIVADAS
    // -------------------------------------------------------

    public List<NotificationRecipient> obtenerPorNotificacion(
            Long notificationId) {
        log.info("Obteniendo destinatarios de notificación: {}",
                notificationId);
        return recipientRepository.findByNotificationId(notificationId);
    }

    public List<NotificationRecipient> obtenerNoLeidosPorNotificacion(
            Long notificationId) {
        log.info("Obteniendo destinatarios no leídos: {}",
                notificationId);
        return recipientRepository
                .findByNotificationIdAndLeidaFalse(notificationId);
    }

    public List<NotificationRecipient> obtenerPorDestinatario(
            String rutDestinatario) {
        log.info("Obteniendo notificaciones del destinatario: {}",
                rutDestinatario);
        return recipientRepository
                .findByRutDestinatario(rutDestinatario);
    }

    public List<NotificationRecipient> obtenerNoLeidasPorDestinatario(
            String rutDestinatario) {
        log.info("Obteniendo no leídas del destinatario: {}",
                rutDestinatario);
        return recipientRepository
                .findByRutDestinatarioAndLeidaFalse(rutDestinatario);
    }

    public List<NotificationRecipient> obtenerNoLeidas() {
        log.info("Obteniendo todas las notificaciones no leídas");
        return recipientRepository.findByLeidaFalse();
    }

    public List<NotificationRecipient> obtenerUltimosDestinatarios() {
        log.info("Obteniendo los últimos 10 destinatarios");
        return recipientRepository.findTop10ByOrderByIdDesc();
    }

    public long contarNoLeidasPorDestinatario(String rutDestinatario) {
        log.info("Contando no leídas del destinatario: {}",
                rutDestinatario);
        return recipientRepository
                .countByRutDestinatarioAndLeidaFalse(rutDestinatario);
    }
}