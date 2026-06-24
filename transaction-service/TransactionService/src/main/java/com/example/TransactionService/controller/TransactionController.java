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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/v1/transactions")
@RequiredArgsConstructor
@Tag(name = "Transacciones", description = "Endpoints para la gestión, control contable y auditoría de aranceles y multas aduaneras")
@SecurityRequirement(name = "bearerAuth")
public class TransactionController {

    private final TransactionService transactionService;

    @Operation(summary = "Listar transacciones", description = "Recupera el libro histórico consolidado con todos los movimientos financieros registrados en la frontera.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Colección de transacciones obtenida con éxito"),
            @ApiResponse(responseCode = "401", description = "No autenticado - Falta token Bearer JWT válido"),
            @ApiResponse(responseCode = "403", description = "Sin permisos - Rol operativo insuficiente")
    })
    @GetMapping
    public ResponseEntity<List<Transaction>> obtenerTodas() {
        return ResponseEntity.ok(transactionService.obtenerTodas());
    }

    // GET /api/v1/transactions/1 : Busca una transacción por ID
    @Operation(summary = "Obtener transacción por ID", description = "Busca el estado físico y la glosa de una transacción monetaria a través de su clave primaria.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Transacción localizada correctamente"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "404", description = "El ID de transacción solicitado no existe")
    })
    @GetMapping("/{id}")
    public ResponseEntity<Transaction> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(transactionService.obtenerPorId(id));
    }

    // POST /api/v1/transactions : Registra una nueva transacción
    @Operation(summary = "Registrar una nueva transacción", description = "Valida el payload financiero y da de alta un cobro, tasa o arancel en estado PENDIENTE.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Transacción financiera facturada exitosamente"),
            @ApiResponse(responseCode = "400", description = "Estructura de datos errónea o montos fuera de rango reglamentario")
    })
    @PostMapping
    public ResponseEntity<Transaction> registrar(
            @Valid @RequestBody TransactionDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(transactionService.registrar(dto));
    }

    // PATCH /api/v1/transactions/1/completar : Cambia el estado a COMPLETADA
    @Operation(summary = "Completar transacción", description = "Actualiza el estado de la transacción a COMPLETADA tras ratificar el pago íntegro de los montos.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Transacción completada y cerrada con éxito"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "404", description = "El registro a completar no fue localizado")
    })
    @PatchMapping("/{id}/completar")
    public ResponseEntity<Transaction> completar(@PathVariable Long id) {
        return ResponseEntity.ok(transactionService.completar(id));
    }

    // PATCH /api/v1/transactions/1/rechazar : Cambia el estado a RECHAZADA
    @Operation(summary = "Rechazar transacción", description = "Marca la transacción en estado RECHAZADA debido a inconsistencias o rechazo de pasarela de pago.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Transacción rechazada correctamente"),
            @ApiResponse(responseCode = "401", description = "No autenticado")
    })
    @PatchMapping("/{id}/rechazar")
    public ResponseEntity<Transaction> rechazar(@PathVariable Long id) {
        return ResponseEntity.ok(transactionService.rechazar(id));
    }

    // PATCH /api/v1/transactions/1/anular : Cambia el estado a ANULADA
    @Operation(summary = "Anular transacción", description = "Ejecuta una nota de crédito o anulación lógica sobre el folio financiero por errores de digitación o de procedimiento.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Folio aduanero anulado exitosamente"),
            @ApiResponse(responseCode = "401", description = "No autenticado")
    })
    @PatchMapping("/{id}/anular")
    public ResponseEntity<Transaction> anular(@PathVariable Long id) {
        return ResponseEntity.ok(transactionService.anular(id));
    }

    // DELETE /api/v1/transactions/1 : Elimina físicamente una transacción por ID
    @Operation(summary = "Eliminar transacción físicamente", description = "Remueve permanentemente el registro financiero de la base de datos central por motivos de purga u auditoría radical.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Transacción eliminada con éxito. Sin contenido."),
            @ApiResponse(responseCode = "401", description = "No autenticado")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        transactionService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    // GET /api/v1/transactions/usuario/12345678-9 : Filtra transacciones de un usuario por RUT
    @Operation(summary = "Listar transacciones por Usuario", description = "Filtra la cuenta corriente aduanera extrayendo los movimientos asociados al RUN de un ciudadano.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Historial monetario del usuario devuelto de forma exitosa"),
            @ApiResponse(responseCode = "401", description = "No autenticado")
    })
    @GetMapping("/usuario/{rut}")
    public ResponseEntity<List<Transaction>> obtenerPorUsuario(
            @PathVariable String rut) {
        return ResponseEntity.ok(transactionService.obtenerPorUsuario(rut));
    }

    // GET /api/v1/transactions/tipo/PAGO_MULTA : Filtra transacciones por tipo
    @Operation(summary = "Listar transacciones por Tipo de Cobro", description = "Aísla las operaciones contables según su categoría (ej: PAGO_MULTA, PAGO_TASA, DEVOLUCION).")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Registros agrupados por tipo obtenidos con éxito"),
            @ApiResponse(responseCode = "401", description = "No autenticado")
    })
    @GetMapping("/tipo/{tipo}")
    public ResponseEntity<List<Transaction>> obtenerPorTipo(
            @PathVariable String tipo) {
        return ResponseEntity.ok(transactionService.obtenerPorTipo(tipo));
    }

    // GET /api/v1/transactions/estado/PENDIENTE : Filtra transacciones por estado
    @Operation(summary = "Listar transacciones por Estado", description = "Agrupa los folios contables según su situación actual de flujo (PENDIENTE, COMPLETADA, ANULADA).")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Registros agrupados por estado devueltos correctamente"),
            @ApiResponse(responseCode = "401", description = "No autenticado")
    })
    @GetMapping("/estado/{estado}")
    public ResponseEntity<List<Transaction>> obtenerPorEstado(
            @PathVariable String estado) {
        return ResponseEntity.ok(transactionService.obtenerPorEstado(estado));
    }

    // GET /api/v1/transactions/usuario/12345678-9/estado/COMPLETADA : Filtra transacciones combinando RUT y Estado
    @Operation(summary = "Listar transacciones por Usuario y Estado", description = "Cruza filtros para hallar las liquidaciones ya saldadas o pendientes de un RUN en particular.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Filtro cruzado ejecutado exitosamente"),
            @ApiResponse(responseCode = "401", description = "No autenticado")
    })
    @GetMapping("/usuario/{rut}/estado/{estado}")
    public ResponseEntity<List<Transaction>> obtenerPorUsuarioYEstado(
            @PathVariable String rut,
            @PathVariable String estado) {
        return ResponseEntity.ok(
                transactionService.obtenerPorUsuarioYEstado(rut, estado));
    }

    // GET /api/v1/transactions/fechas?desde=...&hasta=... : Filtra transacciones en un rango de fechas
    @Operation(summary = "Listar transacciones por Rango de Fechas", description = "Recupera el extracto contable delimitado por un bloque cronológico temporal cerrado.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Movimientos contenidos en el rango de tiempo devueltos con éxito"),
            @ApiResponse(responseCode = "400", description = "Formato de fecha ISO inválido")
    })
    @GetMapping("/fechas")
    public ResponseEntity<List<Transaction>> obtenerPorFechas(
            @RequestParam String desde,
            @RequestParam String hasta) {
        LocalDateTime fechaDesde = LocalDateTime.parse(desde);
        LocalDateTime fechaHasta = LocalDateTime.parse(hasta);
        return ResponseEntity.ok(
                transactionService.obtenerPorRangoFechas(fechaDesde, fechaHasta));
    }

    // GET /api/v1/transactions/monto/mayor?valor=10000 : Filtra transacciones con monto superior al valor enviado
    @Operation(summary = "Listar transacciones con Monto Superior", description = "Filtro de control aduanero para auditar transacciones cuyo valor neto supere el umbral monetario inyectado.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Registros sobre el umbral económico recuperados exitosamente"),
            @ApiResponse(responseCode = "401", description = "No autenticado")
    })
    @GetMapping("/monto/mayor")
    public ResponseEntity<List<Transaction>> obtenerPorMontoMayorA(
            @RequestParam BigDecimal valor) {
        return ResponseEntity.ok(transactionService.obtenerPorMontoMayorA(valor));
    }

    // GET /api/v1/transactions/buscar?descripcion=multa : Busca transacciones por coincidencia en la descripción
    @Operation(summary = "Buscar transacciones por Glosa/Descripción", description = "Realiza una búsqueda de texto parcial por palabra clave (LIKE) sobre la descripción del trámite.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Resultados de búsqueda textual devueltos correctamente"),
            @ApiResponse(responseCode = "401", description = "No autenticado")
    })
    @GetMapping("/buscar")
    public ResponseEntity<List<Transaction>> buscarPorDescripcion(
            @RequestParam String descripcion) {
        return ResponseEntity.ok(transactionService.buscarPorDescripcion(descripcion));
    }

    // GET /api/v1/transactions/usuario/12345678-9/ordenadas : Obtiene transacciones del usuario de la más reciente a la más antigua
    @Operation(summary = "Listar transacciones de Usuario ordenadas cronológicamente", description = "Retorna el historial de pagos de un RUN ordenado de forma descendente (más recientes primero).")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Historial ordenado devuelto de forma exitosa"),
            @ApiResponse(responseCode = "401", description = "No autenticado")
    })
    @GetMapping("/usuario/{rut}/ordenadas")
    public ResponseEntity<List<Transaction>> obtenerPorUsuarioOrdenadas(
            @PathVariable String rut) {
        return ResponseEntity.ok(transactionService.obtenerPorUsuarioOrdenadas(rut));
    }

    // GET /api/v1/transactions/ultimas : Devuelve las últimas 10 transacciones del sistema
    @Operation(summary = "Listar últimas transacciones facturadas", description = "Panel de auditoría en tiempo real que expone los últimos 10 movimientos liquidados globalmente.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Últimas transacciones leídas de forma correcta"),
            @ApiResponse(responseCode = "401", description = "No autenticado")
    })
    @GetMapping("/ultimas")
    public ResponseEntity<List<Transaction>> obtenerUltimas() {
        return ResponseEntity.ok(transactionService.obtenerUltimasTransacciones());
    }

    // GET /api/v1/transactions/estadisticas/estado/COMPLETADA
    @Operation(summary = "Contar transacciones por Estado", description = "Endpoint estadístico para reportería que devuelve la suma total volumétrica de folios en un estado específico.")
    @GetMapping("/estadisticas/estado/{estado}")
    public ResponseEntity<Map<String, Long>> contarPorEstado(
            @PathVariable String estado) {
        long total = transactionService.contarPorEstado(estado);
        return ResponseEntity.ok(Map.of("total", total));
    }

    // GET /api/v1/transactions/estadisticas/tipo/PAGO_MULTA
    @Operation(summary = "Contar transacciones por Tipo", description = "Endpoint estadístico para reportería que devuelve la suma total volumétrica de folios emitidos bajo un tipo conceptual.")
    @GetMapping("/estadisticas/tipo/{tipo}")
    public ResponseEntity<Map<String, Long>> contarPorTipo(
            @PathVariable String tipo) {
        long total = transactionService.contarPorTipo(tipo);
        return ResponseEntity.ok(Map.of("total", total));
    }
}