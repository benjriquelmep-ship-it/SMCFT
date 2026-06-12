// Lo que el cliente manda para crear o actualizar un item

package com.example.ItemCategoryService.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import io.swagger.v3.oas.annotations.media.Schema;

@Data
@Schema(description = "DTO que representa item")
public class ItemDTO {

    // ID de la categoría a la que pertenece este item
    @NotNull(message = "El ID de la categoría es obligatorio")
    @Schema(description = "Categoria Id", example = "1")
    private Long categoriaId;

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 100,
            message = "El nombre no puede tener más de 100 caracteres")
    @Schema(description = "Nombre", example = "Juan Pérez", maxLength = 100)
    private String nombre;

    @NotBlank(message = "La descripción es obligatoria")
    @Size(max = 300,
            message = "La descripción no puede tener más de 300 caracteres")
    @Schema(description = "Descripcion", example = "ejemplo", maxLength = 300)
    private String descripcion;

    @NotBlank(message = "La unidad es obligatoria")
    @Size(max = 30,
            message = "La unidad no puede tener más de 30 caracteres")
    @Schema(description = "Unidad", example = "ejemplo", maxLength = 30)
    private String unidad;
}