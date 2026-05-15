// dto/ReportDTO.java
package com.example.ReportService.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ReportDTO {

    @NotBlank(message = "El título es obligatorio")
    @Size(max = 200,
            message = "El título no puede tener más de 200 caracteres")
    private String titulo;

    @NotBlank(message = "El tipo de reporte es obligatorio")
    @Pattern(
            regexp = "CRUCE_FRONTERIZO|ADMISION_TEMPORAL|VEHICULOS|USUARIOS",
            message = "El tipo debe ser CRUCE_FRONTERIZO, ADMISION_TEMPORAL, " +
                    "VEHICULOS o USUARIOS"
    )
    private String tipoReporte;

    @NotNull(message = "La fecha de inicio es obligatoria")
    private LocalDateTime fechaInicio;

    @NotNull(message = "La fecha de fin es obligatoria")
    private LocalDateTime fechaFin;

    @NotBlank(message = "El RUT del generador es obligatorio")
    @Size(max = 12,
            message = "El RUT no puede tener más de 12 caracteres")
    private String generadoPor;

    private String observaciones;
}