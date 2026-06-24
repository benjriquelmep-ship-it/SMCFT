package com.example.ItemCategoryService.controller;

import com.example.ItemCategoryService.dto.ItemDTO;
import com.example.ItemCategoryService.model.Item;
import com.example.ItemCategoryService.service.ItemService;
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
@RequestMapping("/api/v1/items")
@RequiredArgsConstructor
@Tag(name = "Catálogo de Artículos (Ítems)", description = "Endpoints para el desglose, administración y vigencia de las mercancías o artículos específicos declarables")
@SecurityRequirement(name = "bearerAuth")
public class ItemController {

    private final ItemService itemService;

    @Operation(summary = "Listar todos los artículos del catálogo", description = "Expone el maestro central unificado de todas las mercancías e ítems específicos cargados en el sistema.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Maestro de artículos recuperado con éxito"),
            @ApiResponse(responseCode = "401", description = "No autenticado - Token JWT ausente"),
            @ApiResponse(responseCode = "403", description = "Sin privilegios requeridos")
    })
    @GetMapping
    public ResponseEntity<CollectionModel<EntityModel<Item>>> obtenerTodos() {
        List<EntityModel<Item>> items = itemService.obtenerTodos().stream()
                .map(i -> EntityModel.of(i,
                        linkTo(methodOn(ItemController.class).obtenerPorId(i.getId())).withSelfRel()))
                .toList();
        return ResponseEntity.ok(CollectionModel.of(items,
                linkTo(methodOn(ItemController.class).obtenerTodos()).withSelfRel()));
    }

    // GET /api/v1/items/1
    @Operation(summary = "Obtener artículo específico por ID", description = "Busca el nombre comercial, unidad métrica y estado de vigencia de un artículo por su clave primaria.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Artículo localizado de forma correcta en el catálogo"),
            @ApiResponse(responseCode = "404", description = "El ID del artículo solicitado no se encuentra registrado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<Item>> obtenerPorId(@PathVariable Long id) {
        Item item = itemService.obtenerPorId(id);
        return ResponseEntity.ok(EntityModel.of(item,
                linkTo(methodOn(ItemController.class).obtenerPorId(id)).withSelfRel(),
                linkTo(methodOn(ItemController.class).obtenerTodos()).withRel("items")));
    }

    // POST /api/v1/items
    @Operation(summary = "Insertar nuevo artículo al catálogo", description = "Valida el formulario inyectado y acopla una nueva mercancía vinculada obligatoriamente al ID de una categoría arancelaria.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Artículo incorporado de forma conforme al catálogo general"),
            @ApiResponse(responseCode = "400", description = "Payload DTO con campos vacíos o inconsistencias de tipo")
    })
    @PostMapping
    public ResponseEntity<EntityModel<Item>> crear(@Valid @RequestBody ItemDTO dto) {
        Item item = itemService.crear(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(EntityModel.of(item,
                linkTo(methodOn(ItemController.class).obtenerPorId(item.getId())).withSelfRel(),
                linkTo(methodOn(ItemController.class).obtenerTodos()).withRel("items")));
    }

    // PUT /api/v1/items/1
    @Operation(summary = "Actualizar artículo por completo", description = "Sobreescribe de forma total las propiedades, nombres comerciales y unidades físicas de medida de un artículo por su ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Artículo modificado exitosamente en el maestro"),
            @ApiResponse(responseCode = "400", description = "Datos de actualización erróneos"),
            @ApiResponse(responseCode = "404", description = "El artículo a modificar no fue localizado")
    })
    @PutMapping("/{id}")
    public ResponseEntity<EntityModel<Item>> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody ItemDTO dto) {
        Item item = itemService.actualizar(id, dto);
        return ResponseEntity.ok(EntityModel.of(item,
                linkTo(methodOn(ItemController.class).obtenerPorId(id)).withSelfRel(),
                linkTo(methodOn(ItemController.class).obtenerTodos()).withRel("items")));
    }

    // PATCH /api/v1/items/1/desactivar
    @Operation(summary = "Desactivar artículo del catálogo", description = "Pone la vigencia lógica del bien en falso, impidiendo que el Border Crossing Service lo asocie a nuevos tránsitos fronterizos.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Artículo inhabilitado del catálogo correctamente. Sin contenido."),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "404", description = "El ID de artículo especificado no existe")
    })
    @PatchMapping("/{id}/desactivar")
    public ResponseEntity<Void> desactivar(@PathVariable Long id) {
        itemService.desactivar(id);
        return ResponseEntity.noContent().build();
    }

    // DELETE /api/v1/items/1
    @Operation(summary = "Eliminar artículo de la base de datos", description = "Remueve físicamente la hilera de persistencia del artículo de forma definitiva.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Artículo purgado con éxito de la persistencia. Sin contenido."),
            @ApiResponse(responseCode = "401", description = "No autenticado")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        itemService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    // GET /api/v1/items/categoria/1
    @Operation(summary = "Listar artículos por Categoría arancelaria", description = "Recupera la lista histórica desglosada (activos e inactivos) de artículos acoplados a una partida arancelaria.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Colección de artículos de la categoría obtenida con éxito")
    })
    @GetMapping("/categoria/{categoryId}")
    public ResponseEntity<CollectionModel<EntityModel<Item>>> obtenerPorCategoria(@PathVariable Long categoryId) {
        List<EntityModel<Item>> items = itemService.obtenerPorCategoria(categoryId).stream()
                .map(i -> EntityModel.of(i,
                        linkTo(methodOn(ItemController.class).obtenerPorId(i.getId())).withSelfRel()))
                .toList();
        return ResponseEntity.ok(CollectionModel.of(items,
                linkTo(methodOn(ItemController.class).obtenerPorCategoria(categoryId)).withSelfRel()));
    }

    // GET /api/v1/items/categoria/1/activos
    @Operation(summary = "Listar artículos Activos por Categoría", description = "Aísla y retorna las mercancías vigentes y seleccionables en andén aduanero que pertenecen a una subpartida arancelaria.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Desglose operativo activo devuelto de forma conforme")
    })
    @GetMapping("/categoria/{categoryId}/activos")
    public ResponseEntity<CollectionModel<EntityModel<Item>>> obtenerActivosPorCategoria(@PathVariable Long categoryId) {
        List<EntityModel<Item>> items = itemService.obtenerActivosPorCategoria(categoryId).stream()
                .map(i -> EntityModel.of(i,
                        linkTo(methodOn(ItemController.class).obtenerPorId(i.getId())).withSelfRel()))
                .toList();
        return ResponseEntity.ok(CollectionModel.of(items,
                linkTo(methodOn(ItemController.class).obtenerActivosPorCategoria(categoryId)).withSelfRel()));
    }

    // GET /api/v1/items/activos
    @Operation(summary = "Listar todos los artículos vigentes", description = "Filtro global operativo que expone la totalidad de artículos habilitados para transacciones comerciales o personales en frontera.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Maestro de artículos vigentes leídos con éxito")
    })
    @GetMapping("/activos")
    public ResponseEntity<CollectionModel<EntityModel<Item>>> obtenerActivos() {
        List<EntityModel<Item>> items = itemService.obtenerActivos().stream()
                .map(i -> EntityModel.of(i,
                        linkTo(methodOn(ItemController.class).obtenerPorId(i.getId())).withSelfRel()))
                .toList();
        return ResponseEntity.ok(CollectionModel.of(items,
                linkTo(methodOn(ItemController.class).obtenerActivos()).withSelfRel()));
    }

    // GET /api/v1/items/buscar?nombre=laptop
    @Operation(summary = "Buscar artículos por Coincidencia descriptiva", description = "Ejecuta una consulta de concordancia parcial (LIKE) sobre los nombres de las mercancías catalogadas.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Coincidencias de búsqueda leídas de forma conforme")
    })
    @GetMapping("/buscar")
    public ResponseEntity<CollectionModel<EntityModel<Item>>> buscarPorNombre(@RequestParam String nombre) {
        List<EntityModel<Item>> items = itemService.buscarPorNombre(nombre).stream()
                .map(i -> EntityModel.of(i,
                        linkTo(methodOn(ItemController.class).obtenerPorId(i.getId())).withSelfRel()))
                .toList();
        return ResponseEntity.ok(CollectionModel.of(items,
                linkTo(methodOn(ItemController.class).buscarPorNombre(nombre)).withSelfRel()));
    }

    // GET /api/v1/items/unidad/kg
    @Operation(summary = "Listar artículos por Unidad de medida", description = "Agrupa y extrae los ítems basándose en su métrica de empaque o volumen (ej: Litros, Unidades, Kilogramos).")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Registros agrupados por métrica devueltos con éxito")
    })
    @GetMapping("/unidad/{unidad}")
    public ResponseEntity<CollectionModel<EntityModel<Item>>> obtenerPorUnidad(@PathVariable String unidad) {
        List<EntityModel<Item>> items = itemService.obtenerPorUnidad(unidad).stream()
                .map(i -> EntityModel.of(i,
                        linkTo(methodOn(ItemController.class).obtenerPorId(i.getId())).withSelfRel()))
                .toList();
        return ResponseEntity.ok(CollectionModel.of(items,
                linkTo(methodOn(ItemController.class).obtenerPorUnidad(unidad)).withSelfRel()));
    }

    // GET /api/v1/items/categoria/1/ordenados
    @Operation(summary = "Listar artículos de una categoría ordenados alfabéticamente", description = "Retorna el desglose de productos de una subpartida arancelaria indexada de la A a la Z, ideal para componentes Frontend.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Colección ordenada devuelta con éxito")
    })
    @GetMapping("/categoria/{categoryId}/ordenados")
    public ResponseEntity<CollectionModel<EntityModel<Item>>> obtenerPorCategoriaOrdenados(@PathVariable Long categoryId) {
        List<EntityModel<Item>> items = itemService.obtenerPorCategoriaOrdenados(categoryId).stream()
                .map(i -> EntityModel.of(i,
                        linkTo(methodOn(ItemController.class).obtenerPorId(i.getId())).withSelfRel()))
                .toList();
        return ResponseEntity.ok(CollectionModel.of(items,
                linkTo(methodOn(ItemController.class).obtenerPorCategoriaOrdenados(categoryId)).withSelfRel()));
    }

    // GET /api/v1/items/ultimos
    @Operation(summary = "Listar últimas inserciones de artículos", description = "Endpoint administrativo que lee los últimos 10 artículos incorporados recientemente al catálogo general.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Últimas inserciones leídas de forma conforme")
    })
    @GetMapping("/ultimos")
    public ResponseEntity<CollectionModel<EntityModel<Item>>> obtenerUltimos() {
        List<EntityModel<Item>> items = itemService.obtenerUltimosItems().stream()
                .map(i -> EntityModel.of(i,
                        linkTo(methodOn(ItemController.class).obtenerPorId(i.getId())).withSelfRel()))
                .toList();
        return ResponseEntity.ok(CollectionModel.of(items,
                linkTo(methodOn(ItemController.class).obtenerUltimos()).withSelfRel()));
    }
}