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

@RestController

// Todas las rutas empiezan con /api/v1/auth
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    // AuthService contiene toda la lógica de negocio
    // El Controller solo recibe y responde — nunca tiene lógica propia
    private final AuthService authService;

    // POST /api/v1/auth/login — ruta pública, no necesita token
    // El cliente manda email y password en el body
    // @Valid activa las validaciones del LoginDTO
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
    @GetMapping("/blacklist")
    public ResponseEntity<List<TokenBlacklist>> obtenerTodaLaBlacklist() {
        return ResponseEntity.ok(authService.obtenerTodaLaBlacklist());
    }

    // GET /api/v1/auth/blacklist/1 : Devuelve un token invalidado específico por su id
    @GetMapping("/blacklist/{id}")
    public ResponseEntity<TokenBlacklist> obtenerBlacklistPorId(
            @PathVariable Long id) {
        return ResponseEntity.ok(authService.obtenerBlacklistPorId(id));
    }

    // DELETE /api/v1/auth/blacklist/1 : Elimina un token de la blacklist
    @DeleteMapping("/blacklist/{id}")
    public ResponseEntity<Void> eliminarDeBlacklist(@PathVariable Long id) {
        authService.eliminarDeBlacklist(id);
        return ResponseEntity.noContent().build();
    }

    // GET /api/v1/auth/blacklist/historial/juan@gmail.com : Devuelve todos los logouts que hizo un usuario específico
    // Útil para ver cuántas veces cerró sesión ese usuario
    @GetMapping("/blacklist/historial/{email}")
    public ResponseEntity<List<TokenBlacklist>> obtenerHistorialLogout(
            @PathVariable String email) {
        return ResponseEntity.ok(authService.obtenerHistorialLogout(email));
    }

    // GET /api/v1/auth/blacklist/ultimos : Devuelve los últimos 10 tokens invalidados del sistema
    @GetMapping("/blacklist/ultimos")
    public ResponseEntity<List<TokenBlacklist>> obtenerUltimosLogouts() {
        return ResponseEntity.ok(authService.obtenerUltimosLogouts());
    }

    // GET /api/v1/auth/intentos : Devuelve todos los intentos de login del sistema
    @GetMapping("/intentos")
    public ResponseEntity<List<LoginAttempt>> obtenerTodosLosIntentos() {
        return ResponseEntity.ok(authService.obtenerTodosLosIntentos());
    }

    // GET /api/v1/auth/intentos/1 : Devuelve un intento de login específico por su id
    @GetMapping("/intentos/{id}")
    public ResponseEntity<LoginAttempt> obtenerIntentoPorId(
            @PathVariable Long id) {
        return ResponseEntity.ok(authService.obtenerIntentoPorId(id));
    }

    // DELETE /api/v1/auth/intentos/1 : Elimina un intento de login por su id
    @DeleteMapping("/intentos/{id}")
    public ResponseEntity<Void> eliminarIntento(@PathVariable Long id) {
        authService.eliminarIntento(id);
        return ResponseEntity.noContent().build();
    }

    // GET /api/v1/auth/intentos/email/juan@gmail.com : Devuelve todos los intentos de login de un usuario específico
    // Incluye exitosos y fallidos
    @GetMapping("/intentos/email/{email}")
    public ResponseEntity<List<LoginAttempt>> obtenerIntentosPorEmail(
            @PathVariable String email) {
        return ResponseEntity.ok(authService.obtenerIntentosPorEmail(email));
    }

    // GET /api/v1/auth/intentos/fallidos/juan@gmail.com : Devuelve solo los intentos FALLIDOS de un usuario
    @GetMapping("/intentos/fallidos/{email}")
    public ResponseEntity<List<LoginAttempt>> obtenerIntentosFallidos(
            @PathVariable String email) {
        return ResponseEntity.ok(authService.obtenerIntentosFallidos(email));
    }

    // GET /api/v1/auth/intentos/ultimos : Devuelve los últimos 10 intentos de login del sistema
    @GetMapping("/intentos/ultimos")
    public ResponseEntity<List<LoginAttempt>> obtenerUltimosIntentos() {
        return ResponseEntity.ok(authService.obtenerUltimosIntentos());
    }

    // GET /api/v1/auth/intentos/rango?desde=2025-01-01T00:00:00&hasta=2026-12-31T23:59:59 : Devuelve intentos de login en un rango de fechas
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