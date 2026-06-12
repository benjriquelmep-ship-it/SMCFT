// Recibe las peticiones HTTP para los items de ingresos al país
// Llama al Service y retorna ResponseEntity con JSON
// Nunca tiene lógica de negocio directamente
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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@RestController
@RequestMapping("/api/v1/entries/items")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class EntryItemController {

    // EntryItemService contiene toda la lógica de negocio
    // El Controller solo recibe y responde — nunca tiene lógica propia
    private final EntryItemService itemService;

    // GET /api/v1/entries/items
    // Devuelve todos los items declarados en todos los ingresos del sistema
    @Operation(summary = "Obtener Todos", description = "Devuelve todos los items declarados en todos los ingresos del sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operación exitosa"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "Sin permisos")
    })
    @GetMapping
    public ResponseEntity<List<EntryItem>> obtenerTodos() {
        return ResponseEntity.ok(itemService.obtenerTodos());
    }

    // GET /api/v1/entries/items/1
    // Devuelve un item específico por su id
    @Operation(summary = "Obtener Por Id", description = "Devuelve un item específico por su id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operación exitosa"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "Sin permisos")
    })
    @GetMapping("/{id}")
    public ResponseEntity<EntryItem> obtenerPorId(
            @PathVariable Long id) {
        return ResponseEntity.ok(itemService.obtenerPorId(id));
    }

    // POST /api/v1/entries/items
    // Agrega un nuevo item a un ingreso existente
    @Operation(summary = "Agregar", description = "Agrega un nuevo item a un ingreso existente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Operación exitosa"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "Sin permisos")
    })
    @PostMapping
    public ResponseEntity<EntryItem> agregar(
            @Valid @RequestBody EntryItemDTO dto) {
        // HTTP 201 = se creó un nuevo recurso exitosamente
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(itemService.agregar(dto));
    }

    // PATCH /api/v1/entries/items/1/aprobar
    // Aprueba un item específico — el fiscalizador lo acepta
    // Solo funciona si el ingreso al que pertenece está PENDIENTE
    @Operation(summary = "Aprobar", description = "Solo funciona si el ingreso al que pertenece está PENDIENTE")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operación exitosa"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "Sin permisos")
    })
    @PatchMapping("/{id}/aprobar")
    public ResponseEntity<EntryItem> aprobar(@PathVariable Long id) {
        return ResponseEntity.ok(itemService.aprobar(id));
    }

    // PATCH /api/v1/entries/items/1/rechazar
    // Rechaza un item específico — el fiscalizador no lo acepta
    // Solo funciona si el ingreso al que pertenece está PENDIENTE
    @Operation(summary = "Rechazar", description = "Solo funciona si el ingreso al que pertenece está PENDIENTE")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operación exitosa"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "Sin permisos")
    })
    @PatchMapping("/{id}/rechazar")
    public ResponseEntity<EntryItem> rechazar(@PathVariable Long id) {
        return ResponseEntity.ok(itemService.rechazar(id));
    }

    // DELETE /api/v1/entries/items/1
    // Elimina un item por su id
    @Operation(summary = "Eliminar", description = "Elimina un item por su id")
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

    // GET /api/v1/entries/items/ingreso/1
    // Devuelve todos los items que pertenecen a un ingreso específico
    @Operation(summary = "Obtener Por Ingreso", description = "Devuelve todos los items que pertenecen a un ingreso específico")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operación exitosa"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "Sin permisos")
    })
    @GetMapping("/ingreso/{entryId}")
    public ResponseEntity<List<EntryItem>> obtenerPorIngreso(
            @PathVariable Long entryId) {
        return ResponseEntity.ok(itemService.obtenerPorIngreso(entryId));
    }

    // GET /api/v1/entries/items/ingreso/1/aprobados
    // Devuelve solo los items APROBADOS de un ingreso específico
    // Útil para ver qué objetos pasaron la revisión en ese ingreso
    @Operation(summary = "Obtener Aprobados", description = "Útil para ver qué objetos pasaron la revisión en ese ingreso")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operación exitosa"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "Sin permisos")
    })
    @GetMapping("/ingreso/{entryId}/aprobados")
    public ResponseEntity<List<EntryItem>> obtenerAprobados(
            @PathVariable Long entryId) {
        return ResponseEntity.ok(
                itemService.obtenerAprobadosPorIngreso(entryId));
    }

    // GET /api/v1/entries/items/ingreso/1/no-aprobados
    // Devuelve los items NO APROBADOS de un ingreso específico
    // Útil para ver qué objetos tienen problemas en ese ingreso
    @Operation(summary = "Obtener No Aprobados", description = "Útil para ver qué objetos tienen problemas en ese ingreso")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operación exitosa"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "Sin permisos")
    })
    @GetMapping("/ingreso/{entryId}/no-aprobados")
    public ResponseEntity<List<EntryItem>> obtenerNoAprobados(
            @PathVariable Long entryId) {
        return ResponseEntity.ok(
                itemService.obtenerNoAprobadosPorIngreso(entryId));
    }

    // GET /api/v1/entries/items/buscar?descripcion=laptop
    // Busca items cuya descripción contenga el texto buscado
    // No necesitas escribir el nombre exacto — busca si lo contiene
    @Operation(summary = "Buscar Por Descripcion", description = "No necesitas escribir el nombre exacto — busca si lo contiene")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operación exitosa"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "Sin permisos")
    })
    @GetMapping("/buscar")
    public ResponseEntity<List<EntryItem>> buscarPorDescripcion(
            @RequestParam String descripcion) {
        return ResponseEntity.ok(
                itemService.buscarPorDescripcion(descripcion));
    }

    // GET /api/v1/entries/items/ingreso/1/ordenados
    // Devuelve los items de un ingreso ordenados por valor
    // del más caro al más barato
    // Útil para identificar los objetos de mayor valor en un ingreso
    @Operation(summary = "Obtener Ordenados", description = "Útil para identificar los objetos de mayor valor en un ingreso")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operación exitosa"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "Sin permisos")
    })
    @GetMapping("/ingreso/{entryId}/ordenados")
    public ResponseEntity<List<EntryItem>> obtenerOrdenados(
            @PathVariable Long entryId) {
        return ResponseEntity.ok(
                itemService.obtenerPorIngresoOrdenadosPorValor(entryId));
    }

    // GET /api/v1/entries/items/ultimos
    // Devuelve los últimos 10 items registrados en el sistema
    // Útil para monitorear en tiempo real los últimos objetos declarados
    @Operation(summary = "Obtener Ultimos", description = "Útil para monitorear en tiempo real los últimos objetos declarados")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operación exitosa"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "Sin permisos")
    })
    @GetMapping("/ultimos")
    public ResponseEntity<List<EntryItem>> obtenerUltimos() {
        return ResponseEntity.ok(itemService.obtenerUltimosItems());
    }
}