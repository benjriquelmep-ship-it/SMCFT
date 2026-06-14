// Lo que el cliente manda para agregar un item de equipaje al cruce
package com.example.BorderCrossingService.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.math.BigDecimal;
import io.swagger.v3.oas.annotations.media.Schema;

@Data
@Schema(description = "Estructura de datos requerida para declarar un artículo o mercancía individual acoplada al equipaje del cruce")
public class BorderCrossingItemDTO {

    // ID del cruce al que pertenece este item
    @NotNull(message = "El ID del cruce es obligatorio")
    @Schema(
            description = "Identificador único (ID) de la orden de cruce fronterizo de cabecera",
            example = "1",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private Long borderCrossingId;

    // ID de la categoría del item en Item Category Service
    @NotNull(message = "El ID de la categoría es obligatorio")
    @Schema(
            description = "Identificador único de la categoría arancelaria provisto por el Item Category Service",
            example = "1",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private Long categoriaId;

    // Descripción del item
    @NotBlank(message = "La descripción es obligatoria")
    @Size(max = 200, message = "La descripción no puede tener más de 200 caracteres")
    @Schema(
            description = "Glosa detallada o nombre comercial del artículo o bien bajo aforo",
            example = "Cámara fotográfica profesional con lentes de repuesto",
            maxLength = 200,
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String descripcion;

    // Cantidad del item — mínimo 1
    @NotNull(message = "La cantidad es obligatoria")
    @Min(value = 1, message = "La cantidad debe ser al menos 1")
    @Schema(
            description = "Volumen o cantidad física de unidades del artículo que componen el equipaje",
            example = "1",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private Integer cantidad;

    // Valor en dólares — no puede ser negativo
    @NotNull(message = "El valor en USD es obligatorio")
    @DecimalMin(value = "0.0", message = "El valor no puede ser negativo")
    @Schema(
            description = "Valoración comercial estimada del artículo expresada en dólares americanos (USD)",
            example = "850.50",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private BigDecimal valorUsd;
}