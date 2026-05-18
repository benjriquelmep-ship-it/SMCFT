package com.example.AuditService.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class AuditDTO {

    // RUT del auditor que está creando la auditoría
    @NotBlank(message = "El RUT del auditor es obligatorio")
    @Size(max = 12,
            message = "El RUT no puede tener más de 12 caracteres")
    private String rutAuditor;

    // Tipo de auditoría que se va a realizar
    @NotBlank(message = "El tipo de auditoría es obligatorio")
    @Pattern(
            regexp = "USUARIO|CRUCE_FRONTERIZO|TRANSACCION|SISTEMA",
            message = "El tipo debe ser USUARIO, CRUCE_FRONTERIZO, " +
                    "TRANSACCION o SISTEMA"
    )
    private String tipoAuditoria;

    // Nombre del microservicio o proceso que se está auditando
    @NotBlank(message = "La entidad es obligatoria")
    @Size(max = 100,
            message = "La entidad no puede tener más de 100 caracteres")
    private String entidad;

    // ID del registro específico dentro de la entidad que se audita
    private Long entidadId;

    // Descripción de lo que se va a revisar en la auditoría
    @NotBlank(message = "La descripción es obligatoria")
    @Size(max = 500,
            message = "La descripción no puede tener más de 500 caracteres")
    private String descripcion;

    // Fecha y hora en que inició la auditoría
    @NotNull(message = "La fecha de inicio es obligatoria")
    private LocalDateTime fechaInicio;
}