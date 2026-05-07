// repository/VehicleDocumentRepository.java
// Acceso a datos de la tabla vehicle_documents

package com.example.VehicleService.repository;

import com.example.VehicleService.model.VehicleDocument;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface VehicleDocumentRepository
        extends JpaRepository<VehicleDocument, Long> {

    // IGUALDAD BÁSICA
    // Todos los documentos de un vehículo
    // Spring genera: SELECT * FROM vehicle_documents WHERE vehicle_id = ?
    List<VehicleDocument> findByVehicleId(Long vehicleId);

    // AND CON BOOLEANO
    // Documentos vigentes de un vehículo
    // Spring genera: SELECT * FROM vehicle_documents
    //                WHERE vehicle_id = ? AND vigente = true
    List<VehicleDocument> findByVehicleIdAndVigenteTrue(Long vehicleId);

    // POR TIPO
    // Spring genera: SELECT * FROM vehicle_documents WHERE tipo = ?
    List<VehicleDocument> findByTipo(String tipo);

    // COMPARACIÓN DE FECHA
    // Documentos que vencen antes de una fecha
    // Útil para alertar sobre documentos próximos a vencer
    // Spring genera: SELECT * FROM vehicle_documents
    //                WHERE fecha_vencimiento < ?
    List<VehicleDocument> findByFechaVencimientoBefore(LocalDate fecha);

    // AND CON FECHA Y BOOLEANO
    // Documentos vencidos que siguen marcados como vigentes
    // Spring genera: SELECT * FROM vehicle_documents
    //                WHERE fecha_vencimiento < ? AND vigente = true
    List<VehicleDocument> findByFechaVencimientoBeforeAndVigenteTrue(
            LocalDate fecha);

    // EXISTENCIA
    // Verificar si existe un número de documento
    // Spring genera: SELECT COUNT(*) > 0 FROM vehicle_documents WHERE numero = ?
    boolean existsByNumero(String numero);

    // ORDENAMIENTO
    // Documentos de un vehículo ordenados por fecha de vencimiento
    // Spring genera: SELECT * FROM vehicle_documents
    //                WHERE vehicle_id = ? ORDER BY fecha_vencimiento ASC
    List<VehicleDocument> findByVehicleIdOrderByFechaVencimientoAsc(
            Long vehicleId);

    // TOP
    // Los últimos 10 documentos registrados
    // Spring genera: SELECT * FROM vehicle_documents ORDER BY id DESC LIMIT 10
    List<VehicleDocument> findTop10ByOrderByIdDesc();
}