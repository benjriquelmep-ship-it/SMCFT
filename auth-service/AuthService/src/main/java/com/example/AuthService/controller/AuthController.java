package com.example.AuthService.controller;

import com.example.AuthService.dto.LoginDTO;
import com.example.AuthService.dto.TokenResponseDTO;
import com.example.AuthService.model.LoginAttempt;
import com.example.AuthService.model.TokenBlacklist;
import com.example.AuthService.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
    @RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "1. Autenticación, Auditoría y Control de Sesiones", description = "Endpoints de seguridad centralizados responsables de la validación de credenciales, revocación de tokens (Blacklist) y trazabilidad forense de intentos de acceso")
public class AuthController {

    private final AuthService authService;

    @Operation(summary = "Autenticar Usuario / Emitir Token", description = "Procesa las credenciales de un usuario (email/password) contra el User Service. Si la validación es conforme, emite un token firmado JWT con sus roles operativos.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Identidad validada de forma conforme. Token emitido.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = TokenResponseDTO.class),
                            examples = @ExampleObject(value = "{\"token\":\"eyJhbGciOiJIUzI1NiJ9...\",\"rol\":\"ADMINISTRADOR\",\"mensaje\":\"Autenticación concedida con éxito.\"}"))),
            @ApiResponse(responseCode = "400", description = "La solicitud infringe validaciones de formato o campos obligatorios vacíos"),
            @ApiResponse(responseCode = "401", description = "Credenciales inválidas o cuenta de usuario inactiva administrativa")
    })
    @PostMapping("/login")
    public ResponseEntity<TokenResponseDTO> login(
            @Valid @RequestBody LoginDTO dto) {
        return ResponseEntity.ok(authService.login(dto));
    }

    @Operation(summary = "Cerrar Sesión Activa / Invalidar Token", description = "Revoca el ciclo de vida del JSON Web Token actual del usuario en tránsito, indexándolo de inmediato en la lista negra (Blacklist) del sistema.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Sesión revocada y token añadido a la lista de exclusión con éxito"),
            @ApiResponse(responseCode = "401", description = "Falta de token de seguridad o firma expirada")
    })
    @SecurityRequirement(name = "bearerAuth")
    @PostMapping("/logout")
    public ResponseEntity<Map<String, String>> logout(
            @RequestHeader("Authorization") String authHeader) {
        String token = authHeader.substring(7);
        authService.logout(token);
        return ResponseEntity.ok(
                Map.of("mensaje", "Sesión cerrada correctamente"));
    }

    @Operation(summary = "Verificar Vigencia de Token (Uso Interno Gateway)", description = "Endpoint de integración perimetral utilizado por el API Gateway u otros microservicios para comprobar si un token mantiene su integridad y no ha sido revocado.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Evaluación de firma y estado de exclusión finalizada con éxito")
    })
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/validar")
    public ResponseEntity<Map<String, Boolean>> validarToken(
            @RequestParam String token) {
        return ResponseEntity.ok(
                Map.of("valido", authService.validarToken(token)));
    }

    @Operation(summary = "Extraer Roles desde Token Cifrado", description = "Decodifica los claims del token provisto en el parámetro para aislar el perfil técnico/operativo del usuario.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Perfil extraído de forma conforme")
    })
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/rol")
    public ResponseEntity<Map<String, String>> obtenerRol(
            @RequestParam String token) {
        return ResponseEntity.ok(
                Map.of("rol", authService.obtenerRolDesdeToken(token)));
    }

    @Operation(summary = "Listar Bóveda Global de Tokens Invalidados (Blacklist)", description = "Recupera la totalidad de tokens JWT revocados administrativamente por acciones de logout voluntarias o cierres remotos.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Bóveda de exclusión expuesta con éxito"),
            @ApiResponse(responseCode = "403", description = "Privilegios insuficientes para auditar la lista negra")
    })
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/blacklist")
    public ResponseEntity<CollectionModel<EntityModel<TokenBlacklist>>> obtenerTodaLaBlacklist() {
        List<EntityModel<TokenBlacklist>> tokens = authService.obtenerTodaLaBlacklist().stream()
                .map(t -> EntityModel.of(t,
                        linkTo(methodOn(AuthController.class).obtenerBlacklistPorId(t.getId())).withSelfRel()))
                .toList();
        return ResponseEntity.ok(CollectionModel.of(tokens,
                linkTo(methodOn(AuthController.class).obtenerTodaLaBlacklist()).withSelfRel()));
    }

    @Operation(summary = "Obtener Registro de Revocación Específico", description = "Busca los metadatos de un token en lista negra (usuario titular, fecha de baja) utilizando su clave primaria.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Registro localizado de forma conforme"),
            @ApiResponse(responseCode = "404", description = "Identificador de revocación no hallado")
    })
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/blacklist/{id}")
    public ResponseEntity<EntityModel<TokenBlacklist>> obtenerBlacklistPorId(
            @PathVariable Long id) {
        TokenBlacklist token = authService.obtenerBlacklistPorId(id);
        return ResponseEntity.ok(EntityModel.of(token,
                linkTo(methodOn(AuthController.class).obtenerBlacklistPorId(id)).withSelfRel(),
                linkTo(methodOn(AuthController.class).obtenerTodaLaBlacklist()).withRel("tokens")));
    }

    @Operation(summary = "Remover Token de Blacklist / Rehabilitación", description = "Elimina físicamente el registro de la lista negra por su ID, permitiendo que el token recupere vigencia remota (si no ha expirado cronológicamente).")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Token removido de la lista negra. Sin contenido."),
            @ApiResponse(responseCode = "404", description = "Registro de exclusión no localizado")
    })
    @SecurityRequirement(name = "bearerAuth")
    @DeleteMapping("/blacklist/{id}")
    public ResponseEntity<Void> eliminarDeBlacklist(@PathVariable Long id) {
        authService.eliminarDeBlacklist(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Auditar Historial de Logouts por Usuario", description = "Filtra la traza de la lista negra para extraer cronológicamente todos los cierres de sesión voluntarios ejecutados por un correo específico.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Historial analítico de bajas recuperado con éxito")
    })
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/blacklist/historial/{email}")
    public ResponseEntity<CollectionModel<EntityModel<TokenBlacklist>>> obtenerHistorialLogout(
            @PathVariable String email) {
        List<EntityModel<TokenBlacklist>> tokens = authService.obtenerHistorialLogout(email).stream()
                .map(t -> EntityModel.of(t,
                        linkTo(methodOn(AuthController.class).obtenerBlacklistPorId(t.getId())).withSelfRel()))
                .toList();
        return ResponseEntity.ok(CollectionModel.of(tokens,
                linkTo(methodOn(AuthController.class).obtenerHistorialLogout(email)).withSelfRel()));
    }

    @Operation(summary = "Monitorear Últimas Revocaciones de Sesión", description = "Endpoint administrativo orientado a paneles de control en tiempo real para observar las últimas 10 bajas del sistema.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Panel de monitoreo de bajas recuperado de forma conforme")
    })
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/blacklist/ultimos")
    public ResponseEntity<CollectionModel<EntityModel<TokenBlacklist>>> obtenerUltimosLogouts() {
        List<EntityModel<TokenBlacklist>> tokens = authService.obtenerUltimosLogouts().stream()
                .map(t -> EntityModel.of(t,
                        linkTo(methodOn(AuthController.class).obtenerBlacklistPorId(t.getId())).withSelfRel()))
                .toList();
        return ResponseEntity.ok(CollectionModel.of(tokens,
                linkTo(methodOn(AuthController.class).obtenerUltimosLogouts()).withSelfRel()));
    }

    @Operation(summary = "Listar Historial de Intentos de Acceso", description = "Expone el registro consolidado global de todos los accesos solicitados al microservicio, permitiendo auditorías forenses.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Trazabilidad global de accesos expuesta correctamente")
    })
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/intentos")
    public ResponseEntity<CollectionModel<EntityModel<LoginAttempt>>> obtenerTodosLosIntentos() {
        List<EntityModel<LoginAttempt>> intentos = authService.obtenerTodosLosIntentos().stream()
                .map(i -> EntityModel.of(i,
                        linkTo(methodOn(AuthController.class).obtenerIntentoPorId(i.getId())).withSelfRel()))
                .toList();
        return ResponseEntity.ok(CollectionModel.of(intentos,
                linkTo(methodOn(AuthController.class).obtenerTodosLosIntentos()).withSelfRel()));
    }

    @Operation(summary = "Obtener Traza de Intento por ID", description = "Recupera la glosa técnica de un intento de autenticación (IP de origen, estado, marca de tiempo) por su identificador primario.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Registro de auditoría de acceso localizado"),
            @ApiResponse(responseCode = "404", description = "ID de intento de autenticación no hallado")
    })
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/intentos/{id}")
    public ResponseEntity<EntityModel<LoginAttempt>> obtenerIntentoPorId(
            @PathVariable Long id) {
        LoginAttempt intento = authService.obtenerIntentoPorId(id);
        return ResponseEntity.ok(EntityModel.of(intento,
                linkTo(methodOn(AuthController.class).obtenerIntentoPorId(id)).withSelfRel(),
                linkTo(methodOn(AuthController.class).obtenerTodosLosIntentos()).withRel("intentos")));
    }

    @Operation(summary = "Remover Registro de Intento del Sistema", description = "Elimina físicamente el log de auditoría de un intento de autenticación individual.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Log de auditoría de acceso purgado con éxito. Sin contenido.")
    })
    @SecurityRequirement(name = "bearerAuth")
    @DeleteMapping("/intentos/{id}")
    public ResponseEntity<Void> eliminarIntento(@PathVariable Long id) {
        authService.eliminarIntento(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Filtrar Intentos de Acceso por Usuario", description = "Extrae de forma unificada la totalidad de solicitudes de inicio de sesión de un usuario, agrupando resoluciones conformes y denegadas.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Filtro de auditoría por identidad completado")
    })
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/intentos/email/{email}")
    public ResponseEntity<CollectionModel<EntityModel<LoginAttempt>>> obtenerIntentosPorEmail(
            @PathVariable String email) {
        List<EntityModel<LoginAttempt>> intentos = authService.obtenerIntentosPorEmail(email).stream()
                .map(i -> EntityModel.of(i,
                        linkTo(methodOn(AuthController.class).obtenerIntentoPorId(i.getId())).withSelfRel()))
                .toList();
        return ResponseEntity.ok(CollectionModel.of(intentos,
                linkTo(methodOn(AuthController.class).obtenerIntentosPorEmail(email)).withSelfRel()));
    }

    @Operation(summary = "Detectar Intentos Denegados (Mitigación de Fuerza Bruta)", description = "Aísla y entrega exclusivamente los registros de autenticación rechazados para un usuario, útil para activar políticas de bloqueo preventivo.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Historial de accesos denegados obtenido con éxito")
    })
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/intentos/fallidos/{email}")
    public ResponseEntity<CollectionModel<EntityModel<LoginAttempt>>> obtenerIntentosFallidos(
            @PathVariable String email) {
        List<EntityModel<LoginAttempt>> intentos = authService.obtenerIntentosFallidos(email).stream()
                .map(i -> EntityModel.of(i,
                        linkTo(methodOn(AuthController.class).obtenerIntentoPorId(i.getId())).withSelfRel()))
                .toList();
        return ResponseEntity.ok(CollectionModel.of(intentos,
                linkTo(methodOn(AuthController.class).obtenerIntentosFallidos(email)).withSelfRel()));
    }

    @Operation(summary = "Monitorear Últimos Intentos de Acceso Perimetral", description = "Muestra de manera segregada los últimos 10 logs de inicio de sesión registrados en el servidor en tiempo real.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Dashboard de monitoreo perimetral devuelto de forma conforme")
    })
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/intentos/ultimos")
    public ResponseEntity<CollectionModel<EntityModel<LoginAttempt>>> obtenerUltimosIntentos() {
        List<EntityModel<LoginAttempt>> intentos = authService.obtenerUltimosIntentos().stream()
                .map(i -> EntityModel.of(i,
                        linkTo(methodOn(AuthController.class).obtenerIntentoPorId(i.getId())).withSelfRel()))
                .toList();
        return ResponseEntity.ok(CollectionModel.of(intentos,
                linkTo(methodOn(AuthController.class).obtenerUltimosIntentos()).withSelfRel()));
    }

    @Operation(summary = "Filtrar Intentos de Acceso por Rango Cronológico", description = "Permite extraer la traza forense de autenticaciones delimitando una fecha y hora inicial (desde) y una fecha y hora final (hasta).")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Registros cronológicos devueltos con éxito"),
            @ApiResponse(responseCode = "400", description = "El formato ISO-860ik de las cadenas de fecha provistas es incorrecto")
    })
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/intentos/rango")
    public ResponseEntity<CollectionModel<EntityModel<LoginAttempt>>> obtenerIntentosPorFechas(
            @RequestParam String desde,
            @RequestParam String hasta) {
        LocalDateTime fechaDesde = LocalDateTime.parse(desde);
        LocalDateTime fechaHasta = LocalDateTime.parse(hasta);
        List<EntityModel<LoginAttempt>> intentos = authService.obtenerIntentosPorFechas(fechaDesde, fechaHasta).stream()
                .map(i -> EntityModel.of(i,
                        linkTo(methodOn(AuthController.class).obtenerIntentoPorId(i.getId())).withSelfRel()))
                .toList();
        return ResponseEntity.ok(CollectionModel.of(intentos,
                linkTo(methodOn(AuthController.class).obtenerIntentosPorFechas(desde, hasta)).withSelfRel()));
    }
}