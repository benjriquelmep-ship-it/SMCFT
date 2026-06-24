package com.example.TransactionService.service;

import com.example.TransactionService.dto.TransactionDTO;
import com.example.TransactionService.dto.UserResponseDTO;
import com.example.TransactionService.model.Transaction;
import com.example.TransactionService.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class TransactionService {


    private final TransactionRepository transactionRepository;
    private final WebClient webClient;

    public List<Transaction> obtenerTodas() {
        log.info("Obteniendo todas las transacciones");
        return transactionRepository.findAll();
    }

    public Transaction obtenerPorId(Long id) {
        log.info("Buscando transacción con id: {}", id);
        return transactionRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Transacción con id {} no encontrada", id);
                    return new RuntimeException(
                            "Transacción no encontrada con id: " + id);
                });
    }

    public Transaction registrar(TransactionDTO dto) {
        log.info("Registrando transacción para: {}", dto.getRutUsuario());

        verificarUsuarioEnUserService(dto.getRutUsuario());

        // REGLA DE NEGOCIO: el monto debe ser mayor a 0
        if (dto.getMontoTotal().compareTo(BigDecimal.ZERO) <= 0) {
            log.warn("Monto inválido: {}", dto.getMontoTotal());
            throw new RuntimeException(
                    "El monto total debe ser mayor a 0");
        }

        Transaction nueva = new Transaction();
        nueva.setRutUsuario(dto.getRutUsuario());
        nueva.setTipo(dto.getTipo());
        nueva.setMontoTotal(dto.getMontoTotal());
        nueva.setEstado("PENDIENTE");
        nueva.setDescripcion(dto.getDescripcion());
        nueva.setReferencia(dto.getReferencia());
        nueva.setCreatedAt(LocalDateTime.now());

        Transaction guardada = transactionRepository.save(nueva);
        log.info("Transacción registrada con id: {}", guardada.getId());
        return guardada;
    }

    public Transaction completar(Long id) {
        log.info("Completando transacción con id: {}", id);
        Transaction transaccion = obtenerPorId(id);

        // REGLA DE NEGOCIO: solo se pueden completar transacciones PENDIENTES
        if (!transaccion.getEstado().equals("PENDIENTE")) {
            log.warn("Transacción {} no está PENDIENTE. Estado: {}",
                    id, transaccion.getEstado());
            throw new RuntimeException(
                    "Solo se pueden completar transacciones PENDIENTES. "
                            + "Estado actual: " + transaccion.getEstado());
        }

        transaccion.setEstado("COMPLETADA");
        transaccion.setCompletadaAt(LocalDateTime.now());

        Transaction actualizada = transactionRepository.save(transaccion);
        log.info("Transacción {} completada correctamente", id);
        return actualizada;
    }

    public Transaction rechazar(Long id) {
        log.info("Rechazando transacción con id: {}", id);
        Transaction transaccion = obtenerPorId(id);

        // REGLA DE NEGOCIO: solo se pueden rechazar transacciones PENDIENTES
        if (!transaccion.getEstado().equals("PENDIENTE")) {
            log.warn("Transacción {} no está PENDIENTE. Estado: {}",
                    id, transaccion.getEstado());
            throw new RuntimeException(
                    "Solo se pueden rechazar transacciones PENDIENTES. "
                            + "Estado actual: " + transaccion.getEstado());
        }

        transaccion.setEstado("RECHAZADA");
        Transaction actualizada = transactionRepository.save(transaccion);
        log.info("Transacción {} rechazada correctamente", id);
        return actualizada;
    }

    public Transaction anular(Long id) {
        log.info("Anulando transacción con id: {}", id);
        Transaction transaccion = obtenerPorId(id);

        // REGLA DE NEGOCIO: no se pueden anular transacciones ya ANULADAS
        if (transaccion.getEstado().equals("ANULADA")) {
            log.warn("Transacción {} ya está ANULADA", id);
            throw new RuntimeException(
                    "La transacción ya está anulada");
        }

        transaccion.setEstado("ANULADA");
        Transaction actualizada = transactionRepository.save(transaccion);
        log.info("Transacción {} anulada correctamente", id);
        return actualizada;
    }

    public void eliminar(Long id) {
        log.info("Eliminando transacción con id: {}", id);
        if (!transactionRepository.existsById(id)) {
            log.warn("Transacción con id {} no encontrada", id);
            throw new RuntimeException(
                    "Transacción no encontrada con id: " + id);
        }
        transactionRepository.deleteById(id);
        log.info("Transacción {} eliminado correctamente", id);
    }


    public List<Transaction> obtenerPorUsuario(String rutUsuario) {
        log.info("Obteniendo transacciones del usuario: {}", rutUsuario);
        return transactionRepository.findByRutUsuario(rutUsuario);
    }

    public List<Transaction> obtenerPorTipo(String tipo) {
        log.info("Obteniendo transacciones de tipo: {}", tipo);
        return transactionRepository.findByTipo(tipo);
    }

    public List<Transaction> obtenerPorEstado(String estado) {
        log.info("Obteniendo transacciones con estado: {}", estado);
        return transactionRepository.findByEstado(estado);
    }

    public List<Transaction> obtenerPorUsuarioYEstado(
            String rutUsuario, String estado) {
        log.info("Obteniendo transacciones de {} con estado {}",
                rutUsuario, estado);
        return transactionRepository.findByRutUsuarioAndEstado(
                rutUsuario, estado);
    }

    public List<Transaction> obtenerPorRangoFechas(
            LocalDateTime desde, LocalDateTime hasta) {
        log.info("Obteniendo transacciones entre {} y {}", desde, hasta);
        return transactionRepository.findByCreatedAtBetween(
                desde, hasta);
    }

    public List<Transaction> obtenerPorMontoMayorA(BigDecimal monto) {
        log.info("Obteniendo transacciones con monto mayor a {}", monto);
        return transactionRepository.findByMontoTotalGreaterThan(monto);
    }

    public List<Transaction> buscarPorDescripcion(String texto) {
        log.info("Buscando transacciones con descripción: {}", texto);
        return transactionRepository
                .findByDescripcionContainingIgnoreCase(texto);
    }

    public List<Transaction> obtenerPorUsuarioOrdenadas(
            String rutUsuario) {
        log.info("Obteniendo transacciones de {} ordenadas", rutUsuario);
        return transactionRepository
                .findByRutUsuarioOrderByCreatedAtDesc(rutUsuario);
    }

    public List<Transaction> obtenerUltimasTransacciones() {
        log.info("Obteniendo las últimas 10 transacciones");
        return transactionRepository.findTop10ByOrderByIdDesc();
    }

    public long contarPorEstado(String estado) {
        log.info("Contando transacciones con estado: {}", estado);
        return transactionRepository.countByEstado(estado);
    }

    public long contarPorTipo(String tipo) {
        log.info("Contando transacciones de tipo: {}", tipo);
        return transactionRepository.countByTipo(tipo);
    }

    private void verificarUsuarioEnUserService(String rut) {
        try {
            log.info("Verificando usuario {} en User Service", rut);

            UserResponseDTO usuario = webClient.get()
                    // GET http://localhost:8082/api/v1/users/rut/12345678-9
                    .uri("/api/v1/users/rut/{rut}", rut)
                    .retrieve()
                    .bodyToMono(UserResponseDTO.class)
                    .block();

            // REGLA DE NEGOCIO: el usuario debe estar activo
            if (!usuario.getActivo()) {
                log.warn("Usuario {} está inactivo", rut);
                throw new RuntimeException(
                        "El usuario con RUT " + rut
                                + " está inactivo en el sistema");
            }

            log.info("Usuario {} verificado correctamente", rut);

        } catch (WebClientResponseException.NotFound e) {
            log.warn("Usuario {} no encontrado en User Service", rut);
            throw new RuntimeException(
                    "El usuario con RUT " + rut
                            + " no existe en el sistema");

        } catch (RuntimeException e) {
            throw e;

        } catch (Exception e) {
            log.error("Error al comunicarse con User Service: {}",
                    e.getMessage());
            throw new RuntimeException(
                    "Error al verificar el usuario. "
                            + "Verifique que User Service esté corriendo "
                            + "en el puerto 8082");
        }
    }
}