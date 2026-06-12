// Lo que el cliente manda para registrar un ingreso al país

package com.example.EntryService.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.time.LocalDateTime;
import io.swagger.v3.oas.annotations.media.Schema;

@Data
@Schema(description = "DTO que representa entry")
public class EntryDTO {

    // Patente del vehículo que ingresa
    @NotBlank(message = "La patente es obligatoria")
    @Size(max = 10,
            message = "La patente no puede tener más de 10 caracteres")
    @Schema(description = "Patente", example = "ABC-123", maxLength = 10)
    private String patente;

    // RUT del conductor
    @NotBlank(message = "El RUT del conductor es obligatorio")
    @Size(max = 12,
            message = "El RUT no puede tener más de 12 caracteres")
    @Schema(description = "Rut Conductor", example = "12345678-9", maxLength = 12)
    private String rutConductor;

    // País de origen
    @NotBlank(message = "El país de origen es obligatorio")
    @Size(max = 100,
            message = "El país no puede tener más de 100 caracteres")
    @Schema(description = "Pais Origen", example = "ejemplo", maxLength = 100)
    private String paisOrigen;

    // Paso fronterizo
    @NotBlank(message = "El paso fronterizo es obligatorio")
    @Size(max = 100,
            message = "El paso no puede tener más de 100 caracteres")
    @Schema(description = "Paso Fronterizo", example = "ejemplo", maxLength = 100)
    private String pasoFronterizo;

    // Fecha y hora del ingreso
    @NotNull(message = "La fecha de ingreso es obligatoria")
    @Schema(description = "Fecha Ingreso", example = "2024-01-15")
    private LocalDateTime fechaIngreso;

    // Tipo de ingreso
    @NotBlank(message = "El tipo de ingreso es obligatorio")
    @Pattern(
            regexp = "RETORNO|ADMISION_TEMPORAL",
            message = "El tipo debe ser RETORNO o ADMISION_TEMPORAL"
    )
    @Schema(description = "Tipo Ingreso", example = "PARTICULAR")
    private String tipoIngreso;

    // Estado del ingreso — PENDIENTE, AUTORIZADO o RECHAZADO
    @Schema(description = "Estado", example = "ACTIVO")
    private String estado;
}
