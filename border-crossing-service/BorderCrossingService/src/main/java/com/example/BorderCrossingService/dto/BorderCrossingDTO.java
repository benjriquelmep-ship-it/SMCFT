// Lo que el cliente manda para registrar un nuevo cruce fronterizo

package com.example.BorderCrossingService.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.time.LocalDateTime;
import io.swagger.v3.oas.annotations.media.Schema;

@Data
@Schema(description = "DTO que representa border crossing")
public class BorderCrossingDTO {

    // Patente del vehículo que sale del país
    @NotBlank(message = "La patente es obligatoria")
    @Size(max = 10, message = "La patente no puede tener más de 10 caracteres")
    @Schema(description = "Patente", example = "ABC-123", maxLength = 10)
    private String patente;

    // RUT del conductor
    @NotBlank(message = "El RUT del conductor es obligatorio")
    @Size(max = 12, message = "El RUT no puede tener más de 12 caracteres")
    @Schema(description = "Rut Conductor", example = "12345678-9", maxLength = 12)
    private String rutConductor;

    // País de destino
    @NotBlank(message = "El país de destino es obligatorio")
    @Size(max = 100, message = "El país no puede tener más de 100 caracteres")
    @Schema(description = "Pais Destino", example = "ejemplo", maxLength = 100)
    private String paisDestino;

    // Paso fronterizo
    @NotBlank(message = "El paso fronterizo es obligatorio")
    @Size(max = 100, message = "El paso no puede tener más de 100 caracteres")
    @Schema(description = "Paso Fronterizo", example = "ejemplo", maxLength = 100)
    private String pasoFronterizo;

    // Fecha y hora del cruce
    // @NotNull porque es LocalDateTime — @NotBlank solo aplica a String
    @NotNull(message = "La fecha del cruce es obligatoria")
    @Schema(description = "Fecha Cruce", example = "2024-01-15")
    private LocalDateTime fechaCruce;
}