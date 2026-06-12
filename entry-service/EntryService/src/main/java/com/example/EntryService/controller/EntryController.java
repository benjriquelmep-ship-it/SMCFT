// Recibe las peticiones HTTP del Entry Service
// Llama al Service y retorna ResponseEntity con JSON
// Nunca tiene lógica de negocio directamente
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

@RestController
@RequestMapping("/api/v1/entries")

@RequiredArgsConstructor
public class EntryController {

    // EntryService contiene toda la lógica de negocio
    // El Controller solo recibe y responde — nunca tiene lógica propia
    private final EntryService entryService;

    // GET /api/v1/entries
    // Devuelve todos los ingresos del sistema
    @GetMapping
    public ResponseEntity<List<Entry>> obtenerTodos() {
        return ResponseEntity.ok(entryService.obtenerTodos());
    }

    // GET /api/v1/entries/1
    // Devuelve un ingreso específico por su id
    // Deadline Service llama a este endpoint para verificar ingresos
    @GetMapping("/{id}")
    public ResponseEntity<Entry> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(entryService.obtenerPorId(id));
    }

    // POST /api/v1/entries
    // Registra un nuevo ingreso al país
    @PostMapping
    public ResponseEntity<Entry> registrar(
            @Valid @RequestBody EntryDTO dto) {
        // HTTP 201 = se creó un nuevo recurso exitosamente
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(entryService.registrar(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Entry> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody EntryDTO dto) {
        return ResponseEntity.ok(entryService.actualizar(id, dto));
    }

    // PATCH /api/v1/entries/1/autorizar
    // El fiscalizador autoriza el ingreso — cambia estado a AUTORIZADO
    // rutFiscalizador = obligatorio, quien autoriza
    // observaciones = opcional, comentarios adicionales
    @PatchMapping("/{id}/autorizar")
    public ResponseEntity<Entry> autorizar(
            @PathVariable Long id,
            @RequestParam String rutFiscalizador,
            @RequestParam(required = false) String observaciones) {
        return ResponseEntity.ok(
                entryService.autorizar(id, rutFiscalizador, observaciones));
    }

    // PATCH /api/v1/entries/1/rechazar
    // El fiscalizador rechaza el ingreso — cambia estado a RECHAZADO
    // Al rechazar el vehículo vuelve a su estado anterior
    @PatchMapping("/{id}/rechazar")
    public ResponseEntity<Entry> rechazar(
            @PathVariable Long id,
            @RequestParam String rutFiscalizador,
            @RequestParam(required = false) String observaciones) {
        return ResponseEntity.ok(
                entryService.rechazar(id, rutFiscalizador, observaciones));
    }

    // DELETE /api/v1/entries/1
    // Elimina un ingreso por su id
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        entryService.eliminar(id);
        // HTTP 204 = operación exitosa sin contenido en la respuesta
        return ResponseEntity.noContent().build();
    }

    // GET /api/v1/entries/patente/ABC123
    // Devuelve todos los ingresos de un vehículo específico
    @GetMapping("/patente/{patente}")
    public ResponseEntity<List<Entry>> obtenerPorPatente(
            @PathVariable String patente) {
        return ResponseEntity.ok(
                entryService.obtenerPorPatente(patente));
    }

    // GET /api/v1/entries/conductor/12345678-9
    // Devuelve todos los ingresos de un conductor específico
    @GetMapping("/conductor/{rut}")
    public ResponseEntity<List<Entry>> obtenerPorConductor(
            @PathVariable String rut) {
        return ResponseEntity.ok(
                entryService.obtenerPorConductor(rut));
    }

    // GET /api/v1/entries/estado/PENDIENTE
    // Devuelve ingresos por estado (PENDIENTE, AUTORIZADO, RECHAZADO)
    @GetMapping("/estado/{estado}")
    public ResponseEntity<List<Entry>> obtenerPorEstado(
            @PathVariable String estado) {
        return ResponseEntity.ok(entryService.obtenerPorEstado(estado));
    }

    // GET /api/v1/entries/tipo/RETORNO
    // Devuelve ingresos por tipo
    // RETORNO           → vehículo que regresa al país
    // ADMISION_TEMPORAL → vehículo extranjero que entra temporalmente
    @GetMapping("/tipo/{tipoIngreso}")
    public ResponseEntity<List<Entry>> obtenerPorTipo(
            @PathVariable String tipoIngreso) {
        return ResponseEntity.ok(
                entryService.obtenerPorTipoIngreso(tipoIngreso));
    }

    // GET /api/v1/entries/paso/Los Libertadores
    // Devuelve todos los ingresos de un paso fronterizo específico
    @GetMapping("/paso/{pasoFronterizo}")
    public ResponseEntity<List<Entry>> obtenerPorPaso(
            @PathVariable String pasoFronterizo) {
        return ResponseEntity.ok(
                entryService.obtenerPorPasoFronterizo(pasoFronterizo));
    }

    // GET /api/v1/entries/fiscalizador/12345678-9
    // Devuelve todos los ingresos que procesó un fiscalizador específico
    @GetMapping("/fiscalizador/{rut}")
    public ResponseEntity<List<Entry>> obtenerPorFiscalizador(
            @PathVariable String rut) {
        return ResponseEntity.ok(
                entryService.obtenerPorFiscalizador(rut));
    }

    // GET /api/v1/entries/patente/ABC123/estado/AUTORIZADO
    // Combina dos filtros: patente + estado
    @GetMapping("/patente/{patente}/estado/{estado}")
    public ResponseEntity<List<Entry>> obtenerPorPatenteYEstado(
            @PathVariable String patente,
            @PathVariable String estado) {
        return ResponseEntity.ok(
                entryService.obtenerPorPatenteYEstado(patente, estado));
    }

    // GET /api/v1/entries/fechas?desde=2025-01-01T00:00:00&hasta=2025-12-31T23:59:59
    // Devuelve ingresos registrados en un rango de fechas
    @GetMapping("/fechas")
    public ResponseEntity<List<Entry>> obtenerPorFechas(
            @RequestParam String desde,
            @RequestParam String hasta) {
        // Convierte el texto a fecha
        // Ej: "2025-01-01T00:00:00" → LocalDateTime del 1 enero 2025
        LocalDateTime fechaDesde = LocalDateTime.parse(desde);
        LocalDateTime fechaHasta = LocalDateTime.parse(hasta);
        return ResponseEntity.ok(
                entryService.obtenerPorRangoFechas(fechaDesde, fechaHasta));
    }

    // GET /api/v1/entries/buscar?pais=argentina
    // Busca ingresos cuyo país de origen contenga el texto buscado
    // No necesitas escribir el nombre exacto — busca si lo contiene
    @GetMapping("/buscar")
    public ResponseEntity<List<Entry>> buscarPorPais(
            @RequestParam String pais) {
        return ResponseEntity.ok(
                entryService.buscarPorPaisOrigen(pais));
    }

    // GET /api/v1/entries/patente/ABC123/ordenados
    // Devuelve los ingresos de un vehículo del más reciente al más antiguo
    @GetMapping("/patente/{patente}/ordenados")
    public ResponseEntity<List<Entry>> obtenerPorPatenteOrdenados(
            @PathVariable String patente) {
        return ResponseEntity.ok(
                entryService.obtenerPorPatenteOrdenados(patente));
    }

    // GET /api/v1/entries/ultimos
    // Devuelve los últimos 10 ingresos registrados en el sistema
    @GetMapping("/ultimos")
    public ResponseEntity<List<Entry>> obtenerUltimosIngresos() {
        return ResponseEntity.ok(entryService.obtenerUltimosIngresos());
    }

    // GET /api/v1/entries/estadisticas/estado/AUTORIZADO
    // Cuenta cuántos ingresos hay con ese estado
    @GetMapping("/estadisticas/estado/{estado}")
    public ResponseEntity<Map<String, Long>> contarPorEstado(
            @PathVariable String estado) {
        long total = entryService.contarPorEstado(estado);
        return ResponseEntity.ok(Map.of("total", total));
    }

    // GET /api/v1/entries/estadisticas/tipo/RETORNO
    // Cuenta cuántos ingresos hay de ese tipo
    @GetMapping("/estadisticas/tipo/{tipo}")
    public ResponseEntity<Map<String, Long>> contarPorTipo(
            @PathVariable String tipo) {
        long total = entryService.contarPorTipo(tipo);
        return ResponseEntity.ok(Map.of("total", total));
    }
}