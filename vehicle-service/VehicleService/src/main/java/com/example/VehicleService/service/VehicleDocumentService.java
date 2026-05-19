// service/VehicleDocumentService.java
// Lógica de negocio para los documentos de vehículos

package com.example.VehicleService.service;

import com.example.VehicleService.dto.VehicleDocumentDTO;
import com.example.VehicleService.model.Vehicle;
import com.example.VehicleService.model.VehicleDocument;
import com.example.VehicleService.repository.VehicleDocumentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class VehicleDocumentService {


    private final VehicleDocumentRepository documentRepository;

    // VehicleService se inyecta para verificar que el vehículo existe
    private final VehicleService vehicleService;

    // -------------------------------------------------------
    // CRUD BÁSICO
    // -------------------------------------------------------

    public List<VehicleDocument> obtenerTodos() {
        log.info("Obteniendo todos los documentos");
        return documentRepository.findAll();
    }

    public VehicleDocument obtenerPorId(Long id) {
        log.info("Buscando documento con id: {}", id);
        return documentRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Documento con id {} no encontrado", id);
                    return new RuntimeException(
                            "Documento no encontrado con id: " + id);
                });
    }

    // Agregar documento a un vehículo
    public VehicleDocument agregar(VehicleDocumentDTO dto) {
        log.info("Agregando documento tipo {} al vehículo id: {}",
                dto.getTipo(), dto.getVehicleId());

        // REGLA DE NEGOCIO 1: el vehículo debe existir
        Vehicle vehiculo = vehicleService.obtenerPorId(dto.getVehicleId());

        // REGLA DE NEGOCIO 2: no puede existir otro doc con el mismo número
        if (documentRepository.existsByNumero(dto.getNumero())) {
            log.warn("Número de documento duplicado: {}", dto.getNumero());
            throw new RuntimeException(
                    "Ya existe un documento con el número: " + dto.getNumero());
        }

        // Mapeo DTO → Entidad
        VehicleDocument documento = new VehicleDocument();
        documento.setVehicle(vehiculo);
        documento.setTipo(dto.getTipo());
        documento.setNumero(dto.getNumero());
        documento.setFechaVencimiento(dto.getFechaVencimiento());
        documento.setVigente(true);

        VehicleDocument guardado = documentRepository.save(documento);
        log.info("Documento agregado con id: {}", guardado.getId());
        return guardado;
    }

    // Actualizar documento
    public VehicleDocument actualizar(Long id, VehicleDocumentDTO dto) {
        log.info("Actualizando documento con id: {}", id);
        VehicleDocument existente = obtenerPorId(id);

        existente.setTipo(dto.getTipo());
        existente.setFechaVencimiento(dto.getFechaVencimiento());

        VehicleDocument actualizado = documentRepository.save(existente);
        log.info("Documento {} actualizado correctamente", id);
        return actualizado;
    }

    // Marcar documento como no vigente
    public VehicleDocument invalidar(Long id) {
        log.info("Invalidando documento con id: {}", id);
        VehicleDocument documento = obtenerPorId(id);
        documento.setVigente(false);
        VehicleDocument actualizado = documentRepository.save(documento);
        log.info("Documento {} invalidado correctamente", id);
        return actualizado;
    }

    // Eliminar documento
    public void eliminar(Long id) {
        log.info("Eliminando documento con id: {}", id);
        if (!documentRepository.existsById(id)) {
            log.warn("Documento con id {} no encontrado", id);
            throw new RuntimeException(
                    "Documento no encontrado con id: " + id);
        }
        documentRepository.deleteById(id);
        log.info("Documento {} eliminado correctamente", id);
    }

    // -------------------------------------------------------
    // CONSULTAS DERIVADAS
    // -------------------------------------------------------

    // Todos los documentos de un vehículo
    public List<VehicleDocument> obtenerPorVehiculo(Long vehicleId) {
        log.info("Obteniendo documentos del vehículo id: {}", vehicleId);
        return documentRepository.findByVehicleId(vehicleId);
    }

    // Solo documentos vigentes de un vehículo
    public List<VehicleDocument> obtenerVigentesPorVehiculo(Long vehicleId) {
        log.info("Obteniendo documentos vigentes del vehículo: {}", vehicleId);
        return documentRepository.findByVehicleIdAndVigenteTrue(vehicleId);
    }

    // Documentos próximos a vencer en los próximos 30 días
    // Útil para que Notification Service envíe alertas
    public List<VehicleDocument> obtenerProximosAVencer() {
        log.info("Obteniendo documentos que vencen en 30 días");
        LocalDate en30Dias = LocalDate.now().plusDays(30);
        return documentRepository.findByFechaVencimientoBefore(en30Dias);
    }

    // Documentos vencidos que siguen marcados como vigentes
    public List<VehicleDocument> obtenerVencidos() {
        log.info("Obteniendo documentos vencidos");
        return documentRepository
                .findByFechaVencimientoBeforeAndVigenteTrue(LocalDate.now());
    }

    // Documentos de un vehículo ordenados por fecha de vencimiento
    public List<VehicleDocument> obtenerPorVehiculoOrdenados(Long vehicleId) {
        log.info("Obteniendo documentos del vehículo {} ordenados",
                vehicleId);
        return documentRepository
                .findByVehicleIdOrderByFechaVencimientoAsc(vehicleId);
    }

    // Los últimos 10 documentos registrados
    public List<VehicleDocument> obtenerUltimosRegistrados() {
        log.info("Obteniendo los últimos 10 documentos registrados");
        return documentRepository.findTop10ByOrderByIdDesc();
    }
}