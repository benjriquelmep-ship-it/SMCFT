// Este archivo es el DTO de respuesta del Vehicle Service
// Representa los datos que llegan cuando Sanitary Service
// consulta un vehículo por su patente
package com.example.SanitaryService.dto;

import lombok.Data;
import io.swagger.v3.oas.annotations.media.Schema;

@Data
@Schema(description = "Payload que procesa la información técnica e historial del vehículo importada desde el Vehicle Service para auditorías sanitarias")
public class VehicleResponseDTO {

    @Schema(
            description = "Identificador único del vehículo en la base de datos origen",
            example = "1"
    )
    private Long id;

    @Schema(
            description = "Placa patente única de circulación vehicular",
            example = "ABCD-12"
    )
    private String patente;

    @Schema(
            description = "Fabricante o marca comercial de la unidad automotriz",
            example = "Suzuki"
    )
    private String marca;

    @Schema(
            description = "Línea o modelo comercial del vehículo consultado",
            example = "Grand Vitara"
    )
    private String modelo;

    @Schema(
            description = "Año calendario de fabricación de la estructura",
            example = "2024"
    )
    private Integer anio;

    @Schema(
            description = "Clasificación aduanera del tipo de vehículo",
            example = "PARTICULAR"
    )
    private String tipoVehiculo;

    @Schema(
            description = "RUN/RUT del propietario legal registrado del móvil",
            example = "12345678-9"
    )
    private String rutPropietario;

    @Schema(
            description = "Estado operativo actual del vehículo en frontera (Indica si está habilitado para someterse a fiscalización fitozoosanitaria)",
            example = "EN_TERRITORIO_NACIONAL"
    )
    private String estado;
}