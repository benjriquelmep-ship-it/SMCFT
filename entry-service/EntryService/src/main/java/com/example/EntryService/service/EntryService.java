// service/EntryService.java
// Lógica de negocio del Entry Service
// Se comunica con Vehicle Service para actualizar el estado del vehículo

package com.example.EntryService.service;

import com.example.EntryService.dto.EntryDTO;
import com.example.EntryService.dto.VehicleResponseDTO;
import com.example.EntryService.model.Entry;
import com.example.EntryService.repository.EntryRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EntryService {

    private static final Logger log =
            LoggerFactory.getLogger(EntryService.class);

    private final EntryRepository entryRepository;

    // WebClient para comunicarse con Vehicle Service
    private final WebClient webClient;

    // -------------------------------------------------------
    // CRUD BÁSICO
    // -------------------------------------------------------

    // Obtener todos los ingresos
    public List<Entry> obtenerTodos() {
        log.info("Obteniendo todos los ingresos");
        return entryRepository.findAll();
    }

    // Obtener ingreso por id
    public Entry obtenerPorId(Long id) {
        log.info("Buscando ingreso con id: {}", id);
        return entryRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Ingreso con id {} no encontrado", id);
                    return new RuntimeException(
                            "Ingreso no encontrado con id: " + id);
                });
    }

    // Registrar nuevo ingreso al país
    public Entry registrar(EntryDTO dto) {
        log.info("Registrando ingreso para vehículo: {}",
                dto.getPatente());

        // PASO 1 — Comunicación con Vehicle Service
        // Verificar que el vehículo existe
        VehicleResponseDTO vehiculo =
                verificarVehiculoEnVehicleService(dto.getPatente());

        // REGLA DE NEGOCIO 1: para RETORNO el vehículo debe estar
        // FUERA_DEL_PAIS — solo puede regresar si salió antes
        if (dto.getTipoIngreso().equals("RETORNO")
                && !vehiculo.getEstado().equals("FUERA_DEL_PAIS")) {
            log.warn("Vehículo {} no está FUERA_DEL_PAIS. Estado: {}",
                    dto.getPatente(), vehiculo.getEstado());
            throw new RuntimeException(
                    "Para registrar un RETORNO el vehículo debe estar "
                            + "FUERA_DEL_PAIS. Estado actual: "
                            + vehiculo.getEstado());
        }

        // REGLA DE NEGOCIO 2: para ADMISION_TEMPORAL el vehículo
        // debe estar EN_TERRITORIO_NACIONAL o no existir en el sistema
        if (dto.getTipoIngreso().equals("ADMISION_TEMPORAL")
                && vehiculo.getEstado().equals("FUERA_DEL_PAIS")) {
            log.warn("Estado inválido para admisión temporal: {}",
                    vehiculo.getEstado());
            throw new RuntimeException(
                    "Estado inválido para ADMISION_TEMPORAL. "
                            + "Estado actual: " + vehiculo.getEstado());
        }

        // REGLA DE NEGOCIO 3: la fecha de ingreso no puede ser futura
        if (dto.getFechaIngreso().isAfter(LocalDateTime.now())) {
            log.warn("Fecha de ingreso futura: {}",
                    dto.getFechaIngreso());
            throw new RuntimeException(
                    "La fecha de ingreso no puede ser futura");
        }

        // Mapeo DTO → Entidad
        Entry nuevo = new Entry();
        nuevo.setPatente(dto.getPatente().toUpperCase());
        nuevo.setRutConductor(dto.getRutConductor());
        nuevo.setPaisOrigen(dto.getPaisOrigen());
        nuevo.setPasoFronterizo(dto.getPasoFronterizo());
        nuevo.setFechaIngreso(dto.getFechaIngreso());
        nuevo.setTipoIngreso(dto.getTipoIngreso());
        // Estado inicial siempre PENDIENTE
        nuevo.setEstado("PENDIENTE");

        Entry guardado = entryRepository.save(nuevo);

        // PASO 2 — Actualizar estado del vehículo en Vehicle Service
        // Según el tipo de ingreso el estado cambia
        if (dto.getTipoIngreso().equals("RETORNO")) {
            // Vehículo chileno que regresa → EN_TERRITORIO_NACIONAL
            actualizarEstadoVehiculo(
                    dto.getPatente(), "EN_TERRITORIO_NACIONAL");
        } else {
            // Vehículo extranjero que entra → ADMISION_TEMPORAL
            actualizarEstadoVehiculo(
                    dto.getPatente(), "ADMISION_TEMPORAL");
        }

        log.info("Ingreso registrado con id: {}", guardado.getId());
        return guardado;
    }

    // Autorizar un ingreso — lo hace el fiscalizador
    public Entry autorizar(Long id, String rutFiscalizador,
                           String observaciones) {
        log.info("Autorizando ingreso id: {} por fiscalizador: {}",
                id, rutFiscalizador);

        Entry ingreso = obtenerPorId(id);

        // REGLA DE NEGOCIO: solo se pueden autorizar ingresos PENDIENTES
        if (!ingreso.getEstado().equals("PENDIENTE")) {
            log.warn("Ingreso {} no está PENDIENTE. Estado: {}",
                    id, ingreso.getEstado());
            throw new RuntimeException(
                    "Solo se pueden autorizar ingresos en estado PENDIENTE. "
                            + "Estado actual: " + ingreso.getEstado());
        }

        ingreso.setEstado("AUTORIZADO");
        ingreso.setRutFiscalizador(rutFiscalizador);
        ingreso.setObservaciones(observaciones);

        Entry actualizado = entryRepository.save(ingreso);
        log.info("Ingreso {} autorizado por {}", id, rutFiscalizador);
        return actualizado;
    }

    // Rechazar un ingreso — lo hace el fiscalizador
    public Entry rechazar(Long id, String rutFiscalizador,
                          String observaciones) {
        log.info("Rechazando ingreso id: {} por fiscalizador: {}",
                id, rutFiscalizador);

        Entry ingreso = obtenerPorId(id);

        // REGLA DE NEGOCIO: solo se pueden rechazar ingresos PENDIENTES
        if (!ingreso.getEstado().equals("PENDIENTE")) {
            log.warn("Ingreso {} no está PENDIENTE. Estado: {}",
                    id, ingreso.getEstado());
            throw new RuntimeException(
                    "Solo se pueden rechazar ingresos en estado PENDIENTE. "
                            + "Estado actual: " + ingreso.getEstado());
        }

        ingreso.setEstado("RECHAZADO");
        ingreso.setRutFiscalizador(rutFiscalizador);
        ingreso.setObservaciones(observaciones);

        // Si se rechaza el ingreso el vehículo vuelve al estado anterior
        // Para RETORNO vuelve a FUERA_DEL_PAIS
        // Para ADMISION_TEMPORAL vuelve a EN_TERRITORIO_NACIONAL
        if (ingreso.getTipoIngreso().equals("RETORNO")) {
            actualizarEstadoVehiculo(
                    ingreso.getPatente(), "FUERA_DEL_PAIS");
        } else {
            actualizarEstadoVehiculo(
                    ingreso.getPatente(), "EN_TERRITORIO_NACIONAL");
        }

        Entry actualizado = entryRepository.save(ingreso);
        log.info("Ingreso {} rechazado por {}", id, rutFiscalizador);
        return actualizado;
    }

    // Eliminar ingreso
    public void eliminar(Long id) {
        log.info("Eliminando ingreso con id: {}", id);
        if (!entryRepository.existsById(id)) {
            log.warn("Ingreso con id {} no encontrado", id);
            throw new RuntimeException(
                    "Ingreso no encontrado con id: " + id);
        }
        entryRepository.deleteById(id);
        log.info("Ingreso {} eliminado correctamente", id);
    }

    // -------------------------------------------------------
    // CONSULTAS DERIVADAS
    // -------------------------------------------------------

    public List<Entry> obtenerPorPatente(String patente) {
        log.info("Obteniendo ingresos de la patente: {}", patente);
        return entryRepository.findByPatente(patente);
    }

    public List<Entry> obtenerPorConductor(String rutConductor) {
        log.info("Obteniendo ingresos del conductor: {}", rutConductor);
        return entryRepository.findByRutConductor(rutConductor);
    }

    public List<Entry> obtenerPorEstado(String estado) {
        log.info("Obteniendo ingresos con estado: {}", estado);
        return entryRepository.findByEstado(estado);
    }

    public List<Entry> obtenerPorTipoIngreso(String tipoIngreso) {
        log.info("Obteniendo ingresos de tipo: {}", tipoIngreso);
        return entryRepository.findByTipoIngreso(tipoIngreso);
    }

    public List<Entry> obtenerPorPasoFronterizo(String pasoFronterizo) {
        log.info("Obteniendo ingresos del paso: {}", pasoFronterizo);
        return entryRepository.findByPasoFronterizo(pasoFronterizo);
    }

    public List<Entry> obtenerPorFiscalizador(String rutFiscalizador) {
        log.info("Obteniendo ingresos del fiscalizador: {}",
                rutFiscalizador);
        return entryRepository.findByRutFiscalizador(rutFiscalizador);
    }

    public List<Entry> obtenerPorPatenteYEstado(
            String patente, String estado) {
        log.info("Obteniendo ingresos de {} con estado {}",
                patente, estado);
        return entryRepository.findByPatenteAndEstado(patente, estado);
    }

    public List<Entry> obtenerPorRangoFechas(
            LocalDateTime desde, LocalDateTime hasta) {
        log.info("Obteniendo ingresos entre {} y {}", desde, hasta);
        return entryRepository.findByFechaIngresoBetween(desde, hasta);
    }

    public List<Entry> buscarPorPaisOrigen(String pais) {
        log.info("Buscando ingresos desde: {}", pais);
        return entryRepository
                .findByPaisOrigenContainingIgnoreCase(pais);
    }

    public List<Entry> obtenerPorPatenteOrdenados(String patente) {
        log.info("Obteniendo ingresos de {} ordenados", patente);
        return entryRepository
                .findByPatenteOrderByFechaIngresoDesc(patente);
    }

    public List<Entry> obtenerUltimosIngresos() {
        log.info("Obteniendo los últimos 10 ingresos");
        return entryRepository.findTop10ByOrderByFechaIngresoDesc();
    }

    public long contarPorEstado(String estado) {
        log.info("Contando ingresos con estado: {}", estado);
        return entryRepository.countByEstado(estado);
    }

    public long contarPorTipo(String tipoIngreso) {
        log.info("Contando ingresos de tipo: {}", tipoIngreso);
        return entryRepository.countByTipoIngreso(tipoIngreso);
    }

    // -------------------------------------------------------
    // COMUNICACIÓN CON VEHICLE SERVICE — WebClient
    // -------------------------------------------------------

    // Verifica que el vehículo existe en Vehicle Service
    private VehicleResponseDTO verificarVehiculoEnVehicleService(
            String patente) {
        try {
            log.info("Consultando Vehicle Service para patente: {}",
                    patente);

            return webClient.get()
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
    private void actualizarEstadoVehiculo(String patente,
                                          String nuevoEstado) {
        try {
            log.info("Actualizando estado del vehículo {} a {}",
                    patente, nuevoEstado);

            webClient.patch()
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