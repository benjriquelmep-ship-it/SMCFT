// repository/NotificationRecipientRepository.java
package com.example.NotificationService.repository;

import com.example.NotificationService.model.NotificationRecipient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface NotificationRecipientRepository
        extends JpaRepository<NotificationRecipient, Long> {

    // Spring genera: SELECT * FROM notification_recipients
    //                WHERE notification_id = ?
    List<NotificationRecipient> findByNotificationId(Long notificationId);

    // Spring genera: SELECT * FROM notification_recipients
    //                WHERE notification_id = ? AND leida = false
    List<NotificationRecipient> findByNotificationIdAndLeidaFalse(
            Long notificationId);

    // Spring genera: SELECT * FROM notification_recipients
    //                WHERE notification_id = ? AND leida = true
    List<NotificationRecipient> findByNotificationIdAndLeidaTrue(
            Long notificationId);

    // Spring genera: SELECT * FROM notification_recipients
    //                WHERE rut_destinatario = ?
    List<NotificationRecipient> findByRutDestinatario(
            String rutDestinatario);

    // Spring genera: SELECT * FROM notification_recipients
    //                WHERE rut_destinatario = ? AND leida = false
    List<NotificationRecipient> findByRutDestinatarioAndLeidaFalse(
            String rutDestinatario);

    // Spring genera: SELECT * FROM notification_recipients WHERE leida = false
    List<NotificationRecipient> findByLeidaFalse();
    List<NotificationRecipient> findByLeidaTrue();

    // Spring genera: SELECT * FROM notification_recipients
    //                WHERE rut_destinatario = ? ORDER BY id DESC
    List<NotificationRecipient> findByRutDestinatarioOrderByIdDesc(
            String rutDestinatario);

    // Spring genera: SELECT * FROM notification_recipients
    //                ORDER BY id DESC LIMIT 10
    List<NotificationRecipient> findTop10ByOrderByIdDesc();

    // Spring genera: SELECT COUNT(*) FROM notification_recipients
    //                WHERE rut_destinatario = ? AND leida = false
    long countByRutDestinatarioAndLeidaFalse(String rutDestinatario);
}