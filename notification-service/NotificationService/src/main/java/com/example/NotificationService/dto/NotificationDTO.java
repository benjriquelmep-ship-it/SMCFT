// dto/NotificationDTO.java
package com.example.NotificationService.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class NotificationDTO {

    @NotBlank(message = "El título es obligatorio")
    @Size(max = 200,
            message = "El título no puede tener más de 200 caracteres")
    private String titulo;

    @NotBlank(message = "El mensaje es obligatorio")
    @Size(max = 500,
            message = "El mensaje no puede tener más de 500 caracteres")
    private String mensaje;

    @NotBlank(message = "El tipo es obligatorio")
    @Pattern(
            regexp = "ALERTA_DEADLINE|ALERTA_URGENTE|ALERTA_VENCIDO|INFORMATIVA",
            message = "El tipo debe ser ALERTA_DEADLINE, ALERTA_URGENTE, " +
                    "ALERTA_VENCIDO o INFORMATIVA"
    )
    private String tipo;

    // ID de la alerta de deadline — opcional
    private Long deadlineAlertId;
}