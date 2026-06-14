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
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/v1/sanitary/items")
@RequiredArgsConstructor
@Tag(name = "Detalles de Inspección (Ítems)", description = "Endpoints para el desglose, análisis y dictamen por lotes de mercancías, cargas y productos orgánicos individuales")
@SecurityRequirement(name = "bearerAuth")
public class SanitaryItemController {

    private final SanitaryItemService itemService;

    // GET /api/v1/sanitary/items
    @Operation(summary = "Listar todas las líneas de mercancía fiscalizadas", description = "Muestra un listado global de todos los ítems e insumos individuales cargados en el sistema sanitario.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Historial de líneas obtenido de forma correcta"),
            @ApiResponse(responseCode = "401", description = "No autenticado - Token JWT ausente"),
            @ApiResponse(responseCode = "403", description = "Sin privilegios requeridos")
    })
    @GetMapping
    public ResponseEntity<List<SanitaryItem>> obtenerTodos() {
        return ResponseEntity.ok(itemService.obtenerTodos());
    }

    // GET /api/v1/sanitary/items/1
    @Operation(summary = "Obtener línea de mercancía por ID", description = "Busca el dictamen, descripción y folios de un lote/ítem individual a través de su clave primaria.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Línea de lote localizada con éxito"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "404", description = "El ID del lote solicitado no existe en las bases sanitarias")
    })
    @GetMapping("/{id}")
    public ResponseEntity<SanitaryItem> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(itemService.obtenerPorId(id));
    }

    // POST /api/v1/sanitary/items
    @Operation(summary = "Anexar lote de carga a inspección", description = "Agrega y valida una nueva línea de producto revisada vinculándola directamente al ID de una orden de inspección activa.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Línea de cargamento indexada con éxito"),
            @ApiResponse(responseCode = "400", description = "Payload DTO con campos vacíos o inconsistencias de formato")
    })
    @PostMapping
    public ResponseEntity<SanitaryItem> agregar(@Valid @RequestBody SanitaryItemDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(itemService.agregar(dto));
    }

    // PUT /api/v1/sanitary/items/1
    @Operation(summary = "Actualizar línea de lote por completo", description = "Sobreescribe las propiedades técnicas, observaciones y dictamen de un lote analizado a partir de su ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Línea de mercancía actualizada de forma conforme"),
            @ApiResponse(responseCode = "400", description = "Datos de actualización inválidos"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "404", description = "El lote a modificar no se encuentra registrado")
    })
    @PutMapping("/{id}")
    public ResponseEntity<SanitaryItem> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody SanitaryItemDTO dto) {
        return ResponseEntity.ok(itemService.actualizar(id, dto));
    }

    // DELETE /api/v1/sanitary/items/1
    @Operation(summary = "Eliminar línea de lote físicamente", description = "Remueve permanentemente el registro del lote o producto analizado de la base de datos.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Línea de mercancía purgada con éxito. Sin contenido."),
            @ApiResponse(responseCode = "401", description = "No autenticado")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        itemService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    // GET /api/v1/sanitary/items/inspeccion/1
    @Operation(summary = "Listar desgloses por Orden de Inspección de cabecera", description = "Lista de forma secuencial todas las cargas y productos revisados contenidos dentro de una misma inspección por su ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Desglose de productos de la orden recuperado exitosamente"),
            @ApiResponse(responseCode = "401", description = "No autenticado")
    })
    @GetMapping("/inspeccion/{sanitaryId}")
    public ResponseEntity<List<SanitaryItem>> obtenerPorInspeccion(@PathVariable Long sanitaryId) {
        return ResponseEntity.ok(itemService.obtenerPorInspeccion(sanitaryId));
    }

    // GET /api/v1/sanitary/items/inspeccion/1/resultado/APROBADO
    @Operation(summary = "Listar productos de una inspección por Resultado", description = "Filtra analíticamente los ítems de una orden basándose en su dictamen técnico (ej. aislar solo los RECHAZADOS de la orden ID 1).")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Productos filtrados de la orden obtenidos correctamente"),
            @ApiResponse(responseCode = "401", description = "No autenticado")
    })
    @GetMapping("/inspeccion/{sanitaryId}/resultado/{resultado}")
    public ResponseEntity<List<SanitaryItem>> obtenerPorResultado(
            @PathVariable Long sanitaryId,
            @PathVariable String resultado) {
        return ResponseEntity.ok(itemService.obtenerPorInspeccionYResultado(sanitaryId, resultado));
    }

    // GET /api/v1/sanitary/items/resultado/RECHAZADO
    @Operation(summary = "Listar todos los productos rechazados globalmente", description = "Filtro operativo radical que devuelve la colección de todas las mercancías RECHAZADAS en frontera, sin importar a qué camión o inspección pertenecen.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Historial global de mermas y rechazos devuelto con éxito"),
            @ApiResponse(responseCode = "401", description = "No autenticado")
    })
    @GetMapping("/resultado/{resultado}")
    public ResponseEntity<List<SanitaryItem>> obtenerTodosPorResultado(@PathVariable String resultado) {
        return ResponseEntity.ok(itemService.obtenerPorResultado(resultado));
    }

    // GET /api/v1/sanitary/items/buscar?descripcion=animal
    @Operation(summary = "Buscar productos por texto descriptivo", description = "Realiza una búsqueda de concordancia de texto parcial (LIKE) sobre el nombre o descripción de las mercancías.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Coincidencias de búsqueda devueltas correctamente"),
            @ApiResponse(responseCode = "401", description = "No autenticado")
    })
    @GetMapping("/buscar")
    public ResponseEntity<List<SanitaryItem>> buscarPorDescripcion(@RequestParam String descripcion) {
        return ResponseEntity.ok(itemService.buscarPorDescripcion(descripcion));
    }

    // GET /api/v1/sanitary/items/inspeccion/1/ordenados
    @Operation(summary = "Listar productos de una orden ordenados alfabéticamente", description = "Obtiene los desgloses de una inspección ordenados alfabéticamente (A-Z) por descripción, ideal para despliegues de Frontend.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Desglose indexado en orden alfabético devuelto exitosamente"),
            @ApiResponse(responseCode = "401", description = "No autenticado")
    })
    @GetMapping("/inspeccion/{sanitaryId}/ordenados")
    public ResponseEntity<List<SanitaryItem>> obtenerOrdenados(@PathVariable Long sanitaryId) {
        return ResponseEntity.ok(itemService.obtenerPorInspeccionOrdenados(sanitaryId));
    }

    // GET /api/v1/sanitary/items/ultimos
    @Operation(summary = "Listar últimos productos ingresados al sistema", description = "Muestra de forma rápida una lista con las últimas 10 líneas de mercancías catalogadas en los andenes fronterizos.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Historial operativo inmediato leído correctamente"),
            @ApiResponse(responseCode = "401", description = "No autenticado")
    })
    @GetMapping("/ultimos")
    public ResponseEntity<List<SanitaryItem>> obtenerUltimos() {
        return ResponseEntity.ok(itemService.obtenerUltimosItems());
    }

    // GET /api/v1/sanitary/items/estadisticas/rechazados/1
    @Operation(summary = "Contar mermas/rechazos de una Inspección", description = "Endpoint de reportería que devuelve la sumatoria volumétrica exacta de cuántos lotes de producto fueron RECHAZADOS dentro de una misma orden.")
    @GetMapping("/estadisticas/rechazados/{sanitaryId}")
    public ResponseEntity<Map<String, Long>> contarRechazados(@PathVariable Long sanitaryId) {
        long total = itemService.contarRechazadosPorInspeccion(sanitaryId);
        return ResponseEntity.ok(Map.of("total", total));
    }
}