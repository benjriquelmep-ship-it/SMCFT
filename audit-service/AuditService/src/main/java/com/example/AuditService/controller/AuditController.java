package com.example.AuditService.controller;

import com.example.AuditService.dto.AuditDTO;
import com.example.AuditService.model.Audit;
import com.example.AuditService.service.AuditService;
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

//donde recibe peticiones HTTP y las devuelves en JSON
@RestController
//ruta de todas las clases
@RequestMapping("/api/v1/audits")
//genera el constructor automatico
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class AuditController {

    //donde se contiene la logica del negocio
    private final AuditService auditService;

    // CRUD BÁSICO

    // GET /api/v1/audits = devuelve todas las auditorias
    @Operation(summary = "Obtener Todas", description = "GET /api/v1/audits = devuelve todas las auditorias")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operación exitosa"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "Sin permisos")
    })
    @GetMapping
    public ResponseEntity<List<Audit>> obtenerTodas() {
        return ResponseEntity.ok(auditService.obtenerTodas());
    }

    // GET /api/v1/audits/1 = para buscar por id
    @Operation(summary = "Obtener Por Id", description = "GET /api/v1/audits/1 = para buscar por id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operación exitosa"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "Sin permisos")
    })
    @GetMapping("/{id}")
    public ResponseEntity<Audit> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(auditService.obtenerPorId(id));
    }

    // POST /api/v1/audits = donde creamos una nueva auditoria
    @Operation(summary = "Registrar", description = "POST /api/v1/audits = donde creamos una nueva auditoria")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Operación exitosa"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "Sin permisos")
    })
    @PostMapping
    public ResponseEntity<Audit> registrar(
            @Valid @RequestBody AuditDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(auditService.registrar(dto));
    }

    // PATCH /api/v1/audits/1/completar = cambiamos el estado a completado (actualizacion parcial)
    @Operation(summary = "Completar", description = "PATCH /api/v1/audits/1/completar = cambiamos el estado a completado (actualizacion parcial)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operación exitosa"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "Sin permisos")
    })
    @PatchMapping("/{id}/completar")
    public ResponseEntity<Audit> completar(@PathVariable Long id) {
        return ResponseEntity.ok(auditService.completar(id));
    }

    // PATCH /api/v1/audits/1/observacion = cambia el estado a observacion (actualizacion parcial)
    @Operation(summary = "Marcar Observacion", description = "PATCH /api/v1/audits/1/observacion = cambia el estado a observacion (actualizacion parcial)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operación exitosa"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "Sin permisos")
    })
    @PatchMapping("/{id}/observacion")
    public ResponseEntity<Audit> marcarObservacion(
            @PathVariable Long id) {
        return ResponseEntity.ok(auditService.marcarObservacion(id));
    }

    // DELETE /api/v1/audits/1 = elimina  auditoria (no tiene body)
    @Operation(summary = "Eliminar", description = "DELETE /api/v1/audits/1 = elimina  auditoria (no tiene body)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Operación exitosa"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "Sin permisos")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        auditService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    // CONSULTAS DERIVADAS

    // GET /api/v1/audits/auditor/12345678-9 = busca las auditorias de un auditor especifico
    @Operation(summary = "Obtener Por Auditor", description = "GET /api/v1/audits/auditor/12345678-9 = busca las auditorias de un auditor especifico")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operación exitosa"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "Sin permisos")
    })
    @GetMapping("/auditor/{rut}")
    public ResponseEntity<List<Audit>> obtenerPorAuditor(
            @PathVariable String rut) {
        return ResponseEntity.ok(auditService.obtenerPorAuditor(rut));
    }

    // GET /api/v1/audits/tipo/USUARIO = Busca auditorías por tipo (/tipo/USUARIO, /tipo/SISTEMA, /tipo/TRANSACCION)
    @Operation(summary = "Obtener Por Tipo", description = "GET /api/v1/audits/tipo/USUARIO = Busca auditorías por tipo (/tipo/USUARIO, /tipo/SISTEMA, /tipo/TRANSACCION)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operación exitosa"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "Sin permisos")
    })
    @GetMapping("/tipo/{tipo}")
    public ResponseEntity<List<Audit>> obtenerPorTipo(
            @PathVariable String tipo) {
        return ResponseEntity.ok(auditService.obtenerPorTipo(tipo));
    }

    // GET /api/v1/audits/estado/EN_PROCESO = Busca auditorías por estado (/estado/EN_PROCESO, /estado/COMPLETADA, /estado/OBSERVACION)
    @Operation(summary = "Obtener Por Estado", description = "GET /api/v1/audits/estado/EN_PROCESO = Busca auditorías por estado (/estado/EN_PROCESO, /estado/COMPLETADA, /estado/OBSERVACION)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operación exitosa"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "Sin permisos")
    })
    @GetMapping("/estado/{estado}")
    public ResponseEntity<List<Audit>> obtenerPorEstado(
            @PathVariable String estado) {
        return ResponseEntity.ok(auditService.obtenerPorEstado(estado));
    }

    // GET /api/v1/audits/entidad/User Service = Busca auditorías de una entidad específica (/entidad/User Service, /entidad/Border Crossing Service)
    @Operation(summary = "Obtener Por Entidad", description = "GET /api/v1/audits/entidad/User Service = Busca auditorías de una entidad específica (/entidad/User Service, /entidad/Border Crossing Service)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operación exitosa"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "Sin permisos")
    })
    @GetMapping("/entidad/{entidad}")
    public ResponseEntity<List<Audit>> obtenerPorEntidad(
            @PathVariable String entidad) {
        return ResponseEntity.ok(auditService.obtenerPorEntidad(entidad));
    }

    // GET /api/v1/audits/entidad/User Service/1 = Busca auditorías de una entidad Y un registro específico
    @Operation(summary = "Obtener Por Entidad Y Id", description = "GET /api/v1/audits/entidad/User Service/1 = Busca auditorías de una entidad Y un registro específico")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operación exitosa"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "Sin permisos")
    })
    @GetMapping("/entidad/{entidad}/{entidadId}")
    public ResponseEntity<List<Audit>> obtenerPorEntidadYId(
            @PathVariable String entidad,
            @PathVariable Long entidadId) {
        return ResponseEntity.ok(
                auditService.obtenerPorEntidadYId(entidad, entidadId));
    }

    // GET /api/v1/audits/fechas?desde=...&hasta=... = busca por un rango de fecha y hora
    @Operation(summary = "Obtener Por Fechas", description = "GET /api/v1/audits/fechas?desde=...&hasta=... = busca por un rango de fecha y hora")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operación exitosa"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "Sin permisos")
    })
    @GetMapping("/fechas")
    public ResponseEntity<List<Audit>> obtenerPorFechas(
            @RequestParam String desde,
            @RequestParam String hasta) {
        LocalDateTime fechaDesde = LocalDateTime.parse(desde);
        LocalDateTime fechaHasta = LocalDateTime.parse(hasta);
        return ResponseEntity.ok(
                auditService.obtenerPorRangoFechas(fechaDesde, fechaHasta));
    }

    // GET /api/v1/audits/auditor/12345678-9/ordenadas = ordena de más reciente a más antigua
    @Operation(summary = "Obtener Por Auditor Ordenadas", description = "GET /api/v1/audits/auditor/12345678-9/ordenadas = ordena de más reciente a más antigua")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operación exitosa"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "Sin permisos")
    })
    @GetMapping("/auditor/{rut}/ordenadas")
    public ResponseEntity<List<Audit>> obtenerPorAuditorOrdenadas(
            @PathVariable String rut) {
        return ResponseEntity.ok(
                auditService.obtenerPorAuditorOrdenadas(rut));
    }

    // GET /api/v1/audits/en-proceso/ordenadas = Devuelve solo las auditorías EN_PROCESO ordenadas de más antigua a más reciente
    @Operation(summary = "Obtener En Proceso Ordenadas", description = "GET /api/v1/audits/en-proceso/ordenadas = Devuelve solo las auditorías EN_PROCESO ordenadas de más antigua a más reciente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operación exitosa"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "Sin permisos")
    })
    @GetMapping("/en-proceso/ordenadas")
    public ResponseEntity<List<Audit>> obtenerEnProcesoOrdenadas() {
        return ResponseEntity.ok(
                auditService.obtenerEnProcesoOrdenadas());
    }

    // GET /api/v1/audits/ultimas = Devuelve las últimas 10 auditorías registradas en el sistema
    @Operation(summary = "Obtener Ultimas", description = "GET /api/v1/audits/ultimas = Devuelve las últimas 10 auditorías registradas en el sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operación exitosa"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "Sin permisos")
    })
    @GetMapping("/ultimas")
    public ResponseEntity<List<Audit>> obtenerUltimas() {
        return ResponseEntity.ok(
                auditService.obtenerUltimasAuditorias());
    }

    // GET /api/v1/audits/estadisticas/estado/COMPLETADA = Devuelve cuántas auditorías hay con ese estado
    @GetMapping("/estadisticas/estado/{estado}")
    public ResponseEntity<Map<String, Long>> contarPorEstado(
            @PathVariable String estado) {
        long total = auditService.contarPorEstado(estado);
        return ResponseEntity.ok(Map.of("total", total));
    }

    // GET /api/v1/audits/estadisticas/tipo/USUARIO = Devuelve cuántas auditorías hay de ese tipo
    @GetMapping("/estadisticas/tipo/{tipo}")
    public ResponseEntity<Map<String, Long>> contarPorTipo(
            @PathVariable String tipo) {
        long total = auditService.contarPorTipo(tipo);
        return ResponseEntity.ok(Map.of("total", total));
    }
}