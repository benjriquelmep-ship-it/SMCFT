// dto/VehicleDTO.java
// Lo que el cliente manda en el body del POST y PUT
// Separado de la entidad para validar datos de entrada de forma limpia

package com.example.VehicleService.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class VehicleDTO {

    // @NotBlank valida que no sea null, vacío ni solo espacios
    @NotBlank(message = "La patente es obligatoria")
    @Size(max = 10, message = "La patente no puede tener más de 10 caracteres")
    private String patente;

    @NotBlank(message = "La marca es obligatoria")
    @Size(max = 50, message = "La marca no puede tener más de 50 caracteres")
    private String marca;

    @NotBlank(message = "El modelo es obligatorio")
    @Size(max = 50, message = "El modelo no puede tener más de 50 caracteres")
    private String modelo;

    // @NotNull porque es Integer — @NotBlank solo aplica a String
    @NotNull(message = "El año es obligatorio")
    @Min(value = 1900, message = "El año no puede ser menor a 1900")
    @Max(value = 2100, message = "El año no puede ser mayor a 2100")
    private Integer anio;

    // @Pattern valida que coincida con la expresión regular
    @NotBlank(message = "El tipo de vehículo es obligatorio")
    @Pattern(
            regexp = "PARTICULAR|DIPLOMATICO|COMERCIAL",
            message = "El tipo debe ser PARTICULAR, DIPLOMATICO o COMERCIAL"
    )
    private String tipoVehiculo;

    @NotBlank(message = "El RUT del propietario es obligatorio")
    @Size(max = 12, message = "El RUT no puede tener más de 12 caracteres")
    private String rutPropietario;
}