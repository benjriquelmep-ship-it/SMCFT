// controller/BorderCrossingItemController.java

package com.example.BorderCrossingService.controller;

import com.example.BorderCrossingService.dto.BorderCrossingItemDTO;
import com.example.BorderCrossingService.model.BorderCrossingItem;
import com.example.BorderCrossingService.service.BorderCrossingItemService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
// Todas las rutas empiezan con /api/v1/border-crossings/items
@RequestMapping("/api/v1/border-crossings/items")
@RequiredArgsConstructor
public class BorderCrossingItemController {

    private final BorderCrossingItemService itemService;

    // -------------------------------------------------------
    // CRUD BÁSICO
    // -------------------------------------------------------

    // GET /api/v1/border-crossings/items
    @GetMapping
    public ResponseEntity<List<BorderCrossingItem>> obtenerTodos() {
        return ResponseEntity.ok(itemService.obtenerTodos());
    }

    // GET /api/v1/border-crossings/items/1
    @GetMapping("/{id}")
    public ResponseEntity<BorderCrossingItem> obtenerPorId(
            @PathVariable Long id) {
        return ResponseEntity.ok(itemService.obtenerPorId(id));
    }

    // POST /api/v1/border-crossings/items
    @PostMapping
    public ResponseEntity<BorderCrossingItem> agregar(
            @Valid @RequestBody BorderCrossingItemDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(itemService.agregar(dto));
    }

    // PATCH /api/v1/border-crossings/items/1/aprobar
    @PatchMapping("/{id}/aprobar")
    public ResponseEntity<BorderCrossingItem> aprobar(
            @PathVariable Long id) {
        return ResponseEntity.ok(itemService.aprobar(id));
    }

    // PATCH /api/v1/border-crossings/items/1/rechazar
    @PatchMapping("/{id}/rechazar")
    public ResponseEntity<BorderCrossingItem> rechazar(
            @PathVariable Long id) {
        return ResponseEntity.ok(itemService.rechazar(id));
    }

    // DELETE /api/v1/border-crossings/items/1
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        itemService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    // -------------------------------------------------------
    // CONSULTAS DERIVADAS
    // -------------------------------------------------------

    // GET /api/v1/border-crossings/items/cruce/1
    @GetMapping("/cruce/{borderCrossingId}")
    public ResponseEntity<List<BorderCrossingItem>> obtenerPorCruce(
            @PathVariable Long borderCrossingId) {
        return ResponseEntity.ok(
                itemService.obtenerPorCruce(borderCrossingId));
    }

    // GET /api/v1/border-crossings/items/cruce/1/aprobados
    @GetMapping("/cruce/{borderCrossingId}/aprobados")
    public ResponseEntity<List<BorderCrossingItem>> obtenerAprobados(
            @PathVariable Long borderCrossingId) {
        return ResponseEntity.ok(
                itemService.obtenerAprobadosPorCruce(borderCrossingId));
    }

    // GET /api/v1/border-crossings/items/cruce/1/no-aprobados
    @GetMapping("/cruce/{borderCrossingId}/no-aprobados")
    public ResponseEntity<List<BorderCrossingItem>> obtenerNoAprobados(
            @PathVariable Long borderCrossingId) {
        return ResponseEntity.ok(
                itemService.obtenerNoAprobadosPorCruce(borderCrossingId));
    }

    // GET /api/v1/border-crossings/items/categoria/1
    @GetMapping("/categoria/{categoriaId}")
    public ResponseEntity<List<BorderCrossingItem>> obtenerPorCategoria(
            @PathVariable Long categoriaId) {
        return ResponseEntity.ok(
                itemService.obtenerPorCategoria(categoriaId));
    }

    // GET /api/v1/border-crossings/items/buscar?descripcion=laptop
    @GetMapping("/buscar")
    public ResponseEntity<List<BorderCrossingItem>> buscarPorDescripcion(
            @RequestParam String descripcion) {
        return ResponseEntity.ok(
                itemService.buscarPorDescripcion(descripcion));
    }

    // GET /api/v1/border-crossings/items/cruce/1/ordenados
    @GetMapping("/cruce/{borderCrossingId}/ordenados")
    public ResponseEntity<List<BorderCrossingItem>> obtenerOrdenados(
            @PathVariable Long borderCrossingId) {
        return ResponseEntity.ok(
                itemService.obtenerPorCruceOrdenadosPorValor(
                        borderCrossingId));
    }

    // GET /api/v1/border-crossings/items/ultimos
    @GetMapping("/ultimos")
    public ResponseEntity<List<BorderCrossingItem>> obtenerUltimos() {
        return ResponseEntity.ok(itemService.obtenerUltimosItems());
    }
}