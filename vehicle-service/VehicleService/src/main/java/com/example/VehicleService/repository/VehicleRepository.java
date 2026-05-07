// repository/VehicleRepository.java
// Acceso a datos de la tabla vehicles
// Consultas derivadas del apunte integradas

package com.example.VehicleService.repository;

import com.example.VehicleService.model.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface VehicleRepository extends JpaRepository<Vehicle, Long> {

    // IGUALDAD BÁSICA
    // Spring genera: SELECT * FROM vehicles WHERE patente = ?
    Optional<Vehicle> findByPatente(String patente);

    // EXISTENCIA
    // Spring genera: SELECT COUNT(*) > 0 FROM vehicles WHERE patente = ?
    boolean existsByPatente(String patente);

    // POR PROPIETARIO
    // Spring genera: SELECT * FROM vehicles WHERE rut_propietario = ?
    List<Vehicle> findByRutPropietario(String rutPropietario);

    // POR ESTADO
    // Spring genera: SELECT * FROM vehicles WHERE estado = ?
    List<Vehicle> findByEstado(String estado);

    // POR TIPO
    // Spring genera: SELECT * FROM vehicles WHERE tipo_vehiculo = ?
    List<Vehicle> findByTipoVehiculo(String tipoVehiculo);

    // AND
    // Spring genera: SELECT * FROM vehicles
    //                WHERE rut_propietario = ? AND estado = ?
    List<Vehicle> findByRutPropietarioAndEstado(
            String rutPropietario, String estado);

    // AND CON TIPO
    // Spring genera: SELECT * FROM vehicles
    //                WHERE tipo_vehiculo = ? AND estado = ?
    List<Vehicle> findByTipoVehiculoAndEstado(String tipoVehiculo, String estado);

    // COMPARACIONES NUMÉRICAS
    // Spring genera: SELECT * FROM vehicles WHERE anio >= ?
    List<Vehicle> findByAnioGreaterThanEqual(Integer anio);

    // Spring genera: SELECT * FROM vehicles WHERE anio < ?
    List<Vehicle> findByAnioLessThan(Integer anio);

    // BETWEEN
    // Spring genera: SELECT * FROM vehicles WHERE anio BETWEEN ? AND ?
    List<Vehicle> findByAnioBetween(Integer anioMin, Integer anioMax);

    // CONTAINING + IGNORE CASE
    // Spring genera: SELECT * FROM vehicles
    //                WHERE LOWER(marca) LIKE LOWER('%texto%')
    List<Vehicle> findByMarcaContainingIgnoreCase(String marca);

    // ORDENAMIENTO
    // Spring genera: SELECT * FROM vehicles
    //                WHERE rut_propietario = ? ORDER BY anio DESC
    List<Vehicle> findByRutPropietarioOrderByAnioDesc(String rutPropietario);

    // Spring genera: SELECT * FROM vehicles
    //                WHERE estado = ? ORDER BY patente ASC
    List<Vehicle> findByEstadoOrderByPatenteAsc(String estado);

    // IN
    // Spring genera: SELECT * FROM vehicles WHERE id IN (1, 2, 3)
    List<Vehicle> findByIdIn(List<Long> ids);

    // TOP
    // Spring genera: SELECT * FROM vehicles ORDER BY id DESC LIMIT 10
    List<Vehicle> findTop10ByOrderByIdDesc();
}