// Objeto de transferencia de datos para la validación de certificados y documentos vehiculares
package com.example.VehicleService.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.time.LocalDate;
import io.swagger.v3.oas.annotations.media.Schema;

@Data
@Schema(description = "Estructura de datos requerida para el registro o actualización de un documento legal asociado a un vehículo")
public class VehicleDocumentDTO {

    @NotNull(message = "El ID del vehículo es obligatorio")
    @Schema(
            description = "Identificador único (ID) del vehículo al cual se anexará la pieza documental",
            example = "1",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private Long vehicleId;

    @NotBlank(message = "El tipo de documento es obligatorio")
    @Pattern(
            regexp = "PERMISO_CIRCULACION|SEGURO_OBLIGATORIO|REVISION_TECNICA",
            message = "El tipo debe ser PERMISO_CIRCULACION, SEGURO_OBLIGATORIO o REVISION_TECNICA"
    )
    @Schema(
            description = "Categoría o tipo reglamentario del documento legal",
            example = "REVISION_TECNICA",
            allowableValues = {"PERMISO_CIRCULACION", "SEGURO_OBLIGATORIO", "REVISION_TECNICA"},
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String tipo;

    @NotBlank(message = "El número de documento es obligatorio")
    @Size(max = 50, message = "El número no puede tener más de 50 caracteres")
    @Schema(
            description = "Código de folio, serie o número único impreso físicamente en la credencial",
            example = "FOLIO-2026-99",
            maxLength = 50,
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String numero;

    @NotNull(message = "La fecha de vencimiento es obligatoria")
    @Future(message = "La fecha de vencimiento debe ser futura")
    @Schema(
            description = "Fecha límite calendario de vigencia legal (Debe ser una fecha futura respecto al día de hoy)",
            example = "2027-03-15",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private LocalDate fechaVencimiento;
}