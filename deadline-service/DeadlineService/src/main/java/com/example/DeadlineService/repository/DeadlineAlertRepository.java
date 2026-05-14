// repository/DeadlineAlertRepository.java
package com.example.DeadlineService.repository;

import com.example.DeadlineService.model.DeadlineAlert;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface DeadlineAlertRepository
        extends JpaRepository<DeadlineAlert, Long> {

    // Spring genera: SELECT * FROM deadline_alerts WHERE deadline_id = ?
    List<DeadlineAlert> findByDeadlineId(Long deadlineId);

    // Spring genera: SELECT * FROM deadline_alerts
    //                WHERE deadline_id = ? AND enviada = false
    List<DeadlineAlert> findByDeadlineIdAndEnviadaFalse(Long deadlineId);

    // Spring genera: SELECT * FROM deadline_alerts
    //                WHERE deadline_id = ? AND enviada = true
    List<DeadlineAlert> findByDeadlineIdAndEnviadaTrue(Long deadlineId);

    // Spring genera: SELECT * FROM deadline_alerts WHERE tipo_alerta = ?
    List<DeadlineAlert> findByTipoAlerta(String tipoAlerta);

    // Spring genera: SELECT * FROM deadline_alerts WHERE enviada = false
    List<DeadlineAlert> findByEnviadaFalse();
    List<DeadlineAlert> findByEnviadaTrue();

    // Spring genera: SELECT * FROM deadline_alerts
    //                WHERE tipo_alerta = ? AND enviada = false
    List<DeadlineAlert> findByTipoAlertaAndEnviadaFalse(String tipoAlerta);

    // Spring genera: SELECT * FROM deadline_alerts
    //                WHERE deadline_id = ? ORDER BY dias_restantes ASC
    List<DeadlineAlert> findByDeadlineIdOrderByDiasRestantesAsc(
            Long deadlineId);

    // Spring genera: SELECT * FROM deadline_alerts ORDER BY id DESC LIMIT 10
    List<DeadlineAlert> findTop10ByOrderByIdDesc();

    // Spring genera: SELECT COUNT(*) FROM deadline_alerts WHERE enviada = false
    long countByEnviadaFalse();
}