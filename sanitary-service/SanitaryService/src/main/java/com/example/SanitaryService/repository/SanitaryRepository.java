// Accede a la tabla sanitary_inspections en la base de datos
// Spring genera el SQL automáticamente leyendo el nombre de cada método
package com.example.SanitaryService.repository;

import com.example.SanitaryService.model.Sanitary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface SanitaryRepository
        extends JpaRepository<Sanitary, Long> {

    // Devuelve todas las inspecciones de un vehículo específico
    List<Sanitary> findByPatente(String patente);

    // Devuelve todas las inspecciones de un conductor específico
    List<Sanitary> findByRutConductor(String rutConductor);

    // Devuelve todas las inspecciones realizadas por un inspector específico
    List<Sanitary> findByRutInspector(String rutInspector);

    // Devuelve inspecciones con un resultado específico
    List<Sanitary> findByResultado(String resultado);

    // Devuelve todas las inspecciones de un paso fronterizo específico
    List<Sanitary> findByPasoFronterizo(String pasoFronterizo);

    // Devuelve inspecciones de un vehículo con un resultado específico
    List<Sanitary> findByPatenteAndResultado(
            String patente, String resultado);

    // Devuelve inspecciones de un inspector con un resultado específico
    List<Sanitary> findByRutInspectorAndResultado(
            String rutInspector, String resultado);

    // Devuelve inspecciones registradas en un rango de fechas
    List<Sanitary> findByFechaInspeccionBetween(
            LocalDateTime desde, LocalDateTime hasta);

    // Busca inspecciones cuyo paso fronterizo contenga el texto buscado
    List<Sanitary> findByPasoFronterizoContainingIgnoreCase(String paso);

    // Devuelve inspecciones de un vehículo del más reciente al más antiguo
    // Útil para ver el historial de inspecciones de un vehículo
    List<Sanitary> findByPatenteOrderByFechaInspeccionDesc(String patente);

    // Devuelve inspecciones con un resultado ordenadas por fecha
    // Útil para procesar inspecciones PENDIENTES en orden de llegada
    List<Sanitary> findByResultadoOrderByFechaInspeccionAsc(
            String resultado);

    // Devuelve las últimas 10 inspecciones registradas en el sistema
    // Útil para monitorear en tiempo real la actividad del servicio sanitario
    List<Sanitary> findTop10ByOrderByIdDesc();

    // Cuenta cuántas inspecciones hay con un resultado específico
    long countByResultado(String resultado);

    // Cuenta cuántas inspecciones se hicieron en un paso fronterizo específico
    long countByPasoFronterizo(String pasoFronterizo);
}