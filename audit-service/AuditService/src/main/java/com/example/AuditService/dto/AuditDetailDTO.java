// dto/AuditDetailDTO.java
package com.example.AuditService.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class AuditDetailDTO {

    @NotNull(message = "El ID de la auditoría es obligatorio")
    private Long auditId;

    @NotBlank(message = "La acción es obligatoria")
    @Size(max = 100,
            message = "La acción no puede tener más de 100 caracteres")
    private String accion;

    @NotBlank(message = "La descripción es obligatoria")
    @Size(max = 500,
            message = "La descripción no puede tener más de 500 caracteres")
    private String descripcion;

    @NotBlank(message = "El RUT del usuario es obligatorio")
    @Size(max = 12,
            message = "El RUT no puede tener más de 12 caracteres")
    private String rutUsuario;

    @NotBlank(message = "El resultado es obligatorio")
    @Pattern(
            regexp = "EXITOSO|FALLIDO|SOSPECHOSO",
            message = "El resultado debe ser EXITOSO, FALLIDO o SOSPECHOSO"
    )
    private String resultado;

    @Size(max = 45,
            message = "La IP no puede tener más de 45 caracteres")
    private String ipAddress;

    @NotNull(message = "La fecha de acción es obligatoria")
    private LocalDateTime fechaAccion;
}