// Recibe las peticiones HTTP de Vehicle Service y retorna ResponseEntity con JSON
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

@RestController
@RequestMapping("/api/v1/vehicles")
@RequiredArgsConstructor
public class VehicleController {

    private final VehicleService vehicleService;

    // GET /api/v1/vehicles : Lista todos los vehículos registrados en la base de datos
    @GetMapping
    public ResponseEntity<List<Vehicle>> obtenerTodos() {
        return ResponseEntity.ok(vehicleService.obtenerTodos());
    }

    // GET /api/v1/vehicles/1 : Busca un vehículo por su identificador primario
    @GetMapping("/{id}")
    public ResponseEntity<Vehicle> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(vehicleService.obtenerPorId(id));
    }

    // GET /api/v1/vehicles/patente/ABC123
    // Entry Service y Border Crossing Service usan este endpoint : Busca los datos de un vehículo mediante su placa patente única
    @GetMapping("/patente/{patente}")
    public ResponseEntity<Vehicle> obtenerPorPatente(
            @PathVariable String patente) {
        return ResponseEntity.ok(vehicleService.obtenerPorPatente(patente));
    }

    // POST /api/v1/vehicles : Registra un nuevo parque automotor aplicando reglas de validación
    @PostMapping
    public ResponseEntity<Vehicle> registrar(
            @Valid @RequestBody VehicleDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(vehicleService.registrar(dto));
    }

    // PUT /api/v1/vehicles/1 : Modifica los valores de un vehículo existente mediante su ID
    @PutMapping("/{id}")
    public ResponseEntity<Vehicle> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody VehicleDTO dto) {
        return ResponseEntity.ok(vehicleService.actualizar(id, dto));
    }

    // PATCH /api/v1/vehicles/patente/ABC123/estado?nuevoEstado=FUERA_DEL_PAIS
    // PATCH se usa para actualización parcial — solo el estado : Modifica de forma exclusiva la situación aduanera o de tránsito
    @PatchMapping("/patente/{patente}/estado")
    public ResponseEntity<Vehicle> actualizarEstado(
            @PathVariable String patente,
            @RequestParam String nuevoEstado) {
        return ResponseEntity.ok(
                vehicleService.actualizarEstado(patente, nuevoEstado));
    }

    // DELETE /api/v1/vehicles/1 : Remueve físicamente el registro de la base de datos según ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        vehicleService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    // GET /api/v1/vehicles/propietario/12345678-9 : Filtra los vehículos asociados al RUN de un ciudadano
    @GetMapping("/propietario/{rut}")
    public ResponseEntity<List<Vehicle>> obtenerPorPropietario(
            @PathVariable String rut) {
        return ResponseEntity.ok(vehicleService.obtenerPorPropietario(rut));
    }

    // GET /api/v1/vehicles/estado/FUERA_DEL_PAIS : Lista unidades según su ubicación actual (DENTRO_DEL_PAIS/FUERA_DEL_PAIS)
    @GetMapping("/estado/{estado}")
    public ResponseEntity<List<Vehicle>> obtenerPorEstado(
            @PathVariable String estado) {
        return ResponseEntity.ok(vehicleService.obtenerPorEstado(estado));
    }

    // GET /api/v1/vehicles/tipo/PARTICULAR : Clasifica los vehículos según su categoría de uso registrado
    @GetMapping("/tipo/{tipo}")
    public ResponseEntity<List<Vehicle>> obtenerPorTipo(
            @PathVariable String tipo) {
        return ResponseEntity.ok(vehicleService.obtenerPorTipo(tipo));
    }

    // GET /api/v1/vehicles/propietario/12345678-9/estado/FUERA_DEL_PAIS : Cruza filtros para hallar los vehículos de un dueño en un estado puntual
    @GetMapping("/propietario/{rut}/estado/{estado}")
    public ResponseEntity<List<Vehicle>> obtenerPorPropietarioYEstado(
            @PathVariable String rut,
            @PathVariable String estado) {
        return ResponseEntity.ok(
                vehicleService.obtenerPorPropietarioYEstado(rut, estado));
    }

    // GET /api/v1/vehicles/anio/desde/2020 : Filtra unidades cuyo año de fabricación sea igual o superior al parámetro
    @GetMapping("/anio/desde/{anio}")
    public ResponseEntity<List<Vehicle>> obtenerPorAnioDesde(
            @PathVariable Integer anio) {
        return ResponseEntity.ok(vehicleService.obtenerPorAnioDesde(anio));
    }

    // GET /api/v1/vehicles/anio/rango?desde=2018&hasta=2022 : Recupera registros vehiculares contenidos en un bloque temporal de años
    @GetMapping("/anio/rango")
    public ResponseEntity<List<Vehicle>> obtenerPorRangoAnio(
            @RequestParam Integer desde,
            @RequestParam Integer hasta) {
        return ResponseEntity.ok(
                vehicleService.obtenerPorRangoAnio(desde, hasta));
    }

    // GET /api/v1/vehicles/buscar?marca=toyota : Busca coincidencias parciales por texto en la cadena de marcas automotrices
    @GetMapping("/buscar")
    public ResponseEntity<List<Vehicle>> buscarPorMarca(
            @RequestParam String marca) {
        return ResponseEntity.ok(vehicleService.buscarPorMarca(marca));
    }

    // GET /api/v1/vehicles/propietario/12345678-9/ordenado : Lista vehículos de un RUN ordenados desde el modelo más nuevo al más antiguo
    @GetMapping("/propietario/{rut}/ordenado")
    public ResponseEntity<List<Vehicle>> obtenerPorPropietarioOrdenado(
            @PathVariable String rut) {
        return ResponseEntity.ok(
                vehicleService.obtenerPorPropietarioOrdenadoPorAnio(rut));
    }

    // GET /api/v1/vehicles/ultimos : Devuelve las últimas 10 inserciones de vehículos en el sistema
    @GetMapping("/ultimos")
    public ResponseEntity<List<Vehicle>> obtenerUltimosRegistrados() {
        return ResponseEntity.ok(vehicleService.obtenerUltimosRegistrados());
    }
}