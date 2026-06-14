// Objeto de transferencia de datos para validar el registro y edición de vehículos
package com.example.VehicleService.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import io.swagger.v3.oas.annotations.media.Schema;

@Data
@Schema(description = "Estructura de datos requerida para dar de alta o actualizar las propiedades técnicas de un vehículo en el parque automotor")
public class VehicleDTO {

    @NotBlank(message = "La patente es obligatoria")
    @Size(max = 10, message = "La patente no puede tener más de 10 caracteres")
    @Schema(
            description = "Placa patente identificatoria única del vehículo",
            example = "ABCD-12",
            maxLength = 10,
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String patente;

    @NotBlank(message = "La marca es obligatoria")
    @Size(max = 50, message = "La marca no puede tener más de 50 caracteres")
    @Schema(
            description = "Nombre del fabricante o marca automotriz de la unidad",
            example = "Suzuki",
            maxLength = 50,
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String marca;

    @NotBlank(message = "El modelo es obligatorio")
    @Size(max = 50, message = "El modelo no puede tener más de 50 caracteres")
    @Schema(
            description = "Línea comercial o modelo específico del vehículo",
            example = "Grand Vitara",
            maxLength = 50,
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String modelo;

    @NotNull(message = "El año es obligatorio")
    @Min(value = 1900, message = "El año no puede ser menor a 1900")
    @Max(value = 2100, message = "El año no puede ser mayor a 2100")
    @Schema(
            description = "Año calendario de fabricación de la unidad",
            example = "2024",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private Integer anio;

    @NotBlank(message = "El tipo de vehículo es obligatorio")
    @Pattern(
            regexp = "PARTICULAR|DIPLOMATICO|COMERCIAL",
            message = "El tipo debe ser PARTICULAR, DIPLOMATICO o COMERCIAL"
    )
    @Schema(
            description = "Categoría o tipo de circulación legal del vehículo según la regulación aduanera",
            example = "PARTICULAR",
            allowableValues = {"PARTICULAR", "DIPLOMATICO", "COMERCIAL"},
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String tipoVehiculo;

    @NotBlank(message = "El RUT del propietario es obligatorio")
    @Size(max = 12, message = "El RUT no puede tener más de 12 caracteres")
    @Schema(
            description = "RUN/RUT del propietario asignado y validado en el sistema",
            example = "12345678-9",
            maxLength = 12,
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String rutPropietario;

    @Schema(
            description = "Situación actual de tránsito fronterizo o estado aduanero de la unidad",
            example = "EN_TERRITORIO_NACIONAL",
            allowableValues = {"EN_TERRITORIO_NACIONAL", "FUERA_DEL_PAIS", "ADMISION_TEMPORAL"},
            maxLength = 12
    )
    private String estado;
}