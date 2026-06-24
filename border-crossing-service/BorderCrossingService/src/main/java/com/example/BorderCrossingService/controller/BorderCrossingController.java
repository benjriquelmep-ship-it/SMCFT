package com.example.BorderCrossingService.controller;

import com.example.BorderCrossingService.dto.BorderCrossingDTO;
import com.example.BorderCrossingService.model.BorderCrossing;
import com.example.BorderCrossingService.service.BorderCrossingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/v1/border-crossings")
@RequiredArgsConstructor
@Tag(name = "1. Gestión de Cruces Fronterizos", description = "Endpoints operativos destinados al control, apertura, dictamen de andén y auditoría de tránsitos de salida del país")
@SecurityRequirement(name = "bearerAuth")
public class BorderCrossingController {

    private final BorderCrossingService crossingService;

    @Operation(summary = "Listar todos los cruces fronterizos", description = "Recupera la nómina histórica consolidada de todas las solicitudes y registros de salida vehicular gestionados en frontera.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Bitácora de cruces fronterizos recuperada con éxito"),
            @ApiResponse(responseCode = "401", description = "No autenticado - Falta token Bearer JWT válido"),
            @ApiResponse(responseCode = "403", description = "Sin permisos - Privilegios de control perimetral insuficientes")
    })
    @GetMapping
    public ResponseEntity<List<BorderCrossing>> obtenerTodos() {
        return ResponseEntity.ok(crossingService.obtenerTodos());
    }

    // GET /api/v1/border-crossings/1
    @Operation(summary = "Obtener cruce fronterizo por ID", description = "Busca la información detallada, patente vinculada y el estado situacional de una orden de salida específica mediante su ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Registro de cruce fronterizo localizado correctamente"),
            @ApiResponse(responseCode = "404", description = "El ID de cruce solicitado no se encuentra registrado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<BorderCrossing> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(crossingService.obtenerPorId(id));
    }

    // POST /api/v1/border-crossings
    @Operation(summary = "Registrar nueva solicitud de cruce", description = "Valida el estado registral del vehículo en el exterior y da de alta una nueva orden de salida en estado PENDIENTE.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Solicitud de cruce inicializada de forma conforme en el andén"),
            @ApiResponse(responseCode = "400", description = "El vehículo no se encuentra en territorio nacional o el payload DTO es inválido")
    })
    @PostMapping
    public ResponseEntity<BorderCrossing> registrar(@Valid @RequestBody BorderCrossingDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(crossingService.registrar(dto));
    }

    // PATCH /api/v1/border-crossings/1/autorizar
    @Operation(summary = "Autorizar e instruir salida del país", description = "Firma digitalmente el trámite cambiando el estado a 'AUTORIZADO', visando de forma conforme la salida de la unidad vehicular.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tránsito de salida visado y autorizado en caseta correctamente"),
            @ApiResponse(responseCode = "400", description = "El RUT del fiscalizador es obligatorio o la orden no se encuentra PENDIENTE")
    })
    @PatchMapping("/{id}/autorizar")
    public ResponseEntity<BorderCrossing> autorizar(
            @PathVariable Long id,
            @RequestParam String rutFiscalizador,
            @RequestParam(required = false) String observaciones) {
        return ResponseEntity.ok(
                crossingService.autorizar(id, rutFiscalizador, observaciones));
    }

    // PATCH /api/v1/border-crossings/1/rechazar
    @Operation(summary = "Rechazar solicitud de cruce", description = "Deniega el paso fronterizo de la unidad vehicular cambiando su estado operativo a 'RECHAZADO' tras el aforo físico desfavorables.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Trámite de salida rechazado exitosamente en los registros")
    })
    @PatchMapping("/{id}/rechazar")
    public ResponseEntity<BorderCrossing> rechazar(
            @PathVariable Long id,
            @RequestParam String rutFiscalizador,
            @RequestParam(required = false) String observaciones) {
        return ResponseEntity.ok(
                crossingService.rechazar(id, rutFiscalizador, observaciones));
    }

    // DELETE /api/v1/border-crossings/1
    @Operation(summary = "Eliminar cruce de la persistencia", description = "Remueve físicamente la hilera de persistencia de la orden de salida de la base de datos central.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Registro purgado correctamente del maestro central. Sin contenido."),
            @ApiResponse(responseCode = "401", description = "No autenticado")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        crossingService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    // GET /api/v1/border-crossings/patente/ABC123
    @Operation(summary = "Buscar cruces por Placa Patente", description = "Recupera la trazabilidad histórica de tránsitos de salida del país asociados a una patente vehicular.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Historial de salidas del vehículo localizado con éxito")
    })
    @GetMapping("/patente/{patente}")
    public ResponseEntity<List<BorderCrossing>> obtenerPorPatente(@PathVariable String patente) {
        return ResponseEntity.ok(crossingService.obtenerPorPatente(patente));
    }

    // GET /api/v1/border-crossings/conductor/12345678-9
    @Operation(summary = "Buscar cruces por RUN del Conductor", description = "Entrega la nómina de tránsitos migratorios internacionales tramitados bajo la identidad del conductor.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Historial por cédula de conductor extraído de forma conforme")
    })
    @GetMapping("/conductor/{rut}")
    public ResponseEntity<List<BorderCrossing>> obtenerPorConductor(@PathVariable String rut) {
        return ResponseEntity.ok(crossingService.obtenerPorConductor(rut));
    }

    // GET /api/v1/border-crossings/estado/PENDIENTE
    @Operation(summary = "Filtrar cruces por Estado operativo", description = "Extrae las órdenes de salida agrupándolas por su situación actual de tramitación (PENDIENTE, AUTORIZADO, RECHAZADO).")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Registros agrupados por situación aduanera devueltos")
    })
    @GetMapping("/estado/{estado}")
    public ResponseEntity<List<BorderCrossing>> obtenerPorEstado(@PathVariable String estado) {
        return ResponseEntity.ok(crossingService.obtenerPorEstado(estado));
    }

    // GET /api/v1/border-crossings/paso/Los Libertadores
    @Operation(summary = "Buscar cruces por Complejo Fronterizo", description = "Filtra la bitácora exponiendo la actividad y revisiones ejecutadas en una aduana específica del perímetro.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Historial de tránsitos del paso fronterizo recuperado")
    })
    @GetMapping("/paso/{pasoFronterizo}")
    public ResponseEntity<List<BorderCrossing>> obtenerPorPaso(@PathVariable String pasoFronterizo) {
        return ResponseEntity.ok(crossingService.obtenerPorPasoFronterizo(pasoFronterizo));
    }

    // GET /api/v1/border-crossings/fiscalizador/12345678-9
    @Operation(summary = "Buscar cruces por RUN de Fiscalizador", description = "Permite auditar de forma cuantitativa los tránsitos y dictámenes firmados por un funcionario aduanero específico.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Registros visados por el funcionario obtenidos con éxito")
    })
    @GetMapping("/fiscalizador/{rut}")
    public ResponseEntity<List<BorderCrossing>> obtenerPorFiscalizador(@PathVariable String rut) {
        return ResponseEntity.ok(crossingService.obtenerPorFiscalizador(rut));
    }

    // GET /api/v1/border-crossings/patente/ABC123/estado/AUTORIZADO
    @Operation(summary = "Filtrar por Coincidencia Dual de Patente y Estado", description = "Ejecuta una búsqueda combinada acotando las salidas autorizadas o rechazadas de un vehículo automotor.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Coincidencia analítica de registros devuelta")
    })
    @GetMapping("/patente/{patente}/estado/{estado}")
    public ResponseEntity<List<BorderCrossing>> obtenerPorPatenteYEstado(
            @PathVariable String patente,
            @PathVariable String estado) {
        return ResponseEntity.ok(crossingService.obtenerPorPatenteYEstado(patente, estado));
    }

    // GET /api/v1/border-crossings/fechas?desde=...&hasta=...
    @Operation(summary = "Filtrar por Rango Cronológico de Fechas", description = "Extrae los tránsitos de salida del territorio nacional concretados dentro de una ventana temporal ISO delimitada.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ventana temporal de cruces localizada de forma conforme")
    })
    @GetMapping("/fechas")
    public ResponseEntity<List<BorderCrossing>> obtenerPorFechas(
            @RequestParam String desde,
            @RequestParam String hasta) {
        LocalDateTime fechaDesde = LocalDateTime.parse(desde);
        LocalDateTime fechaHasta = LocalDateTime.parse(hasta);
        return ResponseEntity.ok(crossingService.obtenerPorRangoFechas(fechaDesde, fechaHasta));
    }

    // GET /api/v1/border-crossings/buscar?pais=argentina
    @Operation(summary = "Buscar por Coincidencia Parcial de País Destino", description = "Buscador de texto predictivo parcial (LIKE) aplicado sobre la columna de la nación destino del viaje.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Resultados parciales de búsqueda recuperados con éxito")
    })
    @GetMapping("/buscar")
    public ResponseEntity<List<BorderCrossing>> buscarPorPais(@RequestParam String pais) {
        return ResponseEntity.ok(crossingService.buscarPorPaisDestino(pais));
    }

    // GET /api/v1/border-crossings/patente/ABC123/ordenados
    @Operation(summary = "Obtener Historial Cronológico Inverso por Patente", description = "Retorna el historial correlativo de cruces de una unidad ordenados de forma descendente (del evento más nuevo al antiguo).")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Historial ordenado jerárquicamente recuperado")
    })
    @GetMapping("/patente/{patente}/ordenados")
    public ResponseEntity<List<BorderCrossing>> obtenerPorPatenteOrdenados(@PathVariable String patente) {
        return ResponseEntity.ok(crossingService.obtenerPorPatenteOrdenados(patente));
    }

    // GET /api/v1/border-crossings/ultimos
    @Operation(summary = "Listar últimas solicitudes globales de cruces", description = "Mapea un cuadro de mando rápido en la interfaz exponiendo los últimos 10 movimientos de salida registrados.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Dashboard de las últimas 10 salidas obtenido con éxito")
    })
    @GetMapping("/ultimos")
    public ResponseEntity<List<BorderCrossing>> obtenerUltimosCruces() {
        return ResponseEntity.ok(crossingService.obtenerUltimosCruces());
    }

    // GET /api/v1/border-crossings/estadisticas/estado/PENDIENTE
    @Operation(summary = "Métrica Totalizadora cuantitativa por Estado", description = "Entrega la sumatoria métrica consolidada de hileras clasificadas bajo la situación operativa dada.")
    @GetMapping("/estadisticas/estado/{estado}")
    public ResponseEntity<Map<String, Long>> contarPorEstado(@PathVariable String estado) {
        long total = crossingService.contarPorEstado(estado);
        return ResponseEntity.ok(Map.of("total", total));
    }

    // GET /api/v1/border-crossings/estadisticas/paso/Los Libertadores
    @Operation(summary = "Métrica Totalizadora de Flujo por Complejo Fronterizo", description = "Indica estadísticamente qué pasos de control registran mayor volumetría y densidad de tránsitos de salida.")
    @GetMapping("/estadisticas/paso/{pasoFronterizo}")
    public ResponseEntity<Map<String, Long>> contarPorPaso(@PathVariable String pasoFronterizo) {
        long total = crossingService.contarPorPasoFronterizo(pasoFronterizo);
        return ResponseEntity.ok(Map.of("total", total));
    }
}