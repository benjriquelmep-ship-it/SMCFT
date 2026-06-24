package com.example.SanitaryService.controller;

import com.example.SanitaryService.dto.SanitaryDTO;
import com.example.SanitaryService.model.Sanitary;
import com.example.SanitaryService.service.SanitaryService;
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
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/v1/sanitary")
@RequiredArgsConstructor
@Tag(name = "Controles Sanitarios", description = "Endpoints para la apertura, dictamen y auditoría de inspecciones fitozoosanitarias en pasos fronterizos")
@SecurityRequirement(name = "bearerAuth")
public class SanitaryController {

    private final SanitaryService sanitaryService;

    @Operation(summary = "Listar todas las inspecciones sanitarias", description = "Recupera la colección completa de órdenes de inspección y fiscalizaciones de sanidad ejecutadas en el sistema.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Colección de inspecciones sanitarias obtenida con éxito"),
            @ApiResponse(responseCode = "401", description = "No autenticado - Falta token Bearer JWT"),
            @ApiResponse(responseCode = "403", description = "Sin permisos - Rol fiscalizador insuficiente")
    })
    @GetMapping
    public ResponseEntity<List<Sanitary>> obtenerTodas() {
        return ResponseEntity.ok(sanitaryService.obtenerTodas());
    }

    // GET /api/v1/sanitary/1
    @Operation(summary = "Obtener inspección sanitaria por ID", description = "Busca los datos de cabecera de una fiscalización de salud a partir de su clave primaria.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Inspección sanitaria localizada correctamente"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "404", description = "El ID de inspección solicitado no existe")
    })
    @GetMapping("/{id}")
    public ResponseEntity<Sanitary> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(sanitaryService.obtenerPorId(id));
    }

    // POST /api/v1/sanitary
    @Operation(summary = "Registrar nueva inspección sanitaria", description = "Abre una nueva orden de fiscalización fitozoosanitaria para un vehículo y su carga, fijando su estado inicial en PENDIENTE.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Orden de inspección sanitaria abierta exitosamente"),
            @ApiResponse(responseCode = "400", description = "Payload inválido o inconsistencia en los RUT/Patente inyectados")
    })
    @PostMapping
    public ResponseEntity<Sanitary> registrar(@Valid @RequestBody SanitaryDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(sanitaryService.registrar(dto));
    }

    // PATCH /api/v1/sanitary/1/aprobar
    @Operation(summary = "Visar y aprobar inspección sanitaria", description = "Establece la resolución definitiva de la orden como APROBADO, autorizando el paso de la carga.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Inspección aprobada y visación guardada de forma conforme"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "404", description = "La orden de inspección no fue localizada")
    })
    @PatchMapping("/{id}/aprobar")
    public ResponseEntity<Sanitary> aprobar(
            @PathVariable Long id,
            @RequestParam(required = false) String observaciones) {
        return ResponseEntity.ok(sanitaryService.aprobar(id, observaciones));
    }

    // PATCH /api/v1/sanitary/1/rechazar
    @Operation(summary = "Rechazar inspección sanitaria", description = "Dictamina la resolución formal como RECHAZADO, bloqueando el tránsito aduanero de la unidad por razones fitozoosanitarias.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Inspección rechazada y alerta levantada en el sistema fronterizo"),
            @ApiResponse(responseCode = "401", description = "No autenticado")
    })
    @PatchMapping("/{id}/rechazar")
    public ResponseEntity<Sanitary> rechazar(
            @PathVariable Long id,
            @RequestParam(required = false) String observaciones) {
        return ResponseEntity.ok(sanitaryService.rechazar(id, observaciones));
    }

    // DELETE /api/v1/sanitary/1
    @Operation(summary = "Eliminar inspección sanitaria", description = "Remueve físicamente el registro de la inspección y sus desgloses de la base de datos central.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Registro de inspección eliminado correctamente. Sin contenido."),
            @ApiResponse(responseCode = "401", description = "No autenticado")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        sanitaryService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    // GET /api/v1/sanitary/patente/ABC123
    @Operation(summary = "Listar inspecciones por Patente", description = "Recupera la lista histórica de controles sanitarios asociados a la patente de un vehículo.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Historial de controles del vehículo devuelto con éxito"),
            @ApiResponse(responseCode = "401", description = "No autenticado")
    })
    @GetMapping("/patente/{patente}")
    public ResponseEntity<List<Sanitary>> obtenerPorPatente(@PathVariable String patente) {
        return ResponseEntity.ok(sanitaryService.obtenerPorPatente(patente));
    }

    // GET /api/v1/sanitary/conductor/12345678-9
    @Operation(summary = "Listar inspecciones por Conductor", description = "Busca e identifica los partes e inspecciones de salud en las que estuvo involucrado el RUN de un chofer.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Controles asociados al conductor obtenidos con éxito"),
            @ApiResponse(responseCode = "401", description = "No autenticado")
    })
    @GetMapping("/conductor/{rut}")
    public ResponseEntity<List<Sanitary>> obtenerPorConductor(@PathVariable String rut) {
        return ResponseEntity.ok(sanitaryService.obtenerPorConductor(rut));
    }

    // GET /api/v1/sanitary/inspector/12345678-9
    @Operation(summary = "Listar inspecciones por Fiscalizador", description = "Extrae el historial de órdenes firmadas y auditadas por el RUN de un inspector específico.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Historial operativo del inspector recuperado correctamente"),
            @ApiResponse(responseCode = "401", description = "No autenticado")
    })
    @GetMapping("/inspector/{rut}")
    public ResponseEntity<List<Sanitary>> obtenerPorInspector(@PathVariable String rut) {
        return ResponseEntity.ok(sanitaryService.obtenerPorInspector(rut));
    }

    // GET /api/v1/sanitary/resultado/APROBADO
    @Operation(summary = "Listar inspecciones por Dictamen/Resultado", description = "Filtra los expedientes sanitarios según su estado resolutivo (PENDIENTE, APROBADO, RECHAZADO).")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Expedientes agrupados por resultado devueltos con éxito"),
            @ApiResponse(responseCode = "401", description = "No autenticado")
    })
    @GetMapping("/resultado/{resultado}")
    public ResponseEntity<List<Sanitary>> obtenerPorResultado(@PathVariable String resultado) {
        return ResponseEntity.ok(sanitaryService.obtenerPorResultado(resultado));
    }

    // GET /api/v1/sanitary/paso/Los Libertadores
    @Operation(summary = "Listar inspecciones por Complejo Fronterizo", description = "Filtra los registros de control sanitario realizados en un paso fronterizo determinado.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Controles del paso fronterizo listados correctamente"),
            @ApiResponse(responseCode = "401", description = "No autenticado")
    })
    @GetMapping("/paso/{pasoFronterizo}")
    public ResponseEntity<List<Sanitary>> obtenerPorPaso(@PathVariable String pasoFronterizo) {
        return ResponseEntity.ok(sanitaryService.obtenerPorPasoFronterizo(pasoFronterizo));
    }

    // GET /api/v1/sanitary/patente/ABC123/resultado/APROBADO
    @Operation(summary = "Listar inspecciones por Patente y Resultado", description = "Cruza filtros analíticos para encontrar resoluciones específicas asociadas a la patente de un vehículo.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Filtro cruzado de patentes ejecutado de forma exitosa"),
            @ApiResponse(responseCode = "401", description = "No autenticado")
    })
    @GetMapping("/patente/{patente}/resultado/{resultado}")
    public ResponseEntity<List<Sanitary>> obtenerPorPatenteYResultado(
            @PathVariable String patente,
            @PathVariable String resultado) {
        return ResponseEntity.ok(sanitaryService.obtenerPorPatenteYResultado(patente, resultado));
    }

    // GET /api/v1/sanitary/fechas?desde=...&hasta=...
    @Operation(summary = "Listar inspecciones por Rango de Fechas", description = "Recupera la bitácora de revisiones sanitarias acotadas dentro de un rango cronológico temporal cerrado.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Revisiones del rango de tiempo devueltas correctamente"),
            @ApiResponse(responseCode = "400", description = "Sello temporal de entrada con formato ISO erróneo")
    })
    @GetMapping("/fechas")
    public ResponseEntity<List<Sanitary>> obtenerPorFechas(
            @RequestParam String desde,
            @RequestParam String hasta) {
        LocalDateTime fechaDesde = LocalDateTime.parse(desde);
        LocalDateTime fechaHasta = LocalDateTime.parse(hasta);
        return ResponseEntity.ok(sanitaryService.obtenerPorRangoFechas(fechaDesde, fechaHasta));
    }

    // GET /api/v1/sanitary/patente/ABC123/ordenadas
    @Operation(summary = "Listar inspecciones de vehículo ordenadas cronológicamente", description = "Retorna el historial de fiscalizaciones de una patente ordenado de forma descendente (más recientes primero).")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Historial ordenado correctamente"),
            @ApiResponse(responseCode = "401", description = "No autenticado")
    })
    @GetMapping("/patente/{patente}/ordenadas")
    public ResponseEntity<List<Sanitary>> obtenerPorPatenteOrdenadas(@PathVariable String patente) {
        return ResponseEntity.ok(sanitaryService.obtenerPorPatenteOrdenadas(patente));
    }

    // GET /api/v1/sanitary/pendientes/ordenadas
    @Operation(summary = "Listar inspecciones pendientes por Orden de Llegada", description = "Lista de control operativa que devuelve las solicitudes PENDIENTES ordenadas desde la más antigua a la más nueva para agilizar los andenes.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cola de solicitudes pendientes recuperada de forma exitosa"),
            @ApiResponse(responseCode = "401", description = "No autenticado")
    })
    @GetMapping("/pendientes/ordenadas")
    public ResponseEntity<List<Sanitary>> obtenerPendientesOrdenadas() {
        return ResponseEntity.ok(sanitaryService.obtenerPendientesOrdenadas());
    }

    // GET /api/v1/sanitary/ultimas
    @Operation(summary = "Listar últimos controles registrados", description = "Endpoint administrativo de auditoría que expone las últimas 10 fiscalizaciones registradas globalmente en los andenes.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Últimos registros leídos con éxito"),
            @ApiResponse(responseCode = "401", description = "No autenticado")
    })
    @GetMapping("/ultimas")
    public ResponseEntity<List<Sanitary>> obtenerUltimas() {
        return ResponseEntity.ok(sanitaryService.obtenerUltimasInspecciones());
    }

    // GET /api/v1/sanitary/estadisticas/resultado/APROBADO
    @Operation(summary = "Contar inspecciones por Resultado", description = "Endpoint estadístico que devuelve el volumen total consolidado de expedientes bajo un dictamen específico.")
    @GetMapping("/estadisticas/resultado/{resultado}")
    public ResponseEntity<Map<String, Long>> contarPorResultado(@PathVariable String resultado) {
        long total = sanitaryService.contarPorResultado(resultado);
        return ResponseEntity.ok(Map.of("total", total));
    }
}