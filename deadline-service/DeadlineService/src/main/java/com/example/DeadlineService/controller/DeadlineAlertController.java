// Recibe las peticiones HTTP para las alertas de deadlines
// Llama al Service y retorna ResponseEntity con JSON
// Nunca tiene lógica de negocio directamente
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

    // DeadlineAlertService contiene toda la lógica de negocio
    // El Controller solo recibe y responde — nunca tiene lógica propia
    private final DeadlineAlertService alertService;

    // GET /api/v1/deadline-alerts : Devuelve todas las alertas del sistema
    @GetMapping
    public ResponseEntity<List<DeadlineAlert>> obtenerTodas() {
        return ResponseEntity.ok(alertService.obtenerTodas());
    }

    // GET /api/v1/deadline-alerts/1 : Devuelve una alerta específica por su id
    @GetMapping("/{id}")
    public ResponseEntity<DeadlineAlert> obtenerPorId(
            @PathVariable Long id) {
        return ResponseEntity.ok(alertService.obtenerPorId(id));
    }

    // POST /api/v1/deadline-alerts : Crea una alerta manual para un deadline específico
    @PostMapping
    public ResponseEntity<DeadlineAlert> crear(
            @Valid @RequestBody DeadlineAlertDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(alertService.crear(dto));
    }

    // POST /api/v1/deadline-alerts/automatica/1 : Genera una alerta automáticamente según los días restantes del deadline
    @PostMapping("/automatica/{deadlineId}")
    public ResponseEntity<DeadlineAlert> generarAutomatica(
            @PathVariable Long deadlineId) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(alertService
                        .generarAlertaAutomatica(deadlineId));
    }

    // PATCH /api/v1/deadline-alerts/1/enviada : Marca una alerta como enviada al Notification Service
    @PatchMapping("/{id}/enviada")
    public ResponseEntity<DeadlineAlert> marcarComoEnviada(
            @PathVariable Long id) {
        return ResponseEntity.ok(alertService.marcarComoEnviada(id));
    }

    // DELETE /api/v1/deadline-alerts/1 : Elimina una alerta por su id
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        alertService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    // GET /api/v1/deadline-alerts/deadline/1 : Devuelve todas las alertas que pertenecen a un deadline específico
    @GetMapping("/deadline/{deadlineId}")
    public ResponseEntity<List<DeadlineAlert>> obtenerPorDeadline(
            @PathVariable Long deadlineId) {
        return ResponseEntity.ok(
                alertService.obtenerPorDeadline(deadlineId));
    }

    // GET /api/v1/deadline-alerts/no-enviadas : Devuelve todas las alertas que aún no fueron enviadas
    @GetMapping("/no-enviadas")
    public ResponseEntity<List<DeadlineAlert>> obtenerNoEnviadas() {
        return ResponseEntity.ok(alertService.obtenerNoEnviadas());
    }

    // GET /api/v1/deadline-alerts/enviadas : Devuelve todas las alertas que ya fueron enviadas al Notification Service
    @GetMapping("/enviadas")
    public ResponseEntity<List<DeadlineAlert>> obtenerEnviadas() {
        return ResponseEntity.ok(alertService.obtenerEnviadas());
    }

    // GET /api/v1/deadline-alerts/urgentes : Devuelve alertas de tipo URGENTE que aún no fueron enviadas
    @GetMapping("/urgentes")
    public ResponseEntity<List<DeadlineAlert>> obtenerUrgentes() {
        return ResponseEntity.ok(
                alertService.obtenerUrgentesNoEnviadas());
    }

    // GET /api/v1/deadline-alerts/vencidos : Devuelve alertas de tipo VENCIDO que aún no fueron enviadas
    @GetMapping("/vencidos")
    public ResponseEntity<List<DeadlineAlert>> obtenerVencidos() {
        return ResponseEntity.ok(
                alertService.obtenerVencidasNoEnviadas());
    }

    // GET /api/v1/deadline-alerts/deadline/1/ordenadas : Devuelve las alertas de un deadline ordenadas por días restantes
    @GetMapping("/deadline/{deadlineId}/ordenadas")
    public ResponseEntity<List<DeadlineAlert>> obtenerOrdenadas(
            @PathVariable Long deadlineId) {
        return ResponseEntity.ok(
                alertService.obtenerPorDeadlineOrdenadas(deadlineId));
    }

    // GET /api/v1/deadline-alerts/ultimas : Devuelve las últimas 10 alertas registradas en el sistema
    @GetMapping("/ultimas")
    public ResponseEntity<List<DeadlineAlert>> obtenerUltimas() {
        return ResponseEntity.ok(alertService.obtenerUltimasAlertas());
    }

    // GET /api/v1/deadline-alerts/estadisticas/pendientes : Cuenta cuántas alertas no han sido enviadas todavía
    @GetMapping("/estadisticas/pendientes")
    public ResponseEntity<Map<String, Long>> contarNoEnviadas() {
        long total = alertService.contarNoEnviadas();
        return ResponseEntity.ok(Map.of("total", total));
    }
}