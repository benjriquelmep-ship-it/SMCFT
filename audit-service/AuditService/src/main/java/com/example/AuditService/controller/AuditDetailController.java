package com.example.AuditService.controller;

import com.example.AuditService.dto.AuditDetailDTO;
import com.example.AuditService.model.AuditDetail;
import com.example.AuditService.service.AuditDetailService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.CollectionModel;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/v1/audit-details")
@RequiredArgsConstructor
@Tag(name = "2. Bloque de Líneas de Acción Forense", description = "Endpoints operativos orientados al desglose, inserción inmutable de trazas y detección analítica de anomalías perimetrales")
@SecurityRequirement(name = "bearerAuth")
public class AuditDetailController {

    private final AuditDetailService detailService;

    @Operation(summary = "Obtener total de logs y líneas de acción registradas", description = "Expone el listado consolidado global de la totalidad de trazas analíticas detalladas en los andenes del ecosistema.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Listado de logs forenses expuesto de forma conforme"),
            @ApiResponse(responseCode = "401", description = "Falta de credenciales de seguridad Bearer JWT válidas"),
            @ApiResponse(responseCode = "403", description = "Privilegios insuficientes para listar la base inmutable global")
    })
    @GetMapping
    public ResponseEntity<List<AuditDetail>> obtenerTodos() {
        return ResponseEntity.ok(detailService.obtenerTodos());
    }

    // GET /api/v1/audit-details/1
    @Operation(summary = "Obtener línea de acción forense por ID", description = "Busca el nombre de la operación, glosa explicativa, IP del terminal de red y resultado técnico de una traza por su ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Línea de log analítico localizada con éxito"),
            @ApiResponse(responseCode = "404", description = "Ítem de traza forense solicitado no hallado en los sistemas")
    })
    @GetMapping("/{id}")
    public ResponseEntity<AuditDetail> obtenerPorId(@PathVariable Long id) {
        // CORREGIDO: Retorna una sola instancia de AuditDetail, solucionando el error de tipos
        return ResponseEntity.ok(detailService.obtenerPorId(id));
    }

    // POST /api/v1/audit-details
    @Operation(summary = "Inyectar línea de evento inmutable en un expediente", description = "Acopla una nueva traza de acción detallada a una cabecera de auditoría institucional activa.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Línea de evento indexada de forma exitosa y blindada en los registros"),
            @ApiResponse(responseCode = "400", description = "El ID del expediente padre no es válido o el DTO infringe restricciones estructurales")
    })
    @PostMapping
    public ResponseEntity<AuditDetail> registrar(@Valid @RequestBody AuditDetailDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(detailService.registrar(dto));
    }

    // DELETE /api/v1/audit-details/1
    @Operation(summary = "Remover registro de línea forense", description = "Elimina físicamente del sistema una hilera de log mediante su clave primaria.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Traza purgada de la persistencia de forma correcta. Sin contenido.")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        detailService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    // GET /api/v1/audit-details/auditoria/1
    @Operation(summary = "Listar líneas de acción desglosadas por ID de Auditoría", description = "Obtiene la totalidad de eventos y operaciones que integran el cuerpo analítico de un expediente consultado.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cuerpo desglosado del expediente recuperado correctamente")
    })
    @GetMapping("/auditoria/{auditId}")
    public ResponseEntity<List<AuditDetail>> obtenerPorAuditoria(@PathVariable Long auditId) {
        return ResponseEntity.ok(detailService.obtenerPorAuditoria(auditId));
    }

    // GET /api/v1/audit-details/resultado/SOSPECHOSO
    @Operation(summary = "Filtrar líneas de acción por Resultado técnico", description = "Separa e identifica las operaciones ejecutadas basándose en su nivel de alerta de seguridad (EXITOSO, FALLIDO o SOSPECHOSO).")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Filtro resolutivo de eventos completado con éxito")
    })
    @GetMapping("/resultado/{resultado}")
    public ResponseEntity<List<AuditDetail>> obtenerPorResultado(@PathVariable String resultado) {
        return ResponseEntity.ok(detailService.obtenerPorResultado(resultado));
    }

    // GET /api/v1/audit-details/auditoria/1/resultado/FALLIDO
    @Operation(summary = "Filtrar por Coincidencia Dual de Expediente Padre y Resultado", description = "Aísla de forma prioritaria los fallos o sospechas indexadas a una orden de control dada.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Coincidencia analítica de trazas devuelta")
    })
    @GetMapping("/auditoria/{auditId}/resultado/{resultado}")
    public ResponseEntity<List<AuditDetail>> obtenerPorAuditoriaYResultado(
            @PathVariable Long auditId,
            @PathVariable String resultado) {
        return ResponseEntity.ok(detailService.obtenerPorAuditoriaYResultado(auditId, resultado));
    }

    // GET /api/v1/audit-details/usuario/12345678-9
    @Operation(summary = "Buscar líneas de acción por RUN de Usuario", description = "Extrae el rastro completo de operaciones y transacciones realizadas por un operador en los andenes fronterizos.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Trazabilidad de operaciones del usuario extraída de forma conforme")
    })
    @GetMapping("/usuario/{rut}")
    public ResponseEntity<List<AuditDetail>> obtenerPorUsuario(@PathVariable String rut) {
        return ResponseEntity.ok(detailService.obtenerPorUsuario(rut));
    }

    // GET /api/v1/audit-details/usuario/12345678-9/sospechosos
    @Operation(summary = "Detectar Alertas de Tránsitos Sospechosos por Operador", description = "Aísla con carácter de urgencia aquellos eventos catalogados bajo comportamiento anómalo vinculados a un RUN de control.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Bandeja de alertas sospechosas filtrada de forma conforme")
    })
    @GetMapping("/usuario/{rut}/sospechosos")
    public ResponseEntity<List<AuditDetail>> obtenerSospechosos(@PathVariable String rut) {
        return ResponseEntity.ok(detailService.obtenerSospechososPorUsuario(rut));
    }

    // GET /api/v1/audit-details/fechas?desde=...&hasta=...
    @Operation(summary = "Filtrar por Rango Cronológico de Ocurrencia", description = "Entrega el conjunto de marcas analíticas de eventos registradas en frontera dentro de un rango temporal ISO delimitado.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Segmentación temporal de trazas completada")
    })
    @GetMapping("/fechas")
    public ResponseEntity<List<AuditDetail>> obtenerPorFechas(
            @RequestParam String desde,
            @RequestParam String hasta) {
        LocalDateTime fechaDesde = LocalDateTime.parse(desde);
        LocalDateTime fechaHasta = LocalDateTime.parse(hasta);
        return ResponseEntity.ok(detailService.obtenerPorRangoFechas(fechaDesde, fechaHasta));
    }

    // GET /api/v1/audit-details/buscar?accion=login
    @Operation(summary = "Buscar acciones por Coincidencia Predictiva de Nombre", description = "Buscador de texto predictivo parcial (LIKE) aplicado sobre la columna del tipo de operación perimetral.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Resultados predictivos parciales de logs devueltos")
    })
    @GetMapping("/buscar")
    public ResponseEntity<List<AuditDetail>> buscarPorAccion(@RequestParam String accion) {
        return ResponseEntity.ok(detailService.buscarPorAccion(accion));
    }

    // GET /api/v1/audit-details/auditoria/1/ordenados
    @Operation(summary = "Listar logs de un Expediente ordenados por Mayor Reciencia", description = "Entrega las marcas de eventos de una orden ordenadas linealmente de forma descendente (del log más nuevo al más antiguo).")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cuerpo de logs jerarquizado por fecha obtenido con éxito")
    })
    @GetMapping("/auditoria/{auditId}/ordenados")
    public ResponseEntity<List<AuditDetail>> obtenerOrdenados(@PathVariable Long auditId) {
        return ResponseEntity.ok(detailService.obtenerPorAuditoriaOrdenados(auditId));
    }

    // GET /api/v1/audit-details/ultimos
    @Operation(summary = "Monitorear últimas líneas de logs inyectadas", description = "Endpoint de reportería forense inmediata para observar las últimas 10 operaciones de andén impactadas en la persistencia.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Dashboard de monitoreo de logs de control obtenido")
    })
    @GetMapping("/ultimos")
    public ResponseEntity<List<AuditDetail>> obtenerUltimos() {
        return ResponseEntity.ok(detailService.obtenerUltimosDetalles());
    }

    // GET /api/v1/audit-details/estadisticas/sospechosos/12345678-9
    @Operation(summary = "Métrica Totalizadora de Alertas por Operador", description = "Indica de forma métrica cuantitativa cuántas alertas por comportamiento SOSPECHOSO registra un RUN específico.")
    @GetMapping("/estadisticas/sospechosos/{rut}")
    public ResponseEntity<Map<String, Long>> contarSospechosos(@PathVariable String rut) {
        long total = detailService.contarSospechososPorUsuario(rut);
        return ResponseEntity.ok(Map.of("total", total));
    }
}