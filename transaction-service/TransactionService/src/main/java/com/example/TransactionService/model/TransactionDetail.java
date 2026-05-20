// Entidad JPA que mapea la estructura de la tabla "transaction_details" en la base de datos
package com.example.TransactionService.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.math.BigDecimal;

@Entity
@Table(name = "transaction_details")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransactionDetail {

    // Identificador único de la línea de detalle
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // RELACIÓN @ManyToOne — muchos detalles pertenecen a una transacción
    @ManyToOne
    @JoinColumn(name = "transaction_id", nullable = false)
    @com.fasterxml.jackson.annotation.JsonBackReference
    private Transaction transaction;

    // Descripción del concepto cobrado
    @Column(nullable = false, length = 200)
    private String concepto;

    // Monto de este concepto
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal monto;

    // Cantidad de unidades
    @Column(nullable = false)
    private Integer cantidad;

    // Tipo de detalle
    // "COBRO", "DESCUENTO", "IMPUESTO"
    @Column(name = "tipo_detalle", nullable = false, length = 30)
    private String tipoDetalle;
}