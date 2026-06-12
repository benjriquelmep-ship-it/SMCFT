// Recibe las peticiones HTTP del Item Category Service
// Llama al Service y retorna ResponseEntity con JSON
// Nunca tiene lógica de negocio directamente
package com.example.ItemCategoryService.controller;

import com.example.ItemCategoryService.dto.ItemCategoryDTO;
import com.example.ItemCategoryService.model.ItemCategory;
import com.example.ItemCategoryService.service.ItemCategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.util.List;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@RestController
@RequestMapping("/api/v1/item-categories")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class ItemCategoryController {

    // ItemCategoryService contiene toda la lógica de negocio
    // El Controller solo recibe y responde — nunca tiene lógica propia
    private final ItemCategoryService categoryService;

    // GET /api/v1/item-categories
    // Devuelve todas las categorías del sistema
    @Operation(summary = "Obtener Todas", description = "Devuelve todas las categorías del sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operación exitosa"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "Sin permisos")
    })
    @GetMapping
    public ResponseEntity<List<ItemCategory>> obtenerTodas() {
        return ResponseEntity.ok(categoryService.obtenerTodas());
    }

    // GET /api/v1/item-categories/1
    // Devuelve una categoría específica por su id
    // Border Crossing Service llama a este endpoint
    // para verificar que la categoría existe y está activa
    // antes de agregar un item a un cruce
    @Operation(summary = "Obtener Por Id", description = "antes de agregar un item a un cruce")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operación exitosa"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "Sin permisos")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ItemCategory> obtenerPorId(
            @PathVariable Long id) {
        return ResponseEntity.ok(categoryService.obtenerPorId(id));
    }

    // POST /api/v1/item-categories
    // Crea una nueva categoría de item
    @Operation(summary = "Crear", description = "Crea una nueva categoría de item")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Operación exitosa"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "Sin permisos")
    })
    @PostMapping
    public ResponseEntity<ItemCategory> crear(
            @Valid @RequestBody ItemCategoryDTO dto) {
        // HTTP 201 = se creó un nuevo recurso exitosamente
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(categoryService.crear(dto));
    }

    // PUT /api/v1/item-categories/1
    // Actualiza TODOS los campos de una categoría
    @Operation(summary = "Actualizar", description = "Actualiza TODOS los campos de una categoría")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operación exitosa"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "Sin permisos")
    })
    @PutMapping("/{id}")
    public ResponseEntity<ItemCategory> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody ItemCategoryDTO dto) {
        return ResponseEntity.ok(categoryService.actualizar(id, dto));
    }

    // PATCH /api/v1/item-categories/1/desactivar
    // Desactiva una categoría — cambia activo a false
    // Las categorías desactivadas no pueden usarse en nuevos items
    // ResponseEntity<Void> = respuesta sin body → HTTP 204
    @Operation(summary = "Desactivar", description = "ResponseEntity<Void> = respuesta sin body → HTTP 204")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operación exitosa"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "Sin permisos")
    })
    @PatchMapping("/{id}/desactivar")
    public ResponseEntity<Void> desactivar(@PathVariable Long id) {
        categoryService.desactivar(id);
        // HTTP 204 = operación exitosa sin contenido en la respuesta
        return ResponseEntity.noContent().build();
    }

    // DELETE /api/v1/item-categories/1
    // Elimina una categoría por su id
    @Operation(summary = "Eliminar", description = "Elimina una categoría por su id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Operación exitosa"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "Sin permisos")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        categoryService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    // GET /api/v1/item-categories/activas
    // Devuelve solo las categorías activas (activo = true)
    // Border Crossing Service solo puede usar categorías activas
    @Operation(summary = "Obtener Activas", description = "Border Crossing Service solo puede usar categorías activas")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operación exitosa"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "Sin permisos")
    })
    @GetMapping("/activas")
    public ResponseEntity<List<ItemCategory>> obtenerActivas() {
        return ResponseEntity.ok(categoryService.obtenerActivas());
    }

    // GET /api/v1/item-categories/inactivas
    // Devuelve solo las categorías desactivadas (activo = false)
    // Útil para ver qué categorías ya no están disponibles
    @Operation(summary = "Obtener Inactivas", description = "Útil para ver qué categorías ya no están disponibles")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operación exitosa"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "Sin permisos")
    })
    @GetMapping("/inactivas")
    public ResponseEntity<List<ItemCategory>> obtenerInactivas() {
        return ResponseEntity.ok(categoryService.obtenerInactivas());
    }

    // GET /api/v1/item-categories/requieren-declaracion
    // Devuelve categorías que requieren declaración obligatoria
    @Operation(summary = "Obtener Con Declaracion", description = "Devuelve categorías que requieren declaración obligatoria")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operación exitosa"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "Sin permisos")
    })
    @GetMapping("/requieren-declaracion")
    public ResponseEntity<List<ItemCategory>> obtenerConDeclaracion() {
        return ResponseEntity.ok(
                categoryService.obtenerRequierenDeclaracion());
    }

    // GET /api/v1/item-categories/sin-declaracion
    // Devuelve categorías que NO requieren declaración obligatoria
    @Operation(summary = "Obtener Sin Declaracion", description = "Devuelve categorías que NO requieren declaración obligatoria")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operación exitosa"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "Sin permisos")
    })
    @GetMapping("/sin-declaracion")
    public ResponseEntity<List<ItemCategory>> obtenerSinDeclaracion() {
        return ResponseEntity.ok(categoryService.obtenerSinDeclaracion());
    }

    // GET /api/v1/item-categories/buscar?nombre=electro
    // Busca categorías cuyo nombre contenga el texto buscado
    // No necesitas escribir el nombre exacto — busca si lo contiene
    @Operation(summary = "Buscar Por Nombre", description = "No necesitas escribir el nombre exacto — busca si lo contiene")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operación exitosa"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "Sin permisos")
    })
    @GetMapping("/buscar")
    public ResponseEntity<List<ItemCategory>> buscarPorNombre(
            @RequestParam String nombre) {
        return ResponseEntity.ok(categoryService.buscarPorNombre(nombre));
    }

    // GET /api/v1/item-categories/limite?valor=300
    // Devuelve categorías cuyo límite de valor sea menor o igual al valor buscado
    @Operation(summary = "Obtener Por Limite", description = "Devuelve categorías cuyo límite de valor sea menor o igual al valor buscado")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operación exitosa"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "Sin permisos")
    })
    @GetMapping("/limite")
    public ResponseEntity<List<ItemCategory>> obtenerPorLimite(
            @RequestParam BigDecimal valor) {
        return ResponseEntity.ok(
                categoryService.obtenerPorLimiteValor(valor));
    }

    // GET /api/v1/item-categories/activas/ordenadas
    // Devuelve categorías activas ordenadas alfabéticamente por nombre
    // Útil para mostrar una lista ordenada en el frontend
    @Operation(summary = "Obtener Activas Ordenadas", description = "Útil para mostrar una lista ordenada en el frontend")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operación exitosa"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "Sin permisos")
    })
    @GetMapping("/activas/ordenadas")
    public ResponseEntity<List<ItemCategory>> obtenerActivasOrdenadas() {
        return ResponseEntity.ok(
                categoryService.obtenerActivasOrdenadas());
    }
}