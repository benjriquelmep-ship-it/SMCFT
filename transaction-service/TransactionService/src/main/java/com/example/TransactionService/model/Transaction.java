// Entidad JPA que mapea la estructura de la tabla "transactions" en la base de datos
package com.example.TransactionService.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import io.swagger.v3.oas.annotations.media.Schema;

@Entity
@Table(name = "transactions")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Entidad que representa transaction")
public class Transaction {

    // Clave primaria autoincremental de la tabla
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Id", example = "1")
    private Long id;

    // RUT del usuario que realiza la transacción
    @Column(name = "rut_usuario", nullable = false, length = 12)
    @Schema(description = "Rut Usuario", example = "12345678-9", maxLength = 12)
    private String rutUsuario;

    // Tipo de transacción
    // "PAGO_MULTA", "PAGO_TASA", "DEVOLUCION", "COBRO_SERVICIO"
    @Column(nullable = false, length = 50)
    @Schema(description = "Tipo", example = "PARTICULAR", maxLength = 50)
    private String tipo;

    // Monto total en pesos chilenos
    @Column(name = "monto_total", nullable = false,
            precision = 12, scale = 2)
    @Schema(description = "Monto Total", example = "15000.50")
    private BigDecimal montoTotal;

    // Estado de la transacción
    // "PENDIENTE", "COMPLETADA", "RECHAZADA", "ANULADA"
    @Column(nullable = false, length = 30)
    @Schema(description = "Estado", example = "ACTIVO", maxLength = 30)
    private String estado;

    // Descripción de la transacción
    @Column(nullable = false, length = 500)
    @Schema(description = "Descripcion", example = "ejemplo", maxLength = 500)
    private String descripcion;

    // Referencia externa — número de comprobante
    @Column(length = 100)
    @Schema(description = "Referencia", example = "ejemplo", maxLength = 100)
    private String referencia;

    // Fecha de creación
    @Column(name = "created_at")
    @Schema(description = "Created At", example = "2024-01-15")
    private LocalDateTime createdAt;

    // Fecha de completación
    @Column(name = "completada_at")
    @Schema(description = "Completada At", example = "2024-01-15")
    private LocalDateTime completadaAt;

    // RELACIÓN @OneToMany — una transacción tiene muchos detalles
    @OneToMany(mappedBy = "transaction",
            cascade = CascadeType.ALL,
            fetch = FetchType.LAZY)
    @com.fasterxml.jackson.annotation.JsonManagedReference
    private List<TransactionDetail> detalles;
}