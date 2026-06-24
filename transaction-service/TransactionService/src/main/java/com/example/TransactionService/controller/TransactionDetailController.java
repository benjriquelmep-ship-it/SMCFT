package com.example.TransactionService.controller;

import com.example.TransactionService.dto.TransactionDetailDTO;
import com.example.TransactionService.model.TransactionDetail;
import com.example.TransactionService.service.TransactionDetailService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/v1/transaction-details")
@RequiredArgsConstructor
@Tag(name = "Detalles de la Transacción", description = "Endpoints para el desglose pormenorizado, cobro por ítems individuales, impuestos de internación y aranceles específicos")
@SecurityRequirement(name = "bearerAuth")
public class TransactionDetailController {

    private final TransactionDetailService detailService;

    @Operation(summary = "Listar todas las líneas de detalle", description = "Expone el registro global de desgloses e ítems facturados de forma global en la aduana.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Colección de detalles obtenida correctamente"),
            @ApiResponse(responseCode = "401", description = "No autenticado - Falta token Bearer JWT"),
            @ApiResponse(responseCode = "403", description = "Sin privilegios requeridos")
    })
    @GetMapping
    public ResponseEntity<CollectionModel<EntityModel<TransactionDetail>>> obtenerTodos() {
        List<EntityModel<TransactionDetail>> details = detailService.obtenerTodos().stream()
                .map(d -> EntityModel.of(d,
                        linkTo(methodOn(TransactionDetailController.class).obtenerPorId(d.getId())).withSelfRel()))
                .toList();
        return ResponseEntity.ok(CollectionModel.of(details,
                linkTo(methodOn(TransactionDetailController.class).obtenerTodos()).withSelfRel()));
    }

    // GET /api/v1/transaction-details/1 : Busca un detalle de transacción específico por ID
    @Operation(summary = "Obtener línea de detalle por ID", description = "Busca el concepto, la cantidad y el precio de una línea de detalle individual a través de su clave primaria.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Línea de detalle localizada correctamente"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "404", description = "El ID de detalle solicitado no existe en los registros")
    })
    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<TransactionDetail>> obtenerPorId(@PathVariable Long id) {
        TransactionDetail detail = detailService.obtenerPorId(id);
        return ResponseEntity.ok(EntityModel.of(detail,
                linkTo(methodOn(TransactionDetailController.class).obtenerPorId(id)).withSelfRel(),
                linkTo(methodOn(TransactionDetailController.class).obtenerTodos()).withRel("detalles")));
    }

    // POST /api/v1/transaction-details : Agrega un nuevo concepto o línea de detalle a la transacción
    @Operation(summary = "Inyectar concepto a transacción", description = "Añade un nuevo ítem de cobro o impuesto (línea de detalle) anexado al ID de una transacción de cabecera.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Línea de detalle anexada de forma exitosa"),
            @ApiResponse(responseCode = "400", description = "Estructura DTO errónea o ID de cabecera inválido")
    })
    @PostMapping
    public ResponseEntity<EntityModel<TransactionDetail>> agregar(
            @Valid @RequestBody TransactionDetailDTO dto) {
        TransactionDetail detail = detailService.agregar(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(EntityModel.of(detail,
                linkTo(methodOn(TransactionDetailController.class).obtenerPorId(detail.getId())).withSelfRel(),
                linkTo(methodOn(TransactionDetailController.class).obtenerTodos()).withRel("detalles")));
    }

    // PUT /api/v1/transaction-details/1 : Actualiza por completo un detalle existente mediante su ID
    @Operation(summary = "Actualizar línea de detalle por ID", description = "Sobreescribe por completo los valores, cantidades o glosas de una línea de detalle contable basándose en su ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Detalles de la línea actualizados con éxito"),
            @ApiResponse(responseCode = "400", description = "Datos de actualización inválidos"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "404", description = "El registro de detalle a modificar no fue hallado")
    })
    @PutMapping("/{id}")
    public ResponseEntity<EntityModel<TransactionDetail>> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody TransactionDetailDTO dto) {
        TransactionDetail detail = detailService.actualizar(id, dto);
        return ResponseEntity.ok(EntityModel.of(detail,
                linkTo(methodOn(TransactionDetailController.class).obtenerPorId(id)).withSelfRel(),
                linkTo(methodOn(TransactionDetailController.class).obtenerTodos()).withRel("detalles")));
    }

    // DELETE /api/v1/transaction-details/1 : Elimina físicamente un registro de detalle por ID
    @Operation(summary = "Eliminar línea de detalle", description = "Elimina físicamente una línea de cobro de la base de datos central.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Línea de detalle eliminada correctamente. Sin contenido."),
            @ApiResponse(responseCode = "401", description = "No autenticado")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        detailService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    // GET /api/v1/transaction-details/transaccion/1 : Filtra todos los detalles asociados a una transacción principal
    @Operation(summary = "Listar desgloses por Transacción de cabecera", description = "Aísla y lista todos los cobros particulares y tasas que componen una transacción de cabecera por su ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Desglose de la transacción recuperado con éxito"),
            @ApiResponse(responseCode = "401", description = "No autenticado")
    })
    @GetMapping("/transaccion/{transactionId}")
    public ResponseEntity<CollectionModel<EntityModel<TransactionDetail>>> obtenerPorTransaccion(
            @PathVariable Long transactionId) {
        List<EntityModel<TransactionDetail>> details = detailService.obtenerPorTransaccion(transactionId).stream()
                .map(d -> EntityModel.of(d,
                        linkTo(methodOn(TransactionDetailController.class).obtenerPorId(d.getId())).withSelfRel()))
                .toList();
        return ResponseEntity.ok(CollectionModel.of(details,
                linkTo(methodOn(TransactionDetailController.class).obtenerPorTransaccion(transactionId)).withSelfRel()));
    }

    // GET /api/v1/transaction-details/tipo/COBRO : Filtra los detalles de transacciones por su tipo
    @Operation(summary = "Listar desgloses por Tipo de Detalle", description = "Filtra las líneas del libro contable según su naturaleza (COBRO, DESCUENTO, IMPUESTO).")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Colección filtrada devuelta correctamente"),
            @ApiResponse(responseCode = "401", description = "No autenticado")
    })
    @GetMapping("/tipo/{tipoDetalle}")
    public ResponseEntity<CollectionModel<EntityModel<TransactionDetail>>> obtenerPorTipo(
            @PathVariable String tipoDetalle) {
        List<EntityModel<TransactionDetail>> details = detailService.obtenerPorTipoDetalle(tipoDetalle).stream()
                .map(d -> EntityModel.of(d,
                        linkTo(methodOn(TransactionDetailController.class).obtenerPorId(d.getId())).withSelfRel()))
                .toList();
        return ResponseEntity.ok(CollectionModel.of(details,
                linkTo(methodOn(TransactionDetailController.class).obtenerPorTipo(tipoDetalle)).withSelfRel()));
    }

    // GET /api/v1/transaction-details/transaccion/1/tipo/COBRO : Combina filtros
    @Operation(summary = "Listar desgloses por Transacción y Tipo", description = "Cruza filtros para hallar un tipo de ítem específico (ej: buscar los IMPUESTOS cobrados dentro de la transacción ID 1).")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Filtro analítico cruzado completado con éxito"),
            @ApiResponse(responseCode = "401", description = "No autenticado")
    })
    @GetMapping("/transaccion/{transactionId}/tipo/{tipoDetalle}")
    public ResponseEntity<CollectionModel<EntityModel<TransactionDetail>>> obtenerPorTransaccionYTipo(
            @PathVariable Long transactionId,
            @PathVariable String tipoDetalle) {
        List<EntityModel<TransactionDetail>> details = detailService.obtenerPorTransaccionYTipo(transactionId, tipoDetalle).stream()
                .map(d -> EntityModel.of(d,
                        linkTo(methodOn(TransactionDetailController.class).obtenerPorId(d.getId())).withSelfRel()))
                .toList();
        return ResponseEntity.ok(CollectionModel.of(details,
                linkTo(methodOn(TransactionDetailController.class).obtenerPorTransaccionYTipo(transactionId, tipoDetalle)).withSelfRel()));
    }

    // GET /api/v1/transaction-details/buscar?concepto=multa : Busca detalles mediante coincidencia parcial de texto en el concepto
    @Operation(summary = "Buscar desgloses por Concepto", description = "Realiza una consulta de texto parcial (LIKE) sobre la columna concepto de las líneas de cobro.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Coincidencias conceptuales devueltas correctamente"),
            @ApiResponse(responseCode = "401", description = "No autenticado")
    })
    @GetMapping("/buscar")
    public ResponseEntity<CollectionModel<EntityModel<TransactionDetail>>> buscarPorConcepto(
            @RequestParam String concepto) {
        List<EntityModel<TransactionDetail>> details = detailService.buscarPorConcepto(concepto).stream()
                .map(d -> EntityModel.of(d,
                        linkTo(methodOn(TransactionDetailController.class).obtenerPorId(d.getId())).withSelfRel()))
                .toList();
        return ResponseEntity.ok(CollectionModel.of(details,
                linkTo(methodOn(TransactionDetailController.class).buscarPorConcepto(concepto)).withSelfRel()));
    }

    // GET /api/v1/transaction-details/transaccion/1/ordenados : Obtiene los detalles de una transacción ordenados por valor descendente
    @Operation(summary = "Listar desgloses ordenados por valor", description = "Muestra los ítems de una transacción ordenados financieramente de mayor a menor costo (descendente).")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Desglose contable ordenado devuelto de forma exitosa"),
            @ApiResponse(responseCode = "401", description = "No autenticado")
    })
    @GetMapping("/transaccion/{transactionId}/ordenados")
    public ResponseEntity<CollectionModel<EntityModel<TransactionDetail>>> obtenerOrdenados(
            @PathVariable Long transactionId) {
        List<EntityModel<TransactionDetail>> details = detailService.obtenerPorTransaccionOrdenados(transactionId).stream()
                .map(d -> EntityModel.of(d,
                        linkTo(methodOn(TransactionDetailController.class).obtenerPorId(d.getId())).withSelfRel()))
                .toList();
        return ResponseEntity.ok(CollectionModel.of(details,
                linkTo(methodOn(TransactionDetailController.class).obtenerOrdenados(transactionId)).withSelfRel()));
    }

    // GET /api/v1/transaction-details/ultimos : Devuelve los últimos 10 detalles registrados en la base de datos
    @Operation(summary = "Listar últimos ítems facturados globalmente", description = "Endpoint de auditoría que expone las últimas 10 líneas de detalle registradas en el sistema fronterizo.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Últimas inserciones leídas con éxito"),
            @ApiResponse(responseCode = "401", description = "No autenticado")
    })
    @GetMapping("/ultimos")
    public ResponseEntity<CollectionModel<EntityModel<TransactionDetail>>> obtenerUltimos() {
        List<EntityModel<TransactionDetail>> details = detailService.obtenerUltimosDetalles().stream()
                .map(d -> EntityModel.of(d,
                        linkTo(methodOn(TransactionDetailController.class).obtenerPorId(d.getId())).withSelfRel()))
                .toList();
        return ResponseEntity.ok(CollectionModel.of(details,
                linkTo(methodOn(TransactionDetailController.class).obtenerUltimos()).withSelfRel()));
    }
}