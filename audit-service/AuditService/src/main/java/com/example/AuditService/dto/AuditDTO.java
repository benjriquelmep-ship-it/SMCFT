// dto/AuditDTO.java
package com.example.AuditService.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class AuditDTO {

    @NotBlank(message = "El RUT del auditor es obligatorio")
    @Size(max = 12,
            message = "El RUT no puede tener más de 12 caracteres")
    private String rutAuditor;

    @NotBlank(message = "El tipo de auditoría es obligatorio")
    @Pattern(
            regexp = "USUARIO|CRUCE_FRONTERIZO|TRANSACCION|SISTEMA",
            message = "El tipo debe ser USUARIO, CRUCE_FRONTERIZO, " +
                    "TRANSACCION o SISTEMA"
    )
    private String tipoAuditoria;

    @NotBlank(message = "La entidad es obligatoria")
    @Size(max = 100,
            message = "La entidad no puede tener más de 100 caracteres")
    private String entidad;

    // ID del registro auditado — opcional
    private Long entidadId;

    @NotBlank(message = "La descripción es obligatoria")
    @Size(max = 500,
            message = "La descripción no puede tener más de 500 caracteres")
    private String descripcion;

    @NotNull(message = "La fecha de inicio es obligatoria")
    private LocalDateTime fechaInicio;
}