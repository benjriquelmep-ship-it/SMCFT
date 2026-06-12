package com.example.DeadlineService.dto;

import lombok.Data;
import java.time.LocalDateTime;
import io.swagger.v3.oas.annotations.media.Schema;

@Data
@Schema(description = "DTO que representa entry")
public class EntryResponseDTO {
    @Schema(description = "Id", example = "1")
    private Long id;
    @Schema(description = "Patente", example = "ABC-123")
    private String patente;
    @Schema(description = "Rut Conductor", example = "12345678-9")
    private String rutConductor;
    @Schema(description = "Pais Origen", example = "ejemplo")
    private String paisOrigen;
    @Schema(description = "Paso Fronterizo", example = "ejemplo")
    private String pasoFronterizo;
    @Schema(description = "Fecha Ingreso", example = "2024-01-15")
    private LocalDateTime fechaIngreso;
    @Schema(description = "Tipo Ingreso", example = "PARTICULAR")
    private String tipoIngreso;
    @Schema(description = "Estado", example = "ACTIVO")
    private String estado;
}