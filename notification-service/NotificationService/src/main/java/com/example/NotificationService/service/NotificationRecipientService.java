// Lógica de negocio para los destinatarios de notificaciones
// Un destinatario = persona que debe recibir una notificación específica
package com.example.NotificationService.service;

import com.example.NotificationService.dto.NotificationRecipientDTO;
import com.example.NotificationService.model.Notification;
import com.example.NotificationService.model.NotificationRecipient;
import com.example.NotificationService.repository.NotificationRecipientRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationRecipientService {

    // Accede a la tabla notification_recipients en la BD
    private final NotificationRecipientRepository recipientRepository;

    // Necesario para verificar que la notificación existe
    // antes de agregar un destinatario
    private final NotificationService notificationService;

    // Devuelve todos los destinatarios de la BD
    public List<NotificationRecipient> obtenerTodos() {
        log.info("Obteniendo todos los destinatarios");
        return recipientRepository.findAll();
    }

    // Busca un destinatario por su id
    // Si no existe lanza RuntimeException → HTTP 404
    public NotificationRecipient obtenerPorId(Long id) {
        log.info("Buscando destinatario con id: {}", id);
        return recipientRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Destinatario con id {} no encontrado", id);
                    return new RuntimeException(
                            "Destinatario no encontrado con id: " + id);
                });
    }

    // Agrega un nuevo destinatario a una notificación existente
    public NotificationRecipient agregar(NotificationRecipientDTO dto) {
        log.info("Agregando destinatario a notificación: {}",
                dto.getNotificationId());

        // REGLA 1: la notificación debe existir en la BD
        // Si no existe → notificationService lanza RuntimeException → HTTP 404
        Notification notificacion =
                notificationService.obtenerPorId(dto.getNotificationId());

        // REGLA 2: solo se agregan destinatarios a notificaciones PENDIENTES
        // Si ya fue ENVIADA o tuvo ERROR → no tiene sentido agregar destinatarios
        if (!notificacion.getEstado().equals("PENDIENTE")) {
            log.warn("No se pueden agregar destinatarios — estado: {}",
                    notificacion.getEstado());
            throw new RuntimeException(
                    "Solo se pueden agregar destinatarios "
                            + "a notificaciones PENDIENTES");
        }

        // Mapeo DTO → Entidad
        NotificationRecipient destinatario = new NotificationRecipient();
        destinatario.setNotification(notificacion);           // FK hacia la notificación
        destinatario.setRutDestinatario(dto.getRutDestinatario()); // Ej: "12345678-9"
        destinatario.setEmail(dto.getEmail());                // Ej: "juan@gmail.com"
        destinatario.setNombre(dto.getNombre());              // Ej: "Juan Pérez"
        destinatario.setLeida(false);                         // inicia como no leída

        // Guarda en la BD y retorna el destinatario con su id generado
        NotificationRecipient guardado =
                recipientRepository.save(destinatario);
        log.info("Destinatario agregado con id: {}", guardado.getId());
        return guardado;
    }

    // Marca que un destinatario leyó la notificación
    // Cambia leida a true y guarda la fecha exacta de lectura
    public NotificationRecipient marcarComoLeida(Long id) {
        log.info("Marcando notificación como leída para destinatario {}",
                id);
        NotificationRecipient destinatario = obtenerPorId(id);
        destinatario.setLeida(true);
        // Guarda la fecha y hora exacta en que el destinatario leyó la notificación
        destinatario.setLeidaAt(LocalDateTime.now());
        NotificationRecipient actualizado =
                recipientRepository.save(destinatario);
        log.info("Notificación marcada como leída para destinatario {}",
                id);
        return actualizado;
    }

    // Elimina un destinatario por su id
    // existsById verifica si existe antes de intentar eliminar
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

    // Devuelve todos los destinatarios de una notificación específica
    public List<NotificationRecipient> obtenerPorNotificacion(
            Long notificationId) {
        log.info("Obteniendo destinatarios de notificación: {}",
                notificationId);
        return recipientRepository.findByNotificationId(notificationId);
    }

    // Devuelve los destinatarios que AÚN NO leyeron una notificación
    public List<NotificationRecipient> obtenerNoLeidosPorNotificacion(
            Long notificationId) {
        log.info("Obteniendo destinatarios no leídos: {}",
                notificationId);
        return recipientRepository
                .findByNotificationIdAndLeidaFalse(notificationId);
    }

    // Devuelve todas las notificaciones asignadas a un destinatario
    // Incluye leídas y no leídas
    public List<NotificationRecipient> obtenerPorDestinatario(
            String rutDestinatario) {
        log.info("Obteniendo notificaciones del destinatario: {}",
                rutDestinatario);
        return recipientRepository
                .findByRutDestinatario(rutDestinatario);
    }

    // Devuelve solo las notificaciones NO leídas de un destinatario
    // Útil para mostrar la bandeja de notificaciones pendientes de un usuario
    public List<NotificationRecipient> obtenerNoLeidasPorDestinatario(
            String rutDestinatario) {
        log.info("Obteniendo no leídas del destinatario: {}",
                rutDestinatario);
        return recipientRepository
                .findByRutDestinatarioAndLeidaFalse(rutDestinatario);
    }

    // Devuelve TODAS las notificaciones no leídas del sistema
    // Sin importar a quién pertenecen
    public List<NotificationRecipient> obtenerNoLeidas() {
        log.info("Obteniendo todas las notificaciones no leídas");
        return recipientRepository.findByLeidaFalse();
    }

    // Devuelve los últimos 10 destinatarios registrados en el sistema
    public List<NotificationRecipient> obtenerUltimosDestinatarios() {
        log.info("Obteniendo los últimos 10 destinatarios");
        return recipientRepository.findTop10ByOrderByIdDesc();
    }

    // Cuenta cuántas notificaciones NO leídas tiene un destinatario
    public long contarNoLeidasPorDestinatario(String rutDestinatario) {
        log.info("Contando no leídas del destinatario: {}",
                rutDestinatario);
        return recipientRepository
                .countByRutDestinatarioAndLeidaFalse(rutDestinatario);
    }
}