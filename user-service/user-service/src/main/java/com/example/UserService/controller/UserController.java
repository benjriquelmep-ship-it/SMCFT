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

@RestController
@RequestMapping("/api/v1/users")   // Todas las rutas empiezan con /api/users
@RequiredArgsConstructor
public class UserController {
    // Spring inyecta el UserService automáticamente
    private final UserService userService;

    // GET /api/v1/users → obtener todos los usuarios : Lista todos los usuarios registrados
    // ResponseEntity.ok() envuelve la respuesta con HTTP 200
    @GetMapping
    public ResponseEntity<List<User>> obtenerTodos() {
        return ResponseEntity.ok(userService.obtenerTodos());
    }

    // GET /api/v1/users/1 : Busca un usuario por su ID primario
    // @PathVariable extrae el {id} de la URL
    @GetMapping("/{id}")
    public ResponseEntity<User> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(userService.obtenerPorId(id));
    }

    // GET /api/v1/users/email/juan@gmail.com : Obtiene un usuario mediante su correo electrónico institucional
    // Auth Service llama este endpoint durante el login
    @GetMapping("/email/{email}")
    public ResponseEntity<User> obtenerPorEmail(@PathVariable String email) {
        return ResponseEntity.ok(userService.obtenerPorEmail(email));
    }

    // GET /api/v1/users/rut/12345678-9 : Obtiene un usuario mediante su RUN/RUT de identidad chileno
    // Vehicle Service llama este endpoint para verificar propietarios
    @GetMapping("/rut/{rut}")
    public ResponseEntity<User> obtenerPorRut(@PathVariable String rut) {
        return ResponseEntity.ok(userService.obtenerPorRut(rut));
    }

    // POST /api/v1/users → crear nuevo usuario : Registra un nuevo usuario aplicando reglas de validación
    // @Valid activa las validaciones del UserDTO
    // @RequestBody convierte el JSON del body al DTO automáticamente
    @PostMapping
    public ResponseEntity<User> crear(@Valid @RequestBody UserDTO dto) {
        // HTTP 201 Created — se creó un nuevo recurso
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(userService.crear(dto));
    }

    // PUT /api/v1/users/1 → actualizar usuario existente : Reemplaza los datos de un usuario por su ID
    @PutMapping("/{id}")
    public ResponseEntity<User> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody UserDTO dto) {
        // HTTP 200 OK — actualización exitosa
        return ResponseEntity.ok(userService.actualizar(id, dto));
    }

    // DELETE /api/v1/users/1 → desactivar usuario (soft delete) : Desactiva lógicamente el flag de vigencia
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        userService.eliminar(id);
        // HTTP 204 No Content — operación exitosa sin body en la respuesta
        return ResponseEntity.noContent().build();
    }

    // GET /api/v1/users/activos : Filtra únicamente las cuentas vigentes del sistema
    @GetMapping("/activos")
    public ResponseEntity<List<User>> obtenerActivos() {
        return ResponseEntity.ok(userService.obtenerActivos());
    }

    // GET /api/v1/users/inactivos : Filtra cuentas que sufrieron soft delete o están deshabilitadas
    @GetMapping("/inactivos")
    public ResponseEntity<List<User>> obtenerInactivos() {
        return ResponseEntity.ok(userService.obtenerInactivos());
    }

    // GET /api/v1/users/rol/FISCALIZADOR : Lista usuarios pertenecientes a un perfil de accesos específico
    @GetMapping("/rol/{rol}")
    public ResponseEntity<List<User>> obtenerPorRol(@PathVariable String rol) {
        return ResponseEntity.ok(userService.obtenerPorRol(rol));
    }

    // GET /api/v1/users/rol/FISCALIZADOR/activos : Cruza filtros para obtener usuarios vigentes de un perfil determinado
    @GetMapping("/rol/{rol}/activos")
    public ResponseEntity<List<User>> obtenerActivosPorRol(
            @PathVariable String rol) {
        return ResponseEntity.ok(userService.obtenerActivosPorRol(rol));
    }

    // GET /api/v1/users/buscar?nombre=juan : Busca coincidencias parciales por texto en la cadena de nombres
    // @RequestParam extrae el parámetro de la query string (?nombre=juan)
    @GetMapping("/buscar")
    public ResponseEntity<List<User>> buscarPorNombre(
            @RequestParam String nombre) {
        return ResponseEntity.ok(userService.buscarPorNombre(nombre));
    }

    // GET /api/v1/users/activos/ordenados : Lista cuentas vigentes ordenadas alfabéticamente por nombre
    @GetMapping("/activos/ordenados")
    public ResponseEntity<List<User>> obtenerActivosOrdenados() {
        return ResponseEntity.ok(userService.obtenerActivosOrdenados());
    }
}