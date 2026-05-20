// Repositorio JPA para gestionar las consultas de la tabla "transactions"
package com.example.TransactionService.repository;

import com.example.TransactionService.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TransactionRepository
        extends JpaRepository<Transaction, Long> {

    // IGUALDAD BÁSICA
    // Spring genera: SELECT * FROM transactions WHERE rut_usuario = ?
    List<Transaction> findByRutUsuario(String rutUsuario);

    // POR TIPO
    // Spring genera: SELECT * FROM transactions WHERE tipo = ?
    List<Transaction> findByTipo(String tipo);

    // POR ESTADO
    // Spring genera: SELECT * FROM transactions WHERE estado = ?
    List<Transaction> findByEstado(String estado);

    // AND
    // Transacciones de un usuario con estado específico
    // Spring genera: SELECT * FROM transactions
    //                WHERE rut_usuario = ? AND estado = ?
    List<Transaction> findByRutUsuarioAndEstado(
            String rutUsuario, String estado);

    // Transacciones de un tipo con estado específico
    // Spring genera: SELECT * FROM transactions
    //                WHERE tipo = ? AND estado = ?
    List<Transaction> findByTipoAndEstado(String tipo, String estado);

    // BETWEEN
    // Transacciones en un rango de fechas
    // Spring genera: SELECT * FROM transactions
    //                WHERE created_at BETWEEN ? AND ?
    List<Transaction> findByCreatedAtBetween(
            LocalDateTime desde, LocalDateTime hasta);

    // GREATER THAN
    // Transacciones con monto mayor a un valor
    // Spring genera: SELECT * FROM transactions WHERE monto_total > ?
    List<Transaction> findByMontoTotalGreaterThan(BigDecimal monto);

    // LESS THAN
    // Transacciones con monto menor a un valor
    // Spring genera: SELECT * FROM transactions WHERE monto_total < ?
    List<Transaction> findByMontoTotalLessThan(BigDecimal monto);

    // CONTAINING + IGNORE CASE
    // Búsqueda parcial por descripción
    // Spring genera: SELECT * FROM transactions
    //                WHERE LOWER(descripcion) LIKE LOWER('%texto%')
    List<Transaction> findByDescripcionContainingIgnoreCase(
            String texto);

    // ORDENAMIENTO
    // Transacciones de un usuario del más reciente al más antiguo
    // Spring genera: SELECT * FROM transactions
    //                WHERE rut_usuario = ? ORDER BY created_at DESC
    List<Transaction> findByRutUsuarioOrderByCreatedAtDesc(
            String rutUsuario);

    // Transacciones pendientes ordenadas
    // Spring genera: SELECT * FROM transactions
    //                WHERE estado = ? ORDER BY created_at ASC
    List<Transaction> findByEstadoOrderByCreatedAtAsc(String estado);

    // TOP
    // Las últimas 10 transacciones
    // Spring genera: SELECT * FROM transactions ORDER BY id DESC LIMIT 10
    List<Transaction> findTop10ByOrderByIdDesc();

    // COUNT
    // Contar transacciones por estado
    // Spring genera: SELECT COUNT(*) FROM transactions WHERE estado = ?
    long countByEstado(String estado);

    // Contar transacciones por tipo
    // Spring genera: SELECT COUNT(*) FROM transactions WHERE tipo = ?
    long countByTipo(String tipo);
}