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
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
    @RequestMapping("/api/v1/users/roles")
@RequiredArgsConstructor
@Tag(name = "Roles de Usuario", description = "Endpoints para la asignación, revocación e historial de perfiles de acceso aduaneros")
@SecurityRequirement(name = "bearerAuth")
public class UserRoleController {

    private final UserRoleService userRoleService;

    @Operation(summary = "Listar todas las asignaciones de roles", description = "Muestra el registro completo de asignaciones de roles hechas en la plataforma.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Colección histórica de roles devuelta con éxito"),
            @ApiResponse(responseCode = "401", description = "No autenticado - Token inválido o ausente"),
            @ApiResponse(responseCode = "403", description = "Sin permisos de administrador")
    })
    @GetMapping
    public ResponseEntity<List<UserRole>> obtenerTodos() {
        return ResponseEntity.ok(userRoleService.obtenerTodos());
    }

    // GET /api/v1/users/roles/1 → rol por id : Busca una asignación específica mediante su clave primaria
    @Operation(summary = "Obtener asignación de rol por ID", description = "Busca el registro individual de un rol histórico basándose en su ID único.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Asignación de rol localizada correctamente"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "Sin permisos"),
            @ApiResponse(responseCode = "404", description = "El ID del registro de rol solicitado no fue encontrado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<UserRole> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(userRoleService.obtenerPorId(id));
    }

    // POST /api/v1/users/roles → asignar nuevo rol a un usuario : Registra una nueva vinculación de perfil para un usuario
    @Operation(summary = "Asignar un nuevo rol a un usuario", description = "Crea un registro de asignación de perfil vinculándolo de manera mandatoria a un ID de usuario.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Rol asignado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Estructura DTO errónea o datos incompatibles"),
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
    @Operation(summary = "Eliminar un rol del historial", description = "Remueve permanentemente el registro físico de la asignación de un perfil específico en la base de datos.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Asignación de rol eliminada correctamente. Sin contenido de retorno."),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "Sin permisos"),
            @ApiResponse(responseCode = "404", description = "El ID del registro de rol no existe")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        userRoleService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    // GET /api/v1/users/roles/usuario/1 : Filtra los perfiles que han sido asignados históricamente a un usuario
    @Operation(summary = "Obtener historial de roles de un usuario", description = "Lista la secuencia histórica completa de roles que ha poseído un usuario a lo largo del tiempo.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Historial de roles del usuario obtenido con éxito"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "Sin permisos")
    })
    @GetMapping("/usuario/{userId}")
    public ResponseEntity<List<UserRole>> obtenerPorUsuario(
            @PathVariable Long userId) {
        return ResponseEntity.ok(userRoleService.obtenerPorUsuario(userId));
    }

    // GET /api/v1/users/roles/usuario/1/activos : Obtiene la asignación que se encuentra vigente para el usuario
    @Operation(summary = "Obtener el rol activo actual de un usuario", description = "Retorna de manera exclusiva la asignación de rol que se encuentra vigente en el instante actual de la consulta.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Rol activo actual obtenido correctamente"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "Sin permisos"),
            @ApiResponse(responseCode = "404", description = "El usuario no cuenta con un perfil activo asignado")
    })
    @GetMapping("/usuario/{userId}/activos")
    public ResponseEntity<List<UserRole>> obtenerActivosPorUsuario(
            @PathVariable Long userId) {
        return ResponseEntity.ok(
                userRoleService.obtenerActivosPorUsuario(userId));
    }

    // GET /api/v1/users/roles/tipo/FISCALIZADOR : Lista todas las asignaciones históricas de un tipo de perfil
    @Operation(summary = "Listar asignaciones por tipo de rol específico", description = "Lista de forma masiva a todos los usuarios vinculados históricamente a una categoría de rol.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Registros mapeados correctamente"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "Sin permisos")
    })
    @GetMapping("/tipo/{rol}")
    public ResponseEntity<List<UserRole>> obtenerPorRol(
            @PathVariable String rol) {
        return ResponseEntity.ok(userRoleService.obtenerPorRol(rol));
    }

    // GET /api/v1/users/roles/tipo/FISCALIZADOR/activos : Filtra usuarios que tienen el perfil indicado vigente actualmente
    @Operation(summary = "Listar usuarios con rol activo específico", description = "Filtra la base de datos para extraer únicamente a los usuarios cuyo rol activo y vigente sea el ingresado en el parámetro.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Colección de usuarios con rol vigente devuelta con éxito"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "Sin permisos")
    })
    @GetMapping("/tipo/{rol}/activos")
    public ResponseEntity<List<UserRole>> obtenerActivosPorRol(
            @PathVariable String rol) {
        return ResponseEntity.ok(userRoleService.obtenerActivosPorRol(rol));
    }

    // GET /api/v1/users/roles/ultimos : Devuelve los últimos 10 cambios de perfil registrados globalmente
    @Operation(summary = "Listar las últimas 10 asignaciones de rol", description = "Endpoint de control de auditoría administrativa que expone las últimas 10 transacciones de roles efectuadas en la plataforma.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Historial de auditoría de roles recuperado exitosamente"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "Sin permisos")
    })
    @GetMapping("/ultimos")
    public ResponseEntity<List<UserRole>> obtenerUltimosAsignados() {
        return ResponseEntity.ok(userRoleService.obtenerUltimosAsignados());
    }
}