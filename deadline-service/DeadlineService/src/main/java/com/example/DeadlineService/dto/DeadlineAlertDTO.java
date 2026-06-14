// Este archivo es el DTO de entrada para crear una alerta de deadline
// DTO = formulario de entrada — define qué datos debe mandar
// el cliente cuando quiere crear una alerta manualmente
// Si falta algún campo obligatorio → responde HTTP 400 automáticamente
package com.example.DeadlineService.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import io.swagger.v3.oas.annotations.media.Schema;

@Data
@Schema(description = "Estructura de entrada requerida para el alta manual o automatizada de una alerta de aproximación al vencimiento temporal")
public class DeadlineAlertDTO {

    // ID del deadline al que pertenece esta alerta
    @NotNull(message = "El ID del deadline es obligatorio")
    @Schema(
            description = "Identificador único (ID) del plazo de control cronológico de cabecera",
            example = "1",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private Long deadlineId;

    // Mensaje descriptivo de la alerta
    @NotBlank(message = "El mensaje es obligatorio")
    @Size(max = 500, message = "El mensaje no puede tener más de 500 caracteres")
    @Schema(
            description = "Glosa explicativa o texto institucional que compone el cuerpo del aviso de vencimiento",
            example = "Alerta de control: Quedan menos de 72 horas para la expiración del plazo máximo de permanencia vehicular.",
            maxLength = 500,
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String mensaje;

    // Cuántos días le quedan al deadline antes de vencer
    @NotNull(message = "Los días restantes son obligatorios")
    @Min(value = 0, message = "Los días restantes no pueden ser negativos")
    @Schema(
            description = "Cantidad de días calendario faltantes antes de que la permanencia pase a condición de infracción",
            example = "3",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private Integer diasRestantes;

    // Tipo de alerta según la urgencia
    @NotBlank(message = "El tipo de alerta es obligatorio")
    @Pattern(
            regexp = "AVISO|URGENTE|VENCIDO",
            message = "El tipo debe ser AVISO, URGENTE o VENCIDO"
    )
    @Schema(
            description = "Nivel de criticidad u oportunidad del aviso cronológico emitido",
            example = "URGENTE",
            allowableValues = {"AVISO", "URGENTE", "VENCIDO"},
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String tipoAlerta;
}