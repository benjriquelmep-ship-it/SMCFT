package com.example.SanitaryService.repository;

import com.example.SanitaryService.model.Sanitary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface SanitaryRepository
        extends JpaRepository<Sanitary, Long> {

    // IGUALDAD BÁSICA
    // Spring genera: SELECT * FROM sanitary_inspections WHERE patente = ?
    List<Sanitary> findByPatente(String patente);

    // Spring genera: SELECT * FROM sanitary_inspections
    //                WHERE rut_conductor = ?
    List<Sanitary> findByRutConductor(String rutConductor);

    // Spring genera: SELECT * FROM sanitary_inspections
    //                WHERE rut_inspector = ?
    List<Sanitary> findByRutInspector(String rutInspector);

    // POR RESULTADO
    // Spring genera: SELECT * FROM sanitary_inspections WHERE resultado = ?
    List<Sanitary> findByResultado(String resultado);

    // POR PASO FRONTERIZO
    // Spring genera: SELECT * FROM sanitary_inspections
    //                WHERE paso_fronterizo = ?
    List<Sanitary> findByPasoFronterizo(String pasoFronterizo);

    // AND
    // Inspecciones de una patente con resultado específico
    // Spring genera: SELECT * FROM sanitary_inspections
    //                WHERE patente = ? AND resultado = ?
    List<Sanitary> findByPatenteAndResultado(
            String patente, String resultado);

    // Inspecciones de un inspector con resultado específico
    // Spring genera: SELECT * FROM sanitary_inspections
    //                WHERE rut_inspector = ? AND resultado = ?
    List<Sanitary> findByRutInspectorAndResultado(
            String rutInspector, String resultado);

    // BETWEEN
    // Inspecciones en un rango de fechas
    // Spring genera: SELECT * FROM sanitary_inspections
    //                WHERE fecha_inspeccion BETWEEN ? AND ?
    List<Sanitary> findByFechaInspeccionBetween(
            LocalDateTime desde, LocalDateTime hasta);

    // CONTAINING + IGNORE CASE
    // Búsqueda parcial por paso fronterizo
    // Spring genera: SELECT * FROM sanitary_inspections
    //                WHERE LOWER(paso_fronterizo) LIKE LOWER('%texto%')
    List<Sanitary> findByPasoFronterizoContainingIgnoreCase(String paso);

    // ORDENAMIENTO
    // Inspecciones de una patente del más reciente al más antiguo
    // Spring genera: SELECT * FROM sanitary_inspections
    //                WHERE patente = ? ORDER BY fecha_inspeccion DESC
    List<Sanitary> findByPatenteOrderByFechaInspeccionDesc(String patente);

    // Inspecciones pendientes ordenadas por fecha
    // Spring genera: SELECT * FROM sanitary_inspections
    //                WHERE resultado = ? ORDER BY fecha_inspeccion ASC
    List<Sanitary> findByResultadoOrderByFechaInspeccionAsc(
            String resultado);

    // TOP
    // Las últimas 10 inspecciones
    // Spring genera: SELECT * FROM sanitary_inspections
    //                ORDER BY id DESC LIMIT 10
    List<Sanitary> findTop10ByOrderByIdDesc();

    // COUNT
    // Contar inspecciones por resultado
    // Spring genera: SELECT COUNT(*) FROM sanitary_inspections
    //                WHERE resultado = ?
    long countByResultado(String resultado);

    // Contar por paso fronterizo
    long countByPasoFronterizo(String pasoFronterizo);
}