// Lógica de negocio del Notification Service
// Se comunica con Deadline Service para obtener alertas
// y generar notificaciones automáticamente
package com.example.NotificationService.service;

import com.example.NotificationService.dto.DeadlineAlertResponseDTO;
import com.example.NotificationService.dto.NotificationDTO;
import com.example.NotificationService.model.Notification;
import com.example.NotificationService.model.NotificationRecipient;
import com.example.NotificationService.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    // Accede a la tabla notifications en la BD
    private final NotificationRepository notificationRepository;

    // Cliente HTTP para llamar a Deadline Service
    // Se usa para obtener alertas pendientes y marcarlas como enviadas
    private final WebClient webClient;

    // Cliente HTTP para llamar a User Service
    // Se usa para consultar datos del usuario (nombre, email) por RUT
    private final WebClient userServiceWebClient;

    // Devuelve todas las notificaciones de la BD
    public List<Notification> obtenerTodas() {
        log.info("Obteniendo todas las notificaciones");
        return notificationRepository.findAll();
    }

    // Busca una notificación por su id
    // Si no existe lanza RuntimeException → HTTP 404
    public Notification obtenerPorId(Long id) {
        log.info("Buscando notificación con id: {}", id);
        return notificationRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Notificación con id {} no encontrada", id);
                    return new RuntimeException(
                            "Notificación no encontrada con id: " + id);
                });
    }

    // Crea una notificación manual con los datos del DTO
    // Si el DTO incluye destinatarioRut, consulta User Service para obtener
    // los datos del destinatario y lo agrega automáticamente
    public Notification crear(NotificationDTO dto) {
        log.info("Creando notificación: {}", dto.getTitulo());

        // Mapeo DTO → Entidad
        Notification nueva = new Notification();
        nueva.setTitulo(dto.getTitulo());               // título de la notificación
        nueva.setMensaje(dto.getMensaje());             // mensaje detallado
        nueva.setTipo(dto.getTipo());                   // ALERTA_DEADLINE, INFORMATIVA, etc.
        nueva.setDeadlineAlertId(dto.getDeadlineAlertId()); // id de la alerta o null
        nueva.setEstado("PENDIENTE");                   // toda notificación nueva inicia PENDIENTE
        nueva.setCreatedAt(LocalDateTime.now());        // fecha y hora actual

        // Si se especificó un RUT de destinatario, buscar sus datos en User Service
        String rut = dto.getDestinatarioRut();
        if (rut != null && !rut.isBlank()) {
            try {
                log.info("Buscando destinatario con RUT: {}", rut);
                Map<String, Object> userData = userServiceWebClient.get()
                        .uri("/api/v1/users/rut/{rut}", rut)
                        .retrieve()
                        .bodyToMono(Map.class)
                        .block();

                if (userData != null) {
                    NotificationRecipient destinatario = new NotificationRecipient();
                    destinatario.setNotification(nueva);
                    destinatario.setRutDestinatario(rut);
                    destinatario.setEmail((String) userData.getOrDefault("email", ""));
                    destinatario.setNombre((String) userData.getOrDefault("nombre", ""));
                    destinatario.setLeida(false);

                    List<NotificationRecipient> destinatarios = new ArrayList<>();
                    destinatarios.add(destinatario);
                    nueva.setDestinatarios(destinatarios);
                    log.info("Destinatario asignado: {} - {}", rut, destinatario.getNombre());
                }
            } catch (Exception e) {
                log.error("Error al obtener datos del usuario con RUT {}: {}", rut, e.getMessage());
            }
        }

        Notification guardada = notificationRepository.save(nueva);
        log.info("Notificación creada con id: {}", guardada.getId());
        return guardada;
    }

    // Actualiza los datos editables de una notificación existente
    // Solo se actualizan titulo, mensaje, tipo — el estado se maneja por separado
    public Notification actualizar(Long id, NotificationDTO dto) {
        log.info("Actualizando notificación {}", id);
        Notification notificacion = obtenerPorId(id);
        notificacion.setTitulo(dto.getTitulo());
        notificacion.setMensaje(dto.getMensaje());
        notificacion.setTipo(dto.getTipo());

        // Limpiar destinatarios anteriores y reemplazar con el nuevo si se especifica RUT
        if (notificacion.getDestinatarios() != null) {
            notificacion.getDestinatarios().clear();
        }

        String rut = dto.getDestinatarioRut();
        if (rut != null && !rut.isBlank()) {
            try {
                Map<String, Object> userData = userServiceWebClient.get()
                        .uri("/api/v1/users/rut/{rut}", rut)
                        .retrieve()
                        .bodyToMono(Map.class)
                        .block();
                if (userData != null) {
                    NotificationRecipient destinatario = new NotificationRecipient();
                    destinatario.setNotification(notificacion);
                    destinatario.setRutDestinatario(rut);
                    destinatario.setEmail((String) userData.getOrDefault("email", ""));
                    destinatario.setNombre((String) userData.getOrDefault("nombre", ""));
                    destinatario.setLeida(false);
                    notificacion.getDestinatarios().add(destinatario);
                    log.info("Destinatario actualizado: {} - {}", rut, destinatario.getNombre());
                }
            } catch (Exception e) {
                log.error("Error al obtener datos del usuario con RUT {}: {}", rut, e.getMessage());
            }
        }

        Notification actualizada = notificationRepository.save(notificacion);
        log.info("Notificación {} actualizada", id);
        return actualizada;
    }

    // Marca una notificación como enviada al destinatario
    // Solo funciona si la notificación está PENDIENTE
    public Notification marcarComoEnviada(Long id) {
        log.info("Marcando notificación {} como enviada", id);
        Notification notificacion = obtenerPorId(id);

        // REGLA: solo se pueden enviar notificaciones PENDIENTES
        // Si ya fue ENVIADA o tuvo ERROR → no se puede volver a enviar
        if (!notificacion.getEstado().equals("PENDIENTE")) {
            log.warn("Notificación {} no está PENDIENTE. Estado: {}",
                    id, notificacion.getEstado());
            throw new RuntimeException(
                    "Solo se pueden enviar notificaciones PENDIENTES. "
                            + "Estado actual: " + notificacion.getEstado());
        }

        notificacion.setEstado("ENVIADA");
        // Guarda la fecha y hora exacta en que se marcó como enviada
        notificacion.setEnviadaAt(LocalDateTime.now());
        Notification actualizada =
                notificationRepository.save(notificacion);
        log.info("Notificación {} marcada como enviada", id);
        return actualizada;
    }

    // Marca una notificación como ERROR — falló el envío
    // No tiene restricción de estado — cualquier notificación puede marcar error
    public Notification marcarComoError(Long id) {
        log.info("Marcando notificación {} como error", id);
        Notification notificacion = obtenerPorId(id);
        notificacion.setEstado("ERROR");
        Notification actualizada =
                notificationRepository.save(notificacion);
        log.info("Notificación {} marcada como error", id);
        return actualizada;
    }

    // Elimina una notificación por su id
    // existsById verifica si existe antes de intentar eliminar
    public void eliminar(Long id) {
        log.info("Eliminando notificación con id: {}", id);
        if (!notificationRepository.existsById(id)) {
            log.warn("Notificación con id {} no encontrada", id);
            throw new RuntimeException(
                    "Notificación no encontrada con id: " + id);
        }
        notificationRepository.deleteById(id);
        log.info("Notificación {} eliminada correctamente", id);
    }

    // Genera notificaciones automáticamente desde las alertas de Deadline Service
    // Consulta alertas no enviadas → crea una notificación por cada alerta
    // → marca cada alerta como enviada en Deadline Service
    public List<Notification> generarDesdeAlertas() {
        log.info("Generando notificaciones desde Deadline Service");

        try {
            // PASO 1 — Consulta alertas no enviadas de Deadline Service
            List<DeadlineAlertResponseDTO> alertas =
                    webClient.get()
                            .uri("/api/v1/deadline-alerts/no-enviadas")
                            .retrieve()
                            .bodyToFlux(DeadlineAlertResponseDTO.class)
                            .collectList()
                            .block();

            // Si no hay alertas pendientes → retorna lista vacía
            if (alertas == null || alertas.isEmpty()) {
                log.info("No hay alertas pendientes");
                return List.of();
            }

            // PASO 2 — Crea una notificación por cada alerta pendiente
            alertas.forEach(alerta -> {

                // Construye la notificación con los datos de la alerta
                Notification nueva = new Notification();
                nueva.setTitulo("Alerta de Deadline: "
                        + alerta.getTipoAlerta()); // Ej: "Alerta de Deadline: URGENTE"
                nueva.setMensaje(alerta.getMensaje()); // mensaje original de la alerta
                // Construye el tipo concatenando "ALERTA_" + tipoAlerta
                // Ej: "ALERTA_" + "URGENTE" → "ALERTA_URGENTE"
                nueva.setTipo("ALERTA_" + alerta.getTipoAlerta());
                nueva.setDeadlineAlertId(alerta.getId()); // vincula con la alerta original
                nueva.setEstado("PENDIENTE");
                nueva.setCreatedAt(LocalDateTime.now());
                notificationRepository.save(nueva);

                // PASO 3 — Marca la alerta como enviada en Deadline Service
                // Para que no se vuelva a procesar en la próxima llamada
                marcarAlertaComoEnviada(alerta.getId());

                log.info("Notificación creada para alerta {}",
                        alerta.getId());
            });

            log.info("{} notificaciones generadas", alertas.size());

            // Retorna las notificaciones PENDIENTES recién creadas
            return notificationRepository
                    .findByEstadoOrderByIdDesc("PENDIENTE");

        } catch (Exception e) {
            // Deadline Service está caído o sin conexión
            log.error("Error al comunicarse con Deadline Service: {}",
                    e.getMessage());
            throw new RuntimeException(
                    "Error al obtener alertas de Deadline Service. "
                            + "Verifique que Deadline Service esté corriendo "
                            + "en el puerto 8087");
        }
    }

    // Devuelve notificaciones por tipo (ALERTA_URGENTE, INFORMATIVA, etc.)
    public List<Notification> obtenerPorTipo(String tipo) {
        log.info("Obteniendo notificaciones de tipo: {}", tipo);
        return notificationRepository.findByTipo(tipo);
    }

    // Devuelve notificaciones por estado (PENDIENTE, ENVIADA, ERROR)
    public List<Notification> obtenerPorEstado(String estado) {
        log.info("Obteniendo notificaciones con estado: {}", estado);
        return notificationRepository.findByEstado(estado);
    }

    // Devuelve notificaciones PENDIENTES del más reciente al más antiguo
    public List<Notification> obtenerPendientes() {
        log.info("Obteniendo notificaciones pendientes");
        return notificationRepository
                .findByEstadoOrderByIdDesc("PENDIENTE");
    }

    // Devuelve notificaciones ENVIADAS del más reciente al más antiguo
    public List<Notification> obtenerEnviadas() {
        log.info("Obteniendo notificaciones enviadas");
        return notificationRepository
                .findByEstadoOrderByIdDesc("ENVIADA");
    }

    // Busca notificaciones cuyo título contenga el texto buscado
    public List<Notification> buscarPorTitulo(String texto) {
        log.info("Buscando notificaciones con título: {}", texto);
        return notificationRepository
                .findByTituloContainingIgnoreCase(texto);
    }

    // Devuelve las últimas 10 notificaciones del sistema
    public List<Notification> obtenerUltimasNotificaciones() {
        log.info("Obteniendo las últimas 10 notificaciones");
        return notificationRepository.findTop10ByOrderByIdDesc();
    }

    // Cuenta cuántas notificaciones hay con un estado específico
    public long contarPorEstado(String estado) {
        log.info("Contando notificaciones con estado: {}", estado);
        return notificationRepository.countByEstado(estado);
    }

    // Cuenta cuántas notificaciones hay de un tipo específico
    public long contarPorTipo(String tipo) {
        log.info("Contando notificaciones de tipo: {}", tipo);
        return notificationRepository.countByTipo(tipo);
    }

    // Marca una alerta como enviada en Deadline Service
    // Se llama después de crear la notificación para esa alerta
    // Así la alerta no se vuelve a procesar en la próxima llamada
    private void marcarAlertaComoEnviada(Long alertaId) {
        try {
            log.info("Marcando alerta {} como enviada en Deadline Service",
                    alertaId);

            webClient.patch()
                    .uri("/api/v1/deadline-alerts/{id}/enviada", alertaId)
                    .retrieve()
                    .bodyToMono(Void.class)
                    .block();

            log.info("Alerta {} marcada como enviada", alertaId);

        } catch (Exception e) {
            // Si falla no lanza excepción — solo registra el error
            // La notificación ya fue creada y no debe deshacerse
            // por un error al marcar la alerta
            log.error("Error al marcar alerta {} como enviada: {}",
                    alertaId, e.getMessage());
        }
    }
}