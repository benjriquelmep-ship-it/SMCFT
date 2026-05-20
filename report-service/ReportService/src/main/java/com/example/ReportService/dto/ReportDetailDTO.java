// Objeto de transferencia de datos para validar la entrada de un detalle de reporte
package com.example.ReportService.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class ReportDetailDTO {

    // ID del reporte principal asociado
    @NotNull(message = "El ID del reporte es obligatorio")
    private Long reportId;

    // Glosa o texto descriptivo del detalle (máx 300 caracteres)
    @NotBlank(message = "La descripción es obligatoria")
    @Size(max = 300,
            message = "La descripción no puede tener más de 300 caracteres")
    private String descripcion;

    // Monto, cantidad métrica o resultado del indicador (mínimo 0.0)
    @NotNull(message = "El valor es obligatorio")
    @DecimalMin(value = "0.0",
            message = "El valor no puede ser negativo")
    private BigDecimal valor;

    // Unidad de medida del valor (ej: "Vehículos", "Porcentaje", "Pesos")
    @NotBlank(message = "La unidad es obligatoria")
    @Size(max = 50,
            message = "La unidad no puede tener más de 50 caracteres")
    private String unidad;

    // Clasificación del detalle (ej: "AUTORIZADOS", "RECHAZADOS")
    @NotBlank(message = "La categoría es obligatoria")
    @Size(max = 100,
            message = "La categoría no puede tener más de 100 caracteres")
    private String categoria;
}