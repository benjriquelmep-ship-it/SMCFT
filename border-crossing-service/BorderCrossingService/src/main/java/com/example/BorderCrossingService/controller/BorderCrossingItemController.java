package com.example.BorderCrossingService.controller;

import com.example.BorderCrossingService.dto.BorderCrossingItemDTO;
import com.example.BorderCrossingService.model.BorderCrossingItem;
import com.example.BorderCrossingService.service.BorderCrossingItemService;
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
@RequestMapping("/api/v1/border-crossings/items")
@RequiredArgsConstructor
@Tag(name = "2. Detalle de Equipajes e Ítems", description = "Endpoints operativos orientados al desglose, aforo físico, aprobación o desestimación de líneas de mercancía transportadas")
@SecurityRequirement(name = "bearerAuth")
public class BorderCrossingItemController {

    private final BorderCrossingItemService itemService;

    @Operation(summary = "Obtener total de mercancías declaradas en salidas", description = "Expone el listado consolidado global de todas las líneas de artículos detalladas en los tránsitos de salida.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Listado completo de mercancía expuesto con éxito"),
            @ApiResponse(responseCode = "401", description = "Falta de credenciales de seguridad JWT"),
            @ApiResponse(responseCode = "403", description = "Privilegios insuficientes para listar equipaje global")
    })
    @GetMapping
    public ResponseEntity<CollectionModel<EntityModel<BorderCrossingItem>>> obtenerTodos() {
        List<EntityModel<BorderCrossingItem>> itemModels = itemService.obtenerTodos().stream()
                .map(i -> EntityModel.of(i,
                        linkTo(methodOn(BorderCrossingItemController.class).obtenerPorId(i.getId())).withSelfRel()))
                .toList();
        return ResponseEntity.ok(CollectionModel.of(itemModels,
                linkTo(methodOn(BorderCrossingItemController.class).obtenerTodos()).withSelfRel()));
    }

    // GET /api/v1/border-crossings/items/1
    @Operation(summary = "Obtener ítem de equipaje por ID", description = "Busca el nombre comercial, cantidad, valoración USD y el estado de validación aduanera de una línea por su ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Línea de mercancía localizada de forma conforme"),
            @ApiResponse(responseCode = "404", description = "Ítem de declaración de equipaje no hallado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<BorderCrossingItem>> obtenerPorId(@PathVariable Long id) {
        BorderCrossingItem item = itemService.obtenerPorId(id);
        return ResponseEntity.ok(EntityModel.of(item,
                linkTo(methodOn(BorderCrossingItemController.class).obtenerPorId(id)).withSelfRel(),
                linkTo(methodOn(BorderCrossingItemController.class).obtenerTodos()).withRel("items")));
    }

    // POST /api/v1/border-crossings/items
    @Operation(summary = "Acoplar artículo declarado a una orden de salida", description = "Agrega una nueva línea de artículo o equipaje a una cabecera de cruce fronterizo activa.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Línea de artículo registrada e indexada correctamente a la orden"),
            @ApiResponse(responseCode = "400", description = "La categoría arancelaria provista no está activa o el DTO infringe validaciones numéricas")
    })
    @PostMapping
    public ResponseEntity<EntityModel<BorderCrossingItem>> agregar(@Valid @RequestBody BorderCrossingItemDTO dto) {
        BorderCrossingItem item = itemService.agregar(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(EntityModel.of(item,
                linkTo(methodOn(BorderCrossingItemController.class).obtenerPorId(item.getId())).withSelfRel(),
                linkTo(methodOn(BorderCrossingItemController.class).obtenerTodos()).withRel("items")));
    }

    // PATCH /api/v1/border-crossings/items/1/aprobar
    @Operation(summary = "Aprobar línea de mercancía en andén", description = "Modifica la bandera de aprobación a verdadero, indicando que la mercancía superó el aforo técnico (Solo si la cabecera está PENDIENTE).")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Línea de mercancía visada y aprobada con éxito"),
            @ApiResponse(responseCode = "400", description = "El trámite de cruce padre ya se encuentra cerrado o resuelto")
    })
    @PatchMapping("/{id}/aprobar")
    public ResponseEntity<EntityModel<BorderCrossingItem>> aprobar(@PathVariable Long id) {
        BorderCrossingItem item = itemService.aprobar(id);
        return ResponseEntity.ok(EntityModel.of(item,
                linkTo(methodOn(BorderCrossingItemController.class).obtenerPorId(id)).withSelfRel(),
                linkTo(methodOn(BorderCrossingItemController.class).obtenerTodos()).withRel("items")));
    }

    // PATCH /api/v1/border-crossings/items/1/rechazar
    @Operation(summary = "Objetar o Rechazar línea de mercancía", description = "Marca la línea declarada con aprobación en falso tras el aforo físico aduanero por irregularidades o aforos desfavorables.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Línea de mercancía rechazada u objetada con éxito")
    })
    @PatchMapping("/{id}/rechazar")
    public ResponseEntity<EntityModel<BorderCrossingItem>> rechazar(@PathVariable Long id) {
        BorderCrossingItem item = itemService.rechazar(id);
        return ResponseEntity.ok(EntityModel.of(item,
                linkTo(methodOn(BorderCrossingItemController.class).obtenerPorId(id)).withSelfRel(),
                linkTo(methodOn(BorderCrossingItemController.class).obtenerTodos()).withRel("items")));
    }

    // DELETE /api/v1/border-crossings/items/1
    @Operation(summary = "Remover artículo del equipaje", description = "Elimina físicamente del sistema una línea de declaración mediante su clave primaria.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Artículo eliminado de la orden de cruce. Sin contenido.")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        itemService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    // GET /api/v1/border-crossings/items/cruce/1
    @Operation(summary = "Listar artículos desglosados por ID de Cruce", description = "Obtiene la totalidad de bienes que integran la declaración jurada de equipaje de un cruce específico.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Desglose de equipaje de la orden recuperado correctamente")
    })
    @GetMapping("/cruce/{borderCrossingId}")
    public ResponseEntity<CollectionModel<EntityModel<BorderCrossingItem>>> obtenerPorCruce(@PathVariable Long borderCrossingId) {
        List<EntityModel<BorderCrossingItem>> itemModels = itemService.obtenerPorCruce(borderCrossingId).stream()
                .map(i -> EntityModel.of(i,
                        linkTo(methodOn(BorderCrossingItemController.class).obtenerPorId(i.getId())).withSelfRel()))
                .toList();
        return ResponseEntity.ok(CollectionModel.of(itemModels,
                linkTo(methodOn(BorderCrossingItemController.class).obtenerPorCruce(borderCrossingId)).withSelfRel()));
    }

    // GET /api/v1/border-crossings/items/cruce/1/aprobados
    @Operation(summary = "Listar solo bienes Aprobados de un Cruce", description = "Aísla y entrega exclusivamente los artículos del viajero que pasaron de forma conforme la revisión física.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Filtro de artículos visados completado con éxito")
    })
    @GetMapping("/cruce/{borderCrossingId}/aprobados")
    public ResponseEntity<CollectionModel<EntityModel<BorderCrossingItem>>> obtenerAprobados(@PathVariable Long borderCrossingId) {
        List<EntityModel<BorderCrossingItem>> itemModels = itemService.obtenerAprobadosPorCruce(borderCrossingId).stream()
                .map(i -> EntityModel.of(i,
                        linkTo(methodOn(BorderCrossingItemController.class).obtenerPorId(i.getId())).withSelfRel()))
                .toList();
        return ResponseEntity.ok(CollectionModel.of(itemModels,
                linkTo(methodOn(BorderCrossingItemController.class).obtenerAprobados(borderCrossingId)).withSelfRel()));
    }

    // GET /api/v1/border-crossings/items/cruce/1/no-aprobados
    @Operation(summary = "Listar bienes Observados o No Aprobados de un Cruce", description = "Muestra de forma segregada las líneas de artículos retenidas, pendientes o rechazadas en andén.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Filtro de artículos retenidos u observados completado")
    })
    @GetMapping("/cruce/{borderCrossingId}/no-aprobados")
    public ResponseEntity<CollectionModel<EntityModel<BorderCrossingItem>>> obtenerNoAprobados(@PathVariable Long borderCrossingId) {
        List<EntityModel<BorderCrossingItem>> itemModels = itemService.obtenerNoAprobadosPorCruce(borderCrossingId).stream()
                .map(i -> EntityModel.of(i,
                        linkTo(methodOn(BorderCrossingItemController.class).obtenerPorId(i.getId())).withSelfRel()))
                .toList();
        return ResponseEntity.ok(CollectionModel.of(itemModels,
                linkTo(methodOn(BorderCrossingItemController.class).obtenerNoAprobados(borderCrossingId)).withSelfRel()));
    }

    // GET /api/v1/border-crossings/items/categoria/1
    @Operation(summary = "Listar artículos por Categoría arancelaria", description = "Extrae todos los bienes declarados en frontera que correspondan a un ID de subpartida consultado.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Agrupación arancelaria de artículos devuelta")
    })
    @GetMapping("/categoria/{categoriaId}")
    public ResponseEntity<CollectionModel<EntityModel<BorderCrossingItem>>> obtenerPorCategoria(@PathVariable Long categoriaId) {
        List<EntityModel<BorderCrossingItem>> itemModels = itemService.obtenerPorCategoria(categoriaId).stream()
                .map(i -> EntityModel.of(i,
                        linkTo(methodOn(BorderCrossingItemController.class).obtenerPorId(i.getId())).withSelfRel()))
                .toList();
        return ResponseEntity.ok(CollectionModel.of(itemModels,
                linkTo(methodOn(BorderCrossingItemController.class).obtenerPorCategoria(categoriaId)).withSelfRel()));
    }

    // GET /api/v1/border-crossings/items/buscar?descripcion=laptop
    @Operation(summary = "Buscar artículos por Glosa o Descripción parcial", description = "Buscador textual flexible que localiza coincidencias (LIKE) sobre el detalle descriptivo de los bienes.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Resultados de la consulta textual devueltos")
    })
    @GetMapping("/buscar")
    public ResponseEntity<CollectionModel<EntityModel<BorderCrossingItem>>> buscarPorDescripcion(@RequestParam String descripcion) {
        List<EntityModel<BorderCrossingItem>> itemModels = itemService.buscarPorDescripcion(descripcion).stream()
                .map(i -> EntityModel.of(i,
                        linkTo(methodOn(BorderCrossingItemController.class).obtenerPorId(i.getId())).withSelfRel()))
                .toList();
        return ResponseEntity.ok(CollectionModel.of(itemModels,
                linkTo(methodOn(BorderCrossingItemController.class).buscarPorDescripcion(descripcion)).withSelfRel()));
    }

    // GET /api/v1/border-crossings/items/cruce/1/ordenados
    @Operation(summary = "Listar mercancías de un Cruce ordenadas por Mayor Valor", description = "Entrega el desglose de bienes de una orden ordenados de forma descendente basándose en su tasación comercial en USD.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Aforo comercial jerarquizado obtenido con éxito")
    })
    @GetMapping("/cruce/{borderCrossingId}/ordenados")
    public ResponseEntity<CollectionModel<EntityModel<BorderCrossingItem>>> obtenerOrdenados(@PathVariable Long borderCrossingId) {
        // Enlace corregido con la semántica del BorderCrossingItemService
        List<EntityModel<BorderCrossingItem>> itemModels = itemService.obtenerPorCruceOrdenadosPorValor(borderCrossingId).stream()
                .map(i -> EntityModel.of(i,
                        linkTo(methodOn(BorderCrossingItemController.class).obtenerPorId(i.getId())).withSelfRel()))
                .toList();
        return ResponseEntity.ok(CollectionModel.of(itemModels,
                linkTo(methodOn(BorderCrossingItemController.class).obtenerOrdenados(borderCrossingId)).withSelfRel()));
    }

    // GET /api/v1/border-crossings/items/ultimos
    @Operation(summary = "Monitorear últimas declaraciones de equipaje", description = "Endpoint de reportería administrativa que lee las últimas 10 líneas de artículos ingresadas en tiempo real.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Dashboard de monitoreo de equipajes obtenido de forma conforme")
    })
    @GetMapping("/ultimos")
    public ResponseEntity<CollectionModel<EntityModel<BorderCrossingItem>>> obtenerUltimos() {
        List<EntityModel<BorderCrossingItem>> itemModels = itemService.obtenerUltimosItems().stream()
                .map(i -> EntityModel.of(i,
                        linkTo(methodOn(BorderCrossingItemController.class).obtenerPorId(i.getId())).withSelfRel()))
                .toList();
        return ResponseEntity.ok(CollectionModel.of(itemModels,
                linkTo(methodOn(BorderCrossingItemController.class).obtenerUltimos()).withSelfRel()));
    }
}
