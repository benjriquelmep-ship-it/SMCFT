package com.example.SanitaryService.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class SanitaryDTO {

    @NotBlank(message = "La patente es obligatoria")
    @Size(max = 10,
            message = "La patente no puede tener más de 10 caracteres")
    private String patente;

    @NotBlank(message = "El RUT del conductor es obligatorio")
    @Size(max = 12,
            message = "El RUT no puede tener más de 12 caracteres")
    private String rutConductor;

    @NotBlank(message = "El RUT del inspector es obligatorio")
    @Size(max = 12,
            message = "El RUT no puede tener más de 12 caracteres")
    private String rutInspector;

    @NotBlank(message = "El paso fronterizo es obligatorio")
    @Size(max = 100,
            message = "El paso no puede tener más de 100 caracteres")
    private String pasoFronterizo;

    @NotNull(message = "La fecha de inspección es obligatoria")
    private LocalDateTime fechaInspeccion;

    private String observaciones;
}