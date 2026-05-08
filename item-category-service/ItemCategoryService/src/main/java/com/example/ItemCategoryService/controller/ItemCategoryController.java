// controller/ItemCategoryController.java

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

    private final ItemCategoryService categoryService;

    // -------------------------------------------------------
    // CRUD BÁSICO
    // -------------------------------------------------------

    // GET /api/v1/item-categories
    @GetMapping
    public ResponseEntity<List<ItemCategory>> obtenerTodas() {
        return ResponseEntity.ok(categoryService.obtenerTodas());
    }

    // GET /api/v1/item-categories/1
    // Border Crossing Service llama este endpoint para validar categorías
    @GetMapping("/{id}")
    public ResponseEntity<ItemCategory> obtenerPorId(
            @PathVariable Long id) {
        return ResponseEntity.ok(categoryService.obtenerPorId(id));
    }

    // POST /api/v1/item-categories
    @PostMapping
    public ResponseEntity<ItemCategory> crear(
            @Valid @RequestBody ItemCategoryDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(categoryService.crear(dto));
    }

    // PUT /api/v1/item-categories/1
    @PutMapping("/{id}")
    public ResponseEntity<ItemCategory> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody ItemCategoryDTO dto) {
        return ResponseEntity.ok(categoryService.actualizar(id, dto));
    }

    // PATCH /api/v1/item-categories/1/desactivar
    @PatchMapping("/{id}/desactivar")
    public ResponseEntity<Void> desactivar(@PathVariable Long id) {
        categoryService.desactivar(id);
        return ResponseEntity.noContent().build();
    }

    // DELETE /api/v1/item-categories/1
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        categoryService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    // -------------------------------------------------------
    // CONSULTAS DERIVADAS
    // -------------------------------------------------------

    // GET /api/v1/item-categories/activas
    @GetMapping("/activas")
    public ResponseEntity<List<ItemCategory>> obtenerActivas() {
        return ResponseEntity.ok(categoryService.obtenerActivas());
    }

    // GET /api/v1/item-categories/inactivas
    @GetMapping("/inactivas")
    public ResponseEntity<List<ItemCategory>> obtenerInactivas() {
        return ResponseEntity.ok(categoryService.obtenerInactivas());
    }

    // GET /api/v1/item-categories/requieren-declaracion
    @GetMapping("/requieren-declaracion")
    public ResponseEntity<List<ItemCategory>> obtenerConDeclaracion() {
        return ResponseEntity.ok(
                categoryService.obtenerQueRequierenDeclaracion());
    }

    // GET /api/v1/item-categories/sin-declaracion
    @GetMapping("/sin-declaracion")
    public ResponseEntity<List<ItemCategory>> obtenerSinDeclaracion() {
        return ResponseEntity.ok(categoryService.obtenerSinDeclaracion());
    }

    // GET /api/v1/item-categories/buscar?nombre=electro
    @GetMapping("/buscar")
    public ResponseEntity<List<ItemCategory>> buscarPorNombre(
            @RequestParam String nombre) {
        return ResponseEntity.ok(categoryService.buscarPorNombre(nombre));
    }

    // GET /api/v1/item-categories/limite?valor=300
    @GetMapping("/limite")
    public ResponseEntity<List<ItemCategory>> obtenerPorLimite(
            @RequestParam BigDecimal valor) {
        return ResponseEntity.ok(
                categoryService.obtenerPorLimiteValor(valor));
    }

    // GET /api/v1/item-categories/activas/ordenadas
    @GetMapping("/activas/ordenadas")
    public ResponseEntity<List<ItemCategory>> obtenerActivasOrdenadas() {
        return ResponseEntity.ok(
                categoryService.obtenerActivasOrdenadas());
    }
}