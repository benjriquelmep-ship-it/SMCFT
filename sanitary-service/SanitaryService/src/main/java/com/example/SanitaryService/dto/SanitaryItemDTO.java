// Este archivo es el DTO de entrada para agregar un item a una inspección sanitaria
// DTO = formulario de entrada — define qué datos debe mandar
// el cliente cuando quiere agregar un objeto inspeccionado
// Si falta algún campo obligatorio → responde HTTP 400 automáticamente
package com.example.SanitaryService.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import io.swagger.v3.oas.annotations.media.Schema;

@Data
@Schema(description = "DTO que representa sanitary item")
public class SanitaryItemDTO {

    // ID de la inspección sanitaria a la que pertenece este item
    // SanitaryItemService verifica que la inspección existe
    // antes de agregar el item
    @NotNull(message = "El ID de la inspección es obligatorio")
    @Schema(description = "Inspection Id", example = "1")
    private Long inspectionId;

    // Descripción del objeto que fue inspeccionado
    @NotBlank(message = "La descripción es obligatoria")
    @Size(max = 200,
            message = "La descripción no puede tener más de 200 caracteres")
    @Schema(description = "Descripcion", example = "ejemplo", maxLength = 200)
    private String descripcion;

    // Resultado de la inspección de este item específico
    @NotBlank(message = "El resultado del item es obligatorio")
    @Pattern(
            regexp = "APROBADO|RECHAZADO|NO_APLICA|PENDIENTE",
            message = "El resultado debe ser APROBADO, RECHAZADO, " +
                    "NO_APLICA o PENDIENTE"
    )
    @Schema(description = "Resultado Item", example = "ejemplo")
    private String resultadoItem;

    // Observaciones adicionales sobre este item específico
    @Schema(description = "Observaciones", example = "Observación de ejemplo")
    private String observaciones;
}