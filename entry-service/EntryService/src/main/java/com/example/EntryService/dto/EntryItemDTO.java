// Lo que el cliente manda para agregar un item declarado al ingreso

package com.example.EntryService.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class EntryItemDTO {

    // ID del ingreso al que pertenece este item
    @NotNull(message = "El ID del ingreso es obligatorio")
    private Long entryId;

    // Descripción del item
    @NotBlank(message = "La descripción es obligatoria")
    @Size(max = 200,
            message = "La descripción no puede tener más de 200 caracteres")
    private String descripcion;

    // Cantidad del item — mínimo 1
    @NotNull(message = "La cantidad es obligatoria")
    @Min(value = 1, message = "La cantidad debe ser al menos 1")
    private Integer cantidad;

    // Valor en dólares — no puede ser negativo
    @NotNull(message = "El valor en USD es obligatorio")
    @DecimalMin(value = "0.0",
            message = "El valor no puede ser negativo")
    private BigDecimal valorUsd;
}