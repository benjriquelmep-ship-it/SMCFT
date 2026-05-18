package com.example.AuditService.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class AuditDetailDTO {

    // ID de la auditoría a la que pertenece este detalle
    @NotNull(message = "El ID de la auditoría es obligatorio")
    private Long auditId;

    // Nombre de la acción que realizó el usuario ("LOGIN", "LOGOUT", "CREAR_USUARIO", "AUTORIZAR_CRUCE")
    @NotBlank(message = "La acción es obligatoria")
    @Size(max = 100,
            message = "La acción no puede tener más de 100 caracteres")
    private String accion;

    // Descripción detallada de lo que hizo el usuario
    @NotBlank(message = "La descripción es obligatoria")
    @Size(max = 500,
            message = "La descripción no puede tener más de 500 caracteres")
    private String descripcion;

    // RUT del usuario que realizó la acción
    @NotBlank(message = "El RUT del usuario es obligatorio")
    @Size(max = 12,
            message = "El RUT no puede tener más de 12 caracteres")
    private String rutUsuario;

    // Resultado de la acción que realizó el usuario
    @NotBlank(message = "El resultado es obligatorio")
    @Pattern(
            regexp = "EXITOSO|FALLIDO|SOSPECHOSO",
            message = "El resultado debe ser EXITOSO, FALLIDO o SOSPECHOSO"
    )
    private String resultado;

    // Dirección IP desde donde se realizó la acción
    @Size(max = 45,
            message = "La IP no puede tener más de 45 caracteres")
    private String ipAddress;

    @NotNull(message = "La fecha de acción es obligatoria")
    private LocalDateTime fechaAccion;
}