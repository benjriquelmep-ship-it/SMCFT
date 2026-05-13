package com.example.TransactionService.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class TransactionDTO {

    @NotBlank(message = "El RUT del usuario es obligatorio")
    @Size(max = 12,
            message = "El RUT no puede tener más de 12 caracteres")
    private String rutUsuario;

    @NotBlank(message = "El tipo es obligatorio")
    @Pattern(
            regexp = "PAGO_MULTA|PAGO_TASA|DEVOLUCION|COBRO_SERVICIO",
            message = "El tipo debe ser PAGO_MULTA, PAGO_TASA, " +
                    "DEVOLUCION o COBRO_SERVICIO"
    )
    private String tipo;

    @NotNull(message = "El monto total es obligatorio")
    @DecimalMin(value = "0.01",
            message = "El monto debe ser mayor a 0")
    private BigDecimal montoTotal;

    @NotBlank(message = "La descripción es obligatoria")
    @Size(max = 500,
            message = "La descripción no puede tener más de 500 caracteres")
    private String descripcion;

    @Size(max = 100,
            message = "La referencia no puede tener más de 100 caracteres")
    private String referencia;
}