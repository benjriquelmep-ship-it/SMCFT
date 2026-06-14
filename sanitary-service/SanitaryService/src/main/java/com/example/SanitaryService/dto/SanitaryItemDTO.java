// Este archivo es el DTO de entrada para agregar un item a una inspección sanitaria
// DTO = formulario de entrada — define qué datos debe mandar
// el cliente cuando quiere agregar un objeto inspeccionado
// Si falta algún campo obligatorio → responde HTTP 400 automáticamente
package com.example.SanitaryService.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import io.swagger.v3.oas.annotations.media.Schema;

@Data
@Schema(description = "Estructura de datos requerida para indexar el desglose de un producto, carga o lote específico sujeto a revisión sanitaria")
public class SanitaryItemDTO {

    // ID de la inspección sanitaria a la que pertenece este item
    @NotNull(message = "El ID de la inspección es obligatorio")
    @Schema(
            description = "Identificador único (ID) de la inspección de cabecera a la cual se le asocia este ítem",
            example = "1",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private Long inspectionId;

    // Descripción del objeto que fue inspeccionado
    @NotBlank(message = "La descripción es obligatoria")
    @Size(max = 200, message = "La descripción no puede tener más de 200 caracteres")
    @Schema(
            description = "Detalle, naturaleza o nombre técnico de la mercancía inspeccionada (ej. Pallets de manzanas, subproductos orgánicos)",
            example = "Caja de productos hortofrutícolas procesados",
            maxLength = 200,
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String descripcion;

    // Resultado de la inspección de este item específico
    @NotBlank(message = "El resultado del item es obligatorio")
    @Pattern(
            regexp = "APROBADO|RECHAZADO|NO_APLICA|PENDIENTE",
            message = "El resultado debe ser APROBADO, RECHAZADO, NO_APLICA o PENDIENTE"
    )
    @Schema(
            description = "Dictamen o resolución sanitaria aplicada de forma puntual a este lote de mercancía",
            example = "APROBADO",
            allowableValues = {"APROBADO", "RECHAZADO", "NO_APLICA", "PENDIENTE"},
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String resultadoItem;

    // Observaciones adicionales sobre este item específico
    @Schema(
            description = "Anotaciones específicas de laboratorio o motivos técnicos en caso de rechazo del producto",
            example = "Cumple con las resoluciones de importación y certificado de origen adjunto."
    )
    private String observaciones;
}