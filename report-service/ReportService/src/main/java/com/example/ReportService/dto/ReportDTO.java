// Objeto de transferencia de datos para validar la creación de un reporte
package com.example.ReportService.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ReportDTO {

    // Nombre o asunto del reporte (máx 200 caracteres)
    @NotBlank(message = "El título es obligatorio")
    @Size(max = 200,
            message = "El título no puede tener más de 200 caracteres")
    private String titulo;

    // Categoría del reporte, restringido por expresión regular
    @NotBlank(message = "El tipo de reporte es obligatorio")
    @Pattern(
            regexp = "CRUCE_FRONTERIZO|ADMISION_TEMPORAL|VEHICULOS|USUARIOS",
            message = "El tipo debe ser CRUCE_FRONTERIZO, ADMISION_TEMPORAL, " +
                    "VEHICULOS o USUARIOS"
    )
    private String tipoReporte;

    // Límite inferior del rango de tiempo analizado
    @NotNull(message = "La fecha de inicio es obligatoria")
    private LocalDateTime fechaInicio;

    // Límite superior del rango de tiempo analizado
    @NotNull(message = "La fecha de fin es obligatoria")
    private LocalDateTime fechaFin;

    // Identificador (RUT) del usuario que solicita el documento
    @NotBlank(message = "El RUT del generador es obligatorio")
    @Size(max = 12,
            message = "El RUT no puede tener más de 12 caracteres")
    private String generadoPor;

    // Notas aclaratorias o comentarios adicionales opcionales
    private String observaciones;
}