// Objeto de transferencia de datos para validar la creación de un reporte
package com.example.ReportService.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.time.LocalDateTime;
import io.swagger.v3.oas.annotations.media.Schema;

@Data
@Schema(description = "Estructura de datos requerida para solicitar la compilación y generación de un nuevo informe gerencial aduanero")
public class ReportDTO {

    // Nombre o asunto del reporte (máx 200 caracteres)
    @NotBlank(message = "El título es obligatorio")
    @Size(max = 200, message = "El título no puede tener más de 200 caracteres")
    @Schema(
            description = "Asunto o título institucional del reporte consolidado",
            example = "Informe Cuatrimestral de Tránsito Vehicular y Cargas",
            maxLength = 200,
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String titulo;

    // Categoría del reporte, restringido por expresión regular
    @NotBlank(message = "El tipo de reporte es obligatorio")
    @Pattern(
            regexp = "CRUCE_FRONTERIZO|ADMISION_TEMPORAL|VEHICULOS|USUARIOS",
            message = "El tipo debe ser CRUCE_FRONTERIZO, ADMISION_TEMPORAL, VEHICULOS o USUARIOS"
    )
    @Schema(
            description = "Área analítica o módulo origen objeto del estudio estadístico",
            example = "CRUCE_FRONTERIZO",
            allowableValues = {"CRUCE_FRONTERIZO", "ADMISION_TEMPORAL", "VEHICULOS", "USUARIOS"},
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String tipoReporte;

    // Límite inferior del rango de tiempo analizado
    @NotNull(message = "La fecha de inicio es obligatoria")
    @Schema(
            description = "Fecha y hora que marca el límite inferior de la muestra estadística evaluada",
            example = "2026-01-01T00:00:00",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private LocalDateTime fechaInicio;

    // Límite superior del rango de tiempo analizado
    @NotNull(message = "La fecha de fin es obligatoria")
    @Schema(
            description = "Fecha y hora que marca el límite superior o de corte de la muestra estadística",
            example = "2026-04-30T23:59:59",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private LocalDateTime fechaFin;

    // Identificador (RUT) del usuario que solicita el documento
    @NotBlank(message = "El RUT del generador es obligatorio")
    @Size(max = 12, message = "El RUT no puede tener más de 12 caracteres")
    @Schema(
            description = "RUN/RUT del usuario, jefe de aduana o administrador que gatilla la generación del reporte",
            example = "12345678-9",
            maxLength = 12,
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String generadoPor;

    // Notas aclaratorias o comentarios adicionales opcionales
    @Schema(
            description = "Anotaciones al margen, justificaciones administrativas o comentarios del emisor",
            example = "Reporte emitido con fines de auditoría para la dirección regional de aduanas."
    )
    private String observaciones;
}