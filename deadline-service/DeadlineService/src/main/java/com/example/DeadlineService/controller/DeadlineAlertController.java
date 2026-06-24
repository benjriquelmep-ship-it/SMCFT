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
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/v1/deadline-alerts")
@RequiredArgsConstructor
@Tag(name = "1. Bitácora de Alertas Cronológicas", description = "Endpoints operativos orientados al cálculo, disparo automático e historial de despacho de alertas de proximidad e infracción")
@SecurityRequirement(name = "bearerAuth")
public class DeadlineAlertController {

    private final DeadlineAlertService alertService;

    @Operation(summary = "Listar todas las alertas del sistema", description = "Recupera la bitácora histórica unificada de todas las alertas gatilladas por proximidad o expiración de plazos.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Historial global de alertas obtenido con éxito"),
            @ApiResponse(responseCode = "401", description = "No autenticado - Token Bearer JWT ausente o inválido"),
            @ApiResponse(responseCode = "403", description = "Sin permisos - Rol de auditoría temporal insuficiente")
    })
    @GetMapping
    public ResponseEntity<List<DeadlineAlert>> obtenerTodas() {
        return ResponseEntity.ok(alertService.obtenerTodas());
    }

    // GET /api/v1/deadline-alerts/1
    @Operation(summary = "Obtener alerta específica por ID", description = "Busca los metadatos, glosa y bandera de sincronización de una alerta cronológica a partir de su clave primaria.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Alerta cronológica localizada de forma conforme"),
            @ApiResponse(responseCode = "404", description = "El ID de la alerta solicitado no existe en la base de datos")
    })
    @GetMapping("/{id}")
    public ResponseEntity<DeadlineAlert> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(alertService.obtenerPorId(id));
    }

    // POST /api/v1/deadline-alerts
    @Operation(summary = "Registrar alerta cronológica manual", description = "Valida la estructura de entrada e inserta de forma explícita un aviso temporal acoplado a un ID de plazo específico.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Alerta manual registrada exitosamente en los sistemas de control"),
            @ApiResponse(responseCode = "400", description = "Payload DTO inválido o violación de restricciones estructurales")
    })
    @PostMapping
    public ResponseEntity<DeadlineAlert> crear(@Valid @RequestBody DeadlineAlertDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(alertService.crear(dto));
    }

    // POST /api/v1/deadline-alerts/automatica/1
    @Operation(summary = "Gatillar cálculo automático de alerta", description = "Ejecuta los algoritmos de lógica de negocio para evaluar la proximidad temporal de un plazo y dispara de forma automática la alerta correspondiente.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Alerta automatizada procesada y registrada con éxito"),
            @ApiResponse(responseCode = "400", description = "El ID del plazo provisto no calza con ninguna cabecera vigente")
    })
    @PostMapping("/automatica/{deadlineId}")
    public ResponseEntity<DeadlineAlert> generarAutomatica(@PathVariable Long deadlineId) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(alertService.generarAlertaAutomatica(deadlineId));
    }

    // PATCH /api/v1/deadline-alerts/1/enviada
    @Operation(summary = "Marcar alerta como Despachada", description = "Mapeo analítico perimetral consumido tras sincronizar el aviso con Notification Service, cambiando la bandera de control a verdadero.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Bandera de despacho actualizada de forma conforme"),
            @ApiResponse(responseCode = "404", description = "El ID de la alerta especificado no fue localizado")
    })
    @PatchMapping("/{id}/enviada")
    public ResponseEntity<DeadlineAlert> marcarComoEnviada(@PathVariable Long id) {
        return ResponseEntity.ok(alertService.marcarComoEnviada(id));
    }

    // DELETE /api/v1/deadline-alerts/1
    @Operation(summary = "Eliminar alerta de forma física", description = "Remueve permanentemente el registro de control de la alerta de la base de datos central.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Registro de alerta purgado correctamente de la persistencia. Sin contenido."),
            @ApiResponse(responseCode = "401", description = "No autenticado")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        alertService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    // GET /api/v1/deadline-alerts/deadline/1
    @Operation(summary = "Listar alertas asociadas a un Plazo ID", description = "Extrae e identifica la totalidad de avisos (históricos y pendientes) acoplados secuencialmente a una orden de permanencia.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Colección de alertas de la orden recuperada con éxito")
    })
    @GetMapping("/deadline/{deadlineId}")
    public ResponseEntity<List<DeadlineAlert>> obtenerPorDeadline(@PathVariable Long deadlineId) {
        return ResponseEntity.ok(alertService.obtenerPorDeadline(deadlineId));
    }

    // GET /api/v1/deadline-alerts/no-enviadas
    @Operation(summary = "Listar alertas Pendientes de despacho", description = "Aísla y expone los avisos generados en cola que registran la bandera de despacho en falso. Utilizado como pasarela por el Notification Service.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cola de sincronización pendiente recuperada con éxito")
    })
    @GetMapping("/no-enviadas")
    public ResponseEntity<List<DeadlineAlert>> obtenerNoEnviadas() {
        return ResponseEntity.ok(alertService.obtenerNoEnviadas());
    }

    // GET /api/v1/deadline-alerts/enviadas
    @Operation(summary = "Listar historial de alertas Despachadas", description = "Recupera la bitácora de auditoría cronológica de todos los avisos que ya fueron compartidos con el Notification Service.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Historial de envíos sincronizados obtenido con éxito")
    })
    @GetMapping("/enviadas")
    public ResponseEntity<List<DeadlineAlert>> obtenerEnviadas() {
        return ResponseEntity.ok(alertService.obtenerEnviadas());
    }

    // GET /api/v1/deadline-alerts/urgentes
    @Operation(summary = "Listar alertas de criticidad Urgente", description = "Aísla prioritariamente aquellos avisos pendientes de envío categorizados bajo el nivel crítico URGENTE por proximidad fatal.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Bandeja de alertas críticas e imprevistas filtrada con éxito")
    })
    @GetMapping("/urgentes")
    public ResponseEntity<List<DeadlineAlert>> obtenerUrgentes() {
        return ResponseEntity.ok(alertService.obtenerUrgentesNoEnviadas());
    }

    // GET /api/v1/deadline-alerts/vencidos
    @Operation(summary = "Listar alertas de condición Vencido", description = "Retorna el conjunto de avisos en cola que notifican penalizaciones temporales activas tras consumirse el plazo máximo legal.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Bandeja de avisos de infracción temporal extraída de forma conforme")
    })
    @GetMapping("/vencidos")
    public ResponseEntity<List<DeadlineAlert>> obtenerVencidos() {
        return ResponseEntity.ok(alertService.obtenerVencidasNoEnviadas());
    }

    // GET /api/v1/deadline-alerts/deadline/1/ordenadas
    @Operation(summary = "Listar alertas ordenadas cronológicamente", description = "Recupera los avisos indexados a una orden de permanencia, ordenados linealmente de mayor a menor según los días restantes calculados.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Nómina secuencial ordenada recuperada con éxito")
    })
    @GetMapping("/deadline/{deadlineId}/ordenadas")
    public ResponseEntity<List<DeadlineAlert>> obtenerOrdenadas(@PathVariable Long deadlineId) {
        return ResponseEntity.ok(alertService.obtenerPorDeadlineOrdenadas(deadlineId));
    }

    // GET /api/v1/deadline-alerts/ultimas
    @Operation(summary = "Listar últimas solicitudes globales de alertas", description = "Endpoint administrativo de monitoreo inmediato que expone las últimas 10 alertas gatilladas a nivel global.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Extracto analítico rápido de auditoría devuelto")
    })
    @GetMapping("/ultimas")
    public ResponseEntity<List<DeadlineAlert>> obtenerUltimas() {
        return ResponseEntity.ok(alertService.obtenerUltimasAlertas());
    }

    // GET /api/v1/deadline-alerts/estadisticas/pendientes
    @Operation(summary = "Contar volumen de alertas Pendientes", description = "Devuelve el indicador métrico cuantitativo que representa la sumatoria total de avisos en cola de envío.")
    @GetMapping("/estadisticas/pendientes")
    public ResponseEntity<Map<String, Long>> contarNoEnviadas() {
        long total = alertService.contarNoEnviadas();
        return ResponseEntity.ok(Map.of("total", total));
    }
}