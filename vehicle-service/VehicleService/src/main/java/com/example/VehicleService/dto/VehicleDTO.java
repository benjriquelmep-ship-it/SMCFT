// Objeto de transferencia de datos para validar el registro y edición de vehículos
package com.example.VehicleService.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class VehicleDTO {

    // Placa patente identificatoria única del vehículo (Formato nacional o extranjero)
    // @NotBlank valida que no sea null, vacío ni solo espacios
    @NotBlank(message = "La patente es obligatoria")
    @Size(max = 10, message = "La patente no puede tener más de 10 caracteres")
    private String patente;

    // Nombre del fabricante o marca automotriz
    @NotBlank(message = "La marca es obligatoria")
    @Size(max = 50, message = "La marca no puede tener más de 50 caracteres")
    private String marca;

    // Línea o modelo específico del vehículo
    @NotBlank(message = "El modelo es obligatorio")
    @Size(max = 50, message = "El modelo no puede tener más de 50 caracteres")
    private String modelo;

    // Año de fabricación o modelo cronológico del móvil
    // @NotNull porque es Integer — @NotBlank solo aplica a String
    @NotNull(message = "El año es obligatorio")
    @Min(value = 1900, message = "El año no puede ser menor a 1900")
    @Max(value = 2100, message = "El año no puede ser mayor a 2100")
    private Integer anio;

    // Categoría de circulación legal del vehículo según regulaciones
    // @Pattern valida que coincida con la expresión regular
    @NotBlank(message = "El tipo de vehículo es obligatorio")
    @Pattern(
            regexp = "PARTICULAR|DIPLOMATICO|COMERCIAL",
            message = "El tipo debe ser PARTICULAR, DIPLOMATICO o COMERCIAL"
    )
    private String tipoVehiculo;

    // Documento de identidad (RUT) del dueño asignado
    @NotBlank(message = "El RUT del propietario es obligatorio")
    @Size(max = 12, message = "El RUT no puede tener más de 12 caracteres")
    private String rutPropietario;
}