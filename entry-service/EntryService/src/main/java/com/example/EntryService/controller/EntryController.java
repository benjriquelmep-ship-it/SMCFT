// controller/EntryController.java

package com.example.EntryService.controller;

import com.example.EntryService.dto.EntryDTO;
import com.example.EntryService.model.Entry;
import com.example.EntryService.service.EntryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

// @RestController indica que esta clase maneja peticiones HTTP
@RestController
// Todas las rutas empiezan con /api/v1/entries
@RequestMapping("/api/v1/entries")
@RequiredArgsConstructor
public class EntryController {

    private final EntryService entryService;

    // -------------------------------------------------------
    // CRUD BÁSICO
    // -------------------------------------------------------

    // GET /api/v1/entries
    @GetMapping
    public ResponseEntity<List<Entry>> obtenerTodos() {
        return ResponseEntity.ok(entryService.obtenerTodos());
    }

    // GET /api/v1/entries/1
    @GetMapping("/{id}")
    public ResponseEntity<Entry> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(entryService.obtenerPorId(id));
    }

    // POST /api/v1/entries
    @PostMapping
    public ResponseEntity<Entry> registrar(
            @Valid @RequestBody EntryDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(entryService.registrar(dto));
    }

    // PATCH /api/v1/entries/1/autorizar
    @PatchMapping("/{id}/autorizar")
    public ResponseEntity<Entry> autorizar(
            @PathVariable Long id,
            @RequestParam String rutFiscalizador,
            @RequestParam(required = false) String observaciones) {
        return ResponseEntity.ok(
                entryService.autorizar(id, rutFiscalizador, observaciones));
    }

    // PATCH /api/v1/entries/1/rechazar
    @PatchMapping("/{id}/rechazar")
    public ResponseEntity<Entry> rechazar(
            @PathVariable Long id,
            @RequestParam String rutFiscalizador,
            @RequestParam(required = false) String observaciones) {
        return ResponseEntity.ok(
                entryService.rechazar(id, rutFiscalizador, observaciones));
    }

    // DELETE /api/v1/entries/1
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        entryService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    // -------------------------------------------------------
    // CONSULTAS DERIVADAS
    // -------------------------------------------------------

    // GET /api/v1/entries/patente/ABC123
    @GetMapping("/patente/{patente}")
    public ResponseEntity<List<Entry>> obtenerPorPatente(
            @PathVariable String patente) {
        return ResponseEntity.ok(
                entryService.obtenerPorPatente(patente));
    }

    // GET /api/v1/entries/conductor/12345678-9
    @GetMapping("/conductor/{rut}")
    public ResponseEntity<List<Entry>> obtenerPorConductor(
            @PathVariable String rut) {
        return ResponseEntity.ok(
                entryService.obtenerPorConductor(rut));
    }

    // GET /api/v1/entries/estado/PENDIENTE
    @GetMapping("/estado/{estado}")
    public ResponseEntity<List<Entry>> obtenerPorEstado(
            @PathVariable String estado) {
        return ResponseEntity.ok(entryService.obtenerPorEstado(estado));
    }

    // GET /api/v1/entries/tipo/RETORNO
    @GetMapping("/tipo/{tipoIngreso}")
    public ResponseEntity<List<Entry>> obtenerPorTipo(
            @PathVariable String tipoIngreso) {
        return ResponseEntity.ok(
                entryService.obtenerPorTipoIngreso(tipoIngreso));
    }

    // GET /api/v1/entries/paso/Los Libertadores
    @GetMapping("/paso/{pasoFronterizo}")
    public ResponseEntity<List<Entry>> obtenerPorPaso(
            @PathVariable String pasoFronterizo) {
        return ResponseEntity.ok(
                entryService.obtenerPorPasoFronterizo(pasoFronterizo));
    }

    // GET /api/v1/entries/fiscalizador/12345678-9
    @GetMapping("/fiscalizador/{rut}")
    public ResponseEntity<List<Entry>> obtenerPorFiscalizador(
            @PathVariable String rut) {
        return ResponseEntity.ok(
                entryService.obtenerPorFiscalizador(rut));
    }

    // GET /api/v1/entries/patente/ABC123/estado/AUTORIZADO
    @GetMapping("/patente/{patente}/estado/{estado}")
    public ResponseEntity<List<Entry>> obtenerPorPatenteYEstado(
            @PathVariable String patente,
            @PathVariable String estado) {
        return ResponseEntity.ok(
                entryService.obtenerPorPatenteYEstado(patente, estado));
    }

    // GET /api/v1/entries/fechas?desde=...&hasta=...
    @GetMapping("/fechas")
    public ResponseEntity<List<Entry>> obtenerPorFechas(
            @RequestParam String desde,
            @RequestParam String hasta) {
        LocalDateTime fechaDesde = LocalDateTime.parse(desde);
        LocalDateTime fechaHasta = LocalDateTime.parse(hasta);
        return ResponseEntity.ok(
                entryService.obtenerPorRangoFechas(fechaDesde, fechaHasta));
    }

    // GET /api/v1/entries/buscar?pais=argentina
    @GetMapping("/buscar")
    public ResponseEntity<List<Entry>> buscarPorPais(
            @RequestParam String pais) {
        return ResponseEntity.ok(
                entryService.buscarPorPaisOrigen(pais));
    }

    // GET /api/v1/entries/patente/ABC123/ordenados
    @GetMapping("/patente/{patente}/ordenados")
    public ResponseEntity<List<Entry>> obtenerPorPatenteOrdenados(
            @PathVariable String patente) {
        return ResponseEntity.ok(
                entryService.obtenerPorPatenteOrdenados(patente));
    }

    // GET /api/v1/entries/ultimos
    @GetMapping("/ultimos")
    public ResponseEntity<List<Entry>> obtenerUltimosIngresos() {
        return ResponseEntity.ok(entryService.obtenerUltimosIngresos());
    }

    // GET /api/v1/entries/estadisticas/estado/AUTORIZADO
    @GetMapping("/estadisticas/estado/{estado}")
    public ResponseEntity<Map<String, Long>> contarPorEstado(
            @PathVariable String estado) {
        long total = entryService.contarPorEstado(estado);
        return ResponseEntity.ok(Map.of("total", total));
    }

    // GET /api/v1/entries/estadisticas/tipo/RETORNO
    @GetMapping("/estadisticas/tipo/{tipo}")
    public ResponseEntity<Map<String, Long>> contarPorTipo(
            @PathVariable String tipo) {
        long total = entryService.contarPorTipo(tipo);
        return ResponseEntity.ok(Map.of("total", total));
    }
}