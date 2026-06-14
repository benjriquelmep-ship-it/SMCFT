// Representa la respuesta que llega desde Vehicle Service
// cuando Entry Service verifica el estado del vehículo
package com.example.EntryService.dto;

import lombok.Data;
import io.swagger.v3.oas.annotations.media.Schema;

@Data
@Schema(description = "Payload analítico de salida que expone las propiedades y situación legal vigentes de un vehículo importadas desde el Vehicle Service")
public class VehicleResponseDTO {

    @Schema(description = "Identificador único del vehículo en la persistencia central (Clave primaria)", example = "1")
    private Long id;

    @Schema(description = "Placa patente única del vehículo consultado", example = "ABCD-12")
    private String patente;

    @Schema(description = "Marca del fabricante del vehículo automotor", example = "Suzuki")
    private String marca;

    @Schema(description = "Modelo comercial específico de la unidad", example = "Grand Vitara")
    private String modelo;

    @Schema(description = "Año de fabricación cronológico del vehículo", example = "2024")
    private Integer anio;

    @Schema(description = "Categoría operativa del vehículo según su homologación de tránsito", example = "PARTICULAR")
    private String tipoVehicle;

    @Schema(description = "RUN/RUT de identidad oficial de la persona natural o jurídica dueña del bien", example = "12345678-9")
    private String rutPropietario;

    // Estado actual del vehículo
    // Para RETORNO debe ser FUERA_DEL_PAIS
    // Para ADMISION_TEMPORAL puede ser cualquier estado
    @Schema(
            description = "Situación registral y de localización geográfica actual controlada por el sistema aduanero",
            example = "FUERA_DEL_PAIS",
            allowableValues = {"EN_PAIS", "FUERA_DEL_PAIS", "ENCARGO_ROBO", "RETENIDO"}
    )
    private String estado;
}