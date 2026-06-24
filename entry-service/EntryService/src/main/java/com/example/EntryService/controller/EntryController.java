package com.example.EntryService.controller;

import com.example.EntryService.dto.EntryDTO;
import com.example.EntryService.model.Entry;
import com.example.EntryService.service.EntryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@RestController
@RequestMapping("/api/v1/entries")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "1. Gestión de Ingresos Fronterizos", description = "Endpoints operativos destinados a la apertura, tramitación, andén de visación y auditoría de accesos vehiculares al territorio nacional")
public class EntryController {

    private final EntryService entryService;

    @Operation(summary = "Obtener Catálogo Global de Ingresos", description = "Recupera la nómina histórica y completa de solicitudes de ingreso registradas en los complejos fronterizos.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Listado de ingresos recuperado exitosamente."),
            @ApiResponse(responseCode = "400", description = "Petición incorrecta o parámetros de búsqueda inconsistentes."),
            @ApiResponse(responseCode = "401", description = "Falta de autenticación. Token JWT ausente o inválido."),
            @ApiResponse(responseCode = "403", description = "Acceso denegado. Rol de usuario insuficiente.")
    })
    @GetMapping
    public ResponseEntity<List<Entry>> obtenerTodos() {
        return ResponseEntity.ok(entryService.obtenerTodos());
    }

    @Operation(summary = "Obtener Detalle de un Ingreso por ID", description = "Recupera los datos de un trámite perimetral específico mediante su identificador numérico único. Utilizado internamente por Deadline Service.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Registro de ingreso localizado y devuelto."),
            @ApiResponse(responseCode = "400", description = "Identificador de ingreso provisto con formato no válido."),
            @ApiResponse(responseCode = "401", description = "Acceso no autorizado."),
            @ApiResponse(responseCode = "403", description = "Permisos de usuario insuficientes.")
    })
    @GetMapping("/{id}")
    public ResponseEntity<Entry> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(entryService.obtenerPorId(id));
    }

    @Operation(summary = "Registrar Solicitud de Ingreso Vehicular", description = "Inicia una nueva orden de internación vehicular en el andén de control, dejando el estado inicial como PENDIENTE.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Solicitud de ingreso creada correctamente en el sistema."),
            @ApiResponse(responseCode = "400", description = "Payload de entrada inválido o errores de validación estructural."),
            @ApiResponse(responseCode = "401", description = "Token de autenticación faltante."),
            @ApiResponse(responseCode = "403", description = "Acceso restringido.")
    })
    @PostMapping
    public ResponseEntity<Entry> registrar(@Valid @RequestBody EntryDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(entryService.registrar(dto));
    }

    @Operation(summary = "Actualizar Solicitud de Ingreso", description = "Modifica las propiedades de cabecera asociadas a una orden de acceso fronterizo identificada por su ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Registro modificado y persistido de manera conforme."),
            @ApiResponse(responseCode = "400", description = "Errores de validación en los datos del DTO."),
            @ApiResponse(responseCode = "401", description = "Usuario no autenticado."),
            @ApiResponse(responseCode = "403", description = "Operación denegada.")
    })
    @PutMapping("/{id}")
    public ResponseEntity<Entry> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody EntryDTO dto) {
        return ResponseEntity.ok(entryService.actualizar(id, dto));
    }

    @Operation(summary = "Autorizar Trámite de Ingreso", description = "Cambia el estado de la solicitud a 'AUTORIZADO', visando el acceso de la unidad vehicular por parte del fiscalizador en caseta.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Trámite de ingreso visado y firmado correctamente."),
            @ApiResponse(responseCode = "400", description = "El RUT del fiscalizador es obligatorio o el ingreso no está en estado válido."),
            @ApiResponse(responseCode = "401", description = "Inicie sesión para completar el aforo."),
            @ApiResponse(responseCode = "403", description = "Rol de fiscalizador requerido.")
    })
    @PatchMapping("/{id}/autorizar")
    public ResponseEntity<Entry> autorizar(
            @PathVariable Long id,
            @RequestParam String rutFiscalizador,
            @RequestParam(required = false) String observaciones) {
        return ResponseEntity.ok(
                entryService.autorizar(id, rutFiscalizador, observaciones));
    }

    @Operation(summary = "Rechazar Trámite de Ingreso", description = "Deniega el acceso del vehículo al territorio nacional cambiando su estado a 'RECHAZADO' y revirtiendo la situación previa de la unidad.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Acceso denegado exitosamente en los registros."),
            @ApiResponse(responseCode = "400", description = "Inconsistencia en los datos o parámetros de firma del rechazo."),
            @ApiResponse(responseCode = "401", description = "No autenticado."),
            @ApiResponse(responseCode = "403", description = "Falta de privilegios de control.")
    })
    @PatchMapping("/{id}/rechazar")
    public ResponseEntity<Entry> rechazar(
            @PathVariable Long id,
            @RequestParam String rutFiscalizador,
            @RequestParam(required = false) String observaciones) {
        return ResponseEntity.ok(
                entryService.rechazar(id, rutFiscalizador, observaciones));
    }

    @Operation(summary = "Remover Registro de Ingreso", description = "Elimina permanentemente una orden de acceso del motor de base de datos por medio de su clave primaria.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Registro eliminado físicamente sin contenido de retorno."),
            @ApiResponse(responseCode = "400", description = "Id provisto inexistente o bloqueado."),
            @ApiResponse(responseCode = "401", description = "Acceso no válido."),
            @ApiResponse(responseCode = "403", description = "Acceso estrictamente prohibido para este rol.")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        entryService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Buscar Ingresos por Placa Patente", description = "Recupera la trazabilidad histórica de ingresos al país filtrando por una patente específica.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Historial de tránsito vehicular recuperado.")
    })
    @GetMapping("/patente/{patente}")
    public ResponseEntity<List<Entry>> obtenerPorPatente(@PathVariable String patente) {
        return ResponseEntity.ok(entryService.obtenerPorPatente(patente));
    }

    @Operation(summary = "Buscar Ingresos por RUN del Conductor", description = "Entrega el listado de registros migratorios asociados al documento de identidad del conductor.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Historial por conductor obtenido con éxito.")
    })
    @GetMapping("/conductor/{rut}")
    public ResponseEntity<List<Entry>> obtenerPorConductor(@PathVariable String rut) {
        return ResponseEntity.ok(entryService.obtenerPorConductor(rut));
    }

    @Operation(summary = "Filtrar Ingresos por Estado Operativo", description = "Devuelve las solicitudes agrupadas por su estado actual de tramitación (PENDIENTE, AUTORIZADO, RECHAZADO).")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Registros agrupados por estado recuperados.")
    })
    @GetMapping("/estado/{estado}")
    public ResponseEntity<List<Entry>> obtenerPorEstado(@PathVariable String estado) {
        return ResponseEntity.ok(entryService.obtenerPorEstado(estado));
    }

    @Operation(summary = "Filtrar Ingresos por Régimen Aduanero", description = "Filtra el universo de accesos según la modalidad legal invocada (RETORNO o ADMISION_TEMPORAL).")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Registros clasificados por tipo legal devueltos.")
    })
    @GetMapping("/tipo/{tipoIngreso}")
    public ResponseEntity<List<Entry>> obtenerPorTipo(@PathVariable String tipoIngreso) {
        return ResponseEntity.ok(entryService.obtenerPorTipoIngreso(tipoIngreso));
    }

    @Operation(summary = "Buscar Ingresos por Complejo Fronterizo", description = "Lista todas las fiscalizaciones ejecutadas en un andén o aduana específica del perímetro.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Historial del complejo fronterizo recuperado.")
    })
    @GetMapping("/paso/{pasoFronterizo}")
    public ResponseEntity<List<Entry>> obtenerPorPaso(@PathVariable String pasoFronterizo) {
        return ResponseEntity.ok(entryService.obtenerPorPasoFronterizo(pasoFronterizo));
    }

    @Operation(summary = "Buscar Ingresos por RUN de Fiscalizador", description = "Permite auditar el volumen de trámites revisados e ingresados por un funcionario aduanero en particular.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Registros visados por el funcionario obtenidos.")
    })
    @GetMapping("/fiscalizador/{rut}")
    public ResponseEntity<List<Entry>> obtenerPorFiscalizador(@PathVariable String rut) {
        return ResponseEntity.ok(entryService.obtenerPorFiscalizador(rut));
    }

    @Operation(summary = "Filtrar por Coincidencia Dual de Patente y Estado", description = "Ejecuta una búsqueda de control combinando la placa patente con la situación resolutiva actual de la orden.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Coincidencia de registros devuelta exitosamente.")
    })
    @GetMapping("/patente/{patente}/estado/{estado}")
    public ResponseEntity<List<Entry>> obtenerPorPatenteYEstado(
            @PathVariable String patente,
            @PathVariable String estado) {
        return ResponseEntity.ok(entryService.obtenerPorPatenteYEstado(patente, estado));
    }

    @Operation(summary = "Filtrar por Rango Cronológico de Fechas", description = "Extrae las solicitudes formalizadas dentro de una ventana de tiempo delimitada por parámetros ISO LocalDateTime.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ventana cronológica de ingresos localizada.")
    })
    @GetMapping("/fechas")
    public ResponseEntity<List<Entry>> obtenerPorFechas(
            @RequestParam String desde,
            @RequestParam String hasta) {
        LocalDateTime fechaDesde = LocalDateTime.parse(desde);
        LocalDateTime fechaHasta = LocalDateTime.parse(hasta);
        return ResponseEntity.ok(entryService.obtenerPorRangoFechas(fechaDesde, fechaHasta));
    }

    @Operation(summary = "Buscar por Coincidencia Parcial de País de Origen", description = "Buscador flexible que localiza registros cuyo origen calce con el patrón o subcadena de texto ingresada.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Resultados parciales recuperados.")
    })
    @GetMapping("/buscar")
    public ResponseEntity<List<Entry>> buscarPorPais(@RequestParam String pais) {
        return ResponseEntity.ok(entryService.buscarPorPaisOrigen(pais));
    }

    @Operation(summary = "Obtener Historial Cronológico Inverso por Patente", description = "Retorna la trazabilidad de accesos de un vehículo ordenados de forma descendente, del evento más nuevo al más antiguo.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Historial ordenado correctamente.")
    })
    @GetMapping("/patente/{patente}/ordenados")
    public ResponseEntity<List<Entry>> obtenerPorPatenteOrdenados(@PathVariable String patente) {
        return ResponseEntity.ok(entryService.obtenerPorPatenteOrdenados(patente));
    }

    @Operation(summary = "Obtener Últimos Ingresos del Sistema", description = "Mapea un cuadro de mando rápido devolviendo los últimos 10 registros ingresados a la plataforma.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Dashboard de las últimas 10 entradas obtenido.")
    })
    @GetMapping("/ultimos")
    public ResponseEntity<List<Entry>> obtenerUltimosIngresos() {
        return ResponseEntity.ok(entryService.obtenerUltimosIngresos());
    }

    @Operation(summary = "Métrica Totalizadora por Estado", description = "Entrega un contador estadístico consolidado que indica la cantidad de registros en base a un estado consultado.")
    @GetMapping("/estadisticas/estado/{estado}")
    public ResponseEntity<Map<String, Long>> contarPorEstado(@PathVariable String estado) {
        long total = entryService.contarPorEstado(estado);
        return ResponseEntity.ok(Map.of("total", total));
    }

    @Operation(summary = "Métrica Totalizadora por Régimen", description = "Entrega un contador estadístico consolidado basado en el tipo de destinación aduanera indicada.")
    @GetMapping("/estadisticas/tipo/{tipo}")
    public ResponseEntity<Map<String, Long>> contarPorTipo(@PathVariable String tipo) {
        long total = entryService.contarPorTipo(tipo);
        return ResponseEntity.ok(Map.of("total", total));
    }
}