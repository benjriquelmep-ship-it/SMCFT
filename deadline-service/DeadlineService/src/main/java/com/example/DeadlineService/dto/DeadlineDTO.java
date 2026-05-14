// dto/DeadlineDTO.java
package com.example.DeadlineService.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class DeadlineDTO {

    @NotBlank(message = "La patente es obligatoria")
    @Size(max = 10,
            message = "La patente no puede tener más de 10 caracteres")
    private String patente;

    @NotBlank(message = "El RUT del conductor es obligatorio")
    @Size(max = 12,
            message = "El RUT no puede tener más de 12 caracteres")
    private String rutConductor;

    @NotNull(message = "El ID del ingreso es obligatorio")
    private Long entryId;

    @NotNull(message = "La fecha de ingreso es obligatoria")
    private LocalDateTime fechaIngreso;

    @NotNull(message = "La fecha límite es obligatoria")
    private LocalDateTime fechaLimite;

    @NotBlank(message = "El tipo es obligatorio")
    @Pattern(
            regexp = "ADMISION_TEMPORAL|RETORNO_OBLIGATORIO",
            message = "El tipo debe ser ADMISION_TEMPORAL o RETORNO_OBLIGATORIO"
    )
    private String tipo;

    private String observaciones;
}