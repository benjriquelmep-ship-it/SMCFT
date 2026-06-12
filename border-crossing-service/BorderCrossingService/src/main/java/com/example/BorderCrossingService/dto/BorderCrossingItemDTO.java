// Lo que el cliente manda para agregar un item de equipaje al cruce

package com.example.BorderCrossingService.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.math.BigDecimal;
import io.swagger.v3.oas.annotations.media.Schema;

@Data
@Schema(description = "DTO que representa border crossing item")
public class BorderCrossingItemDTO {

    // ID del cruce al que pertenece este item
    @NotNull(message = "El ID del cruce es obligatorio")
    @Schema(description = "Border Crossing Id", example = "1")
    private Long borderCrossingId;

    // ID de la categoría del item en Item Category Service
    @NotNull(message = "El ID de la categoría es obligatorio")
    @Schema(description = "Categoria Id", example = "1")
    private Long categoriaId;

    // Descripción del item
    @NotBlank(message = "La descripción es obligatoria")
    @Size(max = 200,
            message = "La descripción no puede tener más de 200 caracteres")
    @Schema(description = "Descripcion", example = "ejemplo", maxLength = 200)
    private String descripcion;

    // Cantidad del item — mínimo 1
    @NotNull(message = "La cantidad es obligatoria")
    @Min(value = 1, message = "La cantidad debe ser al menos 1")
    @Schema(description = "Cantidad", example = "1")
    private Integer cantidad;

    // Valor en dólares — no puede ser negativo
    @NotNull(message = "El valor en USD es obligatorio")
    @DecimalMin(value = "0.0",
            message = "El valor no puede ser negativo")
    @Schema(description = "Valor Usd", example = "15000.50")
    private BigDecimal valorUsd;
}