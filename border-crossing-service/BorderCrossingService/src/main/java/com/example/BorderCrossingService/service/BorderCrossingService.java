// service/BorderCrossingService.java
// Lógica de negocio del Border Crossing Service
// Se comunica con Vehicle Service e Item Category Service

package com.example.BorderCrossingService.service;

import com.example.BorderCrossingService.dto.BorderCrossingDTO;
import com.example.BorderCrossingService.dto.VehicleResponseDTO;
import com.example.BorderCrossingService.model.BorderCrossing;
import com.example.BorderCrossingService.repository.BorderCrossingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
public class BorderCrossingService {


    private final BorderCrossingRepository crossingRepository;

    // WebClient para Vehicle Service
    // @Qualifier especifica cuál bean usar cuando hay varios WebClient
    @Qualifier("vehicleWebClient")
    private final WebClient vehicleWebClient;

    // -------------------------------------------------------
    // CRUD BÁSICO
    // -------------------------------------------------------

    // Obtener todos los cruces
    public List<BorderCrossing> obtenerTodos() {
        log.info("Obteniendo todos los cruces fronterizos");
        return crossingRepository.findAll();
    }

    // Obtener cruce por id
    public BorderCrossing obtenerPorId(Long id) {
        log.info("Buscando cruce con id: {}", id);
        return crossingRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Cruce con id {} no encontrado", id);
                    return new RuntimeException(
                            "Cruce no encontrado con id: " + id);
                });
    }

    // Registrar nuevo cruce fronterizo (salida del país)
    public BorderCrossing registrar(BorderCrossingDTO dto) {
        log.info("Registrando cruce para vehículo: {}", dto.getPatente());

        // PASO 1 — Comunicación con Vehicle Service
        // Verificar que el vehículo existe y está en territorio nacional
        VehicleResponseDTO vehiculo =
                verificarVehiculoEnVehicleService(dto.getPatente());

        // REGLA DE NEGOCIO 1: el vehículo debe estar EN_TERRITORIO_NACIONAL
        // para poder registrar una salida del país
        if (!vehiculo.getEstado().equals("EN_TERRITORIO_NACIONAL")) {
            log.warn("Vehículo {} no está en territorio nacional. Estado: {}",
                    dto.getPatente(), vehiculo.getEstado());
            throw new RuntimeException(
                    "El vehículo " + dto.getPatente()
                            + " no está en territorio nacional. "
                            + "Estado actual: " + vehiculo.getEstado());
        }

        // REGLA DE NEGOCIO 2: la fecha del cruce no puede ser futura
        if (dto.getFechaCruce().isAfter(LocalDateTime.now())) {
            log.warn("Fecha de cruce futura: {}", dto.getFechaCruce());
            throw new RuntimeException(
                    "La fecha del cruce no puede ser futura");
        }

        // Mapeo DTO → Entidad
        BorderCrossing nuevo = new BorderCrossing();
        nuevo.setPatente(dto.getPatente().toUpperCase());
        nuevo.setRutConductor(dto.getRutConductor());
        nuevo.setPaisDestino(dto.getPaisDestino());
        nuevo.setPasoFronterizo(dto.getPasoFronterizo());
        nuevo.setFechaCruce(dto.getFechaCruce());
        // Estado inicial siempre PENDIENTE hasta que el fiscalizador lo procese
        nuevo.setEstado("PENDIENTE");

        BorderCrossing guardado = crossingRepository.save(nuevo);

        // PASO 2 — Actualizar estado del vehículo en Vehicle Service
        // El vehículo pasa a FUERA_DEL_PAIS después de registrar el cruce
        actualizarEstadoVehiculo(dto.getPatente(), "FUERA_DEL_PAIS");

        log.info("Cruce registrado con id: {}", guardado.getId());
        return guardado;
    }

    // Autorizar un cruce — lo hace el fiscalizador
    public BorderCrossing autorizar(Long id, String rutFiscalizador,
                                    String observaciones) {
        log.info("Autorizando cruce id: {} por fiscalizador: {}",
                id, rutFiscalizador);

        BorderCrossing cruce = obtenerPorId(id);

        // REGLA DE NEGOCIO: solo se pueden autorizar cruces PENDIENTES
        if (!cruce.getEstado().equals("PENDIENTE")) {
            log.warn("Cruce {} no está PENDIENTE. Estado: {}",
                    id, cruce.getEstado());
            throw new RuntimeException(
                    "Solo se pueden autorizar cruces en estado PENDIENTE. "
                            + "Estado actual: " + cruce.getEstado());
        }

        cruce.setEstado("AUTORIZADO");
        cruce.setRutFiscalizador(rutFiscalizador);
        cruce.setObservaciones(observaciones);

        BorderCrossing actualizado = crossingRepository.save(cruce);
        log.info("Cruce {} autorizado por {}", id, rutFiscalizador);
        return actualizado;
    }

    // Rechazar un cruce — lo hace el fiscalizador
    public BorderCrossing rechazar(Long id, String rutFiscalizador,
                                   String observaciones) {
        log.info("Rechazando cruce id: {} por fiscalizador: {}",
                id, rutFiscalizador);

        BorderCrossing cruce = obtenerPorId(id);

        // REGLA DE NEGOCIO: solo se pueden rechazar cruces PENDIENTES
        if (!cruce.getEstado().equals("PENDIENTE")) {
            log.warn("Cruce {} no está PENDIENTE. Estado: {}",
                    id, cruce.getEstado());
            throw new RuntimeException(
                    "Solo se pueden rechazar cruces en estado PENDIENTE. "
                            + "Estado actual: " + cruce.getEstado());
        }

        cruce.setEstado("RECHAZADO");
        cruce.setRutFiscalizador(rutFiscalizador);
        cruce.setObservaciones(observaciones);

        // Si se rechaza el cruce el vehículo vuelve a EN_TERRITORIO_NACIONAL
        actualizarEstadoVehiculo(
                cruce.getPatente(), "EN_TERRITORIO_NACIONAL");

        BorderCrossing actualizado = crossingRepository.save(cruce);
        log.info("Cruce {} rechazado por {}", id, rutFiscalizador);
        return actualizado;
    }

    // Eliminar cruce
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

    // -------------------------------------------------------
    // CONSULTAS DERIVADAS
    // -------------------------------------------------------

    // Todos los cruces de una patente
    public List<BorderCrossing> obtenerPorPatente(String patente) {
        log.info("Obteniendo cruces de la patente: {}", patente);
        return crossingRepository.findByPatente(patente);
    }

    // Todos los cruces de un conductor
    public List<BorderCrossing> obtenerPorConductor(String rutConductor) {
        log.info("Obteniendo cruces del conductor: {}", rutConductor);
        return crossingRepository.findByRutConductor(rutConductor);
    }

    // Cruces por estado
    public List<BorderCrossing> obtenerPorEstado(String estado) {
        log.info("Obteniendo cruces con estado: {}", estado);
        return crossingRepository.findByEstado(estado);
    }

    // Cruces por paso fronterizo
    public List<BorderCrossing> obtenerPorPasoFronterizo(
            String pasoFronterizo) {
        log.info("Obteniendo cruces del paso: {}", pasoFronterizo);
        return crossingRepository.findByPasoFronterizo(pasoFronterizo);
    }

    // Cruces autorizados por un fiscalizador
    public List<BorderCrossing> obtenerPorFiscalizador(
            String rutFiscalizador) {
        log.info("Obteniendo cruces del fiscalizador: {}",
                rutFiscalizador);
        return crossingRepository.findByRutFiscalizador(rutFiscalizador);
    }

    // Cruces de una patente con estado específico
    public List<BorderCrossing> obtenerPorPatenteYEstado(
            String patente, String estado) {
        log.info("Obteniendo cruces de {} con estado {}", patente, estado);
        return crossingRepository.findByPatenteAndEstado(patente, estado);
    }

    // Cruces en un rango de fechas
    public List<BorderCrossing> obtenerPorRangoFechas(
            LocalDateTime desde, LocalDateTime hasta) {
        log.info("Obteniendo cruces entre {} y {}", desde, hasta);
        return crossingRepository.findByFechaCruceBetween(desde, hasta);
    }

    // Búsqueda parcial por país de destino
    public List<BorderCrossing> buscarPorPaisDestino(String pais) {
        log.info("Buscando cruces hacia: {}", pais);
        return crossingRepository
                .findByPaisDestinoContainingIgnoreCase(pais);
    }

    // Cruces de una patente ordenados del más reciente al más antiguo
    public List<BorderCrossing> obtenerPorPatenteOrdenados(String patente) {
        log.info("Obteniendo cruces de {} ordenados por fecha", patente);
        return crossingRepository
                .findByPatenteOrderByFechaCruceDesc(patente);
    }

    // Los últimos 10 cruces del sistema
    public List<BorderCrossing> obtenerUltimosCruces() {
        log.info("Obteniendo los últimos 10 cruces");
        return crossingRepository.findTop10ByOrderByFechaCruceDesc();
    }

    // Contar cruces por estado — para estadísticas
    public long contarPorEstado(String estado) {
        log.info("Contando cruces con estado: {}", estado);
        return crossingRepository.countByEstado(estado);
    }

    // Contar cruces por paso fronterizo
    public long contarPorPasoFronterizo(String pasoFronterizo) {
        log.info("Contando cruces del paso: {}", pasoFronterizo);
        return crossingRepository.countByPasoFronterizo(pasoFronterizo);
    }

    // -------------------------------------------------------
    // COMUNICACIÓN CON VEHICLE SERVICE — WebClient
    // -------------------------------------------------------

    // Verifica que el vehículo existe y obtiene su estado actual
    private VehicleResponseDTO verificarVehiculoEnVehicleService(
            String patente) {
        try {
            log.info("Consultando Vehicle Service para patente: {}",
                    patente);

            return vehicleWebClient.get()
                    // GET http://localhost:8083/api/v1/vehicles/patente/ABC123
                    .uri("/api/v1/vehicles/patente/{patente}", patente)
                    .retrieve()
                    .bodyToMono(VehicleResponseDTO.class)
                    .block();

        } catch (WebClientResponseException.NotFound e) {
            log.warn("Vehículo no encontrado en Vehicle Service: {}",
                    patente);
            throw new RuntimeException(
                    "El vehículo con patente " + patente
                            + " no existe en el sistema");

        } catch (Exception e) {
            log.error("Error al comunicarse con Vehicle Service: {}",
                    e.getMessage());
            throw new RuntimeException(
                    "Error al verificar el vehículo. "
                            + "Verifique que Vehicle Service esté corriendo "
                            + "en el puerto 8083");
        }
    }

    // Actualiza el estado del vehículo en Vehicle Service
    // Se llama después de registrar o rechazar un cruce
    private void actualizarEstadoVehiculo(String patente,
                                          String nuevoEstado) {
        try {
            log.info("Actualizando estado del vehículo {} a {}",
                    patente, nuevoEstado);

            vehicleWebClient.patch()
                    // PATCH http://localhost:8083/api/v1/vehicles/patente/ABC123/estado
                    .uri("/api/v1/vehicles/patente/{patente}/estado"
                            + "?nuevoEstado={estado}", patente, nuevoEstado)
                    .retrieve()
                    .bodyToMono(Void.class)
                    .block();

            log.info("Estado del vehículo {} actualizado a {}",
                    patente, nuevoEstado);

        } catch (Exception e) {
            log.error("Error al actualizar estado del vehículo: {}",
                    e.getMessage());
            throw new RuntimeException(
                    "Error al actualizar el estado del vehículo "
                            + "en Vehicle Service");
        }
    }
}