package com.example.NotificationService.controller;

import com.example.NotificationService.dto.NotificationRecipientDTO;
import com.example.NotificationService.model.NotificationRecipient;
import com.example.NotificationService.service.NotificationRecipientService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.CollectionModel;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;
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

    private final NotificationRecipientService recipientService;

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

    @Operation(summary = "Obtener destinatario por ID", description = "Busca la información de contacto y metadatos de un destinatario indexado a partir de su ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Destinatario localizado correctamente"),
            @ApiResponse(responseCode = "404", description = "El ID de destinatario solicitado no existe")
    })
    @GetMapping("/{id}")
    public ResponseEntity<NotificationRecipient> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(recipientService.obtenerPorId(id));
    }

    @Operation(summary = "Anexar destinatario a notificación", description = "Vincula un ciudadano, conductor o transportista legal a una cabecera de notificación ya creada para su despacho.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Destinatario indexado de forma correcta"),
            @ApiResponse(responseCode = "400", description = "Estructura DTO incorrecta o violación de tipos")
    })
    @PostMapping
    public ResponseEntity<NotificationRecipient> agregar(@Valid @RequestBody NotificationRecipientDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(recipientService.agregar(dto));
    }

    @Operation(summary = "Marcar notificación como Leída por el usuario", description = "Ruta pública operacional. Modifica la bandera lógica a LEÍDA inyectando de forma automática el sello temporal exacto de la lectura.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Confirmación de lectura grabada con éxito"),
            @ApiResponse(responseCode = "404", description = "El ID de destinatario solicitado no existe")
    })
    @PatchMapping("/{id}/leida")
    public ResponseEntity<NotificationRecipient> marcarComoLeida(@PathVariable Long id) {
        return ResponseEntity.ok(recipientService.marcarComoLeida(id));
    }

    @Operation(summary = "Eliminar vinculación de destinatario", description = "Remueve de forma física la asignación de un usuario a una notificación en la base de datos central.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Vinculación purgada de la persistencia. Sin contenido."),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "404", description = "El ID de destinatario solicitado no existe")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        recipientService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Listar destinatarios por Notificación", description = "Extrae el listado completo de canales y usuarios asignados a un ID de notificación de cabecera.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Listado de destinatarios obtenido con éxito")
    })
    @GetMapping("/notificacion/{notificationId}")
    public ResponseEntity<List<NotificationRecipient>> obtenerPorNotificacion(@PathVariable Long notificationId) {
        return ResponseEntity.ok(
                recipientService.obtenerPorNotificacion(notificationId));
    }

    @Operation(summary = "Listar destinatarios con lecturas Pendientes", description = "Aísla a los usuarios asignados que registran la bandera de lectura en falso para auditoría o reenvío de alertas.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Destinatarios no leídos filtrados de manera exitosa")
    })
    @GetMapping("/notificacion/{notificationId}/no-leidos")
    public ResponseEntity<List<NotificationRecipient>> obtenerNoLeidos(@PathVariable Long notificationId) {
        return ResponseEntity.ok(
                recipientService.obtenerNoLeidosPorNotificacion(notificationId));
    }

    @Operation(summary = "Listar bandeja histórica por RUN de usuario", description = "Recupera la bandeja de entrada total (Leídas y No Leídas) asociada al RUT/RUN del ciudadano consultado.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Bandeja histórica del usuario extraída correctamente")
    })
    @GetMapping("/destinatario/{rut}")
    public ResponseEntity<List<NotificationRecipient>> obtenerPorDestinatario(@PathVariable String rut) {
        return ResponseEntity.ok(
                recipientService.obtenerPorDestinatario(rut));
    }

    @Operation(summary = "Listar alertas Pendientes por RUN de usuario", description = "Filtra la bandeja de entrada del usuario devolviendo única y exclusivamente sus avisos no leídos.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Bandeja de pendientes activa recuperada correctamente")
    })
    @GetMapping("/destinatario/{rut}/no-leidas")
    public ResponseEntity<List<NotificationRecipient>> obtenerNoLeidasPorDestinatario(@PathVariable String rut) {
        return ResponseEntity.ok(
                recipientService.obtenerNoLeidasPorDestinatario(rut));
    }

    @Operation(summary = "Listar alertas globales No Leídas", description = "Endpoint de reportería que extrae todos los despachos del sistema que registran lectura pendiente.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Volumen global de no leídos extraído con éxito")
    })
    @GetMapping("/no-leidas")
    public ResponseEntity<List<NotificationRecipient>> obtenerNoLeidas() {
        return ResponseEntity.ok(recipientService.obtenerNoLeidas());
    }

    @Operation(summary = "Listar últimos destinatarios indexados", description = "Retorna de manera inmediata los últimos 10 registros de destinatarios anexados al módulo global.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Bitácora inmediata de auditoría devuelta de manera exitosa")
    })
    @GetMapping("/ultimos")
    public ResponseEntity<List<NotificationRecipient>> obtenerUltimos() {
        return ResponseEntity.ok(
                recipientService.obtenerUltimosDestinatarios());
    }

    @Operation(summary = "Contar alertas pendientes por RUN", description = "Devuelve el número métrico total de mensajes no leídos de un ciudadano (Utilizado para renderizar badges en la UI).")
    @GetMapping("/estadisticas/destinatario/{rut}")
    public ResponseEntity<Map<String, Long>> contarNoLeidas(@PathVariable String rut) {
        long total = recipientService.contarNoLeidasPorDestinatario(rut);
        return ResponseEntity.ok(Map.of("total", total));
    }
}