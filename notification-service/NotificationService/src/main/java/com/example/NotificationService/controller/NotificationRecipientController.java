// Recibe las peticiones HTTP para los destinatarios de notificaciones
// Llama al Service y retorna ResponseEntity con JSON
// Nunca tiene lógica de negocio directamente
package com.example.NotificationService.controller;

import com.example.NotificationService.dto.NotificationRecipientDTO;   // formulario de entrada
import com.example.NotificationService.model.NotificationRecipient;    // modelo de destinatario
import com.example.NotificationService.service.NotificationRecipientService; // lógica de negocio
import jakarta.validation.Valid;                                         // valida el formulario
import lombok.RequiredArgsConstructor;                                   // inyecta dependencias
import org.springframework.http.HttpStatus;                              // códigos HTTP
import org.springframework.http.ResponseEntity;                          // envuelve la respuesta
import org.springframework.web.bind.annotation.*;                        // anotaciones REST
import java.util.List;                                                   // para listas
import java.util.Map;                                                    // para { "total": x }
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@RestController
@RequestMapping("/api/v1/notification-recipients")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class NotificationRecipientController {

    // NotificationRecipientService contiene toda la lógica de negocio
    // El Controller solo recibe y responde — nunca tiene lógica propia
    private final NotificationRecipientService recipientService;

    // GET /api/v1/notification-recipients
    // Devuelve todos los destinatarios del sistema
    @Operation(summary = "Obtener Todos", description = "Devuelve todos los destinatarios del sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operación exitosa"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "Sin permisos")
    })
    @GetMapping
    public ResponseEntity<List<NotificationRecipient>> obtenerTodos() {
        return ResponseEntity.ok(recipientService.obtenerTodos());
    }

    // GET /api/v1/notification-recipients/1
    // Devuelve un destinatario específico por su id
    @Operation(summary = "Obtener Por Id", description = "Devuelve un destinatario específico por su id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operación exitosa"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "Sin permisos")
    })
    @GetMapping("/{id}")
    public ResponseEntity<NotificationRecipient> obtenerPorId(
            @PathVariable Long id) {
        return ResponseEntity.ok(recipientService.obtenerPorId(id));
    }

    // POST /api/v1/notification-recipients
    // Agrega un nuevo destinatario a una notificación existente
    @Operation(summary = "Agregar", description = "Agrega un nuevo destinatario a una notificación existente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Operación exitosa"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "Sin permisos")
    })
    @PostMapping
    public ResponseEntity<NotificationRecipient> agregar(
            @Valid @RequestBody NotificationRecipientDTO dto) {
        // HTTP 201 = se creó un nuevo recurso exitosamente
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(recipientService.agregar(dto));
    }

    // PATCH /api/v1/notification-recipients/1/leida
    // Marca que un destinatario leyó la notificación
    // Cambia leida de false a true y guarda la fecha de lectura
    // Ruta pública — el viajero puede marcar sin token
    @Operation(summary = "Marcar Como Leida", description = "Ruta pública — el viajero puede marcar sin token")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operación exitosa"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "Sin permisos")
    })
    @PatchMapping("/{id}/leida")
    public ResponseEntity<NotificationRecipient> marcarComoLeida(
            @PathVariable Long id) {
        return ResponseEntity.ok(recipientService.marcarComoLeida(id));
    }

    // DELETE /api/v1/notification-recipients/1
    // Elimina un destinatario por su id
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
        recipientService.eliminar(id);
        // HTTP 204 = operación exitosa sin contenido en la respuesta
        return ResponseEntity.noContent().build();
    }

    // GET /api/v1/notification-recipients/notificacion/1
    // Devuelve todos los destinatarios de una notificación específica
    @Operation(summary = "Obtener Por Notificacion", description = "Devuelve todos los destinatarios de una notificación específica")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operación exitosa"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "Sin permisos")
    })
    @GetMapping("/notificacion/{notificationId}")
    public ResponseEntity<List<NotificationRecipient>> obtenerPorNotificacion(
            @PathVariable Long notificationId) {
        return ResponseEntity.ok(
                recipientService.obtenerPorNotificacion(notificationId));
    }

    // GET /api/v1/notification-recipients/notificacion/1/no-leidos
    // Devuelve los destinatarios que AÚN NO leyeron una notificación específica
    // Útil para saber a quiénes hay que recordarles la notificación
    @Operation(summary = "Obtener No Leidos", description = "Útil para saber a quiénes hay que recordarles la notificación")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operación exitosa"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "Sin permisos")
    })
    @GetMapping("/notificacion/{notificationId}/no-leidos")
    public ResponseEntity<List<NotificationRecipient>> obtenerNoLeidos(
            @PathVariable Long notificationId) {
        return ResponseEntity.ok(
                recipientService
                        .obtenerNoLeidosPorNotificacion(notificationId));
    }

    // GET /api/v1/notification-recipients/destinatario/12345678-9
    // Devuelve todas las notificaciones asignadas a un destinatario específico
    // Incluye leídas y no leídas
    @Operation(summary = "Obtener Por Destinatario", description = "Incluye leídas y no leídas")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operación exitosa"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "Sin permisos")
    })
    @GetMapping("/destinatario/{rut}")
    public ResponseEntity<List<NotificationRecipient>> obtenerPorDestinatario(
            @PathVariable String rut) {
        return ResponseEntity.ok(
                recipientService.obtenerPorDestinatario(rut));
    }

    // GET /api/v1/notification-recipients/destinatario/12345678-9/no-leidas
    // Devuelve solo las notificaciones NO leídas de un destinatario específico
    // Útil para mostrar la bandeja de notificaciones pendientes de un usuario
    @Operation(summary = "Obtener No Leidas Por Destinatario", description = "Útil para mostrar la bandeja de notificaciones pendientes de un usuario")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operación exitosa"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "Sin permisos")
    })
    @GetMapping("/destinatario/{rut}/no-leidas")
    public ResponseEntity<List<NotificationRecipient>> obtenerNoLeidasPorDestinatario(
            @PathVariable String rut) {
        return ResponseEntity.ok(
                recipientService.obtenerNoLeidasPorDestinatario(rut));
    }

    // GET /api/v1/notification-recipients/no-leidas
    // Devuelve TODAS las notificaciones no leídas del sistema
    // sin importar a quién pertenecen
    @Operation(summary = "Obtener No Leidas", description = "sin importar a quién pertenecen")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operación exitosa"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "Sin permisos")
    })
    @GetMapping("/no-leidas")
    public ResponseEntity<List<NotificationRecipient>> obtenerNoLeidas() {
        return ResponseEntity.ok(recipientService.obtenerNoLeidas());
    }

    // GET /api/v1/notification-recipients/ultimos
    // Devuelve los últimos 10 destinatarios registrados en el sistema
    @Operation(summary = "Obtener Ultimos", description = "Devuelve los últimos 10 destinatarios registrados en el sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operación exitosa"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "Sin permisos")
    })
    @GetMapping("/ultimos")
    public ResponseEntity<List<NotificationRecipient>> obtenerUltimos() {
        return ResponseEntity.ok(
                recipientService.obtenerUltimosDestinatarios());
    }

    // GET /api/v1/notification-recipients/estadisticas/destinatario/12345678-9
    // Cuenta cuántas notificaciones NO leídas tiene un destinatario específico
    // Útil para mostrar el badge de notificaciones pendientes en el frontend
    @GetMapping("/estadisticas/destinatario/{rut}")
    public ResponseEntity<Map<String, Long>> contarNoLeidas(
            @PathVariable String rut) {
        long total =
                recipientService.contarNoLeidasPorDestinatario(rut);
        return ResponseEntity.ok(Map.of("total", total));
    }
}