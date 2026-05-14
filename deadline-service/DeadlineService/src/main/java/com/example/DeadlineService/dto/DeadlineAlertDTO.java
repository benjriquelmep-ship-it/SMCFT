// dto/DeadlineAlertDTO.java
package com.example.DeadlineService.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class DeadlineAlertDTO {

    @NotNull(message = "El ID del deadline es obligatorio")
    private Long deadlineId;

    @NotBlank(message = "El mensaje es obligatorio")
    @Size(max = 500,
            message = "El mensaje no puede tener más de 500 caracteres")
    private String mensaje;

    @NotNull(message = "Los días restantes son obligatorios")
    @Min(value = 0,
            message = "Los días restantes no pueden ser negativos")
    private Integer diasRestantes;

    @NotBlank(message = "El tipo de alerta es obligatorio")
    @Pattern(
            regexp = "AVISO|URGENTE|VENCIDO",
            message = "El tipo debe ser AVISO, URGENTE o VENCIDO"
    )
    private String tipoAlerta;
}