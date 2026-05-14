// repository/NotificationRepository.java
package com.example.NotificationService.repository;

import com.example.NotificationService.model.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface NotificationRepository
        extends JpaRepository<Notification, Long> {

    // Spring genera: SELECT * FROM notifications WHERE tipo = ?
    List<Notification> findByTipo(String tipo);

    // Spring genera: SELECT * FROM notifications WHERE estado = ?
    List<Notification> findByEstado(String estado);

    // Spring genera: SELECT * FROM notifications
    //                WHERE deadline_alert_id = ?
    List<Notification> findByDeadlineAlertId(Long deadlineAlertId);

    // Spring genera: SELECT * FROM notifications
    //                WHERE tipo = ? AND estado = ?
    List<Notification> findByTipoAndEstado(String tipo, String estado);

    // Spring genera: SELECT * FROM notifications
    //                WHERE estado = ? ORDER BY id DESC
    List<Notification> findByEstadoOrderByIdDesc(String estado);

    // Spring genera: SELECT * FROM notifications
    //                WHERE LOWER(titulo) LIKE LOWER('%texto%')
    List<Notification> findByTituloContainingIgnoreCase(String texto);

    // Spring genera: SELECT * FROM notifications ORDER BY id DESC LIMIT 10
    List<Notification> findTop10ByOrderByIdDesc();

    // Spring genera: SELECT COUNT(*) FROM notifications WHERE estado = ?
    long countByEstado(String estado);

    // Spring genera: SELECT COUNT(*) FROM notifications WHERE tipo = ?
    long countByTipo(String tipo);
}