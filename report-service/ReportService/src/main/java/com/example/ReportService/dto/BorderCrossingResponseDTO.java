// Estructura de datos que mapea la respuesta recibida del Border Crossing Service
package com.example.ReportService.dto;

import lombok.Data;
import java.time.LocalDateTime;
import io.swagger.v3.oas.annotations.media.Schema;

@Data
@Schema(description = "DTO que representa border crossing")
public class BorderCrossingResponseDTO {
    // Identificador único del cruce en el microservicio origen
    @Schema(description = "Id", example = "1")
    private Long id;

    // Placa patente del vehículo consultado
    @Schema(description = "Patente", example = "ABC-123")
    private String patente;

    // RUN o pasaporte del conductor que realiza el paso
    @Schema(description = "Rut Conductor", example = "12345678-9")
    private String rutConductor;

    // Nación de destino del viaje
    @Schema(description = "Pais Destino", example = "ejemplo")
    private String paisDestino;

    // Nombre del complejo o paso aduanero
    @Schema(description = "Paso Fronterizo", example = "ejemplo")
    private String pasoFronterizo;

    // Timestamp exacto del registro
    @Schema(description = "Fecha Cruce", example = "2024-01-15")
    private LocalDateTime fechaCruce;

    // Condición actual del trámite (PENDIENTE, AUTORIZADO, RECHAZADO)
    @Schema(description = "Estado", example = "ACTIVO")
    private String estado;

    // Identificador del funcionario aduanero a cargo
    @Schema(description = "Rut Fiscalizador", example = "12345678-9")
    private String rutFiscalizador;
}