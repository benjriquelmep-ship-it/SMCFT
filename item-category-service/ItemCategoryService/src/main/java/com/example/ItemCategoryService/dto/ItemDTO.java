// dto/ItemDTO.java
// Lo que el cliente manda para crear o actualizar un item

package com.example.ItemCategoryService.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class ItemDTO {

    // ID de la categoría a la que pertenece este item
    @NotNull(message = "El ID de la categoría es obligatorio")
    private Long categoriaId;

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 100,
            message = "El nombre no puede tener más de 100 caracteres")
    private String nombre;

    @NotBlank(message = "La descripción es obligatoria")
    @Size(max = 300,
            message = "La descripción no puede tener más de 300 caracteres")
    private String descripcion;

    @NotBlank(message = "La unidad es obligatoria")
    @Size(max = 30,
            message = "La unidad no puede tener más de 30 caracteres")
    private String unidad;
}