// Recibe las peticiones HTTP de Transaction Service y retorna ResponseEntity con JSON
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


    // GET /api/v1/transactions : Lista todas las transacciones
    @GetMapping
    public ResponseEntity<List<Transaction>> obtenerTodas() {
        return ResponseEntity.ok(transactionService.obtenerTodas());
    }

    // GET /api/v1/transactions/1 : Busca una transacción por ID
    @GetMapping("/{id}")
    public ResponseEntity<Transaction> obtenerPorId(
            @PathVariable Long id) {
        return ResponseEntity.ok(transactionService.obtenerPorId(id));
    }

    // POST /api/v1/transactions : Registra una nueva transacción
    @PostMapping
    public ResponseEntity<Transaction> registrar(
            @Valid @RequestBody TransactionDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(transactionService.registrar(dto));
    }

    // PATCH /api/v1/transactions/1/completar : Cambia el estado a COMPLETADA
    @PatchMapping("/{id}/completar")
    public ResponseEntity<Transaction> completar(
            @PathVariable Long id) {
        return ResponseEntity.ok(transactionService.completar(id));
    }

    // PATCH /api/v1/transactions/1/rechazar : Cambia el estado a RECHAZADA
    @PatchMapping("/{id}/rechazar")
    public ResponseEntity<Transaction> rechazar(@PathVariable Long id) {
        return ResponseEntity.ok(transactionService.rechazar(id));
    }

    // PATCH /api/v1/transactions/1/anular : Cambia el estado a ANULADA
    @PatchMapping("/{id}/anular")
    public ResponseEntity<Transaction> anular(@PathVariable Long id) {
        return ResponseEntity.ok(transactionService.anular(id));
    }

    // DELETE /api/v1/transactions/1 : Elimina físicamente una transacción por ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        transactionService.eliminar(id);
        return ResponseEntity.noContent().build();
    }


    // GET /api/v1/transactions/usuario/12345678-9 : Filtra transacciones de un usuario por RUT
    @GetMapping("/usuario/{rut}")
    public ResponseEntity<List<Transaction>> obtenerPorUsuario(
            @PathVariable String rut) {
        return ResponseEntity.ok(
                transactionService.obtenerPorUsuario(rut));
    }

    // GET /api/v1/transactions/tipo/PAGO_MULTA : Filtra transacciones por tipo
    @GetMapping("/tipo/{tipo}")
    public ResponseEntity<List<Transaction>> obtenerPorTipo(
            @PathVariable String tipo) {
        return ResponseEntity.ok(transactionService.obtenerPorTipo(tipo));
    }

    // GET /api/v1/transactions/estado/PENDIENTE : Filtra transacciones por estado
    @GetMapping("/estado/{estado}")
    public ResponseEntity<List<Transaction>> obtenerPorEstado(
            @PathVariable String estado) {
        return ResponseEntity.ok(
                transactionService.obtenerPorEstado(estado));
    }

    // GET /api/v1/transactions/usuario/12345678-9/estado/COMPLETADA : Filtra transacciones combinando RUT y Estado
    @GetMapping("/usuario/{rut}/estado/{estado}")
    public ResponseEntity<List<Transaction>> obtenerPorUsuarioYEstado(
            @PathVariable String rut,
            @PathVariable String estado) {
        return ResponseEntity.ok(
                transactionService.obtenerPorUsuarioYEstado(rut, estado));
    }

    // GET /api/v1/transactions/fechas?desde=...&hasta=... : Filtra transacciones en un rango de fechas
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

    // GET /api/v1/transactions/monto/mayor?valor=10000 : Filtra transacciones con monto superior al valor enviado
    @GetMapping("/monto/mayor")
    public ResponseEntity<List<Transaction>> obtenerPorMontoMayorA(
            @RequestParam BigDecimal valor) {
        return ResponseEntity.ok(
                transactionService.obtenerPorMontoMayorA(valor));
    }

    // GET /api/v1/transactions/buscar?descripcion=multa : Busca transacciones por coincidencia en la descripción
    @GetMapping("/buscar")
    public ResponseEntity<List<Transaction>> buscarPorDescripcion(
            @RequestParam String descripcion) {
        return ResponseEntity.ok(
                transactionService.buscarPorDescripcion(descripcion));
    }

    // GET /api/v1/transactions/usuario/12345678-9/ordenadas : Obtiene transacciones del usuario de la más reciente a la más antigua
    @GetMapping("/usuario/{rut}/ordenadas")
    public ResponseEntity<List<Transaction>> obtenerPorUsuarioOrdenadas(
            @PathVariable String rut) {
        return ResponseEntity.ok(
                transactionService.obtenerPorUsuarioOrdenadas(rut));
    }

    // GET /api/v1/transactions/ultimas : Devuelve las últimas 10 transacciones del sistema
    @GetMapping("/ultimas")
    public ResponseEntity<List<Transaction>> obtenerUltimas() {
        return ResponseEntity.ok(
                transactionService.obtenerUltimasTransacciones());
    }

    // GET /api/v1/transactions/estadisticas/estado/COMPLETADA : Cuenta el total de transacciones en un estado
    @GetMapping("/estadisticas/estado/{estado}")
    public ResponseEntity<Map<String, Long>> contarPorEstado(
            @PathVariable String estado) {
        long total = transactionService.contarPorEstado(estado);
        return ResponseEntity.ok(Map.of("total", total));
    }

    // GET /api/v1/transactions/estadisticas/tipo/PAGO_MULTA : Cuenta el total de transacciones de un tipo
    @GetMapping("/estadisticas/tipo/{tipo}")
    public ResponseEntity<Map<String, Long>> contarPorTipo(
            @PathVariable String tipo) {
        long total = transactionService.contarPorTipo(tipo);
        return ResponseEntity.ok(Map.of("total", total));
    }
}