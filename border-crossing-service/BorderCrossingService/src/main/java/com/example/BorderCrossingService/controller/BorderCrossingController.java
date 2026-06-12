// Recibe las peticiones HTTP del Border Crossing Service
// Llama al Service y retorna ResponseEntity con JSON
// Nunca tiene lógica de negocio directamente
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

@RestController
@RequestMapping("/api/v1/border-crossings")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class BorderCrossingController {

    // BorderCrossingService contiene toda la lógica de negocio
    // El Controller solo recibe y responde — nunca tiene lógica propia
    private final BorderCrossingService crossingService;

    // GET /api/v1/border-crossings : Devuelve todos los cruces fronterizos del sistema
    @Operation(summary = "Obtener Todos", description = "GET /api/v1/border-crossings : Devuelve todos los cruces fronterizos del sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operación exitosa"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "Sin permisos")
    })
    @GetMapping
    public ResponseEntity<List<BorderCrossing>> obtenerTodos() {
        return ResponseEntity.ok(crossingService.obtenerTodos());
    }

    // GET /api/v1/border-crossings/1 : Devuelve un cruce específico por su id
    @Operation(summary = "Obtener Por Id", description = "GET /api/v1/border-crossings/1 : Devuelve un cruce específico por su id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operación exitosa"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "Sin permisos")
    })
    @GetMapping("/{id}")
    public ResponseEntity<BorderCrossing> obtenerPorId(
            @PathVariable Long id) {
        return ResponseEntity.ok(crossingService.obtenerPorId(id));
    }

    // POST /api/v1/border-crossings : Registra un nuevo cruce fronterizo
    @Operation(summary = "Registrar", description = "POST /api/v1/border-crossings : Registra un nuevo cruce fronterizo")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Operación exitosa"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "Sin permisos")
    })
    @PostMapping
    public ResponseEntity<BorderCrossing> registrar(
            @Valid @RequestBody BorderCrossingDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(crossingService.registrar(dto));
    }

    // PATCH /api/v1/border-crossings/1/autorizar : El fiscalizador autoriza el cruce — cambia estado a AUTORIZADO
    @Operation(summary = "Autorizar", description = "PATCH /api/v1/border-crossings/1/autorizar : El fiscalizador autoriza el cruce — cambia estado a AUTORIZADO")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operación exitosa"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "Sin permisos")
    })
    @PatchMapping("/{id}/autorizar")
    public ResponseEntity<BorderCrossing> autorizar(
            @PathVariable Long id,
            @RequestParam String rutFiscalizador,
            @RequestParam(required = false) String observaciones) {
        return ResponseEntity.ok(
                crossingService.autorizar(id, rutFiscalizador,
                        observaciones));
    }

    // PATCH /api/v1/border-crossings/1/rechazar : El fiscalizador rechaza el cruce — cambia estado a RECHAZADO
    // Igual que autorizar pero con resultado negativo
    @Operation(summary = "Rechazar", description = "Igual que autorizar pero con resultado negativo")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operación exitosa"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "Sin permisos")
    })
    @PatchMapping("/{id}/rechazar")
    public ResponseEntity<BorderCrossing> rechazar(
            @PathVariable Long id,
            @RequestParam String rutFiscalizador,
            @RequestParam(required = false) String observaciones) {
        return ResponseEntity.ok(
                crossingService.rechazar(id, rutFiscalizador,
                        observaciones));
    }

    // DELETE /api/v1/border-crossings/1 : Elimina un cruce por su id
    @Operation(summary = "Eliminar", description = "DELETE /api/v1/border-crossings/1 : Elimina un cruce por su id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Operación exitosa"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "Sin permisos")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        crossingService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    // GET /api/v1/border-crossings/patente/ABC123 : Devuelve todos los cruces de un vehículo específico
    @Operation(summary = "Obtener Por Patente", description = "GET /api/v1/border-crossings/patente/ABC123 : Devuelve todos los cruces de un vehículo específico")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operación exitosa"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "Sin permisos")
    })
    @GetMapping("/patente/{patente}")
    public ResponseEntity<List<BorderCrossing>> obtenerPorPatente(
            @PathVariable String patente) {
        return ResponseEntity.ok(
                crossingService.obtenerPorPatente(patente));
    }

    // GET /api/v1/border-crossings/conductor/12345678-9 : Devuelve todos los cruces realizados por un conductor específico
    @Operation(summary = "Obtener Por Conductor", description = "GET /api/v1/border-crossings/conductor/12345678-9 : Devuelve todos los cruces realizados por un conductor específico")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operación exitosa"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "Sin permisos")
    })
    @GetMapping("/conductor/{rut}")
    public ResponseEntity<List<BorderCrossing>> obtenerPorConductor(
            @PathVariable String rut) {
        return ResponseEntity.ok(
                crossingService.obtenerPorConductor(rut));
    }

    // GET /api/v1/border-crossings/estado/PENDIENTE : Devuelve cruces por estado (PENDIENTE, AUTORIZADO, RECHAZADO)
    @Operation(summary = "Obtener Por Estado", description = "GET /api/v1/border-crossings/estado/PENDIENTE : Devuelve cruces por estado (PENDIENTE, AUTORIZADO, RECHAZADO)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operación exitosa"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "Sin permisos")
    })
    @GetMapping("/estado/{estado}")
    public ResponseEntity<List<BorderCrossing>> obtenerPorEstado(
            @PathVariable String estado) {
        return ResponseEntity.ok(
                crossingService.obtenerPorEstado(estado));
    }

    // GET /api/v1/border-crossings/paso/Los Libertadores : Devuelve todos los cruces de un paso fronterizo específico
    @Operation(summary = "Obtener Por Paso", description = "GET /api/v1/border-crossings/paso/Los Libertadores : Devuelve todos los cruces de un paso fronterizo específico")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operación exitosa"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "Sin permisos")
    })
    @GetMapping("/paso/{pasoFronterizo}")
    public ResponseEntity<List<BorderCrossing>> obtenerPorPaso(
            @PathVariable String pasoFronterizo) {
        return ResponseEntity.ok(
                crossingService.obtenerPorPasoFronterizo(pasoFronterizo));
    }

    // GET /api/v1/border-crossings/fiscalizador/12345678-9 : Devuelve todos los cruces que procesó un fiscalizador específico
    @Operation(summary = "Obtener Por Fiscalizador", description = "GET /api/v1/border-crossings/fiscalizador/12345678-9 : Devuelve todos los cruces que procesó un fiscalizador específico")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operación exitosa"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "Sin permisos")
    })
    @GetMapping("/fiscalizador/{rut}")
    public ResponseEntity<List<BorderCrossing>> obtenerPorFiscalizador(
            @PathVariable String rut) {
        return ResponseEntity.ok(
                crossingService.obtenerPorFiscalizador(rut));
    }

    // GET /api/v1/border-crossings/patente/ABC123/estado/AUTORIZADO : Combina dos filtros: patente + estado
    // Ej: todos los cruces AUTORIZADOS del vehículo ABC123
    @Operation(summary = "Obtener Por Patente Y Estado", description = "Ej: todos los cruces AUTORIZADOS del vehículo ABC123")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operación exitosa"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "Sin permisos")
    })
    @GetMapping("/patente/{patente}/estado/{estado}")
    public ResponseEntity<List<BorderCrossing>> obtenerPorPatenteYEstado(
            @PathVariable String patente,
            @PathVariable String estado) {
        return ResponseEntity.ok(
                crossingService.obtenerPorPatenteYEstado(patente, estado));
    }

    // GET /api/v1/border-crossings/fechas?desde=...&hasta=... : Devuelve cruces registrados en un rango de fechas
    @Operation(summary = "Obtener Por Fechas", description = "GET /api/v1/border-crossings/fechas?desde=...&hasta=... : Devuelve cruces registrados en un rango de fechas")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operación exitosa"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "Sin permisos")
    })
    @GetMapping("/fechas")
    public ResponseEntity<List<BorderCrossing>> obtenerPorFechas(
            @RequestParam String desde,
            @RequestParam String hasta) {
        LocalDateTime fechaDesde = LocalDateTime.parse(desde);
        LocalDateTime fechaHasta = LocalDateTime.parse(hasta);
        return ResponseEntity.ok(
                crossingService.obtenerPorRangoFechas(
                        fechaDesde, fechaHasta));
    }

    // GET /api/v1/border-crossings/buscar?pais=argentina : Busca cruces cuyo país destino contenga el texto buscado
    // No necesitas escribir el nombre exacto — busca si lo contiene
    @Operation(summary = "Buscar Por Pais", description = "No necesitas escribir el nombre exacto — busca si lo contiene")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operación exitosa"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "Sin permisos")
    })
    @GetMapping("/buscar")
    public ResponseEntity<List<BorderCrossing>> buscarPorPais(
            @RequestParam String pais) {
        return ResponseEntity.ok(
                crossingService.buscarPorPaisDestino(pais));
    }

    // GET /api/v1/border-crossings/patente/ABC123/ordenados : Devuelve los cruces de un vehículo del más reciente al más antiguo
    @Operation(summary = "Obtener Por Patente Ordenados", description = "GET /api/v1/border-crossings/patente/ABC123/ordenados : Devuelve los cruces de un vehículo del más reciente al más antiguo")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operación exitosa"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "Sin permisos")
    })
    @GetMapping("/patente/{patente}/ordenados")
    public ResponseEntity<List<BorderCrossing>> obtenerPorPatenteOrdenados(
            @PathVariable String patente) {
        return ResponseEntity.ok(
                crossingService.obtenerPorPatenteOrdenados(patente));
    }

    // GET /api/v1/border-crossings/ultimos : Devuelve los últimos 10 cruces registrados en el sistema
    @Operation(summary = "Obtener Ultimos Cruces", description = "GET /api/v1/border-crossings/ultimos : Devuelve los últimos 10 cruces registrados en el sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operación exitosa"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "Sin permisos")
    })
    @GetMapping("/ultimos")
    public ResponseEntity<List<BorderCrossing>> obtenerUltimosCruces() {
        return ResponseEntity.ok(crossingService.obtenerUltimosCruces());
    }

    // GET /api/v1/border-crossings/estadisticas/estado/PENDIENTE : Cuenta cuántos cruces hay con ese estado
    @GetMapping("/estadisticas/estado/{estado}")
    public ResponseEntity<Map<String, Long>> contarPorEstado(
            @PathVariable String estado) {
        long total = crossingService.contarPorEstado(estado);
        return ResponseEntity.ok(Map.of("total", total));
    }

    // GET /api/v1/border-crossings/estadisticas/paso/Los Libertadores : Cuenta cuántos cruces se hicieron en un paso fronterizo específico
    // Útil para saber qué paso tiene más actividad
    @GetMapping("/estadisticas/paso/{pasoFronterizo}")
    public ResponseEntity<Map<String, Long>> contarPorPaso(
            @PathVariable String pasoFronterizo) {
        long total = crossingService.contarPorPasoFronterizo(
                pasoFronterizo);
        return ResponseEntity.ok(Map.of("total", total));
    }
}