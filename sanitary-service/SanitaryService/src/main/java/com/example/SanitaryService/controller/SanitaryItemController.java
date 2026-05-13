// controller/SanitaryItemController.java
package com.example.SanitaryService.controller;

import com.example.SanitaryService.dto.SanitaryItemDTO;
import com.example.SanitaryService.model.SanitaryItem;
import com.example.SanitaryService.service.SanitaryItemService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/sanitary/items")
@RequiredArgsConstructor
public class SanitaryItemController {

    private final SanitaryItemService itemService;

    // -------------------------------------------------------
    // CRUD BÁSICO
    // -------------------------------------------------------

    // GET /api/v1/sanitary/items
    @GetMapping
    public ResponseEntity<List<SanitaryItem>> obtenerTodos() {
        return ResponseEntity.ok(itemService.obtenerTodos());
    }

    // GET /api/v1/sanitary/items/1
    @GetMapping("/{id}")
    public ResponseEntity<SanitaryItem> obtenerPorId(
            @PathVariable Long id) {
        return ResponseEntity.ok(itemService.obtenerPorId(id));
    }

    // POST /api/v1/sanitary/items
    @PostMapping
    public ResponseEntity<SanitaryItem> agregar(
            @Valid @RequestBody SanitaryItemDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(itemService.agregar(dto));
    }

    // PUT /api/v1/sanitary/items/1
    @PutMapping("/{id}")
    public ResponseEntity<SanitaryItem> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody SanitaryItemDTO dto) {
        return ResponseEntity.ok(itemService.actualizar(id, dto));
    }

    // DELETE /api/v1/sanitary/items/1
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        itemService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    // -------------------------------------------------------
    // CONSULTAS DERIVADAS
    // -------------------------------------------------------

    // GET /api/v1/sanitary/items/inspeccion/1
    @GetMapping("/inspeccion/{sanitaryId}")
    public ResponseEntity<List<SanitaryItem>> obtenerPorInspeccion(
            @PathVariable Long sanitaryId) {
        return ResponseEntity.ok(
                itemService.obtenerPorInspeccion(sanitaryId));
    }

    // GET /api/v1/sanitary/items/inspeccion/1/resultado/APROBADO
    @GetMapping("/inspeccion/{sanitaryId}/resultado/{resultado}")
    public ResponseEntity<List<SanitaryItem>> obtenerPorResultado(
            @PathVariable Long sanitaryId,
            @PathVariable String resultado) {
        return ResponseEntity.ok(
                itemService.obtenerPorInspeccionYResultado(
                        sanitaryId, resultado));
    }

    // GET /api/v1/sanitary/items/resultado/RECHAZADO
    @GetMapping("/resultado/{resultado}")
    public ResponseEntity<List<SanitaryItem>> obtenerTodosPorResultado(
            @PathVariable String resultado) {
        return ResponseEntity.ok(
                itemService.obtenerPorResultado(resultado));
    }

    // GET /api/v1/sanitary/items/buscar?descripcion=animal
    @GetMapping("/buscar")
    public ResponseEntity<List<SanitaryItem>> buscarPorDescripcion(
            @RequestParam String descripcion) {
        return ResponseEntity.ok(
                itemService.buscarPorDescripcion(descripcion));
    }

    // GET /api/v1/sanitary/items/inspeccion/1/ordenados
    @GetMapping("/inspeccion/{sanitaryId}/ordenados")
    public ResponseEntity<List<SanitaryItem>> obtenerOrdenados(
            @PathVariable Long sanitaryId) {
        return ResponseEntity.ok(
                itemService.obtenerPorInspeccionOrdenados(sanitaryId));
    }

    // GET /api/v1/sanitary/items/ultimos
    @GetMapping("/ultimos")
    public ResponseEntity<List<SanitaryItem>> obtenerUltimos() {
        return ResponseEntity.ok(itemService.obtenerUltimosItems());
    }

    // GET /api/v1/sanitary/items/estadisticas/rechazados/1
    @GetMapping("/estadisticas/rechazados/{sanitaryId}")
    public ResponseEntity<Map<String, Long>> contarRechazados(
            @PathVariable Long sanitaryId) {
        long total = itemService.contarRechazadosPorInspeccion(sanitaryId);
        return ResponseEntity.ok(Map.of("total", total));
    }
}