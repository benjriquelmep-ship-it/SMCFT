// Lo que el cliente manda para registrar un nuevo cruce fronterizo

package com.example.BorderCrossingService.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class BorderCrossingDTO {

    // Patente del vehículo que sale del país
    @NotBlank(message = "La patente es obligatoria")
    @Size(max = 10, message = "La patente no puede tener más de 10 caracteres")
    private String patente;

    // RUT del conductor
    @NotBlank(message = "El RUT del conductor es obligatorio")
    @Size(max = 12, message = "El RUT no puede tener más de 12 caracteres")
    private String rutConductor;

    // País de destino
    @NotBlank(message = "El país de destino es obligatorio")
    @Size(max = 100, message = "El país no puede tener más de 100 caracteres")
    private String paisDestino;

    // Paso fronterizo
    @NotBlank(message = "El paso fronterizo es obligatorio")
    @Size(max = 100, message = "El paso no puede tener más de 100 caracteres")
    private String pasoFronterizo;

    // Fecha y hora del cruce
    // @NotNull porque es LocalDateTime — @NotBlank solo aplica a String
    @NotNull(message = "La fecha del cruce es obligatoria")
    private LocalDateTime fechaCruce;
}