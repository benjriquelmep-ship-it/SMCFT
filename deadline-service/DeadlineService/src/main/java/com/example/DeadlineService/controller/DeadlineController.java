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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@RestController
@RequestMapping("/api/v1/deadlines")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class DeadlineController {

    // DeadlineService contiene toda la lógica de negocio
    // El Controller solo recibe y responde — nunca tiene lógica propia
    private final DeadlineService deadlineService;

    // GET /api/v1/deadlines : Devuelve todos los deadlines del sistema
    @Operation(summary = "Obtener Todos", description = "GET /api/v1/deadlines : Devuelve todos los deadlines del sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operación exitosa"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "Sin permisos")
    })
    @GetMapping
    public ResponseEntity<List<Deadline>> obtenerTodos() {
        return ResponseEntity.ok(deadlineService.obtenerTodos());
    }

    // GET /api/v1/deadlines/1 : Devuelve un deadline específico por su id
    @Operation(summary = "Obtener Por Id", description = "GET /api/v1/deadlines/1 : Devuelve un deadline específico por su id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operación exitosa"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "Sin permisos")
    })
    @GetMapping("/{id}")
    public ResponseEntity<Deadline> obtenerPorId(
            @PathVariable Long id) {
        return ResponseEntity.ok(deadlineService.obtenerPorId(id));
    }

    // POST /api/v1/deadlines : Registra un nuevo deadline para un vehículo
    @Operation(summary = "Registrar", description = "POST /api/v1/deadlines : Registra un nuevo deadline para un vehículo")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Operación exitosa"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "Sin permisos")
    })
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
    @Operation(summary = "Cerrar", description = "observaciones es opcional — puede incluir comentarios adicionales")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operación exitosa"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "Sin permisos")
    })
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
    @Operation(summary = "Vencer", description = "Solo funciona si el deadline está ACTIVO")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operación exitosa"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "Sin permisos")
    })
    @PatchMapping("/{id}/vencer")
    public ResponseEntity<Deadline> vencer(@PathVariable Long id) {
        return ResponseEntity.ok(deadlineService.vencer(id));
    }

    // DELETE /api/v1/deadlines/1
    // Elimina un deadline por su id
    @Operation(summary = "Eliminar", description = "Elimina un deadline por su id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Operación exitosa"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "Sin permisos")
    })
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
    @Operation(summary = "Obtener Por Patente", description = "Devuelve todos los deadlines de un vehículo específico")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operación exitosa"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "Sin permisos")
    })
    @GetMapping("/patente/{patente}")
    public ResponseEntity<List<Deadline>> obtenerPorPatente(
            @PathVariable String patente) {
        return ResponseEntity.ok(
                deadlineService.obtenerPorPatente(patente));
    }

    // GET /api/v1/deadlines/conductor/12345678-9
    // Devuelve todos los deadlines de un conductor específico
    @Operation(summary = "Obtener Por Conductor", description = "Devuelve todos los deadlines de un conductor específico")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operación exitosa"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "Sin permisos")
    })
    @GetMapping("/conductor/{rut}")
    public ResponseEntity<List<Deadline>> obtenerPorConductor(
            @PathVariable String rut) {
        return ResponseEntity.ok(
                deadlineService.obtenerPorConductor(rut));
    }

    // GET /api/v1/deadlines/estado/ACTIVO
    // Devuelve deadlines por estado (ACTIVO, VENCIDO, CERRADO)
    @Operation(summary = "Obtener Por Estado", description = "Devuelve deadlines por estado (ACTIVO, VENCIDO, CERRADO)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operación exitosa"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "Sin permisos")
    })
    @GetMapping("/estado/{estado}")
    public ResponseEntity<List<Deadline>> obtenerPorEstado(
            @PathVariable String estado) {
        return ResponseEntity.ok(
                deadlineService.obtenerPorEstado(estado));
    }

    // GET /api/v1/deadlines/tipo/ADMISION_TEMPORAL
    // Devuelve deadlines por tipo : ADMISION_TEMPORAL   → vehículo extranjero con plazo de permanencia
    // RETORNO_OBLIGATORIO → vehículo que debe regresar al país de origen
    @Operation(summary = "Obtener Por Tipo", description = "RETORNO_OBLIGATORIO → vehículo que debe regresar al país de origen")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operación exitosa"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "Sin permisos")
    })
    @GetMapping("/tipo/{tipo}")
    public ResponseEntity<List<Deadline>> obtenerPorTipo(
            @PathVariable String tipo) {
        return ResponseEntity.ok(deadlineService.obtenerPorTipo(tipo));
    }

    // GET /api/v1/deadlines/proximos-a-vencer
    // Devuelve deadlines ACTIVOS que vencen en los próximos 15 días
    @Operation(summary = "Obtener Proximos A Vencer", description = "Devuelve deadlines ACTIVOS que vencen en los próximos 15 días")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operación exitosa"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "Sin permisos")
    })
    @GetMapping("/proximos-a-vencer")
    public ResponseEntity<List<Deadline>> obtenerProximosAVencer() {
        return ResponseEntity.ok(
                deadlineService.obtenerProximosAVencer());
    }

    // GET /api/v1/deadlines/activos/ordenados
    // Devuelve deadlines ACTIVOS ordenados del que vence antes al que vence después
    @Operation(summary = "Obtener Activos Ordenados", description = "Devuelve deadlines ACTIVOS ordenados del que vence antes al que vence después")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operación exitosa"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "Sin permisos")
    })
    @GetMapping("/activos/ordenados")
    public ResponseEntity<List<Deadline>> obtenerActivosOrdenados() {
        return ResponseEntity.ok(
                deadlineService.obtenerActivosOrdenados());
    }

    // GET /api/v1/deadlines/ultimos
    // Devuelve los últimos 10 deadlines registrados en el sistema
    @Operation(summary = "Obtener Ultimos", description = "Devuelve los últimos 10 deadlines registrados en el sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operación exitosa"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "Sin permisos")
    })
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