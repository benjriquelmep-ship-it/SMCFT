// Representa la respuesta que llega desde Item Category Service
// cuando Border Crossing verifica las categorías de equipaje

package com.example.BorderCrossingService.dto;

import lombok.Data;

@Data
public class ItemCategoryResponseDTO {
    private Long id;
    private String nombre;
    private String descripcion;

    // Si la categoría requiere declaración especial en aduanas
    private Boolean requiereDeclaracion;

    // Si la categoría está activa en el sistema
    private Boolean activo;
}