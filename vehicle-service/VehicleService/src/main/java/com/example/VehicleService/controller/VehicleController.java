package com.example.VehicleService.controller;

import com.example.VehicleService.dto.VehicleDTO;
import com.example.VehicleService.model.Vehicle;
import com.example.VehicleService.service.VehicleService;
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
@RequestMapping("/api/v1/vehicles")
@RequiredArgsConstructor
@Tag(name = "Vehículos", description = "Endpoints para el registro, control y consulta de la situación aduanera de vehículos")
@SecurityRequirement(name = "bearerAuth")
public class VehicleController {

    private final VehicleService vehicleService;

    @Operation(summary = "Listar vehículos", description = "Recupera la colección global de vehículos o filtra por el RUN del propietario aduanero.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de vehículos recuperada con éxito"),
            @ApiResponse(responseCode = "401", description = "No autenticado - Falta token Bearer JWT"),
            @ApiResponse(responseCode = "403", description = "Sin permisos - Rol no autorizado")
    })
    @GetMapping
    public ResponseEntity<List<Vehicle>> obtenerTodos(
            @RequestParam(required = false) String rutPropietario) {
        if (rutPropietario != null) {
            return ResponseEntity.ok(vehicleService.obtenerPorPropietario(rutPropietario));
        }
        return ResponseEntity.ok(vehicleService.obtenerTodos());
    }

    // GET /api/v1/vehicles/1 : Busca un vehículo por su identificador primario
    @Operation(summary = "Obtener vehículo por ID", description = "Busca en la base de datos la ficha técnica de un vehículo a partir de su clave primaria.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Vehículo localizado correctamente"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "404", description = "El ID del vehículo solicitado no existe")
    })
    @GetMapping("/{id}")
    public ResponseEntity<Vehicle> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(vehicleService.obtenerPorId(id));
    }

    // GET /api/v1/vehicles/patente/ABC123
    @Operation(summary = "Obtener vehículo por Patente", description = "Busca los datos de un vehículo mediante su placa patente única. Consumido por Entry Service y Border Crossing Service.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Vehículo identificado correctamente"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "404", description = "La patente ingresada no se encuentra registrada")
    })
    @GetMapping("/patente/{patente}")
    public ResponseEntity<Vehicle> obtenerPorPatente(
            @PathVariable String patente) {
        return ResponseEntity.ok(vehicleService.obtenerPorPatente(patente));
    }

    // POST /api/v1/vehicles : Registra un nuevo parque automotor aplicando reglas de validación
    @Operation(summary = "Registrar un nuevo vehículo", description = "Valida el esquema del payload y da de alta un vehículo en el parque automotor fronterizo.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Vehículo registrado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Estructura del JSON inválida o patente duplicada"),
            @ApiResponse(responseCode = "401", description = "No autenticado")
    })
    @PostMapping
    public ResponseEntity<Vehicle> registrar(
            @Valid @RequestBody VehicleDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(vehicleService.registrar(dto));
    }

    // PUT /api/v1/vehicles/1 : Modifica los valores de un vehículo existente mediante su ID
    @Operation(summary = "Actualizar vehículo por ID", description = "Sobreescribe las propiedades técnicas de un vehículo basándose en su clave primaria.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Datos de vehículo actualizados correctamente"),
            @ApiResponse(responseCode = "400", description = "Datos de actualización inválidos"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "404", description = "El vehículo a modificar no fue localizado")
    })
    @PutMapping("/{id}")
    public ResponseEntity<Vehicle> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody VehicleDTO dto) {
        return ResponseEntity.ok(vehicleService.actualizar(id, dto));
    }

    // PATCH /api/v1/vehicles/patente/ABC123/estado?nuevoEstado=FUERA_DEL_PAIS
    @Operation(summary = "Actualizar estado aduanero de un vehículo", description = "Modifica de forma exclusiva la situación aduanera o de tránsito del vehículo (ej. DENTRO_DEL_PAIS/FUERA_DEL_PAIS).")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Estado de tránsito actualizado correctamente"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "404", description = "Vehículo no encontrado")
    })
    @PatchMapping("/patente/{patente}/estado")
    public ResponseEntity<Vehicle> actualizarEstado(
            @PathVariable String patente,
            @RequestParam String nuevoEstado) {
        return ResponseEntity.ok(
                vehicleService.actualizarEstado(patente, nuevoEstado));
    }

    // DELETE /api/v1/vehicles/1 : Remueve físicamente el registro de la base de datos según ID
    @Operation(summary = "Eliminar vehículo físicamente", description = "Remueve permanentemente el registro de un vehículo de la base de datos central.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Vehículo eliminado con éxito. Sin contenido."),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "404", description = "El vehículo especificado no existe")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        vehicleService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    // GET /api/v1/vehicles/propietario/12345678-9 : Filtra los vehículos asociados al RUN de un ciudadano
    @Operation(summary = "Listar vehículos por Propietario", description = "Filtra e identifica todos los vehículos vinculados al RUN de un ciudadano.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Vehículos del propietario obtenidos con éxito"),
            @ApiResponse(responseCode = "401", description = "No autenticado")
    })
    @GetMapping("/propietario/{rut}")
    public ResponseEntity<List<Vehicle>> obtenerPorPropietario(
            @PathVariable String rut) {
        return ResponseEntity.ok(vehicleService.obtenerPorPropietario(rut));
    }

    // GET /api/v1/vehicles/estado/FUERA_DEL_PAIS : Lista unidades según su ubicación actual
    @Operation(summary = "Listar vehículos por Estado Aduanero", description = "Filtra la lista de unidades según su ubicación actual en el tránsito aduanero.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Colección de vehículos filtrada por estado devuelta con éxito"),
            @ApiResponse(responseCode = "401", description = "No autenticado")
    })
    @GetMapping("/estado/{estado}")
    public ResponseEntity<List<Vehicle>> obtenerPorEstado(
            @PathVariable String estado) {
        return ResponseEntity.ok(vehicleService.obtenerPorEstado(estado));
    }

    // GET /api/v1/vehicles/tipo/PARTICULAR : Clasifica los vehículos según su categoría de uso registrado
    @Operation(summary = "Listar vehículos por Tipo de Uso", description = "Clasifica y retorna las unidades según su categoría de uso registrado (ej. PARTICULAR, CARGA, PASAJEROS).")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Vehículos filtrados por tipo obtenidos correctamente"),
            @ApiResponse(responseCode = "401", description = "No autenticado")
    })
    @GetMapping("/tipo/{tipo}")
    public ResponseEntity<List<Vehicle>> obtenerPorTipo(
            @PathVariable String tipo) {
        return ResponseEntity.ok(vehicleService.obtenerPorTipo(tipo));
    }

    // GET /api/v1/vehicles/propietario/12345678-9/estado/FUERA_DEL_PAIS
    @Operation(summary = "Listar vehículos por Propietario y Estado", description = "Cruza filtros para hallar los vehículos de un dueño en una situación de tránsito aduanero puntual.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Filtro cruzado ejecutado de forma exitosa"),
            @ApiResponse(responseCode = "401", description = "No autenticado")
    })
    @GetMapping("/propietario/{rut}/estado/{estado}")
    public ResponseEntity<List<Vehicle>> obtenerPorPropietarioYEstado(
            @PathVariable String rut,
            @PathVariable String estado) {
        return ResponseEntity.ok(
                vehicleService.obtenerPorPropietarioYEstado(rut, estado));
    }

    // GET /api/v1/vehicles/anio/desde/2020 : Filtra unidades cuyo año de fabricación sea igual o superior al parámetro
    @Operation(summary = "Listar vehículos desde un Año", description = "Filtra las unidades cuyo año de fabricación sea igual o superior al parámetro ingresado.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Colección filtrada obtenida con éxito"),
            @ApiResponse(responseCode = "401", description = "No autenticado")
    })
    @GetMapping("/anio/desde/{anio}")
    public ResponseEntity<List<Vehicle>> obtenerPorAnioDesde(
            @PathVariable Integer anio) {
        return ResponseEntity.ok(vehicleService.obtenerPorAnioDesde(anio));
    }

    // GET /api/v1/vehicles/anio/rango?desde=2018&hasta=2022
    @Operation(summary = "Listar vehículos por Rango de Años", description = "Recupera registros vehiculares contenidos en un bloque temporal cerrado de años.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Registros dentro del rango temporal devueltos con éxito"),
            @ApiResponse(responseCode = "400", description = "Rango de años inválido o mal estructurado"),
            @ApiResponse(responseCode = "401", description = "No autenticado")
    })
    @GetMapping("/anio/rango")
    public ResponseEntity<List<Vehicle>> obtenerPorRangoAnio(
            @RequestParam Integer desde,
            @RequestParam Integer hasta) {
        return ResponseEntity.ok(
                vehicleService.obtenerPorRangoAnio(desde, hasta));
    }

    // GET /api/v1/vehicles/buscar?marca=toyota
    @Operation(summary = "Buscar vehículos por Marca", description = "Realiza una búsqueda de texto con coincidencia parcial (LIKE) en la columna de marcas automotrices.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Coincidencias de marcas devueltas con éxito"),
            @ApiResponse(responseCode = "401", description = "No autenticado")
    })
    @GetMapping("/buscar")
    public ResponseEntity<List<Vehicle>> buscarPorMarca(
            @RequestParam String marca) {
        return ResponseEntity.ok(vehicleService.buscarPorMarca(marca));
    }

    // GET /api/v1/vehicles/propietario/12345678-9/ordenado
    @Operation(summary = "Listar vehículos de Propietario ordenados por Año", description = "Lista los vehículos de un RUN ordenados cronológicamente desde el modelo más nuevo al más antiguo.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista ordenada devuelta de forma exitosa"),
            @ApiResponse(responseCode = "401", description = "No autenticado")
    })
    @GetMapping("/propietario/{rut}/ordenado")
    public ResponseEntity<List<Vehicle>> obtenerPorPropietarioOrdenado(
            @PathVariable String rut) {
        return ResponseEntity.ok(
                vehicleService.obtenerPorPropietarioOrdenadoPorAnio(rut));
    }

    // GET /api/v1/vehicles/ultimos : Devuelve las últimas 10 inserciones de vehículos en el sistema
    @Operation(summary = "Listar últimos vehículos registrados", description = "Endpoint de auditoría que expone las últimas 10 transacciones vehiculares añadidas globalmente.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Historial de adiciones recuperado exitosamente"),
            @ApiResponse(responseCode = "401", description = "No autenticado")
    })
    @GetMapping("/ultimos")
    public ResponseEntity<List<Vehicle>> obtenerUltimosRegistrados() {
        return ResponseEntity.ok(vehicleService.obtenerUltimosRegistrados());
    }
}