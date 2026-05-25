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

@RestController
@RequestMapping("/api/v1/item-categories")
@RequiredArgsConstructor
public class ItemCategoryController {

    // ItemCategoryService contiene toda la lógica de negocio
    // El Controller solo recibe y responde — nunca tiene lógica propia
    private final ItemCategoryService categoryService;

    // GET /api/v1/item-categories
    // Devuelve todas las categorías del sistema
    @GetMapping
    public ResponseEntity<List<ItemCategory>> obtenerTodas() {
        return ResponseEntity.ok(categoryService.obtenerTodas());
    }

    // GET /api/v1/item-categories/1
    // Devuelve una categoría específica por su id
    // Border Crossing Service llama a este endpoint
    // para verificar que la categoría existe y está activa
    // antes de agregar un item a un cruce
    @GetMapping("/{id}")
    public ResponseEntity<ItemCategory> obtenerPorId(
            @PathVariable Long id) {
        return ResponseEntity.ok(categoryService.obtenerPorId(id));
    }

    // POST /api/v1/item-categories
    // Crea una nueva categoría de item
    @PostMapping
    public ResponseEntity<ItemCategory> crear(
            @Valid @RequestBody ItemCategoryDTO dto) {
        // HTTP 201 = se creó un nuevo recurso exitosamente
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(categoryService.crear(dto));
    }

    // PUT /api/v1/item-categories/1
    // Actualiza TODOS los campos de una categoría
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
    @PatchMapping("/{id}/desactivar")
    public ResponseEntity<Void> desactivar(@PathVariable Long id) {
        categoryService.desactivar(id);
        // HTTP 204 = operación exitosa sin contenido en la respuesta
        return ResponseEntity.noContent().build();
    }

    // DELETE /api/v1/item-categories/1
    // Elimina una categoría por su id
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        categoryService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    // GET /api/v1/item-categories/activas
    // Devuelve solo las categorías activas (activo = true)
    // Border Crossing Service solo puede usar categorías activas
    @GetMapping("/activas")
    public ResponseEntity<List<ItemCategory>> obtenerActivas() {
        return ResponseEntity.ok(categoryService.obtenerActivas());
    }

    // GET /api/v1/item-categories/inactivas
    // Devuelve solo las categorías desactivadas (activo = false)
    // Útil para ver qué categorías ya no están disponibles
    @GetMapping("/inactivas")
    public ResponseEntity<List<ItemCategory>> obtenerInactivas() {
        return ResponseEntity.ok(categoryService.obtenerInactivas());
    }

    // GET /api/v1/item-categories/requieren-declaracion
    // Devuelve categorías que requieren declaración obligatoria
    @GetMapping("/requieren-declaracion")
    public ResponseEntity<List<ItemCategory>> obtenerConDeclaracion() {
        return ResponseEntity.ok(
                categoryService.obtenerQueRequierenDeclaracion());
    }

    // GET /api/v1/item-categories/sin-declaracion
    // Devuelve categorías que NO requieren declaración obligatoria
    @GetMapping("/sin-declaracion")
    public ResponseEntity<List<ItemCategory>> obtenerSinDeclaracion() {
        return ResponseEntity.ok(categoryService.obtenerSinDeclaracion());
    }

    // GET /api/v1/item-categories/buscar?nombre=electro
    // Busca categorías cuyo nombre contenga el texto buscado
    // No necesitas escribir el nombre exacto — busca si lo contiene
    @GetMapping("/buscar")
    public ResponseEntity<List<ItemCategory>> buscarPorNombre(
            @RequestParam String nombre) {
        return ResponseEntity.ok(categoryService.buscarPorNombre(nombre));
    }

    // GET /api/v1/item-categories/limite?valor=300
    // Devuelve categorías cuyo límite de valor sea menor o igual al valor buscado
    @GetMapping("/limite")
    public ResponseEntity<List<ItemCategory>> obtenerPorLimite(
            @RequestParam BigDecimal valor) {
        return ResponseEntity.ok(
                categoryService.obtenerPorLimiteValor(valor));
    }

    // GET /api/v1/item-categories/activas/ordenadas
    // Devuelve categorías activas ordenadas alfabéticamente por nombre
    // Útil para mostrar una lista ordenada en el frontend
    @GetMapping("/activas/ordenadas")
    public ResponseEntity<List<ItemCategory>> obtenerActivasOrdenadas() {
        return ResponseEntity.ok(
                categoryService.obtenerActivasOrdenadas());
    }
}