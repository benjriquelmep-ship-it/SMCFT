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

@RestController
@RequestMapping("/api/v1/items")
@RequiredArgsConstructor
public class ItemController {

    // ItemService contiene toda la lógica de negocio
    // El Controller solo recibe y responde — nunca tiene lógica propia
    private final ItemService itemService;

    // GET /api/v1/items
    // Devuelve todos los items del sistema
    @GetMapping
    public ResponseEntity<List<Item>> obtenerTodos() {
        return ResponseEntity.ok(itemService.obtenerTodos());
    }

    // GET /api/v1/items/1
    // Devuelve un item específico por su id
    @GetMapping("/{id}")
    public ResponseEntity<Item> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(itemService.obtenerPorId(id));
    }

    // POST /api/v1/items
    // Crea un nuevo item dentro de una categoría
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
    @PutMapping("/{id}")
    public ResponseEntity<Item> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody ItemDTO dto) {
        return ResponseEntity.ok(itemService.actualizar(id, dto));
    }

    // PATCH /api/v1/items/1/desactivar
    // Desactiva un item — cambia activo a false
    // Los items desactivados no pueden usarse en nuevos cruces
    @PatchMapping("/{id}/desactivar")
    public ResponseEntity<Void> desactivar(@PathVariable Long id) {
        itemService.desactivar(id);
        // HTTP 204 = operación exitosa sin contenido en la respuesta
        return ResponseEntity.noContent().build();
    }

    // DELETE /api/v1/items/1
    // Elimina un item por su id
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        itemService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    // GET /api/v1/items/categoria/1
    // Devuelve todos los items de una categoría específica
    // Incluye activos e inactivos
    @GetMapping("/categoria/{categoryId}")
    public ResponseEntity<List<Item>> obtenerPorCategoria(
            @PathVariable Long categoryId) {
        return ResponseEntity.ok(
                itemService.obtenerPorCategoria(categoryId));
    }

    // GET /api/v1/items/categoria/1/activos
    // Devuelve solo los items ACTIVOS de una categoría específica
    // Los items activos son los que se pueden usar en nuevos cruces
    @GetMapping("/categoria/{categoryId}/activos")
    public ResponseEntity<List<Item>> obtenerActivosPorCategoria(
            @PathVariable Long categoryId) {
        return ResponseEntity.ok(
                itemService.obtenerActivosPorCategoria(categoryId));
    }

    // GET /api/v1/items/activos
    // Devuelve TODOS los items activos del sistema sin importar la categoría
    // Útil para ver qué items están disponibles en el sistema
    @GetMapping("/activos")
    public ResponseEntity<List<Item>> obtenerActivos() {
        return ResponseEntity.ok(itemService.obtenerActivos());
    }

    // GET /api/v1/items/buscar?nombre=laptop
    // Busca items cuyo nombre contenga el texto buscado
    // No necesitas escribir el nombre exacto — busca si lo contiene
    @GetMapping("/buscar")
    public ResponseEntity<List<Item>> buscarPorNombre(
            @RequestParam String nombre) {
        return ResponseEntity.ok(itemService.buscarPorNombre(nombre));
    }

    // GET /api/v1/items/unidad/kg
    // Devuelve todos los items que se miden en una unidad específica
    @GetMapping("/unidad/{unidad}")
    public ResponseEntity<List<Item>> obtenerPorUnidad(
            @PathVariable String unidad) {
        return ResponseEntity.ok(itemService.obtenerPorUnidad(unidad));
    }

    // GET /api/v1/items/categoria/1/ordenados
    // Devuelve los items de una categoría ordenados alfabéticamente por nombre
    // Útil para mostrar una lista ordenada en el frontend
    @GetMapping("/categoria/{categoryId}/ordenados")
    public ResponseEntity<List<Item>> obtenerPorCategoriaOrdenados(
            @PathVariable Long categoryId) {
        return ResponseEntity.ok(
                itemService.obtenerPorCategoriaOrdenados(categoryId));
    }

    // GET /api/v1/items/ultimos
    // Devuelve los últimos 10 items registrados en el sistema
    // Útil para monitorear los items más recientemente creados
    @GetMapping("/ultimos")
    public ResponseEntity<List<Item>> obtenerUltimos() {
        return ResponseEntity.ok(itemService.obtenerUltimosItems());
    }
}