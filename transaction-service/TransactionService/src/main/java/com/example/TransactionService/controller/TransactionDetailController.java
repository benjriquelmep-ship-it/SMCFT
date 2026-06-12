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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@RestController
@RequestMapping("/api/v1/transaction-details")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class TransactionDetailController {

    private final TransactionDetailService detailService;

    // GET /api/v1/transaction-details : Lista todos los detalles de transacciones del sistema
    @Operation(summary = "Obtener Todos", description = "GET /api/v1/transaction-details : Lista todos los detalles de transacciones del sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operación exitosa"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "Sin permisos")
    })
    @GetMapping
    public ResponseEntity<List<TransactionDetail>> obtenerTodos() {
        return ResponseEntity.ok(detailService.obtenerTodos());
    }

    // GET /api/v1/transaction-details/1 : Busca un detalle de transacción específico por ID
    @Operation(summary = "Obtener Por Id", description = "GET /api/v1/transaction-details/1 : Busca un detalle de transacción específico por ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operación exitosa"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "Sin permisos")
    })
    @GetMapping("/{id}")
    public ResponseEntity<TransactionDetail> obtenerPorId(
            @PathVariable Long id) {
        return ResponseEntity.ok(detailService.obtenerPorId(id));
    }

    // POST /api/v1/transaction-details : Agrega un nuevo concepto o línea de detalle a la transacción
    @Operation(summary = "Agregar", description = "POST /api/v1/transaction-details : Agrega un nuevo concepto o línea de detalle a la transacción")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Operación exitosa"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "Sin permisos")
    })
    @PostMapping
    public ResponseEntity<TransactionDetail> agregar(
            @Valid @RequestBody TransactionDetailDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(detailService.agregar(dto));
    }

    // PUT /api/v1/transaction-details/1 : Actualiza por completo un detalle existente mediante su ID
    @Operation(summary = "Actualizar", description = "PUT /api/v1/transaction-details/1 : Actualiza por completo un detalle existente mediante su ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operación exitosa"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "Sin permisos")
    })
    @PutMapping("/{id}")
    public ResponseEntity<TransactionDetail> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody TransactionDetailDTO dto) {
        return ResponseEntity.ok(detailService.actualizar(id, dto));
    }

    // DELETE /api/v1/transaction-details/1 : Elimina físicamente un registro de detalle por ID
    @Operation(summary = "Eliminar", description = "DELETE /api/v1/transaction-details/1 : Elimina físicamente un registro de detalle por ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Operación exitosa"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "Sin permisos")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        detailService.eliminar(id);
        return ResponseEntity.noContent().build();
    }


    // GET /api/v1/transaction-details/transaccion/1 : Filtra todos los detalles asociados a una transacción principal
    @Operation(summary = "Obtener Por Transaccion", description = "GET /api/v1/transaction-details/transaccion/1 : Filtra todos los detalles asociados a una transacción principal")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operación exitosa"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "Sin permisos")
    })
    @GetMapping("/transaccion/{transactionId}")
    public ResponseEntity<List<TransactionDetail>> obtenerPorTransaccion(
            @PathVariable Long transactionId) {
        return ResponseEntity.ok(
                detailService.obtenerPorTransaccion(transactionId));
    }

    // GET /api/v1/transaction-details/tipo/COBRO : Filtra los detalles de transacciones por su tipo (ej: COBRO, DESCUENTO)
    @Operation(summary = "Obtener Por Tipo", description = "GET /api/v1/transaction-details/tipo/COBRO : Filtra los detalles de transacciones por su tipo (ej: COBRO, DESCUENTO)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operación exitosa"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "Sin permisos")
    })
    @GetMapping("/tipo/{tipoDetalle}")
    public ResponseEntity<List<TransactionDetail>> obtenerPorTipo(
            @PathVariable String tipoDetalle) {
        return ResponseEntity.ok(
                detailService.obtenerPorTipoDetalle(tipoDetalle));
    }

    // GET /api/v1/transaction-details/transaccion/1/tipo/COBRO : Combina filtros: ID de transacción + Tipo de detalle
    @Operation(summary = "Obtener Por Transaccion Y Tipo", description = "GET /api/v1/transaction-details/transaccion/1/tipo/COBRO : Combina filtros: ID de transacción + Tipo de detalle")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operación exitosa"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "Sin permisos")
    })
    @GetMapping("/transaccion/{transactionId}/tipo/{tipoDetalle}")
    public ResponseEntity<List<TransactionDetail>> obtenerPorTransaccionYTipo(
            @PathVariable Long transactionId,
            @PathVariable String tipoDetalle) {
        return ResponseEntity.ok(
                detailService.obtenerPorTransaccionYTipo(
                        transactionId, tipoDetalle));
    }

    // GET /api/v1/transaction-details/buscar?concepto=multa : Busca detalles mediante coincidencia parcial de texto en el concepto
    @Operation(summary = "Buscar Por Concepto", description = "GET /api/v1/transaction-details/buscar?concepto=multa : Busca detalles mediante coincidencia parcial de texto en el concepto")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operación exitosa"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "Sin permisos")
    })
    @GetMapping("/buscar")
    public ResponseEntity<List<TransactionDetail>> buscarPorConcepto(
            @RequestParam String concepto) {
        return ResponseEntity.ok(
                detailService.buscarPorConcepto(concepto));
    }

    // GET /api/v1/transaction-details/transaccion/1/ordenados : Obtiene los detalles de una transacción ordenados por valor descendente
    @Operation(summary = "Obtener Ordenados", description = "GET /api/v1/transaction-details/transaccion/1/ordenados : Obtiene los detalles de una transacción ordenados por valor descendente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operación exitosa"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "Sin permisos")
    })
    @GetMapping("/transaccion/{transactionId}/ordenados")
    public ResponseEntity<List<TransactionDetail>> obtenerOrdenados(
            @PathVariable Long transactionId) {
        return ResponseEntity.ok(
                detailService.obtenerPorTransaccionOrdenados(transactionId));
    }

    // GET /api/v1/transaction-details/ultimos : Devuelve los últimos 10 detalles registrados en la base de datos
    @Operation(summary = "Obtener Ultimos", description = "GET /api/v1/transaction-details/ultimos : Devuelve los últimos 10 detalles registrados en la base de datos")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operación exitosa"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "Sin permisos")
    })
    @GetMapping("/ultimos")
    public ResponseEntity<List<TransactionDetail>> obtenerUltimos() {
        return ResponseEntity.ok(detailService.obtenerUltimosDetalles());
    }
}