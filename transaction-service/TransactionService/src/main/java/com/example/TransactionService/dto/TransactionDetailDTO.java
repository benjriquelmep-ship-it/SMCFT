// Objeto de transferencia de datos para validar la entrada de un detalle de transacción
package com.example.TransactionService.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.math.BigDecimal;
import io.swagger.v3.oas.annotations.media.Schema;

@Data
@Schema(description = "Estructura de datos que desglosa los ítems, impuestos o cobros específicos contenidos en una transacción aduanera")
public class TransactionDetailDTO {

    // ID de la transacción de cabecera asociada
    @NotNull(message = "El ID de la transacción es obligatorio")
    @Schema(
            description = "Identificador único (ID) de la transacción principal a la cual pertenece este desglose",
            example = "1",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private Long transactionId;

    // Glosa, ítem o nombre del concepto a registrar (máx 200 caracteres)
    @NotBlank(message = "El concepto es obligatorio")
    @Size(max = 200, message = "El concepto no puede tener más de 200 caracteres")
    @Schema(
            description = "Glosa o concepto del ítem cobrado (ej. Arancel aduanero, tasa de ingreso, multa por exceso de equipaje)",
            example = "Tasa de Internación de Mercancías",
            maxLength = 200,
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String concepto;

    // Valor monetario unitario del concepto (mínimo 0.01)
    @NotNull(message = "El monto es obligatorio")
    @DecimalMin(value = "0.01", message = "El monto debe ser mayor a 0")
    @Schema(
            description = "Monto financiero unitario asignado al concepto (en valor decimal)",
            example = "15000.50",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private BigDecimal monto;

    // Número de unidades del concepto a aplicar (mínimo 1)
    @NotNull(message = "La cantidad es obligatoria")
    @Min(value = 1, message = "La cantidad debe ser al menos 1")
    @Schema(
            description = "Número de unidades, tasas o multiplicador aplicado al concepto",
            example = "1",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private Integer cantidad;

    // Clasificación de la línea, restringido por expresión regular
    @NotBlank(message = "El tipo de detalle es obligatorio")
    @Pattern(
            regexp = "COBRO|DESCUENTO|IMPUESTO",
            message = "El tipo debe ser COBRO, DESCUENTO o IMPUESTO"
    )
    @Schema(
            description = "Clasificación o naturaleza contable del detalle de la línea",
            example = "IMPUESTO",
            allowableValues = {"COBRO", "DESCUENTO", "IMPUESTO"},
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String tipoDetalle;
}