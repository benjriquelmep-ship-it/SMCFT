package com.example.SanitaryService.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class SanitaryItemDTO {

    @NotNull(message = "El ID de la inspección es obligatorio")
    private Long inspectionId;

    @NotBlank(message = "La descripción es obligatoria")
    @Size(max = 200,
            message = "La descripción no puede tener más de 200 caracteres")
    private String descripcion;

    @NotBlank(message = "El resultado del item es obligatorio")
    @Pattern(
            regexp = "APROBADO|RECHAZADO|NO_APLICA|PENDIENTE",
            message = "El resultado debe ser APROBADO, RECHAZADO, " +
                    "NO_APLICA o PENDIENTE"
    )
    private String resultadoItem;

    private String observaciones;
}