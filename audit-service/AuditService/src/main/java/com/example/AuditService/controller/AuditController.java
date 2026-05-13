// controller/AuditController.java
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

@RestController
@RequestMapping("/api/v1/audits")
@RequiredArgsConstructor
public class AuditController {

    private final AuditService auditService;

    // -------------------------------------------------------
    // CRUD BÁSICO
    // -------------------------------------------------------

    // GET /api/v1/audits
    @GetMapping
    public ResponseEntity<List<Audit>> obtenerTodas() {
        return ResponseEntity.ok(auditService.obtenerTodas());
    }

    // GET /api/v1/audits/1
    @GetMapping("/{id}")
    public ResponseEntity<Audit> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(auditService.obtenerPorId(id));
    }

    // POST /api/v1/audits
    @PostMapping
    public ResponseEntity<Audit> registrar(
            @Valid @RequestBody AuditDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(auditService.registrar(dto));
    }

    // PATCH /api/v1/audits/1/completar
    @PatchMapping("/{id}/completar")
    public ResponseEntity<Audit> completar(@PathVariable Long id) {
        return ResponseEntity.ok(auditService.completar(id));
    }

    // PATCH /api/v1/audits/1/observacion
    @PatchMapping("/{id}/observacion")
    public ResponseEntity<Audit> marcarObservacion(
            @PathVariable Long id) {
        return ResponseEntity.ok(auditService.marcarObservacion(id));
    }

    // DELETE /api/v1/audits/1
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        auditService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    // -------------------------------------------------------
    // CONSULTAS DERIVADAS
    // -------------------------------------------------------

    // GET /api/v1/audits/auditor/12345678-9
    @GetMapping("/auditor/{rut}")
    public ResponseEntity<List<Audit>> obtenerPorAuditor(
            @PathVariable String rut) {
        return ResponseEntity.ok(auditService.obtenerPorAuditor(rut));
    }

    // GET /api/v1/audits/tipo/USUARIO
    @GetMapping("/tipo/{tipo}")
    public ResponseEntity<List<Audit>> obtenerPorTipo(
            @PathVariable String tipo) {
        return ResponseEntity.ok(auditService.obtenerPorTipo(tipo));
    }

    // GET /api/v1/audits/estado/EN_PROCESO
    @GetMapping("/estado/{estado}")
    public ResponseEntity<List<Audit>> obtenerPorEstado(
            @PathVariable String estado) {
        return ResponseEntity.ok(auditService.obtenerPorEstado(estado));
    }

    // GET /api/v1/audits/entidad/User Service
    @GetMapping("/entidad/{entidad}")
    public ResponseEntity<List<Audit>> obtenerPorEntidad(
            @PathVariable String entidad) {
        return ResponseEntity.ok(auditService.obtenerPorEntidad(entidad));
    }

    // GET /api/v1/audits/entidad/User Service/1
    @GetMapping("/entidad/{entidad}/{entidadId}")
    public ResponseEntity<List<Audit>> obtenerPorEntidadYId(
            @PathVariable String entidad,
            @PathVariable Long entidadId) {
        return ResponseEntity.ok(
                auditService.obtenerPorEntidadYId(entidad, entidadId));
    }

    // GET /api/v1/audits/fechas?desde=...&hasta=...
    @GetMapping("/fechas")
    public ResponseEntity<List<Audit>> obtenerPorFechas(
            @RequestParam String desde,
            @RequestParam String hasta) {
        LocalDateTime fechaDesde = LocalDateTime.parse(desde);
        LocalDateTime fechaHasta = LocalDateTime.parse(hasta);
        return ResponseEntity.ok(
                auditService.obtenerPorRangoFechas(fechaDesde, fechaHasta));
    }

    // GET /api/v1/audits/auditor/12345678-9/ordenadas
    @GetMapping("/auditor/{rut}/ordenadas")
    public ResponseEntity<List<Audit>> obtenerPorAuditorOrdenadas(
            @PathVariable String rut) {
        return ResponseEntity.ok(
                auditService.obtenerPorAuditorOrdenadas(rut));
    }

    // GET /api/v1/audits/en-proceso/ordenadas
    @GetMapping("/en-proceso/ordenadas")
    public ResponseEntity<List<Audit>> obtenerEnProcesoOrdenadas() {
        return ResponseEntity.ok(
                auditService.obtenerEnProcesoOrdenadas());
    }

    // GET /api/v1/audits/ultimas
    @GetMapping("/ultimas")
    public ResponseEntity<List<Audit>> obtenerUltimas() {
        return ResponseEntity.ok(
                auditService.obtenerUltimasAuditorias());
    }

    // GET /api/v1/audits/estadisticas/estado/COMPLETADA
    @GetMapping("/estadisticas/estado/{estado}")
    public ResponseEntity<Map<String, Long>> contarPorEstado(
            @PathVariable String estado) {
        long total = auditService.contarPorEstado(estado);
        return ResponseEntity.ok(Map.of("total", total));
    }

    // GET /api/v1/audits/estadisticas/tipo/USUARIO
    @GetMapping("/estadisticas/tipo/{tipo}")
    public ResponseEntity<Map<String, Long>> contarPorTipo(
            @PathVariable String tipo) {
        long total = auditService.contarPorTipo(tipo);
        return ResponseEntity.ok(Map.of("total", total));
    }
}