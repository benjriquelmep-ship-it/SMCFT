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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@RestController
@RequestMapping("/api/v1/report-details")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class ReportDetailController {

    private final ReportDetailService detailService;

    // GET /api/v1/report-details : Lista todos los detalles de reportes del sistema
    @Operation(summary = "Obtener Todos", description = "GET /api/v1/report-details : Lista todos los detalles de reportes del sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operación exitosa"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "Sin permisos")
    })
    @GetMapping
    public ResponseEntity<List<ReportDetail>> obtenerTodos() {
        return ResponseEntity.ok(detailService.obtenerTodos());
    }

    // GET /api/v1/report-details/1 : Busca un detalle específico por su ID
    @Operation(summary = "Obtener Por Id", description = "GET /api/v1/report-details/1 : Busca un detalle específico por su ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operación exitosa"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "Sin permisos")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ReportDetail> obtenerPorId(
            @PathVariable Long id) {
        return ResponseEntity.ok(detailService.obtenerPorId(id));
    }

    // POST /api/v1/report-details : Agrega una nueva línea de detalle a un reporte
    @Operation(summary = "Agregar", description = "POST /api/v1/report-details : Agrega una nueva línea de detalle a un reporte")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Operación exitosa"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "Sin permisos")
    })
    @PostMapping
    public ResponseEntity<ReportDetail> agregar(
            @Valid @RequestBody ReportDetailDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(detailService.agregar(dto));
    }

    // PUT /api/v1/report-details/1 : Actualiza por completo un detalle existente por su ID
    @Operation(summary = "Actualizar", description = "PUT /api/v1/report-details/1 : Actualiza por completo un detalle existente por su ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operación exitosa"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "Sin permisos")
    })
    @PutMapping("/{id}")
    public ResponseEntity<ReportDetail> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody ReportDetailDTO dto) {
        return ResponseEntity.ok(detailService.actualizar(id, dto));
    }

    // DELETE /api/v1/report-details/1 : Elimina un detalle físico por su ID
    @Operation(summary = "Eliminar", description = "DELETE /api/v1/report-details/1 : Elimina un detalle físico por su ID")
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

    // GET /api/v1/report-details/reporte/1 : Filtra todos los detalles asociados a un reporte padre
    @Operation(summary = "Obtener Por Reporte", description = "GET /api/v1/report-details/reporte/1 : Filtra todos los detalles asociados a un reporte padre")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operación exitosa"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "Sin permisos")
    })
    @GetMapping("/reporte/{reportId}")
    public ResponseEntity<List<ReportDetail>> obtenerPorReporte(
            @PathVariable Long reportId) {
        return ResponseEntity.ok(
                detailService.obtenerPorReporte(reportId));
    }

    // GET /api/v1/report-details/categoria/AUTORIZADOS : Filtra detalles por categoría o etiqueta
    @Operation(summary = "Obtener Por Categoria", description = "GET /api/v1/report-details/categoria/AUTORIZADOS : Filtra detalles por categoría o etiqueta")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operación exitosa"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "Sin permisos")
    })
    @GetMapping("/categoria/{categoria}")
    public ResponseEntity<List<ReportDetail>> obtenerPorCategoria(
            @PathVariable String categoria) {
        return ResponseEntity.ok(
                detailService.obtenerPorCategoria(categoria));
    }

    // GET /api/v1/report-details/reporte/1/categoria/AUTORIZADOS : Combina filtros: ID de reporte + categoría
    @Operation(summary = "Obtener Por Reporte Y Categoria", description = "GET /api/v1/report-details/reporte/1/categoria/AUTORIZADOS : Combina filtros: ID de reporte + categoría")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operación exitosa"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "Sin permisos")
    })
    @GetMapping("/reporte/{reportId}/categoria/{categoria}")
    public ResponseEntity<List<ReportDetail>> obtenerPorReporteYCategoria(
            @PathVariable Long reportId,
            @PathVariable String categoria) {
        return ResponseEntity.ok(
                detailService.obtenerPorReporteYCategoria(
                        reportId, categoria));
    }

    // GET /api/v1/report-details/buscar?descripcion=cruces : Busca detalles que contengan el texto en su descripción
    @Operation(summary = "Buscar Por Descripcion", description = "GET /api/v1/report-details/buscar?descripcion=cruces : Busca detalles que contengan el texto en su descripción")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operación exitosa"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "Sin permisos")
    })
    @GetMapping("/buscar")
    public ResponseEntity<List<ReportDetail>> buscarPorDescripcion(
            @RequestParam String descripcion) {
        return ResponseEntity.ok(
                detailService.buscarPorDescripcion(descripcion));
    }

    // GET /api/v1/report-details/reporte/1/ordenados : Lista los detalles de un reporte de forma secuencial u ordenada
    @Operation(summary = "Obtener Ordenados", description = "GET /api/v1/report-details/reporte/1/ordenados : Lista los detalles de un reporte de forma secuencial u ordenada")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operación exitosa"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "Sin permisos")
    })
    @GetMapping("/reporte/{reportId}/ordenados")
    public ResponseEntity<List<ReportDetail>> obtenerOrdenados(
            @PathVariable Long reportId) {
        return ResponseEntity.ok(
                detailService.obtenerPorReporteOrdenados(reportId));
    }

    // GET /api/v1/report-details/ultimos : Devuelve los últimos 10 detalles registrados en la base de datos
    @Operation(summary = "Obtener Ultimos", description = "GET /api/v1/report-details/ultimos : Devuelve los últimos 10 detalles registrados en la base de datos")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operación exitosa"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "Sin permisos")
    })
    @GetMapping("/ultimos")
    public ResponseEntity<List<ReportDetail>> obtenerUltimos() {
        return ResponseEntity.ok(detailService.obtenerUltimosDetalles());
    }
}