// repository/BorderCrossingRepository.java
// Consultas derivadas del apunte integradas

package com.example.BorderCrossingService.repository;

import com.example.BorderCrossingService.model.BorderCrossing;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BorderCrossingRepository
        extends JpaRepository<BorderCrossing, Long> {

    // IGUALDAD BÁSICA
    // Todos los cruces de una patente
    // Spring genera: SELECT * FROM border_crossings WHERE patente = ?
    List<BorderCrossing> findByPatente(String patente);

    // Todos los cruces de un conductor
    // Spring genera: SELECT * FROM border_crossings WHERE rut_conductor = ?
    List<BorderCrossing> findByRutConductor(String rutConductor);

    // POR ESTADO
    // Spring genera: SELECT * FROM border_crossings WHERE estado = ?
    List<BorderCrossing> findByEstado(String estado);

    // POR PASO FRONTERIZO
    // Spring genera: SELECT * FROM border_crossings
    //                WHERE paso_fronterizo = ?
    List<BorderCrossing> findByPasoFronterizo(String pasoFronterizo);

    // POR FISCALIZADOR
    // Spring genera: SELECT * FROM border_crossings
    //                WHERE rut_fiscalizador = ?
    List<BorderCrossing> findByRutFiscalizador(String rutFiscalizador);

    // AND
    // Cruces de una patente con estado específico
    // Spring genera: SELECT * FROM border_crossings
    //                WHERE patente = ? AND estado = ?
    List<BorderCrossing> findByPatenteAndEstado(
            String patente, String estado);

    // Cruces de un conductor con estado específico
    // Spring genera: SELECT * FROM border_crossings
    //                WHERE rut_conductor = ? AND estado = ?
    List<BorderCrossing> findByRutConductorAndEstado(
            String rutConductor, String estado);

    // BETWEEN
    // Cruces en un rango de fechas — para reportes de flujo migratorio
    // Spring genera: SELECT * FROM border_crossings
    //                WHERE fecha_cruce BETWEEN ? AND ?
    List<BorderCrossing> findByFechaCruceBetween(
            LocalDateTime desde, LocalDateTime hasta);

    // CONTAINING + IGNORE CASE
    // Búsqueda parcial por país de destino
    // Spring genera: SELECT * FROM border_crossings
    //                WHERE LOWER(pais_destino) LIKE LOWER('%texto%')
    List<BorderCrossing> findByPaisDestinoContainingIgnoreCase(String pais);

    // ORDENAMIENTO
    // Cruces de una patente del más reciente al más antiguo
    // Spring genera: SELECT * FROM border_crossings
    //                WHERE patente = ? ORDER BY fecha_cruce DESC
    List<BorderCrossing> findByPatenteOrderByFechaCruceDesc(String patente);

    // Cruces pendientes ordenados por fecha más antigua primero
    // Spring genera: SELECT * FROM border_crossings
    //                WHERE estado = ? ORDER BY fecha_cruce ASC
    List<BorderCrossing> findByEstadoOrderByFechaCruceAsc(String estado);

    // TOP
    // Los últimos 10 cruces registrados en el sistema
    // Spring genera: SELECT * FROM border_crossings
    //                ORDER BY fecha_cruce DESC LIMIT 10
    List<BorderCrossing> findTop10ByOrderByFechaCruceDesc();

    // COUNT
    // Contar cruces por estado — para estadísticas del sistema
    // Spring genera: SELECT COUNT(*) FROM border_crossings WHERE estado = ?
    long countByEstado(String estado);

    // Contar cruces por paso fronterizo
    // Spring genera: SELECT COUNT(*) FROM border_crossings
    //                WHERE paso_fronterizo = ?
    long countByPasoFronterizo(String pasoFronterizo);
}