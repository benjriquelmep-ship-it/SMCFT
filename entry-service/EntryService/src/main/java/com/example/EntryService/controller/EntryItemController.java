package com.example.EntryService.controller;

import com.example.EntryService.dto.EntryItemDTO;
import com.example.EntryService.model.EntryItem;
import com.example.EntryService.service.EntryItemService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.CollectionModel;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@RestController
@RequestMapping("/api/v1/entries/items")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "2. Detalle de Mercancías Declaradas", description = "Endpoints operativos orientados al desglose, aforo técnico, aprobación o desestimación de líneas de bienes transportadas")
public class EntryItemController {

    private final EntryItemService itemService;

    @Operation(summary = "Obtener Todas las Mercancías Declaradas", description = "Recupera un listado consolidado de todas las líneas de artículos detalladas en el sistema global.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Listado completo de artículos expuesto."),
            @ApiResponse(responseCode = "401", description = "Falta de credenciales de seguridad JWT."),
            @ApiResponse(responseCode = "403", description = "Privilegios insuficientes para listar mercancía global.")
    })
    @GetMapping
    public ResponseEntity<List<EntryItem>> obtenerTodos() {
        return ResponseEntity.ok(itemService.obtenerTodos());
    }

    @Operation(summary = "Obtener Ítem por ID", description = "Recupera la metadata de un bien declarado en base a su clave primaria.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Mercancía localizada y devuelta."),
            @ApiResponse(responseCode = "404", description = "Ítem de declaración no hallado.")
    })
    @GetMapping("/{id}")
    public ResponseEntity<EntryItem> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(itemService.obtenerPorId(id));
    }

    @Operation(summary = "Anexar Artículo Declarado a un Ingreso", description = "Agrega una nueva línea de artículo o equipaje a una cabecera de ingreso fronterizo existente.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Ítem registrado y enlazado correctamente al ingreso correspondientes."),
            @ApiResponse(responseCode = "400", description = "Campos inválidos en la estructura de precios o cantidades.")
    })
    @PostMapping
    public ResponseEntity<EntryItem> agregar(@Valid @RequestBody EntryItemDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(itemService.agregar(dto));
    }

    @Operation(summary = "Aprobar Línea de Mercancía", description = "Establece el flag de aprobación en verdadero, indicando que el bien superó la inspección aduanera (Solo si la cabecera está PENDIENTE).")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Línea de mercancía aprobada en andén."),
            @ApiResponse(responseCode = "400", description = "Inconsistencia operativa o el trámite padre ya se encuentra cerrado.")
    })
    @PatchMapping("/{id}/aprobar")
    public ResponseEntity<EntryItem> aprobar(@PathVariable Long id) {
        return ResponseEntity.ok(itemService.aprobar(id));
    }

    @Operation(summary = "Rechazar u Objetar Línea de Mercancía", description = "Marca la línea declarada con aprobación en falso tras el aforo aduanero por irregularidades o aforos desfavorables.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Línea rechazada u objetada con éxito.")
    })
    @PatchMapping("/{id}/rechazar")
    public ResponseEntity<EntryItem> rechazar(@PathVariable Long id) {
        return ResponseEntity.ok(itemService.rechazar(id));
    }

    @Operation(summary = "Remover Artículo Declarado", description = "Elimina físicamente del sistema una línea de declaración mediante su clave primaria.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Artículo eliminado de la orden de ingreso.")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        itemService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Listar Artículos por ID de Ingreso", description = "Obtiene el desglose de bienes que pertenecen a una orden de acceso vehicular específica.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Detalle de mercancías de la orden recuperado.")
    })
    @GetMapping("/ingreso/{entryId}")
    public ResponseEntity<List<EntryItem>> obtenerPorIngreso(@PathVariable Long entryId) {
        return ResponseEntity.ok(itemService.obtenerPorIngreso(entryId));
    }

    @Operation(summary = "Listar Solo Bienes Aprobados de un Ingreso", description = "Entrega exclusivamente los artículos declarados que pasaron de forma conforme la fiscalización de andén.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Filtro de artículos visados completado.")
    })
    @GetMapping("/ingreso/{entryId}/aprobados")
    public ResponseEntity<List<EntryItem>> obtenerAprobados(@PathVariable Long entryId) {
        return ResponseEntity.ok(itemService.obtenerAprobadosPorIngreso(entryId));
    }

    @Operation(summary = "Listar Solo Bienes Objetados o No Aprobados de un Ingreso", description = "Muestra de forma segregada los artículos que presentaron problemas o no superaron la revisión técnica.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Filtro de artículos observados completado.")
    })
    @GetMapping("/ingreso/{entryId}/no-aprobados")
    public ResponseEntity<List<EntryItem>> obtenerNoAprobados(@PathVariable Long entryId) {
        return ResponseEntity.ok(itemService.obtenerNoAprobadosPorIngreso(entryId));
    }

    @Operation(summary = "Buscar Artículos por Glosa o Descripción Parcial", description = "Buscador de texto libre orientado a buscar coincidencias parciales dentro del detalle descriptivo de los bienes.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Resultados de la búsqueda textual devueltos.")
    })
    @GetMapping("/buscar")
    public ResponseEntity<List<EntryItem>> buscarPorDescripcion(@RequestParam String descripcion) {
        return ResponseEntity.ok(itemService.buscarPorDescripcion(descripcion));
    }

    @Operation(summary = "Listar Mercancías de un Ingreso Ordenadas por Mayor Valor", description = "Entrega el desglose de bienes de una orden ordenados de forma descendente por su valor comercial aduanero (USD).")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Aforo comercial ordenado jerárquicamente.")
    })
    @GetMapping("/ingreso/{entryId}/ordenados")
    public ResponseEntity<List<EntryItem>> obtenerOrdenados(@PathVariable Long entryId) {
        return ResponseEntity.ok(itemService.obtenerPorIngresoOrdenadosPorValor(entryId));
    }

    @Operation(summary = "Monitorear Últimas Declaraciones de Mercancías", description = "Retorna los últimos 10 artículos declarados en tiempo real dentro del complejo perimetral.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Dashboard de monitoreo de equipaje obtenido.")
    })
    @GetMapping("/ultimos")
    public ResponseEntity<List<EntryItem>> obtenerUltimos() {
        return ResponseEntity.ok(itemService.obtenerUltimosItems());
    }
}