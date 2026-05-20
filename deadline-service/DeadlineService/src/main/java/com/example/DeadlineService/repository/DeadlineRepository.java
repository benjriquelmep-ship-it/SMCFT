// Accede a la tabla deadlines en la base de datos
// Spring genera el SQL automáticamente leyendo el nombre de cada método
package com.example.DeadlineService.repository;

import com.example.DeadlineService.model.Deadline;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface DeadlineRepository
        extends JpaRepository<Deadline, Long> {

    // Devuelve todos los deadlines de un vehículo específico
    List<Deadline> findByPatente(String patente);

    // Devuelve todos los deadlines de un conductor específico
    List<Deadline> findByRutConductor(String rutConductor);

    // Devuelve deadlines por estado (ACTIVO, VENCIDO, CERRADO)
    List<Deadline> findByEstado(String estado);

    // Devuelve deadlines por tipo de ingresos
    List<Deadline> findByTipo(String tipo);

    // Devuelve deadlines asociados a un ingreso específico de Entry Service
    List<Deadline> findByEntryId(Long entryId);

    // Devuelve deadlines de un vehículo con un estado específico
    List<Deadline> findByPatenteAndEstado(String patente, String estado);

    // Devuelve deadlines de un conductor con un estado específico
    List<Deadline> findByRutConductorAndEstado(
            String rutConductor, String estado);

    // Devuelve deadlines cuya fecha límite está en un rango
    List<Deadline> findByFechaLimiteBetween(
            LocalDateTime desde, LocalDateTime hasta);

    // Devuelve deadlines cuya fecha límite ya pasó
    List<Deadline> findByFechaLimiteBefore(LocalDateTime fecha);

    // Devuelve deadlines vencidos con un estado específico
    // Útil para encontrar deadlines que vencieron pero siguen ACTIVOS y deben ser marcados como VENCIDOS
    List<Deadline> findByFechaLimiteBeforeAndEstado(
            LocalDateTime fecha, String estado);

    // Devuelve deadlines de un vehículo ordenados del que vence antes
    // Útil para ver cuándo vence próximamente el vehículo DEF456
    List<Deadline> findByPatenteOrderByFechaLimiteAsc(String patente);

    // Devuelve deadlines de un estado ordenados del que vence antes
    List<Deadline> findByEstadoOrderByFechaLimiteAsc(String estado);

    // Devuelve los últimos 10 deadlines registrados en el sistema
    List<Deadline> findTop10ByOrderByIdDesc();

    // Cuenta cuántos deadlines hay con un estado específico
    long countByEstado(String estado);
}