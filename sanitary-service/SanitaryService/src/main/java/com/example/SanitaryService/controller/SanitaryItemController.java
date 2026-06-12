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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@RestController
@RequestMapping("/api/v1/sanitary/items")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class SanitaryItemController {

    // SanitaryItemService contiene toda la lógica de negocio
    // El Controller solo recibe y responde — nunca tiene lógica propia
    private final SanitaryItemService itemService;

    // GET /api/v1/sanitary/items
    // Devuelve todos los items de todas las inspecciones del sistema
    @Operation(summary = "Obtener Todos", description = "Devuelve todos los items de todas las inspecciones del sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operación exitosa"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "Sin permisos")
    })
    @GetMapping
    public ResponseEntity<List<SanitaryItem>> obtenerTodos() {
        return ResponseEntity.ok(itemService.obtenerTodos());
    }

    // GET /api/v1/sanitary/items/1
    // Devuelve un item específico por su id
    @Operation(summary = "Obtener Por Id", description = "Devuelve un item específico por su id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operación exitosa"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "Sin permisos")
    })
    @GetMapping("/{id}")
    public ResponseEntity<SanitaryItem> obtenerPorId(
            @PathVariable Long id) {
        return ResponseEntity.ok(itemService.obtenerPorId(id));
    }

    // POST /api/v1/sanitary/items
    // Agrega un nuevo item a una inspección existente
    @Operation(summary = "Agregar", description = "Agrega un nuevo item a una inspección existente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Operación exitosa"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "Sin permisos")
    })
    @PostMapping
    public ResponseEntity<SanitaryItem> agregar(
            @Valid @RequestBody SanitaryItemDTO dto) {
        // HTTP 201 = se creó un nuevo recurso exitosamente
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(itemService.agregar(dto));
    }

    // PUT /api/v1/sanitary/items/1
    // Actualiza TODOS los campos de un item
    @Operation(summary = "Actualizar", description = "Actualiza TODOS los campos de un item")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operación exitosa"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "Sin permisos")
    })
    @PutMapping("/{id}")
    public ResponseEntity<SanitaryItem> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody SanitaryItemDTO dto) {
        return ResponseEntity.ok(itemService.actualizar(id, dto));
    }

    // DELETE /api/v1/sanitary/items/1
    // Elimina un item por su id
    // ResponseEntity<Void> = respuesta sin body → HTTP 204
    @Operation(summary = "Eliminar", description = "ResponseEntity<Void> = respuesta sin body → HTTP 204")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Operación exitosa"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "Sin permisos")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        itemService.eliminar(id);
        // HTTP 204 = operación exitosa sin contenido en la respuesta
        return ResponseEntity.noContent().build();
    }

    // GET /api/v1/sanitary/items/inspeccion/1
    // Devuelve todos los items que pertenecen a una inspección específica
    @Operation(summary = "Obtener Por Inspeccion", description = "Devuelve todos los items que pertenecen a una inspección específica")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operación exitosa"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "Sin permisos")
    })
    @GetMapping("/inspeccion/{sanitaryId}")
    public ResponseEntity<List<SanitaryItem>> obtenerPorInspeccion(
            @PathVariable Long sanitaryId) {
        return ResponseEntity.ok(
                itemService.obtenerPorInspeccion(sanitaryId));
    }

    // GET /api/v1/sanitary/items/inspeccion/1/resultado/APROBADO
    // Combina dos filtros: inspección + resultado
    @Operation(summary = "Obtener Por Resultado", description = "Combina dos filtros: inspección + resultado")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operación exitosa"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "Sin permisos")
    })
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
    @Operation(summary = "Obtener Todos Por Resultado", description = "Sin importar a qué inspección pertenecen")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operación exitosa"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "Sin permisos")
    })
    @GetMapping("/resultado/{resultado}")
    public ResponseEntity<List<SanitaryItem>> obtenerTodosPorResultado(
            @PathVariable String resultado) {
        return ResponseEntity.ok(
                itemService.obtenerPorResultado(resultado));
    }

    // GET /api/v1/sanitary/items/buscar?descripcion=animal
    // Busca items cuya descripción contenga el texto buscado
    // No necesitas escribir la descripción exacta — busca si lo contiene
    @Operation(summary = "Buscar Por Descripcion", description = "No necesitas escribir la descripción exacta — busca si lo contiene")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operación exitosa"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "Sin permisos")
    })
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
    @Operation(summary = "Obtener Ordenados", description = "Útil para mostrar los items en orden en el frontend")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operación exitosa"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "Sin permisos")
    })
    @GetMapping("/inspeccion/{sanitaryId}/ordenados")
    public ResponseEntity<List<SanitaryItem>> obtenerOrdenados(
            @PathVariable Long sanitaryId) {
        return ResponseEntity.ok(
                itemService.obtenerPorInspeccionOrdenados(sanitaryId));
    }

    // GET /api/v1/sanitary/items/ultimos
    // Devuelve los últimos 10 items registrados en el sistema
    @Operation(summary = "Obtener Ultimos", description = "Devuelve los últimos 10 items registrados en el sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operación exitosa"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "Sin permisos")
    })
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