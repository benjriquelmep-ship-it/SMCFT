// Recibe las peticiones HTTP de User Service y retorna ResponseEntity con JSON
package com.example.UserService.controller;

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
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Tag(name = "Usuarios", description = "Endpoints para la gestión, registro y consulta de ciudadanos y funcionarios en el sistema fronterizo")
@SecurityRequirement(name = "bearerAuth")
public class UserController {

    private final UserService userService;

    // GET /api/v1/users → obtener todos los usuarios : Lista todos los usuarios registrados
    @Operation(summary = "Listar todos los usuarios", description = "Recupera la colección global de usuarios registrados en el sistema.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de usuarios recuperada con éxito"),
            @ApiResponse(responseCode = "401", description = "No autenticado - Falta token Bearer JWT"),
            @ApiResponse(responseCode = "403", description = "Sin permisos - Rol no autorizado")
    })
    @GetMapping
    public ResponseEntity<List<User>> obtenerTodos() {
        return ResponseEntity.ok(userService.obtenerTodos());
    }

    // GET /api/v1/users/1 : Busca un usuario por su ID primario
    @Operation(summary = "Obtener usuario por ID", description = "Busca en la base de datos la ficha detallada de un usuario a partir de su clave primaria.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuario localizado correctamente"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "Sin permisos"),
            @ApiResponse(responseCode = "404", description = "El ID de usuario solicitado no existe")
    })
    @GetMapping("/{id}")
    public ResponseEntity<User> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(userService.obtenerPorId(id));
    }

    // GET /api/v1/users/email/juan@gmail.com : Obtiene un usuario mediante su correo electrónico institucional
    @Operation(summary = "Obtener usuario por Email", description = "Recupera los datos del usuario utilizando su correo electrónico. Endpoint consumido por Auth Service durante el login.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuario autenticado/localizado correctamente"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "404", description = "No existe ninguna cuenta asociada a este correo")
    })
    @GetMapping("/email/{email}")
    public ResponseEntity<User> obtenerPorEmail(@PathVariable String email) {
        return ResponseEntity.ok(userService.obtenerPorEmail(email));
    }

    // GET /api/v1/users/rut/12345678-9 : Obtiene un usuario mediante su RUN/RUT de identidad chileno
    @Operation(summary = "Obtener usuario por RUN/RUT", description = "Busca a un ciudadano mediante su identificador nacional chileno. Consumido por Vehicle Service para validar propietarios.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ciudadano verificado correctamente"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "404", description = "El RUN ingresado no pertenece a ningún usuario registrado")
    })
    @GetMapping("/rut/{rut}")
    public ResponseEntity<User> obtenerPorRut(@PathVariable String rut) {
        return ResponseEntity.ok(userService.obtenerPorRut(rut));
    }

    // POST /api/v1/users → crear nuevo usuario : Registra un nuevo usuario aplicando reglas de validación
    @Operation(summary = "Registrar un nuevo usuario", description = "Valida el esquema del payload y da de alta un nuevo usuario en la base de datos centralizada.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Usuario creado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Estructura del JSON inválida o datos mal formateados"),
            @ApiResponse(responseCode = "401", description = "No autenticado")
    })
    @PostMapping
    public ResponseEntity<User> crear(@Valid @RequestBody UserDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(userService.crear(dto));
    }

    // PUT /api/v1/users/1 → actualizar usuario existente : Reemplaza los datos de un usuario por su ID
    @Operation(summary = "Actualizar usuario existente", description = "Sobreescribe las propiedades de un usuario en base a su clave primaria.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuario actualizado correctamente"),
            @ApiResponse(responseCode = "400", description = "Datos de actualización inválidos"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "404", description = "El usuario a modificar no fue localizado")
    })
    @PutMapping("/{id}")
    public ResponseEntity<User> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody UserDTO dto) {
        return ResponseEntity.ok(userService.actualizar(id, dto));
    }

    // DELETE /api/v1/users/1 → desactivar usuario (soft delete) : Desactiva lógicamente el flag de vigencia
    @Operation(summary = "Dar de baja un usuario (Soft Delete)", description = "Desactiva lógicamente la cuenta de un usuario cambiando su estado de vigencia a inactivo.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Usuario deshabilitado exitosamente. Sin contenido de retorno."),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "404", description = "El usuario especificado no existe")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        userService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    // GET /api/v1/users/activos : Filtra únicamente las cuentas vigentes del sistema
    @Operation(summary = "Listar usuarios activos", description = "Retorna únicamente los usuarios que se encuentran habilitados y vigentes en el sistema.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Colección de usuarios activos devuelta con éxito"),
            @ApiResponse(responseCode = "401", description = "No autenticado")
    })
    @GetMapping("/activos")
    public ResponseEntity<List<User>> obtenerActivos() {
        return ResponseEntity.ok(userService.obtenerActivos());
    }

    // GET /api/v1/users/inactivos : Filtra cuentas que sufrieron soft delete o están deshabilitadas
    @Operation(summary = "Listar usuarios inactivos", description = "Retorna la lista de usuarios deshabilitados o que sufrieron una baja lógica.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Colección de usuarios inactivos devuelta con éxito"),
            @ApiResponse(responseCode = "401", description = "No autenticado")
    })
    @GetMapping("/inactivos")
    public ResponseEntity<List<User>> obtenerInactivos() {
        return ResponseEntity.ok(userService.obtenerInactivos());
    }

    // GET /api/v1/users/rol/FISCALIZADOR : Lista usuarios pertenecientes a un perfil de accesos específico
    @Operation(summary = "Filtrar usuarios por Rol", description = "Filtra e identifica a las cuentas de usuario que posean un perfil de acceso específico (ej. ADMINISTRADOR, FISCALIZADOR, VIAJERO).")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuarios filtrados por rol obtenidos correctamente"),
            @ApiResponse(responseCode = "401", description = "No autenticado")
    })
    @GetMapping("/rol/{rol}")
    public ResponseEntity<List<User>> obtenerPorRol(@PathVariable String rol) {
        return ResponseEntity.ok(userService.obtenerPorRol(rol));
    }

    // GET /api/v1/users/rol/FISCALIZADOR/activos : Cruza filtros para obtener usuarios vigentes de un perfil determinado
    @Operation(summary = "Filtrar usuarios activos por Rol", description = "Cruza lógica de filtros para aislar las cuentas que además de pertenecer al rol indicado estén vigentes de forma activa.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Filtro cruzado ejecutado con éxito"),
            @ApiResponse(responseCode = "401", description = "No autenticado")
    })
    @GetMapping("/rol/{rol}/activos")
    public ResponseEntity<List<User>> obtenerActivosPorRol(
            @PathVariable String rol) {
        return ResponseEntity.ok(userService.obtenerActivosPorRol(rol));
    }

    // GET /api/v1/users/buscar?nombre=juan : Busca coincidencias parciales por texto en la cadena de nombres
    @Operation(summary = "Buscar usuarios por coincidencia de nombre", description = "Realiza una consulta de texto con coincidencia parcial (LIKE) en el campo de nombres.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Coincidencias encontradas devueltas de forma exitosa"),
            @ApiResponse(responseCode = "401", description = "No autenticado")
    })
    @GetMapping("/buscar")
    public ResponseEntity<List<User>> buscarPorNombre(
            @RequestParam String nombre) {
        return ResponseEntity.ok(userService.buscarPorNombre(nombre));
    }

    // GET /api/v1/users/activos/ordenados : Lista cuentas vigentes ordenadas alfabéticamente por nombre
    @Operation(summary = "Listar usuarios activos ordenados alfabéticamente", description = "Devuelve la lista de cuentas activas ordenadas de la A a la Z basándose en la propiedad nombre.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Colección ordenada devuelta con éxito"),
            @ApiResponse(responseCode = "401", description = "No autenticado")
    })
    @GetMapping("/activos/ordenados")
    public ResponseEntity<List<User>> obtenerActivosOrdenados() {
        return ResponseEntity.ok(userService.obtenerActivosOrdenados());
    }
}