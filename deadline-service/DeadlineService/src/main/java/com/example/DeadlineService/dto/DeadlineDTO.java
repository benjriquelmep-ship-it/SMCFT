// Este archivo es el DTO de entrada para crear un deadline
// DTO = formulario de entrada — define qué datos debe mandar
// el cliente cuando quiere registrar un nuevo deadline
// Si falta algún campo obligatorio → responde HTTP 400 automáticamente
package com.example.DeadlineService.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class DeadlineDTO {

    // Patente del vehículo al que aplica el deadline
    @NotBlank(message = "La patente es obligatoria")
    @Size(max = 10,
            message = "La patente no puede tener más de 10 caracteres")
    private String patente;

    // RUT del conductor responsable del vehículo
    @NotBlank(message = "El RUT del conductor es obligatorio")
    @Size(max = 12,
            message = "El RUT no puede tener más de 12 caracteres")
    private String rutConductor;

    // ID del ingreso en Entry Service que originó este deadline
    // Deadline Service llama a Entry Service para verificar que este ingreso existe antes de registrar el deadline
    @NotNull(message = "El ID del ingreso es obligatorio")
    private Long entryId;

    // Fecha y hora en que el vehículo ingresó al país
    @NotNull(message = "La fecha de ingreso es obligatoria")
    private LocalDateTime fechaIngreso;

    // Fecha y hora límite de permanencia del vehículo en el país
    @NotNull(message = "La fecha límite es obligatoria")
    private LocalDateTime fechaLimite;

    // Tipo de deadline según el motivo del ingreso
    // Si llega otro valor → responde con el mensaje de error
    @NotBlank(message = "El tipo es obligatorio")
    @Pattern(
            regexp = "ADMISION_TEMPORAL|RETORNO_OBLIGATORIO",
            message = "El tipo debe ser ADMISION_TEMPORAL o RETORNO_OBLIGATORIO"
    )
    private String tipo;

    // Observaciones adicionales sobre el deadline
    // Ej: "Vehículo brasileño turista", "Admisión temporal por trabajo"
    private String observaciones;
}