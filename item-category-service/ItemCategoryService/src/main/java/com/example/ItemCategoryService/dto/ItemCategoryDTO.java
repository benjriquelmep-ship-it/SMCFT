// Lo que el cliente manda para crear o actualizar una categoría

package com.example.ItemCategoryService.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class ItemCategoryDTO {

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 100,
            message = "El nombre no puede tener más de 100 caracteres")
    private String nombre;

    @NotBlank(message = "La descripción es obligatoria")
    @Size(max = 500,
            message = "La descripción no puede tener más de 500 caracteres")
    private String descripcion;

    // @NotNull porque es Boolean — @NotBlank solo aplica a String
    @NotNull(message = "El campo requiereDeclaracion es obligatorio")
    private Boolean requiereDeclaracion;

    // El límite de valor es opcional — puede ser null
    @DecimalMin(value = "0.0",
            message = "El límite no puede ser negativo")
    private BigDecimal limiteValorUsd;
}