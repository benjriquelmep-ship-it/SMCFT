// controller/BorderCrossingController.java

package com.example.BorderCrossingService.controller;

import com.example.BorderCrossingService.dto.BorderCrossingDTO;
import com.example.BorderCrossingService.model.BorderCrossing;
import com.example.BorderCrossingService.service.BorderCrossingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

// @RestController indica que esta clase maneja peticiones HTTP y devuelve JSON
@RestController
// Todas las rutas empiezan con /api/v1/border-crossings
@RequestMapping("/api/v1/border-crossings")
@RequiredArgsConstructor
public class BorderCrossingController {

    private final BorderCrossingService crossingService;

    // -------------------------------------------------------
    // CRUD BÁSICO
    // -------------------------------------------------------

    // GET /api/v1/border-crossings
    @GetMapping
    public ResponseEntity<List<BorderCrossing>> obtenerTodos() {
        return ResponseEntity.ok(crossingService.obtenerTodos());
    }

    // GET /api/v1/border-crossings/1
    @GetMapping("/{id}")
    public ResponseEntity<BorderCrossing> obtenerPorId(
            @PathVariable Long id) {
        return ResponseEntity.ok(crossingService.obtenerPorId(id));
    }

    // POST /api/v1/border-crossings
    // @Valid activa las validaciones del BorderCrossingDTO
    @PostMapping
    public ResponseEntity<BorderCrossing> registrar(
            @Valid @RequestBody BorderCrossingDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(crossingService.registrar(dto));
    }

    // PATCH /api/v1/border-crossings/1/autorizar
    // El fiscalizador autoriza el cruce
    @PatchMapping("/{id}/autorizar")
    public ResponseEntity<BorderCrossing> autorizar(
            @PathVariable Long id,
            @RequestParam String rutFiscalizador,
            @RequestParam(required = false) String observaciones) {
        return ResponseEntity.ok(
                crossingService.autorizar(id, rutFiscalizador,
                        observaciones));
    }

    // PATCH /api/v1/border-crossings/1/rechazar
    // El fiscalizador rechaza el cruce
    @PatchMapping("/{id}/rechazar")
    public ResponseEntity<BorderCrossing> rechazar(
            @PathVariable Long id,
            @RequestParam String rutFiscalizador,
            @RequestParam(required = false) String observaciones) {
        return ResponseEntity.ok(
                crossingService.rechazar(id, rutFiscalizador,
                        observaciones));
    }

    // DELETE /api/v1/border-crossings/1
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        crossingService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    // -------------------------------------------------------
    // CONSULTAS DERIVADAS
    // -------------------------------------------------------

    // GET /api/v1/border-crossings/patente/ABC123
    @GetMapping("/patente/{patente}")
    public ResponseEntity<List<BorderCrossing>> obtenerPorPatente(
            @PathVariable String patente) {
        return ResponseEntity.ok(
                crossingService.obtenerPorPatente(patente));
    }

    // GET /api/v1/border-crossings/conductor/12345678-9
    @GetMapping("/conductor/{rut}")
    public ResponseEntity<List<BorderCrossing>> obtenerPorConductor(
            @PathVariable String rut) {
        return ResponseEntity.ok(
                crossingService.obtenerPorConductor(rut));
    }

    // GET /api/v1/border-crossings/estado/PENDIENTE
    @GetMapping("/estado/{estado}")
    public ResponseEntity<List<BorderCrossing>> obtenerPorEstado(
            @PathVariable String estado) {
        return ResponseEntity.ok(
                crossingService.obtenerPorEstado(estado));
    }

    // GET /api/v1/border-crossings/paso/Los Libertadores
    @GetMapping("/paso/{pasoFronterizo}")
    public ResponseEntity<List<BorderCrossing>> obtenerPorPaso(
            @PathVariable String pasoFronterizo) {
        return ResponseEntity.ok(
                crossingService.obtenerPorPasoFronterizo(pasoFronterizo));
    }

    // GET /api/v1/border-crossings/fiscalizador/12345678-9
    @GetMapping("/fiscalizador/{rut}")
    public ResponseEntity<List<BorderCrossing>> obtenerPorFiscalizador(
            @PathVariable String rut) {
        return ResponseEntity.ok(
                crossingService.obtenerPorFiscalizador(rut));
    }

    // GET /api/v1/border-crossings/patente/ABC123/estado/AUTORIZADO
    @GetMapping("/patente/{patente}/estado/{estado}")
    public ResponseEntity<List<BorderCrossing>> obtenerPorPatenteYEstado(
            @PathVariable String patente,
            @PathVariable String estado) {
        return ResponseEntity.ok(
                crossingService.obtenerPorPatenteYEstado(patente, estado));
    }

    // GET /api/v1/border-crossings/fechas?desde=...&hasta=...
    @GetMapping("/fechas")
    public ResponseEntity<List<BorderCrossing>> obtenerPorFechas(
            @RequestParam String desde,
            @RequestParam String hasta) {
        LocalDateTime fechaDesde = LocalDateTime.parse(desde);
        LocalDateTime fechaHasta = LocalDateTime.parse(hasta);
        return ResponseEntity.ok(
                crossingService.obtenerPorRangoFechas(
                        fechaDesde, fechaHasta));
    }

    // GET /api/v1/border-crossings/buscar?pais=argentina
    @GetMapping("/buscar")
    public ResponseEntity<List<BorderCrossing>> buscarPorPais(
            @RequestParam String pais) {
        return ResponseEntity.ok(
                crossingService.buscarPorPaisDestino(pais));
    }

    // GET /api/v1/border-crossings/patente/ABC123/ordenados
    @GetMapping("/patente/{patente}/ordenados")
    public ResponseEntity<List<BorderCrossing>> obtenerPorPatenteOrdenados(
            @PathVariable String patente) {
        return ResponseEntity.ok(
                crossingService.obtenerPorPatenteOrdenados(patente));
    }

    // GET /api/v1/border-crossings/ultimos
    @GetMapping("/ultimos")
    public ResponseEntity<List<BorderCrossing>> obtenerUltimosCruces() {
        return ResponseEntity.ok(crossingService.obtenerUltimosCruces());
    }

    // GET /api/v1/border-crossings/estadisticas/estado/PENDIENTE
    @GetMapping("/estadisticas/estado/{estado}")
    public ResponseEntity<Map<String, Long>> contarPorEstado(
            @PathVariable String estado) {
        long total = crossingService.contarPorEstado(estado);
        return ResponseEntity.ok(Map.of("total", total));
    }

    // GET /api/v1/border-crossings/estadisticas/paso/Los Libertadores
    @GetMapping("/estadisticas/paso/{pasoFronterizo}")
    public ResponseEntity<Map<String, Long>> contarPorPaso(
            @PathVariable String pasoFronterizo) {
        long total = crossingService.contarPorPasoFronterizo(
                pasoFronterizo);
        return ResponseEntity.ok(Map.of("total", total));
    }
}