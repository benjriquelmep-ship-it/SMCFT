// Recibe las peticiones HTTP para los items de cruces fronterizos
// Llama al Service y retorna ResponseEntity con JSON
// Nunca tiene lógica de negocio directamente
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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@RestController
@RequestMapping("/api/v1/border-crossings/items")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class BorderCrossingItemController {

    // BorderCrossingItemService contiene toda la lógica de negocio
    // El Controller solo recibe y responde — nunca tiene lógica propia
    private final BorderCrossingItemService itemService;

    // GET /api/v1/border-crossings/items : Devuelve todos los items declarados en todos los cruces del sistema
    @Operation(summary = "Obtener Todos", description = "GET /api/v1/border-crossings/items : Devuelve todos los items declarados en todos los cruces del sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operación exitosa"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "Sin permisos")
    })
    @GetMapping
    public ResponseEntity<List<BorderCrossingItem>> obtenerTodos() {
        return ResponseEntity.ok(itemService.obtenerTodos());
    }

    // GET /api/v1/border-crossings/items/1 : Devuelve un item específico por su id
    @Operation(summary = "Obtener Por Id", description = "GET /api/v1/border-crossings/items/1 : Devuelve un item específico por su id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operación exitosa"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "Sin permisos")
    })
    @GetMapping("/{id}")
    public ResponseEntity<BorderCrossingItem> obtenerPorId(
            @PathVariable Long id) {
        return ResponseEntity.ok(itemService.obtenerPorId(id));
    }

    // POST /api/v1/border-crossings/items : Agrega un nuevo item a un cruce existente
    @Operation(summary = "Agregar", description = "POST /api/v1/border-crossings/items : Agrega un nuevo item a un cruce existente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Operación exitosa"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "Sin permisos")
    })
    @PostMapping
    public ResponseEntity<BorderCrossingItem> agregar(
            @Valid @RequestBody BorderCrossingItemDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(itemService.agregar(dto));
    }

    // PATCH /api/v1/border-crossings/items/1/aprobar : Aprueba un item específico — cambia su estado a APROBADO
    // Solo funciona si el item está PENDIENTE
    @Operation(summary = "Aprobar", description = "Solo funciona si el item está PENDIENTE")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operación exitosa"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "Sin permisos")
    })
    @PatchMapping("/{id}/aprobar")
    public ResponseEntity<BorderCrossingItem> aprobar(
            @PathVariable Long id) {
        return ResponseEntity.ok(itemService.aprobar(id));
    }

    // PATCH /api/v1/border-crossings/items/1/rechazar : Rechaza un item específico — cambia su estado a RECHAZADO
    // Solo funciona si el item está PENDIENTE
    @Operation(summary = "Rechazar", description = "Solo funciona si el item está PENDIENTE")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operación exitosa"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "Sin permisos")
    })
    @PatchMapping("/{id}/rechazar")
    public ResponseEntity<BorderCrossingItem> rechazar(
            @PathVariable Long id) {
        return ResponseEntity.ok(itemService.rechazar(id));
    }

    // DELETE /api/v1/border-crossings/items/1 : Elimina un item por su id
    @Operation(summary = "Eliminar", description = "DELETE /api/v1/border-crossings/items/1 : Elimina un item por su id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Operación exitosa"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "Sin permisos")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        itemService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    // GET /api/v1/border-crossings/items/cruce/1 : Devuelve todos los items que pertenecen a un cruce específico
    @Operation(summary = "Obtener Por Cruce", description = "GET /api/v1/border-crossings/items/cruce/1 : Devuelve todos los items que pertenecen a un cruce específico")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operación exitosa"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "Sin permisos")
    })
    @GetMapping("/cruce/{borderCrossingId}")
    public ResponseEntity<List<BorderCrossingItem>> obtenerPorCruce(
            @PathVariable Long borderCrossingId) {
        return ResponseEntity.ok(
                itemService.obtenerPorCruce(borderCrossingId));
    }

    // GET /api/v1/border-crossings/items/cruce/1/aprobados : Devuelve solo los items APROBADOS de un cruce específico
    // Útil para ver qué objetos pasaron la revisión en ese cruce
    @Operation(summary = "Obtener Aprobados", description = "Útil para ver qué objetos pasaron la revisión en ese cruce")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operación exitosa"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "Sin permisos")
    })
    @GetMapping("/cruce/{borderCrossingId}/aprobados")
    public ResponseEntity<List<BorderCrossingItem>> obtenerAprobados(
            @PathVariable Long borderCrossingId) {
        return ResponseEntity.ok(
                itemService.obtenerAprobadosPorCruce(borderCrossingId));
    }

    // GET /api/v1/border-crossings/items/cruce/1/no-aprobados : Devuelve los items NO APROBADOS de un cruce específico
    // Incluye items PENDIENTES y RECHAZADOS
    // Útil para ver qué objetos tienen problemas en ese cruce
    @Operation(summary = "Obtener No Aprobados", description = "Útil para ver qué objetos tienen problemas en ese cruce")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operación exitosa"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "Sin permisos")
    })
    @GetMapping("/cruce/{borderCrossingId}/no-aprobados")
    public ResponseEntity<List<BorderCrossingItem>> obtenerNoAprobados(
            @PathVariable Long borderCrossingId) {
        return ResponseEntity.ok(
                itemService.obtenerNoAprobadosPorCruce(borderCrossingId));
    }

    // GET /api/v1/border-crossings/items/categoria/1 : Devuelve todos los items de una categoría específica
    @Operation(summary = "Obtener Por Categoria", description = "GET /api/v1/border-crossings/items/categoria/1 : Devuelve todos los items de una categoría específica")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operación exitosa"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "Sin permisos")
    })
    @GetMapping("/categoria/{categoriaId}")
    public ResponseEntity<List<BorderCrossingItem>> obtenerPorCategoria(
            @PathVariable Long categoriaId) {
        return ResponseEntity.ok(
                itemService.obtenerPorCategoria(categoriaId));
    }

    // GET /api/v1/border-crossings/items/buscar?descripcion=laptop : Busca items cuya descripción contenga el texto buscado
    // No necesitas escribir el nombre exacto — busca si lo contiene
    @Operation(summary = "Buscar Por Descripcion", description = "No necesitas escribir el nombre exacto — busca si lo contiene")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operación exitosa"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "Sin permisos")
    })
    @GetMapping("/buscar")
    public ResponseEntity<List<BorderCrossingItem>> buscarPorDescripcion(
            @RequestParam String descripcion) {
        return ResponseEntity.ok(
                itemService.buscarPorDescripcion(descripcion));
    }

    // GET /api/v1/border-crossings/items/cruce/1/ordenados : Devuelve los items de un cruce ordenados por valor
    // del más caro al más barato
    @Operation(summary = "Obtener Ordenados", description = "del más caro al más barato")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operación exitosa"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "Sin permisos")
    })
    @GetMapping("/cruce/{borderCrossingId}/ordenados")
    public ResponseEntity<List<BorderCrossingItem>> obtenerOrdenados(
            @PathVariable Long borderCrossingId) {
        return ResponseEntity.ok(
                itemService.obtenerPorCruceOrdenadosPorValor(
                        borderCrossingId));
    }

    // GET /api/v1/border-crossings/items/ultimos : Devuelve los últimos 10 items registrados en el sistema
    @Operation(summary = "Obtener Ultimos", description = "GET /api/v1/border-crossings/items/ultimos : Devuelve los últimos 10 items registrados en el sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operación exitosa"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "Sin permisos")
    })
    @GetMapping("/ultimos")
    public ResponseEntity<List<BorderCrossingItem>> obtenerUltimos() {
        return ResponseEntity.ok(itemService.obtenerUltimosItems());
    }
}