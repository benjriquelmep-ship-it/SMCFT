// Recibe las peticiones HTTP del Notification Service
// Llama al Service y retorna ResponseEntity con JSON
// Nunca tiene lógica de negocio directamente
package com.example.NotificationService.controller;

import com.example.NotificationService.dto.NotificationDTO;
import com.example.NotificationService.model.Notification;
import com.example.NotificationService.service.NotificationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class NotificationController {

    // NotificationService contiene toda la lógica de negocio
    // El Controller solo recibe y responde — nunca tiene lógica propia
    private final NotificationService notificationService;

    // GET /api/v1/notifications
    // Devuelve todas las notificaciones del sistema
    @Operation(summary = "Obtener Todas", description = "Devuelve todas las notificaciones del sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operación exitosa"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "Sin permisos")
    })
    @GetMapping
    public ResponseEntity<List<Notification>> obtenerTodas() {
        return ResponseEntity.ok(notificationService.obtenerTodas());
    }

    // GET /api/v1/notifications/1
    // Devuelve una notificación específica por su id
    // @PathVariable extrae el {id} de la URL
    @Operation(summary = "Obtener Por Id", description = "@PathVariable extrae el {id} de la URL")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operación exitosa"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "Sin permisos")
    })
    @GetMapping("/{id}")
    public ResponseEntity<Notification> obtenerPorId(
            @PathVariable Long id) {
        return ResponseEntity.ok(notificationService.obtenerPorId(id));
    }

    // POST /api/v1/notifications
    // Crea una notificación manual
    @Operation(summary = "Crear", description = "Crea una notificación manual")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Operación exitosa"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "Sin permisos")
    })
    @PostMapping
    public ResponseEntity<Notification> crear(
            @Valid @RequestBody NotificationDTO dto) {
        // HTTP 201 = se creó un nuevo recurso exitosamente
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(notificationService.crear(dto));
    }

    // POST /api/v1/notifications/generar-desde-alertas
    // Genera notificaciones automáticamente desde las alertas pendientes
    // del Deadline Service
    // No necesita body — el Service consulta Deadline Service directamente
    // y crea una notificación por cada alerta no enviada
    @Operation(summary = "Generar Desde Alertas", description = "y crea una notificación por cada alerta no enviada")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Operación exitosa"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "Sin permisos")
    })
    @PostMapping("/generar-desde-alertas")
    public ResponseEntity<List<Notification>> generarDesdeAlertas() {
        // HTTP 201 = se crearon nuevos recursos exitosamente
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(notificationService
                        .generarDesdeAlertas());
    }

    // PATCH /api/v1/notifications/1/enviada
    // Marca una notificación como enviada al destinatario
    // Solo funciona si la notificación está PENDIENTE
    @Operation(summary = "Marcar Como Enviada", description = "Solo funciona si la notificación está PENDIENTE")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operación exitosa"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "Sin permisos")
    })
    @PatchMapping("/{id}/enviada")
    public ResponseEntity<Notification> marcarComoEnviada(
            @PathVariable Long id) {
        return ResponseEntity.ok(
                notificationService.marcarComoEnviada(id));
    }

    // PATCH /api/v1/notifications/1/error
    // Marca una notificación como ERROR — falló el envío
    // Útil para registrar que hubo un problema al enviar
    @Operation(summary = "Marcar Como Error", description = "Útil para registrar que hubo un problema al enviar")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operación exitosa"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "Sin permisos")
    })
    @PatchMapping("/{id}/error")
    public ResponseEntity<Notification> marcarComoError(
            @PathVariable Long id) {
        return ResponseEntity.ok(
                notificationService.marcarComoError(id));
    }

    // PUT /api/v1/notifications/1
    // Actualiza los datos editables de una notificación existente
    @Operation(summary = "Actualizar", description = "Actualiza los datos editables de una notificación existente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operación exitosa"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "Sin permisos")
    })
    @PutMapping("/{id}")
    public ResponseEntity<Notification> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody NotificationDTO dto) {
        return ResponseEntity.ok(notificationService.actualizar(id, dto));
    }

    // DELETE /api/v1/notifications/1
    // Elimina una notificación por su id
    // ResponseEntity<Void> = respuesta sin body → HTTP 204
    @Operation(summary = "Eliminar", description = "ResponseEntity<Void> = respuesta sin body → HTTP 204")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Operación exitosa"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "Sin permisos")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        notificationService.eliminar(id);
        // HTTP 204 = operación exitosa sin contenido en la respuesta
        return ResponseEntity.noContent().build();
    }

    // GET /api/v1/notifications/tipo/ALERTA_DEADLINE
    // Devuelve notificaciones por tipo
    @Operation(summary = "Obtener Por Tipo", description = "Devuelve notificaciones por tipo")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operación exitosa"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "Sin permisos")
    })
    @GetMapping("/tipo/{tipo}")
    public ResponseEntity<List<Notification>> obtenerPorTipo(
            @PathVariable String tipo) {
        return ResponseEntity.ok(
                notificationService.obtenerPorTipo(tipo));
    }

    // GET /api/v1/notifications/estado/PENDIENTE
    // Devuelve notificaciones por estado (PENDIENTE, ENVIADA, ERROR)
    @Operation(summary = "Obtener Por Estado", description = "Devuelve notificaciones por estado (PENDIENTE, ENVIADA, ERROR)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operación exitosa"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "Sin permisos")
    })
    @GetMapping("/estado/{estado}")
    public ResponseEntity<List<Notification>> obtenerPorEstado(
            @PathVariable String estado) {
        return ResponseEntity.ok(
                notificationService.obtenerPorEstado(estado));
    }

    // GET /api/v1/notifications/pendientes
    // Devuelve todas las notificaciones que aún no fueron enviadas
    // Atajo para /estado/PENDIENTE ordenadas del más reciente al más antiguo
    @Operation(summary = "Obtener Pendientes", description = "Atajo para /estado/PENDIENTE ordenadas del más reciente al más antiguo")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operación exitosa"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "Sin permisos")
    })
    @GetMapping("/pendientes")
    public ResponseEntity<List<Notification>> obtenerPendientes() {
        return ResponseEntity.ok(
                notificationService.obtenerPendientes());
    }

    // GET /api/v1/notifications/enviadas
    // Devuelve todas las notificaciones que ya fueron enviadas
    // Historial de notificaciones procesadas
    @Operation(summary = "Obtener Enviadas", description = "Historial de notificaciones procesadas")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operación exitosa"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "Sin permisos")
    })
    @GetMapping("/enviadas")
    public ResponseEntity<List<Notification>> obtenerEnviadas() {
        return ResponseEntity.ok(
                notificationService.obtenerEnviadas());
    }

    // GET /api/v1/notifications/buscar?titulo=alerta
    // Busca notificaciones cuyo título contenga el texto buscado
    // No necesitas escribir el título exacto — busca si lo contiene
    @Operation(summary = "Buscar Por Titulo", description = "No necesitas escribir el título exacto — busca si lo contiene")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operación exitosa"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "Sin permisos")
    })
    @GetMapping("/buscar")
    public ResponseEntity<List<Notification>> buscarPorTitulo(
            @RequestParam String titulo) {
        return ResponseEntity.ok(
                notificationService.buscarPorTitulo(titulo));
    }

    // GET /api/v1/notifications/ultimas
    // Devuelve las últimas 10 notificaciones registradas en el sistema
    @Operation(summary = "Obtener Ultimas", description = "Devuelve las últimas 10 notificaciones registradas en el sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operación exitosa"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "Sin permisos")
    })
    @GetMapping("/ultimas")
    public ResponseEntity<List<Notification>> obtenerUltimas() {
        return ResponseEntity.ok(
                notificationService.obtenerUltimasNotificaciones());
    }

    // GET /api/v1/notifications/estadisticas/estado/PENDIENTE
    // Cuenta cuántas notificaciones hay con ese estado
    @GetMapping("/estadisticas/estado/{estado}")
    public ResponseEntity<Map<String, Long>> contarPorEstado(
            @PathVariable String estado) {
        long total = notificationService.contarPorEstado(estado);
        return ResponseEntity.ok(Map.of("total", total));
    }

    // GET /api/v1/notifications/estadisticas/tipo/ALERTA_URGENTE
    // Cuenta cuántas notificaciones hay de ese tipo
    @GetMapping("/estadisticas/tipo/{tipo}")
    public ResponseEntity<Map<String, Long>> contarPorTipo(
            @PathVariable String tipo) {
        long total = notificationService.contarPorTipo(tipo);
        return ResponseEntity.ok(Map.of("total", total));
    }
}