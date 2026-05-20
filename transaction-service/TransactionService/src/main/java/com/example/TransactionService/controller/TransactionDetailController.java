// Recibe las peticiones HTTP de Transaction Detail Service y retorna ResponseEntity con JSON
package com.example.TransactionService.controller;

import com.example.TransactionService.dto.TransactionDetailDTO;
import com.example.TransactionService.model.TransactionDetail;
import com.example.TransactionService.service.TransactionDetailService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/transaction-details")
@RequiredArgsConstructor
public class TransactionDetailController {

    private final TransactionDetailService detailService;

    // GET /api/v1/transaction-details : Lista todos los detalles de transacciones del sistema
    @GetMapping
    public ResponseEntity<List<TransactionDetail>> obtenerTodos() {
        return ResponseEntity.ok(detailService.obtenerTodos());
    }

    // GET /api/v1/transaction-details/1 : Busca un detalle de transacción específico por ID
    @GetMapping("/{id}")
    public ResponseEntity<TransactionDetail> obtenerPorId(
            @PathVariable Long id) {
        return ResponseEntity.ok(detailService.obtenerPorId(id));
    }

    // POST /api/v1/transaction-details : Agrega un nuevo concepto o línea de detalle a la transacción
    @PostMapping
    public ResponseEntity<TransactionDetail> agregar(
            @Valid @RequestBody TransactionDetailDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(detailService.agregar(dto));
    }

    // PUT /api/v1/transaction-details/1 : Actualiza por completo un detalle existente mediante su ID
    @PutMapping("/{id}")
    public ResponseEntity<TransactionDetail> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody TransactionDetailDTO dto) {
        return ResponseEntity.ok(detailService.actualizar(id, dto));
    }

    // DELETE /api/v1/transaction-details/1 : Elimina físicamente un registro de detalle por ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        detailService.eliminar(id);
        return ResponseEntity.noContent().build();
    }


    // GET /api/v1/transaction-details/transaccion/1 : Filtra todos los detalles asociados a una transacción principal
    @GetMapping("/transaccion/{transactionId}")
    public ResponseEntity<List<TransactionDetail>> obtenerPorTransaccion(
            @PathVariable Long transactionId) {
        return ResponseEntity.ok(
                detailService.obtenerPorTransaccion(transactionId));
    }

    // GET /api/v1/transaction-details/tipo/COBRO : Filtra los detalles de transacciones por su tipo (ej: COBRO, DESCUENTO)
    @GetMapping("/tipo/{tipoDetalle}")
    public ResponseEntity<List<TransactionDetail>> obtenerPorTipo(
            @PathVariable String tipoDetalle) {
        return ResponseEntity.ok(
                detailService.obtenerPorTipoDetalle(tipoDetalle));
    }

    // GET /api/v1/transaction-details/transaccion/1/tipo/COBRO : Combina filtros: ID de transacción + Tipo de detalle
    @GetMapping("/transaccion/{transactionId}/tipo/{tipoDetalle}")
    public ResponseEntity<List<TransactionDetail>> obtenerPorTransaccionYTipo(
            @PathVariable Long transactionId,
            @PathVariable String tipoDetalle) {
        return ResponseEntity.ok(
                detailService.obtenerPorTransaccionYTipo(
                        transactionId, tipoDetalle));
    }

    // GET /api/v1/transaction-details/buscar?concepto=multa : Busca detalles mediante coincidencia parcial de texto en el concepto
    @GetMapping("/buscar")
    public ResponseEntity<List<TransactionDetail>> buscarPorConcepto(
            @RequestParam String concepto) {
        return ResponseEntity.ok(
                detailService.buscarPorConcepto(concepto));
    }

    // GET /api/v1/transaction-details/transaccion/1/ordenados : Obtiene los detalles de una transacción ordenados por valor descendente
    @GetMapping("/transaccion/{transactionId}/ordenados")
    public ResponseEntity<List<TransactionDetail>> obtenerOrdenados(
            @PathVariable Long transactionId) {
        return ResponseEntity.ok(
                detailService.obtenerPorTransaccionOrdenados(transactionId));
    }

    // GET /api/v1/transaction-details/ultimos : Devuelve los últimos 10 detalles registrados en la base de datos
    @GetMapping("/ultimos")
    public ResponseEntity<List<TransactionDetail>> obtenerUltimos() {
        return ResponseEntity.ok(detailService.obtenerUltimosDetalles());
    }
}