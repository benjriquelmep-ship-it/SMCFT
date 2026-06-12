// Este archivo es el DTO de entrada para crear una alerta de deadline
// DTO = formulario de entrada — define qué datos debe mandar
// el cliente cuando quiere crear una alerta manualmente
// Si falta algún campo obligatorio → responde HTTP 400 automáticamente
package com.example.DeadlineService.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import io.swagger.v3.oas.annotations.media.Schema;

@Data
@Schema(description = "DTO que representa deadline alert")
public class DeadlineAlertDTO {

    // ID del deadline al que pertenece esta alerta
    // @NotNull = no puede llegar vacío (null)
    @NotNull(message = "El ID del deadline es obligatorio")
    @Schema(description = "Deadline Id", example = "1")
    private Long deadlineId;

    // Mensaje descriptivo de la alerta
    @NotBlank(message = "El mensaje es obligatorio")
    @Size(max = 500,
            message = "El mensaje no puede tener más de 500 caracteres")
    @Schema(description = "Mensaje", example = "ejemplo", maxLength = 500)
    private String mensaje;

    // Cuántos días le quedan al deadline antes de vencer
    @NotNull(message = "Los días restantes son obligatorios")
    @Min(value = 0,
            message = "Los días restantes no pueden ser negativos")
    @Schema(description = "Dias Restantes", example = "1")
    private Integer diasRestantes;

    // Tipo de alerta según la urgencia
    @NotBlank(message = "El tipo de alerta es obligatorio")
    @Pattern(
            regexp = "AVISO|URGENTE|VENCIDO",
            message = "El tipo debe ser AVISO, URGENTE o VENCIDO"
    )
    @Schema(description = "Tipo Alerta", example = "PARTICULAR")
    private String tipoAlerta;
}