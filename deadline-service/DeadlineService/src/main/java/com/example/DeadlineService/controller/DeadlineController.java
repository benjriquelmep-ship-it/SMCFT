package com.example.DeadlineService.controller;

import com.example.DeadlineService.dto.DeadlineDTO;
import com.example.DeadlineService.model.Deadline;
import com.example.DeadlineService.service.DeadlineService;
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
@RequestMapping("/api/v1/deadlines")
@RequiredArgsConstructor
@Tag(name = "2. Control de Ciclos de Permanencia", description = "Endpoints analíticos para la fijación, prórrogas, vencimientos jurídicos y finiquito de plazos de internación temporal")
@SecurityRequirement(name = "bearerAuth")
public class DeadlineController {

    private final DeadlineService deadlineService;

    @Operation(summary = "Listar todos los plazos vigentes", description = "Expone el padrón maestro total con todos los controles cronológicos y estados situacionales registrados en el perímetro.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Padrón maestro de plazos temporales recuperado"),
            @ApiResponse(responseCode = "401", description = "No autenticado - Falta token Bearer JWT"),
            @ApiResponse(responseCode = "403", description = "Sin privilegios requeridos para el control perimetral")
    })
    @GetMapping
    public ResponseEntity<List<Deadline>> obtenerTodos() {
        return ResponseEntity.ok(deadlineService.obtenerTodos());
    }

    // GET /api/v1/deadlines/1
    @Operation(summary = "Obtener plazo de permanencia por ID", description = "Busca los metadatos específicos, la patente asociada y las ventanas cronológicas de un control de permanencia dado.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Registro de plazo temporal localizado correctamente"),
            @ApiResponse(responseCode = "404", description = "El ID de control solicitado no se encuentra registrado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<Deadline> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(deadlineService.obtenerPorId(id));
    }

    // POST /api/v1/deadlines
    @Operation(summary = "Fijar e indexar plazo de permanencia", description = "Consume el id de acceso provisto por Entry Service, valida la veracidad del tránsito y da de alta un nuevo cronograma de control temporal.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Estructura cronológica de permanencia fijada con éxito"),
            @ApiResponse(responseCode = "400", description = "El id de ingreso origen es inválido o se violaron restricciones del DTO")
    })
    @PostMapping
    public ResponseEntity<Deadline> registrar(@Valid @RequestBody DeadlineDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(deadlineService.registrar(dto));
    }

    // PATCH /api/v1/deadlines/1/cerrar
    @Operation(summary = "Cerrar control por Salida del país", description = "Finiquita el control cronológico marcándolo como CERRADO tras constatarse la salida física de la unidad vehicular antes de expirar el plazo legal.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ciclo de control de permanencia cerrado de forma conforme"),
            @ApiResponse(responseCode = "400", description = "El plazo solicitado no se encuentra en estado ACTIVO"),
            @ApiResponse(responseCode = "404", description = "El ID de control temporal especificado no existe")
    })
    @PatchMapping("/{id}/cerrar")
    public ResponseEntity<Deadline> cerrar(
            @PathVariable Long id,
            @RequestParam(required = false) String observaciones) {
        return ResponseEntity.ok(deadlineService.cerrar(id, observaciones));
    }

    // PATCH /api/v1/deadlines/1/vencer
    @Operation(summary = "Declarar infracción por Vencimiento legal", description = "Modifica de forma explícita el estado situacional a VENCIDO tras consumirse la fecha fatal sin registrarse la salida del país.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Infracción temporal declarada y registrada de manera conforme"),
            @ApiResponse(responseCode = "400", description = "El control ya se encuentra cerrado o inactivo")
    })
    @PatchMapping("/{id}/vencer")
    public ResponseEntity<Deadline> vencer(@PathVariable Long id) {
        return ResponseEntity.ok(deadlineService.vencer(id));
    }

    // DELETE /api/v1/deadlines/1
    @Operation(summary = "Eliminar control temporal físicamente", description = "Remueve de forma permanente la hilera de persistencia del control cronológico de la base de datos.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Registro purgado de la base de datos central de forma exitosa. Sin contenido."),
            @ApiResponse(responseCode = "401", description = "No autenticado")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        deadlineService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    // GET /api/v1/deadlines/1/dias-restantes
    @Operation(summary = "Calcular días calendario faltantes", description = "Ejecuta un cómputo dinámico en tiempo real que devuelve la cantidad de días remanentes antes del vencimiento formal (Arroja números negativos si la permanencia está vencida).")
    @GetMapping("/{id}/dias-restantes")
    public ResponseEntity<Map<String, Long>> calcularDiasRestantes(@PathVariable Long id) {
        long dias = deadlineService.calcularDiasRestantes(id);
        return ResponseEntity.ok(Map.of("diasRestantes", dias));
    }

    // GET /api/v1/deadlines/patente/ABC123
    @Operation(summary = "Buscar controles por Placa Patente", description = "Recupera la trazabilidad analítica temporal completa asociada a una placa patente vehicular.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Trazabilidad cronológica de la unidad vehicular recuperada")
    })
    @GetMapping("/patente/{patente}")
    public ResponseEntity<List<Deadline>> obtenerPorPatente(@PathVariable String patente) {
        return ResponseEntity.ok(deadlineService.obtenerPorPatente(patente));
    }

    // GET /api/v1/deadlines/conductor/12345678-9
    @Operation(summary = "Buscar controles por RUN del Conductor", description = "Lista los plazos e infracciones históricas asociadas al rut o pasaporte de la persona conductora responsable.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Registros vinculados al conductor devueltos con éxito")
    })
    @GetMapping("/conductor/{rut}")
    public ResponseEntity<List<Deadline>> obtenerPorConductor(@PathVariable String rut) {
        return ResponseEntity.ok(deadlineService.obtenerPorConductor(rut));
    }

    // GET /api/v1/deadlines/estado/ACTIVO
    @Operation(summary = "Filtrar controles por Estado operativo", description = "Extrae el conjunto de registros clasificados bajo una situación de vigencia dada (ACTIVO, VENCIDO, CERRADO).")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Conjunto segregado por estado operativo devuelto")
    })
    @GetMapping("/estado/{estado}")
    public ResponseEntity<List<Deadline>> obtenerPorEstado(@PathVariable String estado) {
        return ResponseEntity.ok(deadlineService.obtenerPorEstado(estado));
    }

    // GET /api/v1/deadlines/tipo/ADMISION_TEMPORAL
    @Operation(summary = "Filtrar controles por Régimen legal", description = "Agrupa y expone los plazos basándose en el tipo de destinación aduanera aplicada en frontera (ADMISION_TEMPORAL o RETORNO_OBLIGATORIO).")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Registros agrupados por régimen arancelario devueltos con éxito")
    })
    @GetMapping("/tipo/{tipo}")
    public ResponseEntity<List<Deadline>> obtenerPorTipo(@PathVariable String tipo) {
        return ResponseEntity.ok(deadlineService.obtenerPorTipo(tipo));
    }

    // GET /api/v1/deadlines/proximos-a-vencer
    @Operation(summary = "Listar plazos próximos a expirar", description = "Endpoint prioritario de fiscalización preventiva que extrae aquellos controles ACTIVOS cuya fecha fatal se encuentra dentro de una ventana crítica de 15 días.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Listado de control preventivo temprano extraído con éxito")
    })
    @GetMapping("/proximos-a-vencer")
    public ResponseEntity<List<Deadline>> obtenerProximosAVencer() {
        return ResponseEntity.ok(deadlineService.obtenerProximosAVencer());
    }

    // GET /api/v1/deadlines/activos/ordenados
    @Operation(summary = "Listar controles activos ordenados por Urgencia", description = "Retorna las órdenes activas ordenadas secuencialmente desde el vencimiento más inminente al más lejano, optimizado para componentes Frontend.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Padrón ordenado correlativamente por fecha fatal recuperado con éxito")
    })
    @GetMapping("/activos/ordenados")
    public ResponseEntity<List<Deadline>> obtenerActivosOrdenados() {
        return ResponseEntity.ok(deadlineService.obtenerActivosOrdenados());
    }

    // GET /api/v1/deadlines/ultimos
    @Operation(summary = "Listar últimas inserciones de plazos", description = "Retorna un extracto operativo inmediato con las últimas 10 transacciones de plazos incorporadas recientemente en la base de datos.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Últimos registros de control leídos correctamente")
    })
    @GetMapping("/ultimos")
    public ResponseEntity<List<Deadline>> obtenerUltimos() {
        return ResponseEntity.ok(deadlineService.obtenerUltimosDeadlines());
    }

    // GET /api/v1/deadlines/estadisticas/estado/ACTIVO
    @Operation(summary = "Contar volumen cuantitativo por Estado", description = "Devuelve el indicador estadístico consolidado que representa la sumatoria volumétrica total de registros bajo el estado consultado.")
    @GetMapping("/estadisticas/estado/{estado}")
    public ResponseEntity<Map<String, Long>> contarPorEstado(@PathVariable String estado) {
        long total = deadlineService.contarPorEstado(estado);
        return ResponseEntity.ok(Map.of("total", total));
    }
}