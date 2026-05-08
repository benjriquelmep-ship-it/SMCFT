// repository/EntryRepository.java
// Consultas derivadas del apunte integradas

package com.example.EntryService.repository;

import com.example.EntryService.model.Entry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface EntryRepository extends JpaRepository<Entry, Long> {

    // IGUALDAD BÁSICA
    // Todos los ingresos de una patente
    // Spring genera: SELECT * FROM entries WHERE patente = ?
    List<Entry> findByPatente(String patente);

    // Todos los ingresos de un conductor
    // Spring genera: SELECT * FROM entries WHERE rut_conductor = ?
    List<Entry> findByRutConductor(String rutConductor);

    // POR ESTADO
    // Spring genera: SELECT * FROM entries WHERE estado = ?
    List<Entry> findByEstado(String estado);

    // POR TIPO DE INGRESO
    // Spring genera: SELECT * FROM entries WHERE tipo_ingreso = ?
    List<Entry> findByTipoIngreso(String tipoIngreso);

    // POR PASO FRONTERIZO
    // Spring genera: SELECT * FROM entries WHERE paso_fronterizo = ?
    List<Entry> findByPasoFronterizo(String pasoFronterizo);

    // POR FISCALIZADOR
    // Spring genera: SELECT * FROM entries WHERE rut_fiscalizador = ?
    List<Entry> findByRutFiscalizador(String rutFiscalizador);

    // AND
    // Ingresos de una patente con estado específico
    // Spring genera: SELECT * FROM entries WHERE patente = ? AND estado = ?
    List<Entry> findByPatenteAndEstado(String patente, String estado);

    // Ingresos de un conductor con tipo específico
    // Spring genera: SELECT * FROM entries
    //                WHERE rut_conductor = ? AND tipo_ingreso = ?
    List<Entry> findByRutConductorAndTipoIngreso(
            String rutConductor, String tipoIngreso);

    // BETWEEN
    // Ingresos en un rango de fechas
    // Spring genera: SELECT * FROM entries
    //                WHERE fecha_ingreso BETWEEN ? AND ?
    List<Entry> findByFechaIngresoBetween(
            LocalDateTime desde, LocalDateTime hasta);

    // CONTAINING + IGNORE CASE
    // Búsqueda parcial por país de origen
    // Spring genera: SELECT * FROM entries
    //                WHERE LOWER(pais_origen) LIKE LOWER('%texto%')
    List<Entry> findByPaisOrigenContainingIgnoreCase(String pais);

    // ORDENAMIENTO
    // Ingresos de una patente del más reciente al más antiguo
    // Spring genera: SELECT * FROM entries
    //                WHERE patente = ? ORDER BY fecha_ingreso DESC
    List<Entry> findByPatenteOrderByFechaIngresoDesc(String patente);

    // Ingresos pendientes ordenados por fecha más antigua primero
    // Spring genera: SELECT * FROM entries
    //                WHERE estado = ? ORDER BY fecha_ingreso ASC
    List<Entry> findByEstadoOrderByFechaIngresoAsc(String estado);

    // TOP
    // Los últimos 10 ingresos al sistema
    // Spring genera: SELECT * FROM entries
    //                ORDER BY fecha_ingreso DESC LIMIT 10
    List<Entry> findTop10ByOrderByFechaIngresoDesc();

    // COUNT
    // Contar ingresos por estado
    // Spring genera: SELECT COUNT(*) FROM entries WHERE estado = ?
    long countByEstado(String estado);

    // Contar ingresos por tipo
    // Spring genera: SELECT COUNT(*) FROM entries WHERE tipo_ingreso = ?
    long countByTipoIngreso(String tipoIngreso);
}