// Objeto de transferencia de datos para validar la entrada de un detalle de reporte
package com.example.ReportService.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.math.BigDecimal;
import io.swagger.v3.oas.annotations.media.Schema;

@Data
@Schema(description = "DTO que representa report detail")
public class ReportDetailDTO {

    // ID del reporte principal asociado
    @NotNull(message = "El ID del reporte es obligatorio")
    @Schema(description = "Report Id", example = "1")
    private Long reportId;

    // Glosa o texto descriptivo del detalle (máx 300 caracteres)
    @NotBlank(message = "La descripción es obligatoria")
    @Size(max = 300,
            message = "La descripción no puede tener más de 300 caracteres")
    @Schema(description = "Descripcion", example = "ejemplo", maxLength = 300)
    private String descripcion;

    // Monto, cantidad métrica o resultado del indicador (mínimo 0.0)
    @NotNull(message = "El valor es obligatorio")
    @DecimalMin(value = "0.0",
            message = "El valor no puede ser negativo")
    @Schema(description = "Valor", example = "15000.50")
    private BigDecimal valor;

    // Unidad de medida del valor (ej: "Vehículos", "Porcentaje", "Pesos")
    @NotBlank(message = "La unidad es obligatoria")
    @Size(max = 50,
            message = "La unidad no puede tener más de 50 caracteres")
    @Schema(description = "Unidad", example = "ejemplo", maxLength = 50)
    private String unidad;

    // Clasificación del detalle (ej: "AUTORIZADOS", "RECHAZADOS")
    @NotBlank(message = "La categoría es obligatoria")
    @Size(max = 100,
            message = "La categoría no puede tener más de 100 caracteres")
    @Schema(description = "Categoria", example = "ejemplo", maxLength = 100)
    private String categoria;
}