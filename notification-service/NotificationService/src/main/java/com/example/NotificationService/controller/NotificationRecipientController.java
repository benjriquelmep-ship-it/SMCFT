// controller/NotificationRecipientController.java
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

@RestController
@RequestMapping("/api/v1/notification-recipients")
@RequiredArgsConstructor
public class NotificationRecipientController {

    private final NotificationRecipientService recipientService;

    // GET /api/v1/notification-recipients
    @GetMapping
    public ResponseEntity<List<NotificationRecipient>> obtenerTodos() {
        return ResponseEntity.ok(recipientService.obtenerTodos());
    }

    // GET /api/v1/notification-recipients/1
    @GetMapping("/{id}")
    public ResponseEntity<NotificationRecipient> obtenerPorId(
            @PathVariable Long id) {
        return ResponseEntity.ok(recipientService.obtenerPorId(id));
    }

    // POST /api/v1/notification-recipients
    @PostMapping
    public ResponseEntity<NotificationRecipient> agregar(
            @Valid @RequestBody NotificationRecipientDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(recipientService.agregar(dto));
    }

    // PATCH /api/v1/notification-recipients/1/leida
    @PatchMapping("/{id}/leida")
    public ResponseEntity<NotificationRecipient> marcarComoLeida(
            @PathVariable Long id) {
        return ResponseEntity.ok(recipientService.marcarComoLeida(id));
    }

    // DELETE /api/v1/notification-recipients/1
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        recipientService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    // GET /api/v1/notification-recipients/notificacion/1
    @GetMapping("/notificacion/{notificationId}")
    public ResponseEntity<List<NotificationRecipient>> obtenerPorNotificacion(
            @PathVariable Long notificationId) {
        return ResponseEntity.ok(
                recipientService.obtenerPorNotificacion(notificationId));
    }

    // GET /api/v1/notification-recipients/notificacion/1/no-leidos
    @GetMapping("/notificacion/{notificationId}/no-leidos")
    public ResponseEntity<List<NotificationRecipient>> obtenerNoLeidos(
            @PathVariable Long notificationId) {
        return ResponseEntity.ok(
                recipientService
                        .obtenerNoLeidosPorNotificacion(notificationId));
    }

    // GET /api/v1/notification-recipients/destinatario/12345678-9
    @GetMapping("/destinatario/{rut}")
    public ResponseEntity<List<NotificationRecipient>> obtenerPorDestinatario(
            @PathVariable String rut) {
        return ResponseEntity.ok(
                recipientService.obtenerPorDestinatario(rut));
    }

    // GET /api/v1/notification-recipients/destinatario/12345678-9/no-leidas
    @GetMapping("/destinatario/{rut}/no-leidas")
    public ResponseEntity<List<NotificationRecipient>> obtenerNoLeidasPorDestinatario(
            @PathVariable String rut) {
        return ResponseEntity.ok(
                recipientService.obtenerNoLeidasPorDestinatario(rut));
    }

    // GET /api/v1/notification-recipients/no-leidas
    @GetMapping("/no-leidas")
    public ResponseEntity<List<NotificationRecipient>> obtenerNoLeidas() {
        return ResponseEntity.ok(recipientService.obtenerNoLeidas());
    }

    // GET /api/v1/notification-recipients/ultimos
    @GetMapping("/ultimos")
    public ResponseEntity<List<NotificationRecipient>> obtenerUltimos() {
        return ResponseEntity.ok(
                recipientService.obtenerUltimosDestinatarios());
    }

    // GET /api/v1/notification-recipients/estadisticas/destinatario/12345678-9
    @GetMapping("/estadisticas/destinatario/{rut}")
    public ResponseEntity<Map<String, Long>> contarNoLeidas(
            @PathVariable String rut) {
        long total =
                recipientService.contarNoLeidasPorDestinatario(rut);
        return ResponseEntity.ok(Map.of("total", total));
    }
}