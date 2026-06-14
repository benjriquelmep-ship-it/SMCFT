// Objeto de transferencia de datos para validar la creación de una transacción
package com.example.TransactionService.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.math.BigDecimal;
import io.swagger.v3.oas.annotations.media.Schema;

@Data
@Schema(description = "Estructura de datos requerida para el registro o facturación de una transacción financiera en el control de aduanas")
public class TransactionDTO {

    // RUN o identificador oficial del usuario involucrado
    @NotBlank(message = "El RUT del usuario es obligatorio")
    @Size(max = 12, message = "El RUT no puede tener más de 12 caracteres")
    @Schema(
            description = "RUN/RUT del usuario, viajero o transportista asociado a la transacción",
            example = "12345678-9",
            maxLength = 12,
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String rutUsuario;

    // Naturaleza o clasificación del movimiento financiero
    @NotBlank(message = "El tipo es obligatorio")
    @Pattern(
            regexp = "PAGO_MULTA|PAGO_TASA|DEVOLUCION|COBRO_SERVICIO",
            message = "El tipo debe ser PAGO_MULTA, PAGO_TASA, DEVOLUCION o COBRO_SERVICIO"
    )
    @Schema(
            description = "Naturaleza, destino o clasificación del movimiento financiero",
            example = "PAGO_TASA",
            allowableValues = {"PAGO_MULTA", "PAGO_TASA", "DEVOLUCION", "COBRO_SERVICIO"},
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String tipo;

    // Suma económica total acumulada del movimiento (mínimo 0.01)
    @NotNull(message = "El monto total es obligatorio")
    @DecimalMin(value = "0.01", message = "El monto debe ser mayor a 0")
    @Schema(
            description = "Monto económico total consolidado de la transacción (Suma neta del movimiento)",
            example = "45000.00",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private BigDecimal montoTotal;

    // Justificación o glosa explicativa del registro (máx 500 caracteres)
    @NotBlank(message = "La descripción es obligatoria")
    @Size(max = 500, message = "La descripción no puede tener más de 500 caracteres")
    @Schema(
            description = "Justificación o glosa descriptiva detallada del motivo de la transacción",
            example = "Cobro de tasa aduanera obligatoria por internación de repuestos automotrices",
            maxLength = 500,
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String descripcion;

    // Código, folio externo o identificador del documento asociado
    @Size(max = 100, message = "La referencia no puede tener más de 100 caracteres")
    @Schema(
            description = "Código, número de folio o identificador externo del documento de respaldo (ej. Comprobante bancario, declaración jurada)",
            example = "REF-2026-XYZ88",
            maxLength = 100
    )
    private String referencia;
}