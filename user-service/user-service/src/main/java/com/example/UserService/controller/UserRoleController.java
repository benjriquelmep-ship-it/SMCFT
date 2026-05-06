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

@RestController
// Todas las rutas empiezan con /api/v1/users/roles
@RequestMapping("/api/v1/users/roles")
@RequiredArgsConstructor
public class UserRoleController {
    private final UserRoleService userRoleService;

    // -------------------------------------------------------
    // CRUD BÁSICO
    // -------------------------------------------------------

    // GET /api/v1/users/roles → todos los roles del sistema
    @GetMapping
    public ResponseEntity<List<UserRole>> obtenerTodos() {
        return ResponseEntity.ok(userRoleService.obtenerTodos());
    }

    // GET /api/v1/users/roles/1 → rol por id
    @GetMapping("/{id}")
    public ResponseEntity<UserRole> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(userRoleService.obtenerPorId(id));
    }

    // POST /api/v1/users/roles → asignar nuevo rol a un usuario
    @PostMapping
    public ResponseEntity<UserRole> asignar(
            @Valid @RequestBody UserRoleDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(userRoleService.asignar(dto));
    }

    // DELETE /api/v1/users/roles/1 → eliminar un rol del historial
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        userRoleService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    // -------------------------------------------------------
    // CONSULTAS DERIVADAS
    // -------------------------------------------------------

    // GET /api/v1/users/roles/usuario/1
    // Historial completo de roles de un usuario
    @GetMapping("/usuario/{userId}")
    public ResponseEntity<List<UserRole>> obtenerPorUsuario(
            @PathVariable Long userId) {
        return ResponseEntity.ok(userRoleService.obtenerPorUsuario(userId));
    }

    // GET /api/v1/users/roles/usuario/1/activos
    // Solo el rol activo actual de un usuario
    @GetMapping("/usuario/{userId}/activos")
    public ResponseEntity<List<UserRole>> obtenerActivosPorUsuario(
            @PathVariable Long userId) {
        return ResponseEntity.ok(
                userRoleService.obtenerActivosPorUsuario(userId));
    }

    // GET /api/v1/users/roles/tipo/FISCALIZADOR
    // Todos los roles de un tipo específico
    @GetMapping("/tipo/{rol}")
    public ResponseEntity<List<UserRole>> obtenerPorRol(
            @PathVariable String rol) {
        return ResponseEntity.ok(userRoleService.obtenerPorRol(rol));
    }

    // GET /api/v1/users/roles/tipo/FISCALIZADOR/activos
    @GetMapping("/tipo/{rol}/activos")
    public ResponseEntity<List<UserRole>> obtenerActivosPorRol(
            @PathVariable String rol) {
        return ResponseEntity.ok(userRoleService.obtenerActivosPorRol(rol));
    }

    // GET /api/v1/users/roles/ultimos
    // Los últimos 10 roles asignados en el sistema
    @GetMapping("/ultimos")
    public ResponseEntity<List<UserRole>> obtenerUltimosAsignados() {
        return ResponseEntity.ok(userRoleService.obtenerUltimosAsignados());
    }
}
