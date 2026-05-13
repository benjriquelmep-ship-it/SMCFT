package com.example.TransactionService.controller;

import com.example.TransactionService.dto.TransactionDTO;
import com.example.TransactionService.model.Transaction;
import com.example.TransactionService.service.TransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;

    // -------------------------------------------------------
    // CRUD BÁSICO
    // -------------------------------------------------------

    // GET /api/v1/transactions
    @GetMapping
    public ResponseEntity<List<Transaction>> obtenerTodas() {
        return ResponseEntity.ok(transactionService.obtenerTodas());
    }

    // GET /api/v1/transactions/1
    @GetMapping("/{id}")
    public ResponseEntity<Transaction> obtenerPorId(
            @PathVariable Long id) {
        return ResponseEntity.ok(transactionService.obtenerPorId(id));
    }

    // POST /api/v1/transactions
    @PostMapping
    public ResponseEntity<Transaction> registrar(
            @Valid @RequestBody TransactionDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(transactionService.registrar(dto));
    }

    // PATCH /api/v1/transactions/1/completar
    @PatchMapping("/{id}/completar")
    public ResponseEntity<Transaction> completar(
            @PathVariable Long id) {
        return ResponseEntity.ok(transactionService.completar(id));
    }

    // PATCH /api/v1/transactions/1/rechazar
    @PatchMapping("/{id}/rechazar")
    public ResponseEntity<Transaction> rechazar(@PathVariable Long id) {
        return ResponseEntity.ok(transactionService.rechazar(id));
    }

    // PATCH /api/v1/transactions/1/anular
    @PatchMapping("/{id}/anular")
    public ResponseEntity<Transaction> anular(@PathVariable Long id) {
        return ResponseEntity.ok(transactionService.anular(id));
    }

    // DELETE /api/v1/transactions/1
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        transactionService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    // -------------------------------------------------------
    // CONSULTAS DERIVADAS
    // -------------------------------------------------------

    // GET /api/v1/transactions/usuario/12345678-9
    @GetMapping("/usuario/{rut}")
    public ResponseEntity<List<Transaction>> obtenerPorUsuario(
            @PathVariable String rut) {
        return ResponseEntity.ok(
                transactionService.obtenerPorUsuario(rut));
    }

    // GET /api/v1/transactions/tipo/PAGO_MULTA
    @GetMapping("/tipo/{tipo}")
    public ResponseEntity<List<Transaction>> obtenerPorTipo(
            @PathVariable String tipo) {
        return ResponseEntity.ok(transactionService.obtenerPorTipo(tipo));
    }

    // GET /api/v1/transactions/estado/PENDIENTE
    @GetMapping("/estado/{estado}")
    public ResponseEntity<List<Transaction>> obtenerPorEstado(
            @PathVariable String estado) {
        return ResponseEntity.ok(
                transactionService.obtenerPorEstado(estado));
    }

    // GET /api/v1/transactions/usuario/12345678-9/estado/COMPLETADA
    @GetMapping("/usuario/{rut}/estado/{estado}")
    public ResponseEntity<List<Transaction>> obtenerPorUsuarioYEstado(
            @PathVariable String rut,
            @PathVariable String estado) {
        return ResponseEntity.ok(
                transactionService.obtenerPorUsuarioYEstado(rut, estado));
    }

    // GET /api/v1/transactions/fechas?desde=...&hasta=...
    @GetMapping("/fechas")
    public ResponseEntity<List<Transaction>> obtenerPorFechas(
            @RequestParam String desde,
            @RequestParam String hasta) {
        LocalDateTime fechaDesde = LocalDateTime.parse(desde);
        LocalDateTime fechaHasta = LocalDateTime.parse(hasta);
        return ResponseEntity.ok(
                transactionService.obtenerPorRangoFechas(
                        fechaDesde, fechaHasta));
    }

    // GET /api/v1/transactions/monto/mayor?valor=10000
    @GetMapping("/monto/mayor")
    public ResponseEntity<List<Transaction>> obtenerPorMontoMayorA(
            @RequestParam BigDecimal valor) {
        return ResponseEntity.ok(
                transactionService.obtenerPorMontoMayorA(valor));
    }

    // GET /api/v1/transactions/buscar?descripcion=multa
    @GetMapping("/buscar")
    public ResponseEntity<List<Transaction>> buscarPorDescripcion(
            @RequestParam String descripcion) {
        return ResponseEntity.ok(
                transactionService.buscarPorDescripcion(descripcion));
    }

    // GET /api/v1/transactions/usuario/12345678-9/ordenadas
    @GetMapping("/usuario/{rut}/ordenadas")
    public ResponseEntity<List<Transaction>> obtenerPorUsuarioOrdenadas(
            @PathVariable String rut) {
        return ResponseEntity.ok(
                transactionService.obtenerPorUsuarioOrdenadas(rut));
    }

    // GET /api/v1/transactions/ultimas
    @GetMapping("/ultimas")
    public ResponseEntity<List<Transaction>> obtenerUltimas() {
        return ResponseEntity.ok(
                transactionService.obtenerUltimasTransacciones());
    }

    // GET /api/v1/transactions/estadisticas/estado/COMPLETADA
    @GetMapping("/estadisticas/estado/{estado}")
    public ResponseEntity<Map<String, Long>> contarPorEstado(
            @PathVariable String estado) {
        long total = transactionService.contarPorEstado(estado);
        return ResponseEntity.ok(Map.of("total", total));
    }

    // GET /api/v1/transactions/estadisticas/tipo/PAGO_MULTA
    @GetMapping("/estadisticas/tipo/{tipo}")
    public ResponseEntity<Map<String, Long>> contarPorTipo(
            @PathVariable String tipo) {
        long total = transactionService.contarPorTipo(tipo);
        return ResponseEntity.ok(Map.of("total", total));
    }
}