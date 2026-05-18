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

@RestController
@RequestMapping("/api/v1/audit-details")
@RequiredArgsConstructor
public class AuditDetailController {

    private final AuditDetailService detailService;

    // CRUD BÁSICO

    // GET /api/v1/audit-details = Devuelve todos los detalles de auditoría del sistema
    @GetMapping
    public ResponseEntity<List<AuditDetail>> obtenerTodos() {
        return ResponseEntity.ok(detailService.obtenerTodos());
    }

    // GET /api/v1/audit-details/1 = Devuelve un detalle específico por su id
    @GetMapping("/{id}")
    public ResponseEntity<AuditDetail> obtenerPorId(
            @PathVariable Long id) {
        return ResponseEntity.ok(detailService.obtenerPorId(id));
    }

    // POST /api/v1/audit-details = Registra una nueva acción dentro de una auditoría existente
    @PostMapping
    public ResponseEntity<AuditDetail> registrar(
            @Valid @RequestBody AuditDetailDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(detailService.registrar(dto));
    }

    // DELETE /api/v1/audit-details/1 = Elimina un detalle de auditoría por su id
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        detailService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    // CONSULTAS DERIVADAS

    // GET /api/v1/audit-details/auditoria/1 = Devuelve todos los detalles que pertenecen a una auditoría específica
    @GetMapping("/auditoria/{auditId}")
    public ResponseEntity<List<AuditDetail>> obtenerPorAuditoria(
            @PathVariable Long auditId) {
        return ResponseEntity.ok(
                detailService.obtenerPorAuditoria(auditId));
    }

    // GET /api/v1/audit-details/resultado/SOSPECHOSO = Devuelve todos los detalles con un resultado específico (EXITOSO, FALLIDO, SOSPECHOSO)
    @GetMapping("/resultado/{resultado}")
    public ResponseEntity<List<AuditDetail>> obtenerPorResultado(
            @PathVariable String resultado) {
        return ResponseEntity.ok(
                detailService.obtenerPorResultado(resultado));
    }

    // GET /api/v1/audit-details/auditoria/1/resultado/FALLIDO = Combina dos filtros a la vez: auditoría específica + resultado específico
    @GetMapping("/auditoria/{auditId}/resultado/{resultado}")
    public ResponseEntity<List<AuditDetail>> obtenerPorAuditoriaYResultado(
            @PathVariable Long auditId,
            @PathVariable String resultado) {
        return ResponseEntity.ok(
                detailService.obtenerPorAuditoriaYResultado(
                        auditId, resultado));
    }

    // GET /api/v1/audit-details/usuario/12345678-9 = Devuelve todas las acciones realizadas por un usuario específico
    @GetMapping("/usuario/{rut}")
    public ResponseEntity<List<AuditDetail>> obtenerPorUsuario(
            @PathVariable String rut) {
        return ResponseEntity.ok(detailService.obtenerPorUsuario(rut));
    }

    // GET /api/v1/audit-details/usuario/12345678-9/sospechosos = Devuelve solo las acciones SOSPECHOSAS de un usuario específico
    @GetMapping("/usuario/{rut}/sospechosos")
    public ResponseEntity<List<AuditDetail>> obtenerSospechosos(
            @PathVariable String rut) {
        return ResponseEntity.ok(
                detailService.obtenerSospechososPorUsuario(rut));
    }

    // GET /api/v1/audit-details/fechas?desde=...&hasta=... = Devuelve los detalles registrados dentro de un rango de fechas
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
    @GetMapping("/buscar")
    public ResponseEntity<List<AuditDetail>> buscarPorAccion(
            @RequestParam String accion) {
        return ResponseEntity.ok(detailService.buscarPorAccion(accion));
    }

    // GET /api/v1/audit-details/auditoria/1/ordenados = Devuelve los detalles de una auditoría ordenados por fecha del más reciente al más antiguo
    @GetMapping("/auditoria/{auditId}/ordenados")
    public ResponseEntity<List<AuditDetail>> obtenerOrdenados(
            @PathVariable Long auditId) {
        return ResponseEntity.ok(
                detailService.obtenerPorAuditoriaOrdenados(auditId));
    }

    // GET /api/v1/audit-details/ultimos = Devuelve los últimos 10 detalles registrados en el sistema
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