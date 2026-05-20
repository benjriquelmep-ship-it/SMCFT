// Recibe las peticiones HTTP para los items de inspecciones sanitarias
// Llama al Service y retorna ResponseEntity con JSON
// Nunca tiene lógica de negocio directamente
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

    // SanitaryItemService contiene toda la lógica de negocio
    // El Controller solo recibe y responde — nunca tiene lógica propia
    private final SanitaryItemService itemService;

    // GET /api/v1/sanitary/items
    // Devuelve todos los items de todas las inspecciones del sistema
    @GetMapping
    public ResponseEntity<List<SanitaryItem>> obtenerTodos() {
        return ResponseEntity.ok(itemService.obtenerTodos());
    }

    // GET /api/v1/sanitary/items/1
    // Devuelve un item específico por su id
    @GetMapping("/{id}")
    public ResponseEntity<SanitaryItem> obtenerPorId(
            @PathVariable Long id) {
        return ResponseEntity.ok(itemService.obtenerPorId(id));
    }

    // POST /api/v1/sanitary/items
    // Agrega un nuevo item a una inspección existente
    @PostMapping
    public ResponseEntity<SanitaryItem> agregar(
            @Valid @RequestBody SanitaryItemDTO dto) {
        // HTTP 201 = se creó un nuevo recurso exitosamente
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(itemService.agregar(dto));
    }

    // PUT /api/v1/sanitary/items/1
    // Actualiza TODOS los campos de un item
    @PutMapping("/{id}")
    public ResponseEntity<SanitaryItem> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody SanitaryItemDTO dto) {
        return ResponseEntity.ok(itemService.actualizar(id, dto));
    }

    // DELETE /api/v1/sanitary/items/1
    // Elimina un item por su id
    // ResponseEntity<Void> = respuesta sin body → HTTP 204
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        itemService.eliminar(id);
        // HTTP 204 = operación exitosa sin contenido en la respuesta
        return ResponseEntity.noContent().build();
    }

    // GET /api/v1/sanitary/items/inspeccion/1
    // Devuelve todos los items que pertenecen a una inspección específica
    @GetMapping("/inspeccion/{sanitaryId}")
    public ResponseEntity<List<SanitaryItem>> obtenerPorInspeccion(
            @PathVariable Long sanitaryId) {
        return ResponseEntity.ok(
                itemService.obtenerPorInspeccion(sanitaryId));
    }

    // GET /api/v1/sanitary/items/inspeccion/1/resultado/APROBADO
    // Combina dos filtros: inspección + resultado
    @GetMapping("/inspeccion/{sanitaryId}/resultado/{resultado}")
    public ResponseEntity<List<SanitaryItem>> obtenerPorResultado(
            @PathVariable Long sanitaryId,
            @PathVariable String resultado) {
        return ResponseEntity.ok(
                itemService.obtenerPorInspeccionYResultado(
                        sanitaryId, resultado));
    }

    // GET /api/v1/sanitary/items/resultado/RECHAZADO
    // Devuelve TODOS los items del sistema con un resultado específico
    // Sin importar a qué inspección pertenecen
    @GetMapping("/resultado/{resultado}")
    public ResponseEntity<List<SanitaryItem>> obtenerTodosPorResultado(
            @PathVariable String resultado) {
        return ResponseEntity.ok(
                itemService.obtenerPorResultado(resultado));
    }

    // GET /api/v1/sanitary/items/buscar?descripcion=animal
    // Busca items cuya descripción contenga el texto buscado
    // No necesitas escribir la descripción exacta — busca si lo contiene
    @GetMapping("/buscar")
    public ResponseEntity<List<SanitaryItem>> buscarPorDescripcion(
            @RequestParam String descripcion) {
        return ResponseEntity.ok(
                itemService.buscarPorDescripcion(descripcion));
    }

    // GET /api/v1/sanitary/items/inspeccion/1/ordenados
    // Devuelve los items de una inspección ordenados alfabéticamente
    // por descripción
    // Útil para mostrar los items en orden en el frontend
    @GetMapping("/inspeccion/{sanitaryId}/ordenados")
    public ResponseEntity<List<SanitaryItem>> obtenerOrdenados(
            @PathVariable Long sanitaryId) {
        return ResponseEntity.ok(
                itemService.obtenerPorInspeccionOrdenados(sanitaryId));
    }

    // GET /api/v1/sanitary/items/ultimos
    // Devuelve los últimos 10 items registrados en el sistema
    @GetMapping("/ultimos")
    public ResponseEntity<List<SanitaryItem>> obtenerUltimos() {
        return ResponseEntity.ok(itemService.obtenerUltimosItems());
    }

    // GET /api/v1/sanitary/items/estadisticas/rechazados/1
    // Cuenta cuántos items fueron RECHAZADOS en una inspección específica
    // Útil para saber cuántos objetos problemáticos tuvo una inspección
    @GetMapping("/estadisticas/rechazados/{sanitaryId}")
    public ResponseEntity<Map<String, Long>> contarRechazados(
            @PathVariable Long sanitaryId) {
        long total = itemService.contarRechazadosPorInspeccion(sanitaryId);
        return ResponseEntity.ok(Map.of("total", total));
    }
}