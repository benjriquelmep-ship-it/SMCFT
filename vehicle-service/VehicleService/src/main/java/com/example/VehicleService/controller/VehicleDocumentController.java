// Recibe las peticiones HTTP de Vehicle Document Service y retorna ResponseEntity con JSON
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

    // GET /api/v1/vehicles/documentos : Lista de forma global todos los documentos vehiculares ingresados
    @GetMapping
    public ResponseEntity<List<VehicleDocument>> obtenerTodos() {
        return ResponseEntity.ok(documentService.obtenerTodos());
    }

    // GET /api/v1/vehicles/documentos/1 : Busca un documento específico a través de su ID primario
    @GetMapping("/{id}")
    public ResponseEntity<VehicleDocument> obtenerPorId(
            @PathVariable Long id) {
        return ResponseEntity.ok(documentService.obtenerPorId(id));
    }

    // POST /api/v1/vehicles/documentos : Registra un nuevo documento acoplándolo a un vehículo existente
    @PostMapping
    public ResponseEntity<VehicleDocument> agregar(
            @Valid @RequestBody VehicleDocumentDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(documentService.agregar(dto));
    }

    // PUT /api/v1/vehicles/documentos/1 : Sobreescribe los metadatos y fechas de un documento específico
    @PutMapping("/{id}")
    public ResponseEntity<VehicleDocument> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody VehicleDocumentDTO dto) {
        return ResponseEntity.ok(documentService.actualizar(id, dto));
    }

    // PATCH /api/v1/vehicles/documentos/1/invalidar : Deshabilita de forma parcial la vigencia de un papel o padrón
    @PatchMapping("/{id}/invalidar")
    public ResponseEntity<VehicleDocument> invalidar(@PathVariable Long id) {
        return ResponseEntity.ok(documentService.invalidar(id));
    }

    // DELETE /api/v1/vehicles/documentos/1 : Remueve físicamente el registro documental de la base de datos
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        documentService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    // GET /api/v1/vehicles/documentos/vehiculo/1 : Extrae todo el historial de papeles adjuntos a un vehículo por ID
    @GetMapping("/vehiculo/{vehicleId}")
    public ResponseEntity<List<VehicleDocument>> obtenerPorVehiculo(
            @PathVariable Long vehicleId) {
        return ResponseEntity.ok(
                documentService.obtenerPorVehiculo(vehicleId));
    }

    // GET /api/v1/vehicles/documentos/vehiculo/1/vigentes : Filtra exclusivamente los certificados vigentes y no expirados de un móvil
    @GetMapping("/vehiculo/{vehicleId}/vigentes")
    public ResponseEntity<List<VehicleDocument>> obtenerVigentesPorVehiculo(
            @PathVariable Long vehicleId) {
        return ResponseEntity.ok(
                documentService.obtenerVigentesPorVehiculo(vehicleId));
    }

    // GET /api/v1/vehicles/documentos/proximos-a-vencer : Lista los documentos cuya fecha de expiración se acerca al límite de alerta
    @GetMapping("/proximos-a-vencer")
    public ResponseEntity<List<VehicleDocument>> obtenerProximosAVencer() {
        return ResponseEntity.ok(documentService.obtenerProximosAVencer());
    }

    // GET /api/v1/vehicles/documentos/vencidos : Lista todos los registros cuya fecha de expiración es inferior a la actual
    @GetMapping("/vencidos")
    public ResponseEntity<List<VehicleDocument>> obtenerVencidos() {
        return ResponseEntity.ok(documentService.obtenerVencidos());
    }

    // GET /api/v1/vehicles/documentos/vehiculo/1/ordenados : Obtiene los papeles de un vehículo ordenados cronológicamente por emisión
    @GetMapping("/vehiculo/{vehicleId}/ordenados")
    public ResponseEntity<List<VehicleDocument>> obtenerPorVehiculoOrdenados(
            @PathVariable Long vehicleId) {
        return ResponseEntity.ok(
                documentService.obtenerPorVehiculoOrdenados(vehicleId));
    }

    // GET /api/v1/vehicles/documentos/ultimos : Devuelve las últimas 10 inserciones documentales del sistema global
    @GetMapping("/ultimos")
    public ResponseEntity<List<VehicleDocument>> obtenerUltimosRegistrados() {
        return ResponseEntity.ok(documentService.obtenerUltimosRegistrados());
    }
}