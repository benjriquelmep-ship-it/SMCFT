// Lo que el cliente manda para crear o actualizar un item
package com.example.ItemCategoryService.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import io.swagger.v3.oas.annotations.media.Schema;

@Data
@Schema(description = "Estructura de datos requerida para el alta, registro o modificación de un artículo o mercancía individual en el catálogo")
public class ItemDTO {

    // ID de la categoría a la que pertenece este item
    @NotNull(message = "El ID de la categoría es obligatorio")
    @Schema(
            description = "Identificador único (ID) de la categoría arancelaria a la cual se asocia este artículo",
            example = "1",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private Long categoriaId;

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 100, message = "El nombre no puede tener más de 100 caracteres")
    @Schema(
            description = "Nombre específico o denominación técnica comercial del artículo",
            example = "Notebook Corporativo",
            maxLength = 100,
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String nombre;

    @NotBlank(message = "La descripción es obligatoria")
    @Size(max = 300, message = "La descripción no puede tener más de 300 caracteres")
    @Schema(
            description = "Características técnicas básicas, modelo, marca o especificaciones generales del bien",
            example = "Pantalla de 14 pulgadas, procesador i7, 16GB RAM para uso personal.",
            maxLength = 300,
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String descripcion;

    @NotBlank(message = "La unidad es obligatoria")
    @Size(max = 30, message = "La unidad no puede tener más de 30 caracteres")
    @Schema(
            description = "Unidad métrica o física de empaque bajo la cual se cuantifica la cantidad ingresada",
            example = "Unidades",
            maxLength = 30,
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String unidad;
}