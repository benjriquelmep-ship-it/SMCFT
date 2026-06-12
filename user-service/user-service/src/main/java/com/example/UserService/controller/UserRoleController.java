// Recibe las peticiones HTTP de User Role Service y retorna ResponseEntity con JSON
package com.example.UserService.controller;

import com.example.UserService.dto.UserRoleDTO;
import com.example.UserService.model.UserRole;
import com.example.UserService.service.UserRoleService;
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
// Todas las rutas empiezan con /api/v1/users/roles
@RequestMapping("/api/v1/users/roles")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class UserRoleController {
    private final UserRoleService userRoleService;

    // GET /api/v1/users/roles → todos los roles del sistema : Lista todas las asignaciones de roles globales
    @Operation(summary = "Obtener Todos", description = "GET /api/v1/users/roles → todos los roles del sistema : Lista todas las asignaciones de roles globales")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operación exitosa"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "Sin permisos")
    })
    @GetMapping
    public ResponseEntity<List<UserRole>> obtenerTodos() {
        return ResponseEntity.ok(userRoleService.obtenerTodos());
    }

    // GET /api/v1/users/roles/1 → rol por id : Busca una asignación específica mediante su clave primaria
    @Operation(summary = "Obtener Por Id", description = "GET /api/v1/users/roles/1 → rol por id : Busca una asignación específica mediante su clave primaria")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operación exitosa"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "Sin permisos")
    })
    @GetMapping("/{id}")
    public ResponseEntity<UserRole> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(userRoleService.obtenerPorId(id));
    }

    // POST /api/v1/users/roles → asignar nuevo rol a un usuario : Registra una nueva vinculación de perfil para un usuario
    @Operation(summary = "Asignar", description = "POST /api/v1/users/roles → asignar nuevo rol a un usuario : Registra una nueva vinculación de perfil para un usuario")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Operación exitosa"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "Sin permisos")
    })
    @PostMapping
    public ResponseEntity<UserRole> asignar(
            @Valid @RequestBody UserRoleDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(userRoleService.asignar(dto));
    }

    // DELETE /api/v1/users/roles/1 → eliminar un rol del historial : Remueve físicamente una asignación por su ID
    @Operation(summary = "Eliminar", description = "DELETE /api/v1/users/roles/1 → eliminar un rol del historial : Remueve físicamente una asignación por su ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Operación exitosa"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "Sin permisos")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        userRoleService.eliminar(id);
        return ResponseEntity.noContent().build();
    }


    // GET /api/v1/users/roles/usuario/1 : Filtra los perfiles que han sido asignados históricamente a un usuario
    // Historial completo de roles de un usuario
    @Operation(summary = "Obtener Por Usuario", description = "Historial completo de roles de un usuario")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operación exitosa"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "Sin permisos")
    })
    @GetMapping("/usuario/{userId}")
    public ResponseEntity<List<UserRole>> obtenerPorUsuario(
            @PathVariable Long userId) {
        return ResponseEntity.ok(userRoleService.obtenerPorUsuario(userId));
    }

    // GET /api/v1/users/roles/usuario/1/activos : Obtiene la asignación que se encuentra vigente para el usuario
    // Solo el rol activo actual de un usuario
    @Operation(summary = "Obtener Activos Por Usuario", description = "Solo el rol activo actual de un usuario")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operación exitosa"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "Sin permisos")
    })
    @GetMapping("/usuario/{userId}/activos")
    public ResponseEntity<List<UserRole>> obtenerActivosPorUsuario(
            @PathVariable Long userId) {
        return ResponseEntity.ok(
                userRoleService.obtenerActivosPorUsuario(userId));
    }

    // GET /api/v1/users/roles/tipo/FISCALIZADOR : Lista todas las asignaciones históricas de un tipo de perfil
    // Todos los roles de un tipo específico
    @Operation(summary = "Obtener Por Rol", description = "Todos los roles de un tipo específico")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operación exitosa"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "Sin permisos")
    })
    @GetMapping("/tipo/{rol}")
    public ResponseEntity<List<UserRole>> obtenerPorRol(
            @PathVariable String rol) {
        return ResponseEntity.ok(userRoleService.obtenerPorRol(rol));
    }

    // GET /api/v1/users/roles/tipo/FISCALIZADOR/activos : Filtra usuarios que tienen el perfil indicado vigente actualmente
    // Tipo de rol activo actual
    @Operation(summary = "Obtener Activos Por Rol", description = "Tipo de rol activo actual")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operación exitosa"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "Sin permisos")
    })
    @GetMapping("/tipo/{rol}/activos")
    public ResponseEntity<List<UserRole>> obtenerActivosPorRol(
            @PathVariable String rol) {
        return ResponseEntity.ok(userRoleService.obtenerActivosPorRol(rol));
    }

    // GET /api/v1/users/roles/ultimos : Devuelve los últimos 10 cambios de perfil registrados globalmente
    // Los últimos 10 roles asignados en el sistema
    @Operation(summary = "Obtener Ultimos Asignados", description = "Los últimos 10 roles asignados en el sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operación exitosa"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "Sin permisos")
    })
    @GetMapping("/ultimos")
    public ResponseEntity<List<UserRole>> obtenerUltimosAsignados() {
        return ResponseEntity.ok(userRoleService.obtenerUltimosAsignados());
    }
}