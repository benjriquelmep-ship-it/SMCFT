// Recibe las peticiones HTTP para los deadlines del sistema fronterizo
// Llama al Service y retorna ResponseEntity con JSON
// Nunca tiene lógica de negocio directamente
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

    // DeadlineService contiene toda la lógica de negocio
    // El Controller solo recibe y responde — nunca tiene lógica propia
    private final DeadlineService deadlineService;

    // GET /api/v1/deadlines : Devuelve todos los deadlines del sistema
    @GetMapping
    public ResponseEntity<List<Deadline>> obtenerTodos() {
        return ResponseEntity.ok(deadlineService.obtenerTodos());
    }

    // GET /api/v1/deadlines/1 : Devuelve un deadline específico por su id
    @GetMapping("/{id}")
    public ResponseEntity<Deadline> obtenerPorId(
            @PathVariable Long id) {
        return ResponseEntity.ok(deadlineService.obtenerPorId(id));
    }

    // POST /api/v1/deadlines : Registra un nuevo deadline para un vehículo
    @PostMapping
    public ResponseEntity<Deadline> registrar(
            @Valid @RequestBody DeadlineDTO dto) {

        // HTTP 201 = se creó un nuevo recurso exitosamente
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(deadlineService.registrar(dto));
    }

    // PATCH /api/v1/deadlines/1/cerrar
    // Cierra un deadline — el vehículo salió del país antes del plazo
    // observaciones es opcional — puede incluir comentarios adicionales
    @PatchMapping("/{id}/cerrar")
    public ResponseEntity<Deadline> cerrar(
            @PathVariable Long id,
            @RequestParam(required = false) String observaciones) {
        return ResponseEntity.ok(
                deadlineService.cerrar(id, observaciones));
    }

    // PATCH /api/v1/deadlines/1/vencer
    // Marca un deadline como vencido — el vehículo no salió a tiempo
    // Solo funciona si el deadline está ACTIVO
    @PatchMapping("/{id}/vencer")
    public ResponseEntity<Deadline> vencer(@PathVariable Long id) {
        return ResponseEntity.ok(deadlineService.vencer(id));
    }

    // DELETE /api/v1/deadlines/1
    // Elimina un deadline por su id
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        deadlineService.eliminar(id);
        // HTTP 204 = operación exitosa sin contenido en la respuesta
        return ResponseEntity.noContent().build();
    }

    // GET /api/v1/deadlines/1/dias-restantes
    // Calcula cuántos días le quedan al deadline antes de vencer
    // Si el número es negativo → el deadline ya venció
    @GetMapping("/{id}/dias-restantes")
    public ResponseEntity<Map<String, Long>> calcularDiasRestantes(
            @PathVariable Long id) {
        long dias = deadlineService.calcularDiasRestantes(id);
        return ResponseEntity.ok(Map.of("diasRestantes", dias));
    }

    // GET /api/v1/deadlines/patente/ABC123
    // Devuelve todos los deadlines de un vehículo específico
    @GetMapping("/patente/{patente}")
    public ResponseEntity<List<Deadline>> obtenerPorPatente(
            @PathVariable String patente) {
        return ResponseEntity.ok(
                deadlineService.obtenerPorPatente(patente));
    }

    // GET /api/v1/deadlines/conductor/12345678-9
    // Devuelve todos los deadlines de un conductor específico
    @GetMapping("/conductor/{rut}")
    public ResponseEntity<List<Deadline>> obtenerPorConductor(
            @PathVariable String rut) {
        return ResponseEntity.ok(
                deadlineService.obtenerPorConductor(rut));
    }

    // GET /api/v1/deadlines/estado/ACTIVO
    // Devuelve deadlines por estado (ACTIVO, VENCIDO, CERRADO)
    @GetMapping("/estado/{estado}")
    public ResponseEntity<List<Deadline>> obtenerPorEstado(
            @PathVariable String estado) {
        return ResponseEntity.ok(
                deadlineService.obtenerPorEstado(estado));
    }

    // GET /api/v1/deadlines/tipo/ADMISION_TEMPORAL
    // Devuelve deadlines por tipo : ADMISION_TEMPORAL   → vehículo extranjero con plazo de permanencia
    // RETORNO_OBLIGATORIO → vehículo que debe regresar al país de origen
    @GetMapping("/tipo/{tipo}")
    public ResponseEntity<List<Deadline>> obtenerPorTipo(
            @PathVariable String tipo) {
        return ResponseEntity.ok(deadlineService.obtenerPorTipo(tipo));
    }

    // GET /api/v1/deadlines/proximos-a-vencer
    // Devuelve deadlines ACTIVOS que vencen en los próximos 15 días
    @GetMapping("/proximos-a-vencer")
    public ResponseEntity<List<Deadline>> obtenerProximosAVencer() {
        return ResponseEntity.ok(
                deadlineService.obtenerProximosAVencer());
    }

    // GET /api/v1/deadlines/activos/ordenados
    // Devuelve deadlines ACTIVOS ordenados del que vence antes al que vence después
    @GetMapping("/activos/ordenados")
    public ResponseEntity<List<Deadline>> obtenerActivosOrdenados() {
        return ResponseEntity.ok(
                deadlineService.obtenerActivosOrdenados());
    }

    // GET /api/v1/deadlines/ultimos
    // Devuelve los últimos 10 deadlines registrados en el sistema
    @GetMapping("/ultimos")
    public ResponseEntity<List<Deadline>> obtenerUltimos() {
        return ResponseEntity.ok(
                deadlineService.obtenerUltimosDeadlines());
    }

    // GET /api/v1/deadlines/estadisticas/estado/ACTIVO
    // Cuenta cuántos deadlines hay con ese estado
    @GetMapping("/estadisticas/estado/{estado}")
    public ResponseEntity<Map<String, Long>> contarPorEstado(
            @PathVariable String estado) {
        long total = deadlineService.contarPorEstado(estado);
        return ResponseEntity.ok(Map.of("total", total));
    }
}