// repository/DeadlineRepository.java
package com.example.DeadlineService.repository;

import com.example.DeadlineService.model.Deadline;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface DeadlineRepository
        extends JpaRepository<Deadline, Long> {

    // Spring genera: SELECT * FROM deadlines WHERE patente = ?
    List<Deadline> findByPatente(String patente);

    // Spring genera: SELECT * FROM deadlines WHERE rut_conductor = ?
    List<Deadline> findByRutConductor(String rutConductor);

    // Spring genera: SELECT * FROM deadlines WHERE estado = ?
    List<Deadline> findByEstado(String estado);

    // Spring genera: SELECT * FROM deadlines WHERE tipo = ?
    List<Deadline> findByTipo(String tipo);

    // Spring genera: SELECT * FROM deadlines WHERE entry_id = ?
    List<Deadline> findByEntryId(Long entryId);

    // Spring genera: SELECT * FROM deadlines
    //                WHERE patente = ? AND estado = ?
    List<Deadline> findByPatenteAndEstado(String patente, String estado);

    // Spring genera: SELECT * FROM deadlines
    //                WHERE rut_conductor = ? AND estado = ?
    List<Deadline> findByRutConductorAndEstado(
            String rutConductor, String estado);

    // Spring genera: SELECT * FROM deadlines
    //                WHERE fecha_limite BETWEEN ? AND ?
    List<Deadline> findByFechaLimiteBetween(
            LocalDateTime desde, LocalDateTime hasta);

    // Spring genera: SELECT * FROM deadlines WHERE fecha_limite < ?
    List<Deadline> findByFechaLimiteBefore(LocalDateTime fecha);

    // Spring genera: SELECT * FROM deadlines
    //                WHERE fecha_limite < ? AND estado = ?
    List<Deadline> findByFechaLimiteBeforeAndEstado(
            LocalDateTime fecha, String estado);

    // Spring genera: SELECT * FROM deadlines
    //                WHERE patente = ? ORDER BY fecha_limite ASC
    List<Deadline> findByPatenteOrderByFechaLimiteAsc(String patente);

    // Spring genera: SELECT * FROM deadlines
    //                WHERE estado = ? ORDER BY fecha_limite ASC
    List<Deadline> findByEstadoOrderByFechaLimiteAsc(String estado);

    // Spring genera: SELECT * FROM deadlines ORDER BY id DESC LIMIT 10
    List<Deadline> findTop10ByOrderByIdDesc();

    // Spring genera: SELECT COUNT(*) FROM deadlines WHERE estado = ?
    long countByEstado(String estado);
}