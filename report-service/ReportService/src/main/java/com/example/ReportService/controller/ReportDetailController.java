package com.example.ReportService.controller;

import com.example.ReportService.dto.ReportDetailDTO;
import com.example.ReportService.model.ReportDetail;
import com.example.ReportService.service.ReportDetailService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/v1/report-details")
@RequiredArgsConstructor
@Tag(name = "Detalles de Reporte (Métricas)", description = "Endpoints para el desglose métrico, inyección de indicadores clave (KPIs) y valores estadísticos dentro de los informes")
@SecurityRequirement(name = "bearerAuth")
public class ReportDetailController {

    private final ReportDetailService detailService;

    @Operation(summary = "Listar todas las líneas métricas registradas", description = "Expone el registro global consolidado de todas las variables e indicadores calculados en la base analítica.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Historial global métrico devuelto correctamente"),
            @ApiResponse(responseCode = "401", description = "No autenticado - Falta token JWT"),
            @ApiResponse(responseCode = "403", description = "Sin permisos requeridos")
    })
    @GetMapping
    public ResponseEntity<CollectionModel<EntityModel<ReportDetail>>> obtenerTodos() {
        List<EntityModel<ReportDetail>> details = detailService.obtenerTodos().stream()
                .map(d -> EntityModel.of(d,
                        linkTo(methodOn(ReportDetailController.class).obtenerPorId(d.getId())).withSelfRel()))
                .toList();
        return ResponseEntity.ok(CollectionModel.of(details,
                linkTo(methodOn(ReportDetailController.class).obtenerTodos()).withSelfRel()));
    }

    // GET /api/v1/report-details/1 : Busca un detalle específico por su ID
    @Operation(summary = "Obtener línea métrica por ID", description = "Busca el valor, unidad de medida y descripción de una variable estadística a partir de su clave primaria.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Línea métrica localizada con éxito"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "404", description = "El ID de la variable solicitado no existe en los registros")
    })
    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<ReportDetail>> obtenerPorId(@PathVariable Long id) {
        ReportDetail detail = detailService.obtenerPorId(id);
        return ResponseEntity.ok(EntityModel.of(detail,
                linkTo(methodOn(ReportDetailController.class).obtenerPorId(id)).withSelfRel(),
                linkTo(methodOn(ReportDetailController.class).obtenerTodos()).withRel("detalles")));
    }

    // POST /api/v1/report-details : Agrega una nueva línea de detalle a un reporte
    @Operation(summary = "Inyectar variable métrica a informe", description = "Calcula e indexa una nueva línea de desglose o indicador numérico acoplándolo al ID de un reporte principal.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Línea métrica vinculada al informe de forma exitosa"),
            @ApiResponse(responseCode = "400", description = "Payload inválido o ID de cabecera inexistente")
    })
    @PostMapping
    public ResponseEntity<EntityModel<ReportDetail>> agregar(@Valid @RequestBody ReportDetailDTO dto) {
        ReportDetail detail = detailService.agregar(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(EntityModel.of(detail,
                linkTo(methodOn(ReportDetailController.class).obtenerPorId(detail.getId())).withSelfRel(),
                linkTo(methodOn(ReportDetailController.class).obtenerTodos()).withRel("detalles")));
    }

    // PUT /api/v1/report-details/1 : Actualiza por completo un detalle existente por su ID
    @Operation(summary = "Actualizar línea métrica por ID", description = "Sobreescribe las propiedades, valores o unidades de una línea analítica basándose en su ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Línea métrica actualizada de forma conforme"),
            @ApiResponse(responseCode = "400", description = "Datos de actualización erróneos"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "404", description = "La línea métrica a modificar no fue localizada")
    })
    @PutMapping("/{id}")
    public ResponseEntity<EntityModel<ReportDetail>> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody ReportDetailDTO dto) {
        ReportDetail detail = detailService.actualizar(id, dto);
        return ResponseEntity.ok(EntityModel.of(detail,
                linkTo(methodOn(ReportDetailController.class).obtenerPorId(id)).withSelfRel(),
                linkTo(methodOn(ReportDetailController.class).obtenerTodos()).withRel("detalles")));
    }

    // DELETE /api/v1/report-details/1 : Elimina un detalle físico por su ID
    @Operation(summary = "Eliminar línea métrica", description = "Remueve físicamente la línea analítica o indicador de la base de datos.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Registro de métrica eliminado correctamente. Sin contenido."),
            @ApiResponse(responseCode = "401", description = "No autenticado")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        detailService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    // GET /api/v1/report-details/reporte/1 : Filtra todos los detalles asociados a un reporte padre
    @Operation(summary = "Listar desgloses métricos por Reporte de cabecera", description = "Aísla y lista secuencialmente todos los KPIs e indicadores cuantitativos contenidos dentro de un mismo informe por su ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cuerpo métrico del reporte recuperado de forma exitosa"),
            @ApiResponse(responseCode = "401", description = "No autenticado")
    })
    @GetMapping("/reporte/{reportId}")
    public ResponseEntity<CollectionModel<EntityModel<ReportDetail>>> obtenerPorReporte(@PathVariable Long reportId) {
        List<EntityModel<ReportDetail>> details = detailService.obtenerPorReporte(reportId).stream()
                .map(d -> EntityModel.of(d,
                        linkTo(methodOn(ReportDetailController.class).obtenerPorId(d.getId())).withSelfRel()))
                .toList();
        return ResponseEntity.ok(CollectionModel.of(details,
                linkTo(methodOn(ReportDetailController.class).obtenerPorReporte(reportId)).withSelfRel()));
    }

    // GET /api/v1/report-details/categoria/AUTORIZADOS : Filtra detalles por categoría o etiqueta
    @Operation(summary = "Listar métricas por Categoría/Etiqueta", description = "Agrupa y retorna las variables del libro analítico según su criterio de agrupación contable (ej. AUTORIZADOS, RECHAZADOS).")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Colección filtrada devuelta correctamente"),
            @ApiResponse(responseCode = "401", description = "No autenticado")
    })
    @GetMapping("/categoria/{categoria}")
    public ResponseEntity<CollectionModel<EntityModel<ReportDetail>>> obtenerPorCategoria(@PathVariable String categoria) {
        List<EntityModel<ReportDetail>> details = detailService.obtenerPorCategoria(categoria).stream()
                .map(d -> EntityModel.of(d,
                        linkTo(methodOn(ReportDetailController.class).obtenerPorId(d.getId())).withSelfRel()))
                .toList();
        return ResponseEntity.ok(CollectionModel.of(details,
                linkTo(methodOn(ReportDetailController.class).obtenerPorCategoria(categoria)).withSelfRel()));
    }

    // GET /api/v1/report-details/reporte/1/categoria/AUTORIZADOS : Combina filtros
    @Operation(summary = "Listar métricas por Reporte y Categoría", description = "Cruza filtros analíticos para aislar un grupo específico de variables dentro de un informe (ej. buscar solo las mermas de categoría RECHAZADOS contenidas en el informe ID 1).")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Filtro analítico cruzado completado con éxito"),
            @ApiResponse(responseCode = "401", description = "No autenticado")
    })
    @GetMapping("/reporte/{reportId}/categoria/{categoria}")
    public ResponseEntity<CollectionModel<EntityModel<ReportDetail>>> obtenerPorReporteYCategoria(
            @PathVariable Long reportId,
            @PathVariable String categoria) {
        List<EntityModel<ReportDetail>> details = detailService.obtenerPorReporteYCategoria(reportId, categoria).stream()
                .map(d -> EntityModel.of(d,
                        linkTo(methodOn(ReportDetailController.class).obtenerPorId(d.getId())).withSelfRel()))
                .toList();
        return ResponseEntity.ok(CollectionModel.of(details,
                linkTo(methodOn(ReportDetailController.class).obtenerPorReporteYCategoria(reportId, categoria)).withSelfRel()));
    }

    // GET /api/v1/report-details/buscar?descripcion=cruces : Busca detalles que contengan el texto en su descripción
    @Operation(summary = "Buscar métricas por Glosa explicativa", description = "Realiza una consulta de coincidencia de texto parcial (LIKE) sobre la descripción del indicador estadístico.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Coincidencias analíticas devueltas correctamente"),
            @ApiResponse(responseCode = "401", description = "No autenticado")
    })
    @GetMapping("/buscar")
    public ResponseEntity<CollectionModel<EntityModel<ReportDetail>>> buscarPorDescripcion(@RequestParam String descripcion) {
        List<EntityModel<ReportDetail>> details = detailService.buscarPorDescripcion(descripcion).stream()
                .map(d -> EntityModel.of(d,
                        linkTo(methodOn(ReportDetailController.class).obtenerPorId(d.getId())).withSelfRel()))
                .toList();
        return ResponseEntity.ok(CollectionModel.of(details,
                linkTo(methodOn(ReportDetailController.class).buscarPorDescripcion(descripcion)).withSelfRel()));
    }

    // GET /api/v1/report-details/reporte/1/ordenados : Lista los detalles de un reporte de forma secuencial u ordenada
    @Operation(summary = "Listar métricas de un reporte ordenadas secuencialmente", description = "Obtiene las variables de un informe estructuradas de forma secuencial, ideal para su mapeo ordenado en Frontend.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cuerpo métrico indexado devuelto de forma ordenada"),
            @ApiResponse(responseCode = "401", description = "No autenticado")
    })
    @GetMapping("/reporte/{reportId}/ordenados")
    public ResponseEntity<CollectionModel<EntityModel<ReportDetail>>> obtenerOrdenados(@PathVariable Long reportId) {
        List<EntityModel<ReportDetail>> details = detailService.obtenerPorReporteOrdenados(reportId).stream()
                .map(d -> EntityModel.of(d,
                        linkTo(methodOn(ReportDetailController.class).obtenerPorId(d.getId())).withSelfRel()))
                .toList();
        return ResponseEntity.ok(CollectionModel.of(details,
                linkTo(methodOn(ReportDetailController.class).obtenerOrdenados(reportId)).withSelfRel()));
    }

    // GET /api/v1/report-details/ultimos : Devuelve los últimos 10 detalles registrados en la base de datos
    @Operation(summary = "Listar últimas líneas métricas calculadas", description = "Muestra una vista rápida con los últimos 10 indicadores consolidados globalmente en el sistema.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Historial inmediato de inserciones analíticas leído con éxito"),
            @ApiResponse(responseCode = "401", description = "No autenticado")
    })
    @GetMapping("/ultimos")
    public ResponseEntity<CollectionModel<EntityModel<ReportDetail>>> obtenerUltimos() {
        List<EntityModel<ReportDetail>> details = detailService.obtenerUltimosDetalles().stream()
                .map(d -> EntityModel.of(d,
                        linkTo(methodOn(ReportDetailController.class).obtenerPorId(d.getId())).withSelfRel()))
                .toList();
        return ResponseEntity.ok(CollectionModel.of(details,
                linkTo(methodOn(ReportDetailController.class).obtenerUltimos()).withSelfRel()));
    }
}