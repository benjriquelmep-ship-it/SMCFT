// Accede a la tabla notifications en la base de datos
// Spring genera el SQL automáticamente leyendo el nombre de cada método
package com.example.NotificationService.repository;

import com.example.NotificationService.model.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface NotificationRepository
        extends JpaRepository<Notification, Long> {

    // Devuelve todas las notificaciones de un tipo específico
    List<Notification> findByTipo(String tipo);

    // Devuelve todas las notificaciones con un estado específico
    List<Notification> findByEstado(String estado);

    // Devuelve notificaciones asociadas a una alerta de deadline específica
    //     Útil para verificar si ya existe una notificación para esa alerta
    List<Notification> findByDeadlineAlertId(Long deadlineAlertId);

    // Devuelve notificaciones de un tipo con un estado específico
    List<Notification> findByTipoAndEstado(String tipo, String estado);

    // Devuelve notificaciones de un estado ordenadas del más reciente al más antiguo
    List<Notification> findByEstadoOrderByIdDesc(String estado);

    // Busca notificaciones cuyo título contenga el texto buscado
    List<Notification> findByTituloContainingIgnoreCase(String texto);

    // Devuelve las últimas 10 notificaciones registradas en el sistema
    // Útil para monitorear en tiempo real las últimas notificaciones generadas
    List<Notification> findTop10ByOrderByIdDesc();

    // Cuenta cuántas notificaciones hay con un estado específico
    long countByEstado(String estado);

    // Cuenta cuántas notificaciones hay de un tipo específico
    long countByTipo(String tipo);
}