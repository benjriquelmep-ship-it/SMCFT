// Objeto de transferencia de datos para la validación de certificados y documentos vehiculares
package com.example.VehicleService.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.time.LocalDate;
import io.swagger.v3.oas.annotations.media.Schema;

@Data
@Schema(description = "DTO que representa vehicle document")
public class VehicleDocumentDTO {

    // Identificador único del vehículo al que se asocia el papel
    @NotNull(message = "El ID del vehículo es obligatorio")
    @Schema(description = "Vehicle Id", example = "1")
    private Long vehicleId;

    // Categoría del padrón o seguro según enumeración reglamentaria
    @NotBlank(message = "El tipo de documento es obligatorio")
    @Pattern(
            regexp = "PERMISO_CIRCULACION|SEGURO_OBLIGATORIO|REVISION_TECNICA",
            message = "El tipo debe ser PERMISO_CIRCULACION, " +
                    "SEGURO_OBLIGATORIO o REVISION_TECNICA"
    )
    @Schema(description = "Tipo", example = "PARTICULAR")
    private String tipo;

    // Código, folio o número correlativo único impreso en el documento
    @NotBlank(message = "El número de documento es obligatorio")
    @Size(max = 50, message = "El número no puede tener más de 50 caracteres")
    @Schema(description = "Numero", example = "ejemplo", maxLength = 50)
    private String numero;

    // Fecha límite de vigencia legal para el certificado
    // @Future valida que la fecha sea posterior a hoy
    @NotNull(message = "La fecha de vencimiento es obligatoria")
    @Future(message = "La fecha de vencimiento debe ser futura")
    @Schema(description = "Fecha Vencimiento", example = "2024-01-15")
    private LocalDate fechaVencimiento;
}