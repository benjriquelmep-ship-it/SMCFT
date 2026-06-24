package com.example.ItemCategoryService.controller;

import com.example.ItemCategoryService.dto.ItemDTO;
import com.example.ItemCategoryService.model.Item;
import com.example.ItemCategoryService.service.ItemService;
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
    public ResponseEntity<List<Item>> obtenerTodos() {
        return ResponseEntity.ok(itemService.obtenerTodos());
    }

    // GET /api/v1/items/1
    @Operation(summary = "Obtener artículo específico por ID", description = "Busca el nombre comercial, unidad métrica y estado de vigencia de un artículo por su clave primaria.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Artículo localizado de forma correcta en el catálogo"),
            @ApiResponse(responseCode = "404", description = "El ID del artículo solicitado no se encuentra registrado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<Item> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(itemService.obtenerPorId(id));
    }

    // POST /api/v1/items
    @Operation(summary = "Insertar nuevo artículo al catálogo", description = "Valida el formulario inyectado y acopla una nueva mercancía vinculada obligatoriamente al ID de una categoría arancelaria.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Artículo incorporado de forma conforme al catálogo general"),
            @ApiResponse(responseCode = "400", description = "Payload DTO con campos vacíos o inconsistencias de tipo")
    })
    @PostMapping
    public ResponseEntity<Item> crear(@Valid @RequestBody ItemDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(itemService.crear(dto));
    }

    // PUT /api/v1/items/1
    @Operation(summary = "Actualizar artículo por completo", description = "Sobreescribe de forma total las propiedades, nombres comerciales y unidades físicas de medida de un artículo por su ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Artículo modificado exitosamente en el maestro"),
            @ApiResponse(responseCode = "400", description = "Datos de actualización erróneos"),
            @ApiResponse(responseCode = "404", description = "El artículo a modificar no fue localizado")
    })
    @PutMapping("/{id}")
    public ResponseEntity<Item> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody ItemDTO dto) {
        return ResponseEntity.ok(itemService.actualizar(id, dto));
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
    public ResponseEntity<List<Item>> obtenerPorCategoria(@PathVariable Long categoryId) {
        return ResponseEntity.ok(itemService.obtenerPorCategoria(categoryId));
    }

    // GET /api/v1/items/categoria/1/activos
    @Operation(summary = "Listar artículos Activos por Categoría", description = "Aísla y retorna las mercancías vigentes y seleccionables en andén aduanero que pertenecen a una subpartida arancelaria.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Desglose operativo activo devuelto de forma conforme")
    })
    @GetMapping("/categoria/{categoryId}/activos")
    public ResponseEntity<List<Item>> obtenerActivosPorCategoria(@PathVariable Long categoryId) {
        return ResponseEntity.ok(itemService.obtenerActivosPorCategoria(categoryId));
    }

    // GET /api/v1/items/activos
    @Operation(summary = "Listar todos los artículos vigentes", description = "Filtro global operativo que expone la totalidad de artículos habilitados para transacciones comerciales o personales en frontera.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Maestro de artículos vigentes leídos con éxito")
    })
    @GetMapping("/activos")
    public ResponseEntity<List<Item>> obtenerActivos() {
        return ResponseEntity.ok(itemService.obtenerActivos());
    }

    // GET /api/v1/items/buscar?nombre=laptop
    @Operation(summary = "Buscar artículos por Coincidencia descriptiva", description = "Ejecuta una consulta de concordancia parcial (LIKE) sobre los nombres de las mercancías catalogadas.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Coincidencias de búsqueda leídas de forma conforme")
    })
    @GetMapping("/buscar")
    public ResponseEntity<List<Item>> buscarPorNombre(@RequestParam String nombre) {
        return ResponseEntity.ok(itemService.buscarPorNombre(nombre));
    }

    // GET /api/v1/items/unidad/kg
    @Operation(summary = "Listar artículos por Unidad de medida", description = "Agrupa y extrae los ítems basándose en su métrica de empaque o volumen (ej: Litros, Unidades, Kilogramos).")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Registros agrupados por métrica devueltos con éxito")
    })
    @GetMapping("/unidad/{unidad}")
    public ResponseEntity<List<Item>> obtenerPorUnidad(@PathVariable String unidad) {
        return ResponseEntity.ok(itemService.obtenerPorUnidad(unidad));
    }

    // GET /api/v1/items/categoria/1/ordenados
    @Operation(summary = "Listar artículos de una categoría ordenados alfabéticamente", description = "Retorna el desglose de productos de una subpartida arancelaria indexada de la A a la Z, ideal para componentes Frontend.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Colección ordenada devuelta con éxito")
    })
    @GetMapping("/categoria/{categoryId}/ordenados")
    public ResponseEntity<List<Item>> obtenerPorCategoriaOrdenados(@PathVariable Long categoryId) {
        return ResponseEntity.ok(itemService.obtenerPorCategoriaOrdenados(categoryId));
    }

    // GET /api/v1/items/ultimos
    @Operation(summary = "Listar últimas inserciones de artículos", description = "Endpoint administrativo que lee los últimos 10 artículos incorporados recientemente al catálogo general.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Últimas inserciones leídas de forma conforme")
    })
    @GetMapping("/ultimos")
    public ResponseEntity<List<Item>> obtenerUltimos() {
        return ResponseEntity.ok(itemService.obtenerUltimosItems());
    }
}