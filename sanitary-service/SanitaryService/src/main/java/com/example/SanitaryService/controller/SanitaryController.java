// Recibe las peticiones HTTP del Sanitary Service
// Llama al Service y retorna ResponseEntity con JSON
// Nunca tiene lógica de negocio directamente
package com.example.SanitaryService.controller;

import com.example.SanitaryService.dto.SanitaryDTO;
import com.example.SanitaryService.model.Sanitary;
import com.example.SanitaryService.service.SanitaryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController.
@RequestMapping("/api/v1/sanitary")
@RequiredArgsConstructor
public class SanitaryController {

    // SanitaryService contiene toda la lógica de negocio
    // El Controller solo recibe y responde — nunca tiene lógica propia
    private final SanitaryService sanitaryService;

    // GET /api/v1/sanitary
    // Devuelve todas las inspecciones sanitarias del sistema
    @GetMapping
    public ResponseEntity<List<Sanitary>> obtenerTodas() {
        return ResponseEntity.ok(sanitaryService.obtenerTodas());
    }

    // GET /api/v1/sanitary/1
    // Devuelve una inspección específica por su id
    @GetMapping("/{id}")
    public ResponseEntity<Sanitary> obtenerPorId(
            @PathVariable Long id) {
        return ResponseEntity.ok(sanitaryService.obtenerPorId(id));
    }

    // POST /api/v1/sanitary
    // Registra una nueva inspección sanitaria para un vehículo
    @PostMapping
    public ResponseEntity<Sanitary> registrar(
            @Valid @RequestBody SanitaryDTO dto) {
        // HTTP 201 = se creó un nuevo recurso exitosamente
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(sanitaryService.registrar(dto));
    }

    // PATCH /api/v1/sanitary/1/aprobar
    // El inspector aprueba la inspección — resultado APROBADO
    // observaciones es opcional — puede incluir comentarios adicionales
    @PatchMapping("/{id}/aprobar")
    public ResponseEntity<Sanitary> aprobar(
            @PathVariable Long id,
            // required = false → el parámetro es opcional
            // Si no llega → observaciones = null
            @RequestParam(required = false) String observaciones) {
        return ResponseEntity.ok(
                sanitaryService.aprobar(id, observaciones));
    }

    // PATCH /api/v1/sanitary/1/rechazar
    // El inspector rechaza la inspección — resultado RECHAZADO
    // El vehículo no puede cruzar la frontera
    @PatchMapping("/{id}/rechazar")
    public ResponseEntity<Sanitary> rechazar(
            @PathVariable Long id,
            @RequestParam(required = false) String observaciones) {
        return ResponseEntity.ok(
                sanitaryService.rechazar(id, observaciones));
    }

    // DELETE /api/v1/sanitary/1
    // Elimina una inspección por su id
    // ResponseEntity<Void> = respuesta sin body → HTTP 204
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        sanitaryService.eliminar(id);
        // HTTP 204 = operación exitosa sin contenido en la respuesta
        return ResponseEntity.noContent().build();
    }

    // GET /api/v1/sanitary/patente/ABC123
    // Devuelve todas las inspecciones de un vehículo específico
    @GetMapping("/patente/{patente}")
    public ResponseEntity<List<Sanitary>> obtenerPorPatente(
            @PathVariable String patente) {
        return ResponseEntity.ok(
                sanitaryService.obtenerPorPatente(patente));
    }

    // GET /api/v1/sanitary/conductor/12345678-9
    // Devuelve todas las inspecciones de un conductor específico
    @GetMapping("/conductor/{rut}")
    public ResponseEntity<List<Sanitary>> obtenerPorConductor(
            @PathVariable String rut) {
        return ResponseEntity.ok(
                sanitaryService.obtenerPorConductor(rut));
    }

    // GET /api/v1/sanitary/inspector/12345678-9
    // Devuelve todas las inspecciones realizadas por un inspector específico
    @GetMapping("/inspector/{rut}")
    public ResponseEntity<List<Sanitary>> obtenerPorInspector(
            @PathVariable String rut) {
        return ResponseEntity.ok(
                sanitaryService.obtenerPorInspector(rut));
    }

    // GET /api/v1/sanitary/resultado/APROBADO
    // Devuelve inspecciones por resultado (PENDIENTE, APROBADO, RECHAZADO)
    @GetMapping("/resultado/{resultado}")
    public ResponseEntity<List<Sanitary>> obtenerPorResultado(
            @PathVariable String resultado) {
        return ResponseEntity.ok(
                sanitaryService.obtenerPorResultado(resultado));
    }

    // GET /api/v1/sanitary/paso/Los Libertadores
    // Devuelve todas las inspecciones de un paso fronterizo específico
    @GetMapping("/paso/{pasoFronterizo}")
    public ResponseEntity<List<Sanitary>> obtenerPorPaso(
            @PathVariable String pasoFronterizo) {
        return ResponseEntity.ok(
                sanitaryService.obtenerPorPasoFronterizo(pasoFronterizo));
    }

    // GET /api/v1/sanitary/patente/ABC123/resultado/APROBADO
    // Combina dos filtros: patente + resultado
    @GetMapping("/patente/{patente}/resultado/{resultado}")
    public ResponseEntity<List<Sanitary>> obtenerPorPatenteYResultado(
            @PathVariable String patente,
            @PathVariable String resultado) {
        return ResponseEntity.ok(
                sanitaryService.obtenerPorPatenteYResultado(
                        patente, resultado));
    }

    // GET /api/v1/sanitary/fechas?desde=2025-01-01T00:00:00&hasta=2025-12-31T23:59:59
    // Devuelve inspecciones registradas en un rango de fechas
    @GetMapping("/fechas")
    public ResponseEntity<List<Sanitary>> obtenerPorFechas(
            @RequestParam String desde,
            @RequestParam String hasta) {
        // Convierte el texto a fecha
        // Ej: "2025-01-01T00:00:00" → LocalDateTime del 1 enero 2025
        LocalDateTime fechaDesde = LocalDateTime.parse(desde);
        LocalDateTime fechaHasta = LocalDateTime.parse(hasta);
        return ResponseEntity.ok(
                sanitaryService.obtenerPorRangoFechas(
                        fechaDesde, fechaHasta));
    }

    // GET /api/v1/sanitary/patente/ABC123/ordenadas
    // Devuelve inspecciones de un vehículo del más reciente al más antiguo
    @GetMapping("/patente/{patente}/ordenadas")
    public ResponseEntity<List<Sanitary>> obtenerPorPatenteOrdenadas(
            @PathVariable String patente) {
        return ResponseEntity.ok(
                sanitaryService.obtenerPorPatenteOrdenadas(patente));
    }

    // GET /api/v1/sanitary/pendientes/ordenadas
    // Devuelve inspecciones PENDIENTES del más antiguo al más reciente
    // Útil para procesar las inspecciones en orden de llegada
    @GetMapping("/pendientes/ordenadas")
    public ResponseEntity<List<Sanitary>> obtenerPendientesOrdenadas() {
        return ResponseEntity.ok(
                sanitaryService.obtenerPendientesOrdenadas());
    }

    // GET /api/v1/sanitary/ultimas
    // Devuelve las últimas 10 inspecciones registradas en el sistema
    @GetMapping("/ultimas")
    public ResponseEntity<List<Sanitary>> obtenerUltimas() {
        return ResponseEntity.ok(
                sanitaryService.obtenerUltimasInspecciones());
    }

    // GET /api/v1/sanitary/estadisticas/resultado/APROBADO
    // Cuenta cuántas inspecciones hay con ese resultado
    @GetMapping("/estadisticas/resultado/{resultado}")
    public ResponseEntity<Map<String, Long>> contarPorResultado(
            @PathVariable String resultado) {
        long total = sanitaryService.contarPorResultado(resultado);
        return ResponseEntity.ok(Map.of("total", total));
    }
}