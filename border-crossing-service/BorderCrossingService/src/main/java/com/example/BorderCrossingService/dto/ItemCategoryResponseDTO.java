// Representa la respuesta que llega desde Item Category Service
// cuando Border Crossing verifica las categorías de equipaje
package com.example.BorderCrossingService.dto;

import lombok.Data;
import io.swagger.v3.oas.annotations.media.Schema;

@Data
@Schema(description = "Payload analítico de salida que importa las reglas, restricciones y banderas de declaración desde el Item Category Service")
public class ItemCategoryResponseDTO {

    @Schema(description = "Identificador único (ID) de la categoría arancelaria en origen", example = "1")
    private Long id;

    @Schema(description = "Nombre descriptor oficial de la subpartida de mercancías", example = "Electrónicos de Consumo Personal")
    private String nombre;

    @Schema(description = "Glosa o alcance normativo de la categoría de equipaje", example = "Dispositivos móviles, computadores y accesorios tecnológicos.")
    private String descripcion;

    // Si la categoría requiere declaración especial en aduanas
    @Schema(description = "Bandera regulatoria que determina si la categoría obliga legalmente a un levantamiento de declaración jurada", example = "true")
    private Boolean requiereDeclaracion;

    // Si la categoría está activa en el sistema
    @Schema(description = "Flag de vigencia de la categoría (Border Crossing solo admitirá catálogos operativos activos)", example = "true")
    private Boolean activo;
}