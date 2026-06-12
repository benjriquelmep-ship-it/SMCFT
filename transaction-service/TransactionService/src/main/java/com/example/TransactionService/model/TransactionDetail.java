// Entidad JPA que mapea la estructura de la tabla "transaction_details" en la base de datos
package com.example.TransactionService.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.math.BigDecimal;
import io.swagger.v3.oas.annotations.media.Schema;

@Entity
@Table(name = "transaction_details")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Entidad que representa transaction detail")
public class TransactionDetail {

    // Identificador único de la línea de detalle
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Id", example = "1")
    private Long id;

    // RELACIÓN @ManyToOne — muchos detalles pertenecen a una transacción
    @ManyToOne
    @JoinColumn(name = "transaction_id", nullable = false)
    @com.fasterxml.jackson.annotation.JsonBackReference
    private Transaction transaction;

    // Descripción del concepto cobrado
    @Column(nullable = false, length = 200)
    @Schema(description = "Concepto", example = "ejemplo", maxLength = 200)
    private String concepto;

    // Monto de este concepto
    @Column(nullable = false, precision = 12, scale = 2)
    @Schema(description = "Monto", example = "15000.50")
    private BigDecimal monto;

    // Cantidad de unidades
    @Column(nullable = false)
    @Schema(description = "Cantidad", example = "1")
    private Integer cantidad;

    // Tipo de detalle
    // "COBRO", "DESCUENTO", "IMPUESTO"
    @Column(name = "tipo_detalle", nullable = false, length = 30)
    @Schema(description = "Tipo Detalle", example = "PARTICULAR", maxLength = 30)
    private String tipoDetalle;
}