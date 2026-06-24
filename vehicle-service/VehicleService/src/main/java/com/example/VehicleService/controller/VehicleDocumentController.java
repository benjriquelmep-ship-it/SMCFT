package com.example.VehicleService.controller;

import com.example.VehicleService.dto.VehicleDocumentDTO;
import com.example.VehicleService.model.VehicleDocument;
import com.example.VehicleService.service.VehicleDocumentService;
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
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/v1/vehicles/documentos")
@RequiredArgsConstructor
@Tag(name = "Documentos del Vehículo", description = "Endpoints para el control, validación y vigencia de padrones, seguros y revisiones técnicas")
@SecurityRequirement(name = "bearerAuth")
public class VehicleDocumentController {

    private final VehicleDocumentService documentService;

    @Operation(summary = "Listar todas las credenciales documentales", description = "Muestra el registro global de la documentación vehicular ingresada en el sistema fronterizo.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Historial documental obtenido correctamente"),
            @ApiResponse(responseCode = "401", description = "No autenticado - Falta token válido"),
            @ApiResponse(responseCode = "403", description = "Sin permisos de acceso")
    })
    @GetMapping
    public ResponseEntity<List<VehicleDocument>> obtenerTodos() {
        return ResponseEntity.ok(documentService.obtenerTodos());
    }

    // GET /api/v1/vehicles/documentos/1 : Busca un documento específico a través de su ID primario
    @Operation(summary = "Obtener documento por ID", description = "Busca el estado y metadatos de un documento individual mediante su clave primaria.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Documento localizado correctamente"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "404", description = "El ID de documento solicitado no fue encontrado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<VehicleDocument> obtenerPorId(
            @PathVariable Long id) {
        return ResponseEntity.ok(documentService.obtenerPorId(id));
    }

    // POST /api/v1/vehicles/documentos : Registra un nuevo documento acoplándolo a un vehículo existente
    @Operation(summary = "Vincular documento a vehículo", description = "Registra una nueva pieza documental (padrón, revisión técnica) acoplándola obligatoriamente al ID de un vehículo.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Documento indexado con éxito"),
            @ApiResponse(responseCode = "400", description = "Payload inválido o fechas inconsistentes"),
            @ApiResponse(responseCode = "401", description = "No autenticado")
    })
    @PostMapping
    public ResponseEntity<VehicleDocument> agregar(
            @Valid @RequestBody VehicleDocumentDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(documentService.agregar(dto));
    }

    // PUT /api/v1/vehicles/documentos/1 : Sobreescribe los metadatos y fechas de un documento específico
    @Operation(summary = "Actualizar metadatos del documento", description = "Sobreescribe de forma total las propiedades, folios y fechas de vigencia de un documento.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Documento actualizado correctamente"),
            @ApiResponse(responseCode = "400", description = "Datos de actualización erróneos"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "404", description = "El registro documental no existe")
    })
    @PutMapping("/{id}")
    public ResponseEntity<VehicleDocument> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody VehicleDocumentDTO dto) {
        return ResponseEntity.ok(documentService.actualizar(id, dto));
    }

    // PATCH /api/v1/vehicles/documentos/1/invalidar : Deshabilita de forma parcial la vigencia de un papel o padrón
    @Operation(summary = "Invalidar vigencia de un documento", description = "Efectúa una baja parcial modificando lógicamente el flag de vigencia de un certificado o padrón.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Documento invalidado exitosamente"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "404", description = "Registro no localizado")
    })
    @PatchMapping("/{id}/invalidar")
    public ResponseEntity<VehicleDocument> invalidar(@PathVariable Long id) {
        return ResponseEntity.ok(documentService.invalidar(id));
    }

    // DELETE /api/v1/vehicles/documentos/1 : Remueve físicamente el registro documental de la base de datos
    @Operation(summary = "Eliminar documento del historial", description = "Remueve físicamente el registro del documento de la base de datos central.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Registro documental purgado con éxito. Sin contenido."),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "404", description = "El documento solicitado no existe")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        documentService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    // GET /api/v1/vehicles/documentos/vehiculo/1 : Extrae todo el historial de papeles adjuntos a un vehículo por ID
    @Operation(summary = "Obtener historial documental de un vehículo", description = "Lista secuencialmente todos los papeles históricos que se le han indexado a una unidad móvil por su ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Historial del móvil recuperado de forma exitosa"),
            @ApiResponse(responseCode = "401", description = "No autenticado")
    })
    @GetMapping("/vehiculo/{vehicleId}")
    public ResponseEntity<List<VehicleDocument>> obtenerPorVehiculo(
            @PathVariable Long vehicleId) {
        return ResponseEntity.ok(
                documentService.obtenerPorVehiculo(vehicleId));
    }

    // GET /api/v1/vehicles/documentos/vehiculo/1/vigentes
    @Operation(summary = "Listar documentos vigentes de un vehículo", description = "Aísla y retorna de forma exclusiva las acreditaciones vigentes y no expiradas vigentes para el vehículo.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Acreditaciones vigentes recuperadas correctamente"),
            @ApiResponse(responseCode = "401", description = "No autenticado")
    })
    @GetMapping("/vehiculo/{vehicleId}/vigentes")
    public ResponseEntity<List<VehicleDocument>> obtenerVigentesPorVehiculo(
            @PathVariable Long vehicleId) {
        return ResponseEntity.ok(
                documentService.obtenerVigentesPorVehiculo(vehicleId));
    }

    // GET /api/v1/vehicles/documentos/proximos-a-vencer : Lista los documentos cuya fecha de expiración se acerca al límite de alerta
    @Operation(summary = "Listar documentos próximos a vencer", description = "Filtro de auditoría fronteriza que lista los documentos cuya fecha de caducidad se encuentra dentro del rango de alerta.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Alertas de vencimiento obtenidas con éxito"),
            @ApiResponse(responseCode = "401", description = "No autenticado")
    })
    @GetMapping("/proximos-a-vencer")
    public ResponseEntity<List<VehicleDocument>> obtenerProximosAVencer() {
        return ResponseEntity.ok(documentService.obtenerProximosAVencer());
    }

    // GET /api/v1/vehicles/documentos/vencidos : Lista todos los registros cuya fecha de expiración es inferior a la actual
    @Operation(summary = "Listar documentos vencidos", description = "Aísla de manera global todos los certificados vehiculares cuya fecha de expiración es inferior a la actual.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Colección de papeles vencidos devuelta correctamente"),
            @ApiResponse(responseCode = "401", description = "No autenticado")
    })
    @GetMapping("/vencidos")
    public ResponseEntity<List<VehicleDocument>> obtenerVencidos() {
        return ResponseEntity.ok(documentService.obtenerVencidos());
    }

    // GET /api/v1/vehicles/documentos/vehiculo/1/ordenados
    @Operation(summary = "Listar documentos de un vehículo ordenados por Emisión", description = "Obtiene las acreditaciones de un móvil ordenadas cronológicamente partiendo por la fecha de emisión.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Colección cronológica devuelta exitosamente"),
            @ApiResponse(responseCode = "401", description = "No autenticado")
    })
    @GetMapping("/vehiculo/{vehicleId}/ordenados")
    public ResponseEntity<List<VehicleDocument>> obtenerPorVehiculoOrdenados(
            @PathVariable Long vehicleId) {
        return ResponseEntity.ok(
                documentService.obtenerPorVehiculoOrdenados(vehicleId));
    }

    // GET /api/v1/vehicles/documentos/ultimos : Devuelve las últimas 10 inserciones documentales del sistema global
    @Operation(summary = "Listar últimas inserciones documentales", description = "Endpoint de control administrativo que expone las últimas 10 transacciones documentales indexadas al sistema.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Historial de auditoría recuperado con éxito"),
            @ApiResponse(responseCode = "401", description = "No autenticado")
    })
    @GetMapping("/ultimos")
    public ResponseEntity<List<VehicleDocument>> obtenerUltimosRegistrados() {
        return ResponseEntity.ok(documentService.obtenerUltimosRegistrados());
    }
}