// controller/EntryItemController.java

package com.example.EntryService.controller;

import com.example.EntryService.dto.EntryItemDTO;
import com.example.EntryService.model.EntryItem;
import com.example.EntryService.service.EntryItemService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
// Todas las rutas empiezan con /api/v1/entries/items
@RequestMapping("/api/v1/entries/items")
@RequiredArgsConstructor
public class EntryItemController {

    private final EntryItemService itemService;

    // -------------------------------------------------------
    // CRUD BÁSICO
    // -------------------------------------------------------

    // GET /api/v1/entries/items
    @GetMapping
    public ResponseEntity<List<EntryItem>> obtenerTodos() {
        return ResponseEntity.ok(itemService.obtenerTodos());
    }

    // GET /api/v1/entries/items/1
    @GetMapping("/{id}")
    public ResponseEntity<EntryItem> obtenerPorId(
            @PathVariable Long id) {
        return ResponseEntity.ok(itemService.obtenerPorId(id));
    }

    // POST /api/v1/entries/items
    @PostMapping
    public ResponseEntity<EntryItem> agregar(
            @Valid @RequestBody EntryItemDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(itemService.agregar(dto));
    }

    // PATCH /api/v1/entries/items/1/aprobar
    @PatchMapping("/{id}/aprobar")
    public ResponseEntity<EntryItem> aprobar(@PathVariable Long id) {
        return ResponseEntity.ok(itemService.aprobar(id));
    }

    // PATCH /api/v1/entries/items/1/rechazar
    @PatchMapping("/{id}/rechazar")
    public ResponseEntity<EntryItem> rechazar(@PathVariable Long id) {
        return ResponseEntity.ok(itemService.rechazar(id));
    }

    // DELETE /api/v1/entries/items/1
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        itemService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    // -------------------------------------------------------
    // CONSULTAS DERIVADAS
    // -------------------------------------------------------

    // GET /api/v1/entries/items/ingreso/1
    @GetMapping("/ingreso/{entryId}")
    public ResponseEntity<List<EntryItem>> obtenerPorIngreso(
            @PathVariable Long entryId) {
        return ResponseEntity.ok(itemService.obtenerPorIngreso(entryId));
    }

    // GET /api/v1/entries/items/ingreso/1/aprobados
    @GetMapping("/ingreso/{entryId}/aprobados")
    public ResponseEntity<List<EntryItem>> obtenerAprobados(
            @PathVariable Long entryId) {
        return ResponseEntity.ok(
                itemService.obtenerAprobadosPorIngreso(entryId));
    }

    // GET /api/v1/entries/items/ingreso/1/no-aprobados
    @GetMapping("/ingreso/{entryId}/no-aprobados")
    public ResponseEntity<List<EntryItem>> obtenerNoAprobados(
            @PathVariable Long entryId) {
        return ResponseEntity.ok(
                itemService.obtenerNoAprobadosPorIngreso(entryId));
    }

    // GET /api/v1/entries/items/buscar?descripcion=laptop
    @GetMapping("/buscar")
    public ResponseEntity<List<EntryItem>> buscarPorDescripcion(
            @RequestParam String descripcion) {
        return ResponseEntity.ok(
                itemService.buscarPorDescripcion(descripcion));
    }

    // GET /api/v1/entries/items/ingreso/1/ordenados
    @GetMapping("/ingreso/{entryId}/ordenados")
    public ResponseEntity<List<EntryItem>> obtenerOrdenados(
            @PathVariable Long entryId) {
        return ResponseEntity.ok(
                itemService.obtenerPorIngresoOrdenadosPorValor(entryId));
    }

    // GET /api/v1/entries/items/ultimos
    @GetMapping("/ultimos")
    public ResponseEntity<List<EntryItem>> obtenerUltimos() {
        return ResponseEntity.ok(itemService.obtenerUltimosItems());
    }
}