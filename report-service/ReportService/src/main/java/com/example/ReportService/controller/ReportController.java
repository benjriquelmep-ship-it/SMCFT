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
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/v1/reports")
@RequiredArgsConstructor
@Tag(name = "Reportes Consolidados", description = "Endpoints para la solicitud, procesamiento esatdístico y auditoría de informes gerenciales fronterizos")
@SecurityRequirement(name = "bearerAuth")
public class ReportController {

    private final ReportService reportService;

    @Operation(summary = "Listar reportes consolidados", description = "Recupera el catálogo histórico de todos los informes analíticos solicitados en la plataforma.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Colección de reportes recuperada con éxito"),
            @ApiResponse(responseCode = "401", description = "No autenticado - Falta token Bearer JWT"),
            @ApiResponse(responseCode = "403", description = "Sin permisos - Rol administrativo insuficiente")
    })
    @GetMapping
    public ResponseEntity<List<Report>> obtenerTodos() {
        return ResponseEntity.ok(reportService.obtenerTodos());
    }

    // GET /api/v1/reports/1 : Busca un reporte por su ID
    @Operation(summary = "Obtener reporte por ID", description = "Busca el estado de procesamiento y metadatos de un informe a partir de su clave primaria.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Informe localizado correctamente"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "404", description = "El ID de reporte solicitado no existe")
    })
    @GetMapping("/{id}")
    public ResponseEntity<Report> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(reportService.obtenerPorId(id));
    }

    // POST /api/v1/reports : Crea e inicia la generación de un nuevo reporte
    @Operation(summary = "Gatillar compilación de reporte", description = "Valida los rangos temporales del payload y pone en cola la generación de un nuevo informe analítico.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Orden de compilación de reporte aceptada y creada"),
            @ApiResponse(responseCode = "400", description = "Rango de fechas inválido o estructura DTO incorrecta") // 🚨 Reparado: cambiado 'message' por 'description'
    })
    @PostMapping
    public ResponseEntity<Report> generar(@Valid @RequestBody ReportDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(reportService.generar(dto));
    }

    // PATCH /api/v1/reports/1/completar : Cambia el estado del reporte a COMPLETADO
    @Operation(summary = "Marcar reporte como Completado", description = "Actualiza el estado del informe a COMPLETADO una vez consolidada la recolección de métricas.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Estado del reporte actualizado con éxito"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "404", description = "El ID de reporte especificado no existe")
    })
    @PatchMapping("/{id}/completar")
    public ResponseEntity<Report> completar(@PathVariable Long id) {
        return ResponseEntity.ok(reportService.completar(id));
    }

    // PATCH /api/v1/reports/1/error : Registra una falla en el reporte con observaciones
    @Operation(summary = "Registrar falla en reporte", description = "Interrumpe el procesamiento del informe marcándolo en estado ERROR e inyectando la traza técnica de la falla.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Reporte marcado con estado de error de forma correcta"),
            @ApiResponse(responseCode = "401", description = "No autenticado")
    })
    @PatchMapping("/{id}/error")
    public ResponseEntity<Report> marcarError(
            @PathVariable Long id,
            @RequestParam(required = false) String observaciones) {
        return ResponseEntity.ok(
                reportService.marcarError(id, observaciones));
    }

    // DELETE /api/v1/reports/1 : Elimina físicamente un reporte por su ID
    @Operation(summary = "Eliminar reporte físicamente", description = "Remueve permanentemente el registro del informe y sus desgloses métricos de la base de datos central.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Reporte analítico purgado con éxito. Sin contenido."),
            @ApiResponse(responseCode = "401", description = "No autenticado")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        reportService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    // GET /api/v1/reports/tipo/CRUCE_FRONTERIZO : Filtra reportes por su categoría o tipo
    @Operation(summary = "Listar reportes por Área de estudio", description = "Agrupa e identifica los informes según su módulo de origen (ej: CRUCE_FRONTERIZO, VEHICULOS).")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Informes filtrados devueltos de manera exitosa"),
            @ApiResponse(responseCode = "401", description = "No autenticado")
    })
    @GetMapping("/tipo/{tipoReporte}")
    public ResponseEntity<List<Report>> obtenerPorTipo(@PathVariable String tipoReporte) {
        return ResponseEntity.ok(reportService.obtenerPorTipo(tipoReporte));
    }

    // GET /api/v1/reports/generador/12345678-9 : Filtra reportes creados por el RUT de un usuario
    @Operation(summary = "Listar reportes por Funcionario emisor", description = "Filtra la bitácora analítica extrayendo los informes solicitados por el RUN de un administrador o jefe de aduana.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Extracto de informes del emisor devuelto correctamente"),
            @ApiResponse(responseCode = "401", description = "No autenticado")
    })
    @GetMapping("/generador/{rut}")
    public ResponseEntity<List<Report>> obtenerPorGenerador(@PathVariable String rut) {
        return ResponseEntity.ok(reportService.obtenerPorGenerador(rut));
    }

    // GET /api/v1/reports/estado/ : Filtra reportes según su estado actual
    @Operation(summary = "Listar reportes por Estado de proceso", description = "Agrupa las solicitudes analíticas según su situación de compilación (GENERANDO, COMPLETADO, ERROR).")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Colección agrupada devuelta de forma exitosa"),
            @ApiResponse(responseCode = "401", description = "No autenticado")
    })
    @GetMapping("/estado/{estado}")
    public ResponseEntity<List<Report>> obtenerPorEstado(@PathVariable String estado) {
        return ResponseEntity.ok(reportService.obtenerPorEstado(estado));
    }

    // GET /api/v1/reports/tipo/CRUCE_FRONTERIZO/completados : Obtiene reportes finalizados de un tipo específico
    @Operation(summary = "Listar reportes Completados por Tipo", description = "Aísla y retorna exclusivamente los informes finalizados con éxito pertenecientes a un área analítica específica.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Informes cerrados recuperados con éxito"),
            @ApiResponse(responseCode = "401", description = "No autenticado")
    })
    @GetMapping("/tipo/{tipoReporte}/completados")
    public ResponseEntity<List<Report>> obtenerCompletadosPorTipo(@PathVariable String tipoReporte) {
        return ResponseEntity.ok(reportService.obtenerCompletadosPorTipo(tipoReporte));
    }

    // GET /api/v1/reports/buscar?titulo=frontera : Busca reportes que contengan el texto en su título
    @Operation(summary = "Buscar reportes por Coincidencia de título", description = "Realiza una consulta de texto parcial (LIKE) sobre la columna del título institucional del documento.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Coincidencias de búsqueda devueltas con éxito"),
            @ApiResponse(responseCode = "401", description = "No autenticado")
    })
    @GetMapping("/buscar")
    public ResponseEntity<List<Report>> buscarPorTitulo(@RequestParam String titulo) {
        return ResponseEntity.ok(reportService.buscarPorTitulo(titulo));
    }

    // GET /api/v1/reports/ultimos : Devuelve los últimos 10 reportes registrados
    @Operation(summary = "Listar últimas solicitudes de reportes", description = "Endpoint operativo que expone las últimas 10 transacciones analíticas solicitadas a nivel global.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Historial inmediato de auditoría recuperado con éxito"),
            @ApiResponse(responseCode = "401", description = "No autenticado")
    })
    @GetMapping("/ultimos")
    public ResponseEntity<List<Report>> obtenerUltimos() {
        return ResponseEntity.ok(reportService.obtenerUltimosReportes());
    }

    // GET /api/v1/reports/estadisticas/estado/COMPLETADO : Cuenta el total de reportes en un estado
    @Operation(summary = "Contar volumen de reportes por Estado", description = "Endpoint analítico de reportería que devuelve la sumatoria total volumétrica de solicitudes en una situación dada.")
    @GetMapping("/estadisticas/estado/{estado}")
    public ResponseEntity<Map<String, Long>> contarPorEstado(@PathVariable String estado) {
        long total = reportService.contarPorEstado(estado);
        return ResponseEntity.ok(Map.of("total", total));
    }

    // GET /api/v1/reports/estadisticas/tipo/CRUCE_FRONTERIZO : Cuenta el total de reportes de un tipo
    @Operation(summary = "Contar volumen de reportes por Tipo", description = "Endpoint analítico de reportería que devuelve la sumatoria total volumétrica de solicitudes emitidas bajo una categoría.")
    @GetMapping("/estadisticas/tipo/{tipo}")
    public ResponseEntity<Map<String, Long>> contarPorTipo(@PathVariable String tipo) {
        long total = reportService.contarPorTipo(tipo);
        return ResponseEntity.ok(Map.of("total", total));
    }
}