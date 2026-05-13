// controller/SanitaryController.java
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

@RestController
@RequestMapping("/api/v1/sanitary")
@RequiredArgsConstructor
public class SanitaryController {

    private final SanitaryService sanitaryService;

    // -------------------------------------------------------
    // CRUD BÁSICO
    // -------------------------------------------------------

    // GET /api/v1/sanitary
    @GetMapping
    public ResponseEntity<List<Sanitary>> obtenerTodas() {
        return ResponseEntity.ok(sanitaryService.obtenerTodas());
    }

    // GET /api/v1/sanitary/1
    @GetMapping("/{id}")
    public ResponseEntity<Sanitary> obtenerPorId(
            @PathVariable Long id) {
        return ResponseEntity.ok(sanitaryService.obtenerPorId(id));
    }

    // POST /api/v1/sanitary
    @PostMapping
    public ResponseEntity<Sanitary> registrar(
            @Valid @RequestBody SanitaryDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(sanitaryService.registrar(dto));
    }

    // PATCH /api/v1/sanitary/1/aprobar
    @PatchMapping("/{id}/aprobar")
    public ResponseEntity<Sanitary> aprobar(
            @PathVariable Long id,
            @RequestParam(required = false) String observaciones) {
        return ResponseEntity.ok(
                sanitaryService.aprobar(id, observaciones));
    }

    // PATCH /api/v1/sanitary/1/rechazar
    @PatchMapping("/{id}/rechazar")
    public ResponseEntity<Sanitary> rechazar(
            @PathVariable Long id,
            @RequestParam(required = false) String observaciones) {
        return ResponseEntity.ok(
                sanitaryService.rechazar(id, observaciones));
    }

    // DELETE /api/v1/sanitary/1
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        sanitaryService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    // -------------------------------------------------------
    // CONSULTAS DERIVADAS
    // -------------------------------------------------------

    // GET /api/v1/sanitary/patente/ABC123
    @GetMapping("/patente/{patente}")
    public ResponseEntity<List<Sanitary>> obtenerPorPatente(
            @PathVariable String patente) {
        return ResponseEntity.ok(
                sanitaryService.obtenerPorPatente(patente));
    }

    // GET /api/v1/sanitary/conductor/12345678-9
    @GetMapping("/conductor/{rut}")
    public ResponseEntity<List<Sanitary>> obtenerPorConductor(
            @PathVariable String rut) {
        return ResponseEntity.ok(
                sanitaryService.obtenerPorConductor(rut));
    }

    // GET /api/v1/sanitary/inspector/12345678-9
    @GetMapping("/inspector/{rut}")
    public ResponseEntity<List<Sanitary>> obtenerPorInspector(
            @PathVariable String rut) {
        return ResponseEntity.ok(
                sanitaryService.obtenerPorInspector(rut));
    }

    // GET /api/v1/sanitary/resultado/APROBADO
    @GetMapping("/resultado/{resultado}")
    public ResponseEntity<List<Sanitary>> obtenerPorResultado(
            @PathVariable String resultado) {
        return ResponseEntity.ok(
                sanitaryService.obtenerPorResultado(resultado));
    }

    // GET /api/v1/sanitary/paso/Los Libertadores
    @GetMapping("/paso/{pasoFronterizo}")
    public ResponseEntity<List<Sanitary>> obtenerPorPaso(
            @PathVariable String pasoFronterizo) {
        return ResponseEntity.ok(
                sanitaryService.obtenerPorPasoFronterizo(pasoFronterizo));
    }

    // GET /api/v1/sanitary/patente/ABC123/resultado/APROBADO
    @GetMapping("/patente/{patente}/resultado/{resultado}")
    public ResponseEntity<List<Sanitary>> obtenerPorPatenteYResultado(
            @PathVariable String patente,
            @PathVariable String resultado) {
        return ResponseEntity.ok(
                sanitaryService.obtenerPorPatenteYResultado(
                        patente, resultado));
    }

    // GET /api/v1/sanitary/fechas?desde=...&hasta=...
    @GetMapping("/fechas")
    public ResponseEntity<List<Sanitary>> obtenerPorFechas(
            @RequestParam String desde,
            @RequestParam String hasta) {
        LocalDateTime fechaDesde = LocalDateTime.parse(desde);
        LocalDateTime fechaHasta = LocalDateTime.parse(hasta);
        return ResponseEntity.ok(
                sanitaryService.obtenerPorRangoFechas(
                        fechaDesde, fechaHasta));
    }

    // GET /api/v1/sanitary/patente/ABC123/ordenadas
    @GetMapping("/patente/{patente}/ordenadas")
    public ResponseEntity<List<Sanitary>> obtenerPorPatenteOrdenadas(
            @PathVariable String patente) {
        return ResponseEntity.ok(
                sanitaryService.obtenerPorPatenteOrdenadas(patente));
    }

    // GET /api/v1/sanitary/pendientes/ordenadas
    @GetMapping("/pendientes/ordenadas")
    public ResponseEntity<List<Sanitary>> obtenerPendientesOrdenadas() {
        return ResponseEntity.ok(
                sanitaryService.obtenerPendientesOrdenadas());
    }

    // GET /api/v1/sanitary/ultimas
    @GetMapping("/ultimas")
    public ResponseEntity<List<Sanitary>> obtenerUltimas() {
        return ResponseEntity.ok(
                sanitaryService.obtenerUltimasInspecciones());
    }

    // GET /api/v1/sanitary/estadisticas/resultado/APROBADO
    @GetMapping("/estadisticas/resultado/{resultado}")
    public ResponseEntity<Map<String, Long>> contarPorResultado(
            @PathVariable String resultado) {
        long total = sanitaryService.contarPorResultado(resultado);
        return ResponseEntity.ok(Map.of("total", total));
    }
}