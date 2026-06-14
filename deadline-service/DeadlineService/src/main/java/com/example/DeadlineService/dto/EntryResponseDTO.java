// Representa la respuesta que llega desde Entry Service
// cuando Deadline Service verifica el estado del ingreso
package com.example.DeadlineService.dto;

import lombok.Data;
import java.time.LocalDateTime;
import io.swagger.v3.oas.annotations.media.Schema;

@Data
@Schema(description = "Payload analítico de salida que expone el estado y metadatos de un ingreso importado desde el Entry Service")
public class EntryResponseDTO {

    @Schema(description = "Identificador único (ID) del trámite de acceso en el origen", example = "1")
    private Long id;

    @Schema(description = "Placa patente del vehículo asociado a la revisión", example = "ABCD-12")
    private String patente;

    @Schema(description = "RUN/RUT legal del conductor que ejecutó el tránsito", example = "12345678-9")
    private String rutConductor;

    @Schema(description = "Nación soberana de origen del vehículo", example = "Argentina")
    private String paisOrigen;

    @Schema(description = "Complejo o control fronterizo donde se realizó el andén", example = "Los Libertadores")
    private String pasoFronterizo;

    @Schema(description = "Sello temporal exacto de la visación de entrada", example = "2026-06-12T23:30:00")
    private LocalDateTime fechaIngreso;

    @Schema(description = "Régimen de ingreso aplicado en frontera", example = "ADMISION_TEMPORAL")
    private String tipoIngreso;

    @Schema(
            description = "Situación resolutiva del trámite de entrada en el microservicio origen",
            example = "AUTORIZADO",
            allowableValues = {"PENDIENTE", "AUTORIZADO", "RECHAZADO"}
    )
    private String estado;
}