// Representa la respuesta que llega desde Vehicle Service
// cuando Border Crossing verifica el estado del vehículo
package com.example.BorderCrossingService.dto;

import lombok.Data;
import io.swagger.v3.oas.annotations.media.Schema;

@Data
@Schema(description = "Payload analítico de salida que expone las propiedades y situación de localización vigentes de una unidad importadas desde el Vehicle Service")
public class VehicleResponseDTO {

    @Schema(description = "Identificador único de la unidad vehicular (Clave primaria)", example = "1")
    private Long id;

    @Schema(description = "Placa patente única identificatoria del vehículo consultado", example = "ABCD-12")
    private String patente;

    @Schema(description = "Marca del fabricante automotriz", example = "Suzuki")
    private String marca;

    @Schema(description = "Modelo comercial de la unidad", example = "Grand Vitara")
    private String modelo;

    @Schema(description = "Año de fabricación cronológico", example = "2024")
    private Integer anio;

    @Schema(description = "Categoría de transporte homologada para transitar", example = "PARTICULAR")
    private String tipoVehiculo;

    @Schema(description = "RUN/RUT oficial de la persona natural o jurídica propietaria del bien", example = "12345678-9")
    private String rutPropietario;

    // Estado actual del vehículo
    // Debe ser EN_TERRITORIO_NACIONAL para poder registrar una salida
    @Schema(
            description = "Situación registral y de localización fronteriza controlada por el ecosistema distribuido",
            example = "EN_TERRITORIO_NACIONAL",
            allowableValues = {"EN_TERRITORIO_NACIONAL", "FUERA_DEL_PAIS", "ENCARGO_ROBO", "RETENIDO"}
    )
    private String estado;
}