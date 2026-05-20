// Accede a la tabla notification_recipients en la base de datos
// Spring genera el SQL automáticamente leyendo el nombre de cada método
package com.example.NotificationService.repository;

import com.example.NotificationService.model.NotificationRecipient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface NotificationRecipientRepository
        extends JpaRepository<NotificationRecipient, Long> {

    // Devuelve todos los destinatarios de una notificación específica
    List<NotificationRecipient> findByNotificationId(Long notificationId);

    // Devuelve los destinatarios que AÚN NO leyeron una notificación
    List<NotificationRecipient> findByNotificationIdAndLeidaFalse(
            Long notificationId);

    // Devuelve los destinatarios que YA leyeron una notificación
    List<NotificationRecipient> findByNotificationIdAndLeidaTrue(
            Long notificationId);

    // Devuelve todas las notificaciones asignadas a un destinatario específico
    List<NotificationRecipient> findByRutDestinatario(
            String rutDestinatario);

    // Devuelve solo las notificaciones NO leídas de un destinatario
    // Útil para mostrar la bandeja de notificaciones pendientes
    List<NotificationRecipient> findByRutDestinatarioAndLeidaFalse(
            String rutDestinatario);

    // Devuelve TODAS las notificaciones no leídas del sistema
    // Sin importar a quién pertenecen
    List<NotificationRecipient> findByLeidaFalse();

    // Devuelve TODAS las notificaciones ya leídas del sistema
    List<NotificationRecipient> findByLeidaTrue();

    // Devuelve notificaciones de un destinatario del más reciente al más antiguo
    // Útil para ver el historial de notificaciones de un usuario
    List<NotificationRecipient> findByRutDestinatarioOrderByIdDesc(
            String rutDestinatario);

    // Devuelve los últimos 10 destinatarios registrados en el sistema
    // Útil para monitorear los últimos destinatarios agregados
    List<NotificationRecipient> findTop10ByOrderByIdDesc();

    // Cuenta cuántas notificaciones NO leídas tiene un destinatario
    long countByRutDestinatarioAndLeidaFalse(String rutDestinatario);
}