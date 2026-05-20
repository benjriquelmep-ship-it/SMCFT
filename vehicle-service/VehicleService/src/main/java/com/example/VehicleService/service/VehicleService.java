// Capa de servicio principal encargada de procesar las reglas de negocio de vehículos y la comunicación reactiva entre servicios
package com.example.VehicleService.service;

import com.example.VehicleService.dto.UserResponseDTO;
import com.example.VehicleService.dto.VehicleDTO;
import com.example.VehicleService.model.Vehicle;
import com.example.VehicleService.repository.VehicleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class VehicleService {


    private final VehicleRepository vehicleRepository;

    // WebClient para comunicarse con User Service
    private final WebClient webClient;

    // -------------------------------------------------------
    // CRUD BÁSICO
    // -------------------------------------------------------

    // Devuelve el listado completo de vehículos del parque automotor
    public List<Vehicle> obtenerTodos() {
        log.info("Obteniendo todos los vehículos");
        return vehicleRepository.findAll();
    }

    // Busca un vehículo mediante su ID primario o lanza una excepción en caso de no existir
    public Vehicle obtenerPorId(Long id) {
        log.info("Buscando vehículo con id: {}", id);
        return vehicleRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Vehículo con id {} no encontrado", id);
                    return new RuntimeException(
                            "Vehículo no encontrado con id: " + id);
                });
    }

    // Entry Service y Border Crossing Service usan este método
    public Vehicle obtenerPorPatente(String patente) {
        log.info("Buscando vehículo con patente: {}", patente);
        return vehicleRepository.findByPatente(patente)
                .orElseThrow(() -> {
                    log.warn("Vehículo con patente {} no encontrado", patente);
                    return new RuntimeException(
                            "Vehículo no encontrado con patente: " + patente);
                });
    }

    // Registra un nuevo vehículo en el sistema controlando duplicados y consultando de forma remota al dueño
    public Vehicle registrar(VehicleDTO dto) {
        log.info("Registrando vehículo con patente: {}", dto.getPatente());

        // REGLA DE NEGOCIO 1: no puede existir otra patente igual
        if (vehicleRepository.existsByPatente(
                dto.getPatente().toUpperCase())) {
            log.warn("Patente duplicada: {}", dto.getPatente());
            throw new RuntimeException(
                    "Ya existe un vehículo con la patente: " + dto.getPatente());
        }

        // REGLA DE NEGOCIO 2: año no puede ser mayor al actual
        int anioActual = java.time.Year.now().getValue();
        if (dto.getAnio() > anioActual) {
            log.warn("Año inválido: {} mayor al actual {}", dto.getAnio(),
                    anioActual);
            throw new RuntimeException(
                    "El año del vehículo no puede ser mayor al año actual: "
                            + anioActual);
        }

        // REGLA DE NEGOCIO 3: el propietario debe existir en User Service
        // Comunicación entre microservicios — IE 2.4.1 de la rúbrica
        verificarPropietarioEnUserService(dto.getRutPropietario());

        // Mapeo DTO → Entidad
        Vehicle nuevo = new Vehicle();
        // Siempre en mayúsculas para consistencia
        nuevo.setPatente(dto.getPatente().toUpperCase());
        nuevo.setMarca(dto.getMarca());
        nuevo.setModelo(dto.getModelo());
        nuevo.setAnio(dto.getAnio());
        nuevo.setTipoVehiculo(dto.getTipoVehiculo());
        nuevo.setRutPropietario(dto.getRutPropietario());
        // Estado inicial siempre EN_TERRITORIO_NACIONAL
        nuevo.setEstado("EN_TERRITORIO_NACIONAL");

        Vehicle guardado = vehicleRepository.save(nuevo);
        log.info("Vehículo registrado con id: {}", guardado.getId());
        return guardado;
    }

    // Actualiza las características mecánicas y comerciales básicas de un móvil
    public Vehicle actualizar(Long id, VehicleDTO dto) {
        log.info("Actualizando vehículo con id: {}", id);
        Vehicle existente = obtenerPorId(id);

        // No actualizamos la patente — es el identificador único
        // No actualizamos el propietario — es un cambio legal complejo
        existente.setMarca(dto.getMarca());
        existente.setModelo(dto.getModelo());
        existente.setAnio(dto.getAnio());
        existente.setTipoVehiculo(dto.getTipoVehiculo());

        Vehicle actualizado = vehicleRepository.save(existente);
        log.info("Vehículo {} actualizado correctamente", id);
        return actualizado;
    }

    // Entry Service y Border Crossing Service llaman este método
    public Vehicle actualizarEstado(String patente, String nuevoEstado) {
        log.info("Actualizando estado del vehículo {} a {}", patente,
                nuevoEstado);

        // REGLA DE NEGOCIO: solo estados válidos del dominio fronterizo
        if (!nuevoEstado.matches(
                "EN_TERRITORIO_NACIONAL|FUERA_DEL_PAIS|ADMISION_TEMPORAL")) {
            log.warn("Estado inválido: {}", nuevoEstado);
            throw new RuntimeException(
                    "Estado inválido. Debe ser: EN_TERRITORIO_NACIONAL, "
                            + "FUERA_DEL_PAIS o ADMISION_TEMPORAL");
        }

        Vehicle vehiculo = obtenerPorPatente(patente);
        String estadoAnterior = vehiculo.getEstado();
        vehiculo.setEstado(nuevoEstado);

        Vehicle actualizado = vehicleRepository.save(vehiculo);
        log.info("Vehículo {} cambió estado: {} → {}",
                patente, estadoAnterior, nuevoEstado);
        return actualizado;
    }

    // Remueve físicamente el registro automotor de la base de datos MySQL
    public void eliminar(Long id) {
        log.info("Eliminando vehículo con id: {}", id);
        Vehicle existente = obtenerPorId(id);
        vehicleRepository.delete(existente);
        log.info("Vehículo {} eliminado correctamente", id);
    }

    // -------------------------------------------------------
    // CONSULTAS DERIVADAS
    // -------------------------------------------------------

    // Recupera la lista de vehículos vinculados al RUN de un propietario
    public List<Vehicle> obtenerPorPropietario(String rutPropietario) {
        log.info("Obteniendo vehículos del propietario: {}", rutPropietario);
        return vehicleRepository.findByRutPropietario(rutPropietario);
    }

    // Recupera el conjunto de autos que comparten una misma situación aduanera
    public List<Vehicle> obtenerPorEstado(String estado) {
        log.info("Obteniendo vehículos con estado: {}", estado);
        return vehicleRepository.findByEstado(estado);
    }

    // Filtra las unidades que corresponden a una categoría técnica o diplomática específica
    public List<Vehicle> obtenerPorTipo(String tipo) {
        log.info("Obteniendo vehículos de tipo: {}", tipo);
        return vehicleRepository.findByTipoVehiculo(tipo);
    }

    // Cruza filtros para localizar los bienes de un dueño bajo una condición de tránsito exacta
    public List<Vehicle> obtenerPorPropietarioYEstado(
            String rut, String estado) {
        log.info("Obteniendo vehículos del propietario {} con estado {}",
                rut, estado);
        return vehicleRepository.findByRutPropietarioAndEstado(rut, estado);
    }

    // Devuelve los vehículos cuyo modelo sea igual o superior al año ingresado
    public List<Vehicle> obtenerPorAnioDesde(Integer anio) {
        log.info("Obteniendo vehículos desde el año: {}", anio);
        return vehicleRepository.findByAnioGreaterThanEqual(anio);
    }

    // Obtiene las unidades automotrices construidas en un rango específico de años
    public List<Vehicle> obtenerPorRangoAnio(Integer desde, Integer hasta) {
        log.info("Obteniendo vehículos entre {} y {}", desde, hasta);
        return vehicleRepository.findByAnioBetween(desde, hasta);
    }

    // Realiza búsquedas parciales ignorando diferencias entre mayúsculas y minúsculas por marca
    public List<Vehicle> buscarPorMarca(String marca) {
        log.info("Buscando vehículos con marca: {}", marca);
        return vehicleRepository.findByMarcaContainingIgnoreCase(marca);
    }

    // Obtiene los automóviles de un RUN ordenados de forma descendente por año de fabricación
    public List<Vehicle> obtenerPorPropietarioOrdenadoPorAnio(String rut) {
        log.info("Obteniendo vehículos del propietario {} ordenados", rut);
        return vehicleRepository.findByRutPropietarioOrderByAnioDesc(rut);
    }

    // Devuelve los últimos 10 automóviles indexados cronológicamente por clave primaria
    public List<Vehicle> obtenerUltimosRegistrados() {
        log.info("Obteniendo los últimos 10 vehículos registrados");
        return vehicleRepository.findTop10ByOrderByIdDesc();
    }

    // Verifica que el propietario existe en User Service
    // Se llama antes de registrar un vehículo nuevo
    private void verificarPropietarioEnUserService(String rut) {
        try {
            log.info("Verificando propietario en User Service: {}", rut);

            UserResponseDTO usuario = webClient.get()
                    // GET http://localhost:8082/api/v1/users/rut/12345678-9
                    .uri("/api/v1/users/rut/{rut}", rut)
                    .retrieve()
                    .bodyToMono(UserResponseDTO.class)
                    .block();

            // REGLA DE NEGOCIO: el propietario debe estar activo
            if (!usuario.getActivo()) {
                log.warn("Propietario inactivo: {}", rut);
                throw new RuntimeException(
                        "El propietario con RUT " + rut
                                + " está inactivo en el sistema");
            }

            log.info("Propietario verificado correctamente: {}", rut);

        } catch (WebClientResponseException.NotFound e) {
            // User Service retornó 404 — el RUT no existe
            log.warn("Propietario no encontrado en User Service: {}", rut);
            throw new RuntimeException(
                    "El propietario con RUT " + rut
                            + " no existe en el sistema");

        } catch (RuntimeException e) {
            // Re-lanzar excepciones de negocio sin envolverlas
            throw e;

        } catch (Exception e) {
            // Error de comunicación con User Service
            log.error("Error al comunicarse con User Service: {}",
                    e.getMessage());
            throw new RuntimeException(
                    "Error al verificar el propietario. "
                            + "Verifique que User Service esté corriendo en el puerto 8082");
        }
    }
}