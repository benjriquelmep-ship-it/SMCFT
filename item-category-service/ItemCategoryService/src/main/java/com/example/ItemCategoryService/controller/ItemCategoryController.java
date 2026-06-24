package com.example.ItemCategoryService.controller;

import com.example.ItemCategoryService.dto.ItemCategoryDTO;
import com.example.ItemCategoryService.model.ItemCategory;
import com.example.ItemCategoryService.service.ItemCategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.util.List;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/v1/item-categories")
@RequiredArgsConstructor
@Tag(name = "Categorías Arancelarias", description = "Endpoints para la parametrización, control de franquicias y exenciones tributarias de las clasificaciones de mercancías")
@SecurityRequirement(name = "bearerAuth")
public class ItemCategoryController {

    private final ItemCategoryService categoryService;

    @Operation(summary = "Listar todas las categorías arancelarias", description = "Recupera el catálogo completo de subpartidas y clasificaciones de equipaje configuradas en la plataforma fronteriza.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Catálogo de categorías arancelarias recuperado con éxito"),
            @ApiResponse(responseCode = "401", description = "No autenticado - Falta token Bearer JWT"),
            @ApiResponse(responseCode = "403", description = "Sin permisos - Privilegios insuficientes")
    })
    @GetMapping
    public ResponseEntity<CollectionModel<EntityModel<ItemCategory>>> obtenerTodas() {
        List<EntityModel<ItemCategory>> categories = categoryService.obtenerTodas().stream()
                .map(c -> EntityModel.of(c,
                        linkTo(methodOn(ItemCategoryController.class).obtenerPorId(c.getId())).withSelfRel()))
                .toList();
        return ResponseEntity.ok(CollectionModel.of(categories,
                linkTo(methodOn(ItemCategoryController.class).obtenerTodas()).withSelfRel()));
    }

    // GET /api/v1/item-categories/1
    @Operation(summary = "Obtener categoría arancelaria por ID", description = "Busca las reglas de declaración y límites de valor en USD de una categoría específica. Utilizado de manera perimetral por el Border Crossing Service.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Categoría arancelaria localizada correctamente"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "404", description = "El ID de categoría solicitado no existe")
    })
    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<ItemCategory>> obtenerPorId(@PathVariable Long id) {
        ItemCategory category = categoryService.obtenerPorId(id);
        return ResponseEntity.ok(EntityModel.of(category,
                linkTo(methodOn(ItemCategoryController.class).obtenerPorId(id)).withSelfRel(),
                linkTo(methodOn(ItemCategoryController.class).obtenerTodas()).withRel("categorias")));
    }

    // POST /api/v1/item-categories
    @Operation(summary = "Registrar nueva categoría arancelaria", description = "Valida el payload de entrada y da de alta una nueva clasificación de mercancías con sus respectivas restricciones de exención fiscal.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Categoría arancelaria registrada exitosamente"),
            @ApiResponse(responseCode = "400", description = "Payload inválido o restricciones de validación del DTO infringidas")
    })
    @PostMapping
    public ResponseEntity<EntityModel<ItemCategory>> crear(@Valid @RequestBody ItemCategoryDTO dto) {
        ItemCategory category = categoryService.crear(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(EntityModel.of(category,
                linkTo(methodOn(ItemCategoryController.class).obtenerPorId(category.getId())).withSelfRel(),
                linkTo(methodOn(ItemCategoryController.class).obtenerTodas()).withRel("categorias")));
    }

    // PUT /api/v1/item-categories/1
    @Operation(summary = "Actualizar categoría arancelaria por completo", description = "Reemplaza íntegramente las propiedades, límites arancelarios y banderas de control de una categoría a partir de su ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Categoría arancelaria modificada de forma conforme"),
            @ApiResponse(responseCode = "400", description = "Datos de actualización inválidos"),
            @ApiResponse(responseCode = "404", description = "La categoría solicitada no se encuentra registrada")
    })
    @PutMapping("/{id}")
    public ResponseEntity<EntityModel<ItemCategory>> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody ItemCategoryDTO dto) {
        ItemCategory category = categoryService.actualizar(id, dto);
        return ResponseEntity.ok(EntityModel.of(category,
                linkTo(methodOn(ItemCategoryController.class).obtenerPorId(id)).withSelfRel(),
                linkTo(methodOn(ItemCategoryController.class).obtenerTodas()).withRel("categorias")));
    }

    // PATCH /api/v1/item-categories/1/desactivar
    @Operation(summary = "Desactivar categoría arancelaria", description = "Modifica el flag de vigencia de la categoría a inactivo, bloqueando su uso para nuevas declaraciones en frontera.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Categoría desactivada del sistema correctamente. Sin contenido."),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "404", description = "La categoría solicitada no existe")
    })
    @PatchMapping("/{id}/desactivar")
    public ResponseEntity<Void> desactivar(@PathVariable Long id) {
        categoryService.desactivar(id);
        return ResponseEntity.noContent().build();
    }

    // DELETE /api/v1/item-categories/1
    @Operation(summary = "Eliminar categoría arancelaria físicamente", description = "Remueve de forma permanente el registro de la categoría de la base de datos central.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Registro purgado de la base de datos de manera exitosa. Sin contenido."),
            @ApiResponse(responseCode = "401", description = "No autenticado")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        categoryService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    // GET /api/v1/item-categories/activas
    @Operation(summary = "Listar categorías arancelarias Activas", description = "Filtra la base de datos exponiendo únicamente las clasificaciones vigentes admitidas por el andén del Border Crossing Service.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Nómina de categorías activas recuperada con éxito")
    })
    @GetMapping("/activas")
    public ResponseEntity<CollectionModel<EntityModel<ItemCategory>>> obtenerActivas() {
        List<EntityModel<ItemCategory>> categories = categoryService.obtenerActivas().stream()
                .map(c -> EntityModel.of(c,
                        linkTo(methodOn(ItemCategoryController.class).obtenerPorId(c.getId())).withSelfRel()))
                .toList();
        return ResponseEntity.ok(CollectionModel.of(categories,
                linkTo(methodOn(ItemCategoryController.class).obtenerActivas()).withSelfRel()));
    }

    // GET /api/v1/item-categories/inactivas
    @Operation(summary = "Listar categorías arancelarias Inactivas", description = "Filtra e identifica aquellas subpartidas obsoletas o descontinuadas en los controles fronterizos.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Nómina de categorías inactivas devuelta correctamente")
    })
    @GetMapping("/inactivas")
    public ResponseEntity<CollectionModel<EntityModel<ItemCategory>>> obtenerInactivas() {
        List<EntityModel<ItemCategory>> categories = categoryService.obtenerInactivas().stream()
                .map(c -> EntityModel.of(c,
                        linkTo(methodOn(ItemCategoryController.class).obtenerPorId(c.getId())).withSelfRel()))
                .toList();
        return ResponseEntity.ok(CollectionModel.of(categories,
                linkTo(methodOn(ItemCategoryController.class).obtenerInactivas()).withSelfRel()));
    }

    // GET /api/v1/item-categories/requieren-declaracion
    @Operation(summary = "Listar categorías que Requieren Declaración", description = "Aísla y retorna aquellas categorías que obligan penalmente al operador o viajero a levantar una declaración jurada de internación.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Listado prioritario de control aduanero extraído con éxito")
    })
    @GetMapping("/requieren-declaracion")
    public ResponseEntity<CollectionModel<EntityModel<ItemCategory>>> obtenerConDeclaracion() {
        List<EntityModel<ItemCategory>> categories = categoryService.obtenerRequierenDeclaracion().stream()
                .map(c -> EntityModel.of(c,
                        linkTo(methodOn(ItemCategoryController.class).obtenerPorId(c.getId())).withSelfRel()))
                .toList();
        return ResponseEntity.ok(CollectionModel.of(categories,
                linkTo(methodOn(ItemCategoryController.class).obtenerConDeclaracion()).withSelfRel()));
    }

    // GET /api/v1/item-categories/sin-declaracion
    @Operation(summary = "Listar categorías Sin Obligación de Declaración", description = "Retorna el conjunto de clasificaciones que forman parte del libre tránsito dentro de las franquicias ordinarias.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Listado de libre internación obtenido correctamente")
    })
    @GetMapping("/sin-declaracion")
    public ResponseEntity<CollectionModel<EntityModel<ItemCategory>>> obtenerSinDeclaracion() {
        List<EntityModel<ItemCategory>> categories = categoryService.obtenerSinDeclaracion().stream()
                .map(c -> EntityModel.of(c,
                        linkTo(methodOn(ItemCategoryController.class).obtenerPorId(c.getId())).withSelfRel()))
                .toList();
        return ResponseEntity.ok(CollectionModel.of(categories,
                linkTo(methodOn(ItemCategoryController.class).obtenerSinDeclaracion()).withSelfRel()));
    }

    // GET /api/v1/item-categories/buscar?nombre=electro
    @Operation(summary = "Buscar categorías por Coincidencia de nombre", description = "Realiza una consulta de texto parcial (LIKE) sobre la columna de identificación de las categorías.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Coincidencias arancelarias devueltas de manera exitosa")
    })
    @GetMapping("/buscar")
    public ResponseEntity<CollectionModel<EntityModel<ItemCategory>>> buscarPorNombre(@RequestParam String nombre) {
        List<EntityModel<ItemCategory>> categories = categoryService.buscarPorNombre(nombre).stream()
                .map(c -> EntityModel.of(c,
                        linkTo(methodOn(ItemCategoryController.class).obtenerPorId(c.getId())).withSelfRel()))
                .toList();
        return ResponseEntity.ok(CollectionModel.of(categories,
                linkTo(methodOn(ItemCategoryController.class).buscarPorNombre(nombre)).withSelfRel()));
    }

    // GET /api/v1/item-categories/limite?valor=300
    @Operation(summary = "Listar categorías acotadas por Límite de Valor USD", description = "Retorna las clasificaciones arancelarias cuya franquicia máxima permitida sea menor o igual al monto parametrizado.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Filtro volumétrico de franquicias ejecutado con éxito")
    })
    @GetMapping("/limite")
    public ResponseEntity<CollectionModel<EntityModel<ItemCategory>>> obtenerPorLimite(@RequestParam BigDecimal valor) {
        List<EntityModel<ItemCategory>> categories = categoryService.obtenerPorLimiteValor(valor).stream()
                .map(c -> EntityModel.of(c,
                        linkTo(methodOn(ItemCategoryController.class).obtenerPorId(c.getId())).withSelfRel()))
                .toList();
        return ResponseEntity.ok(CollectionModel.of(categories,
                linkTo(methodOn(ItemCategoryController.class).obtenerPorLimite(valor)).withSelfRel()));
    }

    // GET /api/v1/item-categories/activas/ordenadas
    @Operation(summary = "Listar categorías activas ordenadas alfabéticamente", description = "Recupera el catálogo operativo ordenado correlativamente de la A a la Z por nombre, optimizado para su despliegue en Frontend.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Catálogo ordenado secuencialmente recuperado con éxito")
    })
    @GetMapping("/activas/ordenadas")
    public ResponseEntity<CollectionModel<EntityModel<ItemCategory>>> obtenerActivasOrdenadas() {
        List<EntityModel<ItemCategory>> categories = categoryService.obtenerActivasOrdenadas().stream()
                .map(c -> EntityModel.of(c,
                        linkTo(methodOn(ItemCategoryController.class).obtenerPorId(c.getId())).withSelfRel()))
                .toList();
        return ResponseEntity.ok(CollectionModel.of(categories,
                linkTo(methodOn(ItemCategoryController.class).obtenerActivasOrdenadas()).withSelfRel()));
    }
}