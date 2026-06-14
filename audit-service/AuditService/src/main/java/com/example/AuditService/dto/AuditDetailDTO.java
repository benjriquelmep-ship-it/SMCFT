package com.example.AuditService.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.time.LocalDateTime;
import io.swagger.v3.oas.annotations.media.Schema;

@Data
@Schema(description = "Estructura de entrada obligatoria para el registro pormenorizado de una acción individual dentro de la traza de auditoría")
public class AuditDetailDTO {

    @NotNull(message = "El ID de la auditoría es obligatorio")
    @Schema(
            description = "Identificador único (ID) de la orden de auditoría de cabecera a la cual pertenece este detalle",
            example = "1",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private Long auditId;

    @NotBlank(message = "La acción es obligatoria")
    @Size(max = 100, message = "La acción no puede tener más de 100 caracteres")
    @Schema(
            description = "Nomenclatura operativa estandarizada que tipifica el evento perimetral",
            example = "AUTORIZAR_CRUCE",
            maxLength = 100,
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String accion;

    @NotBlank(message = "La descripción es obligatoria")
    @Size(max = 500, message = "La descripción no puede tener más de 500 caracteres")
    @Schema(
            description = "Glosas de andén detalladas que explican el contexto, motivos o elementos modificados en el tránsito",
            example = "Cruce autorizado conforme. Unidad vehicular visada con aforo físico de equipaje aprobado.",
            maxLength = 500,
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String descripcion;

    @NotBlank(message = "El RUT del usuario es obligatorio")
    @Size(max = 12, message = "El RUT no puede tener más de 12 caracteres")
    @Schema(
            description = "RUN/RUT legal o documento oficial de la persona natural o funcionario que gatilló la acción",
            example = "12345678-9",
            maxLength = 12,
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String rutUsuario;

    @NotBlank(message = "El resultado es obligatorio")
    @Pattern(
            regexp = "EXITOSO|FALLIDO|SOSPECHOSO",
            message = "El resultado debe ser EXITOSO, FALLIDO o SOSPECHOSO"
    )
    @Schema(
            description = "Dictamen técnico resolutivo o estado conclusivo de la operación registrada",
            example = "EXITOSO",
            allowableValues = {"EXITOSO", "FALLIDO", "SOSPECHOSO"},
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String resultado;

    @Size(max = 45, message = "La IP no puede tener más de 45 caracteres")
    @Schema(
            description = "Dirección de red IPv4 o IPv6 de origen desde donde se consumió el endpoint operativo",
            example = "192.168.1.50",
            maxLength = 45
    )
    private String ipAddress;

    @NotNull(message = "La fecha de acción es obligatoria")
    @Schema(
            description = "Sello temporal exacto (Año-Mes-Día y Hora) en que el servidor procesó e indexó la acción",
            example = "2026-06-12T23:58:00",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private LocalDateTime fechaAccion;
}