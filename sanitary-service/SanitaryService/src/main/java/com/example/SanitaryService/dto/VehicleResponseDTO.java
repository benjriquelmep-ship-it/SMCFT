// Este archivo es el DTO de respuesta del Vehicle Service
// Representa los datos que llegan cuando Sanitary Service
// consulta un vehículo por su patente

package com.example.SanitaryService.dto;

import lombok.Data;
import io.swagger.v3.oas.annotations.media.Schema;

@Data
@Schema(description = "DTO que representa vehicle")
public class VehicleResponseDTO {

    // Id del vehículo en la BD de Vehicle Service
    // Sanitary Service no lo usa directamente
    // pero llega en la respuesta
    @Schema(description = "Id", example = "1")
    private Long id;

    // Patente del vehículo
    // Sanitary Service la usó para consultar este vehículo
    @Schema(description = "Patente", example = "ABC-123")
    private String patente;

    // Marca del vehículo
    @Schema(description = "Marca", example = "Toyota")
    private String marca;

    // Modelo del vehículo
    @Schema(description = "Modelo", example = "Corolla")
    private String modelo;

    // Año de fabricación del vehículo
    @Schema(description = "Anio", example = "2024")
    private Integer anio;

    // Tipo de vehículo según su clasificación
    @Schema(description = "Tipo Vehiculo", example = "PARTICULAR")
    private String tipoVehiculo;

    // RUT del propietario del vehículo
    @Schema(description = "Rut Propietario", example = "12345678-9")
    private String rutPropietario;

    // Estado actual del vehículo en el sistema fronterizo
    // Sanitary Service lo lee para verificar si el vehículo
    // puede ser inspeccionado
    @Schema(description = "Estado", example = "ACTIVO")
    private String estado;
}