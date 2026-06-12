// Recibe las peticiones HTTP para los items del Item Category Service
// Llama al Service y retorna ResponseEntity con JSON
// Nunca tiene lógica de negocio directamente
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

@RestController
@RequestMapping("/api/v1/items")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class ItemController {

    // ItemService contiene toda la lógica de negocio
    // El Controller solo recibe y responde — nunca tiene lógica propia
    private final ItemService itemService;

    // GET /api/v1/items
    // Devuelve todos los items del sistema
    @Operation(summary = "Obtener Todos", description = "Devuelve todos los items del sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operación exitosa"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "Sin permisos")
    })
    @GetMapping
    public ResponseEntity<List<Item>> obtenerTodos() {
        return ResponseEntity.ok(itemService.obtenerTodos());
    }

    // GET /api/v1/items/1
    // Devuelve un item específico por su id
    @Operation(summary = "Obtener Por Id", description = "Devuelve un item específico por su id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operación exitosa"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "Sin permisos")
    })
    @GetMapping("/{id}")
    public ResponseEntity<Item> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(itemService.obtenerPorId(id));
    }

    // POST /api/v1/items
    // Crea un nuevo item dentro de una categoría
    @Operation(summary = "Crear", description = "Crea un nuevo item dentro de una categoría")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Operación exitosa"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "Sin permisos")
    })
    @PostMapping
    public ResponseEntity<Item> crear(
            @Valid @RequestBody ItemDTO dto) {
        // HTTP 201 = se creó un nuevo recurso exitosamente
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(itemService.crear(dto));
    }

    // PUT /api/v1/items/1
    // Actualiza TODOS los campos de un item
    // El cliente debe mandar todos los campos del ItemDTO
    @Operation(summary = "Actualizar", description = "El cliente debe mandar todos los campos del ItemDTO")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operación exitosa"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "Sin permisos")
    })
    @PutMapping("/{id}")
    public ResponseEntity<Item> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody ItemDTO dto) {
        return ResponseEntity.ok(itemService.actualizar(id, dto));
    }

    // PATCH /api/v1/items/1/desactivar
    // Desactiva un item — cambia activo a false
    // Los items desactivados no pueden usarse en nuevos cruces
    @Operation(summary = "Desactivar", description = "Los items desactivados no pueden usarse en nuevos cruces")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operación exitosa"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "Sin permisos")
    })
    @PatchMapping("/{id}/desactivar")
    public ResponseEntity<Void> desactivar(@PathVariable Long id) {
        itemService.desactivar(id);
        // HTTP 204 = operación exitosa sin contenido en la respuesta
        return ResponseEntity.noContent().build();
    }

    // DELETE /api/v1/items/1
    // Elimina un item por su id
    @Operation(summary = "Eliminar", description = "Elimina un item por su id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Operación exitosa"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "Sin permisos")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        itemService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    // GET /api/v1/items/categoria/1
    // Devuelve todos los items de una categoría específica
    // Incluye activos e inactivos
    @Operation(summary = "Obtener Por Categoria", description = "Incluye activos e inactivos")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operación exitosa"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "Sin permisos")
    })
    @GetMapping("/categoria/{categoryId}")
    public ResponseEntity<List<Item>> obtenerPorCategoria(
            @PathVariable Long categoryId) {
        return ResponseEntity.ok(
                itemService.obtenerPorCategoria(categoryId));
    }

    // GET /api/v1/items/categoria/1/activos
    // Devuelve solo los items ACTIVOS de una categoría específica
    // Los items activos son los que se pueden usar en nuevos cruces
    @Operation(summary = "Obtener Activos Por Categoria", description = "Los items activos son los que se pueden usar en nuevos cruces")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operación exitosa"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "Sin permisos")
    })
    @GetMapping("/categoria/{categoryId}/activos")
    public ResponseEntity<List<Item>> obtenerActivosPorCategoria(
            @PathVariable Long categoryId) {
        return ResponseEntity.ok(
                itemService.obtenerActivosPorCategoria(categoryId));
    }

    // GET /api/v1/items/activos
    // Devuelve TODOS los items activos del sistema sin importar la categoría
    // Útil para ver qué items están disponibles en el sistema
    @Operation(summary = "Obtener Activos", description = "Útil para ver qué items están disponibles en el sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operación exitosa"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "Sin permisos")
    })
    @GetMapping("/activos")
    public ResponseEntity<List<Item>> obtenerActivos() {
        return ResponseEntity.ok(itemService.obtenerActivos());
    }

    // GET /api/v1/items/buscar?nombre=laptop
    // Busca items cuyo nombre contenga el texto buscado
    // No necesitas escribir el nombre exacto — busca si lo contiene
    @Operation(summary = "Buscar Por Nombre", description = "No necesitas escribir el nombre exacto — busca si lo contiene")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operación exitosa"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "Sin permisos")
    })
    @GetMapping("/buscar")
    public ResponseEntity<List<Item>> buscarPorNombre(
            @RequestParam String nombre) {
        return ResponseEntity.ok(itemService.buscarPorNombre(nombre));
    }

    // GET /api/v1/items/unidad/kg
    // Devuelve todos los items que se miden en una unidad específica
    @Operation(summary = "Obtener Por Unidad", description = "Devuelve todos los items que se miden en una unidad específica")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operación exitosa"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "Sin permisos")
    })
    @GetMapping("/unidad/{unidad}")
    public ResponseEntity<List<Item>> obtenerPorUnidad(
            @PathVariable String unidad) {
        return ResponseEntity.ok(itemService.obtenerPorUnidad(unidad));
    }

    // GET /api/v1/items/categoria/1/ordenados
    // Devuelve los items de una categoría ordenados alfabéticamente por nombre
    // Útil para mostrar una lista ordenada en el frontend
    @Operation(summary = "Obtener Por Categoria Ordenados", description = "Útil para mostrar una lista ordenada en el frontend")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operación exitosa"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "Sin permisos")
    })
    @GetMapping("/categoria/{categoryId}/ordenados")
    public ResponseEntity<List<Item>> obtenerPorCategoriaOrdenados(
            @PathVariable Long categoryId) {
        return ResponseEntity.ok(
                itemService.obtenerPorCategoriaOrdenados(categoryId));
    }

    // GET /api/v1/items/ultimos
    // Devuelve los últimos 10 items registrados en el sistema
    // Útil para monitorear los items más recientemente creados
    @Operation(summary = "Obtener Ultimos", description = "Útil para monitorear los items más recientemente creados")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operación exitosa"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "Sin permisos")
    })
    @GetMapping("/ultimos")
    public ResponseEntity<List<Item>> obtenerUltimos() {
        return ResponseEntity.ok(itemService.obtenerUltimosItems());
    }
}