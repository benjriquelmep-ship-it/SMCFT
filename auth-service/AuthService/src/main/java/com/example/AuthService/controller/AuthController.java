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
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
// Todas las rutas empiezan con /api/v1/auth
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "1. Autenticación, Auditoría y Control de Sesiones", description = "Endpoints de seguridad centralizados responsables de la validación de credenciales, revocación de tokens (Blacklist) y trazabilidad forense de intentos de acceso")
@SecurityRequirement(name = "bearerAuth")
public class AuthController {

    // AuthService contiene toda la lógica de negocio
    // El Controller solo recibe y responde — nunca tiene lógica propia
    private final AuthService authService;

    // POST /api/v1/auth/login — ruta pública, no necesita token
    // El cliente manda email y password en el body
    // @Valid activa las validaciones del LoginDTO
    @Operation(summary = "Autenticar Usuario / Emitir Token", description = "Procesa las credenciales de un usuario (email/password) contra el User Service. Si la validación es conforme, emite un token firmado JWT con sus roles operativos.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Identidad validada de forma conforme. Token emitido."),
            @ApiResponse(responseCode = "400", description = "La solicitud infringe validaciones de formato o campos obligatorios vacíos"),
            @ApiResponse(responseCode = "401", description = "Credenciales inválidas o cuenta de usuario inactiva administrativa")
    })
    @PostMapping("/login")
    public ResponseEntity<TokenResponseDTO> login(
            @Valid @RequestBody LoginDTO dto) {
        return ResponseEntity.ok(authService.login(dto));
    }

    // POST /api/v1/auth/logout
    // Invalida el token actual del usuario
    // El cliente manda el token en el header Authorization
    @Operation(summary = "Cerrar Sesión Activa / Invalidar Token", description = "Revoca el ciclo de vida del JSON Web Token actual del usuario en tránsito, indexándolo de inmediato en la lista negra (Blacklist) del sistema.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Sesión revocada y token añadido a la lista de exclusión con éxito"),
            @ApiResponse(responseCode = "401", description = "Falta de token de seguridad o firma expirada")
    })
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
    @Operation(summary = "Verificar Vigencia de Token (Uso Interno Gateway)", description = "Endpoint de integración perimetral utilizado por el API Gateway u otros microservicios para comprobar si un token mantiene su integridad y no ha sido revocado.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Evaluación de firma y estado de exclusión finalizada con éxito")
    })
    @GetMapping("/validar")
    public ResponseEntity<Map<String, Boolean>> validarToken(
            @RequestParam String token) {
        return ResponseEntity.ok(
                Map.of("valido", authService.validarToken(token)));
    }

    // GET /api/v1/auth/rol?token=... : Extrae el rol del usuario desde el token : Responde: { "rol": "ADMINISTRADOR" }
    @Operation(summary = "Extraer Roles desde Token Cifrado", description = "Decodifica los claims del token provisto en el parámetro para aislar el perfil técnico/operativo del usuario.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Perfil extraído de forma conforme")
    })
    @GetMapping("/rol")
    public ResponseEntity<Map<String, String>> obtenerRol(
            @RequestParam String token) {
        return ResponseEntity.ok(
                Map.of("rol", authService.obtenerRolDesdeToken(token)));
    }

    // GET /api/v1/auth/blacklist : Devuelve todos los tokens invalidados del sistema
    @Operation(summary = "Listar Bóveda Global de Tokens Invalidados (Blacklist)", description = "Recupera la totalidad de tokens JWT revocados administrativamente por acciones de logout voluntarias o cierres remotos.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Bóveda de exclusión expuesta con éxito"),
            @ApiResponse(responseCode = "403", description = "Privilegios insuficientes para auditar la lista negra")
    })
    @GetMapping("/blacklist")
    public ResponseEntity<List<TokenBlacklist>> obtenerTodaLaBlacklist() {
        return ResponseEntity.ok(authService.obtenerTodaLaBlacklist());
    }

    // GET /api/v1/auth/blacklist/1 : Devuelve un token invalidado específico por su id
    @Operation(summary = "Obtener Registro de Revocación Específico", description = "Busca los metadatos de un token en lista negra (usuario titular, fecha de baja) utilizando su clave primaria.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Registro localizado de forma conforme"),
            @ApiResponse(responseCode = "404", description = "Identificador de revocación no hallado")
    })
    @GetMapping("/blacklist/{id}")
    public ResponseEntity<TokenBlacklist> obtenerBlacklistPorId(
            @PathVariable Long id) {
        return ResponseEntity.ok(authService.obtenerBlacklistPorId(id));
    }

    // DELETE /api/v1/auth/blacklist/1 : Elimina un token de la blacklist
    @Operation(summary = "Remover Token de Blacklist / Rehabilitación", description = "Elimina físicamente el registro de la lista negra por su ID, permitiendo que el token recupere vigencia remota (si no ha expirado cronológicamente).")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Token removido de la lista negra. Sin contenido."),
            @ApiResponse(responseCode = "404", description = "Registro de exclusión no localizado")
    })
    @DeleteMapping("/blacklist/{id}")
    public ResponseEntity<Void> eliminarDeBlacklist(@PathVariable Long id) {
        authService.eliminarDeBlacklist(id);
        return ResponseEntity.noContent().build();
    }

    // GET /api/v1/auth/blacklist/historial/juan@gmail.com : Devuelve todos los logouts que hizo un usuario específico
    // Útil para ver cuántas veces cerró sesión ese usuario
    @Operation(summary = "Auditar Historial de Logouts por Usuario", description = "Filtra la traza de la lista negra para extraer cronológicamente todos los cierres de sesión voluntarios ejecutados por un correo específico.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Historial analítico de bajas recuperado con éxito")
    })
    @GetMapping("/blacklist/historial/{email}")
    public ResponseEntity<List<TokenBlacklist>> obtenerHistorialLogout(
            @PathVariable String email) {
        return ResponseEntity.ok(authService.obtenerHistorialLogout(email));
    }

    // GET /api/v1/auth/blacklist/ultimos : Devuelve los últimos 10 tokens invalidados del sistema
    @Operation(summary = "Monitorear Últimas Revocaciones de Sesión", description = "Endpoint administrativo orientado a paneles de control en tiempo real para observar las últimas 10 bajas del sistema.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Panel de monitoreo de bajas recuperado de forma conforme")
    })
    @GetMapping("/blacklist/ultimos")
    public ResponseEntity<List<TokenBlacklist>> obtenerUltimosLogouts() {
        return ResponseEntity.ok(authService.obtenerUltimosLogouts());
    }

    // GET /api/v1/auth/intentos : Devuelve todos los intentos de login del sistema
    @Operation(summary = "Listar Historial de Intentos de Acceso", description = "Expone el registro consolidado global de todos los accesos solicitados al microservicio, permitiendo auditorías forenses.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Trazabilidad global de accesos expuesta correctamente")
    })
    @GetMapping("/intentos")
    public ResponseEntity<List<LoginAttempt>> obtenerTodosLosIntentos() {
        return ResponseEntity.ok(authService.obtenerTodosLosIntentos());
    }

    // GET /api/v1/auth/intentos/1 : Devuelve un intento de login específico por su id
    @Operation(summary = "Obtener Traza de Intento por ID", description = "Recupera la glosa técnica de un intento de autenticación (IP de origen, estado, marca de tiempo) por su identificador primario.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Registro de auditoría de acceso localizado"),
            @ApiResponse(responseCode = "404", description = "ID de intento de autenticación no hallado")
    })
    @GetMapping("/intentos/{id}")
    public ResponseEntity<LoginAttempt> obtenerIntentoPorId(
            @PathVariable Long id) {
        return ResponseEntity.ok(authService.obtenerIntentoPorId(id));
    }

    // DELETE /api/v1/auth/intentos/1 : Elimina un intento de login por su id
    @Operation(summary = "Remover Registro de Intento del Sistema", description = "Elimina físicamente el log de auditoría de un intento de autenticación individual.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Log de auditoría de acceso purgado con éxito. Sin contenido.")
    })
    @DeleteMapping("/intentos/{id}")
    public ResponseEntity<Void> eliminarIntento(@PathVariable Long id) {
        authService.eliminarIntento(id);
        return ResponseEntity.noContent().build();
    }

    // GET /api/v1/auth/intentos/email/juan@gmail.com : Devuelve todos los intentos de login de un usuario específico
    // Incluye exitosos y fallidos
    @Operation(summary = "Filtrar Intentos de Acceso por Usuario", description = "Extrae de forma unificada la totalidad de solicitudes de inicio de sesión de un usuario, agrupando resoluciones conformes y denegadas.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Filtro de auditoría por identidad completado")
    })
    @GetMapping("/intentos/email/{email}")
    public ResponseEntity<List<LoginAttempt>> obtenerIntentosPorEmail(
            @PathVariable String email) {
        return ResponseEntity.ok(authService.obtenerIntentosPorEmail(email));
    }

    // GET /api/v1/auth/intentos/fallidos/juan@gmail.com : Devuelve solo los intentos FALLIDOS de un usuario
    @Operation(summary = "Detectar Intentos Denegados (Mitigación de Fuerza Bruta)", description = "Aísla y entrega exclusivamente los registros de autenticación rechazados para un usuario, útil para activar políticas de bloqueo preventivo.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Historial de accesos denegados obtenido con éxito")
    })
    @GetMapping("/intentos/fallidos/{email}")
    public ResponseEntity<List<LoginAttempt>> obtenerIntentosFallidos(
            @PathVariable String email) {
        return ResponseEntity.ok(authService.obtenerIntentosFallidos(email));
    }

    // GET /api/v1/auth/intentos/ultimos : Devuelve los últimos 10 intentos de login del sistema
    @Operation(summary = "Monitorear Últimos Intentos de Acceso Perimetral", description = "Muestra de manera segregada los últimos 10 logs de inicio de sesión registrados en el servidor en tiempo real.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Dashboard de monitoreo perimetral devuelto de forma conforme")
    })
    @GetMapping("/intentos/ultimos")
    public ResponseEntity<List<LoginAttempt>> obtenerUltimosIntentos() {
        return ResponseEntity.ok(authService.obtenerUltimosIntentos());
    }

    // GET /api/v1/auth/intentos/rango?desde=2025-01-01T00:00:00&hasta=2026-12-31T23:59:59 : Devuelve intentos de login en un rango de fechas
    @Operation(summary = "Filtrar Intentos de Acceso por Rango Cronológico", description = "Permite extraer la traza forense de autenticaciones delimitando una fecha y hora inicial (desde) y una fecha y hora final (hasta).")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Registros cronológicos devueltos con éxito"),
            @ApiResponse(responseCode = "400", description = "El formato ISO-860ik de las cadenas de fecha provistas es incorrecto")
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