// Recibe las peticiones HTTP del Sanitary Service
// Llama al Service y retorna ResponseEntity con JSON
// Nunca tiene lógica de negocio directamente
package com.example.SanitaryService.controller;

import com.example.SanitaryService.dto.SanitaryDTO;
import com.example.SanitaryService.model.Sanitary;
import com.example.SanitaryService.service.SanitaryService;
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
@RequestMapping("/api/v1/sanitary")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class SanitaryController {

    // SanitaryService contiene toda la lógica de negocio
    // El Controller solo recibe y responde — nunca tiene lógica propia
    private final SanitaryService sanitaryService;

    // GET /api/v1/sanitary
    // Devuelve todas las inspecciones sanitarias del sistema
    @Operation(summary = "Obtener Todas", description = "Devuelve todas las inspecciones sanitarias del sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operación exitosa"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "Sin permisos")
    })
    @GetMapping
    public ResponseEntity<List<Sanitary>> obtenerTodas() {
        return ResponseEntity.ok(sanitaryService.obtenerTodas());
    }

    // GET /api/v1/sanitary/1
    // Devuelve una inspección específica por su id
    @Operation(summary = "Obtener Por Id", description = "Devuelve una inspección específica por su id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operación exitosa"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "Sin permisos")
    })
    @GetMapping("/{id}")
    public ResponseEntity<Sanitary> obtenerPorId(
            @PathVariable Long id) {
        return ResponseEntity.ok(sanitaryService.obtenerPorId(id));
    }

    // POST /api/v1/sanitary
    // Registra una nueva inspección sanitaria para un vehículo
    @Operation(summary = "Registrar", description = "Registra una nueva inspección sanitaria para un vehículo")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Operación exitosa"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "Sin permisos")
    })
    @PostMapping
    public ResponseEntity<Sanitary> registrar(
            @Valid @RequestBody SanitaryDTO dto) {
        // HTTP 201 = se creó un nuevo recurso exitosamente
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(sanitaryService.registrar(dto));
    }

    // PATCH /api/v1/sanitary/1/aprobar
    // El inspector aprueba la inspección — resultado APROBADO
    // observaciones es opcional — puede incluir comentarios adicionales
    @Operation(summary = "Aprobar", description = "observaciones es opcional — puede incluir comentarios adicionales")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operación exitosa"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "Sin permisos")
    })
    @PatchMapping("/{id}/aprobar")
    public ResponseEntity<Sanitary> aprobar(
            @PathVariable Long id,
            // required = false → el parámetro es opcional
            // Si no llega → observaciones = null
            @RequestParam(required = false) String observaciones) {
        return ResponseEntity.ok(
                sanitaryService.aprobar(id, observaciones));
    }

    // PATCH /api/v1/sanitary/1/rechazar
    // El inspector rechaza la inspección — resultado RECHAZADO
    // El vehículo no puede cruzar la frontera
    @Operation(summary = "Rechazar", description = "El vehículo no puede cruzar la frontera")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operación exitosa"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "Sin permisos")
    })
    @PatchMapping("/{id}/rechazar")
    public ResponseEntity<Sanitary> rechazar(
            @PathVariable Long id,
            @RequestParam(required = false) String observaciones) {
        return ResponseEntity.ok(
                sanitaryService.rechazar(id, observaciones));
    }

    // DELETE /api/v1/sanitary/1
    // Elimina una inspección por su id
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
        sanitaryService.eliminar(id);
        // HTTP 204 = operación exitosa sin contenido en la respuesta
        return ResponseEntity.noContent().build();
    }

    // GET /api/v1/sanitary/patente/ABC123
    // Devuelve todas las inspecciones de un vehículo específico
    @Operation(summary = "Obtener Por Patente", description = "Devuelve todas las inspecciones de un vehículo específico")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operación exitosa"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "Sin permisos")
    })
    @GetMapping("/patente/{patente}")
    public ResponseEntity<List<Sanitary>> obtenerPorPatente(
            @PathVariable String patente) {
        return ResponseEntity.ok(
                sanitaryService.obtenerPorPatente(patente));
    }

    // GET /api/v1/sanitary/conductor/12345678-9
    // Devuelve todas las inspecciones de un conductor específico
    @Operation(summary = "Obtener Por Conductor", description = "Devuelve todas las inspecciones de un conductor específico")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operación exitosa"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "Sin permisos")
    })
    @GetMapping("/conductor/{rut}")
    public ResponseEntity<List<Sanitary>> obtenerPorConductor(
            @PathVariable String rut) {
        return ResponseEntity.ok(
                sanitaryService.obtenerPorConductor(rut));
    }

    // GET /api/v1/sanitary/inspector/12345678-9
    // Devuelve todas las inspecciones realizadas por un inspector específico
    @Operation(summary = "Obtener Por Inspector", description = "Devuelve todas las inspecciones realizadas por un inspector específico")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operación exitosa"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "Sin permisos")
    })
    @GetMapping("/inspector/{rut}")
    public ResponseEntity<List<Sanitary>> obtenerPorInspector(
            @PathVariable String rut) {
        return ResponseEntity.ok(
                sanitaryService.obtenerPorInspector(rut));
    }

    // GET /api/v1/sanitary/resultado/APROBADO
    // Devuelve inspecciones por resultado (PENDIENTE, APROBADO, RECHAZADO)
    @Operation(summary = "Obtener Por Resultado", description = "Devuelve inspecciones por resultado (PENDIENTE, APROBADO, RECHAZADO)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operación exitosa"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "Sin permisos")
    })
    @GetMapping("/resultado/{resultado}")
    public ResponseEntity<List<Sanitary>> obtenerPorResultado(
            @PathVariable String resultado) {
        return ResponseEntity.ok(
                sanitaryService.obtenerPorResultado(resultado));
    }

    // GET /api/v1/sanitary/paso/Los Libertadores
    // Devuelve todas las inspecciones de un paso fronterizo específico
    @Operation(summary = "Obtener Por Paso", description = "Devuelve todas las inspecciones de un paso fronterizo específico")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operación exitosa"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "Sin permisos")
    })
    @GetMapping("/paso/{pasoFronterizo}")
    public ResponseEntity<List<Sanitary>> obtenerPorPaso(
            @PathVariable String pasoFronterizo) {
        return ResponseEntity.ok(
                sanitaryService.obtenerPorPasoFronterizo(pasoFronterizo));
    }

    // GET /api/v1/sanitary/patente/ABC123/resultado/APROBADO
    // Combina dos filtros: patente + resultado
    @Operation(summary = "Obtener Por Patente Y Resultado", description = "Combina dos filtros: patente + resultado")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operación exitosa"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "Sin permisos")
    })
    @GetMapping("/patente/{patente}/resultado/{resultado}")
    public ResponseEntity<List<Sanitary>> obtenerPorPatenteYResultado(
            @PathVariable String patente,
            @PathVariable String resultado) {
        return ResponseEntity.ok(
                sanitaryService.obtenerPorPatenteYResultado(
                        patente, resultado));
    }

    // GET /api/v1/sanitary/fechas?desde=2025-01-01T00:00:00&hasta=2025-12-31T23:59:59
    // Devuelve inspecciones registradas en un rango de fechas
    @Operation(summary = "Obtener Por Fechas", description = "Devuelve inspecciones registradas en un rango de fechas")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operación exitosa"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "Sin permisos")
    })
    @GetMapping("/fechas")
    public ResponseEntity<List<Sanitary>> obtenerPorFechas(
            @RequestParam String desde,
            @RequestParam String hasta) {
        // Convierte el texto a fecha
        // Ej: "2025-01-01T00:00:00" → LocalDateTime del 1 enero 2025
        LocalDateTime fechaDesde = LocalDateTime.parse(desde);
        LocalDateTime fechaHasta = LocalDateTime.parse(hasta);
        return ResponseEntity.ok(
                sanitaryService.obtenerPorRangoFechas(
                        fechaDesde, fechaHasta));
    }

    // GET /api/v1/sanitary/patente/ABC123/ordenadas
    // Devuelve inspecciones de un vehículo del más reciente al más antiguo
    @Operation(summary = "Obtener Por Patente Ordenadas", description = "Devuelve inspecciones de un vehículo del más reciente al más antiguo")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operación exitosa"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "Sin permisos")
    })
    @GetMapping("/patente/{patente}/ordenadas")
    public ResponseEntity<List<Sanitary>> obtenerPorPatenteOrdenadas(
            @PathVariable String patente) {
        return ResponseEntity.ok(
                sanitaryService.obtenerPorPatenteOrdenadas(patente));
    }

    // GET /api/v1/sanitary/pendientes/ordenadas
    // Devuelve inspecciones PENDIENTES del más antiguo al más reciente
    // Útil para procesar las inspecciones en orden de llegada
    @Operation(summary = "Obtener Pendientes Ordenadas", description = "Útil para procesar las inspecciones en orden de llegada")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operación exitosa"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "Sin permisos")
    })
    @GetMapping("/pendientes/ordenadas")
    public ResponseEntity<List<Sanitary>> obtenerPendientesOrdenadas() {
        return ResponseEntity.ok(
                sanitaryService.obtenerPendientesOrdenadas());
    }

    // GET /api/v1/sanitary/ultimas
    // Devuelve las últimas 10 inspecciones registradas en el sistema
    @Operation(summary = "Obtener Ultimas", description = "Devuelve las últimas 10 inspecciones registradas en el sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operación exitosa"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "Sin permisos")
    })
    @GetMapping("/ultimas")
    public ResponseEntity<List<Sanitary>> obtenerUltimas() {
        return ResponseEntity.ok(
                sanitaryService.obtenerUltimasInspecciones());
    }

    // GET /api/v1/sanitary/estadisticas/resultado/APROBADO
    // Cuenta cuántas inspecciones hay con ese resultado
    @GetMapping("/estadisticas/resultado/{resultado}")
    public ResponseEntity<Map<String, Long>> contarPorResultado(
            @PathVariable String resultado) {
        long total = sanitaryService.contarPorResultado(resultado);
        return ResponseEntity.ok(Map.of("total", total));
    }
}