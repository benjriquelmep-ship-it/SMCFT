// Este archivo es el DTO de entrada para crear un deadline
// DTO = formulario de entrada — define qué datos debe mandar
// el cliente cuando quiere registrar un nuevo deadline
// Si falta algún campo obligatorio → responde HTTP 400 automáticamente
package com.example.DeadlineService.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.time.LocalDateTime;
import io.swagger.v3.oas.annotations.media.Schema;

@Data
@Schema(description = "Estructura de entrada obligatoria para fijar, calcular e indexar un plazo de permanencia vehicular fronterizo")
public class DeadlineDTO {

    // Patente del vehículo al que aplica el deadline
    @NotBlank(message = "La patente es obligatoria")
    @Size(max = 10, message = "La patente no puede tener más de 10 caracteres")
    @Schema(
            description = "Placa patente única identificatoria de la unidad automotriz sujeta a control temporal",
            example = "ABCD-12",
            maxLength = 10,
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String patente;

    // RUT del conductor responsable del vehículo
    @NotBlank(message = "El RUT del conductor es obligatorio")
    @Size(max = 12, message = "El RUT no puede tener más de 12 caracteres")
    @Schema(
            description = "RUN/RUT legal o pasaporte del conductor titular responsable de la restitución del vehículo",
            example = "12345678-9",
            maxLength = 12,
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String rutConductor;

    // ID del ingreso en Entry Service que originó este deadline
    @NotNull(message = "El ID del ingreso es obligatorio")
    @Schema(
            description = "Identificador único (ID) de la orden de acceso perimetral provisto por el Entry Service",
            example = "1",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private Long entryId;

    // Fecha y hora en que el vehículo ingresó al país
    @NotNull(message = "La fecha de ingreso es obligatoria")
    @Schema(
            description = "Punto de partida cronológico (Sello temporal de ingreso) registrado en el andén de control",
            example = "2026-06-12T23:30:00",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private LocalDateTime fechaIngreso;

    // Fecha y hora límite de permanencia del vehículo en el país
    @NotNull(message = "La fecha límite es obligatoria")
    @Schema(
            description = "Fecha fatal y hora límite máxima autorizada por aduanas para la permanencia legal del bien en el territorio",
            example = "2026-09-12T23:30:00",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private LocalDateTime fechaLimite;

    // Tipo de deadline según el motivo del ingreso
    @NotBlank(message = "El tipo es obligatorio")
    @Pattern(
            regexp = "ADMISION_TEMPORAL|RETORNO_OBLIGATORIO",
            message = "El tipo debe ser ADMISION_TEMPORAL o RETORNO_OBLIGATORIO"
    )
    @Schema(
            description = "Naturaleza jurídica o régimen específico que estipula las bases del límite temporal",
            example = "ADMISION_TEMPORAL",
            allowableValues = {"ADMISION_TEMPORAL", "RETORNO_OBLIGATORIO"},
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String tipo;

    // Observaciones adicionales sobre el deadline
    @Schema(
            description = "Anotaciones complementarias, glosas administrativas o justificaciones del régimen aduanero",
            example = "Vehículo de turista extranjero. Plazo ordinario fijado por un máximo de 90 días."
    )
    private String observaciones;
}