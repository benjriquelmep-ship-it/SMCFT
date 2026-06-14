package com.example.AuditService.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Schema(description = "Estructura de entrada requerida para la apertura e indexación de una cabecera de auditoría institucional")
public class AuditDTO {

    @NotBlank(message = "El RUT del auditor es obligatorio")
    @Size(max = 12, message = "El RUT no puede tener más de 12 caracteres")
    @Schema(
            description = "RUN/RUT legal del funcionario asignado al rol de revisor u oficial de seguridad",
            example = "9876543-2",
            maxLength = 12,
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String rutAuditor;

    @NotBlank(message = "El tipo de auditoría es obligatorio")
    @Pattern(
            regexp = "USUARIO|CRUCE_FRONTERIZO|TRANSACCION|SISTEMA",
            message = "El tipo debe ser USUARIO, CRUCE_FRONTERIZO, TRANSACCION o SISTEMA"
    )
    @Schema(
            description = "Área regulatoria o ámbito específico sobre el cual se desenvuelve el proceso de revisión forense",
            example = "CRUCE_FRONTERIZO",
            allowableValues = {"USUARIO", "CRUCE_FRONTERIZO", "TRANSACCION", "SISTEMA"},
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String tipoAuditoria;

    @NotBlank(message = "La entidad es obligatoria")
    @Size(max = 100, message = "La entidad no puede tener más de 100 caracteres")
    @Schema(
            description = "Nombre oficial del microservicio, componente o módulo del ecosistema que se encuentra bajo análisis",
            example = "border-crossing-service",
            maxLength = 100,
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String entidad;

    @Schema(
            description = "Identificador único (ID) del registro físico específico de la tabla destino objeto de fiscalización",
            example = "1"
    )
    private Long entidadId;

    @NotBlank(message = "La descripción es obligatoria")
    @Size(max = 500, message = "La descripción no puede tener más de 500 caracteres")
    @Schema(
            description = "Glosa introductoria o justificación administrativa sobre los objetivos de la auditoría aperturada",
            example = "Revisión ordinaria de flujos migratorios vehiculares y validaciones de equipajes en andén.",
            maxLength = 500,
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String descripcion;

    @NotNull(message = "La fecha de inicio es obligatoria")
    @Schema(
            description = "Marca de tiempo inicial en que se constituye formalmente el proceso de revisión",
            example = "2026-06-12T23:58:00",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private LocalDateTime fechaInicio;
}