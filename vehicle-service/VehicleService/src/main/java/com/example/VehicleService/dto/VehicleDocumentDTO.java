// dto/VehicleDocumentDTO.java
// Lo que el cliente manda para agregar un documento a un vehículo

package com.example.VehicleService.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.time.LocalDate;

@Data
public class VehicleDocumentDTO {

    // ID del vehículo al que se agrega el documento
    @NotNull(message = "El ID del vehículo es obligatorio")
    private Long vehicleId;

    // Tipo de documento aduanero
    @NotBlank(message = "El tipo de documento es obligatorio")
    @Pattern(
            regexp = "PERMISO_CIRCULACION|SEGURO_OBLIGATORIO|REVISION_TECNICA",
            message = "El tipo debe ser PERMISO_CIRCULACION, " +
                    "SEGURO_OBLIGATORIO o REVISION_TECNICA"
    )
    private String tipo;

    // Número único del documento
    @NotBlank(message = "El número de documento es obligatorio")
    @Size(max = 50, message = "El número no puede tener más de 50 caracteres")
    private String numero;

    // @Future valida que la fecha sea posterior a hoy
    @NotNull(message = "La fecha de vencimiento es obligatoria")
    @Future(message = "La fecha de vencimiento debe ser futura")
    private LocalDate fechaVencimiento;
}