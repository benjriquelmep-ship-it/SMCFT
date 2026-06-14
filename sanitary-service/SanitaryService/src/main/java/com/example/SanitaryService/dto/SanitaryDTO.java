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
@Schema(description = "Estructura de datos requerida para el alta y apertura de una nueva orden de inspección sanitaria fronteriza")
public class SanitaryDTO {

    // Patente del vehículo que será inspeccionado
    @NotBlank(message = "La patente es obligatoria")
    @Size(max = 10, message = "La patente no puede tener más de 10 caracteres")
    @Schema(
            description = "Placa patente única de identificación del vehículo que ingresa a control",
            example = "ABCD-12",
            maxLength = 10,
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String patente;

    // RUT del conductor del vehículo inspeccionado
    @NotBlank(message = "El RUT del conductor es obligatorio")
    @Size(max = 12, message = "El RUT no puede tener más de 12 caracteres")
    @Schema(
            description = "RUN/RUT del conductor o transportista a cargo de la unidad de carga",
            example = "12345678-9",
            maxLength = 12,
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String rutConductor;

    // RUT del inspector que realiza la inspección sanitaria
    @NotBlank(message = "El RUT del inspector es obligatorio")
    @Size(max = 12, message = "El RUT no puede tener más de 12 caracteres")
    @Schema(
            description = "RUN/RUT único del funcionario fiscalizador que ejecuta la revisión sanitaria",
            example = "9876543-2",
            maxLength = 12,
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String rutInspector;

    // Nombre del paso fronterizo donde se realiza la inspección
    @NotBlank(message = "El paso fronterizo es obligatorio")
    @Size(max = 100, message = "El paso no puede tener más de 100 caracteres")
    @Schema(
            description = "Nombre o glosa oficial del paso fronterizo habilitado donde se efectúa el control",
            example = "Los Libertadores",
            maxLength = 100,
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String pasoFronterizo;

    // Fecha y hora en que se realiza la inspección sanitaria
    @NotNull(message = "La fecha de inspección es obligatoria")
    @Schema(
            description = "Sello temporal (Fecha y hora) en que se da inicio a la auditoría de control fitozoosanitario",
            example = "2026-06-12T22:45:00",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private LocalDateTime fechaInspeccion;

    // Observaciones adicionales sobre la inspección
    @Schema(
            description = "Notas descriptivas complementarias, hallazgos iniciales o estado general observado por el inspector",
            example = "Se observa carga de frutas sellada con cadena de frío operativa."
    )
    private String observaciones;
}