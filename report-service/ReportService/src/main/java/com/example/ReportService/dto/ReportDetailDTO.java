// Objeto de transferencia de datos para validar la entrada de un detalle de reporte
package com.example.ReportService.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.math.BigDecimal;
import io.swagger.v3.oas.annotations.media.Schema;

@Data
@Schema(description = "Estructura de datos requerida para inyectar una línea de desglose métrico o indicador estadístico a un informe consolidado")
public class ReportDetailDTO {

    // ID del reporte principal asociado
    @NotNull(message = "El ID del reporte es obligatorio")
    @Schema(
            description = "Identificador único (ID) del reporte de cabecera al cual se indexa esta línea métrica",
            example = "1",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private Long reportId;

    // Glosa o texto descriptivo del detalle (máx 300 caracteres)
    @NotBlank(message = "La descripción es obligatoria")
    @Size(max = 300, message = "La descripción no puede tener más de 300 caracteres")
    @Schema(
            description = "Glosa explícita o descripción del KPI/indicador evaluado",
            example = "Total de camiones comerciales de carga pesada controlados",
            maxLength = 300,
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String descripcion;

    // Monto, cantidad métrica o resultado del indicador (mínimo 0.0)
    @NotNull(message = "El valor es obligatorio")
    @DecimalMin(value = "0.0", message = "El valor no puede ser negativo")
    @Schema(
            description = "Valor cuantitativo, monto financiero o porcentaje del indicador aduanero",
            example = "1450.00",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private BigDecimal valor;

    // Unidad de medida del valor (ej: "Vehículos", "Porcentaje", "Pesos")
    @NotBlank(message = "La unidad es obligatoria")
    @Size(max = 50, message = "La unidad no puede tener más de 50 caracteres")
    @Schema(
            description = "Unidad de medida matemática que califica al valor numérico",
            example = "Vehículos",
            maxLength = 50,
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String unidad;

    // Clasificación del detalle (ej: "AUTORIZADOS", "RECHAZADOS")
    @NotBlank(message = "La categoría es obligatoria")
    @Size(max = 100, message = "La categoría no puede tener más de 100 caracteres")
    @Schema(
            description = "Categoría o criterio contable de agrupación para el desglose analítico",
            example = "AUTORIZADOS",
            maxLength = 100,
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String categoria;
}