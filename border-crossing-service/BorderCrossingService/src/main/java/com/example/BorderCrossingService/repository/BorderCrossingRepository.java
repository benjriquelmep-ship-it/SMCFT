// Accede a la tabla border_crossings en la base de datos
// Spring genera el SQL automáticamente leyendo el nombre de cada método
package com.example.BorderCrossingService.repository;

import com.example.BorderCrossingService.model.BorderCrossing;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BorderCrossingRepository
        extends JpaRepository<BorderCrossing, Long> {

    // Devuelve todos los cruces de un vehículo específico
    List<BorderCrossing> findByPatente(String patente);

    // Devuelve todos los cruces realizados por un conductor específico
    List<BorderCrossing> findByRutConductor(String rutConductor);

    // Devuelve todos los cruces con un estado específico
    List<BorderCrossing> findByEstado(String estado);

    // Devuelve todos los cruces de un paso fronterizo específico
    List<BorderCrossing> findByPasoFronterizo(String pasoFronterizo);

    // Devuelve todos los cruces que procesó un fiscalizador específico
    List<BorderCrossing> findByRutFiscalizador(String rutFiscalizador);

    // Devuelve cruces de un vehículo con un estado específico
    List<BorderCrossing> findByPatenteAndEstado(
            String patente, String estado);

    // Devuelve cruces de un conductor con un estado específico
    List<BorderCrossing> findByRutConductorAndEstado(
            String rutConductor, String estado);

    // Devuelve cruces registrados en un rango de fechas
    // Útil para reportes de flujo migratorio por período
    List<BorderCrossing> findByFechaCruceBetween(
            LocalDateTime desde, LocalDateTime hasta);

    // Busca cruces cuyo país destino contenga el texto buscado
    List<BorderCrossing> findByPaisDestinoContainingIgnoreCase(String pais);

    // Devuelve cruces de un vehículo del más reciente al más antiguo
    List<BorderCrossing> findByPatenteOrderByFechaCruceDesc(String patente);

    // Devuelve cruces pendientes del más antiguo al más reciente
    // Útil para procesar los cruces pendientes en orden de llegada
    List<BorderCrossing> findByEstadoOrderByFechaCruceAsc(String estado);

    // Devuelve los últimos 10 cruces registrados en el sistema
    // Útil para monitorear en tiempo real la actividad fronteriza
    List<BorderCrossing> findTop10ByOrderByFechaCruceDesc();

    // Cuenta cuántos cruces hay con un estado específico
    long countByEstado(String estado);

    // Cuenta cuántos cruces se hicieron en un paso fronterizo específico
    long countByPasoFronterizo(String pasoFronterizo);
}