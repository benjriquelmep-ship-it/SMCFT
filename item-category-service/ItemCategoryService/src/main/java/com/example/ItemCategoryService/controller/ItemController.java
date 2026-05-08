// controller/ItemController.java

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

@RestController
@RequestMapping("/api/v1/items")
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;

    // -------------------------------------------------------
    // CRUD BÁSICO
    // -------------------------------------------------------

    // GET /api/v1/items
    @GetMapping
    public ResponseEntity<List<Item>> obtenerTodos() {
        return ResponseEntity.ok(itemService.obtenerTodos());
    }

    // GET /api/v1/items/1
    @GetMapping("/{id}")
    public ResponseEntity<Item> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(itemService.obtenerPorId(id));
    }

    // POST /api/v1/items
    @PostMapping
    public ResponseEntity<Item> crear(
            @Valid @RequestBody ItemDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(itemService.crear(dto));
    }

    // PUT /api/v1/items/1
    @PutMapping("/{id}")
    public ResponseEntity<Item> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody ItemDTO dto) {
        return ResponseEntity.ok(itemService.actualizar(id, dto));
    }

    // PATCH /api/v1/items/1/desactivar
    @PatchMapping("/{id}/desactivar")
    public ResponseEntity<Void> desactivar(@PathVariable Long id) {
        itemService.desactivar(id);
        return ResponseEntity.noContent().build();
    }

    // DELETE /api/v1/items/1
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        itemService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    // -------------------------------------------------------
    // CONSULTAS DERIVADAS
    // -------------------------------------------------------

    // GET /api/v1/items/categoria/1
    @GetMapping("/categoria/{categoryId}")
    public ResponseEntity<List<Item>> obtenerPorCategoria(
            @PathVariable Long categoryId) {
        return ResponseEntity.ok(
                itemService.obtenerPorCategoria(categoryId));
    }

    // GET /api/v1/items/categoria/1/activos
    @GetMapping("/categoria/{categoryId}/activos")
    public ResponseEntity<List<Item>> obtenerActivosPorCategoria(
            @PathVariable Long categoryId) {
        return ResponseEntity.ok(
                itemService.obtenerActivosPorCategoria(categoryId));
    }

    // GET /api/v1/items/activos
    @GetMapping("/activos")
    public ResponseEntity<List<Item>> obtenerActivos() {
        return ResponseEntity.ok(itemService.obtenerActivos());
    }

    // GET /api/v1/items/buscar?nombre=laptop
    @GetMapping("/buscar")
    public ResponseEntity<List<Item>> buscarPorNombre(
            @RequestParam String nombre) {
        return ResponseEntity.ok(itemService.buscarPorNombre(nombre));
    }

    // GET /api/v1/items/unidad/kg
    @GetMapping("/unidad/{unidad}")
    public ResponseEntity<List<Item>> obtenerPorUnidad(
            @PathVariable String unidad) {
        return ResponseEntity.ok(itemService.obtenerPorUnidad(unidad));
    }

    // GET /api/v1/items/categoria/1/ordenados
    @GetMapping("/categoria/{categoryId}/ordenados")
    public ResponseEntity<List<Item>> obtenerPorCategoriaOrdenados(
            @PathVariable Long categoryId) {
        return ResponseEntity.ok(
                itemService.obtenerPorCategoriaOrdenados(categoryId));
    }

    // GET /api/v1/items/ultimos
    @GetMapping("/ultimos")
    public ResponseEntity<List<Item>> obtenerUltimos() {
        return ResponseEntity.ok(itemService.obtenerUltimosItems());
    }
}