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
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
@Tag(name = "Despacho de Notificaciones", description = "Endpoints para la gestión, auditoría y control de alertas, correspondencias y avisos de vencimientos fronterizos")
@SecurityRequirement(name = "bearerAuth")
public class NotificationController {

    // NotificationService contiene toda la lógica de negocio
    // El Controller solo recibe y responde — nunca tiene lógica propia
    private final NotificationService notificationService;

    // GET /api/v1/notifications
    // Devuelve todas las notificaciones del sistema
    @Operation(summary = "Listar notificaciones históricas", description = "Recupera la bitácora completa de todas las notificaciones registradas en la plataforma central.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Colección de notificaciones recuperada con éxito"),
            @ApiResponse(responseCode = "401", description = "No autenticado - Falta token Bearer JWT"),
            @ApiResponse(responseCode = "403", description = "Sin permisos - Rol administrativo insuficiente")
    })
    @GetMapping
    public ResponseEntity<List<Notification>> obtenerTodas() {
        return ResponseEntity.ok(notificationService.obtenerTodas());
    }

    // GET /api/v1/notifications/1
    // Devuelve una notificación específica por su id
    @Operation(summary = "Obtener notificación por ID", description = "Busca el estado analítico y los metadatos de una cabecera de notificación específica mediante su clave primaria.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Notificación localizada correctamente"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "404", description = "El ID de notificación solicitado no existe")
    })
    @GetMapping("/{id}")
    public ResponseEntity<Notification> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(notificationService.obtenerPorId(id));
    }

    // POST /api/v1/notifications
    // Crea una notificación manual
    @Operation(summary = "Crear notificación manual", description = "Valida el formulario de entrada y registra un nuevo mensaje informativo o alerta de forma explícita.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Notificación creada y registrada exitosamente"),
            @ApiResponse(responseCode = "400", description = "Estructura DTO incorrecta o payload inválido")
    })
    @PostMapping
    public ResponseEntity<Notification> crear(@Valid @RequestBody NotificationDTO dto) {
        // HTTP 201 = se creó un nuevo recurso exitosamente
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(notificationService.crear(dto));
    }

    // POST /api/v1/notifications/generar-desde-alertas
    // Genera notificaciones automáticamente desde las alertas pendientes del Deadline Service
    @Operation(summary = "Generar notificaciones desde alertas pendientes", description = "Gatilla un proceso por lotes que consume las infracciones temporales no notificadas del Deadline Service y automatiza su correspondencia.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Lote de notificaciones autogeneradas con éxito"),
            @ApiResponse(responseCode = "401", description = "No autenticado")
    })
    @PostMapping("/generar-desde-alertas")
    public ResponseEntity<List<Notification>> generarDesdeAlertas() {
        // HTTP 201 = se crearon nuevos recursos exitosamente
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(notificationService.generarDesdeAlertas());
    }

    // PATCH /api/v1/notifications/1/enviada
    // Marca una notificación como enviada al destinatario
    @Operation(summary = "Marcar notificación como Enviada", description = "Actualiza el estado de control lógico a ENVIADA una vez despachado el mensaje a través del canal de comunicación.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Estado de despacho actualizado correctamente"),
            @ApiResponse(responseCode = "400", description = "La notificación no se encuentra en estado PENDIENTE"),
            @ApiResponse(responseCode = "404", description = "El ID de notificación especificado no existe")
    })
    @PatchMapping("/{id}/enviada")
    public ResponseEntity<Notification> marcarComoEnviada(@PathVariable Long id) {
        return ResponseEntity.ok(notificationService.marcarComoEnviada(id));
    }

    // PATCH /api/v1/notifications/1/error
    // Marca una notificación como ERROR — falló el envío
    @Operation(summary = "Registrar falla de envío", description = "Interrumpe la cola de despacho marcando la notificación en estado ERROR tras detectar problemas en la entrega.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Falla de envío registrada exitosamente en la auditoría"),
            @ApiResponse(responseCode = "404", description = "El ID de notificación especificado no existe")
    })
    @PatchMapping("/{id}/error")
    public ResponseEntity<Notification> marcarComoError(@PathVariable Long id) {
        return ResponseEntity.ok(notificationService.marcarComoError(id));
    }

    // PUT /api/v1/notifications/1
    // Actualiza los datos editables de una notificación existente
    @Operation(summary = "Actualizar parámetros de notificación", description = "Modifica campos editables de la cabecera de la notificación (como título o descripción) mediante reemplazo completo del payload.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Notificación actualizada con éxito"),
            @ApiResponse(responseCode = "400", description = "Restricciones de validación infringidas en el DTO"),
            @ApiResponse(responseCode = "404", description = "El ID de notificación solicitado no existe")
    })
    @PutMapping("/{id}")
    public ResponseEntity<Notification> actualizar(@PathVariable Long id, @Valid @RequestBody NotificationDTO dto) {
        return ResponseEntity.ok(notificationService.actualizar(id, dto));
    }

    // DELETE /api/v1/notifications/1
    // Elimina una notificación por su id
    @Operation(summary = "Eliminar notificación del sistema", description = "Remueve físicamente el registro de la notificación de la base de datos central.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Notificación eliminada de forma permanente. Sin contenido."),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "404", description = "El ID de notificación solicitado no existe")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        notificationService.eliminar(id);
        // HTTP 204 = operación exitosa sin contenido en la respuesta
        return ResponseEntity.noContent().build();
    }

    // GET /api/v1/notifications/tipo/ALERTA_DEADLINE
    // Devuelve notificaciones por tipo
    @Operation(summary = "Filtrar notificaciones por Categoría", description = "Agrupa y extrae los mensajes del catálogo histórico según su tipo (ej: ALERTA_DEADLINE, INFORMATIVA).")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Colección filtrada por tipo devuelta con éxito"),
            @ApiResponse(responseCode = "401", description = "No autenticado")
    })
    @GetMapping("/tipo/{tipo}")
    public ResponseEntity<List<Notification>> obtenerPorTipo(@PathVariable String tipo) {
        return ResponseEntity.ok(notificationService.obtenerPorTipo(tipo));
    }

    // GET /api/v1/notifications/estado/PENDIENTE
    // Devuelve notificaciones por estado (PENDIENTE, ENVIADA, ERROR)
    @Operation(summary = "Filtrar notificaciones por Estado", description = "Extrae el conjunto de registros de notificaciones según su situación de despacho operativa.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Registros agrupados por estado devueltos correctamente"),
            @ApiResponse(responseCode = "401", description = "No autenticado")
    })
    @GetMapping("/estado/{estado}")
    public ResponseEntity<List<Notification>> obtenerPorEstado(@PathVariable String estado) {
        return ResponseEntity.ok(notificationService.obtenerPorEstado(estado));
    }

    // GET /api/v1/notifications/pendientes
    // Devuelve todas las notificaciones que aún no fueron enviadas
    @Operation(summary = "Listar notificaciones Pendientes", description = "Recupera de forma prioritaria los mensajes en cola que aún no se despachan, ordenados del más reciente al más antiguo.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Bandeja de salida pendiente recuperada con éxito")
    })
    @GetMapping("/pendientes")
    public ResponseEntity<List<Notification>> obtenerPendientes() {
        return ResponseEntity.ok(notificationService.obtenerPendientes());
    }

    // GET /api/v1/notifications/enviadas
    // Devuelve todas las notificaciones que ya fueron enviadas
    @Operation(summary = "Listar historial de notificaciones Enviadas", description = "Recupera la bitácora analítica de todas las correspondencias procesadas y despachadas exitosamente.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Historial de envíos localizado correctamente")
    })
    @GetMapping("/enviadas")
    public ResponseEntity<List<Notification>> obtenerEnviadas() {
        return ResponseEntity.ok(notificationService.obtenerEnviadas());
    }

    // GET /api/v1/notifications/buscar?titulo=alerta
    // Busca notificaciones cuyo título contenga el texto buscado
    @Operation(summary = "Buscar notificaciones por Coincidencia de título", description = "Realiza una consulta de texto parcial (LIKE) sobre el campo del asunto institucional del mensaje.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Coincidencias encontradas devueltas de manera exitosa")
    })
    @GetMapping("/buscar")
    public ResponseEntity<List<Notification>> buscarPorTitulo(@RequestParam String titulo) {
        return ResponseEntity.ok(notificationService.buscarPorTitulo(titulo));
    }

    // GET /api/v1/notifications/ultimas
    // Devuelve las últimas 10 notificaciones registradas en el sistema
    @Operation(summary = "Listar últimas solicitudes globales", description = "Endpoint de monitoreo operativo que expone las últimas 10 transacciones de notificaciones a nivel global.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Extracto inmediato de auditoría recuperado con éxito")
    })
    @GetMapping("/ultimas")
    public ResponseEntity<List<Notification>> obtenerUltimas() {
        return ResponseEntity.ok(notificationService.obtenerUltimasNotificaciones());
    }

    // GET /api/v1/notifications/estadisticas/estado/PENDIENTE
    // Cuenta cuántas notificaciones hay con ese estado
    @Operation(summary = "Contar volumen cuantitativo por Estado", description = "Devuelve la sumatoria volumétrica total de registros clasificados bajo un estado de despacho específico.")
    @GetMapping("/estadisticas/estado/{estado}")
    public ResponseEntity<Map<String, Long>> contarPorEstado(@PathVariable String estado) {
        long total = notificationService.contarPorEstado(estado);
        return ResponseEntity.ok(Map.of("total", total));
    }

    // GET /api/v1/notifications/estadisticas/tipo/ALERTA_URGENTE
    // Cuenta cuántas notificaciones hay de ese tipo
    @Operation(summary = "Contar volumen cuantitativo por Tipo", description = "Devuelve la sumatoria volumétrica total de alertas emitidas bajo una categoría de negocio dada.")
    @GetMapping("/estadisticas/tipo/{tipo}")
    public ResponseEntity<Map<String, Long>> contarPorTipo(@PathVariable String tipo) {
        long total = notificationService.contarPorTipo(tipo);
        return ResponseEntity.ok(Map.of("total", total));
    }
}