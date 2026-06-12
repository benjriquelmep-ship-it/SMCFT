// Recibe las peticiones HTTP para las alertas de deadlines
// Llama al Service y retorna ResponseEntity con JSON
// Nunca tiene lógica de negocio directamente
package com.example.DeadlineService.controller;

import com.example.DeadlineService.dto.DeadlineAlertDTO;
import com.example.DeadlineService.model.DeadlineAlert;
import com.example.DeadlineService.service.DeadlineAlertService;
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
@RequestMapping("/api/v1/deadline-alerts")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class DeadlineAlertController {

    // DeadlineAlertService contiene toda la lógica de negocio
    // El Controller solo recibe y responde — nunca tiene lógica propia
    private final DeadlineAlertService alertService;

    // GET /api/v1/deadline-alerts : Devuelve todas las alertas del sistema
    @Operation(summary = "Obtener Todas", description = "GET /api/v1/deadline-alerts : Devuelve todas las alertas del sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operación exitosa"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "Sin permisos")
    })
    @GetMapping
    public ResponseEntity<List<DeadlineAlert>> obtenerTodas() {
        return ResponseEntity.ok(alertService.obtenerTodas());
    }

    // GET /api/v1/deadline-alerts/1 : Devuelve una alerta específica por su id
    @Operation(summary = "Obtener Por Id", description = "GET /api/v1/deadline-alerts/1 : Devuelve una alerta específica por su id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operación exitosa"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "Sin permisos")
    })
    @GetMapping("/{id}")
    public ResponseEntity<DeadlineAlert> obtenerPorId(
            @PathVariable Long id) {
        return ResponseEntity.ok(alertService.obtenerPorId(id));
    }

    // POST /api/v1/deadline-alerts : Crea una alerta manual para un deadline específico
    @Operation(summary = "Crear", description = "POST /api/v1/deadline-alerts : Crea una alerta manual para un deadline específico")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Operación exitosa"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "Sin permisos")
    })
    @PostMapping
    public ResponseEntity<DeadlineAlert> crear(
            @Valid @RequestBody DeadlineAlertDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(alertService.crear(dto));
    }

    // POST /api/v1/deadline-alerts/automatica/1 : Genera una alerta automáticamente según los días restantes del deadline
    @Operation(summary = "Generar Automatica", description = "POST /api/v1/deadline-alerts/automatica/1 : Genera una alerta automáticamente según los días restantes del deadline")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Operación exitosa"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "Sin permisos")
    })
    @PostMapping("/automatica/{deadlineId}")
    public ResponseEntity<DeadlineAlert> generarAutomatica(
            @PathVariable Long deadlineId) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(alertService
                        .generarAlertaAutomatica(deadlineId));
    }

    // PATCH /api/v1/deadline-alerts/1/enviada : Marca una alerta como enviada al Notification Service
    @Operation(summary = "Marcar Como Enviada", description = "PATCH /api/v1/deadline-alerts/1/enviada : Marca una alerta como enviada al Notification Service")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operación exitosa"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "Sin permisos")
    })
    @PatchMapping("/{id}/enviada")
    public ResponseEntity<DeadlineAlert> marcarComoEnviada(
            @PathVariable Long id) {
        return ResponseEntity.ok(alertService.marcarComoEnviada(id));
    }

    // DELETE /api/v1/deadline-alerts/1 : Elimina una alerta por su id
    @Operation(summary = "Eliminar", description = "DELETE /api/v1/deadline-alerts/1 : Elimina una alerta por su id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Operación exitosa"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "Sin permisos")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        alertService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    // GET /api/v1/deadline-alerts/deadline/1 : Devuelve todas las alertas que pertenecen a un deadline específico
    @Operation(summary = "Obtener Por Deadline", description = "GET /api/v1/deadline-alerts/deadline/1 : Devuelve todas las alertas que pertenecen a un deadline específico")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operación exitosa"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "Sin permisos")
    })
    @GetMapping("/deadline/{deadlineId}")
    public ResponseEntity<List<DeadlineAlert>> obtenerPorDeadline(
            @PathVariable Long deadlineId) {
        return ResponseEntity.ok(
                alertService.obtenerPorDeadline(deadlineId));
    }

    // GET /api/v1/deadline-alerts/no-enviadas : Devuelve todas las alertas que aún no fueron enviadas
    @Operation(summary = "Obtener No Enviadas", description = "GET /api/v1/deadline-alerts/no-enviadas : Devuelve todas las alertas que aún no fueron enviadas")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operación exitosa"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "Sin permisos")
    })
    @GetMapping("/no-enviadas")
    public ResponseEntity<List<DeadlineAlert>> obtenerNoEnviadas() {
        return ResponseEntity.ok(alertService.obtenerNoEnviadas());
    }

    // GET /api/v1/deadline-alerts/enviadas : Devuelve todas las alertas que ya fueron enviadas al Notification Service
    @Operation(summary = "Obtener Enviadas", description = "GET /api/v1/deadline-alerts/enviadas : Devuelve todas las alertas que ya fueron enviadas al Notification Service")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operación exitosa"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "Sin permisos")
    })
    @GetMapping("/enviadas")
    public ResponseEntity<List<DeadlineAlert>> obtenerEnviadas() {
        return ResponseEntity.ok(alertService.obtenerEnviadas());
    }

    // GET /api/v1/deadline-alerts/urgentes : Devuelve alertas de tipo URGENTE que aún no fueron enviadas
    @Operation(summary = "Obtener Urgentes", description = "GET /api/v1/deadline-alerts/urgentes : Devuelve alertas de tipo URGENTE que aún no fueron enviadas")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operación exitosa"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "Sin permisos")
    })
    @GetMapping("/urgentes")
    public ResponseEntity<List<DeadlineAlert>> obtenerUrgentes() {
        return ResponseEntity.ok(
                alertService.obtenerUrgentesNoEnviadas());
    }

    // GET /api/v1/deadline-alerts/vencidos : Devuelve alertas de tipo VENCIDO que aún no fueron enviadas
    @Operation(summary = "Obtener Vencidos", description = "GET /api/v1/deadline-alerts/vencidos : Devuelve alertas de tipo VENCIDO que aún no fueron enviadas")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operación exitosa"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "Sin permisos")
    })
    @GetMapping("/vencidos")
    public ResponseEntity<List<DeadlineAlert>> obtenerVencidos() {
        return ResponseEntity.ok(
                alertService.obtenerVencidasNoEnviadas());
    }

    // GET /api/v1/deadline-alerts/deadline/1/ordenadas : Devuelve las alertas de un deadline ordenadas por días restantes
    @Operation(summary = "Obtener Ordenadas", description = "GET /api/v1/deadline-alerts/deadline/1/ordenadas : Devuelve las alertas de un deadline ordenadas por días restantes")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operación exitosa"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "Sin permisos")
    })
    @GetMapping("/deadline/{deadlineId}/ordenadas")
    public ResponseEntity<List<DeadlineAlert>> obtenerOrdenadas(
            @PathVariable Long deadlineId) {
        return ResponseEntity.ok(
                alertService.obtenerPorDeadlineOrdenadas(deadlineId));
    }

    // GET /api/v1/deadline-alerts/ultimas : Devuelve las últimas 10 alertas registradas en el sistema
    @Operation(summary = "Obtener Ultimas", description = "GET /api/v1/deadline-alerts/ultimas : Devuelve las últimas 10 alertas registradas en el sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operación exitosa"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "Sin permisos")
    })
    @GetMapping("/ultimas")
    public ResponseEntity<List<DeadlineAlert>> obtenerUltimas() {
        return ResponseEntity.ok(alertService.obtenerUltimasAlertas());
    }

    // GET /api/v1/deadline-alerts/estadisticas/pendientes : Cuenta cuántas alertas no han sido enviadas todavía
    @GetMapping("/estadisticas/pendientes")
    public ResponseEntity<Map<String, Long>> contarNoEnviadas() {
        long total = alertService.contarNoEnviadas();
        return ResponseEntity.ok(Map.of("total", total));
    }
}