package com.example.TransactionService.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "transactions")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // RUT del usuario que realiza la transacción
    @Column(name = "rut_usuario", nullable = false, length = 12)
    private String rutUsuario;

    // Tipo de transacción
    // "PAGO_MULTA", "PAGO_TASA", "DEVOLUCION", "COBRO_SERVICIO"
    @Column(nullable = false, length = 50)
    private String tipo;

    // Monto total en pesos chilenos
    @Column(name = "monto_total", nullable = false,
            precision = 12, scale = 2)
    private BigDecimal montoTotal;

    // Estado de la transacción
    // "PENDIENTE", "COMPLETADA", "RECHAZADA", "ANULADA"
    @Column(nullable = false, length = 30)
    private String estado;

    // Descripción de la transacción
    @Column(nullable = false, length = 500)
    private String descripcion;

    // Referencia externa — número de comprobante
    @Column(length = 100)
    private String referencia;

    // Fecha de creación
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    // Fecha de completación
    @Column(name = "completada_at")
    private LocalDateTime completadaAt;

    // RELACIÓN @OneToMany — una transacción tiene muchos detalles
    @OneToMany(mappedBy = "transaction",
            cascade = CascadeType.ALL,
            fetch = FetchType.LAZY)
    @com.fasterxml.jackson.annotation.JsonManagedReference
    private List<TransactionDetail> detalles;
}