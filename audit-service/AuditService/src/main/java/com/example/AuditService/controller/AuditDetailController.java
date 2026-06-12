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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@RestController
@RequestMapping("/api/v1/audit-details")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class AuditDetailController {

    private final AuditDetailService detailService;

    // CRUD BÁSICO

    // GET /api/v1/audit-details = Devuelve todos los detalles de auditoría del sistema
    @Operation(summary = "Obtener Todos", description = "GET /api/v1/audit-details = Devuelve todos los detalles de auditoría del sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operación exitosa"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "Sin permisos")
    })
    @GetMapping
    public ResponseEntity<List<AuditDetail>> obtenerTodos() {
        return ResponseEntity.ok(detailService.obtenerTodos());
    }

    // GET /api/v1/audit-details/1 = Devuelve un detalle específico por su id
    @Operation(summary = "Obtener Por Id", description = "GET /api/v1/audit-details/1 = Devuelve un detalle específico por su id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operación exitosa"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "Sin permisos")
    })
    @GetMapping("/{id}")
    public ResponseEntity<AuditDetail> obtenerPorId(
            @PathVariable Long id) {
        return ResponseEntity.ok(detailService.obtenerPorId(id));
    }

    // POST /api/v1/audit-details = Registra una nueva acción dentro de una auditoría existente
    @Operation(summary = "Registrar", description = "POST /api/v1/audit-details = Registra una nueva acción dentro de una auditoría existente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Operación exitosa"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "Sin permisos")
    })
    @PostMapping
    public ResponseEntity<AuditDetail> registrar(
            @Valid @RequestBody AuditDetailDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(detailService.registrar(dto));
    }

    // DELETE /api/v1/audit-details/1 = Elimina un detalle de auditoría por su id
    @Operation(summary = "Eliminar", description = "DELETE /api/v1/audit-details/1 = Elimina un detalle de auditoría por su id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Operación exitosa"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "Sin permisos")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        detailService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    // CONSULTAS DERIVADAS

    // GET /api/v1/audit-details/auditoria/1 = Devuelve todos los detalles que pertenecen a una auditoría específica
    @Operation(summary = "Obtener Por Auditoria", description = "GET /api/v1/audit-details/auditoria/1 = Devuelve todos los detalles que pertenecen a una auditoría específica")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operación exitosa"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "Sin permisos")
    })
    @GetMapping("/auditoria/{auditId}")
    public ResponseEntity<List<AuditDetail>> obtenerPorAuditoria(
            @PathVariable Long auditId) {
        return ResponseEntity.ok(
                detailService.obtenerPorAuditoria(auditId));
    }

    // GET /api/v1/audit-details/resultado/SOSPECHOSO = Devuelve todos los detalles con un resultado específico (EXITOSO, FALLIDO, SOSPECHOSO)
    @Operation(summary = "Obtener Por Resultado", description = "GET /api/v1/audit-details/resultado/SOSPECHOSO = Devuelve todos los detalles con un resultado específico (EXITOSO, FALLIDO, SOSPECHOSO)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operación exitosa"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "Sin permisos")
    })
    @GetMapping("/resultado/{resultado}")
    public ResponseEntity<List<AuditDetail>> obtenerPorResultado(
            @PathVariable String resultado) {
        return ResponseEntity.ok(
                detailService.obtenerPorResultado(resultado));
    }

    // GET /api/v1/audit-details/auditoria/1/resultado/FALLIDO = Combina dos filtros a la vez: auditoría específica + resultado específico
    @Operation(summary = "Obtener Por Auditoria Y Resultado", description = "GET /api/v1/audit-details/auditoria/1/resultado/FALLIDO = Combina dos filtros a la vez: auditoría específica + resultado específico")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operación exitosa"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "Sin permisos")
    })
    @GetMapping("/auditoria/{auditId}/resultado/{resultado}")
    public ResponseEntity<List<AuditDetail>> obtenerPorAuditoriaYResultado(
            @PathVariable Long auditId,
            @PathVariable String resultado) {
        return ResponseEntity.ok(
                detailService.obtenerPorAuditoriaYResultado(
                        auditId, resultado));
    }

    // GET /api/v1/audit-details/usuario/12345678-9 = Devuelve todas las acciones realizadas por un usuario específico
    @Operation(summary = "Obtener Por Usuario", description = "GET /api/v1/audit-details/usuario/12345678-9 = Devuelve todas las acciones realizadas por un usuario específico")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operación exitosa"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "Sin permisos")
    })
    @GetMapping("/usuario/{rut}")
    public ResponseEntity<List<AuditDetail>> obtenerPorUsuario(
            @PathVariable String rut) {
        return ResponseEntity.ok(detailService.obtenerPorUsuario(rut));
    }

    // GET /api/v1/audit-details/usuario/12345678-9/sospechosos = Devuelve solo las acciones SOSPECHOSAS de un usuario específico
    @Operation(summary = "Obtener Sospechosos", description = "GET /api/v1/audit-details/usuario/12345678-9/sospechosos = Devuelve solo las acciones SOSPECHOSAS de un usuario específico")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operación exitosa"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "Sin permisos")
    })
    @GetMapping("/usuario/{rut}/sospechosos")
    public ResponseEntity<List<AuditDetail>> obtenerSospechosos(
            @PathVariable String rut) {
        return ResponseEntity.ok(
                detailService.obtenerSospechososPorUsuario(rut));
    }

    // GET /api/v1/audit-details/fechas?desde=...&hasta=... = Devuelve los detalles registrados dentro de un rango de fechas
    @Operation(summary = "Obtener Por Fechas", description = "GET /api/v1/audit-details/fechas?desde=...&hasta=... = Devuelve los detalles registrados dentro de un rango de fechas")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operación exitosa"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "Sin permisos")
    })
    @GetMapping("/fechas")
    public ResponseEntity<List<AuditDetail>> obtenerPorFechas(
            @RequestParam String desde,
            @RequestParam String hasta) {
        LocalDateTime fechaDesde = LocalDateTime.parse(desde);
        LocalDateTime fechaHasta = LocalDateTime.parse(hasta);
        return ResponseEntity.ok(
                detailService.obtenerPorRangoFechas(fechaDesde, fechaHasta));
    }

    // GET /api/v1/audit-details/buscar?accion=login = Busca detalles por nombre de acción de forma parcial
    @Operation(summary = "Buscar Por Accion", description = "GET /api/v1/audit-details/buscar?accion=login = Busca detalles por nombre de acción de forma parcial")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operación exitosa"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "Sin permisos")
    })
    @GetMapping("/buscar")
    public ResponseEntity<List<AuditDetail>> buscarPorAccion(
            @RequestParam String accion) {
        return ResponseEntity.ok(detailService.buscarPorAccion(accion));
    }

    // GET /api/v1/audit-details/auditoria/1/ordenados = Devuelve los detalles de una auditoría ordenados por fecha del más reciente al más antiguo
    @Operation(summary = "Obtener Ordenados", description = "GET /api/v1/audit-details/auditoria/1/ordenados = Devuelve los detalles de una auditoría ordenados por fecha del más reciente al más antiguo")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operación exitosa"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "Sin permisos")
    })
    @GetMapping("/auditoria/{auditId}/ordenados")
    public ResponseEntity<List<AuditDetail>> obtenerOrdenados(
            @PathVariable Long auditId) {
        return ResponseEntity.ok(
                detailService.obtenerPorAuditoriaOrdenados(auditId));
    }

    // GET /api/v1/audit-details/ultimos = Devuelve los últimos 10 detalles registrados en el sistema
    @Operation(summary = "Obtener Ultimos", description = "GET /api/v1/audit-details/ultimos = Devuelve los últimos 10 detalles registrados en el sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operación exitosa"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "Sin permisos")
    })
    @GetMapping("/ultimos")
    public ResponseEntity<List<AuditDetail>> obtenerUltimos() {
        return ResponseEntity.ok(detailService.obtenerUltimosDetalles());
    }

    // GET /api/v1/audit-details/estadisticas/sospechosos/12345678-9 = Cuenta cuántas acciones SOSPECHOSAS tiene un usuario específico
    @GetMapping("/estadisticas/sospechosos/{rut}")
    public ResponseEntity<Map<String, Long>> contarSospechosos(
            @PathVariable String rut) {
        long total = detailService.contarSospechososPorUsuario(rut);
        return ResponseEntity.ok(Map.of("total", total));
    }
}