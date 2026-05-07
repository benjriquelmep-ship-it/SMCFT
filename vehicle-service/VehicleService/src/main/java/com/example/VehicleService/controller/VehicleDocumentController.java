// controller/VehicleDocumentController.java

package com.example.VehicleService.controller;

import com.example.VehicleService.dto.VehicleDocumentDTO;
import com.example.VehicleService.model.VehicleDocument;
import com.example.VehicleService.service.VehicleDocumentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/vehicles/documentos")
@RequiredArgsConstructor
public class VehicleDocumentController {

    private final VehicleDocumentService documentService;

    // -------------------------------------------------------
    // CRUD BÁSICO
    // -------------------------------------------------------

    // GET /api/v1/vehicles/documentos
    @GetMapping
    public ResponseEntity<List<VehicleDocument>> obtenerTodos() {
        return ResponseEntity.ok(documentService.obtenerTodos());
    }

    // GET /api/v1/vehicles/documentos/1
    @GetMapping("/{id}")
    public ResponseEntity<VehicleDocument> obtenerPorId(
            @PathVariable Long id) {
        return ResponseEntity.ok(documentService.obtenerPorId(id));
    }

    // POST /api/v1/vehicles/documentos
    @PostMapping
    public ResponseEntity<VehicleDocument> agregar(
            @Valid @RequestBody VehicleDocumentDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(documentService.agregar(dto));
    }

    // PUT /api/v1/vehicles/documentos/1
    @PutMapping("/{id}")
    public ResponseEntity<VehicleDocument> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody VehicleDocumentDTO dto) {
        return ResponseEntity.ok(documentService.actualizar(id, dto));
    }

    // PATCH /api/v1/vehicles/documentos/1/invalidar
    @PatchMapping("/{id}/invalidar")
    public ResponseEntity<VehicleDocument> invalidar(@PathVariable Long id) {
        return ResponseEntity.ok(documentService.invalidar(id));
    }

    // DELETE /api/v1/vehicles/documentos/1
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        documentService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    // -------------------------------------------------------
    // CONSULTAS DERIVADAS
    // -------------------------------------------------------

    // GET /api/v1/vehicles/documentos/vehiculo/1
    @GetMapping("/vehiculo/{vehicleId}")
    public ResponseEntity<List<VehicleDocument>> obtenerPorVehiculo(
            @PathVariable Long vehicleId) {
        return ResponseEntity.ok(
                documentService.obtenerPorVehiculo(vehicleId));
    }

    // GET /api/v1/vehicles/documentos/vehiculo/1/vigentes
    @GetMapping("/vehiculo/{vehicleId}/vigentes")
    public ResponseEntity<List<VehicleDocument>> obtenerVigentesPorVehiculo(
            @PathVariable Long vehicleId) {
        return ResponseEntity.ok(
                documentService.obtenerVigentesPorVehiculo(vehicleId));
    }

    // GET /api/v1/vehicles/documentos/proximos-a-vencer
    @GetMapping("/proximos-a-vencer")
    public ResponseEntity<List<VehicleDocument>> obtenerProximosAVencer() {
        return ResponseEntity.ok(documentService.obtenerProximosAVencer());
    }

    // GET /api/v1/vehicles/documentos/vencidos
    @GetMapping("/vencidos")
    public ResponseEntity<List<VehicleDocument>> obtenerVencidos() {
        return ResponseEntity.ok(documentService.obtenerVencidos());
    }

    // GET /api/v1/vehicles/documentos/vehiculo/1/ordenados
    @GetMapping("/vehiculo/{vehicleId}/ordenados")
    public ResponseEntity<List<VehicleDocument>> obtenerPorVehiculoOrdenados(
            @PathVariable Long vehicleId) {
        return ResponseEntity.ok(
                documentService.obtenerPorVehiculoOrdenados(vehicleId));
    }

    // GET /api/v1/vehicles/documentos/ultimos
    @GetMapping("/ultimos")
    public ResponseEntity<List<VehicleDocument>> obtenerUltimosRegistrados() {
        return ResponseEntity.ok(documentService.obtenerUltimosRegistrados());
    }
}