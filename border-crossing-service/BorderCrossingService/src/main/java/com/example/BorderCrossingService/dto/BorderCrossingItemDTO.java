// Lo que el cliente manda para agregar un item de equipaje al cruce

package com.example.BorderCrossingService.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class BorderCrossingItemDTO {

    // ID del cruce al que pertenece este item
    @NotNull(message = "El ID del cruce es obligatorio")
    private Long borderCrossingId;

    // ID de la categoría del item en Item Category Service
    @NotNull(message = "El ID de la categoría es obligatorio")
    private Long categoriaId;

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