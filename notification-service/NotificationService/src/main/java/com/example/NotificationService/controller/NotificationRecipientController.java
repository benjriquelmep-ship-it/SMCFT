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
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/v1/notification-recipients")
@RequiredArgsConstructor
@Tag(name = "Destinatarios de Alertas", description = "Endpoints para la vinculación, auditoría y control de lecturas de los usuarios asignados a las notificaciones")
@SecurityRequirement(name = "bearerAuth")
public class NotificationRecipientController {

    // NotificationRecipientService contiene toda la lógica de negocio
    // El Controller solo recibe y responde — nunca tiene lógica propia
    private final NotificationRecipientService recipientService;

    // GET /api/v1/notification-recipients
    // Devuelve todos los destinatarios del sistema
    @Operation(summary = "Listar todos los destinatarios", description = "Recupera la nómina histórica de todos los usuarios vinculados a las notificaciones del ecosistema fronterizo.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Nómina de destinatarios recuperada correctamente"),
            @ApiResponse(responseCode = "401", description = "No autenticado - Falta token Bearer JWT"),
            @ApiResponse(responseCode = "403", description = "Sin permisos - Rol de auditoría insuficiente")
    })
    @GetMapping
    public ResponseEntity<List<NotificationRecipient>> obtenerTodos() {
        return ResponseEntity.ok(recipientService.obtenerTodos());
    }

    // GET /api/v1/notification-recipients/1
    // Devuelve un destinatario específico por su id
    @Operation(summary = "Obtener destinatario por ID", description = "Busca la información de contacto y metadatos de un destinatario indexado a partir de su ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Destinatario localizado correctamente"),
            @ApiResponse(responseCode = "404", description = "El ID de destinatario solicitado no existe")
    })
    @GetMapping("/{id}")
    public ResponseEntity<NotificationRecipient> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(recipientService.obtenerPorId(id));
    }

    // POST /api/v1/notification-recipients
    // Agrega un nuevo destinatario a una notificación existente
    @Operation(summary = "Anexar destinatario a notificación", description = "Vincula un ciudadano, conductor o transportista legal a una cabecera de notificación ya creada para su despacho.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Destinatario indexado de forma correcta"),
            @ApiResponse(responseCode = "400", description = "Estructura DTO incorrecta o violación de tipos")
    })
    @PostMapping
    public ResponseEntity<NotificationRecipient> agregar(@Valid @RequestBody NotificationRecipientDTO dto) {
        // HTTP 201 = se creó un nuevo recurso exitosamente
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(recipientService.agregar(dto));
    }

    // PATCH /api/v1/notification-recipients/1/leida
    // Marca que un destinatario leyó la notificación
    @Operation(summary = "Marcar notificación como Leída por el usuario", description = "Ruta pública operacional. Modifica la bandera lógica a LEÍDA inyectando de forma automática el sello temporal exacto de la lectura.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Confirmación de lectura grabada con éxito"),
            @ApiResponse(responseCode = "404", description = "El ID de destinatario solicitado no existe")
    })
    @PatchMapping("/{id}/leida")
    public ResponseEntity<NotificationRecipient> marcarComoLeida(@PathVariable Long id) {
        return ResponseEntity.ok(recipientService.marcarComoLeida(id));
    }

    // DELETE /api/v1/notification-recipients/1
    // Elimina un destinatario por su id
    @Operation(summary = "Eliminar vinculación de destinatario", description = "Remueve de forma física la asignación de un usuario a una notificación en la base de datos central.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Vinculación purgada de la persistencia. Sin contenido."),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "404", description = "El ID de destinatario solicitado no existe")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        recipientService.eliminar(id);
        // HTTP 204 = operación exitosa sin contenido en la respuesta
        return ResponseEntity.noContent().build();
    }

    // GET /api/v1/notification-recipients/notificacion/1
    // Devuelve todos los destinatarios de una notificación específica
    @Operation(summary = "Listar destinatarios por Notificación", description = "Extrae el listado completo de canales y usuarios asignados a un ID de notificación de cabecera.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Listado de destinatarios obtenido con éxito")
    })
    @GetMapping("/notificacion/{notificationId}")
    public ResponseEntity<List<NotificationRecipient>> obtenerPorNotificacion(@PathVariable Long notificationId) {
        return ResponseEntity.ok(
                recipientService.obtenerPorNotificacion(notificationId));
    }

    // GET /api/v1/notification-recipients/notificacion/1/no-leidos
    // Devuelve los destinatarios que AÚN NO leyeron una notificación específica
    @Operation(summary = "Listar destinatarios con lecturas Pendientes", description = "Aísla a los usuarios asignados que registran la bandera de lectura en falso para auditoría o reenvío de alertas.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Destinatarios no leídos filtrados de manera exitosa")
    })
    @GetMapping("/notificacion/{notificationId}/no-leidos")
    public ResponseEntity<List<NotificationRecipient>> obtenerNoLeidos(@PathVariable Long notificationId) {
        return ResponseEntity.ok(
                recipientService.obtenerNoLeidosPorNotificacion(notificationId));
    }

    // GET /api/v1/notification-recipients/destinatario/12345678-9
    // Devuelve todas las notificaciones asignadas a un destinatario específico
    @Operation(summary = "Listar bandeja histórica por RUN de usuario", description = "Recupera la bandeja de entrada total (Leídas y No Leídas) asociada al RUT/RUN del ciudadano consultado.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Bandeja histórica del usuario extraída correctamente")
    })
    @GetMapping("/destinatario/{rut}")
    public ResponseEntity<List<NotificationRecipient>> obtenerPorDestinatario(@PathVariable String rut) {
        return ResponseEntity.ok(
                recipientService.obtenerPorDestinatario(rut));
    }

    // GET /api/v1/notification-recipients/destinatario/12345678-9/no-leidas
    // Devuelve solo las notificaciones NO leídas de un destinatario específico
    @Operation(summary = "Listar alertas Pendientes por RUN de usuario", description = "Filtra la bandeja de entrada del usuario devolviendo única y exclusivamente sus avisos no leídos.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Bandeja de pendientes activa recuperada correctamente")
    })
    @GetMapping("/destinatario/{rut}/no-leidas")
    public ResponseEntity<List<NotificationRecipient>> obtenerNoLeidasPorDestinatario(@PathVariable String rut) {
        return ResponseEntity.ok(
                recipientService.obtenerNoLeidasPorDestinatario(rut));
    }

    // GET /api/v1/notification-recipients/no-leidas
    // Devuelve TODAS las notificaciones no leídas del sistema
    @Operation(summary = "Listar alertas globales No Leídas", description = "Endpoint de reportería que extrae todos los despachos del sistema que registran lectura pendiente.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Volumen global de no leídos extraído con éxito")
    })
    @GetMapping("/no-leidas")
    public ResponseEntity<List<NotificationRecipient>> obtenerNoLeidas() {
        return ResponseEntity.ok(recipientService.obtenerNoLeidas());
    }

    // GET /api/v1/notification-recipients/ultimos
    // Devuelve los últimos 10 destinatarios registrados en el sistema
    @Operation(summary = "Listar últimos destinatarios indexados", description = "Retorna de manera inmediata los últimos 10 registros de destinatarios anexados al módulo global.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Bitácora inmediata de auditoría devuelta de manera exitosa")
    })
    @GetMapping("/ultimos")
    public ResponseEntity<List<NotificationRecipient>> obtenerUltimos() {
        return ResponseEntity.ok(
                recipientService.obtenerUltimosDestinatarios());
    }

    // GET /api/v1/notification-recipients/estadisticas/destinatario/12345678-9
    // Cuenta cuántas notificaciones NO leídas tiene un destinatario específico
    @Operation(summary = "Contar alertas pendientes por RUN", description = "Devuelve el número métrico total de mensajes no leídos de un ciudadano (Utilizado para renderizar badges en la UI).")
    @GetMapping("/estadisticas/destinatario/{rut}")
    public ResponseEntity<Map<String, Long>> contarNoLeidas(@PathVariable String rut) {
        long total = recipientService.contarNoLeidasPorDestinatario(rut);
        return ResponseEntity.ok(Map.of("total", total));
    }
}