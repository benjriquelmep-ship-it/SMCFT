package com.example.TransactionService.service;

import com.example.TransactionService.dto.TransactionDetailDTO;
import com.example.TransactionService.model.Transaction;
import com.example.TransactionService.model.TransactionDetail;
import com.example.TransactionService.repository.TransactionDetailRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class TransactionDetailService {


    private final TransactionDetailRepository detailRepository;
    private final TransactionService transactionService;


    public List<TransactionDetail> obtenerTodos() {
        log.info("Obteniendo todos los detalles de transacción");
        return detailRepository.findAll();
    }

    public TransactionDetail obtenerPorId(Long id) {
        log.info("Buscando detalle con id: {}", id);
        return detailRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Detalle con id {} no encontrado", id);
                    return new RuntimeException(
                            "Detalle no encontrado con id: " + id);
                });
    }

    public TransactionDetail agregar(TransactionDetailDTO dto) {
        log.info("Agregando detalle a transacción: {}",
                dto.getTransactionId());

        // REGLA DE NEGOCIO 1: la transacción debe existir
        Transaction transaccion =
                transactionService.obtenerPorId(dto.getTransactionId());

        // REGLA DE NEGOCIO 2: solo se agregan detalles a
        // transacciones PENDIENTES
        if (!transaccion.getEstado().equals("PENDIENTE")) {
            log.warn("No se pueden agregar detalles — estado: {}",
                    transaccion.getEstado());
            throw new RuntimeException(
                    "Solo se pueden agregar detalles a transacciones PENDIENTES");
        }

        TransactionDetail detalle = new TransactionDetail();
        detalle.setTransaction(transaccion);
        detalle.setConcepto(dto.getConcepto());
        detalle.setMonto(dto.getMonto());
        detalle.setCantidad(dto.getCantidad());
        detalle.setTipoDetalle(dto.getTipoDetalle());

        TransactionDetail guardado = detailRepository.save(detalle);
        log.info("Detalle agregado con id: {}", guardado.getId());
        return guardado;
    }

    public TransactionDetail actualizar(Long id,
                                        TransactionDetailDTO dto) {
        log.info("Actualizando detalle con id: {}", id);
        TransactionDetail existente = obtenerPorId(id);

        existente.setConcepto(dto.getConcepto());
        existente.setMonto(dto.getMonto());
        existente.setCantidad(dto.getCantidad());
        existente.setTipoDetalle(dto.getTipoDetalle());

        TransactionDetail actualizado = detailRepository.save(existente);
        log.info("Detalle {} actualizado correctamente", id);
        return actualizado;
    }

    public void eliminar(Long id) {
        log.info("Eliminando detalle con id: {}", id);
        if (!detailRepository.existsById(id)) {
            log.warn("Detalle con id {} no encontrado", id);
            throw new RuntimeException(
                    "Detalle no encontrado con id: " + id);
        }
        detailRepository.deleteById(id);
        log.info("Detalle {} eliminado correctamente", id);
    }

    public List<TransactionDetail> obtenerPorTransaccion(
            Long transactionId) {
        log.info("Obteniendo detalles de transacción: {}",
                transactionId);
        return detailRepository.findByTransactionId(transactionId);
    }

    public List<TransactionDetail> obtenerPorTipoDetalle(
            String tipoDetalle) {
        log.info("Obteniendo detalles de tipo: {}", tipoDetalle);
        return detailRepository.findByTipoDetalle(tipoDetalle);
    }

    public List<TransactionDetail> obtenerPorTransaccionYTipo(
            Long transactionId, String tipoDetalle) {
        log.info("Obteniendo detalles de {} tipo {}",
                transactionId, tipoDetalle);
        return detailRepository.findByTransactionIdAndTipoDetalle(
                transactionId, tipoDetalle);
    }

    public List<TransactionDetail> buscarPorConcepto(String concepto) {
        log.info("Buscando detalles con concepto: {}", concepto);
        return detailRepository
                .findByConceptoContainingIgnoreCase(concepto);
    }

    public List<TransactionDetail> obtenerPorTransaccionOrdenados(
            Long transactionId) {
        log.info("Obteniendo detalles de {} ordenados por monto",
                transactionId);
        return detailRepository
                .findByTransactionIdOrderByMontoDesc(transactionId);
    }

    public List<TransactionDetail> obtenerUltimosDetalles() {
        log.info("Obteniendo los últimos 10 detalles");
        return detailRepository.findTop10ByOrderByIdDesc();
    }
}