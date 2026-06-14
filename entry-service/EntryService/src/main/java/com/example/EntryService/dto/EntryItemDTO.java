// Lo que el cliente manda para agregar un item declarado al ingreso
package com.example.EntryService.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.math.BigDecimal;
import io.swagger.v3.oas.annotations.media.Schema;

@Data
@Schema(description = "Estructura de datos requerida para declarar un artículo, equipaje o mercancía transportada dentro de la unidad vehicular")
public class EntryItemDTO {

    // ID del ingreso al que pertenece este item
    @NotNull(message = "El ID del ingreso es obligatorio")
    @Schema(
            description = "Identificador único (ID) del trámite de ingreso de cabecera al cual se asocia esta declaración",
            example = "1",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private Long entryId;

    // Descripción del item
    @NotBlank(message = "La descripción es obligatoria")
    @Size(max = 200, message = "La descripción no puede tener más de 200 caracteres")
    @Schema(
            description = "Glosa detallada, nombre comercial o características del artículo declarado",
            example = "Televisor LED de 55 pulgadas y caja de herramientas personales",
            maxLength = 200,
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String descripcion;

    // Cantidad del item — mínimo 1
    @NotNull(message = "La cantidad es obligatoria")
    @Min(value = 1, message = "La cantidad debe ser al menos 1")
    @Schema(
            description = "Volumen o cantidad física de unidades del mismo artículo declaradas",
            example = "1",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private Integer cantidad;

    // Valor en dólares — no puede ser negativo
    @NotNull(message = "El valor en USD es obligatorio")
    @DecimalMin(value = "0.0", message = "El valor no puede ser negativo")
    @Schema(
            description = "Valoración aduanera comercial estimada del bien expresada en dólares americanos (USD)",
            example = "450.00",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private BigDecimal valorUsd;
}