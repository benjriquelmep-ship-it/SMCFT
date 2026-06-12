// Recibe las peticiones HTTP, llama al Service y retorna ResponseEntity
// Nunca tiene lógica de negocio directamente

package com.example.AuthService.controller;

import com.example.AuthService.dto.LoginDTO;
import com.example.AuthService.dto.TokenResponseDTO;
import com.example.AuthService.model.LoginAttempt;
import com.example.AuthService.model.TokenBlacklist;
import com.example.AuthService.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@RestController

// Todas las rutas empiezan con /api/v1/auth
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class AuthController {

    // AuthService contiene toda la lógica de negocio
    // El Controller solo recibe y responde — nunca tiene lógica propia
    private final AuthService authService;

    // POST /api/v1/auth/login — ruta pública, no necesita token
    // El cliente manda email y password en el body
    // @Valid activa las validaciones del LoginDTO
    @Operation(summary = "Login", description = "@Valid activa las validaciones del LoginDTO")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Operación exitosa"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "Sin permisos")
    })
    @PostMapping("/login")
    public ResponseEntity<TokenResponseDTO> login(
            @Valid @RequestBody LoginDTO dto) {
        return ResponseEntity.ok(authService.login(dto));
    }

    // POST /api/v1/auth/logout
    // Invalida el token actual del usuario
    // El cliente manda el token en el header Authorization
    @PostMapping("/logout")
    public ResponseEntity<Map<String, String>> logout(
            @RequestHeader("Authorization") String authHeader) {
        String token = authHeader.substring(7);
        authService.logout(token);
        return ResponseEntity.ok(
                Map.of("mensaje", "Sesión cerrada correctamente"));
    }

    // GET /api/v1/auth/validar?token=...
    // Verifica que el token sea válido Y no esté en la blacklist
    // Otros microservicios usan este endpoint para verificar tokens
    @GetMapping("/validar")
    public ResponseEntity<Map<String, Boolean>> validarToken(
            @RequestParam String token) {
        return ResponseEntity.ok(
                Map.of("valido", authService.validarToken(token)));
    }

    // GET /api/v1/auth/rol?token=... : Extrae el rol del usuario desde el token : Responde: { "rol": "ADMINISTRADOR" }
    @GetMapping("/rol")
    public ResponseEntity<Map<String, String>> obtenerRol(
            @RequestParam String token) {
        return ResponseEntity.ok(
                Map.of("rol", authService.obtenerRolDesdeToken(token)));
    }

    // GET /api/v1/auth/blacklist : Devuelve todos los tokens invalidados del sistema
    @Operation(summary = "Obtener Toda La Blacklist", description = "GET /api/v1/auth/blacklist : Devuelve todos los tokens invalidados del sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operación exitosa"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "Sin permisos")
    })
    @GetMapping("/blacklist")
    public ResponseEntity<List<TokenBlacklist>> obtenerTodaLaBlacklist() {
        return ResponseEntity.ok(authService.obtenerTodaLaBlacklist());
    }

    // GET /api/v1/auth/blacklist/1 : Devuelve un token invalidado específico por su id
    @Operation(summary = "Obtener Blacklist Por Id", description = "GET /api/v1/auth/blacklist/1 : Devuelve un token invalidado específico por su id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operación exitosa"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "Sin permisos")
    })
    @GetMapping("/blacklist/{id}")
    public ResponseEntity<TokenBlacklist> obtenerBlacklistPorId(
            @PathVariable Long id) {
        return ResponseEntity.ok(authService.obtenerBlacklistPorId(id));
    }

    // DELETE /api/v1/auth/blacklist/1 : Elimina un token de la blacklist
    @Operation(summary = "Eliminar De Blacklist", description = "DELETE /api/v1/auth/blacklist/1 : Elimina un token de la blacklist")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Operación exitosa"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "Sin permisos")
    })
    @DeleteMapping("/blacklist/{id}")
    public ResponseEntity<Void> eliminarDeBlacklist(@PathVariable Long id) {
        authService.eliminarDeBlacklist(id);
        return ResponseEntity.noContent().build();
    }

    // GET /api/v1/auth/blacklist/historial/juan@gmail.com : Devuelve todos los logouts que hizo un usuario específico
    // Útil para ver cuántas veces cerró sesión ese usuario
    @Operation(summary = "Obtener Historial Logout", description = "Útil para ver cuántas veces cerró sesión ese usuario")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operación exitosa"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "Sin permisos")
    })
    @GetMapping("/blacklist/historial/{email}")
    public ResponseEntity<List<TokenBlacklist>> obtenerHistorialLogout(
            @PathVariable String email) {
        return ResponseEntity.ok(authService.obtenerHistorialLogout(email));
    }

    // GET /api/v1/auth/blacklist/ultimos : Devuelve los últimos 10 tokens invalidados del sistema
    @Operation(summary = "Obtener Ultimos Logouts", description = "GET /api/v1/auth/blacklist/ultimos : Devuelve los últimos 10 tokens invalidados del sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operación exitosa"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "Sin permisos")
    })
    @GetMapping("/blacklist/ultimos")
    public ResponseEntity<List<TokenBlacklist>> obtenerUltimosLogouts() {
        return ResponseEntity.ok(authService.obtenerUltimosLogouts());
    }

    // GET /api/v1/auth/intentos : Devuelve todos los intentos de login del sistema
    @Operation(summary = "Obtener Todos Los Intentos", description = "GET /api/v1/auth/intentos : Devuelve todos los intentos de login del sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operación exitosa"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "Sin permisos")
    })
    @GetMapping("/intentos")
    public ResponseEntity<List<LoginAttempt>> obtenerTodosLosIntentos() {
        return ResponseEntity.ok(authService.obtenerTodosLosIntentos());
    }

    // GET /api/v1/auth/intentos/1 : Devuelve un intento de login específico por su id
    @Operation(summary = "Obtener Intento Por Id", description = "GET /api/v1/auth/intentos/1 : Devuelve un intento de login específico por su id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operación exitosa"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "Sin permisos")
    })
    @GetMapping("/intentos/{id}")
    public ResponseEntity<LoginAttempt> obtenerIntentoPorId(
            @PathVariable Long id) {
        return ResponseEntity.ok(authService.obtenerIntentoPorId(id));
    }

    // DELETE /api/v1/auth/intentos/1 : Elimina un intento de login por su id
    @Operation(summary = "Eliminar Intento", description = "DELETE /api/v1/auth/intentos/1 : Elimina un intento de login por su id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Operación exitosa"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "Sin permisos")
    })
    @DeleteMapping("/intentos/{id}")
    public ResponseEntity<Void> eliminarIntento(@PathVariable Long id) {
        authService.eliminarIntento(id);
        return ResponseEntity.noContent().build();
    }

    // GET /api/v1/auth/intentos/email/juan@gmail.com : Devuelve todos los intentos de login de un usuario específico
    // Incluye exitosos y fallidos
    @Operation(summary = "Obtener Intentos Por Email", description = "Incluye exitosos y fallidos")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operación exitosa"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "Sin permisos")
    })
    @GetMapping("/intentos/email/{email}")
    public ResponseEntity<List<LoginAttempt>> obtenerIntentosPorEmail(
            @PathVariable String email) {
        return ResponseEntity.ok(authService.obtenerIntentosPorEmail(email));
    }

    // GET /api/v1/auth/intentos/fallidos/juan@gmail.com : Devuelve solo los intentos FALLIDOS de un usuario
    @Operation(summary = "Obtener Intentos Fallidos", description = "GET /api/v1/auth/intentos/fallidos/juan@gmail.com : Devuelve solo los intentos FALLIDOS de un usuario")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operación exitosa"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "Sin permisos")
    })
    @GetMapping("/intentos/fallidos/{email}")
    public ResponseEntity<List<LoginAttempt>> obtenerIntentosFallidos(
            @PathVariable String email) {
        return ResponseEntity.ok(authService.obtenerIntentosFallidos(email));
    }

    // GET /api/v1/auth/intentos/ultimos : Devuelve los últimos 10 intentos de login del sistema
    @Operation(summary = "Obtener Ultimos Intentos", description = "GET /api/v1/auth/intentos/ultimos : Devuelve los últimos 10 intentos de login del sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operación exitosa"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "Sin permisos")
    })
    @GetMapping("/intentos/ultimos")
    public ResponseEntity<List<LoginAttempt>> obtenerUltimosIntentos() {
        return ResponseEntity.ok(authService.obtenerUltimosIntentos());
    }

    // GET /api/v1/auth/intentos/rango?desde=2025-01-01T00:00:00&hasta=2026-12-31T23:59:59 : Devuelve intentos de login en un rango de fechas
    @Operation(summary = "Obtener Intentos Por Fechas", description = "GET /api/v1/auth/intentos/rango?desde=2025-01-01T00:00:00&hasta=2026-12-31T23:59:59 : Devuelve intentos de login en un rango de fechas")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operación exitosa"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "Sin permisos")
    })
    @GetMapping("/intentos/rango")
    public ResponseEntity<List<LoginAttempt>> obtenerIntentosPorFechas(
            @RequestParam String desde,
            @RequestParam String hasta) {
        LocalDateTime fechaDesde = LocalDateTime.parse(desde);
        LocalDateTime fechaHasta = LocalDateTime.parse(hasta);
        return ResponseEntity.ok(
                authService.obtenerIntentosPorFechas(fechaDesde, fechaHasta));
    }
}