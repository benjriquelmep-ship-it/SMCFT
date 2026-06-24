package com.example.EntryService.service;

import com.example.EntryService.dto.EntryDTO;
import com.example.EntryService.dto.VehicleResponseDTO;
import com.example.EntryService.model.Entry;
import com.example.EntryService.repository.EntryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class EntryService {

    private final EntryRepository entryRepository;
    private final WebClient webClient;

    public Entry actualizar(Long id, EntryDTO dto) {
        log.info("Actualizando ingreso id: {}", id);
        Entry entry = obtenerPorId(id);
        entry.setPatente(dto.getPatente().toUpperCase());
        entry.setRutConductor(dto.getRutConductor());
        entry.setPaisOrigen(dto.getPaisOrigen());
        entry.setPasoFronterizo(dto.getPasoFronterizo());
        entry.setFechaIngreso(dto.getFechaIngreso());
        entry.setTipoIngreso(dto.getTipoIngreso());
        if (dto.getEstado() != null) {
            entry.setEstado(dto.getEstado());
        }
        return entryRepository.save(entry);
    }

    public List<Entry> obtenerTodos() {
        log.info("Obteniendo todos los ingresos");
        return entryRepository.findAll();
    }

    public Entry obtenerPorId(Long id) {
        log.info("Buscando ingreso con id: {}", id);
        return entryRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Ingreso con id {} no encontrado", id);
                    return new RuntimeException(
                            "Ingreso no encontrado con id: " + id);
                });
    }

    public Entry registrar(EntryDTO dto) {
        log.info("Registrando ingreso para vehículo: {}",
                dto.getPatente());

        VehicleResponseDTO vehiculo =
                verificarVehiculoEnVehicleService(dto.getPatente());

        // REGLA 1: para RETORNO el vehículo debe estar FUERA_DEL_PAIS
        // Si el vehículo no salió del país no puede "regresar"
        if (dto.getTipoIngreso().equals("RETORNO")
                && !vehiculo.getEstado().equals("FUERA_DEL_PAIS")) {
            log.warn("Vehículo {} no está FUERA_DEL_PAIS. Estado: {}",
                    dto.getPatente(), vehiculo.getEstado());
            throw new RuntimeException(
                    "Para registrar un RETORNO el vehículo debe estar "
                            + "FUERA_DEL_PAIS. Estado actual: "
                            + vehiculo.getEstado());
        }

        // REGLA 2: para ADMISION_TEMPORAL el vehículo NO puede estar FUERA_DEL_PAIS
        // Un vehículo que ya está fuera no puede hacer admisión temporal
        if (dto.getTipoIngreso().equals("ADMISION_TEMPORAL")
                && vehiculo.getEstado().equals("FUERA_DEL_PAIS")) {
            log.warn("Estado inválido para admisión temporal: {}",
                    vehiculo.getEstado());
            throw new RuntimeException(
                    "Estado inválido para ADMISION_TEMPORAL. "
                            + "Estado actual: " + vehiculo.getEstado());
        }

        // REGLA 3: la fecha de ingreso no puede ser futura
        // No se puede registrar un ingreso que aún no ocurrió
        if (dto.getFechaIngreso().isAfter(LocalDateTime.now())) {
            log.warn("Fecha de ingreso futura: {}",
                    dto.getFechaIngreso());
            throw new RuntimeException(
                    "La fecha de ingreso no puede ser futura");
        }

        Entry nuevo = new Entry();
        nuevo.setPatente(dto.getPatente().toUpperCase());
        nuevo.setRutConductor(dto.getRutConductor());
        nuevo.setPaisOrigen(dto.getPaisOrigen());
        nuevo.setPasoFronterizo(dto.getPasoFronterizo());
        nuevo.setFechaIngreso(dto.getFechaIngreso());
        nuevo.setTipoIngreso(dto.getTipoIngreso());
        nuevo.setEstado("PENDIENTE");
        Entry guardado = entryRepository.save(nuevo);

        if (dto.getTipoIngreso().equals("RETORNO")) {
            // Vehículo chileno que regresa → vuelve a EN_TERRITORIO_NACIONAL
            actualizarEstadoVehiculo(
                    dto.getPatente(), "EN_TERRITORIO_NACIONAL");
        } else {
            // Vehículo extranjero que entra → pasa a ADMISION_TEMPORAL
            actualizarEstadoVehiculo(
                    dto.getPatente(), "ADMISION_TEMPORAL");
        }

        log.info("Ingreso registrado con id: {}", guardado.getId());
        return guardado;
    }

    public Entry autorizar(Long id, String rutFiscalizador,
                           String observaciones) {
        log.info("Autorizando ingreso id: {} por fiscalizador: {}",
                id, rutFiscalizador);

        Entry ingreso = obtenerPorId(id);

        // REGLA: solo se pueden autorizar ingresos PENDIENTES
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

    public Entry rechazar(Long id, String rutFiscalizador,
                          String observaciones) {
        log.info("Rechazando ingreso id: {} por fiscalizador: {}",
                id, rutFiscalizador);

        Entry ingreso = obtenerPorId(id);

        // REGLA: solo se pueden rechazar ingresos PENDIENTES
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

        // Al rechazar el ingreso el vehículo vuelve a su estado anterior
        // RETORNO rechazado → el vehículo sigue FUERA_DEL_PAIS
        // ADMISION_TEMPORAL rechazada → el vehículo sigue EN_TERRITORIO_NACIONAL
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

    private VehicleResponseDTO verificarVehiculoEnVehicleService(
            String patente) {
        try {
            log.info("Consultando Vehicle Service para patente: {}",
                    patente);
            return webClient.get()
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

    private void actualizarEstadoVehiculo(String patente,
                                          String nuevoEstado) {
        try {
            log.info("Actualizando estado del vehículo {} a {}",
                    patente, nuevoEstado);
            webClient.patch()
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