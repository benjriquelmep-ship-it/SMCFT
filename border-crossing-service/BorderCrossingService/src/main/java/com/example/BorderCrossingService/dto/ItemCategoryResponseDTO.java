// Representa la respuesta que llega desde Item Category Service
// cuando Border Crossing verifica las categorías de equipaje

package com.example.BorderCrossingService.dto;

import lombok.Data;
import io.swagger.v3.oas.annotations.media.Schema;

@Data
@Schema(description = "DTO que representa item category")
public class ItemCategoryResponseDTO {
    @Schema(description = "Id", example = "1")
    private Long id;
    @Schema(description = "Nombre", example = "Juan Pérez")
    private String nombre;
    @Schema(description = "Descripcion", example = "ejemplo")
    private String descripcion;

    // Si la categoría requiere declaración especial en aduanas
    @Schema(description = "Requiere Declaracion", example = "true")
    private Boolean requiereDeclaracion;

    // Si la categoría está activa en el sistema
    @Schema(description = "Activo", example = "true")
    private Boolean activo;
}