// controller/DeadlineController.java
package com.example.DeadlineService.controller;

import com.example.DeadlineService.dto.DeadlineDTO;
import com.example.DeadlineService.model.Deadline;
import com.example.DeadlineService.service.DeadlineService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/deadlines")
@RequiredArgsConstructor
public class DeadlineController {

    private final DeadlineService deadlineService;

    // GET /api/v1/deadlines
    @GetMapping
    public ResponseEntity<List<Deadline>> obtenerTodos() {
        return ResponseEntity.ok(deadlineService.obtenerTodos());
    }

    // GET /api/v1/deadlines/1
    @GetMapping("/{id}")
    public ResponseEntity<Deadline> obtenerPorId(
            @PathVariable Long id) {
        return ResponseEntity.ok(deadlineService.obtenerPorId(id));
    }

    // POST /api/v1/deadlines
    @PostMapping
    public ResponseEntity<Deadline> registrar(
            @Valid @RequestBody DeadlineDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(deadlineService.registrar(dto));
    }

    // PATCH /api/v1/deadlines/1/cerrar
    @PatchMapping("/{id}/cerrar")
    public ResponseEntity<Deadline> cerrar(
            @PathVariable Long id,
            @RequestParam(required = false) String observaciones) {
        return ResponseEntity.ok(
                deadlineService.cerrar(id, observaciones));
    }

    // PATCH /api/v1/deadlines/1/vencer
    @PatchMapping("/{id}/vencer")
    public ResponseEntity<Deadline> vencer(@PathVariable Long id) {
        return ResponseEntity.ok(deadlineService.vencer(id));
    }

    // DELETE /api/v1/deadlines/1
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        deadlineService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    // GET /api/v1/deadlines/1/dias-restantes
    @GetMapping("/{id}/dias-restantes")
    public ResponseEntity<Map<String, Long>> calcularDiasRestantes(
            @PathVariable Long id) {
        long dias = deadlineService.calcularDiasRestantes(id);
        return ResponseEntity.ok(Map.of("diasRestantes", dias));
    }

    // GET /api/v1/deadlines/patente/ABC123
    @GetMapping("/patente/{patente}")
    public ResponseEntity<List<Deadline>> obtenerPorPatente(
            @PathVariable String patente) {
        return ResponseEntity.ok(
                deadlineService.obtenerPorPatente(patente));
    }

    // GET /api/v1/deadlines/conductor/12345678-9
    @GetMapping("/conductor/{rut}")
    public ResponseEntity<List<Deadline>> obtenerPorConductor(
            @PathVariable String rut) {
        return ResponseEntity.ok(
                deadlineService.obtenerPorConductor(rut));
    }

    // GET /api/v1/deadlines/estado/ACTIVO
    @GetMapping("/estado/{estado}")
    public ResponseEntity<List<Deadline>> obtenerPorEstado(
            @PathVariable String estado) {
        return ResponseEntity.ok(
                deadlineService.obtenerPorEstado(estado));
    }

    // GET /api/v1/deadlines/tipo/ADMISION_TEMPORAL
    @GetMapping("/tipo/{tipo}")
    public ResponseEntity<List<Deadline>> obtenerPorTipo(
            @PathVariable String tipo) {
        return ResponseEntity.ok(deadlineService.obtenerPorTipo(tipo));
    }

    // GET /api/v1/deadlines/proximos-a-vencer
    @GetMapping("/proximos-a-vencer")
    public ResponseEntity<List<Deadline>> obtenerProximosAVencer() {
        return ResponseEntity.ok(
                deadlineService.obtenerProximosAVencer());
    }

    // GET /api/v1/deadlines/activos/ordenados
    @GetMapping("/activos/ordenados")
    public ResponseEntity<List<Deadline>> obtenerActivosOrdenados() {
        return ResponseEntity.ok(
                deadlineService.obtenerActivosOrdenados());
    }

    // GET /api/v1/deadlines/ultimos
    @GetMapping("/ultimos")
    public ResponseEntity<List<Deadline>> obtenerUltimos() {
        return ResponseEntity.ok(
                deadlineService.obtenerUltimosDeadlines());
    }

    // GET /api/v1/deadlines/estadisticas/estado/ACTIVO
    @GetMapping("/estadisticas/estado/{estado}")
    public ResponseEntity<Map<String, Long>> contarPorEstado(
            @PathVariable String estado) {
        long total = deadlineService.contarPorEstado(estado);
        return ResponseEntity.ok(Map.of("total", total));
    }
}