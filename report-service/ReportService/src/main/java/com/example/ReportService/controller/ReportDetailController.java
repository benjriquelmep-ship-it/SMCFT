// controller/ReportDetailController.java
package com.example.ReportService.controller;

import com.example.ReportService.dto.ReportDetailDTO;
import com.example.ReportService.model.ReportDetail;
import com.example.ReportService.service.ReportDetailService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/report-details")
@RequiredArgsConstructor
public class ReportDetailController {

    private final ReportDetailService detailService;

    // GET /api/v1/report-details
    @GetMapping
    public ResponseEntity<List<ReportDetail>> obtenerTodos() {
        return ResponseEntity.ok(detailService.obtenerTodos());
    }

    // GET /api/v1/report-details/1
    @GetMapping("/{id}")
    public ResponseEntity<ReportDetail> obtenerPorId(
            @PathVariable Long id) {
        return ResponseEntity.ok(detailService.obtenerPorId(id));
    }

    // POST /api/v1/report-details
    @PostMapping
    public ResponseEntity<ReportDetail> agregar(
            @Valid @RequestBody ReportDetailDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(detailService.agregar(dto));
    }

    // PUT /api/v1/report-details/1
    @PutMapping("/{id}")
    public ResponseEntity<ReportDetail> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody ReportDetailDTO dto) {
        return ResponseEntity.ok(detailService.actualizar(id, dto));
    }

    // DELETE /api/v1/report-details/1
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        detailService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    // GET /api/v1/report-details/reporte/1
    @GetMapping("/reporte/{reportId}")
    public ResponseEntity<List<ReportDetail>> obtenerPorReporte(
            @PathVariable Long reportId) {
        return ResponseEntity.ok(
                detailService.obtenerPorReporte(reportId));
    }

    // GET /api/v1/report-details/categoria/AUTORIZADOS
    @GetMapping("/categoria/{categoria}")
    public ResponseEntity<List<ReportDetail>> obtenerPorCategoria(
            @PathVariable String categoria) {
        return ResponseEntity.ok(
                detailService.obtenerPorCategoria(categoria));
    }

    // GET /api/v1/report-details/reporte/1/categoria/AUTORIZADOS
    @GetMapping("/reporte/{reportId}/categoria/{categoria}")
    public ResponseEntity<List<ReportDetail>> obtenerPorReporteYCategoria(
            @PathVariable Long reportId,
            @PathVariable String categoria) {
        return ResponseEntity.ok(
                detailService.obtenerPorReporteYCategoria(
                        reportId, categoria));
    }

    // GET /api/v1/report-details/buscar?descripcion=cruces
    @GetMapping("/buscar")
    public ResponseEntity<List<ReportDetail>> buscarPorDescripcion(
            @RequestParam String descripcion) {
        return ResponseEntity.ok(
                detailService.buscarPorDescripcion(descripcion));
    }

    // GET /api/v1/report-details/reporte/1/ordenados
    @GetMapping("/reporte/{reportId}/ordenados")
    public ResponseEntity<List<ReportDetail>> obtenerOrdenados(
            @PathVariable Long reportId) {
        return ResponseEntity.ok(
                detailService.obtenerPorReporteOrdenados(reportId));
    }

    // GET /api/v1/report-details/ultimos
    @GetMapping("/ultimos")
    public ResponseEntity<List<ReportDetail>> obtenerUltimos() {
        return ResponseEntity.ok(detailService.obtenerUltimosDetalles());
    }
}