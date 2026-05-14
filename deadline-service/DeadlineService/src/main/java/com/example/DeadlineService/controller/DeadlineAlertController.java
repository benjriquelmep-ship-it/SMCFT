// controller/DeadlineAlertController.java
package com.example.DeadlineService.controller;

import com.example.DeadlineService.dto.DeadlineAlertDTO;
import com.example.DeadlineService.model.DeadlineAlert;
import com.example.DeadlineService.service.DeadlineAlertService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/deadline-alerts")
@RequiredArgsConstructor
public class DeadlineAlertController {

    private final DeadlineAlertService alertService;

    // GET /api/v1/deadline-alerts
    @GetMapping
    public ResponseEntity<List<DeadlineAlert>> obtenerTodas() {
        return ResponseEntity.ok(alertService.obtenerTodas());
    }

    // GET /api/v1/deadline-alerts/1
    @GetMapping("/{id}")
    public ResponseEntity<DeadlineAlert> obtenerPorId(
            @PathVariable Long id) {
        return ResponseEntity.ok(alertService.obtenerPorId(id));
    }

    // POST /api/v1/deadline-alerts
    @PostMapping
    public ResponseEntity<DeadlineAlert> crear(
            @Valid @RequestBody DeadlineAlertDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(alertService.crear(dto));
    }

    // POST /api/v1/deadline-alerts/automatica/1
    @PostMapping("/automatica/{deadlineId}")
    public ResponseEntity<DeadlineAlert> generarAutomatica(
            @PathVariable Long deadlineId) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(alertService
                        .generarAlertaAutomatica(deadlineId));
    }

    // PATCH /api/v1/deadline-alerts/1/enviada
    @PatchMapping("/{id}/enviada")
    public ResponseEntity<DeadlineAlert> marcarComoEnviada(
            @PathVariable Long id) {
        return ResponseEntity.ok(alertService.marcarComoEnviada(id));
    }

    // DELETE /api/v1/deadline-alerts/1
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        alertService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    // GET /api/v1/deadline-alerts/deadline/1
    @GetMapping("/deadline/{deadlineId}")
    public ResponseEntity<List<DeadlineAlert>> obtenerPorDeadline(
            @PathVariable Long deadlineId) {
        return ResponseEntity.ok(
                alertService.obtenerPorDeadline(deadlineId));
    }

    // GET /api/v1/deadline-alerts/no-enviadas
    @GetMapping("/no-enviadas")
    public ResponseEntity<List<DeadlineAlert>> obtenerNoEnviadas() {
        return ResponseEntity.ok(alertService.obtenerNoEnviadas());
    }

    // GET /api/v1/deadline-alerts/enviadas
    @GetMapping("/enviadas")
    public ResponseEntity<List<DeadlineAlert>> obtenerEnviadas() {
        return ResponseEntity.ok(alertService.obtenerEnviadas());
    }

    // GET /api/v1/deadline-alerts/urgentes
    @GetMapping("/urgentes")
    public ResponseEntity<List<DeadlineAlert>> obtenerUrgentes() {
        return ResponseEntity.ok(
                alertService.obtenerUrgentesNoEnviadas());
    }

    // GET /api/v1/deadline-alerts/vencidos
    @GetMapping("/vencidos")
    public ResponseEntity<List<DeadlineAlert>> obtenerVencidos() {
        return ResponseEntity.ok(
                alertService.obtenerVencidasNoEnviadas());
    }

    // GET /api/v1/deadline-alerts/deadline/1/ordenadas
    @GetMapping("/deadline/{deadlineId}/ordenadas")
    public ResponseEntity<List<DeadlineAlert>> obtenerOrdenadas(
            @PathVariable Long deadlineId) {
        return ResponseEntity.ok(
                alertService.obtenerPorDeadlineOrdenadas(deadlineId));
    }

    // GET /api/v1/deadline-alerts/ultimas
    @GetMapping("/ultimas")
    public ResponseEntity<List<DeadlineAlert>> obtenerUltimas() {
        return ResponseEntity.ok(alertService.obtenerUltimasAlertas());
    }

    // GET /api/v1/deadline-alerts/estadisticas/pendientes
    @GetMapping("/estadisticas/pendientes")
    public ResponseEntity<Map<String, Long>> contarNoEnviadas() {
        long total = alertService.contarNoEnviadas();
        return ResponseEntity.ok(Map.of("total", total));
    }
}