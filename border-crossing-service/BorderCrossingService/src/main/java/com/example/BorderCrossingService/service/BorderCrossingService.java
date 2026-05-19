// Lógica de negocio del Border Crossing Service
// Se comunica con Vehicle Service para verificar y actualizar el estado del vehículo
package com.example.BorderCrossingService.service;

import com.example.BorderCrossingService.dto.BorderCrossingDTO;
import com.example.BorderCrossingService.dto.VehicleResponseDTO;
import com.example.BorderCrossingService.model.BorderCrossing;
import com.example.BorderCrossingService.repository.BorderCrossingRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BorderCrossingService {

    // Para registrar mensajes en la consola de IntelliJ
    private static final Logger log =
            LoggerFactory.getLogger(BorderCrossingService.class);

    // Accede a la tabla border_crossings en la BD
    private final BorderCrossingRepository crossingRepository;

    // WebClient para llamar a Vehicle Service
    @Qualifier("vehicleWebClient")
    private final WebClient vehicleWebClient;

    // Devuelve todos los cruces de la BD
    public List<BorderCrossing> obtenerTodos() {
        log.info("Obteniendo todos los cruces fronterizos");
        return crossingRepository.findAll();
    }

    // Busca un cruce por su id
    // Si no existe lanza RuntimeException → HTTP 404
    public BorderCrossing obtenerPorId(Long id) {
        log.info("Buscando cruce con id: {}", id);
        return crossingRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Cruce con id {} no encontrado", id);
                    return new RuntimeException(
                            "Cruce no encontrado con id: " + id);
                });
    }

    // Registra un nuevo cruce fronterizo — salida del país
    // Son 2 pasos: verificar vehículo → registrar cruce → actualizar vehículo
    public BorderCrossing registrar(BorderCrossingDTO dto) {
        log.info("Registrando cruce para vehículo: {}", dto.getPatente());

        // PASO 1 — Llama a Vehicle Service para verificar el vehículo
        // Si no existe → lanza error → no se registra el cruce
        VehicleResponseDTO vehiculo =
                verificarVehiculoEnVehicleService(dto.getPatente());

        // REGLA 1: el vehículo debe estar EN_TERRITORIO_NACIONAL
        // Si está FUERA_DEL_PAIS ya salió → no puede volver a salir
        // Si está ADMISION_TEMPORAL → tiene otro estado pendiente
        if (!vehiculo.getEstado().equals("EN_TERRITORIO_NACIONAL")) {
            log.warn("Vehículo {} no está en territorio nacional. Estado: {}",
                    dto.getPatente(), vehiculo.getEstado());
            throw new RuntimeException(
                    "El vehículo " + dto.getPatente()
                            + " no está en territorio nacional. "
                            + "Estado actual: " + vehiculo.getEstado());
        }

        // REGLA 2: la fecha del cruce no puede ser futura
        // No se puede registrar un cruce que aún no ocurrió
        if (dto.getFechaCruce().isAfter(LocalDateTime.now())) {
            log.warn("Fecha de cruce futura: {}", dto.getFechaCruce());
            throw new RuntimeException(
                    "La fecha del cruce no puede ser futura");
        }

        // Mapeo DTO → Entidad
        // Convierte el formulario que llegó en un objeto para guardar en la BD
        BorderCrossing nuevo = new BorderCrossing();
        nuevo.setPatente(dto.getPatente().toUpperCase()); // patente en mayúsculas
        nuevo.setRutConductor(dto.getRutConductor());     // quién conduce
        nuevo.setPaisDestino(dto.getPaisDestino());       // a dónde va
        nuevo.setPasoFronterizo(dto.getPasoFronterizo()); // por dónde cruza
        nuevo.setFechaCruce(dto.getFechaCruce());         // cuándo cruzó
        // Estado inicial siempre PENDIENTE — el fiscalizador lo procesará después
        nuevo.setEstado("PENDIENTE");

        // Guarda el cruce en la BD
        BorderCrossing guardado = crossingRepository.save(nuevo);

        // PASO 2 — Actualiza el estado del vehículo en Vehicle Service
        // El vehículo sale del país → cambia a FUERA_DEL_PAIS
        actualizarEstadoVehiculo(dto.getPatente(), "FUERA_DEL_PAIS");

        log.info("Cruce registrado con id: {}", guardado.getId());
        return guardado;
    }

    // El fiscalizador autoriza el cruce — cambia estado a AUTORIZADO
    // Solo funciona si el cruce está PENDIENTE
    public BorderCrossing autorizar(Long id, String rutFiscalizador,
                                    String observaciones) {
        log.info("Autorizando cruce id: {} por fiscalizador: {}",
                id, rutFiscalizador);

        BorderCrossing cruce = obtenerPorId(id);

        // REGLA: solo se pueden autorizar cruces PENDIENTES
        // Si ya fue AUTORIZADO o RECHAZADO → no se puede volver a procesar
        if (!cruce.getEstado().equals("PENDIENTE")) {
            log.warn("Cruce {} no está PENDIENTE. Estado: {}",
                    id, cruce.getEstado());
            throw new RuntimeException(
                    "Solo se pueden autorizar cruces en estado PENDIENTE. "
                            + "Estado actual: " + cruce.getEstado());
        }

        cruce.setEstado("AUTORIZADO");
        cruce.setRutFiscalizador(rutFiscalizador); // quién autorizó
        cruce.setObservaciones(observaciones);     // comentarios opcionales

        BorderCrossing actualizado = crossingRepository.save(cruce);
        log.info("Cruce {} autorizado por {}", id, rutFiscalizador);
        return actualizado;
    }

    // El fiscalizador rechaza el cruce — cambia estado a RECHAZADO
    // Solo funciona si el cruce está PENDIENTE
    // Al rechazar → el vehículo vuelve a EN_TERRITORIO_NACIONAL
    public BorderCrossing rechazar(Long id, String rutFiscalizador,
                                   String observaciones) {
        log.info("Rechazando cruce id: {} por fiscalizador: {}",
                id, rutFiscalizador);

        BorderCrossing cruce = obtenerPorId(id);

        // REGLA: solo se pueden rechazar cruces PENDIENTES
        if (!cruce.getEstado().equals("PENDIENTE")) {
            log.warn("Cruce {} no está PENDIENTE. Estado: {}",
                    id, cruce.getEstado());
            throw new RuntimeException(
                    "Solo se pueden rechazar cruces en estado PENDIENTE. "
                            + "Estado actual: " + cruce.getEstado());
        }

        cruce.setEstado("RECHAZADO");
        cruce.setRutFiscalizador(rutFiscalizador); // quién rechazó
        cruce.setObservaciones(observaciones);     // motivo del rechazo

        // El cruce fue rechazado → el vehículo no salió del país
        // Revierte el estado a EN_TERRITORIO_NACIONAL en Vehicle Service
        actualizarEstadoVehiculo(
                cruce.getPatente(), "EN_TERRITORIO_NACIONAL");

        BorderCrossing actualizado = crossingRepository.save(cruce);
        log.info("Cruce {} rechazado por {}", id, rutFiscalizador);
        return actualizado;
    }

    // Elimina un cruce por su id
    // existsById verifica si existe antes de intentar eliminar
    public void eliminar(Long id) {
        log.info("Eliminando cruce con id: {}", id);
        if (!crossingRepository.existsById(id)) {
            log.warn("Cruce con id {} no encontrado", id);
            throw new RuntimeException(
                    "Cruce no encontrado con id: " + id);
        }
        crossingRepository.deleteById(id);
        log.info("Cruce {} eliminado correctamente", id);
    }

    // Devuelve todos los cruces de un vehículo específico
    public List<BorderCrossing> obtenerPorPatente(String patente) {
        log.info("Obteniendo cruces de la patente: {}", patente);
        return crossingRepository.findByPatente(patente);
    }

    // Devuelve todos los cruces de un conductor específico
    public List<BorderCrossing> obtenerPorConductor(String rutConductor) {
        log.info("Obteniendo cruces del conductor: {}", rutConductor);
        return crossingRepository.findByRutConductor(rutConductor);
    }

    // Devuelve cruces por estado (PENDIENTE, AUTORIZADO, RECHAZADO)
    public List<BorderCrossing> obtenerPorEstado(String estado) {
        log.info("Obteniendo cruces con estado: {}", estado);
        return crossingRepository.findByEstado(estado);
    }

    // Devuelve todos los cruces de un paso fronterizo específico
    public List<BorderCrossing> obtenerPorPasoFronterizo(
            String pasoFronterizo) {
        log.info("Obteniendo cruces del paso: {}", pasoFronterizo);
        return crossingRepository.findByPasoFronterizo(pasoFronterizo);
    }

    // Devuelve todos los cruces procesados por un fiscalizador específico
    public List<BorderCrossing> obtenerPorFiscalizador(
            String rutFiscalizador) {
        log.info("Obteniendo cruces del fiscalizador: {}",
                rutFiscalizador);
        return crossingRepository.findByRutFiscalizador(rutFiscalizador);
    }

    // Devuelve cruces de un vehículo con un estado específico
    public List<BorderCrossing> obtenerPorPatenteYEstado(
            String patente, String estado) {
        log.info("Obteniendo cruces de {} con estado {}", patente, estado);
        return crossingRepository.findByPatenteAndEstado(patente, estado);
    }

    // Devuelve cruces registrados en un rango de fechas
    public List<BorderCrossing> obtenerPorRangoFechas(
            LocalDateTime desde, LocalDateTime hasta) {
        log.info("Obteniendo cruces entre {} y {}", desde, hasta);
        return crossingRepository.findByFechaCruceBetween(desde, hasta);
    }

    // Busca cruces cuyo país destino contenga el texto buscado
    public List<BorderCrossing> buscarPorPaisDestino(String pais) {
        log.info("Buscando cruces hacia: {}", pais);
        return crossingRepository
                .findByPaisDestinoContainingIgnoreCase(pais);
    }

    // Devuelve cruces de un vehículo del más reciente al más antiguo
    public List<BorderCrossing> obtenerPorPatenteOrdenados(String patente) {
        log.info("Obteniendo cruces de {} ordenados por fecha", patente);
        return crossingRepository
                .findByPatenteOrderByFechaCruceDesc(patente);
    }

    // Devuelve los últimos 10 cruces del sistema
    public List<BorderCrossing> obtenerUltimosCruces() {
        log.info("Obteniendo los últimos 10 cruces");
        return crossingRepository.findTop10ByOrderByFechaCruceDesc();
    }

    // Cuenta cuántos cruces hay con un estado específico
    public long contarPorEstado(String estado) {
        log.info("Contando cruces con estado: {}", estado);
        return crossingRepository.countByEstado(estado);
    }

    // Cuenta cuántos cruces se hicieron en un paso fronterizo específico
    public long contarPorPasoFronterizo(String pasoFronterizo) {
        log.info("Contando cruces del paso: {}", pasoFronterizo);
        return crossingRepository.countByPasoFronterizo(pasoFronterizo);
    }

    // Consulta a Vehicle Service el estado actual del vehículo
    // Si no existe → lanza error con mensaje claro
    // Si Vehicle Service está caído → lanza error con mensaje claro
    private VehicleResponseDTO verificarVehiculoEnVehicleService(
            String patente) {
        try {
            log.info("Consultando Vehicle Service para patente: {}",
                    patente);

            // GET http://localhost:8083/api/v1/vehicles/patente/ABC123
            // .retrieve() = ejecuta la petición
            // .bodyToMono() = convierte la respuesta a VehicleResponseDTO
            // .block() = espera la respuesta de forma síncrona
            return vehicleWebClient.get()
                    .uri("/api/v1/vehicles/patente/{patente}", patente)
                    .retrieve()
                    .bodyToMono(VehicleResponseDTO.class)
                    .block();

        } catch (WebClientResponseException.NotFound e) {

            // Vehicle Service respondió HTTP 404 → el vehículo no existe
            log.warn("Vehículo no encontrado en Vehicle Service: {}",
                    patente);
            throw new RuntimeException(
                    "El vehículo con patente " + patente
                            + " no existe en el sistema");

        } catch (Exception e) {
            // Cualquier otro error — Vehicle Service caído o sin conexión
            log.error("Error al comunicarse con Vehicle Service: {}",
                    e.getMessage());
            throw new RuntimeException(
                    "Error al verificar el vehículo. "
                            + "Verifique que Vehicle Service esté corriendo "
                            + "en el puerto 8083");
        }
    }

    // Actualiza el estado del vehículo en Vehicle Service
    // Se llama en dos momentos: Al registrar cruce: vehículo pasa a FUERA_DEL_PAIS
    private void actualizarEstadoVehiculo(String patente,
                                          String nuevoEstado) {
        try {
            log.info("Actualizando estado del vehículo {} a {}",
                    patente, nuevoEstado);

            // PATCH http://localhost:8083/api/v1/vehicles/patente/ABC123/estado?nuevoEstado=FUERA_DEL_PAIS
            // .patch() = petición HTTP PATCH — actualización parcial
            // .retrieve() = ejecuta la petición
            // .bodyToMono(Void.class) = no espera body en la respuesta
            // .block() = espera que se complete de forma síncrona
            vehicleWebClient.patch()
                    .uri("/api/v1/vehicles/patente/{patente}/estado"
                            + "?nuevoEstado={estado}", patente, nuevoEstado)
                    .retrieve()
                    .bodyToMono(Void.class)
                    .block();

            log.info("Estado del vehículo {} actualizado a {}",
                    patente, nuevoEstado);

        } catch (Exception e) {

            // Error al actualizar — Vehicle Service caído o sin conexión
            log.error("Error al actualizar estado del vehículo: {}",
                    e.getMessage());
            throw new RuntimeException(
                    "Error al actualizar el estado del vehículo "
                            + "en Vehicle Service");
        }
    }
}