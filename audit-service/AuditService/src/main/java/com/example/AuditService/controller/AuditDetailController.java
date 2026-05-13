// controller/AuditDetailController.java
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

    // -------------------------------------------------------
    // CRUD BÁSICO
    // -------------------------------------------------------

    // GET /api/v1/audit-details
    @GetMapping
    public ResponseEntity<List<AuditDetail>> obtenerTodos() {
        return ResponseEntity.ok(detailService.obtenerTodos());
    }

    // GET /api/v1/audit-details/1
    @GetMapping("/{id}")
    public ResponseEntity<AuditDetail> obtenerPorId(
            @PathVariable Long id) {
        return ResponseEntity.ok(detailService.obtenerPorId(id));
    }

    // POST /api/v1/audit-details
    @PostMapping
    public ResponseEntity<AuditDetail> registrar(
            @Valid @RequestBody AuditDetailDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(detailService.registrar(dto));
    }

    // DELETE /api/v1/audit-details/1
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        detailService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    // -------------------------------------------------------
    // CONSULTAS DERIVADAS
    // -------------------------------------------------------

    // GET /api/v1/audit-details/auditoria/1
    @GetMapping("/auditoria/{auditId}")
    public ResponseEntity<List<AuditDetail>> obtenerPorAuditoria(
            @PathVariable Long auditId) {
        return ResponseEntity.ok(
                detailService.obtenerPorAuditoria(auditId));
    }

    // GET /api/v1/audit-details/resultado/SOSPECHOSO
    @GetMapping("/resultado/{resultado}")
    public ResponseEntity<List<AuditDetail>> obtenerPorResultado(
            @PathVariable String resultado) {
        return ResponseEntity.ok(
                detailService.obtenerPorResultado(resultado));
    }

    // GET /api/v1/audit-details/auditoria/1/resultado/FALLIDO
    @GetMapping("/auditoria/{auditId}/resultado/{resultado}")
    public ResponseEntity<List<AuditDetail>> obtenerPorAuditoriaYResultado(
            @PathVariable Long auditId,
            @PathVariable String resultado) {
        return ResponseEntity.ok(
                detailService.obtenerPorAuditoriaYResultado(
                        auditId, resultado));
    }

    // GET /api/v1/audit-details/usuario/12345678-9
    @GetMapping("/usuario/{rut}")
    public ResponseEntity<List<AuditDetail>> obtenerPorUsuario(
            @PathVariable String rut) {
        return ResponseEntity.ok(detailService.obtenerPorUsuario(rut));
    }

    // GET /api/v1/audit-details/usuario/12345678-9/sospechosos
    @GetMapping("/usuario/{rut}/sospechosos")
    public ResponseEntity<List<AuditDetail>> obtenerSospechosos(
            @PathVariable String rut) {
        return ResponseEntity.ok(
                detailService.obtenerSospechososPorUsuario(rut));
    }

    // GET /api/v1/audit-details/fechas?desde=...&hasta=...
    @GetMapping("/fechas")
    public ResponseEntity<List<AuditDetail>> obtenerPorFechas(
            @RequestParam String desde,
            @RequestParam String hasta) {
        LocalDateTime fechaDesde = LocalDateTime.parse(desde);
        LocalDateTime fechaHasta = LocalDateTime.parse(hasta);
        return ResponseEntity.ok(
                detailService.obtenerPorRangoFechas(fechaDesde, fechaHasta));
    }

    // GET /api/v1/audit-details/buscar?accion=login
    @GetMapping("/buscar")
    public ResponseEntity<List<AuditDetail>> buscarPorAccion(
            @RequestParam String accion) {
        return ResponseEntity.ok(detailService.buscarPorAccion(accion));
    }

    // GET /api/v1/audit-details/auditoria/1/ordenados
    @GetMapping("/auditoria/{auditId}/ordenados")
    public ResponseEntity<List<AuditDetail>> obtenerOrdenados(
            @PathVariable Long auditId) {
        return ResponseEntity.ok(
                detailService.obtenerPorAuditoriaOrdenados(auditId));
    }

    // GET /api/v1/audit-details/ultimos
    @GetMapping("/ultimos")
    public ResponseEntity<List<AuditDetail>> obtenerUltimos() {
        return ResponseEntity.ok(detailService.obtenerUltimosDetalles());
    }

    // GET /api/v1/audit-details/estadisticas/sospechosos/12345678-9
    @GetMapping("/estadisticas/sospechosos/{rut}")
    public ResponseEntity<Map<String, Long>> contarSospechosos(
            @PathVariable String rut) {
        long total = detailService.contarSospechososPorUsuario(rut);
        return ResponseEntity.ok(Map.of("total", total));
    }
}