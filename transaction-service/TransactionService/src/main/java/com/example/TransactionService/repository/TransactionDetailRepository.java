// Repositorio JPA para gestionar las consultas de la tabla "transaction_details"
package com.example.TransactionService.repository;

import com.example.TransactionService.model.TransactionDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface TransactionDetailRepository
        extends JpaRepository<TransactionDetail, Long> {

    // Todos los detalles de una transacción
    // Spring genera: SELECT * FROM transaction_details
    //                WHERE transaction_id = ?
    List<TransactionDetail> findByTransactionId(Long transactionId);

    // Por tipo de detalle
    // Spring genera: SELECT * FROM transaction_details
    //                WHERE tipo_detalle = ?
    List<TransactionDetail> findByTipoDetalle(String tipoDetalle);

    // Detalles de una transacción por tipo
    // Spring genera: SELECT * FROM transaction_details
    //                WHERE transaction_id = ? AND tipo_detalle = ?
    List<TransactionDetail> findByTransactionIdAndTipoDetalle(
            Long transactionId, String tipoDetalle);

    // CONTAINING + IGNORE CASE
    // Búsqueda parcial por concepto
    // Spring genera: SELECT * FROM transaction_details
    //                WHERE LOWER(concepto) LIKE LOWER('%texto%')
    List<TransactionDetail> findByConceptoContainingIgnoreCase(
            String concepto);

    // ORDENAMIENTO
    // Detalles de una transacción ordenados por monto
    // Spring genera: SELECT * FROM transaction_details
    //                WHERE transaction_id = ? ORDER BY monto DESC
    List<TransactionDetail> findByTransactionIdOrderByMontoDesc(
            Long transactionId);

    // TOP
    // Los últimos 10 detalles
    // Spring genera: SELECT * FROM transaction_details
    //                ORDER BY id DESC LIMIT 10
    List<TransactionDetail> findTop10ByOrderByIdDesc();
}