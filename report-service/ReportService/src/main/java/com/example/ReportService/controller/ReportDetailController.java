// Recibe peticiones HTTP para gestionar las líneas de detalle de los reportes
// Delega la persistencia al ReportDetailService y retorna formato JSON
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

    // GET /api/v1/report-details : Lista todos los detalles de reportes del sistema
    @GetMapping
    public ResponseEntity<List<ReportDetail>> obtenerTodos() {
        return ResponseEntity.ok(detailService.obtenerTodos());
    }

    // GET /api/v1/report-details/1 : Busca un detalle específico por su ID
    @GetMapping("/{id}")
    public ResponseEntity<ReportDetail> obtenerPorId(
            @PathVariable Long id) {
        return ResponseEntity.ok(detailService.obtenerPorId(id));
    }

    // POST /api/v1/report-details : Agrega una nueva línea de detalle a un reporte
    @PostMapping
    public ResponseEntity<ReportDetail> agregar(
            @Valid @RequestBody ReportDetailDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(detailService.agregar(dto));
    }

    // PUT /api/v1/report-details/1 : Actualiza por completo un detalle existente por su ID
    @PutMapping("/{id}")
    public ResponseEntity<ReportDetail> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody ReportDetailDTO dto) {
        return ResponseEntity.ok(detailService.actualizar(id, dto));
    }

    // DELETE /api/v1/report-details/1 : Elimina un detalle físico por su ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        detailService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    // GET /api/v1/report-details/reporte/1 : Filtra todos los detalles asociados a un reporte padre
    @GetMapping("/reporte/{reportId}")
    public ResponseEntity<List<ReportDetail>> obtenerPorReporte(
            @PathVariable Long reportId) {
        return ResponseEntity.ok(
                detailService.obtenerPorReporte(reportId));
    }

    // GET /api/v1/report-details/categoria/AUTORIZADOS : Filtra detalles por categoría o etiqueta
    @GetMapping("/categoria/{categoria}")
    public ResponseEntity<List<ReportDetail>> obtenerPorCategoria(
            @PathVariable String categoria) {
        return ResponseEntity.ok(
                detailService.obtenerPorCategoria(categoria));
    }

    // GET /api/v1/report-details/reporte/1/categoria/AUTORIZADOS : Combina filtros: ID de reporte + categoría
    @GetMapping("/reporte/{reportId}/categoria/{categoria}")
    public ResponseEntity<List<ReportDetail>> obtenerPorReporteYCategoria(
            @PathVariable Long reportId,
            @PathVariable String categoria) {
        return ResponseEntity.ok(
                detailService.obtenerPorReporteYCategoria(
                        reportId, categoria));
    }

    // GET /api/v1/report-details/buscar?descripcion=cruces : Busca detalles que contengan el texto en su descripción
    @GetMapping("/buscar")
    public ResponseEntity<List<ReportDetail>> buscarPorDescripcion(
            @RequestParam String descripcion) {
        return ResponseEntity.ok(
                detailService.buscarPorDescripcion(descripcion));
    }

    // GET /api/v1/report-details/reporte/1/ordenados : Lista los detalles de un reporte de forma secuencial u ordenada
    @GetMapping("/reporte/{reportId}/ordenados")
    public ResponseEntity<List<ReportDetail>> obtenerOrdenados(
            @PathVariable Long reportId) {
        return ResponseEntity.ok(
                detailService.obtenerPorReporteOrdenados(reportId));
    }

    // GET /api/v1/report-details/ultimos : Devuelve los últimos 10 detalles registrados en la base de datos
    @GetMapping("/ultimos")
    public ResponseEntity<List<ReportDetail>> obtenerUltimos() {
        return ResponseEntity.ok(detailService.obtenerUltimosDetalles());
    }
}