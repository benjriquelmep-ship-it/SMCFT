// Lo que el cliente manda para registrar un ingreso al país

package com.example.EntryService.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class EntryDTO {

    // Patente del vehículo que ingresa
    @NotBlank(message = "La patente es obligatoria")
    @Size(max = 10,
            message = "La patente no puede tener más de 10 caracteres")
    private String patente;

    // RUT del conductor
    @NotBlank(message = "El RUT del conductor es obligatorio")
    @Size(max = 12,
            message = "El RUT no puede tener más de 12 caracteres")
    private String rutConductor;

    // País de origen
    @NotBlank(message = "El país de origen es obligatorio")
    @Size(max = 100,
            message = "El país no puede tener más de 100 caracteres")
    private String paisOrigen;

    // Paso fronterizo
    @NotBlank(message = "El paso fronterizo es obligatorio")
    @Size(max = 100,
            message = "El paso no puede tener más de 100 caracteres")
    private String pasoFronterizo;

    // Fecha y hora del ingreso
    @NotNull(message = "La fecha de ingreso es obligatoria")
    private LocalDateTime fechaIngreso;

    // Tipo de ingreso
    @NotBlank(message = "El tipo de ingreso es obligatorio")
    @Pattern(
            regexp = "RETORNO|ADMISION_TEMPORAL",
            message = "El tipo debe ser RETORNO o ADMISION_TEMPORAL"
    )
    private String tipoIngreso;
}
