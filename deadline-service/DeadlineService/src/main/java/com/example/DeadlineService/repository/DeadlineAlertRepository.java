// Accede a la tabla deadline_alerts en la base de datos
// Spring genera el SQL automáticamente leyendo el nombre de cada método
package com.example.DeadlineService.repository;

import com.example.DeadlineService.model.DeadlineAlert;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
@Repository
public interface DeadlineAlertRepository
        extends JpaRepository<DeadlineAlert, Long> {

    // Devuelve todas las alertas que pertenecen a un deadline específico
    List<DeadlineAlert> findByDeadlineId(Long deadlineId);

    // Devuelve las alertas NO ENVIADAS de un deadline específico
    List<DeadlineAlert> findByDeadlineIdAndEnviadaFalse(Long deadlineId);

    // Devuelve las alertas YA ENVIADAS de un deadline específico
    List<DeadlineAlert> findByDeadlineIdAndEnviadaTrue(Long deadlineId);

    // Devuelve todas las alertas de un tipo específico
    List<DeadlineAlert> findByTipoAlerta(String tipoAlerta);

    // Devuelve TODAS las alertas no enviadas del sistema
    List<DeadlineAlert> findByEnviadaFalse();

    // Devuelve TODAS las alertas ya enviadas del sistema
    List<DeadlineAlert> findByEnviadaTrue();

    // Devuelve alertas de un tipo específico que aún no fueron enviadas
    List<DeadlineAlert> findByTipoAlertaAndEnviadaFalse(String tipoAlerta);

    // Devuelve alertas de un deadline ordenadas por días restantes
    List<DeadlineAlert> findByDeadlineIdOrderByDiasRestantesAsc(
            Long deadlineId);

    // Devuelve las últimas 10 alertas registradas en el sistema
    List<DeadlineAlert> findTop10ByOrderByIdDesc();

    // Cuenta cuántas alertas no han sido enviadas todavía
    long countByEnviadaFalse();
}