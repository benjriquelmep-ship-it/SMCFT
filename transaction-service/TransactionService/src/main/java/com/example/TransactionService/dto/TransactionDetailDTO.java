// Objeto de transferencia de datos para validar la entrada de un detalle de transacción
package com.example.TransactionService.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class TransactionDetailDTO {

    // ID de la transacción de cabecera asociada
    @NotNull(message = "El ID de la transacción es obligatorio")
    private Long transactionId;

    // Glosa, ítem o nombre del concepto a registrar (máx 200 caracteres)
    @NotBlank(message = "El concepto es obligatorio")
    @Size(max = 200,
            message = "El concepto no puede tener más de 200 caracteres")
    private String concepto;

    // Valor monetario unitario del concepto (mínimo 0.01)
    @NotNull(message = "El monto es obligatorio")
    @DecimalMin(value = "0.01",
            message = "El monto debe ser mayor a 0")
    private BigDecimal monto;

    // Número de unidades del concepto a aplicar (mínimo 1)
    @NotNull(message = "La cantidad es obligatoria")
    @Min(value = 1, message = "La cantidad debe ser al menos 1")
    private Integer cantidad;

    // Clasificación de la línea, restringido por expresión regular
    @NotBlank(message = "El tipo de detalle es obligatorio")
    @Pattern(
            regexp = "COBRO|DESCUENTO|IMPUESTO",
            message = "El tipo debe ser COBRO, DESCUENTO o IMPUESTO"
    )
    private String tipoDetalle;
}