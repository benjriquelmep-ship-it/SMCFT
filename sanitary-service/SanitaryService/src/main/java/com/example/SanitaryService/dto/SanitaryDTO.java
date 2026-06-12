// Este archivo es el DTO de entrada para registrar una inspección sanitaria
// DTO = formulario de entrada — define qué datos debe mandar
// el cliente cuando quiere registrar una nueva inspección
// Si falta algún campo obligatorio → responde HTTP 400 automáticamente
package com.example.SanitaryService.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.time.LocalDateTime;
import io.swagger.v3.oas.annotations.media.Schema;

@Data
@Schema(description = "DTO que representa sanitary")
public class SanitaryDTO {

    // Patente del vehículo que será inspeccionado
    @NotBlank(message = "La patente es obligatoria")
    @Size(max = 10,
            message = "La patente no puede tener más de 10 caracteres")
    @Schema(description = "Patente", example = "ABC-123", maxLength = 10)
    private String patente;

    // RUT del conductor del vehículo inspeccionado
    @NotBlank(message = "El RUT del conductor es obligatorio")
    @Size(max = 12,
            message = "El RUT no puede tener más de 12 caracteres")
    @Schema(description = "Rut Conductor", example = "12345678-9", maxLength = 12)
    private String rutConductor;

    // RUT del inspector que realiza la inspección sanitaria
    @NotBlank(message = "El RUT del inspector es obligatorio")
    @Size(max = 12,
            message = "El RUT no puede tener más de 12 caracteres")
    @Schema(description = "Rut Inspector", example = "12345678-9", maxLength = 12)
    private String rutInspector;

    // Nombre del paso fronterizo donde se realiza la inspección
    @NotBlank(message = "El paso fronterizo es obligatorio")
    @Size(max = 100,
            message = "El paso no puede tener más de 100 caracteres")
    @Schema(description = "Paso Fronterizo", example = "ejemplo", maxLength = 100)
    private String pasoFronterizo;

    // Fecha y hora en que se realiza la inspección sanitaria
    @NotNull(message = "La fecha de inspección es obligatoria")
    @Schema(description = "Fecha Inspeccion", example = "2024-01-15")
    private LocalDateTime fechaInspeccion;

    // Observaciones adicionales sobre la inspección
    @Schema(description = "Observaciones", example = "Observación de ejemplo")
    private String observaciones;
}