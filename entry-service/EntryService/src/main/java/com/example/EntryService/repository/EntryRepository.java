// Accede a la tabla entries en la base de datos
// Spring genera el SQL automáticamente leyendo el nombre de cada método
package com.example.EntryService.repository;

import com.example.EntryService.model.Entry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface EntryRepository extends JpaRepository<Entry, Long> {

    // Devuelve todos los ingresos de un vehículo específico
    List<Entry> findByPatente(String patente);

    // Devuelve todos los ingresos realizados por un conductor específico
    List<Entry> findByRutConductor(String rutConductor);

    // Devuelve todos los ingresos con un estado específico
    List<Entry> findByEstado(String estado);

    // Devuelve todos los ingresos de un tipo específico
    List<Entry> findByTipoIngreso(String tipoIngreso);

    // Devuelve todos los ingresos de un paso fronterizo específico
    List<Entry> findByPasoFronterizo(String pasoFronterizo);

    // Devuelve todos los ingresos que procesó un fiscalizador específico
    List<Entry> findByRutFiscalizador(String rutFiscalizador);

    // Devuelve ingresos de un vehículo con un estado específico
    List<Entry> findByPatenteAndEstado(String patente, String estado);

    // Devuelve ingresos de un conductor con un tipo específico
    List<Entry> findByRutConductorAndTipoIngreso(
            String rutConductor, String tipoIngreso);

    // Devuelve ingresos registrados en un rango de fechas
    List<Entry> findByFechaIngresoBetween(
            LocalDateTime desde, LocalDateTime hasta);

    // Busca ingresos cuyo país de origen contenga el texto buscado
    List<Entry> findByPaisOrigenContainingIgnoreCase(String pais);

    // Devuelve ingresos de un vehículo del más reciente al más antiguo
    // Útil para ver el historial de ingresos de un vehículo
    List<Entry> findByPatenteOrderByFechaIngresoDesc(String patente);

    // Devuelve ingresos pendientes del más antiguo al más reciente
    // Útil para procesar los ingresos pendientes en orden de llegada
    List<Entry> findByEstadoOrderByFechaIngresoAsc(String estado);

    // Devuelve los últimos 10 ingresos registrados en el sistema
    // Útil para monitorear en tiempo real la actividad fronteriza
    List<Entry> findTop10ByOrderByFechaIngresoDesc();

    // Cuenta cuántos ingresos hay con un estado específico
    long countByEstado(String estado);

    // Cuenta cuántos ingresos hay de un tipo específico
    long countByTipoIngreso(String tipoIngreso);
}