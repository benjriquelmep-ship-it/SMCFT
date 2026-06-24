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
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.CollectionModel;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/v1/audits")
@RequiredArgsConstructor
@Tag(name = "1. Gestión de Expedientes de Auditoría", description = "Endpoints de control perimetral destinados a la apertura, seguimiento, estados conclusivos y monitoreo de expedientes de fiscalización forense")
@SecurityRequirement(name = "bearerAuth")
public class AuditController {

    private final AuditService auditService;

    @Operation(summary = "Listar todos los expedientes de auditoría", description = "Recupera el padrón maestro unificado con todos los procesos de fiscalización y estados de revisión registrados en el ecosistema.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Padrón maestro de auditorías recuperado con éxito"),
            @ApiResponse(responseCode = "401", description = "No autenticado - Token Bearer JWT ausente o inválido"),
            @ApiResponse(responseCode = "403", description = "Sin permisos - Privilegios de oficial de seguridad insuficientes")
    })
    @GetMapping
    public ResponseEntity<List<Audit>> obtenerTodas() {
        return ResponseEntity.ok(auditService.obtenerTodas());
    }

    // GET /api/v1/audits/1
    @Operation(summary = "Obtener expediente de auditoría por ID", description = "Busca la información de cabecera, microservicio afectado, RUN del auditor asignado y estado operativo mediante su identificador único.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Expediente de auditoría localizado correctamente"),
            @ApiResponse(responseCode = "404", description = "El ID de expediente solicitado no se encuentra registrado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<Audit> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(auditService.obtenerPorId(id));
    }

    // POST /api/v1/audits
    @Operation(summary = "Aperturar nuevo expediente de auditoría", description = "Valida la estructura de entrada DTO e inicializa de forma reglamentaria un nuevo proceso de fiscalización en estado 'EN_PROCESO'.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Expediente forense constituido y registrado con éxito"),
            @ApiResponse(responseCode = "400", description = "Payload DTO inválido o violación de restricciones estructurales de los campos")
    })
    @PostMapping
    public ResponseEntity<Audit> registrar(@Valid @RequestBody AuditDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(auditService.registrar(dto));
    }

    // PATCH /api/v1/audits/1/completar
    @Operation(summary = "Cerrar expediente como Completado", description = "Actualización parcial que cambia el estado operativo del expediente a 'COMPLETADA' e inyecta la marca temporal de cierre del servidor.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Expediente finalizado y cerrado de forma conforme"),
            @ApiResponse(responseCode = "404", description = "El ID de auditoría especificado no existe")
    })
    @PatchMapping("/{id}/completar")
    public ResponseEntity<Audit> completar(@PathVariable Long id) {
        return ResponseEntity.ok(auditService.completar(id));
    }

    // PATCH /api/v1/audits/1/observacion
    @Operation(summary = "Marcar expediente con Observaciones", description = "Modifica el estado situacional del proceso a 'OBSERVACION' tras detectarse inconsistencias o alertas críticas en las trazas analizadas.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Estado resolutivo del expediente actualizado correctamente")
    })
    @PatchMapping("/{id}/observacion")
    public ResponseEntity<Audit> marcarObservacion(@PathVariable Long id) {
        return ResponseEntity.ok(auditService.marcarObservacion(id));
    }

    // DELETE /api/v1/audits/1
    @Operation(summary = "Eliminar expediente de auditoría físicamente", description = "Remueve permanentemente el registro de cabecera de la base de datos central (operación restringida sin cuerpo de respuesta).")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Registro purgado correctamente de la persistencia central. Sin contenido."),
            @ApiResponse(responseCode = "401", description = "No autenticado")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        auditService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    // GET /api/v1/audits/auditor/12345678-9
    @Operation(summary = "Buscar expedientes por RUN del Auditor", description = "Filtra e identifica la totalidad de procesos de revisión asignados a la cédula de identidad de un auditor específico.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Colección de auditorías del funcionario recuperada")
    })
    @GetMapping("/auditor/{rut}")
    public ResponseEntity<List<Audit>> obtenerPorAuditor(@PathVariable String rut) {
        return ResponseEntity.ok(auditService.obtenerPorAuditor(rut));
    }

    // GET /api/v1/audits/tipo/USUARIO
    @Operation(summary = "Filtrar expedientes por Tipo de revisión", description = "Agrupa y expone los procesos basándose en la naturaleza técnica analizada (USUARIO, CRUCE_FRONTERIZO, TRANSACCION o SISTEMA).")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Registros segregados por tipo de fiscalización devueltos")
    })
    @GetMapping("/tipo/{tipo}")
    public ResponseEntity<List<Audit>> obtenerPorTipo(@PathVariable String tipo) {
        return ResponseEntity.ok(auditService.obtenerPorTipo(tipo));
    }

    // GET /api/v1/audits/estado/EN_PROCESO
    @Operation(summary = "Filtrar expedientes por Estado operativo", description = "Extrae el conjunto de registros clasificados bajo una situación de vigencia dada (EN_PROCESO, COMPLETADA, OBSERVACION).")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Conjunto segregado por estado operativo retornado")
    })
    @GetMapping("/estado/{estado}")
    public ResponseEntity<List<Audit>> obtenerPorEstado(@PathVariable String estado) {
        return ResponseEntity.ok(auditService.obtenerPorEstado(estado));
    }

    // GET /api/v1/audits/entidad/User Service
    @Operation(summary = "Buscar expedientes por Nombre de Microservicio", description = "Filtra la bitácora exponiendo los procesos que afecten a un componente distribuido específico del ecosistema.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Auditorías vinculadas al microservicio localizadas")
    })
    @GetMapping("/entidad/{entidad}")
    public ResponseEntity<List<Audit>> obtenerPorEntidad(@PathVariable String entidad) {
        return ResponseEntity.ok(auditService.obtenerPorEntidad(entidad));
    }

    // GET /api/v1/audits/entidad/User Service/1
    @Operation(summary = "Buscar por Coincidencia Dual de Microservicio y Registro ID", description = "Ejecuta una búsqueda analítica acotando las revisiones sobre una fila física específica de una tabla de destino.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Coincidencia exacta de auditoría forense devuelta")
    })
    @GetMapping("/entidad/{entidad}/{entidadId}")
    public ResponseEntity<List<Audit>> obtenerPorEntidadYId(
            @PathVariable String entidad,
            @PathVariable Long entidadId) {
        return ResponseEntity.ok(auditService.obtenerPorEntidadYId(entidad, entidadId));
    }

    // GET /api/v1/audits/fechas?desde=...&hasta=...
    @Operation(summary = "Filtrar por Rango Cronológico de Apertura", description = "Extrae los expedientes forenses constituidos en el territorio dentro de una ventana temporal ISO delimitada.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ventana temporal de expedientes localizada de forma conforme")
    })
    @GetMapping("/fechas")
    public ResponseEntity<List<Audit>> obtenerPorFechas(
            @RequestParam String desde,
            @RequestParam String hasta) {
        LocalDateTime fechaDesde = LocalDateTime.parse(desde);
        LocalDateTime fechaHasta = LocalDateTime.parse(hasta);
        return ResponseEntity.ok(auditService.obtenerPorRangoFechas(fechaDesde, fechaHasta));
    }

    // GET /api/v1/audits/auditor/12345678-9/ordenadas
    @Operation(summary = "Obtener Historial por Auditor Ordenado por Reciencia", description = "Retorna el historial de revisiones de un funcionario ordenadas de forma descendente (del evento más nuevo al más antiguo).")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Historial ordenado jerárquicamente recuperado")
    })
    @GetMapping("/auditor/{rut}/ordenadas")
    public ResponseEntity<List<Audit>> obtenerPorAuditorOrdenadas(@PathVariable String rut) {
        return ResponseEntity.ok(auditService.obtenerPorAuditorOrdenadas(rut));
    }

    // GET /api/v1/audits/en-proceso/ordenadas
    @Operation(summary = "Listar revisiones en proceso ordenadas por Antigüedad", description = "Retorna los expedientes ACTIVOS ordenados linealmente desde el más antiguo al más reciente para priorizar colas de trabajo.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cola de trabajo secuencial ordenada por marca de tiempo")
    })
    @GetMapping("/en-proceso/ordenadas")
    public ResponseEntity<List<Audit>> obtenerEnProcesoOrdenadas() {
        return ResponseEntity.ok(auditService.obtenerEnProcesoOrdenadas());
    }

    // GET /api/v1/audits/ultimas
    @Operation(summary = "Listar últimas solicitudes globales de auditorías", description = "Endpoint administrativo de monitoreo perimetral inmediato que expone las últimas 10 cabeceras registradas.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Extracto analítico rápido de panel devuelto")
    })
    @GetMapping("/ultimas")
    public ResponseEntity<List<Audit>> obtenerUltimas() {
        return ResponseEntity.ok(auditService.obtenerUltimasAuditorias());
    }

    // GET /api/v1/audits/estadisticas/estado/COMPLETADA
    @Operation(summary = "Métrica Totalizadora cuantitativa por Estado", description = "Devuelve el indicador estadístico consolidado que representa la sumatoria volumétrica total de registros bajo el estado consultado.")
    @GetMapping("/estadisticas/estado/{estado}")
    public ResponseEntity<Map<String, Long>> contarPorEstado(@PathVariable String estado) {
        long total = auditService.contarPorEstado(estado);
        return ResponseEntity.ok(Map.of("total", total));
    }

    // GET /api/v1/audits/estadisticas/tipo/USUARIO
    @Operation(summary = "Métrica Totalizadora cuantitativa por Tipo de auditoría", description = "Indica estadísticamente qué áreas de control registran mayor volumetría y densidad de trámites forenses.")
    @GetMapping("/estadisticas/tipo/{tipo}")
    public ResponseEntity<Map<String, Long>> contarPorTipo(@PathVariable String tipo) {
        long total = auditService.contarPorTipo(tipo);
        return ResponseEntity.ok(Map.of("total", total));
    }
}