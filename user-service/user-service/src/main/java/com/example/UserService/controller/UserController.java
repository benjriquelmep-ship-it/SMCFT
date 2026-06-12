// Recibe las peticiones HTTP de User Service y retorna ResponseEntity con JSON
package com.example.UserService.controller;

// Solo recibe la petición HTTP, llama al Service y devuelve la respuesta

import com.example.UserService.dto.UserDTO;
import com.example.UserService.model.User;
import com.example.UserService.service.UserService;
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

@RestController
@RequestMapping("/api/v1/users")   // Todas las rutas empiezan con /api/users
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class UserController {
    // Spring inyecta el UserService automáticamente
    private final UserService userService;

    // GET /api/v1/users → obtener todos los usuarios : Lista todos los usuarios registrados
    // ResponseEntity.ok() envuelve la respuesta con HTTP 200
    @Operation(summary = "Obtener Todos", description = "ResponseEntity.ok() envuelve la respuesta con HTTP 200")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operación exitosa"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "Sin permisos")
    })
    @GetMapping
    public ResponseEntity<List<User>> obtenerTodos() {
        return ResponseEntity.ok(userService.obtenerTodos());
    }

    // GET /api/v1/users/1 : Busca un usuario por su ID primario
    // @PathVariable extrae el {id} de la URL
    @Operation(summary = "Obtener Por Id", description = "@PathVariable extrae el {id} de la URL")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operación exitosa"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "Sin permisos")
    })
    @GetMapping("/{id}")
    public ResponseEntity<User> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(userService.obtenerPorId(id));
    }

    // GET /api/v1/users/email/juan@gmail.com : Obtiene un usuario mediante su correo electrónico institucional
    // Auth Service llama este endpoint durante el login
    @Operation(summary = "Obtener Por Email", description = "Auth Service llama este endpoint durante el login")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operación exitosa"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "Sin permisos")
    })
    @GetMapping("/email/{email}")
    public ResponseEntity<User> obtenerPorEmail(@PathVariable String email) {
        return ResponseEntity.ok(userService.obtenerPorEmail(email));
    }

    // GET /api/v1/users/rut/12345678-9 : Obtiene un usuario mediante su RUN/RUT de identidad chileno
    // Vehicle Service llama este endpoint para verificar propietarios
    @Operation(summary = "Obtener Por Rut", description = "Vehicle Service llama este endpoint para verificar propietarios")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operación exitosa"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "Sin permisos")
    })
    @GetMapping("/rut/{rut}")
    public ResponseEntity<User> obtenerPorRut(@PathVariable String rut) {
        return ResponseEntity.ok(userService.obtenerPorRut(rut));
    }

    // POST /api/v1/users → crear nuevo usuario : Registra un nuevo usuario aplicando reglas de validación
    // @Valid activa las validaciones del UserDTO
    // @RequestBody convierte el JSON del body al DTO automáticamente
    @Operation(summary = "Crear", description = "@RequestBody convierte el JSON del body al DTO automáticamente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Operación exitosa"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "Sin permisos")
    })
    @PostMapping
    public ResponseEntity<User> crear(@Valid @RequestBody UserDTO dto) {
        // HTTP 201 Created — se creó un nuevo recurso
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(userService.crear(dto));
    }

    // PUT /api/v1/users/1 → actualizar usuario existente : Reemplaza los datos de un usuario por su ID
    @Operation(summary = "Actualizar", description = "PUT /api/v1/users/1 → actualizar usuario existente : Reemplaza los datos de un usuario por su ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operación exitosa"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "Sin permisos")
    })
    @PutMapping("/{id}")
    public ResponseEntity<User> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody UserDTO dto) {
        // HTTP 200 OK — actualización exitosa
        return ResponseEntity.ok(userService.actualizar(id, dto));
    }

    // DELETE /api/v1/users/1 → desactivar usuario (soft delete) : Desactiva lógicamente el flag de vigencia
    @Operation(summary = "Eliminar", description = "DELETE /api/v1/users/1 → desactivar usuario (soft delete) : Desactiva lógicamente el flag de vigencia")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Operación exitosa"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "Sin permisos")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        userService.eliminar(id);
        // HTTP 204 No Content — operación exitosa sin body en la respuesta
        return ResponseEntity.noContent().build();
    }

    // GET /api/v1/users/activos : Filtra únicamente las cuentas vigentes del sistema
    @Operation(summary = "Obtener Activos", description = "GET /api/v1/users/activos : Filtra únicamente las cuentas vigentes del sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operación exitosa"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "Sin permisos")
    })
    @GetMapping("/activos")
    public ResponseEntity<List<User>> obtenerActivos() {
        return ResponseEntity.ok(userService.obtenerActivos());
    }

    // GET /api/v1/users/inactivos : Filtra cuentas que sufrieron soft delete o están deshabilitadas
    @Operation(summary = "Obtener Inactivos", description = "GET /api/v1/users/inactivos : Filtra cuentas que sufrieron soft delete o están deshabilitadas")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operación exitosa"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "Sin permisos")
    })
    @GetMapping("/inactivos")
    public ResponseEntity<List<User>> obtenerInactivos() {
        return ResponseEntity.ok(userService.obtenerInactivos());
    }

    // GET /api/v1/users/rol/FISCALIZADOR : Lista usuarios pertenecientes a un perfil de accesos específico
    @Operation(summary = "Obtener Por Rol", description = "GET /api/v1/users/rol/FISCALIZADOR : Lista usuarios pertenecientes a un perfil de accesos específico")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operación exitosa"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "Sin permisos")
    })
    @GetMapping("/rol/{rol}")
    public ResponseEntity<List<User>> obtenerPorRol(@PathVariable String rol) {
        return ResponseEntity.ok(userService.obtenerPorRol(rol));
    }

    // GET /api/v1/users/rol/FISCALIZADOR/activos : Cruza filtros para obtener usuarios vigentes de un perfil determinado
    @Operation(summary = "Obtener Activos Por Rol", description = "GET /api/v1/users/rol/FISCALIZADOR/activos : Cruza filtros para obtener usuarios vigentes de un perfil determinado")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operación exitosa"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "Sin permisos")
    })
    @GetMapping("/rol/{rol}/activos")
    public ResponseEntity<List<User>> obtenerActivosPorRol(
            @PathVariable String rol) {
        return ResponseEntity.ok(userService.obtenerActivosPorRol(rol));
    }

    // GET /api/v1/users/buscar?nombre=juan : Busca coincidencias parciales por texto en la cadena de nombres
    // @RequestParam extrae el parámetro de la query string (?nombre=juan)
    @Operation(summary = "Buscar Por Nombre", description = "@RequestParam extrae el parámetro de la query string (?nombre=juan)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operación exitosa"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "Sin permisos")
    })
    @GetMapping("/buscar")
    public ResponseEntity<List<User>> buscarPorNombre(
            @RequestParam String nombre) {
        return ResponseEntity.ok(userService.buscarPorNombre(nombre));
    }

    // GET /api/v1/users/activos/ordenados : Lista cuentas vigentes ordenadas alfabéticamente por nombre
    @Operation(summary = "Obtener Activos Ordenados", description = "GET /api/v1/users/activos/ordenados : Lista cuentas vigentes ordenadas alfabéticamente por nombre")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operación exitosa"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "Sin permisos")
    })
    @GetMapping("/activos/ordenados")
    public ResponseEntity<List<User>> obtenerActivosOrdenados() {
        return ResponseEntity.ok(userService.obtenerActivosOrdenados());
    }
}