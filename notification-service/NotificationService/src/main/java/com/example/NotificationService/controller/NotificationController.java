// controller/NotificationController.java
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

@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    // GET /api/v1/notifications
    @GetMapping
    public ResponseEntity<List<Notification>> obtenerTodas() {
        return ResponseEntity.ok(notificationService.obtenerTodas());
    }

    // GET /api/v1/notifications/1
    @GetMapping("/{id}")
    public ResponseEntity<Notification> obtenerPorId(
            @PathVariable Long id) {
        return ResponseEntity.ok(notificationService.obtenerPorId(id));
    }

    // POST /api/v1/notifications
    @PostMapping
    public ResponseEntity<Notification> crear(
            @Valid @RequestBody NotificationDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(notificationService.crear(dto));
    }

    // POST /api/v1/notifications/generar-desde-alertas
    @PostMapping("/generar-desde-alertas")
    public ResponseEntity<List<Notification>> generarDesdeAlertas() {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(notificationService
                        .generarDesdeAlertas());
    }

    // PATCH /api/v1/notifications/1/enviada
    @PatchMapping("/{id}/enviada")
    public ResponseEntity<Notification> marcarComoEnviada(
            @PathVariable Long id) {
        return ResponseEntity.ok(
                notificationService.marcarComoEnviada(id));
    }

    // PATCH /api/v1/notifications/1/error
    @PatchMapping("/{id}/error")
    public ResponseEntity<Notification> marcarComoError(
            @PathVariable Long id) {
        return ResponseEntity.ok(
                notificationService.marcarComoError(id));
    }

    // DELETE /api/v1/notifications/1
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        notificationService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    // GET /api/v1/notifications/tipo/ALERTA_DEADLINE
    @GetMapping("/tipo/{tipo}")
    public ResponseEntity<List<Notification>> obtenerPorTipo(
            @PathVariable String tipo) {
        return ResponseEntity.ok(
                notificationService.obtenerPorTipo(tipo));
    }

    // GET /api/v1/notifications/estado/PENDIENTE
    @GetMapping("/estado/{estado}")
    public ResponseEntity<List<Notification>> obtenerPorEstado(
            @PathVariable String estado) {
        return ResponseEntity.ok(
                notificationService.obtenerPorEstado(estado));
    }

    // GET /api/v1/notifications/pendientes
    @GetMapping("/pendientes")
    public ResponseEntity<List<Notification>> obtenerPendientes() {
        return ResponseEntity.ok(
                notificationService.obtenerPendientes());
    }

    // GET /api/v1/notifications/enviadas
    @GetMapping("/enviadas")
    public ResponseEntity<List<Notification>> obtenerEnviadas() {
        return ResponseEntity.ok(
                notificationService.obtenerEnviadas());
    }

    // GET /api/v1/notifications/buscar?titulo=alerta
    @GetMapping("/buscar")
    public ResponseEntity<List<Notification>> buscarPorTitulo(
            @RequestParam String titulo) {
        return ResponseEntity.ok(
                notificationService.buscarPorTitulo(titulo));
    }

    // GET /api/v1/notifications/ultimas
    @GetMapping("/ultimas")
    public ResponseEntity<List<Notification>> obtenerUltimas() {
        return ResponseEntity.ok(
                notificationService.obtenerUltimasNotificaciones());
    }

    // GET /api/v1/notifications/estadisticas/estado/PENDIENTE
    @GetMapping("/estadisticas/estado/{estado}")
    public ResponseEntity<Map<String, Long>> contarPorEstado(
            @PathVariable String estado) {
        long total = notificationService.contarPorEstado(estado);
        return ResponseEntity.ok(Map.of("total", total));
    }

    // GET /api/v1/notifications/estadisticas/tipo/ALERTA_URGENTE
    @GetMapping("/estadisticas/tipo/{tipo}")
    public ResponseEntity<Map<String, Long>> contarPorTipo(
            @PathVariable String tipo) {
        long total = notificationService.contarPorTipo(tipo);
        return ResponseEntity.ok(Map.of("total", total));
    }
}