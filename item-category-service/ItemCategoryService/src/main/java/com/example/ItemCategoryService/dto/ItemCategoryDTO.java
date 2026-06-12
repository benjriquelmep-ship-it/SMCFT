// Lo que el cliente manda para crear o actualizar una categoría

package com.example.ItemCategoryService.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.math.BigDecimal;
import io.swagger.v3.oas.annotations.media.Schema;

@Data
@Schema(description = "DTO que representa item category")
public class ItemCategoryDTO {

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 100,
            message = "El nombre no puede tener más de 100 caracteres")
    @Schema(description = "Nombre", example = "Juan Pérez", maxLength = 100)
    private String nombre;

    @NotBlank(message = "La descripción es obligatoria")
    @Size(max = 500,
            message = "La descripción no puede tener más de 500 caracteres")
    @Schema(description = "Descripcion", example = "ejemplo", maxLength = 500)
    private String descripcion;

    // @NotNull porque es Boolean — @NotBlank solo aplica a String
    @NotNull(message = "El campo requiereDeclaracion es obligatorio")
    @Schema(description = "Requiere Declaracion", example = "true")
    private Boolean requiereDeclaracion;

    // El límite de valor es opcional — puede ser null
    @DecimalMin(value = "0.0",
            message = "El límite no puede ser negativo")
    @Schema(description = "Limite Valor Usd", example = "15000.50")
    private BigDecimal limiteValorUsd;
}