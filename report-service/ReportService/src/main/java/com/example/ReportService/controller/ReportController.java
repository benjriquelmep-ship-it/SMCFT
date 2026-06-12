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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@RestController
@RequestMapping("/api/v1/reports")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class ReportController {

    private final ReportService reportService;

    // GET /api/v1/reports : Lista todos los reportes
    @Operation(summary = "Obtener Todos", description = "GET /api/v1/reports : Lista todos los reportes")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operación exitosa"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "Sin permisos")
    })
    @GetMapping
    public ResponseEntity<List<Report>> obtenerTodos() {
        return ResponseEntity.ok(reportService.obtenerTodos());
    }

    // GET /api/v1/reports/1 : Busca un reporte por su ID
    @Operation(summary = "Obtener Por Id", description = "GET /api/v1/reports/1 : Busca un reporte por su ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operación exitosa"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "Sin permisos")
    })
    @GetMapping("/{id}")
    public ResponseEntity<Report> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(reportService.obtenerPorId(id));
    }

    // POST /api/v1/reports : Crea e inicia la generación de un nuevo reporte
    @Operation(summary = "Generar", description = "POST /api/v1/reports : Crea e inicia la generación de un nuevo reporte")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Operación exitosa"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "Sin permisos")
    })
    @PostMapping
    public ResponseEntity<Report> generar(
            @Valid @RequestBody ReportDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(reportService.generar(dto));
    }

    // PATCH /api/v1/reports/1/completar : Cambia el estado del reporte a COMPLETADO
    @Operation(summary = "Completar", description = "PATCH /api/v1/reports/1/completar : Cambia el estado del reporte a COMPLETADO")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operación exitosa"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "Sin permisos")
    })
    @PatchMapping("/{id}/completar")
    public ResponseEntity<Report> completar(@PathVariable Long id) {
        return ResponseEntity.ok(reportService.completar(id));
    }

    // PATCH /api/v1/reports/1/error : Registra una falla en el reporte con observaciones
    @Operation(summary = "Marcar Error", description = "PATCH /api/v1/reports/1/error : Registra una falla en el reporte con observaciones")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operación exitosa"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "Sin permisos")
    })
    @PatchMapping("/{id}/error")
    public ResponseEntity<Report> marcarError(
            @PathVariable Long id,
            @RequestParam(required = false) String observaciones) {
        return ResponseEntity.ok(
                reportService.marcarError(id, observaciones));
    }

    // DELETE /api/v1/reports/1 : Elimina físicamente un reporte por su ID
    @Operation(summary = "Eliminar", description = "DELETE /api/v1/reports/1 : Elimina físicamente un reporte por su ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Operación exitosa"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "Sin permisos")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        reportService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    // GET /api/v1/reports/tipo/CRUCE_FRONTERIZO : Filtra reportes por su categoría o tipo
    @Operation(summary = "Obtener Por Tipo", description = "GET /api/v1/reports/tipo/CRUCE_FRONTERIZO : Filtra reportes por su categoría o tipo")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operación exitosa"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "Sin permisos")
    })
    @GetMapping("/tipo/{tipoReporte}")
    public ResponseEntity<List<Report>> obtenerPorTipo(
            @PathVariable String tipoReporte) {
        return ResponseEntity.ok(
                reportService.obtenerPorTipo(tipoReporte));
    }

    // GET /api/v1/reports/generador/12345678-9 : Filtra reportes creados por el RUT de un usuario
    @Operation(summary = "Obtener Por Generador", description = "GET /api/v1/reports/generador/12345678-9 : Filtra reportes creados por el RUT de un usuario")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operación exitosa"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "Sin permisos")
    })
    @GetMapping("/generador/{rut}")
    public ResponseEntity<List<Report>> obtenerPorGenerador(
            @PathVariable String rut) {
        return ResponseEntity.ok(
                reportService.obtenerPorGenerador(rut));
    }

    // GET /api/v1/reports/estado/ : Filtra reportes según su estado actual
    @Operation(summary = "Obtener Por Estado", description = "GET /api/v1/reports/estado/ : Filtra reportes según su estado actual")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operación exitosa"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "Sin permisos")
    })
    @GetMapping("/estado/{estado}")
    public ResponseEntity<List<Report>> obtenerPorEstado(
            @PathVariable String estado) {
        return ResponseEntity.ok(
                reportService.obtenerPorEstado(estado));
    }

    // GET /api/v1/reports/tipo/CRUCE_FRONTERIZO/completados : Obtiene reportes finalizados de un tipo específico
    @Operation(summary = "Obtener Completados Por Tipo", description = "GET /api/v1/reports/tipo/CRUCE_FRONTERIZO/completados : Obtiene reportes finalizados de un tipo específico")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operación exitosa"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "Sin permisos")
    })
    @GetMapping("/tipo/{tipoReporte}/completados")
    public ResponseEntity<List<Report>> obtenerCompletadosPorTipo(
            @PathVariable String tipoReporte) {
        return ResponseEntity.ok(
                reportService.obtenerCompletadosPorTipo(tipoReporte));
    }

    // GET /api/v1/reports/buscar?titulo=frontera : Busca reportes que contengan el texto en su título
    @Operation(summary = "Buscar Por Titulo", description = "GET /api/v1/reports/buscar?titulo=frontera : Busca reportes que contengan el texto en su título")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operación exitosa"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "Sin permisos")
    })
    @GetMapping("/buscar")
    public ResponseEntity<List<Report>> buscarPorTitulo(
            @RequestParam String titulo) {
        return ResponseEntity.ok(
                reportService.buscarPorTitulo(titulo));
    }

    // GET /api/v1/reports/ultimos : Devuelve los últimos 10 reportes registrados
    @Operation(summary = "Obtener Ultimos", description = "GET /api/v1/reports/ultimos : Devuelve los últimos 10 reportes registrados")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operación exitosa"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "Sin permisos")
    })
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