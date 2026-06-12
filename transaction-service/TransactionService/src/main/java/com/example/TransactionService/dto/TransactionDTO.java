// Objeto de transferencia de datos para validar la creación de una transacción
package com.example.TransactionService.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.math.BigDecimal;
import io.swagger.v3.oas.annotations.media.Schema;

@Data
@Schema(description = "DTO que representa transaction")
public class TransactionDTO {

    // RUN o identificador oficial del usuario involucrado
    @NotBlank(message = "El RUT del usuario es obligatorio")
    @Size(max = 12,
            message = "El RUT no puede tener más de 12 caracteres")
    @Schema(description = "Rut Usuario", example = "12345678-9", maxLength = 12)
    private String rutUsuario;

    // Naturaleza o clasificación del movimiento financiero
    @NotBlank(message = "El tipo es obligatorio")
    @Pattern(
            regexp = "PAGO_MULTA|PAGO_TASA|DEVOLUCION|COBRO_SERVICIO",
            message = "El tipo debe ser PAGO_MULTA, PAGO_TASA, " +
                    "DEVOLUCION o COBRO_SERVICIO"
    )
    @Schema(description = "Tipo", example = "PARTICULAR")
    private String tipo;

    // Suma económica total acumulada del movimiento (mínimo 0.01)
    @NotNull(message = "El monto total es obligatorio")
    @DecimalMin(value = "0.01",
            message = "El monto debe ser mayor a 0")
    @Schema(description = "Monto Total", example = "15000.50")
    private BigDecimal montoTotal;

    // Justificación o glosa explicativa del registro (máx 500 caracteres)
    @NotBlank(message = "La descripción es obligatoria")
    @Size(max = 500,
            message = "La descripción no puede tener más de 500 caracteres")
    @Schema(description = "Descripcion", example = "ejemplo", maxLength = 500)
    private String descripcion;

    // Código, folio externo o identificador del documento asociado
    @Size(max = 100,
            message = "La referencia no puede tener más de 100 caracteres")
    @Schema(description = "Referencia", example = "ejemplo", maxLength = 100)
    private String referencia;
}