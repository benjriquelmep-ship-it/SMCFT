// Lo que el cliente manda para crear o actualizar una categoría
package com.example.ItemCategoryService.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.math.BigDecimal;
import io.swagger.v3.oas.annotations.media.Schema;

@Data
@Schema(description = "Estructura de datos requerida para definir o actualizar una categoría arancelaria y sus restricciones de internación")
public class ItemCategoryDTO {

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 100, message = "El nombre no puede tener más de 100 caracteres")
    @Schema(
            description = "Nombre único de la categoría o subpartida de mercancía aduanera",
            example = "Electrónicos de Consumo Personal",
            maxLength = 100,
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String nombre;

    @NotBlank(message = "La descripción es obligatoria")
    @Size(max = 500, message = "La descripción no puede tener más de 500 caracteres")
    @Schema(
            description = "Glosa detallada que describe el tipo de artículos incluidos y criterios de catalogación",
            example = "Dispositivos móviles, computadores portátiles y accesorios tecnológicos sin fines comerciales directos.",
            maxLength = 500,
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String descripcion;

    // @NotNull porque es Boolean — @NotBlank solo aplica a String
    @NotNull(message = "El campo requiereDeclaracion es obligatorio")
    @Schema(
            description = "Bandera de control aduanero que determina si el pasajero u operador está obligado a declarar la mercancía en el andén",
            example = "true",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private Boolean requiereDeclaracion;

    // El límite de valor es opcional — puede ser null
    @DecimalMin(value = "0.0", message = "El límite no puede ser negativo")
    @Schema(
            description = "Monto máximo en USD (dólares americanos) permitido como franquicia antes de aplicar impuestos de internación",
            example = "500.00"
    )
    private BigDecimal limiteValorUsd;
}