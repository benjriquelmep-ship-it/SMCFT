// Representa la respuesta que llega desde Vehicle Service
// cuando Entry Service verifica el estado del vehículo

package com.example.EntryService.dto;

import lombok.Data;
import io.swagger.v3.oas.annotations.media.Schema;

@Data
@Schema(description = "DTO que representa vehicle")
public class VehicleResponseDTO {
    @Schema(description = "Id", example = "1")
    private Long id;
    @Schema(description = "Patente", example = "ABC-123")
    private String patente;
    @Schema(description = "Marca", example = "Toyota")
    private String marca;
    @Schema(description = "Modelo", example = "Corolla")
    private String modelo;
    @Schema(description = "Anio", example = "2024")
    private Integer anio;
    @Schema(description = "Tipo Vehiculo", example = "PARTICULAR")
    private String tipoVehiculo;
    @Schema(description = "Rut Propietario", example = "12345678-9")
    private String rutPropietario;

    // Estado actual del vehículo
    // Para RETORNO debe ser FUERA_DEL_PAIS
    // Para ADMISION_TEMPORAL puede ser cualquier estado
    @Schema(description = "Estado", example = "ACTIVO")
    private String estado;
}