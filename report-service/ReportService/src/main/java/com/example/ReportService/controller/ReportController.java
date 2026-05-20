// controller/ReportController.java
package com.example.ReportService.controller;

import com.example.ReportService.dto.ReportDTO;
import com.example.ReportService.model.Report;
import com.example.ReportService.service.ReportService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/reports")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    // GET /api/v1/reports : Lista todos los reportes
    @GetMapping
    public ResponseEntity<List<Report>> obtenerTodos() {
        return ResponseEntity.ok(reportService.obtenerTodos());
    }

    // GET /api/v1/reports/1 : Busca un reporte por su ID
    @GetMapping("/{id}")
    public ResponseEntity<Report> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(reportService.obtenerPorId(id));
    }

    // POST /api/v1/reports : Crea e inicia la generación de un nuevo reporte
    @PostMapping
    public ResponseEntity<Report> generar(
            @Valid @RequestBody ReportDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(reportService.generar(dto));
    }

    // PATCH /api/v1/reports/1/completar : Cambia el estado del reporte a COMPLETADO
    @PatchMapping("/{id}/completar")
    public ResponseEntity<Report> completar(@PathVariable Long id) {
        return ResponseEntity.ok(reportService.completar(id));
    }

    // PATCH /api/v1/reports/1/error : Registra una falla en el reporte con observaciones
    @PatchMapping("/{id}/error")
    public ResponseEntity<Report> marcarError(
            @PathVariable Long id,
            @RequestParam(required = false) String observaciones) {
        return ResponseEntity.ok(
                reportService.marcarError(id, observaciones));
    }

    // DELETE /api/v1/reports/1 : Elimina físicamente un reporte por su ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        reportService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    // GET /api/v1/reports/tipo/CRUCE_FRONTERIZO : Filtra reportes por su categoría o tipo
    @GetMapping("/tipo/{tipoReporte}")
    public ResponseEntity<List<Report>> obtenerPorTipo(
            @PathVariable String tipoReporte) {
        return ResponseEntity.ok(
                reportService.obtenerPorTipo(tipoReporte));
    }

    // GET /api/v1/reports/generador/12345678-9 : Filtra reportes creados por el RUT de un usuario
    @GetMapping("/generador/{rut}")
    public ResponseEntity<List<Report>> obtenerPorGenerador(
            @PathVariable String rut) {
        return ResponseEntity.ok(
                reportService.obtenerPorGenerador(rut));
    }

    // GET /api/v1/reports/estado/ : Filtra reportes según su estado actual
    @GetMapping("/estado/{estado}")
    public ResponseEntity<List<Report>> obtenerPorEstado(
            @PathVariable String estado) {
        return ResponseEntity.ok(
                reportService.obtenerPorEstado(estado));
    }

    // GET /api/v1/reports/tipo/CRUCE_FRONTERIZO/completados : Obtiene reportes finalizados de un tipo específico
    @GetMapping("/tipo/{tipoReporte}/completados")
    public ResponseEntity<List<Report>> obtenerCompletadosPorTipo(
            @PathVariable String tipoReporte) {
        return ResponseEntity.ok(
                reportService.obtenerCompletadosPorTipo(tipoReporte));
    }

    // GET /api/v1/reports/buscar?titulo=frontera : Busca reportes que contengan el texto en su título
    @GetMapping("/buscar")
    public ResponseEntity<List<Report>> buscarPorTitulo(
            @RequestParam String titulo) {
        return ResponseEntity.ok(
                reportService.buscarPorTitulo(titulo));
    }

    // GET /api/v1/reports/ultimos : Devuelve los últimos 10 reportes registrados
    @GetMapping("/ultimos")
    public ResponseEntity<List<Report>> obtenerUltimos() {
        return ResponseEntity.ok(
                reportService.obtenerUltimosReportes());
    }

    // GET /api/v1/reports/estadisticas/estado/COMPLETADO : Cuenta el total de reportes en un estado
    @GetMapping("/estadisticas/estado/{estado}")
    public ResponseEntity<Map<String, Long>> contarPorEstado(
            @PathVariable String estado) {
        long total = reportService.contarPorEstado(estado);
        return ResponseEntity.ok(Map.of("total", total));
    }

    // GET /api/v1/reports/estadisticas/tipo/CRUCE_FRONTERIZO : Cuenta el total de reportes de un tipo
    @GetMapping("/estadisticas/tipo/{tipo}")
    public ResponseEntity<Map<String, Long>> contarPorTipo(
            @PathVariable String tipo) {
        long total = reportService.contarPorTipo(tipo);
        return ResponseEntity.ok(Map.of("total", total));
    }
}