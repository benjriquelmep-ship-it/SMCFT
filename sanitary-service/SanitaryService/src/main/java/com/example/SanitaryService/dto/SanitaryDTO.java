// Este archivo es el DTO de entrada para registrar una inspección sanitaria
// DTO = formulario de entrada — define qué datos debe mandar
// el cliente cuando quiere registrar una nueva inspección
// Si falta algún campo obligatorio → responde HTTP 400 automáticamente
package com.example.SanitaryService.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class SanitaryDTO {

    // Patente del vehículo que será inspeccionado
    @NotBlank(message = "La patente es obligatoria")
    @Size(max = 10,
            message = "La patente no puede tener más de 10 caracteres")
    private String patente;

    // RUT del conductor del vehículo inspeccionado
    @NotBlank(message = "El RUT del conductor es obligatorio")
    @Size(max = 12,
            message = "El RUT no puede tener más de 12 caracteres")
    private String rutConductor;

    // RUT del inspector que realiza la inspección sanitaria
    @NotBlank(message = "El RUT del inspector es obligatorio")
    @Size(max = 12,
            message = "El RUT no puede tener más de 12 caracteres")
    private String rutInspector;

    // Nombre del paso fronterizo donde se realiza la inspección
    @NotBlank(message = "El paso fronterizo es obligatorio")
    @Size(max = 100,
            message = "El paso no puede tener más de 100 caracteres")
    private String pasoFronterizo;

    // Fecha y hora en que se realiza la inspección sanitaria
    @NotNull(message = "La fecha de inspección es obligatoria")
    private LocalDateTime fechaInspeccion;

    // Observaciones adicionales sobre la inspección
    private String observaciones;
}